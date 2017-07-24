package com.searchtechnologies.aspire.components;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLock;
import org.tmatesoft.svn.core.SVNMergeInfoInheritance;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNPropertyValue;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.ISVNFileRevisionHandler;
import org.tmatesoft.svn.core.io.ISVNInheritedPropertiesHandler;
import org.tmatesoft.svn.core.io.ISVNLocationEntryHandler;
import org.tmatesoft.svn.core.io.ISVNLocationSegmentHandler;
import org.tmatesoft.svn.core.io.ISVNLockHandler;
import org.tmatesoft.svn.core.io.ISVNReplayHandler;
import org.tmatesoft.svn.core.io.ISVNReporterBaton;
import org.tmatesoft.svn.core.io.ISVNSession;
import org.tmatesoft.svn.core.io.ISVNWorkspaceMediator;
import org.tmatesoft.svn.core.io.SVNCapability;
import org.tmatesoft.svn.core.io.SVNRepository;

public class TestSVNRepository extends SVNRepository {

  protected TestSVNRepository(SVNURL location, ISVNSession options) {
    super(location, options);
    // TODO Auto-generated constructor stub
  }
  
  @SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
  @Override
  public Collection<SVNDirEntry> getDir(String path, long revision, SVNProperties properties, int entryFields, Collection dirEntries) throws SVNException {
    
    final Collection result = dirEntries != null ? dirEntries : new LinkedList();
    
    SVNURL repositoryRoot = SVNURL.parseURIDecoded("https://svn.searchtechnologies.com/svn/aspire/");
    String fileName;
    String url;
    
    //Adding files for first level
    if (path.endsWith("svn-connector-test/")){
      
      for(int i = 0; i < 4; i++){
        
        fileName = "file 00" + Integer.toString(i) + ".txt";
        url = path + fileName;

        SVNDirEntry svnDirEntry = new SVNDirEntry(SVNURL.parseURIDecoded(repositoryRoot + url), repositoryRoot, fileName, SVNNodeKind.FILE, 100, true, revision, new Date(System.currentTimeMillis()), "pmartinez", "This is a test");
        result.add(svnDirEntry);
      }
      
      //Adding a container.
      fileName =  "svn-test-container";
      url = path + "/" + fileName + "/";
      SVNDirEntry svnDirEntryContainer = new SVNDirEntry(SVNURL.parseURIDecoded(repositoryRoot + url), repositoryRoot, fileName, SVNNodeKind.DIR, 100, true, revision, new Date(System.currentTimeMillis()), "pmartinez", "This is a container test");
      result.add(svnDirEntryContainer);
      
    } //Adding files for second level
    else if (path.endsWith("svn-test-container/")){
      for(int i = 0; i < 10; i++){
        
        fileName = "file 00" + Integer.toString(i) + ".java";
        url = path + "/" + fileName;

        SVNDirEntry svnDirEntry = new SVNDirEntry(SVNURL.parseURIDecoded(repositoryRoot + url), repositoryRoot, fileName, SVNNodeKind.FILE, 100, true, revision, new Date(System.currentTimeMillis()), "pmartinez", "This is a test");
        result.add(svnDirEntry);
      }
    }
    
    return result;
}
  
  

  @Override
  public void testConnection() throws SVNException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public long getLatestRevision() throws SVNException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public long getDatedRevision(Date date) throws SVNException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public SVNProperties getRevisionProperties(long revision,
      SVNProperties properties) throws SVNException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setRevisionPropertyValue(long revision, String propertyName,
      SVNPropertyValue propertyValue) throws SVNException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public SVNPropertyValue getRevisionPropertyValue(long revision,
      String propertyName) throws SVNException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SVNNodeKind checkPath(String path, long revision) throws SVNException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public long getFile(String path, long revision, SVNProperties properties,
      OutputStream contents) throws SVNException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public long getDir(String path, long revision, SVNProperties properties,
      ISVNDirEntryHandler handler) throws SVNException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public long getDir(String path, long revision, SVNProperties properties,
      int entryFields, ISVNDirEntryHandler handler) throws SVNException {
    // TODO Auto-generated method stub
    return 0;
  }

