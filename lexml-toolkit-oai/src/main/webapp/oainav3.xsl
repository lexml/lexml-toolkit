<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:oai="http://www.openarchives.org/OAI/2.0/" xmlns:oai_id="http://www.openarchives.org/OAI/2.0/oai-identifier" xmlns:oai_branding="http://www.openarchives.org/OAI/2.0/branding/" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:oai_etdms="http://www.ndltd.org/standards/metadata/etdms/1.0/" xmlns:oai_lexml='http://www.lexml.gov.br/oai_lexml' xmlns:lexml_profile="http://www.lexml.gov.br/profile_lexml" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:toolkit="http://oai.dlib.vt.edu/OAI/metadata/toolkit">
	<xsl:output method="html" version="4.0" encoding="UTF-8"/>
	<xsl:template match="/oai:OAI-PMH">
		<html>
			<head>
				<meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8"/>
				<title>					
					Lexml OAI-PMH - <xsl:value-of select="oai:request/@verb"/> Response
				</title>
				<script src="oaicat.js" language="JavaScript"/>
				<style type="text/css" media="all">
					@import url("oainav3.css");
				</style>
			</head>
			<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
				<table width="797" border="0" cellpadding="0" cellspacing="6">
					<tr valign="top">
						<td>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr valign="top">
									<td width="120">
									    <div style="float: right;"><strong>Repositório OAI-PMH</strong></div>
										<a href="http://www.lexml.gov.br">
											<img src="images/logo3sf.gif" border="0" align="middle" vspace="0" hspace="14"/>
										</a>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr valign="top">
						<td style="background-image:url(images/toolbarbackground.gif) ; height:37px;">
							<xsl:text disable-output-escaping="yes">&#160;</xsl:text>
							<a href="?verb=Identify">Identify</a>
							<xsl:text disable-output-escaping="yes">&#160;</xsl:text>
							<span class="divider">|</span>
							<xsl:text disable-output-escaping="yes">&#160;</xsl:text>
							<a href="?verb=ListSets">ListSets</a>
							<span class="divider">|</span>
							<xsl:text disable-output-escaping="yes">&#160;</xsl:text>
							<a href="?verb=ListMetadataFormats">ListMetadataFormats</a>
							<span class="divider">|</span>
							<xsl:text disable-output-escaping="yes">&#160;</xsl:text>
							<a href="#" onClick="MM_showHideLayers('ListIdentifierslayer','','hide', 'ListRecordslayer','','hide','GetRecordlayer','','show', 'identifierlayer','','show', 'ListErrorslayer','','hide'); return false;">GetRecord</a>
							<xsl:text disable-output-escaping="yes">&#160;</xsl:text>
							<span class="divider">|</span>
							<xsl:text disable-output-escaping="yes">&#160;</xsl:text>
							<a href="#" onClick="MM_showHideLayers('ListIdentifierslayer','','show', 'ListRecordslayer','','hide','GetRecordlayer','','hide', 'identifierlayer','','hide', 'ListErrorslayer','','hide'); return false;">ListIdentifiers</a>
							<xsl:text disable-output-escaping="yes">&#160;</xsl:text>
							<span class="divider">|</span>
							<xsl:text disable-output-escaping="yes">&#160;</xsl:text>
							<a href="#" onClick="MM_showHideLayers('ListIdentifierslayer','','hide', 'ListRecordslayer','','show','GetRecordlayer','','hide', 'identifierlayer','','hide', 'ListErrorslayer','','hide'); return false;">ListRecords</a>
							<xsl:text disable-output-escaping="yes">&#160;</xsl:text>
							<span class="divider">|</span>
							<xsl:text disable-output-escaping="yes">&#160;</xsl:text>
							<a href="#" onClick="MM_showHideLayers('ListIdentifierslayer','','hide', 'ListRecordslayer','','hide','GetRecordlayer','','hide', 'identifierlayer','','hide', 'ListErrorslayer','','show'); return false;">ListErrors</a>
							<xsl:text disable-output-escaping="yes">&#160;</xsl:text>
							<span class="divider">|</span>
						</td>
					</tr>
					<tr>
						<td align="center">
							<table width="265">
								<tr>
									<td align="right">
										<div id="ListIdentifierslayer" class="formlayer">
											<xsl:call-template name="formlayer">
												<xsl:with-param name="ListIdentifiers">
													sim
												</xsl:with-param>
											</xsl:call-template>
										</div>
										<div id="ListRecordslayer" class="formlayer">
											<xsl:call-template name="formlayer">
												<xsl:with-param name="ListRecords">
													sim
												</xsl:with-param>
											</xsl:call-template>
										</div>
                                        <div id="ListErrorslayer" class="formlayer">
                                            <xsl:call-template name="formlayer">
                                                <xsl:with-param name="ListErrors">
                                                    sim
                                                </xsl:with-param>
                                            </xsl:call-template>
                                        </div>
										<div id="GetRecordlayer" class="formlayer">
											<xsl:call-template name="formlayer">
												<xsl:with-param name="GetRecord">
													sim
												</xsl:with-param>
											</xsl:call-template>
										</div>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr valign="top">
						<td bgcolor="#cccccc">
							<table width="100%" border="0" cellpadding="4" cellspacing="0">
								<xsl:apply-templates select="oai:responseDate|oai:request"/>
							</table>
						</td>
					</tr>
					<tr valign="top">
						<td>
							<table width="100%" border="0" cellpadding="0" cellspacing="0">
								<xsl:apply-templates select="oai:Identify|oai:GetRecord|oai:ListIdentifiers|oai:ListErrors|oai:ListMetadataFormats|oai:ListRecords|oai:ListSets|oai:error"/>
							</table>
						</td>
					</tr>
					<tr valign="top">
						<td bgcolor="#F9DD50" height="10"/>
					</tr>
					<tr valign="top">
						<td align="right" colspan="2">
							<a href="http://www.openarchives.org">
								<img border="0" src="http://www.openarchives.org/images/OA100.gif" align="right" height="50"/>
							</a>
						</td>
					</tr>
				</table>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="oai:Identify|oai:GetRecord">
		<h2>
			<xsl:value-of select="name()"/>
		</h2>
		<table width="100%" border="0" cellspacing="2" cellpadding="0">
			<xsl:apply-templates/>
		</table>
	</xsl:template>
	<xsl:template match="oai:ListMetadataFormats">
		<h2>
			<xsl:value-of select="name()"/>
		</h2>
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="oai:ListSets">
		<tr>
			<td>
				<h2>
					<xsl:value-of select="name()"/>
				</h2>
			</td>
			<td>
				<xsl:call-template name="resumptionLayer"/>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<table width="100%" border="0" cellspacing="0" cellpadding="4">
					<tr valign="top">
						<td width="150">
							<strong>setSpec</strong>
						</td>
						<td>
							<strong>setName</strong>
						</td>
					</tr>
					<xsl:apply-templates/>
				</table>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="oai:ListRecords">
		<tr>
			<td>
				<h2>
					<xsl:value-of select="name()"/>
				</h2>
			</td>
			<td>
				<xsl:call-template name="resumptionLayer"/>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<table width="100%" border="0" cellspacing="2" cellpadding="0">
					<xsl:apply-templates/>
				</table>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="oai:ListIdentifiers|oai:ListErrors">
		<tr>
			<td>
				<h2>
					<xsl:value-of select="name()"/>
				</h2>
			</td>
			<td>
			    <xsl:if test="descendant::oai:resumptionToken">
				   <xsl:call-template name="resumptionLayer"/>
			    </xsl:if>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<table width="100%" border="0" cellspacing="2" cellpadding="0">
					<xsl:apply-templates/>
				</table>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="oai:error">
		<h2>
			<font color="red">
				<xsl:value-of select="name()"/>
			</font>
		</h2>
		<table width="100%" border="0" cellspacing="2" cellpadding="0">
			<tr valign="top">
				<td width="200">
					<strong>
						<xsl:value-of select="@code"/>
					</strong>
				</td>
				<td>
					<xsl:value-of select="."/>
				</td>
			</tr>
		</table>
	</xsl:template>
	<xsl:template match="oai:record" name="simpleRecord">
		<tr valign="top">
			<td>
				<table width="100%" border="0" cellspacing="2" cellpadding="0">
					<xsl:apply-templates/>
				</table>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="oai:header">
		<tr>
			<td colspan="3" bgcolor="#D0D0D0">Record Header</td>
		</tr>
		<tr valign="top">
			<td>
				<xsl:attribute name="class"><xsl:value-of select="concat('tdstatus',@status)"/></xsl:attribute>
				<table width="100%" border="0" cellspacing="4" cellpadding="0">
					<tr valign="top">
						<td width="420">
							<strong>Identifier: </strong>
							<xsl:value-of select="./oai:identifier"/>
						</td>
						<td class="tdspacer"/>
						<td>
							<strong>Datestamp: </strong>
							<xsl:value-of select="./oai:datestamp"/>
						</td>
						<td class="tdspacer"/>
						<xsl:choose>
							<xsl:when test="@status">
								<td>
									<strong>Status: </strong>
									<xsl:value-of select="@status"/>
								</td>
							</xsl:when>
							<xsl:otherwise>
								<td>
								</td>
							</xsl:otherwise>
						</xsl:choose>
					</tr>
				</table>
			</td>
		</tr>
		<xsl:choose>
			<xsl:when test="@status">
				<tr>
					<td colspan="3" class="deleted">Record Metadata Unavaiable</td>
				</tr>
			</xsl:when>
			<xsl:when test="ancestor::oai:ListErrors">
				<tr>
					<td colspan="3" bgcolor="#D0D0D0">Record Status</td>
				</tr>
			</xsl:when>
			<xsl:otherwise>
				<tr>
					<td colspan="3" bgcolor="#D0D0D0">Record Metadata</td>
				</tr>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="oai:metadata">
		<tr valign="top">
			<td>
				<xsl:apply-templates/>
			</td>
		</tr>
		<tr>
			<td colspan="3">
				<br/>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="oai:set">
		<tr valign="top">
			<xsl:apply-templates/>
		</tr>
	</xsl:template>
	<xsl:template match="oai:setSpec">
		<td width="150">
			<strong>
				<a>
					<xsl:attribute name="href">?verb=ListRecords&amp;metadataPrefix=oai_lexml&amp;set=<xsl:value-of select="."/></xsl:attribute>
					<xsl:value-of select="."/>
				</a>
			</strong>
		</td>
	</xsl:template>
	<xsl:template match="oai:setName">
		<td>
			<xsl:value-of select="."/>
		</td>
	</xsl:template>
	<xsl:template match="oai:responseDate">
		<tr valign="top">
			<td width="150">
				<strong>
					<xsl:value-of select="name()"/>
				</strong>
			</td>
			<td>
				<xsl:value-of select="."/>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="oai:request">
		<tr valign="top">
			<td width="150">
				<strong>
					<xsl:value-of select="name()"/>
				</strong>
			</td>
			<td>
				<xsl:value-of select="."/>?verb=<xsl:value-of select="@verb"/>
				<xsl:if test="@from">&amp;from=<xsl:value-of select="@from"/>
				</xsl:if>
				<xsl:if test="@until">&amp;until=<xsl:value-of select="@until"/>
				</xsl:if>
				<xsl:if test="@set">&amp;set=<xsl:value-of select="@set"/>
				</xsl:if>
				<xsl:if test="@resumptionToken">&amp;resumptionToken=<xsl:value-of select="@resumptionToken"/>
				</xsl:if>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="oai:*">
		<tr valign="top">
			<td width="150">
				<strong>
					<xsl:value-of select="name()"/>
				</strong>
			</td>
			<td>
				<xsl:value-of select="."/>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="oai:adminEmail">
		<tr valign="top">
			<td width="150">
				<strong>
					<xsl:value-of select="name()"/>
				</strong>
			</td>
			<td>
				<cite>
					<a>
						<xsl:attribute name="href"><xsl:value-of select="."/></xsl:attribute>
						<xsl:value-of select="."/>
					</a>
				</cite>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="oai:resumptionToken" name="resumptionLayer">
