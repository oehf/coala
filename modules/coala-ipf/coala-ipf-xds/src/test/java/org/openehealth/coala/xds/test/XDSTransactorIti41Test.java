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
package org.openehealth.coala.xds.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;

import javax.activation.DataHandler;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openehealth.coala.exception.XDSConfigurationErrorException;
import org.openehealth.coala.exception.XDSRequestFailedException;
import org.openehealth.coala.util.CdaDataSourceImpl;
import org.openehealth.coala.xds.XDSTransactorImpl;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.AssigningAuthority;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Author;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.AvailabilityStatus;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Code;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Document;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.DocumentEntry;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Identifiable;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.LocalizedString;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Name;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Organization;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Person;
import org.slf4j.Logger;

/**
 * Class which tests the {@link XDSTransactorImpl} for valid ITI-41 requests.
 * Because of a known bug in PXS 3.2 only one old active consent document is
 * replaced with the new consent.
 * 
 * For testing issues the patient with the ID "305009" is used to test
 * everything concerning consent creation. The user with the ID "305008" is used
 * to check the consent replacement. Because of the described problem with PXS
 * 3.2 it is important, that this patient has only ONE active consent at a time.
 * 
 * @author wkais, siekmann, mwiesner
 * 
 */
@Ignore
public class XDSTransactorIti41Test extends XDSBaseTest {
	private static final Logger LOG = org.slf4j.LoggerFactory
			.getLogger(XDSTransactorIti43Test.class);

	private List<AvailabilityStatus> stati;
	private Author author;
	private Identifiable createPatient;
	private Identifiable replacePatient;
	private Code policy;
	private DataHandler dataHandler;
	// ID for testing creation of consents
	private static final String PID_CONSENT_CREATE = "305009";
	// ID for testing replacement of consents
	private static final String PID_CONSENT_REPLACE = "305008";
	private static int consentCounter;

	@Before
	public void init() {
		stati = new ArrayList<AvailabilityStatus>();
		stati.add(AvailabilityStatus.APPROVED);
		stati.add(AvailabilityStatus.SUBMITTED);
		stati.add(AvailabilityStatus.DEPRECATED);

		// ####################################
		// Create ProvideAndRegisterDocumentSet
		// for new consent document
		// Register document for patient with
		// DEMO_MPI_PID_REGISTER
		// ####################################
		

		String assigningAutorityOID = xdsConfiguration.getAssigningAuthorityOID();
		createPatient = new Identifiable(PID_CONSENT_CREATE,
				new AssigningAuthority(assigningAutorityOID));
		replacePatient = new Identifiable(PID_CONSENT_REPLACE,
				new AssigningAuthority(assigningAutorityOID));
		author = new Author();
		author.setAuthorPerson(new Person(new Identifiable("1.2.3",
				new AssigningAuthority(assigningAutorityOID)),
				new Name("Riviera Nick M.D. Dr.")));
		author.getAuthorInstitution().add(
				new Organization("coalaAuthorOrg", null, null));
		author.getAuthorRole().add("Primary Care Physician");

		policy = new Code("1.2.840.113619.20.2.9.1", new LocalizedString(
				"1.2.840.113619.20.2.9.1", "en-US", "UTF-8"),
				"Privacy Policies");

		BufferedReader reader;

		String buffer = "";
		String xml = "";
		InputStream testConsentStream = null;
		try {
			// Try to read out the stream
			// See if source can be established and has bytes
			testConsentStream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("TestConsent.xml");
			reader = new BufferedReader(
					new InputStreamReader(testConsentStream));
			buffer = reader.readLine();
			while (buffer != null) {
				xml += buffer + "\n";
				buffer = reader.readLine();
			}
			dataHandler = new DataHandler(new CdaDataSourceImpl(xml));
		} catch (Exception e) {
			dataHandler = null;
			LOG.error("DataHandler for CDA documents could not be initialized!");
		} finally {
			if (testConsentStream != null) {
				try {
					testConsentStream.close();
				} catch (IOException e) {
					LOG.error(e.getLocalizedMessage(), e);
				}
			}
		}
	}

