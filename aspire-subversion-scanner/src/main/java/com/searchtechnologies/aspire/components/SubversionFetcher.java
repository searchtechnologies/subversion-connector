/**
 * Copyright Search Technologies 2014
 */

package com.searchtechnologies.aspire.components;

import java.util.HashMap;
import java.util.Map;

import com.searchtechnologies.aspire.docprocessing.fetchurl.FetchURLStage;
import com.searchtechnologies.aspire.framework.utilities.SecurityUtilities;
import com.searchtechnologies.aspire.framework.utilities.StringUtilities;
import com.searchtechnologies.aspire.services.AspireException;
import com.searchtechnologies.aspire.services.AspireObject;
import com.searchtechnologies.aspire.services.Job;

/**
 * Fetcher for Subversion connector
 * <p/>
 * This component is responsible of opening input streams to the content of each source item, so text extraction and other metadata manipulation can take place.
 * <p/>
 * The fetcher component will be called for each source item (not containers by default) by multiple threads concurrently.
 * <p/>
 * <b>Not all connectors need a fetcher component. Aspire already has a generic Fetcher component which opens streams to URLs (including URLs to resources on the file system).</b>
 * 
 * @author pmartinez
 */
public class SubversionFetcher extends FetchURLStage {
  /**
   * This is the entry point of the fetcher component. Make sure this method is thread safe!!!
   * <p/>
   * Open an input stream for the item content here. At this point, you can assume containers were already filtered.
   */
  @Override
  public void process(Job j) throws AspireException{
    AspireObject doc = j.get();
    AspireObject conn = doc.get("connectorSource");

    Map<String, String> props = null;
    if (conn != null) {
      String username = conn.getText("username", null);
      if (StringUtilities.isNotEmpty(username)) {
        String password = conn.getText("password", null);
        props = new HashMap<String, String>();
        props.put("Authorization", SecurityUtilities.getBasicAuthenticationString(username, password));
      }
    }
    process(j, props);
  }
}