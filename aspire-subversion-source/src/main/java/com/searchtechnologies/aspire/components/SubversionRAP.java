/**
 * Copyright Search Technologies 2015
 */
package com.searchtechnologies.aspire.components;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.w3c.dom.Element;

import com.searchtechnologies.aspire.connector.services.RepositoryAccessProvider;
import com.searchtechnologies.aspire.connector.services.ScanListener;
import com.searchtechnologies.aspire.connector.services.SourceInfo;
import com.searchtechnologies.aspire.connector.services.SourceItem;
import com.searchtechnologies.aspire.framework.ComponentImpl;
import com.searchtechnologies.aspire.framework.Standards;
import com.searchtechnologies.aspire.framework.utilities.DateTimeUtilities;
import com.searchtechnologies.aspire.services.AspireException;
import com.searchtechnologies.aspire.services.AspireObject;

public class SubversionRAP extends ComponentImpl implements RepositoryAccessProvider {
	public static final String RELATIVE_URL = "relativeURL";
	private SubversionSourceInfo srcinfo = null;

	@Override
	public void processCrawlRoot(SourceItem item, SourceInfo info, ScanListener listener) throws AspireException {
		info("Processing crawl root - startUrl: %s (%s)", info.getStartUrl(), item.getId());
		listener.addItem(getRootItem(info));
	}

	public SourceItem getRootItem(SourceInfo info) throws AspireException {
		String relativeURL = ((SubversionSourceInfo) info).getRelativeURL();
		String repositoryURL = getPathFromUrl(info.getStartUrl());
		String absoluteURL = repositoryURL + relativeURL;

		SourceItem rootItem = new SourceItem(absoluteURL);

		rootItem.setType(SubversionItemType.root);
		rootItem.setSourceName(FilenameUtils.getBaseName(absoluteURL));
		rootItem.setUrl(absoluteURL);
		rootItem.setStartUrl(true);
		rootItem.container(true);
		rootItem.setField(RELATIVE_URL, relativeURL);
		return rootItem;
	}
	
	@Override
	public void scan(SourceItem item, SourceInfo info, RepositoryConnection conn, ScanListener listener)
			throws AspireException {
		SVNRepository svnRepository = ((SubversionDSConnection)conn).getRepository();

		try {
			String itemRelativePath = item.getField(RELATIVE_URL);

			@SuppressWarnings("unchecked")
			List<SVNDirEntry> collection = (List<SVNDirEntry>) svnRepository.getDir(itemRelativePath, -1, null,
					(Collection<SVNDirEntry>) null);

			// Sort the collection by name
			Collections.sort(collection, new Comparator<SVNDirEntry>() {
				public int compare(SVNDirEntry entry1, SVNDirEntry entry2) {
					return entry1.getName().compareTo(entry2.getName());
				}
			});

			for (SVNDirEntry svnSubItemEntry : collection) {
				// Get the path of the subItem
				String subItemPath = svnRepository.getLocation() + itemRelativePath + svnSubItemEntry.getName();
				// Create subItem
				SourceItem subItem = createSubItem(subItemPath, item, svnSubItemEntry);
				// Populate subItem
				SVNDirEntry svnDirEntry = svnRepository.getDir(subItem.getField(RELATIVE_URL), -1, true,
						(Collection<?>) null);
				subItem.setField("revision", svnDirEntry.getRevision());
				subItem.setField("message", svnDirEntry.getCommitMessage());

				// Add the subItem to the list
	      listener.addItem(subItem);
			}
		} catch (SVNException e) {
			throw new AspireException("SubversionSourceInfo.scan", e, "Error getting entries for repository: %s",
					svnRepository.getLocation());
		}
	}
	