ResumptionToken: <a>
			<xsl:attribute name="href">?verb=<xsl:value-of select="//oai:OAI-PMH/oai:request/@verb"/>&amp;resumptionToken=<xsl:value-of select="//oai:OAI-PMH/oai:ListRecords/oai:resumptionToken|//oai:OAI-PMH/oai:ListSets/oai:resumptionToken|//oai:OAI-PMH/oai:ListIdentifiers/oai:resumptionToken|//oai:OAI-PMH/oai:ListErrors/oai:resumptionToken"/>
</xsl:attribute>
			<xsl:value-of select="//oai:OAI-PMH/oai:ListRecords/oai:resumptionToken|//oai:OAI-PMH/oai:ListSets/oai:resumptionToken|//oai:OAI-PMH/oai:ListIdentifiers/oai:resumptionToken|//oai:OAI-PMH/oai:ListErrors/oai:resumptionToken"/>
		</a>
	</xsl:template>
	<xsl:template match="oai:identifier">
		<tr valign="top">
			<td>
				<strong>
					<xsl:value-of select="name()"/>
				</strong>
			</td>
			<td>
				<a>
					<xsl:attribute name="href">?verb=GetRecord&amp;metadataPrefix=<xsl:choose><xsl:when test="/oai:OAI-PMH/oai:request/@metadataPrefix"><xsl:value-of select="/oai:OAI-PMH/oai:request/@metadataPrefix"/></xsl:when><xsl:otherwise>oai_lexml</xsl:otherwise></xsl:choose>&amp;identifier=<xsl:value-of select="."/></xsl:attribute>
					<xsl:value-of select="."/>
				</a>
			</td>
		</tr>
	</xsl:template>
	<!--
  <xsl:template name="apply-templates-copy-all">
    <xsl:copy>
      <xsl:call-template name="apply-templates-copy-all"/>
    </xsl:copy>
  </xsl:template>
