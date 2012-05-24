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
package org.openehealth.coala.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openehealth.coala.communication.PdqMessageBuilder;
import org.openehealth.coala.converter.PdqHL7Converter;
import org.openehealth.coala.domain.CoalaAuthor;
import org.openehealth.coala.domain.ConsentSortParameter;
import org.openehealth.coala.domain.FindPatientConsentResult;
import org.openehealth.coala.domain.FindPatientQuery;
import org.openehealth.coala.domain.FindPatientResult;
import org.openehealth.coala.domain.Gender;
import org.openehealth.coala.domain.Patient;
import org.openehealth.coala.domain.PatientAddress;
import org.openehealth.coala.domain.PatientConsent;
import org.openehealth.coala.domain.PatientConsentPolicy;
import org.openehealth.coala.domain.PatientSortParameter;
import org.openehealth.coala.interfacing.ConsentCreationService;
import org.openehealth.coala.interfacing.PXSPatientService;
import org.openehealth.coala.pdq.PDQGate;
import org.openehealth.coala.util.PXSDateConverter;

/**
 * Unit test for {@link PXSQueryServiceImpl}.
 * 
 * @author mwiesner, kmaerz, hhein
 */
@Ignore
public class PXSQueryServiceTest {

	private PXSPatientService pXSQueryService;
	private ConsentCreationService consentCreationService;
	private PXSDateConverter pxsDateConverter;

	private static final String PXS_ASSINGNING_AUTHORITY_OID = "2.16.840.1.113883.3.37.4.1.1.2.2.1";
	private PDQGate pdqGate;
	private PdqMessageBuilder pdqMessageBuilder;
	private PdqHL7Converter pdqHL7Converter;
	
	@Before
	public void setUp() {
		PXSQueryServiceImpl pXSQueryServiceImpl = new PXSQueryServiceImpl();
		pdqMessageBuilder = createMock(PdqMessageBuilder.class);
		pdqGate = createMock(PDQGate.class);
		pdqHL7Converter = createMock(PdqHL7Converter.class);
		pXSQueryServiceImpl.setPdqMessageBuilder(pdqMessageBuilder );
		pXSQueryServiceImpl.setPdqGate(pdqGate );
		pXSQueryServiceImpl.setPdqConverter(pdqHL7Converter);
		pXSQueryService = pXSQueryServiceImpl;
		
		
	}
	
	/**
	 * Test if a FindPatientResult returned correctly via a Lastname
	 * {@link FindPatientQuery}.
	 */

	@Test
	public void testFindPatientsByLastnameCorrect() {
		
		String hl7StringContainingQueryParameter = "HL7StringContainingQueryParameter";
		String hl7StringContainingResult = "HL7StringContainingResult";
		expect(pdqMessageBuilder.buildPdqRequest("", "", "Mue*", null)).andReturn(hl7StringContainingQueryParameter);
		expect(pdqGate.requestPatients(hl7StringContainingQueryParameter)).andReturn(hl7StringContainingResult);
		ArrayList<Patient> patientList = createPatientList();
		expect(pdqHL7Converter.convertPdqToPatients(hl7StringContainingResult)).andReturn(patientList);
		
		replay(pdqGate, pdqMessageBuilder, pdqHL7Converter);

		List<Patient> patientsResList = new ArrayList<Patient>();
		FindPatientQuery findPatientQuery = new FindPatientQuery("", "",
				"Mue*", null);
		FindPatientResult findPatientResult = pXSQueryService.findPatients(
				findPatientQuery, PatientSortParameter.getDefault());
		assertNotNull(findPatientResult);
		patientsResList = findPatientResult.getPatients();
		assertNotNull(patientsResList);

		assertTrue(patientsResList.size() > 0);
		for (Patient p : patientsResList) {
			if (p.getLastName().equals("Mueller")) {
				assertEquals("Hans", p.getGivenName());
			}
		}
		
		
	}

	private ArrayList<Patient> createPatientList() {
		ArrayList<Patient> patientList = new ArrayList<Patient>();
		Patient patient = new Patient("testPatientID", "testPatientIDAssigningAuthorityUniversalId", "Hans", "Mueller", new Date(), Gender.UNKNOWN, new PatientAddress());
		patientList.add(patient);
		return patientList;
	}

