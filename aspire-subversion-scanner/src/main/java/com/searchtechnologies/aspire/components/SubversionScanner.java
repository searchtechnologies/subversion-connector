/**
 * Copyright Search Technologies 2014
 */
package com.searchtechnologies.aspire.components;

import java.util.Collection;

import org.w3c.dom.Element;

import com.searchtechnologies.aspire.groupexpansion.cache.SpecialAclStore;
import com.searchtechnologies.aspire.groupexpansion.cache.UserGroupCache;
import com.searchtechnologies.aspire.scanner.AbstractHierarchicalScanner;
import com.searchtechnologies.aspire.scanner.ItemType;
import com.searchtechnologies.aspire.scanner.SourceInfo;
import com.searchtechnologies.aspire.services.AspireException;
import com.searchtechnologies.aspire.services.AspireObject;
import com.searchtechnologies.aspire.services.groupexpansion.UoG;

/**
 * Sample hierarchical source connector. Fetches documents and folders from a given directory.
 * 
 * <p/> Notice this class extends from AbstractHierarchicalScanner, which implements most of the necessary functions.
 * @author pmartinez
 */
public class SubversionScanner extends AbstractHierarchicalScanner {
  
  /**
   * Does any additional component initialization required by the scanner. Occurs during component initialization. 
   * @param config Element object with the component configuration from the application bundle.
   */
  @Override
  public void doAdditionalInitialization(Element config) throws AspireException {

  }


  /**
   * Initializes a new instance of SubversionInfo. Occurs every time a new source is created on the user interface right after the first scan is fired.
   * @param propertiesXml AspireObject containing the "connectorSource" information that is provided by the user interface.
   * @return A new SubversionInfo object that contains all the necessary data to start scanning.
   * @throws AspireException If there are issues parsing the given parameters.
   */
  @Override
  public SourceInfo initializeSourceInfo(AspireObject propertiesXml) throws AspireException {
    //Create a new source info
    SubversionSourceInfo info = new SubversionSourceInfo();
    info.setRelativeURL(propertiesXml.getText("relativeurl"));
    return info;
  } 

  /**
   * Creates a new instance of the ItemType specific to the Scanner.
   * @return a new ItemType instance.
   */
  @Override
  public ItemType newItemType() {
    return new SubversionItemType();
  }

  
  /**
   * Download a list of any special acls and return them to the scanner
   * for use later
   */
  @Override
  public boolean downloadSpecialAcls(SourceInfo si, SpecialAclStore specialAcls) throws AspireException {
    // Not implemented
    return false;
  }


  @Override
  public boolean canAccessSpecialAcl(byte[] specialAcl, UoG uog, Collection<UoG> grps) {
    // Not implemented
    return false;
  }


  @Override
  public boolean downloadUsersAndGroups(SourceInfo si, UserGroupCache userGroupMap, Collection<UoG> externalUserGroupList)
      throws AspireException {
    // Not implemented
    return false;
  }
}