	/**
	 * Tests the basic provideAndRegister of a document. It is checked that the
	 * document is registered and the uniqueID is returned and equals with the
	 * created document
	 */
	@Test
	public void testProvideAndRegisterConsentDocumentForPID() {
		try {
			// #####################################
			// Do the action
			// #####################################
			DocumentEntry docEntry = createDocumentEntry(author, createPatient,
					policy);
			Document doc = new Document(docEntry, dataHandler);

			String resp = xdsTransactor
					.provideAndRegisterNewConsent(doc, false);
			// #####################################
			// Check whether the document could be
			// registered
			// #####################################
			assertNotNull(resp);
			assertTrue(resp.equals(docEntry.getUniqueId()));
		} catch (Throwable t) {
			LOG.error(t.getLocalizedMessage(), t);
			fail(t.getLocalizedMessage());
		}
	}

	/**
	 * Tests that it is possible to create a new consent without an author
	 */
	@Test
	public void testProvideAndRegisterDocumentSetWithNullAuthor() {
		// #####################################
		// Do the action
		// #####################################
		DocumentEntry docEntry = createDocumentEntry(null, createPatient,
				policy);
		Document doc = new Document(docEntry, dataHandler);
		String resp = xdsTransactor.provideAndRegisterNewConsent(doc, false);
		assertNotNull(resp);
	}

	/**
	 * Tests that an XDSRequestFailedException is thrown when no
	 * patientIdentifiable is set
	 */
	@Test(expected = XDSRequestFailedException.class)
	public void testProvideAndRegisterDocumentSetWithNullIdentifier() {
		// #####################################
		// Do the action
		// #####################################
		DocumentEntry docEntry = createDocumentEntry(author, null, policy);
		Document doc = new Document(docEntry, dataHandler);
		xdsTransactor.provideAndRegisterNewConsent(doc, false);
	}

	/**
	 * Tests that an XDSRequestFailedException is thrown when no policy is set
	 */
	@Test(expected = XDSRequestFailedException.class)
	public void testProvideAndRegisterDocumentSetWithNullPolicy() {
		// #####################################
		// Do the action
		// #####################################
		DocumentEntry docEntry = createDocumentEntry(author, createPatient,
				null);
		Document doc = new Document(docEntry, dataHandler);
		xdsTransactor.provideAndRegisterNewConsent(doc, false);
	}

	/**
	 * This test checks that after a
	 * <code>provideAndRegisterNewConsent(consentDocument, true)</code> are more
	 * consent documents registered for a patient and that the new consent is
	 * still active.
	 */
	@Test
	public void testReplacementOfOldConsents() {
		List<AvailabilityStatus> activeStatus = new ArrayList<AvailabilityStatus>();
		activeStatus.add(AvailabilityStatus.APPROVED);

		List<AvailabilityStatus> fetchAllDocumentsStati = new ArrayList<AvailabilityStatus>();
		fetchAllDocumentsStati.add(AvailabilityStatus.APPROVED);
		fetchAllDocumentsStati.add(AvailabilityStatus.SUBMITTED);
		fetchAllDocumentsStati.add(AvailabilityStatus.DEPRECATED);

		List<DocumentEntry> consentListBefore = xdsTransactor
				.getConsentDocumentList(replacePatient.getId(),
						fetchAllDocumentsStati);
		int consentsBefore = consentListBefore.size();
		// #####################################
		// Do the action
		// #####################################
		DocumentEntry docEntry = createDocumentEntry(author, replacePatient,
				policy);
		Document doc = new Document(docEntry, dataHandler);
		String consentUniqueID = xdsTransactor.provideAndRegisterNewConsent(
				doc, true);

		List<DocumentEntry> consentListAfter = xdsTransactor
				.getConsentDocumentList(replacePatient.getId(),
						fetchAllDocumentsStati);
		int consentsAfter = consentListAfter.size();
		assertTrue(consentsAfter > consentsBefore);

		List<DocumentEntry> activeConsentList = xdsTransactor
				.getConsentDocumentList(replacePatient.getId(), activeStatus);
		boolean isNewConsentActive = false;
		for (DocumentEntry d : activeConsentList) {
			if (d.getUniqueId().equals(consentUniqueID)) {
				isNewConsentActive = true;
				break;
			}
		}
		assertTrue(isNewConsentActive);
		assertTrue((activeConsentList.size()) == 1);
	}

