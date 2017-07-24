/**
 * Copyright Search Technologies 2014
 */
package com.searchtechnologies.aspire.components;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.searchtechnologies.aspire.connector.services.RepositoryAccessProvider.RepositoryConnection;
import com.searchtechnologies.aspire.framework.utilities.SecurityUtilities;
import com.searchtechnologies.aspire.services.AspireException;

/**
 * Stores a connection or authentication mechanism requiered by a scanner to
 * authenticate to the repository.
 * 
 * Avoids creating multiple instances of the same authentication object.
 * 
 * @author jmontealegre
 *
 */
public class SubversionDSConnection implements RepositoryConnection {
	private String userName;
	private String password;
	private SVNRepository repository = null;
  private String url = null;
  private SubversionRAP logger = null;

  /**
   * Create and open a connection object to the repository
   * @param url to set the url of the repository
   * @param user the user to access the repository
   * @param password the password to access the repository
   * @param aLogger 
   * @throws AspireException if the connection couldn't be open
   */
  @SuppressWarnings("deprecation")
  public SubversionDSConnection(String url, String userName, String password, SubversionRAP aLogger) throws AspireException{
    this.url = url;
    this.userName = userName;
    this.password = password;
    this.logger = aLogger;
    try{

      String decrypted = SecurityUtilities.decryptString(password);

    //  System.setProperty("jsse.enableSNIExtension", "false");
      this.repository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(url));
      ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(userName, decrypted);
      this.repository.setAuthenticationManager(authManager);
      this.repository.testConnection();
      logger.info("Successfully initialized SVN root repository!");
    }
    catch (SVNException e){
    	this.repository.closeSession();
    	this.repository = null;
      throw new AspireException("SubversionDSConnection.open", e, "Couldn't connect to SVN root repository: %s", url);
    }
  }
  
  /**
   * Create and open a connection object to the repository
   * @throws AspireException if the connection couldn't be open
   */
  @SuppressWarnings("deprecation")
  public void open() throws AspireException {
    try{

      String decrypted = SecurityUtilities.decryptString(password);

   //   System.setProperty("jsse.enableSNIExtension", "false");
      repository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(url));
      ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(userName, decrypted);
      repository.setAuthenticationManager(authManager);
      
      repository.testConnection();
      logger.info("Successfully initialized SVN root repository!");
    }
    catch (SVNException e){
    	closeConnection();
      throw new AspireException("SubversionDSConnection.open", e, "Couldn't connect to SVN root repository: %s", url);
    }
  }

	/**
	 * Sets the user name.
	 *
	 * @param userName
	 *            The username
	 */
	public void setUsername(String userName) {
		this.userName = userName;
	}

	@Override
	public Object connection() {
		return null;
	}

	@Override
	public boolean checkConnection() {
    return repository != null;
	}

	@Override
	public void closeConnection() throws AspireException {
		if(repository != null) {
			repository.closeSession();
	    repository = null;
		}

	} 
	
	/**
   * This function return all the repository
   * @return the repository
   */
	
  public SVNRepository getRepository(){
    return repository;
  }
}
