/**
 * Copyright Search Technologies 2016
 */
package com.searchtechnologies.aspire.components;

import com.searchtechnologies.aspire.connector.services.SourceInfo;

/**
 * @author jmontealegre
 */
public class SubversionSourceInfo extends SourceInfo {

	/**
	 * The Relative URL of the root item
	 */
	String relativeURL;

	/**
	 * This function return the relative path from the interface
	 * 
	 * @return the relative path
	 */
	public String getRelativeURL() {
		return relativeURL;
	}

	/**
	 * This function set the value of the relative path setting for the user in
	 * the interface
	 * 
	 * @param relativePath
	 *          inserted by the user.
	 */
	public void setRelativeURL(String relativePath) {
		this.relativeURL = relativePath;
	}
}
