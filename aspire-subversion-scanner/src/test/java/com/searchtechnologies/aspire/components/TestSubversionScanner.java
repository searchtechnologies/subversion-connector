package com.searchtechnologies.aspire.components;



import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.ISVNSession;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

import com.searchtechnologies.aspire.components.SubversionItemType.SubversionItemTypeEnum;
import com.searchtechnologies.aspire.framework.JobFactory;
import com.searchtechnologies.aspire.framework.utilities.DateTimeUtilities;
import com.searchtechnologies.aspire.scanner.SourceItem;
import com.searchtechnologies.aspire.services.*;
import com.searchtechnologies.aspire.scanner.DocType;

import junit.framework.TestCase;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class TestSubversionScanner extends TestCase {
  
  String url = "https://svn.searchtechnologies.com/svn/aspire";
  static String relativeUrl = "/svn-connector-test/";
  
  @Override
  public void setUp() throws Exception {
    super.setUp();
    // Setup mock classes
    new MockSVNRepositoryFactory();
  }
  
  @Test
  public void testCreateRootItem() throws AspireException {
    new MockSVNRepositoryFactory();
    SubversionScanner subversionScanner = new SubversionScanner();
    
    AspireObject doc = new AspireObject("doc");
    doc.add("displayName", "SubversionTest");
    doc.add(getConnectorSourceConfig(false));
    Job job = JobFactory.newInstance(doc);
    
    SubversionSourceInfo sourceInfo = (SubversionSourceInfo) subversionScanner.initializeScan(job);
    
    SourceItem root = sourceInfo.createRootItem();
    
    assertEquals("SubversionTest", root.getSourceName());
    assertEquals(0, root.getLevel());
    assertEquals(url + relativeUrl, root.getUrl());
    assertEquals("000 /", root.getFormattedParentUrl());
    assertEquals(null, root.getParent());
    assertEquals(true, root.isContainer());
    assertEquals(SubversionItemTypeEnum.root, root.getItemType().getValue());
    assertEquals(true, root.isStartUrl());
    assertEquals(true, root.isContainer());
    assertEquals(relativeUrl, ((SubversionSourceItem)root).getRelativeURL());
  }
  
  @SuppressWarnings("deprecation")
  @Test
  public void testCreateSubItem() throws AspireException, SVNException{
    new MockSVNRepositoryFactory();
    SubversionScanner subversionScanner = new SubversionScanner();
    
    AspireObject doc = new AspireObject("doc");
    doc.add("displayName", "SubversionTest");
    doc.add(getConnectorSourceConfig(false));
    Job job = JobFactory.newInstance(doc);
    
    SubversionSourceInfo sourceInfo = (SubversionSourceInfo) subversionScanner.initializeScan(job);
    SourceItem root = sourceInfo.createRootItem();
    
    String fileName = "file 001.txt";
    String subItemRelativeUrl = relativeUrl + fileName;
    
    TestSVNDirEntry svnDirEntry = new TestSVNDirEntry(SVNURL.parseURIDecoded(url + subItemRelativeUrl), SVNURL.parseURIDecoded(url), fileName, SVNNodeKind.FILE, 100, true, 1000, new Date(System.currentTimeMillis()), "pmartinez");
    SourceItem subItem = sourceInfo.createSubItem(url + subItemRelativeUrl, (SubversionSourceItem) root, svnDirEntry);
    
    assertEquals(1, subItem.getLevel());
    assertEquals(url + subItemRelativeUrl, subItem.getUrl());
    assertEquals(root, subItem.getParent());
    assertEquals(false,subItem.isContainer());
    assertEquals(SubversionItemTypeEnum.file, subItem.getItemType().getValue());
    assertEquals(DocType.item, subItem.getDocType());
    assertTrue(subItem.getMetadata() != null);
    assertEquals("aspire/file", subItem.getMetadata().getText("repItemType"));
    assertEquals("item", subItem.getMetadata().getText("docType"));
    assertEquals("001 " + url + subItemRelativeUrl, subItem.getMetadata().getText("snapshotUrl"));
    assertEquals(null, subItem.getModificationSignature());
    assertEquals(subItemRelativeUrl, ((SubversionSourceItem)subItem).getRelativeURL());
  }
  
  @SuppressWarnings("deprecation")
  @Test
  public void testScan() throws AspireException, SVNException{
    new MockSVNRepositoryFactory();
    SubversionScanner subversionScanner = new SubversionScanner();
    
    AspireObject doc = new AspireObject("doc");
    doc.add("displayName", "SubversionTest");
    doc.add(getConnectorSourceConfig(false));
    Job job = JobFactory.newInstance(doc);
    
    SubversionSourceInfo sourceInfo = (SubversionSourceInfo) subversionScanner.initializeScan(job);
    SourceItem root = sourceInfo.createRootItem();
    
    List<SourceItem> scannedItems = null;
    int counter = 0;
    
    //First Level
    scannedItems = sourceInfo.sortForEnqueue(sourceInfo.scan(root));
    counter = 0;
    
    for (SourceItem scanItem : scannedItems){
      switch (counter){
        case 0:
        case 1:
        case 2:
        case 3:
          assertEquals(1, scanItem.getLevel());
          assertEquals(url + relativeUrl + "file 00" + counter + ".txt", scanItem.getUrl());
          assertEquals(root, scanItem.getParent());
          assertEquals(false, scanItem.isContainer());
          assertEquals(DocType.item, scanItem.getDocType());
          assertEquals(SubversionItemTypeEnum.file, scanItem.getItemType().getValue());
          assertTrue(scanItem.getMetadata() != null);
          assertEquals("aspire/file", scanItem.getMetadata().getText("repItemType"));
          assertEquals("item", scanItem.getMetadata().getText("docType"));
          assertEquals("001 " + url + relativeUrl + "file 00" + counter + ".txt", scanItem.getMetadata().getText("snapshotUrl"));
          assertEquals(null, scanItem.getModificationSignature());
          break;
        case 4:
          assertEquals(1, scanItem.getLevel());
          assertEquals(url + relativeUrl + "svn-test-container/", scanItem.getUrl());
          assertEquals(root, scanItem.getParent());
          assertEquals(true, scanItem.isContainer());
          assertEquals(DocType.container, scanItem.getDocType());
          assertEquals(SubversionItemTypeEnum.dir, scanItem.getItemType().getValue());
          assertTrue(scanItem.getMetadata() != null);
          assertEquals("aspire/dir", scanItem.getMetadata().getText("repItemType"));
          assertEquals("container", scanItem.getMetadata().getText("docType"));
          assertEquals("001 " + url + relativeUrl + "svn-test-container/", scanItem.getMetadata().getText("snapshotUrl"));
          assertEquals(null, scanItem.getModificationSignature());
          break;
      }
      
      counter++;
    }
    
    // Second Level
    String containerName = "svn-test-container";
    TestSVNDirEntry containerSVNDirEntry = new TestSVNDirEntry(SVNURL.parseURIDecoded(url + relativeUrl + containerName), SVNURL.parseURIDecoded(url), containerName, SVNNodeKind.DIR, 200, true, 2000, new Date(System.currentTimeMillis()), "pmartinez");
    SourceItem container = sourceInfo.createSubItem(relativeUrl + containerName, (SubversionSourceItem) root, containerSVNDirEntry);
    
    scannedItems = sourceInfo.sortForEnqueue(sourceInfo.scan(container));
    counter = 0;
    
    for (SourceItem scanItem : scannedItems){
      switch (counter){
        case 0:
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
        case 8:
        case 9:
          assertEquals(2, scanItem.getLevel());
          assertEquals(url + relativeUrl + "svn-test-container/file 00" + counter + ".java", scanItem.getUrl());
          assertEquals(container, scanItem.getParent());
          assertEquals(false, scanItem.isContainer());
          assertEquals(DocType.item, scanItem.getDocType());
          assertEquals(SubversionItemTypeEnum.file, scanItem.getItemType().getValue());
          assertTrue(scanItem.getMetadata() != null);
          assertEquals("aspire/file", scanItem.getMetadata().getText("repItemType"));
          assertEquals("item", scanItem.getMetadata().getText("docType"));
          assertEquals("002 " + url + relativeUrl + "svn-test-container/file 00" + counter + ".java", scanItem.getMetadata().getText("snapshotUrl"));
          assertEquals(null, scanItem.getModificationSignature());
          break;
      }
      
      counter++;
    }
  }
  
  @SuppressWarnings("deprecation")
  @Test
  public void testPopulateSourceItemInfo() throws AspireException, SVNException, ParseException{
    
    String fileName = "file 001.txt";
    String folderName = "svn-connector-test";
    String subItemRelativeUrl = relativeUrl + fileName;
    Date creationDate = new Date(System.currentTimeMillis());
    
    SubversionScanner subversionScanner = new SubversionScanner();
    
    AspireObject doc = new AspireObject("doc");
    doc.add("displayName", "SubversionTest");
    doc.add(getConnectorSourceConfig(false));
    Job job = JobFactory.newInstance(doc);
    
    SubversionSourceInfo sourceInfo = (SubversionSourceInfo) subversionScanner.initializeScan(job);
    TestSVNDirEntry svnSubItemEntry;
    
    //Validating files
    SubversionSourceItem subItem = new SubversionSourceItem(url);
    svnSubItemEntry = new TestSVNDirEntry(SVNURL.parseURIDecoded(url + subItemRelativeUrl), SVNURL.parseURIDecoded(url), fileName, SVNNodeKind.FILE, 100, true, 1000, creationDate, "pmartinez");
    sourceInfo.populateSourceItemInfo(subItem, svnSubItemEntry);
    
    assertEquals("subversion", subItem.getSourceType());
    assertEquals("SubversionTest", subItem.getSourceName());
    assertEquals(url + relativeUrl, subItem.getField("repositoryUrl"));
    assertEquals(DateTimeUtilities.getISO8601DateTime(creationDate).toString(), subItem.getLastModified());
    assertEquals(fileName, subItem.getField("title"));
    assertEquals("txt", subItem.getField("fileType"));
    
    //Validating folder
    SubversionSourceItem subFolder = new SubversionSourceItem(url);
    svnSubItemEntry = new TestSVNDirEntry(SVNURL.parseURIDecoded(url + relativeUrl), SVNURL.parseURIDecoded(url), folderName, SVNNodeKind.DIR, 100, true, 1000, creationDate, "pmartinez");
    subFolder.setContainer(true);
    
    sourceInfo.populateSourceItemInfo(subFolder, svnSubItemEntry);
    
    assertEquals("subversion", subFolder.getSourceType());
    assertEquals("SubversionTest", subFolder.getSourceName());
    assertEquals(url + relativeUrl, subFolder.getField("repositoryUrl"));
    assertEquals(DateTimeUtilities.getISO8601DateTime(creationDate).toString(), subFolder.getLastModified());
    assertEquals(folderName, subFolder.getField("title"));
    assertEquals(SubversionItemTypeEnum.dir.toString(), subFolder.getField("fileType"));
  }
  
  @Test
  public void testPopulateSourceItem() throws AspireException, SVNException, ParseException{
    new MockSVNRepositoryFactory();
    
    SubversionScanner subversionScanner = new SubversionScanner();
    
    AspireObject doc = new AspireObject("doc");
    doc.add("displayName", "SubversionTest");
    doc.add(getConnectorSourceConfig(false));
    Job job = JobFactory.newInstance(doc);
    
    SubversionSourceInfo sourceInfo = (SubversionSourceInfo) subversionScanner.initializeScan(job);
    
    //Validating files
    SubversionSourceItem subItem = new SubversionSourceItem(url);
    sourceInfo.populateSourceItem(subItem);
    
    assertEquals("201", subItem.getConnectorSpecificField("revision"));
    assertEquals("This is my first commit", subItem.getConnectorSpecificField("message"));
  }
  
  private AspireObject getConnectorSourceConfig(boolean partialScan) throws AspireException{
    AspireObject connectorSource = new AspireObject("connectorSource");
    connectorSource.setAttribute("type", "aspire/fileshare");
    connectorSource.add("url", url);
    connectorSource.add("relativeurl", relativeUrl);
    connectorSource.add("partialScan", Boolean.toString(partialScan));
    connectorSource.add("subDirUrl",partialScan?"SubFolder":"");
    connectorSource.add("indexContainers", "true");
    connectorSource.add("scanRecursively", "true");
    return connectorSource;
  }
  
  public static class MockSVNRepositoryFactory extends MockUp<SVNRepositoryFactory>{

    @Mock
    public static SVNRepository create(SVNURL url, ISVNSession options) throws SVNException {
      @SuppressWarnings("deprecation")
      SVNURL repositoryURL = SVNURL.parseURIDecoded("https://svn.searchtechnologies.com/svn/aspire/");
      return (SVNRepository) new TestSVNRepository(repositoryURL, null);
    }
  }
}
