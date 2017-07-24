/**
 * Copyright Search Technologies 2014
 */
package com.searchtechnologies.aspire.components;

import java.util.Properties;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

/**
 * 
 * @author pmartinez
 *
 */
public class ParseContent
{
  static Properties properties = new Properties();

  public Properties parse(String content){
    
    ASTParser parser = ASTParser.newParser(AST.JLS3);
    parser.setSource(content.toCharArray());
    parser.setKind(ASTParser.K_COMPILATION_UNIT);
    parser.setResolveBindings(true);
    parser.setBindingsRecovery(true);
    parser.setStatementsRecovery(true);

    final CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);

    compilationUnit.accept(new AspireASTVisitor());
    
    return properties;
  }
  
  /**
   * 
   * @author pmartinez
   *
   */
  public class AspireASTVisitor extends ASTVisitor{
    
    String metFullNames = "";
    String importStatements = "";
    String classrefrences = "";

    @Override
    public boolean visit(PackageDeclaration node){
      properties.setProperty("package", node.toString());
      return super.visit(node);
    }

    public boolean visit(ImportDeclaration node){
      importStatements = importStatements + "\n" + node;
      properties.setProperty("imports", importStatements);
      return super.visit(node);
    }

    public boolean visit(VariableDeclarationStatement node){

      if (classrefrences.contains(node.getType().toString())){
        return true;
      }
      else{
        classrefrences = classrefrences + "\n" + node.getType().toString();
        properties.setProperty("classrefrences", classrefrences);
      }

      return true;
    }

    @Override
    public boolean visit(MethodDeclaration node){
      String allModifiers = node.modifiers().toString().substring(1, node.modifiers().toString().length() - 1);
      String modifiers[] = allModifiers.split(",");
      String methodName = "";
      
      for (String modifier : modifiers){
        methodName = methodName + modifier + " ";
      }
      
      String allParameters = node.parameters().toString().substring(1, node.parameters().toString().length() - 1);
      String parameters[] = allParameters.split(",");
      
      if (node.isConstructor()){
        methodName = methodName + node.getName() + "(";
      }
      else{
        methodName = methodName + node.getReturnType2() + " " + node.getName() + "(";
      }

      for (String parameter : parameters){
        methodName = methodName + parameter + ",";
      }

      methodName = methodName.substring(0, methodName.length() - 1);
      methodName = methodName + ")";

      metFullNames = metFullNames + "\n" + methodName;

      properties.setProperty("methods", metFullNames);
      node.thrownExceptions();

      return super.visit(node);
    }
    
  }
}
