/**
 * Copyright Search Technologies 2014
 */
package com.searchtechnologies.aspire.components;

import com.searchtechnologies.aspire.scanner.DSConnection;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.searchtechnologies.aspire.framework.utilities.SecurityUtilities;
import com.searchtechnologies.aspire.services.AspireException;
import com.searchtechnologies.aspire.services.logging.ALogger;

/**
 * Stores a connection or authentication mechanism requiered by a scanner to authenticate to the repository.
 * 
 * Avoids creating multiple instances of the same authentication object.
 * 
 * @author pmartinez
 *
 */
public class SubversionDSConnection implements DSConnection {

  private SVNRepository repository = null;
  private String url = null;
  private String password = null;
  private String user = null;
  private ALogger logger = null;

  /**
   * Initialize the class
   * @param url to set the url of the repository
   * @param user the user to access the repository
   * @param password the password to access the repository
   * @param aLogger 
   */
  public SubversionDSConnection(String url, String user, String password, ALogger aLogger){
    this.url = url;
    this.user = user;
    this.password = password;
    this.logger = aLogger;
  }

  /**
   * Create and open a connection object to the repository
   * @throws AspireException if the connection couldn't be open
   */
  @SuppressWarnings("deprecation")
  @Override
  public void open() throws AspireException {
    try{

      String decrypted = SecurityUtilities.decryptString(password);

      repository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(url));
      ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(user, decrypted);
      repository.setAuthenticationManager(authManager);
      
      repository.testConnection();
      logger.info("Successfully initialized SVN root repository!");
    }
    catch (SVNException e){
      close();
      throw new AspireException("SubversionDSConnection.open", e, "Couldn't connect to SVN root repository: %s", url);
    }
  }

  /**
   * Closes the connection object 
   */
  @Override
  public void close() {
    repository.closeSession();
    repository = null;

  }

  /**
   * Indicates whether or not the connection to the repository is open
   * @return true if it's connected, false otherwise
   */
  @Override
  public boolean isConnected() {
    return repository != null;
  }

  /**
   * This function return all the repository
   * @return the repository
   */
  public SVNRepository getRepository(){
    return repository;
  }

}
