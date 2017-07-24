/**
 * Copyright Search Technologies 2014
 */
package com.searchtechnologies.aspire.components;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.apache.commons.io.FilenameUtils;

import com.searchtechnologies.aspire.components.SubversionItemType.SubversionItemTypeEnum;
import com.searchtechnologies.aspire.framework.Standards;
import com.searchtechnologies.aspire.framework.Standards.Scanner.Action;
import com.searchtechnologies.aspire.framework.utilities.DateTimeUtilities;
import com.searchtechnologies.aspire.scanner.DSConnection;
import com.searchtechnologies.aspire.scanner.HierarchicalSourceInfo;
import com.searchtechnologies.aspire.scanner.SourceItem;
import com.searchtechnologies.aspire.services.AspireException;
import com.searchtechnologies.aspire.services.AspireObject;
import com.searchtechnologies.aspire.services.events.JobEvent;

/** Implementing a new Aspire Scanner stage.
 * 
 * @author pmartinez
 */
public class SubversionSourceInfo extends HierarchicalSourceInfo {
  
  /**
   * The Relative URL of the root item
   */
  String relativeURL;
 
  
  /**
   * Default Constructor
   * @throws AspireException 
   */
  public SubversionSourceInfo() throws AspireException{
    sourceType="subversion";
  }


  /**
   * Creates the root SourceItem object with basic item type information associated to the startUrl property
   * 
   * @return root item
   */
  @Override
  public SourceItem createRootItem() throws AspireException{
    
    String repositoryURL = getPathFromUrl(getStartUrl());
    String absoluteURL = repositoryURL + relativeURL;
    
    SVNRepository repository = getRepository();
    
    try{
      SVNNodeKind node = repository.checkPath(relativeURL, -1);

      if(node == SVNNodeKind.NONE){
        throw new AspireException("SubversionSourceInfo.createRootItem", "Couldn't connect to SVN repository: %s", absoluteURL);
      }

    }
    catch(SVNException e){
      throw new AspireException("SubversionSourceInfo.createRootItem", e, "Couldn't connect to SVN repository: %s", absoluteURL);
    }
    
    SubversionSourceItem rootItem = new SubversionSourceItem(absoluteURL);
    
    SubversionItemType itemType = new SubversionItemType();
    itemType.setValue(SubversionItemTypeEnum.root);
    
    rootItem.setItemType(itemType);
    rootItem.setSourceName(FilenameUtils.getBaseName(absoluteURL));
    rootItem.setLevel(0);
    rootItem.setUrl(absoluteURL);
    rootItem.setStartUrl(true);
    rootItem.setContainer(true);
    rootItem.setSourceName(getFriendlyName());
    rootItem.setSourceType(getSourceType());
    rootItem.setRelativeURL(relativeURL);
    
    return rootItem;
  }
  
  /**
   * This method open the repository connection.
   * @return The repository access
   * @throws AspireException
   */
  private SVNRepository getRepository() throws AspireException
  {
    SubversionDSConnection connection = (SubversionDSConnection) openConnection();
    return connection.getRepository();
  }

  /**
   * Creates a child SourceItem for the given URL.
   * @param url URL of the child sub-item.
   * @param parent Reference to the parent node
   * @param subItemEntry the SVN subItemEntry to get the item type
   * @return A new sourceItem. 
   */
  public SubversionSourceItem createSubItem(String url, SubversionSourceItem parent, SVNDirEntry svnSubItemEntry) throws AspireException{
    
    SubversionSourceItem subItem = new SubversionSourceItem(url, parent);
    setSourceItemType(subItem, svnSubItemEntry);
    
    //Setting the subitem relative URL
    String parentRelativeURL = parent.getRelativeURL();
    String subItemRelativeURL = "";
    
    if(parentRelativeURL.endsWith("/")){
      subItemRelativeURL = parentRelativeURL + svnSubItemEntry.getName();
    }
    else{
      subItemRelativeURL = parentRelativeURL + "/" + svnSubItemEntry.getName();
    }
    
    //Verify if URL is a Directory
    if(!subItemRelativeURL.endsWith("/") && subItem.isContainer()){
      subItemRelativeURL = subItemRelativeURL + "/";
    }
    
     subItem.setRelativeURL(subItemRelativeURL);
     
     //Setting the subItem URL
    
    if(!url.endsWith("/") && subItem.isContainer()){
      url = url + "/";
      subItem.setUrl(url);
    }
    
    return subItem;
  }
  
  /**
   * Sets a source item type.
   * @param item the source item.
   * @param subItemEntry the SVN subItemEntry
   * @throws AspireException
   */
  private void setSourceItemType(SourceItem subItem, SVNDirEntry svnSubItemEntry) throws AspireException {
    subItem.setContainer(svnSubItemEntry.getKind() == SVNNodeKind.DIR);
    SubversionItemType type = new SubversionItemType();
    type.setValue(svnSubItemEntry.getKind() == SVNNodeKind.DIR? SubversionItemTypeEnum.dir : SubversionItemTypeEnum.file);
    subItem.setItemType(type);
   
  }