	/**
	 * Test if a FindPatientResult returned correctly via a GivenName
	 * {@link FindPatientQuery}.
	 */
	@Test
	public void testFindPatientsByGivenNameCorrect() {

		List<Patient> patientsResList = new ArrayList<Patient>();
		FindPatientQuery findPatientQuery = new FindPatientQuery("", "Hanne*",
				"", null);
		FindPatientResult findPatientResult = pXSQueryService.findPatients(
				findPatientQuery, PatientSortParameter.getDefault());
		assertNotNull(findPatientResult);
		patientsResList = findPatientResult.getPatients();
		assertNotNull(patientsResList);

		assertTrue(patientsResList.size() > 0);
		for (Patient p : patientsResList) {
			if (p.getGivenName().equals("Hannes")) {
				assertEquals("Müller", p.getLastName());
			}
		}
	}

	/**
	 * Test if a FindPatientResult returned correctly via a DateOfBirth
	 * {@link FindPatientQuery}.
	 */
	@Test
	public void testFindPatientsByDateOfBirthCorrect() {

		List<Patient> patientsResList = new ArrayList<Patient>();

		int month = 1;
		int day = 1;
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Calendar.YEAR, 1940);
		cal.set(Calendar.MONTH, month - 1); // CAVE: -1 is needed to ensure
											// correct months, as months start
											// with 0 = JAN (!)
		cal.set(Calendar.DAY_OF_MONTH, day);
		Date dateOfBirth = cal.getTime();

		FindPatientQuery findPatientQuery = new FindPatientQuery("", "", "",
				dateOfBirth);
		FindPatientResult findPatientResult = pXSQueryService.findPatients(
				findPatientQuery, PatientSortParameter.getDefault());
		assertNotNull(findPatientResult);
		patientsResList = findPatientResult.getPatients();
		assertNotNull(patientsResList);