	/*
	 * Little helper method to create a new DocumentEntry for testing issues
	 */
	private DocumentEntry createDocumentEntry(Author author,
			Identifiable patientID, Code policy)
			throws XDSConfigurationErrorException {
		try {
			if (patientID == null)
				throw new XDSRequestFailedException(
						"PatientID must not be null");

			consentCounter = 1;
			DocumentEntry docEntry = new DocumentEntry();
			if (author != null)
				docEntry.getAuthors().add(author);
			docEntry.setAvailabilityStatus(AvailabilityStatus.APPROVED);

			String langCode = xdsConfiguration.getConsentLanguageCode();
			setConsentClassCode(docEntry, langCode);
			// Confidentiality
			setConsentConfidentialityCode(docEntry, langCode);
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			docEntry.setCreationTime(dateFormat.format(new Date()));
			docEntry.setEntryUuid("newConsent" + consentCounter);
			consentCounter++;
			docEntry.setServiceStartTime("20110705171421");
			docEntry.setServiceStopTime("20120705171421");
			// set policy
			docEntry.getEventCodeList().add(policy);
			// consent format
			setConsentFormatCode(docEntry, langCode);
			// Generate unique ID
			String uniqueId = "2.16.840.1.113883.3.37.900.5.2";
			uniqueId += "." + patientID.getId() + "."
					+ dateFormat.format(new Date());

			setHealthcareFacilityTypeCode(docEntry);
			docEntry.setLanguageCode(langCode);
			docEntry.setMimeType("text/xml");
			docEntry.setPatientId(patientID);
			// practice setting
			setPracticeSettingCode(docEntry);
			docEntry.setRepositoryUniqueId("1.2.840.113619.20.2.2.1");
			docEntry.setSourcePatientId(patientID);
			docEntry.setTitle(new LocalizedString("Consent for"
					+ patientID.getId(), langCode, encoding));
			// type code
			setConsentTypeCode(docEntry, langCode);
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

	/*
	 * Little helping method that sets the consent type code in the given
	 * DocumentEntry
	 */
	private void setConsentTypeCode(DocumentEntry docEntry, String langCode) {
		docEntry.setTypeCode(new Code(xdsConfiguration.getConsentDocumentTypeCodeCode(),
				new LocalizedString(xdsConfiguration.getConsentDocumentTypeCodeDisplayname(),
						langCode, encoding),xdsConfiguration.getConsentDocumentTypeCodeSchemename()));
	}

	/*
	 * Little helping method that sets the practice setting code in the given
	 * DocumentEntry
	 */
	private void setPracticeSettingCode(DocumentEntry docEntry) {
		docEntry.setPracticeSettingCode(new Code("235500000X",
				new LocalizedString("235500000X"),"Health Care Provider Taxonomy"));
	}

	/*
	 * Little helping method that sets the healthcare facility typecode in the
	 * given DocumentEntry
	 */
	private void setHealthcareFacilityTypeCode(DocumentEntry docEntry) {
		docEntry.setHealthcareFacilityTypeCode(new Code("PRA",
				new LocalizedString("Arztpraxis"),"Einrichtungsart"));
	}

	/*
	 * Little helping method that sets the consent format code in the given
	 * DocumentEntry
	 */
	private void setConsentFormatCode(DocumentEntry docEntry, String langCode) {
		docEntry.setFormatCode(new Code(xdsConfiguration.getConsentFormatCodeCode(),
				new LocalizedString(xdsConfiguration.getConsentFormatCodeDisplayname(),
						langCode, encoding), xdsConfiguration.getConsentFormatCodeSchemename()));
	}

	/*
	 * Little helping method that sets the consent confidentiality code in the
	 * given DocumentEntry
	 */
	private void setConsentConfidentialityCode(DocumentEntry docEntry,
			String langCode) {
		docEntry.getConfidentialityCodes()
				.add(new Code("N",
						new LocalizedString("Normal",
								langCode, encoding),
						"HL7 Confidentiality Codes"));
	}

	/*
	 * Little helping method that sets the consent class code in the given
	 * DocumentEntry
	 */
	private void setConsentClassCode(DocumentEntry docEntry, String langCode) {
		docEntry.setClassCode(new Code("Consent",
				new LocalizedString("Consent",
						langCode, encoding), "IHE Class Codes"));
	}
}
