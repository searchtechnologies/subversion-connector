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

  String relativeURL;

  public SubversionSourceItem(String urlToProcess) throws AspireException {
    super(urlToProcess);
    // TODO Auto-generated constructor stub
  }
  
  public SubversionSourceItem(String urlToProcess, SourceItem parent)
      throws AspireException {
    super(urlToProcess, parent);
    // TODO Auto-generated constructor stub
  }

  public String getRelativeURL() {
    return relativeURL;
  }


  public void setRelativeURL(String relativePath) {
    this.relativeURL = relativePath;
  }
  
}