	/**
   * Creates a child SourceItem for the given URL.
   * @param url URL of the child sub-item.
   * @param parent Reference to the parent node
   * @param subItemEntry the SVN subItemEntry to get the item type
   * @return A new sourceItem. 
   */
  public SourceItem createSubItem(String url, SourceItem parent, SVNDirEntry svnSubItemEntry) throws AspireException{
    
  	SourceItem subItem = new SourceItem(url, parent);
    setSourceItemType(subItem, svnSubItemEntry);
    
    //Setting the subitem relative URL
    String parentRelativeURL = parent.getField(RELATIVE_URL);
    String subItemRelativeURL = "";
    
    if(parentRelativeURL.endsWith("/")){
      subItemRelativeURL = parentRelativeURL + svnSubItemEntry.getName();
    }
    else{
      subItemRelativeURL = parentRelativeURL + "/" + svnSubItemEntry.getName();
    }
    
    //Verify if URL is a Directory
    if(!subItemRelativeURL.endsWith("/") && subItem.container()){
      subItemRelativeURL = subItemRelativeURL + "/";
    }
    
     subItem.setField(RELATIVE_URL, subItemRelativeURL);
     subItem.setName(svnSubItemEntry.getName());
     //Setting the subItem URL
    
    if(!url.endsWith("/") && subItem.container()){
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
    subItem.container(svnSubItemEntry.getKind() == SVNNodeKind.DIR);
    subItem.setType(svnSubItemEntry.getKind() == SVNNodeKind.DIR? SubversionItemType.dir : SubversionItemType.file);
   
  }

	@Override
	public void populate(SourceItem item, SourceInfo sourceInfo, RepositoryConnection conn) throws AspireException {
		try {
			SubversionSourceInfo svnInfo = (SubversionSourceInfo) sourceInfo;
			String repositoryURL = getPathFromUrl(svnInfo.getStartUrl());
			String absoluteURL = repositoryURL + svnInfo.getRelativeURL();

			SVNRepository svnRepository = ((SubversionDSConnection) conn).getRepository();

			SVNDirEntry svnDirEntry = svnRepository.getDir(item.getField(RELATIVE_URL), -1, true,
					(Collection<?>) null);

			String title = svnDirEntry.getName();

			item.setSourceType(svnInfo.getSourceType());
			item.setSourceName(svnInfo.getFriendlyName());
			item.setField("repositoryUrl", absoluteURL);
			item.setCreatedBy(svnDirEntry.getAuthor());
			item.setLastModified(DateTimeUtilities.getISO8601DateTime(svnDirEntry.getDate()));
			item.setField("title", title);

			if (item.container()) {
				item.setField("fileType", SubversionItemType.dir);
			} else {
				item.setField("fileType", FilenameUtils.getExtension(title));
			}

		} catch (SVNException e) {
			throw new AspireException("SubversionSourceInfo.populateSourceItem", e,
					"Cannot get the revision and the commit message of the item: %s", item.getUrl());
		}
	}

	@Override
	public boolean isContainer(SourceItem item, RepositoryConnection conn) throws AspireException {
		switch ((SubversionItemType) item.getType()) {
		case root:
			return true;
		case dir:
			return true;
		case file:
			return false;
		}
		return false;
	}

	@Override
	public String generateSignature(SourceItem item, RepositoryConnection conn) throws AspireException {
		StringBuffer buffer = new StringBuffer();
		buffer.append(item.getField("revision"));

		AspireObject acls = item.getAcls();
		if (acls != null) {
			for (AspireObject acl : acls.getChildren()) {
				buffer.append(acl.getAttribute(Standards.Scanner.ACL_ACCESS_TAG));
				buffer.append(acl.getAttribute(Standards.Scanner.ACL_FULLNAME_TAG));
			}
		}

		return buffer.toString();
	}

	@Override
	public void initialize(Element config) throws AspireException {
	}

	@Override
	public void close() {
		// Nothing to do
	}

	@Override
	public ItemFilter getItemFilter() {
		// Use the default one
		return null;
	}

	@Override
	public RepositoryConnection newConnection(SourceInfo info) throws AspireException {
		SubversionSourceInfo svnInfo = (SubversionSourceInfo) info;
		SubversionDSConnection connection = new SubversionDSConnection(info.getStartUrl(), info.getUser(),
				info.getPassword(), this);
		SVNRepository repository = connection.getRepository();
		try {
			SVNNodeKind node = repository.checkPath(svnInfo.getRelativeURL(), -1);

			if (node == SVNNodeKind.NONE) {
				throw new AspireException("SubversionSourceInfo.createRootItem", "Couldn't connect to SVN repository: %s",
						svnInfo.getStartUrl() + svnInfo.getRelativeURL());
			}

		} catch (SVNException e) {
			throw new AspireException("SubversionSourceInfo.createRootItem", e, "Couldn't connect to SVN repository: %s",
					svnInfo.getStartUrl());
		}
		return connection;
	}

	@Override
	public Fetcher getFetcher() {
		return new SubversionFetcher(this);
	}

	@Override
	public SourceInfo newSourceInfo(AspireObject scanProperties) throws AspireException {
		// Create a new source info
		SubversionSourceInfo info = new SubversionSourceInfo();
		info.setRelativeURL(scanProperties.getText("relativeurl"));
		this.srcinfo = info;
		return info;
	}

	@Override
	public GroupExpansion getGroupExpansion(SourceInfo info) {
		return null;
	}

	/**
	 * Gets the file path from a given URL. Protocol is expected to be 'file://'.
	 * 
	 * @param urlString
	 *          String of the URL.
	 * @return Path contained in the URL. For example, "file://c:\home" changes to
	 *         c:\home.
	 * @throws AspireException
	 */
	private String getPathFromUrl(String urlString) throws AspireException {
		if (urlString.startsWith("file:/")) {
			try {
				return new URL(urlString).getFile();
			} catch (MalformedURLException e) {
				throw new AspireException("SubversionSourceInfo.getPathFromUrl", e, "Malformed URL for: %s", urlString);
			}
		}

		return urlString;
	}

	SubversionSourceInfo getInfo() {
		return this.srcinfo;
	}
}
