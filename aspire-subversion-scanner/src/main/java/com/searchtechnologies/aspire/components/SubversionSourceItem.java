/**
 * Copyright Search Technologies 2014
 */
package com.searchtechnologies.aspire.components;

import com.searchtechnologies.aspire.scanner.SourceItem;
import com.searchtechnologies.aspire.services.AspireException;

/**
 * Represents an item type from the content source.
 * @author pmartinez
 */
public class SubversionSourceItem extends SourceItem {

  private static final long serialVersionUID = 7361526034048737214L;
  String relativeURL;

  public SubversionSourceItem(String urlToProcess) throws AspireException {
    super(urlToProcess);
  }

  public SubversionSourceItem(String urlToProcess, SourceItem parent) throws AspireException {
    super(urlToProcess, parent);
  }

  public String getRelativeURL() {
    return relativeURL;
  }

  public void setRelativeURL(String relativePath) {
    this.relativeURL = relativePath;
  }
}
