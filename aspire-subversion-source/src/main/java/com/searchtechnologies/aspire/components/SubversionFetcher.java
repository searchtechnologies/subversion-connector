package com.searchtechnologies.aspire.components;

import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import com.searchtechnologies.aspire.connector.services.RepositoryAccessProvider.Fetcher;
import com.searchtechnologies.aspire.connector.services.RepositoryAccessProvider.RepositoryConnection;
import com.searchtechnologies.aspire.docprocessing.fetchurl.FetchURLStage;
import com.searchtechnologies.aspire.framework.utilities.SecurityUtilities;
import com.searchtechnologies.aspire.framework.utilities.StringUtilities;
import com.searchtechnologies.aspire.services.AspireException;
import com.searchtechnologies.aspire.services.Job;

public class SubversionFetcher extends FetchURLStage implements Fetcher {

	private static final int flags = Fetcher.Flags.CUSTOM;

	private final SubversionRAP com;

	public SubversionFetcher(SubversionRAP com) {
		this.com = com;
	}

	@Override
	public void process(Job job, RepositoryConnection conn) throws AspireException {

    Map<String, String> props = null;
		String username = com.getInfo().getUser();
    if (StringUtilities.isNotEmpty(username)) {
  		String password = com.getInfo().getPassword();
      props = new HashMap<String, String>();
      props.put("Authorization", SecurityUtilities.getBasicAuthenticationString(username, password));
    }
    
    process(job, props);
	}

	@Override
	public int getFlags() {
		return flags;
	}

	@Override
	public void initializeFetcher(Element config) throws AspireException {
		
	}

	@Override
	public String url(Job job, RepositoryConnection conn) {
		return null;
	}

	@Override
	public Map<String, String> requestProperties(Job job, RepositoryConnection conn) {
		return null;
	}

	@Override
	public URLStreamHandler handler(Job job, RepositoryConnection conn) {
		return null;
	}
}
