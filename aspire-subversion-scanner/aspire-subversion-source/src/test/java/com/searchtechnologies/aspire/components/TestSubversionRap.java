package com.searchtechnologies.aspire.components;



import java.io.IOException;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.ISVNSession;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

import com.searchtechnologies.aspire.connector.services.RepositoryAccessProvider;
import com.searchtechnologies.aspire.connector.services.SourceInfo;
import com.searchtechnologies.aspire.connector.services.SourceItem;
import com.searchtechnologies.aspire.services.*;

import junit.framework.TestCase;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class TestSubversionRap extends TestCase {
  
  String url = "https://svn.searchtechnologies.com/svn/aspire";
  static String relativeUrl = "/svn-connector-test/";
  
  @Override
  public void setUp() throws Exception {
    super.setUp();
    // Setup mock classes
    new MockSVNRepositoryFactory();
  }
  


  protected AspireObject getConnectorSourceConfig() throws AspireException {
    AspireObject doc = new AspireObject("doc");
    doc.add("server", "localhost");
    doc.add("url", url);
    doc.add("relativeurl", relativeUrl);
    doc.add("user", "admin");
    doc.add("password", "admin");
    doc.add("indexContainers", "true");
    doc.add("scanRecursively", "true");
    return doc;
  }
  
  @Test
  public void testSourceInfo() throws AspireException {
  	SubversionRAP rap = new SubversionRAP();
  	SourceInfo info = rap.newSourceInfo(getConnectorSourceConfig());
  	assertEquals(relativeUrl, ((SubversionSourceInfo)info).getRelativeURL());
  }
  
  @Test
  public void testGenerateModificationSignature() throws AspireException, IOException {

  	SubversionRAP rap = new SubversionRAP();
  	SourceItem item = new SourceItem("/file.txt");
  	String revision = "26030";
    item.setField("revision", revision);;
    String signature = rap.generateSignature(item, null);
    assertEquals(revision, signature);

  }

  @Test
  public void testFlagContainer() throws AspireException {
    RepositoryAccessProvider rap = new SubversionRAP();

    SourceItem item = new SourceItem("/file.txt");
    item.setType(SubversionItemType.file);
    assertFalse(rap.isContainer(item, null));

    item.setType(SubversionItemType.dir);
    assertTrue(rap.isContainer(item, null));

    item.setType(SubversionItemType.root);
    assertTrue(rap.isContainer(item, null));
  }
  
  @Test
  public void testCreateRootItem() throws AspireException {
    new MockSVNRepositoryFactory();
    SubversionRAP rap = new SubversionRAP();
    AspireObject props = getConnectorSourceConfig();
    SubversionSourceInfo sourceInfo = (SubversionSourceInfo) rap.newSourceInfo(getConnectorSourceConfig());
    sourceInfo.setStartUrl(props.getText("url"));
    sourceInfo.setUser(props.getText("user"));
    sourceInfo.setPassword(props.getText("password"));
    
    SourceItem root = rap.getRootItem(sourceInfo);
    
    assertEquals(url + relativeUrl, root.getUrl());
    assertEquals(null, root.getParent());
    assertEquals(true, root.container());
    assertEquals(SubversionItemType.root, root.getType());
    assertEquals(true, root.isStartUrl());
    assertEquals(relativeUrl, root.getField(SubversionRAP.RELATIVE_URL));
  }
  
  @SuppressWarnings("deprecation")
  @Test
  public void testCreateSubItem() throws AspireException, SVNException{
    new MockSVNRepositoryFactory();
    SubversionRAP rap = new SubversionRAP();
    
    SubversionSourceInfo sourceInfo = (SubversionSourceInfo) rap.newSourceInfo(getConnectorSourceConfig());
    AspireObject props = getConnectorSourceConfig();
    sourceInfo.setStartUrl(props.getText("url"));
    sourceInfo.setUser(props.getText("user"));
    sourceInfo.setPassword(props.getText("password"));
    SourceItem root = rap.getRootItem(sourceInfo);
    
    String fileName = "file 001.txt";
    String subItemRelativeUrl = relativeUrl + fileName;
    
    TestSVNDirEntry svnDirEntry = new TestSVNDirEntry(SVNURL.parseURIDecoded(url + subItemRelativeUrl), SVNURL.parseURIDecoded(url), fileName, SVNNodeKind.FILE, 100, true, 1000, new Date(System.currentTimeMillis()), "pmartinez");
    SourceItem subItem = rap.createSubItem(url + subItemRelativeUrl, root, svnDirEntry);
    
    assertEquals(url + subItemRelativeUrl, subItem.getUrl());
    assertEquals(root, subItem.getParent());
    assertEquals(false,subItem.container());
    assertEquals(SubversionItemType.file, subItem.getType());
    assertTrue(subItem.getMetadata() != null);
    assertEquals(subItemRelativeUrl, subItem.getField(SubversionRAP.RELATIVE_URL));
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
