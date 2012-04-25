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
package de.hhn.mi.coala.service;

import java.io.InputStream;
import java.util.Date;

import org.openehealth.ipf.commons.ihe.xds.core.metadata.DocumentEntry;

import de.hhn.mi.coala.builder.CDABuilder;
import de.hhn.mi.coala.builder.DocumentEntryBuilder;
import de.hhn.mi.coala.domain.CoalaAuthor;
import de.hhn.mi.coala.domain.Patient;
import de.hhn.mi.coala.domain.PatientConsent;
import de.hhn.mi.coala.domain.PatientConsentPolicy;
import de.hhn.mi.coala.exception.CdaXmlTransformerException;
import de.hhn.mi.coala.exception.XslTransformerException;
import de.hhn.mi.coala.interfacing.CDATransformationService;
import de.hhn.mi.coala.transformer.XmlTransformer;

/**
 * Provides all relevant CDA related functions for the UI.
 * 
 * @author ckarmen, mwiesner
 * 
 */
public class CDATransformationServiceImpl implements CDATransformationService {

	private XmlTransformer xmlTransformer;
	private CDABuilder cdaBuilder;

	public void setCdaBuilder(CDABuilder cdaBuilder) {
		this.cdaBuilder = cdaBuilder;
	}

	private String nameOfXSLTSheet;
	
	private DocumentEntryBuilder documentEntryBuilder;

	public void setDocumentEntryBuilder(DocumentEntryBuilder documentEntryBuilder) {
		this.documentEntryBuilder = documentEntryBuilder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hhn.mi.coala.interfacing.CDAServiceInterface#transformToHTML(de.hhn
	 * .mi.coala.domain.PatientConsent)
	 */
	@Override
	@Deprecated
	public String transformToHTML(PatientConsent consent)
			throws CdaXmlTransformerException {

		return transformToHTML(consent.getCda());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hhn.mi.coala.interfacing.CDAServiceInterface#transformToHTML(java.
	 * lang.String)
	 */
	@Override
	@Deprecated
	public String transformToHTML(String cdaDocumentContent)
			throws CdaXmlTransformerException {
		return xmlTransformer.transformXmlIntoHtml(cdaDocumentContent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hhn.mi.coala.interfacing.CDATransformationService#transformToHTML(
	 * de.hhn.mi.coala.domain.Patient, java.util.Date, java.util.Date,
	 * de.hhn.mi.coala.domain.PatientConsentPolicy,
	 * de.hhn.mi.coala.domain.CoalaAuthor,
	 * org.openehealth.ipf.commons.ihe.xds.core.metadata.DocumentEntry)
	 */
	@Override
	public String transformToHTML(Patient patient, Date validFrom,
			Date validUntil, PatientConsentPolicy policy, CoalaAuthor cauthor)
			throws CdaXmlTransformerException {

		DocumentEntry documentEntry = documentEntryBuilder.createDocumentEntry(
				cauthor, patient, policy, validFrom, validUntil);

		String cda = transformToValidPatientConsent(patient, policy, cauthor,
				documentEntry);

		return xmlTransformer.transformXmlIntoHtml(cda);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hhn.mi.coala.interfacing.CDATransformationService#
	 * transformToValidPatientConsent(de.hhn.mi.coala.domain.Patient,
	 * de.hhn.mi.coala.domain.PatientConsentPolicy, java.util.Date,
	 * java.util.Date, de.hhn.mi.coala.domain.CoalaAuthor,
	 * org.openehealth.ipf.commons.ihe.xds.core.metadata.DocumentEntry)
	 */
	@Override
	public String transformToValidPatientConsent(Patient p,
			PatientConsentPolicy policy, CoalaAuthor cauthor,
			DocumentEntry documentEntry) {

		return cdaBuilder.createConsentCDA(p, policy, documentEntry);
	}

	/*
	 * Ensures correct state of CDATransformationService instance...
	 */
	private void initXSLTDataSource() throws IllegalArgumentException,
			XslTransformerException {
		InputStream xsltStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(nameOfXSLTSheet);
		xmlTransformer = new XmlTransformer(xsltStream);
	}

	/*
	 * Needed for Spring injection
	 */
	public void setNameOfXSLTSheet(String nameOfXSLTSheet) {
		if (nameOfXSLTSheet == null || nameOfXSLTSheet.trim().isEmpty()) {
			throw new IllegalArgumentException(
					"CDA service can not be initialized with parameter 'nameOfXSLTSheet' being NULL or empty!");
		}
		this.nameOfXSLTSheet = nameOfXSLTSheet;
		try {
			initXSLTDataSource();
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (XslTransformerException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * Needed for Spring injection
	 */
	public String getNameOfXSLTSheet() {
		return nameOfXSLTSheet;
	}

}
