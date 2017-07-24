<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:import href="/aspire/files/common.xsl"/>
  <xsl:output indent="yes" method="html" />

  <xsl:template match="/">
    <html><head><title>New Component</title></head>
    <body>
      <xsl:call-template name="standard-result-page"/>
    </body>
    </html>
  </xsl:template>
</xsl:stylesheet>