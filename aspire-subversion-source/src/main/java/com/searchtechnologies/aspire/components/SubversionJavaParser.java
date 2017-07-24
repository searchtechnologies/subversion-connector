/**
 * Copyright Search Technologies 2014
 */

package com.searchtechnologies.aspire.components;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Element;

import com.searchtechnologies.aspire.framework.MetadataMapper;
import com.searchtechnologies.aspire.framework.MetadataMapperFactory;
import com.searchtechnologies.aspire.framework.StageImpl;
import com.searchtechnologies.aspire.framework.Standards;
import com.searchtechnologies.aspire.services.AspireException;
import com.searchtechnologies.aspire.services.AspireObject;
import com.searchtechnologies.aspire.services.Job;

/**
 * Java Parser for Subversion connector
 * 
 * @author pmartinez
 */
public class SubversionJavaParser extends StageImpl{
  
  MetadataMapperFactory mmFactory = new MetadataMapperFactory(this);
  Properties properties = new Properties();
  ParseContent parser = new ParseContent();

  @Override
  public void process(Job job) throws AspireException {
    
    InputStream contentInputStream = (InputStream) job.getVariable(Standards.Basic.CONTENT_STREAM_KEY);
    AspireObject doc = job.get();
    MetadataMapper mm = mmFactory.getNewMapper();
    
    if (doc == null)
      return;
    
    String url = doc.getText("url");
    String fileName = new File(url).getName();
    String title;
    
    if (fileName.endsWith(".java")){
      
      String content = getContent(contentInputStream);
      title = getFullJavaName(content, fileName);
      
      if (title == null){
        title = fileName;
      }

      mm.map("title", title);
      mm.map("filename", fileName);

      String className = getClassName(fileName);
      mm.map("className", className);
      mm.map("classNameSplit", getSplitNames(className));

      properties = parser.parse(content);
      mm.map("methods", properties.getProperty("methods") + "\n");
      mm.map("classreferences", properties.getProperty("classrefrences") + "\n");
      mm.map("imports", properties.getProperty("imports") + "\n");
      mm.map("package", properties.getProperty("package"));

      if (url.contains("/test")){
        mm.map("isTestCase", "yes");
      }
      else{
        mm.map("isTestCase", "no");
      }
      
      mm.writeMappingsToDocument(doc);
      doc.add(Standards.Basic.CONTENT_TAG, "\n" + content).setAttribute("source", "SubversionJavaParser");
    }
  }
  
  /**
   * This function get the content of an input stream
   * @param inputStream is the content of the job.
   * @return the content of the file converted into a String.
   * @throws AspireException 
   */
  private String getContent(InputStream inputStream) throws AspireException{

    BufferedReader bufferReader = null;
    StringBuilder stringBuilder = new StringBuilder();
    String line;
    
    try {
 
      bufferReader = new BufferedReader(new InputStreamReader(inputStream));
      
      while ((line = bufferReader.readLine()) != null) {
        stringBuilder.append(line);
        stringBuilder.append("\n");
      }
 
    } catch (IOException e) {
      throw new AspireException("SubversionJavaParser.getContent", e);
    } finally {
      if (bufferReader != null) {
        try {
          bufferReader.close();
        } catch (IOException e) {
          throw new AspireException("SubversionJavaParser.getContent", e);
        }
      }
    }
 
    return stringBuilder.toString();
  }
  
  /**
   * Return the name of the file including the package path.
   * @param contents to find the complete package name.
   * @param fileName the name of the file
   * @return the complete path of the file including the package name.
   * @throws AspireException
   */
  private String getFullJavaName(String contents, String fileName) throws AspireException{
    
    BufferedReader reader = new BufferedReader(new StringReader(contents));
    String line = null;
    String fullname = null;

    try{
      while ((line = reader.readLine()) != null){
        String newLine = line.trim();
        
        if (newLine.startsWith("package ")){
          String packageSplited[] = newLine.split("\\s+");
          return packageSplited[1].substring(0, (packageSplited[1].length() - 1)) + "." + fileName;
        }
      }
    }
    catch (IOException e){
      throw new AspireException("SubversionJavaParser.getFullJavaName", e, "Could not get java full name for file: %s", fileName);
    }

    return fullname;
  }
  
  /**
   * This function extracts the class name from the file name.
   * @param fileName to extracts the class name
   * @return the class name
   */
  private String getClassName(String fileName){
    String className = FilenameUtils.getBaseName(fileName);
    return className;
  }
  
  /**
   * This function gets any separate name from a className
   * @param className to separate
   * @return the class name separate
   */
  private String getSplitNames(String className){
    String nameSplited = "";

    for (String character : className.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")){
      nameSplited = nameSplited + character + " ";
    }

    return nameSplited;
  }

  @Override
  public void close() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void initialize(Element config) throws AspireException {
    mmFactory.initialize(config);
  }
  
}