-->
	<xsl:template match="oai:description">
		<tr valign="top">
			<td>
				<strong>
					<xsl:value-of select="name()"/>
				</strong>
			</td>
			<td>
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>
	<!--
  <xsl:template match="oai_id:oai-identifier">
    <table border="0">
      <tr valign="top"><td>OAI Identifier</td></tr>
      <xsl:apply-templates/>
    </table>
  </xsl:template>
-->
	<xsl:template match="oai_id:oai-identifier">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr valign="top">
				<td>
					<strong>
						<xsl:value-of select="name()"/>:</strong>
				</td>
			</tr>
			<xsl:apply-templates/>
		</table>
	</xsl:template>
	<xsl:template match="oai_id:*">
		<tr valign="top">
			<td>
				<strong>
					<xsl:value-of select="name()"/>
				</strong>
			</td>
			<td>
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="oai_branding:branding">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<xsl:apply-templates/>
		</table>
	</xsl:template>
	<xsl:template match="oai_branding:metadataRendering">
		<tr valign="top">
			<td>
				<strong>
					<xsl:value-of select="name()"/>
				</strong>
			</td>
			<td>
				<a>
					<xsl:attribute name="href"><xsl:value-of select="."/></xsl:attribute>
					<xsl:attribute name="type"><xsl:value-of select="@mimeType"/></xsl:attribute>
					<xsl:value-of select="@metadataNamespace"/>
				</a>
			</td>
		</tr>
	</xsl:template>
	<!--
  <xsl:template match="oai_branding:metadataRendering>
    <tr valign="top">
      <td><strong><xsl:value-of select="name()"/></strong></td>
    </tr>
  </xsl:template>
