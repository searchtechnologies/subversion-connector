package com.searchtechnologies.aspire.components;

import java.util.Date;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;


public class TestSVNDirEntry extends SVNDirEntry {

  public TestSVNDirEntry(SVNURL url, SVNURL repositoryRoot, String name,
      SVNNodeKind kind, long size, boolean hasProperties, long revision,
      Date createdDate, String lastAuthor) {
    super(url, repositoryRoot, name, kind, size, hasProperties, revision,
        createdDate, lastAuthor);
  }
}
