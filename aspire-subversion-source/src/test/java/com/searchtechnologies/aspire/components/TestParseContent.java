package com.searchtechnologies.aspire.components;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.searchtechnologies.aspire.framework.AXPathFactory;
import com.searchtechnologies.aspire.framework.JobFactory;
import com.searchtechnologies.aspire.framework.Standards;
import com.searchtechnologies.aspire.services.*;
import com.searchtechnologies.aspire.test.UnitTestHelper;

import junit.framework.TestCase;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class TestParseContent extends TestCase {
  
  UnitTestHelper unitTest = new UnitTestHelper(this.getClass());
  
  @Test
  public void testProcess() throws AspireException, FileNotFoundException {
    
    File file = unitTest.getSourceFile("testSubversion" + File.separator + "SubversionSourceInfo.java");
    InputStream inputStream = new FileInputStream(file);
    
    String url = "https://svn.searchtechnologies.com/svn/aspire/svn-connector-test/" + file.getName();
    
    AspireObject doc = new AspireObject("doc");
    doc.add("displayName", "SubversionTest");
    doc.add("url", url);
    
    Job job = JobFactory.newInstance(doc);
    job.putVariable(Standards.Basic.CONTENT_STREAM_KEY, inputStream);
    
    SubversionJavaParser subversionJavaParser = new SubversionJavaParser();
    subversionJavaParser.process(job);
    
    assertEquals("SubversionTest", doc.getText("displayName"));
    assertEquals(url, doc.getText("url"));
    
    AXPath fields = AXPathFactory.newInstance("/doc/extension/field");
    
    String attributeName = "";
    String attribute = "";
    String value = "";
    
    //Run over the fields returned by the doc
    for(AspireObject element : fields.getElementList(doc)) {
      attributeName = element.getAttributeNames().get(0);
      attribute = (String) element.getAttributeValue(attributeName);
      value = element.getText(); 
      
      if (attribute.equals("title"))
        assertEquals("com.searchtechnologies.aspire.components." + file.getName(), value);
      else if (attribute.equals("filename"))
        assertEquals(file.getName(), value);
      else if (attribute.equals("className"))
        assertEquals(FilenameUtils.getBaseName(file.getName()), value);
      else if (attribute.equals("classNameSplit"))
        assertEquals("Subversion Source Info ", value);
      else if (attribute.equals("methods") || attribute.equals("classreferences") || attribute.equals("imports") || attribute.equals("package"))
        assertTrue(value != null);
      else if (attribute.equals("isTestCase"))
        assertEquals("no", value);
    }
    
  }
}