-->
	<xsl:template match="oai_branding:collectionIcon">
		<tr valign="top">
			<td>
				<strong>
					<xsl:value-of select="name()"/>
				</strong>
			</td>
			<td>
				<!--
        <a href="/oai/index.html"><img src="/oai/oaicat_icon.gif" alt="Testing"/></a>
        -->
				<a>
					<xsl:attribute name="href"><xsl:value-of select="oai_branding:link"/></xsl:attribute>
					<img>
						<xsl:attribute name="width"><xsl:value-of select="oai_branding:width"/></xsl:attribute>
						<xsl:attribute name="height"><xsl:value-of select="oai_branding:height"/></xsl:attribute>
						<xsl:attribute name="src"><xsl:value-of select="oai_branding:url"/></xsl:attribute>
						<xsl:attribute name="alt"><xsl:value-of select="oai_branding:title"/></xsl:attribute>
					</img>
				</a>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="oai_branding:*">
		<tr valign="top">
			<td>
				<strong>
					<xsl:value-of select="name()"/>
				</strong>
			</td>
			<td>
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="oai:metadataFormat">
		<table width="100%" border="0" cellspacing="0" cellpadding="4">
			<tr valign="top">
				<td width="150">
					<strong>metadataPrefix</strong>
				</td>
				<td>
					<a>
						<xsl:attribute name="href">?verb=ListRecords&amp;metadataPrefix=<xsl:value-of select="oai:metadataPrefix"/></xsl:attribute>
						<xsl:value-of select="oai:metadataPrefix"/>
					</a>
				</td>
			</tr>
			<tr valign="top">
				<td width="150">
					<strong>schema</strong>
				</td>
				<td>
					<a>
						<xsl:attribute name="href"><xsl:value-of select="oai:schema"/></xsl:attribute>
						<xsl:value-of select="oai:schema"/>
					</a>
				</td>
			</tr>
			<tr valign="top">
				<td width="150">
					<strong>metadataNamespace</strong>
				</td>
				<td>
					<a>
						<xsl:attribute name="href"><xsl:value-of select="oai:metadataNamespace"/></xsl:attribute>
						<xsl:value-of select="oai:metadataNamespace"/>
					</a>
				</td>
			</tr>
		</table>
		<hr/>
	</xsl:template>
	<xsl:template match="oai_dc:dc">
		<table width="100%" border="0" cellspacing="4" cellpadding="0">
			<xsl:apply-templates/>
		</table>
	</xsl:template>
	<xsl:template match="oai_etdms:thesis">
		<table width="100%" border="0" cellspacing="4" cellpadding="0">
			<xsl:apply-templates/>
		</table>
	</xsl:template>
	<xsl:template match="oai_lexml:LexML">
		<table width="100%" border="0" cellspacing="1" cellpadding="0">
			<xsl:apply-templates/>
		</table>
	</xsl:template>
	<xsl:template name="formlayer">
		<xsl:param name="ListRecords"/>
		<xsl:param name="ListErrors"/>
		<xsl:param name="ListIdentifiers"/>
		<xsl:param name="GetRecord"/>
		<form action="?" method="post">
			<xsl:if test="$ListRecords or $ListIdentifiers or $ListErrors">
				from (yyyy-mm-dd): <input type="text" name="from" size="20" maxlength="80" value=""/>
				<br/>
			</xsl:if>
			<xsl:if test="$ListRecords or $ListIdentifiers or $ListErrors">
				until (yyyy-mm-dd): <input type="text" name="until" size="20" maxlength="80" value=""/>
				<br/>
			</xsl:if>
			<xsl:if test="$ListRecords or $ListIdentifiers or $GetRecord">
				*metadataPrefix: <input type="text" name="metadataPrefix" size="20" maxlength="80" value="oai_lexml"/>
				<br/>
			</xsl:if>
			<xsl:if test="$GetRecord or $ListErrors">
				identifier: <input type="text" name="identifier" size="20" maxlength="80" value=""/>
				<br/>
			</xsl:if>
			<xsl:if test="$ListRecords or $ListIdentifiers or $ListErrors">
				set: <input type="text" name="set" size="20" maxlength="80" value=""/>
				<br/>
			</xsl:if>
			<!--xsl:if test="$ListRecords or $ListIdentifiers">
				resumptionToken:<input type="text" name="resumptionToken" size="20" maxlength="1024" value=""/>
				<br/>
			</xsl:if-->
			<hr/>
			<input type="reset" value="Cancel" onClick="MM_showHideLayers('ListIdentifierslayer','','hide', 'ListRecordslayer','','hide','GetRecordlayer','','hide', 'identifierlayer','','hide', 'ListErrorslayer','','hide'); return false;"/>
			<xsl:text disable-output-escaping="yes">&#160;</xsl:text>
			<xsl:if test="$ListRecords">
				<input type="hidden" name="verb" value="ListRecords"/>
				<input type="submit" value="ListRecords"/>
			</xsl:if>
			<xsl:if test="$ListErrors">
				<input type="hidden" name="verb" value="ListErrors"/>
				<input type="submit" value="ListErrors"/>
			</xsl:if>
			<xsl:if test="$ListIdentifiers">
				<input type="hidden" name="verb" value="ListIdentifiers"/>
				<input type="submit" value="ListIdentifiers"/>
			</xsl:if>
			<xsl:if test="$GetRecord">
				<input type="hidden" name="verb" value="GetRecord"/>
				<input type="submit" value="GetRecord"/>
			</xsl:if>
		</form>
	</xsl:template>
	<xsl:template match="oai_lexml:*">
		<tr valign="top">
			<td width="150">
				<strong>
					<xsl:value-of select="name()"/>:</strong>
			</td>
			<td>
				<table width="100%" border="0" cellspacing="4" cellpadding="0">
					<xsl:apply-templates/>
				</table>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="oai_lexml:*/oai_lexml:*/oai_lexml:*/oai_lexml:* |oai_lexml:*/oai_lexml:*/oai_lexml:*/oai_lexml:*">
		<small>
			<b>
				<xsl:value-of select="name()"/>
			</b>: 
			<xsl:copy-of select="."/>
			<br/>
		</small>
	</xsl:template>
	<xsl:template match="dc:identifier">
		<tr valign="top">
			<td>
				<strong>
					<xsl:value-of select="name()"/>
				</strong>
			</td>
			<td>
				<a>
					<xsl:attribute name="href"><xsl:value-of select="."/></xsl:attribute>
					<xsl:value-of select="."/>
				</a>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="dc:*">
		<tr valign="top">
			<td width="150">
				<strong>
					<xsl:value-of select="name()"/>
				</strong>
			</td>
			<td>
				<xsl:value-of select="."/>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="oai_etdms:*">
		<tr valign="top">
			<td width="150">
				<strong>
					<xsl:value-of select="name()"/>
				</strong>
			</td>
			<xsl:if test="@resource">
				<td>
					<a>
						<xsl:attribute name="href"><xsl:value-of select="@resource"/></xsl:attribute>
						<xsl:value-of select="."/>
					</a>
				</td>
			</xsl:if>
			<xsl:if test="not(@resource)">
				<td>
					<xsl:value-of select="."/>
				</td>
			</xsl:if>
		</tr>
	</xsl:template>
	<xsl:template match="toolkit:toolkit">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr valign="top">
				<td width="150">
					<strong>
						<xsl:value-of select="name()"/>
					</strong>
				</td>
				<td>
					<a>
						<xsl:attribute name="href"><xsl:value-of select="toolkit:URL"/></xsl:attribute>
						<img border="0" cellspacing="0" cellpadding="0">
							<xsl:attribute name="alt"><xsl:value-of select="toolkit:title"/></xsl:attribute>
							<xsl:attribute name="src"><xsl:value-of select="toolkit:toolkitIcon"/></xsl:attribute>
						</img>
					</a>
    (version <xsl:value-of select="toolkit:version"/>)
        </td>
			</tr>
		</table>
	</xsl:template>
	<xsl:template match="lexml_profile:ConfiguracaoProvedor">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr valign="top">
				<td width="150" bgcolor="#CCCCCC">
					<strong>
						<xsl:value-of select="name()"/>
					</strong> (Data de Geração: <xsl:value-of select="@dataGeracao"/>)
				</td>
			</tr>
			<tr valign="top">
				<td>
					<xsl:apply-templates/>
				</td>
			</tr>
		</table>
	</xsl:template>
	<!-- ConfiguracaoProvedor -->
	<xsl:template match="lexml_profile:Provedor">
		<h4 style="background-color:#DDF">
			<xsl:value-of select="name()"/> : <xsl:value-of select="@nome"/>	 (IdProvedor = <xsl:value-of select="@idProvedor"/>
			<xsl:for-each select="lexml_profile:Administrador">
				,
				IdResponsavel = <xsl:value-of select="@idResponsavel"/>
			</xsl:for-each>
		)
		</h4>
		<xsl:for-each select="./lexml_profile:Publicador">
			<xsl:call-template name="publicador"/>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="lexml_profile:Administrador">

	</xsl:template>
	<xsl:template match="lexml_profile:Publicador" name="publicador">
		<br/>		
			<xsl:value-of select="name()"/> : <b><xsl:value-of select="@nome"/></b>	 (IdPublicador = <xsl:value-of select="@idPublicador"/>
			<xsl:for-each select="lexml_profile:Responsavel">
				,
				IdResponsavel = <xsl:value-of select="@idResponsavel"/>
			</xsl:for-each>
		)				
		<table width="100%" border="1" cellpadding="0" cellspacing="0">
			<tr>
				<td width="10%" align="center">Localidade</td>
				<td width="38%" align="center">Autoridade</td>
				<td width="52%" align="center">Tipo</td>
			</tr>
			<xsl:for-each select="./lexml_profile:Perfil">
				<xsl:call-template name="perfil"/>
			</xsl:for-each>
		</table>
	</xsl:template>
	<xsl:template match="lexml_profile:Perfil" name="perfil">
		<tr class="perfil">
			<td width="10%" class="perfil">
				<xsl:for-each select="@localidade">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</td>
			<td width="38%" class="perfil">
				<xsl:for-each select="@autoridade">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</td>
			<td width="52%" class="perfil">
				<xsl:for-each select="@tipoDocumento">
					<xsl:value-of select="."/>
				</xsl:for-each>
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>
