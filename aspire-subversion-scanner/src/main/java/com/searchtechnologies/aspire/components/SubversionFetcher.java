/**
 * Copyright Search Technologies 2014
 */

package com.searchtechnologies.aspire.components;

import com.searchtechnologies.aspire.docprocessing.fetchurl.FetchURLStage;
import com.searchtechnologies.aspire.framework.utilities.Base64Utilities;
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
public class SubversionFetcher extends FetchURLStage{
  /**
   * This is the entry point of the fetcher component. Make sure this method is thread safe!!!
   * <p/>
   * Open an input stream for the item content here. At this point, you can assume containers were already filtered.
   */
  @Override
  public void process(Job j) throws AspireException{
    AspireObject doc = j.get();
    AspireObject conn = doc.get("connectorSource");

    if (conn != null){

      String username = conn.getText("username", null);
      String password = conn.getText("password", null);

      if (StringUtilities.isNotEmpty(username)){
        requestProperties.put("Authorization", getAuthorityStringEncoded(username, password));
      }
    }
    super.process(j);
  }

  private String getAuthorityStringEncoded(String username, String password){
    String authString = "";

    try{
      authString = username + ":" + SecurityUtilities.decryptString(password);
    }
    catch (AspireException e){
      e.printStackTrace();
    }

    return "Basic " + new String(Base64Utilities.encodeBase64(authString.getBytes()));
  }
}