  /**
   * Extracts the direct children items of the SourceItem item parameter and returns an order list the children  
   * @param item Item to scan
   * @return Alphabetically ordered list of direct children of item
   */
  @Override
  public List<SourceItem> scan(SourceItem item) throws AspireException{   

    SVNRepository svnRepository = getRepository();
    
    //List of subItems
    
    List<SourceItem> subItems = new ArrayList<SourceItem>();
    
    try{
      String itemRelativePath = ((SubversionSourceItem)item).getRelativeURL();
      
      @SuppressWarnings("unchecked")
      List<SVNDirEntry> collection = (List<SVNDirEntry>) svnRepository.getDir(itemRelativePath, -1, null, (Collection<SVNDirEntry>) null);
      
      //Sort the collection by name
      Collections.sort(collection, new Comparator<SVNDirEntry>() {
        public int compare(SVNDirEntry entry1,SVNDirEntry entry2){
          return entry1.getName().compareTo(entry2.getName());
         }});
 
      for(SVNDirEntry svnSubItemEntry : collection){
        //Get the path of the subItem
        String subItemPath = svnRepository.getLocation() + itemRelativePath + svnSubItemEntry.getName();
        //Create subItem
        SubversionSourceItem subItem = createSubItem(subItemPath, (SubversionSourceItem)item, svnSubItemEntry);
        //Populate subItem
        populateSourceItemInfo(subItem, svnSubItemEntry);
        //Add the subItem to the list
        subItems.add(subItem);
      }
    }
    catch (SVNException e){
      throw new AspireException("SubversionSourceInfo.scan", e, "Error getting entries for repository: %s", svnRepository.getLocation());
    }
    
    return subItems;
  }
  
  /**
   * Populates a SourceItem object with all metadata information associated to it
   * @param subItem is the source item to associate information
   * @param svnSubItemEntry is the entry from the svn repository
   * @throws AspireException
   */
  public void populateSourceItemInfo(SubversionSourceItem subItem, SVNDirEntry svnSubItemEntry) throws AspireException{

    String repositoryURL = getPathFromUrl(getStartUrl());
    String absoluteURL = repositoryURL + relativeURL;
    String title = svnSubItemEntry.getName();
    
    subItem.setSourceType(getSourceType());
    subItem.setSourceName(getFriendlyName());
    subItem.setField("repositoryUrl", absoluteURL);
    subItem.setCreatedBy(svnSubItemEntry.getAuthor());
    subItem.setLastModified(DateTimeUtilities.getISO8601DateTime(svnSubItemEntry.getDate()));
    subItem.setField("title", title);
    
    if(subItem.isContainer()){
      subItem.setField("fileType", SubversionItemTypeEnum.dir);
    }
    else{
      subItem.setField("fileType", FilenameUtils.getExtension(title));
    }
  }

  /**
   * Populates a SourceItem object with all metadata information associated to it
   * @param item
   * @throws AspireException
   */
  @Override
  public void populateSourceItem(SourceItem item) throws AspireException{

    try {
      
      SVNRepository svnRepository = getRepository();

      SVNDirEntry svnDirEntry = svnRepository.getDir(((SubversionSourceItem)item).getRelativeURL(), -1, true, (Collection<?>) null);

      item.setConnectorSpecificField("revision", svnDirEntry.getRevision());
      item.setConnectorSpecificField("message", svnDirEntry.getCommitMessage());

      item.setModificationSignature(generateModificationSignature(item));
      item.setSourceId(getContentSourceId());
      

    } catch (SVNException e) {
      throw new AspireException("SubversionSourceInfo.populateSourceItem", e, "Cannot get the revision and the commit message of the item: %s", item.getUrl());
    }
  }

  /**
   * DSConnection factory method
   * @return
   * @throws AspireException
   */
  @Override
  public DSConnection newDSConnection() {
    return new SubversionDSConnection(getStartUrl(), getUsername(), getPassword(), getLogger());
  }

  /**
   * Gets the file path from a given URL. Protocol is expected to be 'file://'.
   * @param urlString String of the URL.
   * @return Path contained in the URL. For example, "file://c:\home" changes to c:\home.
   * @throws AspireException 
   */
  private String getPathFromUrl(String urlString) throws AspireException {
    if(urlString.startsWith("file:/")) {
      try {
        return new URL(urlString).getFile();
      }
      catch(MalformedURLException e) {
        throw new AspireException("SubversionSourceInfo.getPathFromUrl", e, "Malformed URL for: %s", urlString);
      }
    }

    return urlString;
  }
  
  /**
   * This function return the relative path from the interface 
   * @return the relative path
   */
  public String getRelativeURL() {
    return relativeURL;
  }

  /**
   * This function set the value of the relative path setting for the user in the interface
   * @param relativePath inserted by the user.
   */
  public void setRelativeURL(String relativePath) {
    this.relativeURL = relativePath;
  }
  
  /**
   * Creates the modification signature for a given source item.
   * @param item The item type to which the signature will be calculated.
   * @return String concatenation of lastModified, data size and ACLs if available.
   * @throws AspireException If there are errors generating the signature.
   */
  private String generateModificationSignature(SourceItem item) throws AspireException {
    StringBuffer buffer = new StringBuffer();
    buffer.append(item.getConnectorSpecificField("revision"));

    AspireObject acls = item.getAcls();
    if (acls!=null){
      for(AspireObject acl : acls.getChildren()){
        buffer.append(acl.getAttribute(Standards.Scanner.ACL_ACCESS_TAG));
        buffer.append(acl.getAttribute(Standards.Scanner.ACL_FULLNAME_TAG));
      }
    }

    return buffer.toString();
  }


  @Override
  public void jobComplete(Action action, JobEvent event) throws AspireException {
    // TODO Auto-generated method stub
    
  }
}
