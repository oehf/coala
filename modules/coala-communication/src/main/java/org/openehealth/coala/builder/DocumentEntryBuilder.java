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
package org.openehealth.coala.builder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.openehealth.coala.domain.CoalaAuthor;
import org.openehealth.coala.domain.Patient;
import org.openehealth.coala.domain.PatientConsentPolicy;
import org.openehealth.coala.exception.XDSConfigurationErrorException;
import org.openehealth.coala.util.PXSDateConverter;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.AssigningAuthority;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Author;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.AvailabilityStatus;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Code;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.DocumentEntry;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Identifiable;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.LocalizedString;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Name;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Person;

/**
 * This class builds document entries. It reads it's configuration from coala-document.properties.
 * 
 * @author kmaerz, ssiekmann
 * 
 */
public class DocumentEntryBuilder {
	
	private static String encoding;
	private static String langCode;
	private static String[] classCode;
	private static String[] confiCode;
	private static String[] formatCode;
	private static String uniqueBase;
	private static String[] healthCode;
	private static String[] practiceCode;
	private static String repositoryUniqueId;
	private static String consentDefaultTitle;
	private static String[] typeCode;

	private PXSDateConverter pxsDateConverter;

	public void setPxsDateConverter(PXSDateConverter pxsDateConverter) {
		this.pxsDateConverter = pxsDateConverter;
	}

	/**
	 * Load configuration values and do basic checks
	 */
	static {
		ResourceBundle properties = ResourceBundle.getBundle("coala-document");

		encoding = properties.getString("coala.consent.encoding");

		langCode = properties.getString("coala.consent.language.code");

		classCode = properties.getString("coala.consent.class.code").split(";");
		
		uniqueBase = properties.getString("coala.document.base.unique.id");
		
		repositoryUniqueId = properties.getString("coala.repository.unique.id");
		
		consentDefaultTitle = properties.getString("coala.consent.default.title");
		
		if (classCode.length != 3)
			throw new XDSConfigurationErrorException(
					"The consent class code is corrupted in config file. 3 values according the Code-object are required!");

		confiCode = properties.getString("coala.consent.confidentiality.code")
				.split(";");
		if (confiCode.length != 3)
			throw new XDSConfigurationErrorException(
					"The consent confidentiality code is corrupted in config file. 3 values according the Code-object are required!");

		formatCode = properties.getString("coala.consent.format.code").split(
				";");
		if (formatCode.length != 3)
			throw new XDSConfigurationErrorException(
					"The consent format is corrupted in config file. 3 values according the Code-object are required!");

		healthCode = properties.getString(
				"coala.consent.healthcare.facility.type.code").split(";");
		if (healthCode.length != 3)
			throw new XDSConfigurationErrorException(
					"The healthcare facility type code is corrupted in config file. 3 values according the Code-object are required!");

		practiceCode = properties.getString(
				"coala.consent.practice.setting.code").split(";");
		if (practiceCode.length != 3)
			throw new XDSConfigurationErrorException(
					"The practice setting code is corrupted in config file. 3 values according the Code-object are required!");
		
		typeCode = properties.getString(
			"coala.consent.document.type.code").split(";");
			if (typeCode.length != 3)
				throw new XDSConfigurationErrorException(
			"The consent type code is corrupted in config file. 3 values according the Code-object are required!");
			

		
	}

	/**
	 * Creates an Document entry from the given data.
	 * 
	 * @param coalaAuthor
	 *            the coala Author that carries information about the consent
	 *            creator
	 * @param patient
	 *            the patient to whom this consent belongs
	 * @param policyType
	 *            the policy type for the consent
	 * @param validFrom The Time the validity of the consent begins
	 * @param validUntil The Time the validity of the consent ends
	 * @return A document entry containing all the necessary metadata
	 * @throws XDSConfigurationErrorException
	 */
	public DocumentEntry createDocumentEntry(CoalaAuthor coalaAuthor,
			Patient patient, PatientConsentPolicy policyType, Date validFrom, Date validUntil)
			throws XDSConfigurationErrorException {

		// Basic checks
		if (patient == null)
			throw new IllegalArgumentException("Patient cannot be null.");
		if (policyType == null)
			throw new IllegalArgumentException("policyType cannot be null.");
		if (coalaAuthor == null)
			throw new IllegalArgumentException("author cannot be null.");

		// Basic Type conversion
		Identifiable patientID = new Identifiable(patient.getPatientID(),
				new AssigningAuthority(
						patient.getPatientIDAssigningAuthorityUniversalId()));
		Code policy = new Code(policyType.getCode(), new LocalizedString(
				policyType.getCode(), "en-US", "UTF-8"), "Privacy Policies");

		// Create Author
		Author author = new Author();
		Person person = new Person();
		Name name = new Name(coalaAuthor.getFamilyName());
		name.setGivenName(coalaAuthor.getGivenName());
		name.setPrefix(coalaAuthor.getTitle());
		person.setName(name);
		author.setAuthorPerson(person);

		try {

			DocumentEntry docEntry = new DocumentEntry();
			docEntry.getAuthors().add(author);
			docEntry.setAvailabilityStatus(AvailabilityStatus.APPROVED);

			docEntry.setClassCode(new Code(classCode[0], new LocalizedString(
					classCode[1], langCode, encoding), classCode[2]));
			// Confidentiality
			docEntry.getConfidentialityCodes().add(
					new Code(confiCode[0], new LocalizedString(confiCode[1],
							langCode, encoding), confiCode[2]));
			Date creationTime = new Date();
			docEntry.setCreationTime(pxsDateConverter.DateToString(creationTime));
			
			// Service Start- and Stop Time
			docEntry.setServiceStartTime(pxsDateConverter.DateToString(validFrom));
			docEntry.setServiceStopTime(pxsDateConverter.DateToString(validUntil));
			
			docEntry.setEntryUuid("newConsent");
			// set policy
			docEntry.getEventCodeList().add(policy);
			// consent format

			docEntry.setFormatCode(new Code(formatCode[0], new LocalizedString(
					formatCode[1], langCode, encoding), formatCode[2]));
			// Generate unique ID
			List<AvailabilityStatus> availableList = new ArrayList<AvailabilityStatus>();
			availableList.add(AvailabilityStatus.APPROVED);

			String uniqueId = uniqueBase + "." + patientID.getId() + "." + "."
					+ creationTime.getTime();

			docEntry.setHealthcareFacilityTypeCode(new Code(healthCode[0],
					new LocalizedString(healthCode[1]), healthCode[2]));
			docEntry.setLanguageCode(langCode);
			docEntry.setMimeType("text/xml");
			docEntry.setPatientId(patientID);
			// practice setting

			docEntry.setPracticeSettingCode(new Code(practiceCode[0],
					new LocalizedString(practiceCode[1], langCode, encoding),
					practiceCode[2]));
			docEntry.setRepositoryUniqueId(repositoryUniqueId);
			docEntry.setSourcePatientId(patientID);
			docEntry.setTitle(new LocalizedString(consentDefaultTitle
					+ patientID.getId(), langCode, encoding));
			// type code
			docEntry.setTypeCode(new Code(typeCode[0], new LocalizedString(
					typeCode[1], langCode, encoding), typeCode[2]));
			docEntry.setUniqueId(uniqueId);
			return docEntry;
		} catch (MissingResourceException e) {
			throw new XDSConfigurationErrorException(
					"Configuration file seems to be corrupted. Check for missing properties.");
		} catch (ClassCastException e2) {
			throw new XDSConfigurationErrorException(
					"Configuration file seems to be corrupted. Check for missing properties.");
		}
	}

}