  @SuppressWarnings({ "rawtypes", "deprecation" })
  @Override
  public SVNDirEntry getDir(String path, long revision,
      boolean includeCommitMessages, Collection entries) throws SVNException {
    
    SVNURL repositoryRoot = SVNURL.parseURIDecoded("https://svn.searchtechnologies.com/svn/aspire/");
    String fileName = "file 001.txt";
    String url = path + fileName;

    SVNDirEntry svnDirEntry = new SVNDirEntry(SVNURL.parseURIDecoded(repositoryRoot + url), repositoryRoot, fileName, SVNNodeKind.FILE, 100, true, 201, new Date(System.currentTimeMillis()), "pmartinez", "This is a test");

    if (includeCommitMessages){
      svnDirEntry.setCommitMessage("This is my first commit");
    }
      
    return svnDirEntry;
   
  }

  @Override
  public void diff(SVNURL url, long targetRevision, long revision,
      String target, boolean ignoreAncestry, SVNDepth depth,
      boolean getContents, ISVNReporterBaton reporter, ISVNEditor editor)
      throws SVNException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void update(SVNURL url, long revision, String target, SVNDepth depth,
      ISVNReporterBaton reporter, ISVNEditor editor) throws SVNException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void update(long revision, String target, SVNDepth depth,
      boolean sendCopyFromArgs, ISVNReporterBaton reporter, ISVNEditor editor)
      throws SVNException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void status(long revision, String target, SVNDepth depth,
      ISVNReporterBaton reporter, ISVNEditor editor) throws SVNException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void replay(long lowRevision, long revision, boolean sendDeltas,
      ISVNEditor editor) throws SVNException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public SVNDirEntry info(String path, long revision) throws SVNException {
    // TODO Auto-generated method stub
    return null;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public ISVNEditor getCommitEditor(String logMessage, Map locks,
      boolean keepLocks, ISVNWorkspaceMediator mediator) throws SVNException {
    // TODO Auto-generated method stub
    return null;
  }

  @SuppressWarnings("rawtypes")
  @Override
  protected ISVNEditor getCommitEditorInternal(Map locks, boolean keepLocks,
      SVNProperties revProps, ISVNWorkspaceMediator mediator)
      throws SVNException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SVNLock getLock(String path) throws SVNException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SVNLock[] getLocks(String path) throws SVNException {
    // TODO Auto-generated method stub
    return null;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public void lock(Map pathsToRevisions, String comment, boolean force,
      ISVNLockHandler handler) throws SVNException {
    // TODO Auto-generated method stub
    
  }

  @SuppressWarnings("rawtypes")
  @Override
  public void unlock(Map pathToTokens, boolean force, ISVNLockHandler handler)
      throws SVNException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void closeSession() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean hasCapability(SVNCapability capability) throws SVNException {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  protected void getInheritedPropertiesImpl(String path, long revision,
      String propertyName, ISVNInheritedPropertiesHandler handler)
      throws SVNException {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected long getDeletedRevisionImpl(String path, long pegRevision,
      long endRevision) throws SVNException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  protected long getLocationSegmentsImpl(String path, long pegRevision,
      long startRevision, long endRevision, ISVNLocationSegmentHandler handler)
      throws SVNException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  protected int getLocationsImpl(String path, long pegRevision,
      long[] revisions, ISVNLocationEntryHandler handler) throws SVNException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  protected long logImpl(String[] targetPaths, long startRevision,
      long endRevision, boolean changedPath, boolean strictNode, long limit,
      boolean includeMergedRevisions, String[] revisionProperties,
      ISVNLogEntryHandler handler) throws SVNException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  protected int getFileRevisionsImpl(String path, long startRevision,
      long endRevision, boolean includeMergedRevisions,
      ISVNFileRevisionHandler handler) throws SVNException {
    // TODO Auto-generated method stub
    return 0;
  }

  @SuppressWarnings("rawtypes")
  @Override
  protected Map getMergeInfoImpl(String[] paths, long revision,
      SVNMergeInfoInheritance inherit, boolean includeDescendants)
      throws SVNException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected void replayRangeImpl(long startRevision, long endRevision,
      long lowRevision, boolean sendDeltas, ISVNReplayHandler handler)
      throws SVNException {
    // TODO Auto-generated method stub
    
  }

}
