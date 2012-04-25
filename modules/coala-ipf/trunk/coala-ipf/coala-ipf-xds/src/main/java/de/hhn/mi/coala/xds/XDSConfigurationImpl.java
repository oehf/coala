/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.hhn.mi.coala.xds;

/**
 * This class encapsulates all configuration details (XDS endpoint for ITI-18,
 * ITI-41, ITI-43 and OID of the assigning authority) for the XDSGateImpl. This
 * class does not check any of its values and is only used to hold the data.
 * 
 * @author siekmann
 * 
 */
public class XDSConfigurationImpl extends XDSBaseConfiguration {

	private String xdsIti18endpoint;
	private String xdsIti41endpoint;
	private String xdsIti43endpoint;
	
	private String documentsourceOid;
	private String submissionSetBaseUniqueId;
	private String defaultSubmissionSetTitle;
//	private String repositoryUniqueId;
//	private String documentBaseUniqueId;

	private String consentLanguageCode;
	private String consentEncoding;
//	private String consentClassCodeCode;
//	private String consentClassCodeDisplayname;
//	private String consentClassCodeSchemename;
//	private String consentConfidentialityCodeCode;
//	private String consentConfidentialityCodeDisplayname;
//	private String consentConfidentialityCodeSchemename;
	
	private String consentFormatCodeCode;
	private String consentFormatCodeDisplayname;
	private String consentFormatCodeSchemename;
	private String consentDocumentTypeCodeCode;
	private String consentDocumentTypeCodeDisplayname;
	private String consentDocumentTypeCodeSchemename;

	private String longDatePattern;
	private String shortDatePattern;
	
	public XDSConfigurationImpl() {
		xdsIti18endpoint = "";
		xdsIti41endpoint = "";
		xdsIti43endpoint = "";
	}

	/**
	 * Returns the XDS ITI-18 endpoint
	 * 
	 * @return the xdsIti18endpoint
	 */
	public String getXdsIti18endpoint() {
		return xdsIti18endpoint;
	}

	/**
	 * Sets the XDS ITI-18 endpoint
	 * 
	 * @param xdsIti18endpoint
	 *            the xdsIti18endpoint to set
	 */
	public void setXdsIti18endpoint(String xdsIti18endpoint) {
		this.xdsIti18endpoint = xdsIti18endpoint;
	}

	/**
	 * Returns the XDS ITI-41 endpoint
	 * 
	 * @return the xdsIti41endpoint
	 */
	public String getXdsIti41endpoint() {
		return xdsIti41endpoint;
	}

	/**
	 * Sets the XDS ITI-41 endpoint
	 * 
	 * @param xdsIti41endpoint
	 *            the xdsIti41endpoint to set
	 */
	public void setXdsIti41endpoint(String xdsIti41endpoint) {
		this.xdsIti41endpoint = xdsIti41endpoint;
	}

	/**
	 * Returns the XDS ITI-43 endpoint
	 * 
	 * @return the xdsIti43endpoint
	 */
	public String getXdsIti43endpoint() {
		return xdsIti43endpoint;
	}

	/**
	 * Sets the XDS ITI-43 endpoint
	 * 
	 * @param xdsIti43endpoint
	 *            the xdsIti43endpoint to set
	 */
	public void setXdsIti43endpoint(String xdsIti43endpoint) {
		this.xdsIti43endpoint = xdsIti43endpoint;
	}

	public String getDocumentsourceOid() {
		return documentsourceOid;
	}

	public void setDocumentsourceOid(String documentsourceOid) {
		this.documentsourceOid = documentsourceOid;
	}

	public String getSubmissionSetBaseUniqueId() {
		return submissionSetBaseUniqueId;
	}

	public void setSubmissionSetBaseUniqueId(String submissionSetBaseUniqueId) {
		this.submissionSetBaseUniqueId = submissionSetBaseUniqueId;
	}

	public String getDefaultSubmissionSetTitle() {
		return defaultSubmissionSetTitle;
	}

	public void setDefaultSubmissionSetTitle(String defaultSubmissionSetTitle) {
		this.defaultSubmissionSetTitle = defaultSubmissionSetTitle;
	}

	public String getConsentLanguageCode() {
		return consentLanguageCode;
	}

	public void setConsentLanguageCode(String consentLanguageCode) {
		this.consentLanguageCode = consentLanguageCode;
	}

	public String getConsentEncoding() {
		return consentEncoding;
	}

	public void setConsentEncoding(String consentEncoding) {
		this.consentEncoding = consentEncoding;
	}

	public String getConsentFormatCodeCode() {
		return consentFormatCodeCode;
	}

	public void setConsentFormatCodeCode(String consentFormatCodeCode) {
		this.consentFormatCodeCode = consentFormatCodeCode;
	}

	public String getConsentFormatCodeDisplayname() {
		return consentFormatCodeDisplayname;
	}

	public void setConsentFormatCodeDisplayname(String consentFormatCodeDisplayname) {
		this.consentFormatCodeDisplayname = consentFormatCodeDisplayname;
	}

	public String getConsentFormatCodeSchemename() {
		return consentFormatCodeSchemename;
	}

	public void setConsentFormatCodeSchemename(String consentFormatCodeSchemename) {
		this.consentFormatCodeSchemename = consentFormatCodeSchemename;
	}

	public String getConsentDocumentTypeCodeCode() {
		return consentDocumentTypeCodeCode;
	}

	public void setConsentDocumentTypeCodeCode(String consentDocumentTypeCodeCode) {
		this.consentDocumentTypeCodeCode = consentDocumentTypeCodeCode;
	}

	public String getConsentDocumentTypeCodeDisplayname() {
		return consentDocumentTypeCodeDisplayname;
	}

	public void setConsentDocumentTypeCodeDisplayname(
			String consentDocumentTypeCodeDisplayname) {
		this.consentDocumentTypeCodeDisplayname = consentDocumentTypeCodeDisplayname;
	}

	public String getConsentDocumentTypeCodeSchemename() {
		return consentDocumentTypeCodeSchemename;
	}

	public void setConsentDocumentTypeCodeSchemename(
			String consentDocumentTypeCodeSchemename) {
		this.consentDocumentTypeCodeSchemename = consentDocumentTypeCodeSchemename;
	}

	public String getLongDatePattern() {
		return longDatePattern;
	}

	public void setLongDatePattern(String longDatePattern) {
		this.longDatePattern = longDatePattern;
	}

	public String getShortDatePattern() {
		return shortDatePattern;
	}

	public void setShortDatePattern(String shortDatePattern) {
		this.shortDatePattern = shortDatePattern;
	}
}