		assertTrue(patientsResList.size() > 0);
		for (Patient p : patientsResList) {
			if (p.getLastName().equals("Hans")) {
				assertEquals("Müller", p.getGivenName());
			}
		}
	}

	/**
	 * Test if a FindPatientResult returned correctly via a PID
	 * {@link FindPatientQuery}.
	 */
	@Test
	public void testFindPatientsByPIDCorrect() {

		List<Patient> patientsResList = new ArrayList<Patient>();
		FindPatientQuery findPatientQuery = new FindPatientQuery("6", "", "",
				null);
		FindPatientResult findPatientResult = pXSQueryService.findPatients(
				findPatientQuery, PatientSortParameter.getDefault());
		assertNotNull(findPatientResult);
		patientsResList = findPatientResult.getPatients();
		assertNotNull(patientsResList);

		assertTrue(patientsResList.size() > 0);
		for (Patient p : patientsResList) {
			if (p.getLastName().equals("Hans")) {
				assertEquals("Müller", p.getGivenName());
			}
		}
	}

	/**
	 * Test if a FindPatientResult returned correctly via a PID
	 * {@link FindPatientQuery}.
	 */
	@Test
	public void testFindPatientsByGivenNameANDLastnameCorrect() {

		List<Patient> patientsResList = new ArrayList<Patient>();
		FindPatientQuery findPatientQuery = new FindPatientQuery("", "H*",
				"M*", null);
		FindPatientResult findPatientResult = pXSQueryService.findPatients(
				findPatientQuery, PatientSortParameter.getDefault());
		assertNotNull(findPatientResult);
		patientsResList = findPatientResult.getPatients();
		assertNotNull(patientsResList);

		assertTrue(patientsResList.size() > 0);
		for (Patient p : patientsResList) {
			if (p.getLastName().equals("Hans")) {
				assertEquals("Müller", p.getGivenName());
			}
		}
	}

	/**
	 * Test if a FindPatientResult returned correctly via a last name
	 * {@link FindPatientQuery} which contains German Umlauts ("ü,ä,ö").
	 */
	@Test
	public void testFindPatientsByLastnameWithUmlautsCorrect() {

		List<Patient> patientsResList = new ArrayList<Patient>();
		String mUTF8 = "Müller";
		
		FindPatientQuery findPatientQuery = new FindPatientQuery("", "H*", mUTF8,
				null);

		FindPatientResult findPatientResult = pXSQueryService.findPatients(
				findPatientQuery, PatientSortParameter.getDefault());
		assertNotNull(findPatientResult);
		patientsResList = findPatientResult.getPatients();
		assertNotNull(patientsResList);

		assertTrue(patientsResList.size() > 0);
		for (Patient p : patientsResList) {
			if (p.getGivenName().equals("Hans")) {
				assertEquals("Müller", p.getLastName());
			}
		}
	}

	/**
	 * Tests, whether the Service correctly returns a Result of length 0, if a
	 * patient cannot be found.
	 */
	@Test
	public void testHl7toPDQConversion() {
		// primarily the ID = 1 is important here.

		FindPatientQuery findPatientQuery = new FindPatientQuery("1", "", "",
				null);
		FindPatientResult findPatientResult = pXSQueryService.findPatients(
				findPatientQuery, PatientSortParameter.getDefault());
		assertTrue(findPatientResult.getPatients().size() == 0);
	}

	/**
	 * Tests if the methods works correct, if a name includes PID.
	 */
	@Test
	public void testHl7toPDQConversionCorrectByPID() {

		List<Patient> patientsResList = new ArrayList<Patient>();
		FindPatientQuery findPatientQuery = new FindPatientQuery("", "", "PID",
				null);
		FindPatientResult findPatientResult = pXSQueryService.findPatients(
				findPatientQuery, PatientSortParameter.getDefault());
		assertNotNull(findPatientResult);
		patientsResList = findPatientResult.getPatients();
		assertNotNull(patientsResList);

		assertTrue(patientsResList.size() == 0);
	}

	/**
	 * Tests if it is possible to retrieve any consents for Mr. Norris (FIND) at
	 * all.
	 */
	@Test
	public void testGetPatientConsentsValid() {

		// primarily the ID = 1 is important here.
		Patient beckenbauer = new Patient("305010", PXS_ASSINGNING_AUTHORITY_OID,
				"not important here", "Norris", new Date(), Gender.MALE,
				new PatientAddress());

		// fetching Mr. Beckenbauer's consent documents - expected only one here
		FindPatientConsentResult consents = consentCreationService.getPatientConsents(
				beckenbauer, ConsentSortParameter.START_DATE_NEWEST_FIRST);
		assertNotNull(consents);
		assertTrue(
				"No consent document was found in the response for a FindDocumentQuery",
				consents.getPatientConsents().size() == 4);
	}

	/**
	 * Retrieves the consent of Mister ChuckFIND Norris and checks it's data
	 * for validity.
	 */
	@Test
	public void testGetPatientConsentsCorrectChuckFind() {
		Date validFrom = pxsDateConverter.stringToDate("20110705171421");
		Date validUntil = pxsDateConverter.stringToDate("20500805171421");

		// primarily the ID = 1 is important here.
		Patient chuckFind = new Patient("305010", PXS_ASSINGNING_AUTHORITY_OID,
				"not important here", "Norris", new Date(), Gender.MALE,
				new PatientAddress());

		// fetching Mr. ChuckFIND Norris consent documents - expected only one here
		FindPatientConsentResult consents = consentCreationService.getPatientConsents(
				chuckFind, ConsentSortParameter.END_DATE_OLDEST_FIRST);
		PatientConsent c = consents.getPatientConsents().get(0);
		assertTrue("Consent pointed to an invalid patient", c.getPatient()
				.equals(chuckFind));
		assertTrue("Consent contained an invalid validUntil", c.getValidUntil()
				.equals(validUntil));
		assertTrue("Consent contained a invalid validFrom", c.getValidFrom()
				.equals(validFrom));
		assertTrue("Consent contained invalid policy type",
				c.getPolicyType() == PatientConsentPolicy.ONE);
		assertFalse("Consent is marked as obsolete, should not be obsolete",
				c.isObsolete());
		assertTrue(
				"Consent should be in effect, as it has not expired",
				c.isActive());
	}

	@Test
	public void testProvideConsentAcknowledgementValid() {
		Date start = pxsDateConverter.stringToDate("201001010000");
		Date end = pxsDateConverter.stringToDate("201201010000");

		Patient norris = new Patient("305009", PXS_ASSINGNING_AUTHORITY_OID,
				"ChuckCREATE", "Norris", new Date(), Gender.MALE, new PatientAddress());

		FindPatientConsentResult allConsents = consentCreationService
				.getPatientConsents(norris, ConsentSortParameter.getDefault());
		int beforeSize = allConsents.getPatientConsents().size();

		CoalaAuthor author = new CoalaAuthor("Dr.", "Keno", "März");

		consentCreationService.createPatientConsent(norris, start, end,
				PatientConsentPolicy.ONE, author);

		allConsents = consentCreationService.getPatientConsents(norris,
				ConsentSortParameter.getDefault());
		int afterSize = allConsents.getPatientConsents().size();

		// increase size test assertion here
		assertTrue(
				"Patient Consents did not increase by one for test patient Chuck Norris, but it should!",
				(afterSize - beforeSize) == 1);

	}

}
