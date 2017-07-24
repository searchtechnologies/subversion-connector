/**
 * Copyright Search Technologies 2014
 */
package com.searchtechnologies.aspire.components;

import com.searchtechnologies.aspire.scanner.ItemType;
import com.searchtechnologies.aspire.scanner.ItemTypeEnum;

/**
 * Represents an item type from the content source. 
 * @author pmartinez
 */
public class SubversionItemType extends ItemType {

  /**
   * Enumeration of object types found in Subversion.
   */
  enum SubversionItemTypeEnum implements ItemTypeEnum {
	  root, 
	  dir, 
	  file
  }


  /**
   * Converts a string to the enum value and sets it to the itemType Id.  Can be retrieved by the getValue() method
   * @param s String to parse to the enum
   */
  public void setValue(String s){
    id = SubversionItemTypeEnum.valueOf(s);
  }
}
