<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:import href="/aspire/files/common.xsl"/>
  <xsl:import href="/aspire/files/component-manager.xsl"/>
  <xsl:output indent="yes" method="html" />

  <xsl:template match="/">
    <html>
      <script src="/aspire/files/js/JSON.js" />
      <script src="/aspire/files/js/JSONError.js" />
      <script src="/aspire/files/js/utilities.js" />
      <head>
        <title>Subversion Scanner Component Status - <xsl:value-of select="/status/component/@name"/></title>
      </head>
      <body>
        <xsl:call-template name="header"/>
        <table>
          <tr>
            <td>
              <img border="1">
              <xsl:attribute name="src"><xsl:value-of select="/status/@application"/><xsl:value-of select="/status/@component"/><xsl:text>/files/image.jpeg</xsl:text></xsl:attribute></img>
            </td>
            <td>
              <h2 style="margin-left:2em">SubversionScanner:  <xsl:value-of select="/status/component/@name"/></h2>
            </td>
          </tr>
        </table>

        <p />
        <hr width="50%" />
        <h4>Servlet Commands Supported</h4>
        
        [HTML forms for implementing servlet commands should be inserted here]

        <!--  Example Form: -->
        
        <form method="get">
          <xsl:call-template name="formAction"/>
          Test Input:
          <input size="50" type="text" name="testData" />
          <input type="submit" name="cmd" value="test" />
        </form>

        <p />
        <hr width="50%" />
        <xsl:call-template name="componentDetail"/>
        
        <p />
		<hr width="50%" />
		<xsl:call-template name="scannerDetail"> 		 					
			<xsl:with-param name="title" select="'Scanner Detail:'" />
			<xsl:with-param name="divName" select="'scannerDetail'" />
			<xsl:with-param name="path" select="/status/component/scanner/*" /> 
		</xsl:call-template>
		
		<p />
		<hr width="50%" />
		<xsl:call-template name="scannerDetail"> 		 					
			<xsl:with-param name="title" select="'Scanner Type Detail:'" />
			<xsl:with-param name="divName" select="'scannerTypeDetail'" />
			<xsl:with-param name="path" select="/status/component/scanner/*/*" /> 
		</xsl:call-template>
		
		<p />
		<hr width="50%" />
		<xsl:call-template name="scannerDetail"> 		 					
			<xsl:with-param name="title" select="'Scanner Specific Detail:'" />
			<xsl:with-param name="divName" select="'scannerSpecificDetail'" />
			<xsl:with-param name="path" select="/status/component/scanner/*/*/*" /> 
		</xsl:call-template>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>