<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:res="http://schemas.statsbiblioteket.dk/result/"
                xmlns:qa="http://schemas.statsbiblioteket.dk/qaresult/">

    <xsl:output omit-xml-declaration="yes" indent="yes"/>


    <xsl:param name="disabledChecks" select="''">

    </xsl:param>
    <xsl:param name="batchID"/>
    <!--<xsl:template match="node()|@*" name="identity">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>
-->

    <xsl:template match="*">
         <xsl:element name="qa:{local-name()}">
             <!-- copy attributes if any -->
             <xsl:copy-of select="@*"/>
             <xsl:apply-templates/>
         </xsl:element>
     </xsl:template>


    <xsl:template match="/res:result">
        <xsl:element name="qa:qaresult">
            <xsl:attribute name="tool">qa<xsl:value-of select="@tool"/></xsl:attribute>
            <xsl:attribute name="version">
                <xsl:value-of select="@version"/>
            </xsl:attribute>
            <xsl:attribute name="batchID">
                <xsl:value-of select="$batchID"/>
            </xsl:attribute>
            <xsl:if test="string-length($disabledChecks)>0">
            <xsl:attribute name="disabledChecks">
                <xsl:value-of select="$disabledChecks"/>
            </xsl:attribute>
            </xsl:if>
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="res:date"/>

    <xsl:template match="res:outcome">
        <xsl:element name="qa:result">
            <xsl:value-of select="text()"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="res:failures">
        <xsl:element name="qa:qafailures">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="res:failure">
        <xsl:element name="qa:qafailure">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>


</xsl:stylesheet>