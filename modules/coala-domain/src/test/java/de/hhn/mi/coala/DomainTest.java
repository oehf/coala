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
package de.hhn.mi.coala;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

import de.hhn.mi.coala.domain.CoalaAuthor;
import de.hhn.mi.coala.domain.ConsentSortParameter;
import de.hhn.mi.coala.domain.FindPatientConsentResult;
import de.hhn.mi.coala.domain.FindPatientQuery;
import de.hhn.mi.coala.domain.FindPatientResult;
import de.hhn.mi.coala.domain.Gender;
import de.hhn.mi.coala.domain.Patient;
import de.hhn.mi.coala.domain.PatientAddress;
import de.hhn.mi.coala.domain.PatientConsent;
import de.hhn.mi.coala.domain.PatientConsentPolicy;
import de.hhn.mi.coala.domain.PatientSortParameter;
import de.hhn.mi.coala.exception.ServiceParameterException;

/**
 * Unit test for various coala domain classes.‚
 */
public class DomainTest {

	private String patientIDAssigningAuthorityUniversalId = "2.16.840.1.113883.3.37.4.1.1.2.2.1";

	private static final String TAG_XML = "xml";

	/**
	 * Test if a patient is created correctly
	 */
	@Test
	public void testCreatePatientCorrect() {
		Date date = new Date();
		PatientAddress address = new PatientAddress();
		Patient p = new Patient("id", patientIDAssigningAuthorityUniversalId,
				"vorname", "nachname", date, Gender.NOT_APPLICABLE, address);
		assertTrue(p.getGivenName().equals("vorname"));
		assertTrue(p.getLastName().equals("nachname"));
		assertTrue(p.getPatientID().equals("id"));
		assertTrue(p.getBirthdate().equals(date));
		assertTrue(p.getAddress().equals(address));
		assertTrue(p.getSex().equals(Gender.NOT_APPLICABLE.toString()));

	}

	/**
	 * Test if patient can be created with illegal parameters
	 */
	@Test
	public void testCreatePatientNullAndEmptyStrings() {
		Date date = new Date();
		PatientAddress address = new PatientAddress();
		String errorMsg = "Creation of Patient should have failed, but succeeded";

		@SuppressWarnings("unused")
		Patient p = null;
		try {
			p = new Patient(null, patientIDAssigningAuthorityUniversalId, "a",
					"a", date, Gender.NOT_APPLICABLE, address);
			fail(errorMsg);
		} catch (IllegalArgumentException e) {
		}
		try {
			p = new Patient("", patientIDAssigningAuthorityUniversalId, "a",
					"a", date, Gender.NOT_APPLICABLE, address);
			fail(errorMsg);
		} catch (IllegalArgumentException e) {
		}
		try {
			p = new Patient("", patientIDAssigningAuthorityUniversalId, "a",
					"a", date, Gender.NOT_APPLICABLE, address);
			fail(errorMsg);
		} catch (IllegalArgumentException e) {
		}
		try {
			p = new Patient("a", null, "a", "a", date, Gender.NOT_APPLICABLE,
					address);
			fail(errorMsg);
		} catch (IllegalArgumentException e) {
		}
		try {
			p = new Patient("a", "", "a", "a", date, Gender.NOT_APPLICABLE,
					address);
			fail(errorMsg);
		} catch (IllegalArgumentException e) {
		}
		try {
			p = new Patient("a", patientIDAssigningAuthorityUniversalId, null,
					"a", date, Gender.NOT_APPLICABLE, address);
			fail(errorMsg);
		} catch (IllegalArgumentException e) {
		}
		try {
			p = new Patient("a", patientIDAssigningAuthorityUniversalId, "",
					"a", date, Gender.NOT_APPLICABLE, address);
			fail(errorMsg);
		} catch (IllegalArgumentException e) {
		}
		try {
			p = new Patient("a", patientIDAssigningAuthorityUniversalId, "a",
					null, date, Gender.NOT_APPLICABLE, address);
			fail(errorMsg);
		} catch (IllegalArgumentException e) {
		}
		try {
			p = new Patient("a", patientIDAssigningAuthorityUniversalId, "a",
					"", date, Gender.NOT_APPLICABLE, address);
			fail(errorMsg);
		} catch (IllegalArgumentException e) {
		}
		try {
			p = new Patient("a", patientIDAssigningAuthorityUniversalId, "a",
					"a", null, Gender.NOT_APPLICABLE, address);
			fail(errorMsg);
		} catch (IllegalArgumentException e) {
		}
		try {
			p = new Patient("a", patientIDAssigningAuthorityUniversalId, "a",
					"a", date, Gender.NOT_APPLICABLE, null);
			fail("Creation of Patient should have failed, but succeeded");
		} catch (IllegalArgumentException e) {
		}
		try {
			p = new Patient("a", patientIDAssigningAuthorityUniversalId, "a",
					"a", date, null, address);
			fail("Creation of Patient should have failed, but succeeded");
		} catch (IllegalArgumentException e) {
		}
	}

	/**
	 * Test if a query is generated correctly
	 */
	@Test
	public void testCreateFindPatientQueryCorrect() {
		Date date = new Date();
		FindPatientQuery q = new FindPatientQuery("12", "vorname", "nachname",
				date);
		assertTrue(q.getGivenName().equals("vorname"));
		assertTrue(q.getLastName().equals("nachname"));
		assertTrue(q.getPatientID().equals("12"));
		assertTrue(q.getBirthdate().equals(date));
	}

	/**
	 * Test if a query generates an error, if it is empty.
	 */
	@Test
	public void testCreateFindPatientQueryIncorrect() {
		try {
			new FindPatientQuery("", "", "", null);
			fail();
		} catch (ServiceParameterException e) {
			// We excpected this :)
		}
		try {
			new FindPatientQuery("", null, "", null);
			fail();
		} catch (ServiceParameterException e) {
			// We excpected this :)
		}
		try {
			new FindPatientQuery(null, null, null, null);
			fail();
		} catch (ServiceParameterException e) {
			// We excpected this :)
		}
		try {
			new FindPatientQuery("ad1", null, null, null);
			fail();
		} catch (ServiceParameterException e) {
			// We excpected this :)
		}
	}

	/**
	 * Test if a {@link FindPatientResult} can be modified after access.
	 */
	@Test
	public void testModifyFindPatientResultAfterAccess() {
		Date date = new Date();
		PatientAddress address = new PatientAddress();
		FindPatientResult r = new FindPatientResult(new FindPatientQuery("12",
				"vorname", "nachname", date), PatientSortParameter.getDefault());
		r.addPatient(new Patient("id", patientIDAssigningAuthorityUniversalId,
				"vorname", "nachname", date, Gender.NOT_APPLICABLE, address));
		r.addPatient(new Patient("id2", patientIDAssigningAuthorityUniversalId,
				"vorname2", "nachname2", date, Gender.NOT_APPLICABLE, address));
		r.lock();
		try {
			r.addPatient(new Patient("sollte",
					patientIDAssigningAuthorityUniversalId, "nicht",
					"funktionieren", date, Gender.NOT_APPLICABLE, address));
			fail("Should not be able to add patient once List has been accessed.");
		} catch (IllegalArgumentException e) {
		}

	}

	/**
	 * Tests, whether {@link Patient} instances are correctly sorted by newest
	 * date of birth.
	 */
	@Test
	public void testPatientSortByBirthDateNewestFirst() {
		PatientAddress address = new PatientAddress();
		Patient p1 = new Patient("1", patientIDAssigningAuthorityUniversalId,
				"A", "B", convertStringToDate("20010101"),
				Gender.NOT_APPLICABLE, address);
		Patient p2 = new Patient("2", patientIDAssigningAuthorityUniversalId,
				"A", "B", convertStringToDate("20020101"),
				Gender.NOT_APPLICABLE, address);
		Patient p3 = new Patient("3", patientIDAssigningAuthorityUniversalId,
				"A", "B", convertStringToDate("20020102"),
				Gender.NOT_APPLICABLE, address);
		Patient p4 = new Patient("4", patientIDAssigningAuthorityUniversalId,
				"A", "B", convertStringToDate("20050101"),
				Gender.NOT_APPLICABLE, address);

		FindPatientResult fpr = new FindPatientResult(new FindPatientQuery("1",
				"", "", null), PatientSortParameter.BIRTHDATE_NEWEST_FIRST);
		fpr.addPatient(p3);
		fpr.addPatient(p2);
		fpr.addPatient(p4);
		fpr.addPatient(p1);
		fpr.lock();

		assertEquals(p1, fpr.getPatients().get(0));
		assertEquals(p2, fpr.getPatients().get(1));
		assertEquals(p3, fpr.getPatients().get(2));
		assertEquals(p4, fpr.getPatients().get(3));
	}

	/**
	 * Tests, whether {@link Patient} instances are correctly sorted by oldest
	 * date of birth.
	 */
	@Test
	public void testPatientSortByBirthDateOldestFirst() {
		PatientAddress address = new PatientAddress();
		Patient p1 = new Patient("1", patientIDAssigningAuthorityUniversalId,
				"A", "B", convertStringToDate("20010101"),
				Gender.NOT_APPLICABLE, address);
		Patient p2 = new Patient("2", patientIDAssigningAuthorityUniversalId,
				"A", "B", convertStringToDate("20020101"),
				Gender.NOT_APPLICABLE, address);
		Patient p3 = new Patient("3", patientIDAssigningAuthorityUniversalId,
				"A", "B", convertStringToDate("20020102"),
				Gender.NOT_APPLICABLE, address);
		Patient p4 = new Patient("4", patientIDAssigningAuthorityUniversalId,
				"A", "B", convertStringToDate("20050101"),
				Gender.NOT_APPLICABLE, address);

		FindPatientResult fpr = new FindPatientResult(new FindPatientQuery("1",
				"", "", null), PatientSortParameter.BIRTHDATE_OLDEST_FIRST);
		fpr.addPatient(p3);
		fpr.addPatient(p2);
		fpr.addPatient(p4);
		fpr.addPatient(p1);
		fpr.lock();

		assertEquals(p4, fpr.getPatients().get(0));
		assertEquals(p3, fpr.getPatients().get(1));
		assertEquals(p2, fpr.getPatients().get(2));
		assertEquals(p1, fpr.getPatients().get(3));
	}

	/**
	 * Tests, whether {@link Patient} instances are correctly sorted by their
	 * {@link Patient#getLastName()} in ascending order.
	 */
	@Test
	public void testPatientSortByLastnameAscending() {
		PatientAddress address = new PatientAddress();
		Patient p1 = new Patient("1", patientIDAssigningAuthorityUniversalId,
				"A", "Beckenbauer", convertStringToDate("20010101"),
				Gender.NOT_APPLICABLE, address);
		Patient p2 = new Patient("2", patientIDAssigningAuthorityUniversalId,
				"A", "Breitner", convertStringToDate("20010101"),
				Gender.NOT_APPLICABLE, address);
		Patient p3 = new Patient("3", patientIDAssigningAuthorityUniversalId,
				"A", "Hoeneß", convertStringToDate("20010101"),
				Gender.NOT_APPLICABLE, address);
		Patient p4 = new Patient("4", patientIDAssigningAuthorityUniversalId,
				"A", "Müller", convertStringToDate("20010101"),
				Gender.NOT_APPLICABLE, address);

		FindPatientResult fpr = new FindPatientResult(new FindPatientQuery("1",
				"", "", null), PatientSortParameter.LASTNAME_ASCENDING);
		fpr.addPatient(p3);
		fpr.addPatient(p2);
		fpr.addPatient(p4);
		fpr.addPatient(p1);
		fpr.lock();

		assertEquals(p1, fpr.getPatients().get(0));
		assertEquals(p2, fpr.getPatients().get(1));
		assertEquals(p3, fpr.getPatients().get(2));
		assertEquals(p4, fpr.getPatients().get(3));
	}

	/**
	 * Tests, whether {@link Patient} instances are correctly sorted by their
	 * {@link Patient#getLastName()} in descending order.
	 */
	@Test
	public void testPatientSortByLastnameDescending() {
		PatientAddress address = new PatientAddress();
		Patient p1 = new Patient("1", patientIDAssigningAuthorityUniversalId,
				"A", "Beckenbauer", convertStringToDate("20010101"),
				Gender.NOT_APPLICABLE, address);
		Patient p2 = new Patient("2", patientIDAssigningAuthorityUniversalId,
				"A", "Breitner", convertStringToDate("20010101"),
				Gender.NOT_APPLICABLE, address);
		Patient p3 = new Patient("3", patientIDAssigningAuthorityUniversalId,
				"A", "Hoeneß", convertStringToDate("20010101"),
				Gender.NOT_APPLICABLE, address);
		Patient p4 = new Patient("4", patientIDAssigningAuthorityUniversalId,
				"A", "Müller", convertStringToDate("20010101"),
				Gender.NOT_APPLICABLE, address);

		FindPatientResult fpr = new FindPatientResult(new FindPatientQuery("1",
				"", "", null), PatientSortParameter.LASTNAME_DESCENDING);
		fpr.addPatient(p3);
		fpr.addPatient(p2);
		fpr.addPatient(p4);
		fpr.addPatient(p1);
		fpr.lock();

		assertEquals(p4, fpr.getPatients().get(0));
		assertEquals(p3, fpr.getPatients().get(1));
		assertEquals(p2, fpr.getPatients().get(2));
		assertEquals(p1, fpr.getPatients().get(3));
	}

	/**
	 * Tests, whether {@link Patient} instances are correctly sorted by their
	 * {@link Patient#getGivenName()} in ascending order.
	 */
	@Test
	public void testPatientSortByGivennameAscending() {
		PatientAddress address = new PatientAddress();
		Patient p1 = new Patient("1", patientIDAssigningAuthorityUniversalId,
				"Franz", "Beckenbauer", convertStringToDate("20010101"),
				Gender.NOT_APPLICABLE, address);
		Patient p2 = new Patient("2", patientIDAssigningAuthorityUniversalId,
				"Gerd", "Müller", convertStringToDate("20010101"),
				Gender.NOT_APPLICABLE, address);
		Patient p3 = new Patient("3", patientIDAssigningAuthorityUniversalId,
				"Paul", "Breitner", convertStringToDate("20010101"),
				Gender.NOT_APPLICABLE, address);
		Patient p4 = new Patient("4", patientIDAssigningAuthorityUniversalId,
				"Uli", "Hoeneß", convertStringToDate("20010101"),
				Gender.NOT_APPLICABLE, address);

		FindPatientResult fpr = new FindPatientResult(new FindPatientQuery("1",
				"", "", null), PatientSortParameter.GIVENNAME_ASCENDING);
		fpr.addPatient(p3);
		fpr.addPatient(p2);
		fpr.addPatient(p4);
		fpr.addPatient(p1);
		fpr.lock();

		assertEquals(p1, fpr.getPatients().get(0));
		assertEquals(p2, fpr.getPatients().get(1));
		assertEquals(p3, fpr.getPatients().get(2));
		assertEquals(p4, fpr.getPatients().get(3));
	}

	/**
	 * Tests, whether {@link Patient} instances are correctly sorted by their
	 * {@link Patient#getLastName()} in descending order.
	 */
	@Test
	public void testPatientSortByGivennameDescending() {
		PatientAddress address = new PatientAddress();
		Patient p1 = new Patient("1", patientIDAssigningAuthorityUniversalId,
				"Franz", "Beckenbauer", convertStringToDate("20010101"),
				Gender.NOT_APPLICABLE, address);
		Patient p2 = new Patient("2", patientIDAssigningAuthorityUniversalId,
				"Gerd", "Müller", convertStringToDate("20010101"),
				Gender.NOT_APPLICABLE, address);
		Patient p3 = new Patient("3", patientIDAssigningAuthorityUniversalId,
				"Paul", "Breitner", convertStringToDate("20010101"),
				Gender.NOT_APPLICABLE, address);
		Patient p4 = new Patient("4", patientIDAssigningAuthorityUniversalId,
				"Uli", "Hoeneß", convertStringToDate("20010101"),
				Gender.NOT_APPLICABLE, address);

		FindPatientResult fpr = new FindPatientResult(new FindPatientQuery("1",
				"", "", null), PatientSortParameter.GIVENNAME_DESCENDING);
		fpr.addPatient(p3);
		fpr.addPatient(p2);
		fpr.addPatient(p4);
		fpr.addPatient(p1);
		fpr.lock();

		assertEquals(p4, fpr.getPatients().get(0));
		assertEquals(p3, fpr.getPatients().get(1));
		assertEquals(p2, fpr.getPatients().get(2));
		assertEquals(p1, fpr.getPatients().get(3));
	}

	/**
	 * Tests, whether {@link Patient} instances are correctly sorted by their
	 * {@link Patient#getPatientID()} in ascending order.
	 */
	@Test
	public void testPatientSortByMPIIDAscending() {
		PatientAddress address = new PatientAddress();
		Patient p1 = new Patient("1", patientIDAssigningAuthorityUniversalId,
				"Franz", "Beckenbauer", convertStringToDate("20010101"),
				Gender.NOT_APPLICABLE, address);
		Patient p2 = new Patient("2", patientIDAssigningAuthorityUniversalId,
				"Gerd", "Müller", convertStringToDate("20010101"),
				Gender.NOT_APPLICABLE, address);
		Patient p3 = new Patient("3", patientIDAssigningAuthorityUniversalId,
				"Paul", "Breitner", convertStringToDate("20010101"),
				Gender.NOT_APPLICABLE, address);
		Patient p4 = new Patient("4", patientIDAssigningAuthorityUniversalId,
				"Uli", "Hoeneß", convertStringToDate("20010101"),
				Gender.NOT_APPLICABLE, address);

		FindPatientResult fpr = new FindPatientResult(new FindPatientQuery("1",
				"", "", null), PatientSortParameter.PID_ASCENDING);
		fpr.addPatient(p3);
		fpr.addPatient(p2);
		fpr.addPatient(p4);
		fpr.addPatient(p1);
		fpr.lock();

		assertEquals(p1, fpr.getPatients().get(0));
		assertEquals(p2, fpr.getPatients().get(1));
		assertEquals(p3, fpr.getPatients().get(2));
		assertEquals(p4, fpr.getPatients().get(3));
	}

	/**
	 * Tests, whether {@link Patient} instances are correctly sorted by their
	 * {@link Patient#getPatientID()} in ascending order.
	 */
	@Test
	public void testPatientSortByMPIIDDescending() {
		PatientAddress address = new PatientAddress();
		Patient p1 = new Patient("1", patientIDAssigningAuthorityUniversalId,
				"Franz", "Beckenbauer", convertStringToDate("20010101"),
				Gender.NOT_APPLICABLE, address);
		Patient p2 = new Patient("2", patientIDAssigningAuthorityUniversalId,
				"Gerd", "Müller", convertStringToDate("20010101"),
				Gender.NOT_APPLICABLE, address);
		Patient p3 = new Patient("3", patientIDAssigningAuthorityUniversalId,
				"Paul", "Breitner", convertStringToDate("20010101"),
				Gender.NOT_APPLICABLE, address);
		Patient p4 = new Patient("4", patientIDAssigningAuthorityUniversalId,
				"Uli", "Hoeneß", convertStringToDate("20010101"),
				Gender.NOT_APPLICABLE, address);

		FindPatientResult fpr = new FindPatientResult(new FindPatientQuery("1",
				"", "", null), PatientSortParameter.PID_DESCENDING);
		fpr.addPatient(p3);
		fpr.addPatient(p2);
		fpr.addPatient(p4);
		fpr.addPatient(p1);
		fpr.lock();

		assertEquals(p4, fpr.getPatients().get(0));
		assertEquals(p3, fpr.getPatients().get(1));
		assertEquals(p2, fpr.getPatients().get(2));
		assertEquals(p1, fpr.getPatients().get(3));
	}

	/**
	 * Test, whether {@link FindPatientConsentResult} behaves correctly with
	 * invalid arguments in constructor.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testConsentInvalidConstructor() {
		new FindPatientConsentResult(null, ConsentSortParameter.getDefault());
	}

	/**
	 * Test, whether {@link FindPatientConsentResult} behaves correctly with
	 * invalid sort parameter.
	 */
	@Test
	public void testConsentInvalidSortParameter() {
		Patient patient = new Patient("1",
				patientIDAssigningAuthorityUniversalId, "A", "B", new Date(),
				Gender.NOT_APPLICABLE, new PatientAddress());
		FindPatientConsentResult findPatientConsentResult = new FindPatientConsentResult(
				patient, null);
		assertEquals(patient, findPatientConsentResult.getPatient());
	}

	/**
	 * Tests, whether {@link PatientConsent} are correctly sorted by
	 * {@link PatientConsent#getStartDate()}.
	 */
	@Test
	public void testConsentSortByDateOldestFirst() {
		Patient patient = new Patient("1",
				patientIDAssigningAuthorityUniversalId, "A", "B", new Date(),
				Gender.NOT_APPLICABLE, new PatientAddress());
		CoalaAuthor author = new CoalaAuthor("Dr.", "Keno", "März");

		PatientConsent c0 = new PatientConsent(convertStringToDate("20010101"),
				new Date(), PatientConsentPolicy.ONE, patient, false, author,
				new Date(), TAG_XML);
		PatientConsent c1 = new PatientConsent(convertStringToDate("20020101"),
				new Date(), PatientConsentPolicy.ONE, patient, false, author,
				new Date(), TAG_XML);
		PatientConsent c2 = new PatientConsent(convertStringToDate("20020201"),
				new Date(), PatientConsentPolicy.ONE, patient, false, author,
				new Date(), TAG_XML);
		PatientConsent c3 = new PatientConsent(convertStringToDate("20020202"),
				new Date(), PatientConsentPolicy.ONE, patient, false, author,
				new Date(), TAG_XML);
		PatientConsent c4 = new PatientConsent(convertStringToDate("20050101"),
				new Date(), PatientConsentPolicy.ONE, patient, false, author,
				new Date(), TAG_XML);

		FindPatientConsentResult fpcr = new FindPatientConsentResult(patient,
				ConsentSortParameter.START_DATE_OLDEST_FIRST);
		fpcr.addPatientConsent(c3);
		fpcr.addPatientConsent(c2);
		fpcr.addPatientConsent(c0);
		fpcr.addPatientConsent(c4);
		fpcr.addPatientConsent(c1);

		assertTrue(c4 == fpcr.getPatientConsents().get(0));
		assertTrue(c3 == fpcr.getPatientConsents().get(1));
		assertTrue(c2 == fpcr.getPatientConsents().get(2));
		assertTrue(c1 == fpcr.getPatientConsents().get(3));
		assertTrue(c0 == fpcr.getPatientConsents().get(4));
	}

	/**
	 * Tests, whether {@link PatientConsent} are correctly sorted by
	 * {@link PatientConsent#getStartDate()}.
	 */
	@Test
	public void testConsentSortByDateNewestFirst() {
		Patient patient = new Patient("1",
				patientIDAssigningAuthorityUniversalId, "A", "B", new Date(),
				Gender.NOT_APPLICABLE, new PatientAddress());
		CoalaAuthor author = new CoalaAuthor("Dr.", "Keno", "März");

		PatientConsent c0 = new PatientConsent(convertStringToDate("20010101"),
				new Date(), PatientConsentPolicy.ONE, patient, false, author,
				new Date(), TAG_XML);
		PatientConsent c1 = new PatientConsent(convertStringToDate("20020101"),
				new Date(), PatientConsentPolicy.ONE, patient, false, author,
				new Date(), TAG_XML);
		PatientConsent c2 = new PatientConsent(convertStringToDate("20020201"),
				new Date(), PatientConsentPolicy.ONE, patient, false, author,
				new Date(), TAG_XML);
		PatientConsent c3 = new PatientConsent(convertStringToDate("20020202"),
				new Date(), PatientConsentPolicy.ONE, patient, false, author,
				new Date(), TAG_XML);
		PatientConsent c4 = new PatientConsent(convertStringToDate("20050101"),
				new Date(), PatientConsentPolicy.ONE, patient, false, author,
				new Date(), TAG_XML);

		FindPatientConsentResult fpcr = new FindPatientConsentResult(patient,
				ConsentSortParameter.START_DATE_NEWEST_FIRST);
		fpcr.addPatientConsent(c3);
		fpcr.addPatientConsent(c2);
		fpcr.addPatientConsent(c0);
		fpcr.addPatientConsent(c4);
		fpcr.addPatientConsent(c1);

		assertTrue(c0 == fpcr.getPatientConsents().get(0));
		assertTrue(c1 == fpcr.getPatientConsents().get(1));
		assertTrue(c2 == fpcr.getPatientConsents().get(2));
		assertTrue(c3 == fpcr.getPatientConsents().get(3));
		assertTrue(c4 == fpcr.getPatientConsents().get(4));
	}

	/**
	 * Tests, whether {@link PatientConsent} are correctly sorted by
	 * {@link PatientConsent#isObsolete()}.
	 */
	@Test
	public void testConsentSortByObsolete() {
		Patient patient = new Patient("1",
				patientIDAssigningAuthorityUniversalId, "A", "B", new Date(),
				Gender.NOT_APPLICABLE, new PatientAddress());
		CoalaAuthor author = new CoalaAuthor("Dr.", "Keno", "März");
		PatientConsent c0 = new PatientConsent(convertStringToDate("20010101"),
				new Date(), PatientConsentPolicy.ONE, patient, false, author,
				new Date(), TAG_XML);
		PatientConsent c1 = new PatientConsent(convertStringToDate("20010101"),
				new Date(), PatientConsentPolicy.ONE, patient, false, author,
				new Date(), TAG_XML);
		PatientConsent c2 = new PatientConsent(convertStringToDate("20010101"),
				new Date(), PatientConsentPolicy.ONE, patient, false, author,
				new Date(), TAG_XML);
		PatientConsent c3 = new PatientConsent(convertStringToDate("20010101"),
				new Date(), PatientConsentPolicy.ONE, patient, true, author,
				new Date(), TAG_XML);
		PatientConsent c4 = new PatientConsent(convertStringToDate("20010101"),
				new Date(), PatientConsentPolicy.ONE, patient, true, author,
				new Date(), TAG_XML);

		FindPatientConsentResult fpcr = new FindPatientConsentResult(patient,
				ConsentSortParameter.OBSOLETE_FALSE_FIRST);
		fpcr.addPatientConsent(c3);
		fpcr.addPatientConsent(c2);
		fpcr.addPatientConsent(c0);
		fpcr.addPatientConsent(c4);
		fpcr.addPatientConsent(c1);

		assertFalse(fpcr.getPatientConsents().get(0).isObsolete());
		assertFalse(fpcr.getPatientConsents().get(1).isObsolete());
		assertFalse(fpcr.getPatientConsents().get(2).isObsolete());
		assertTrue(fpcr.getPatientConsents().get(3).isObsolete());
		assertTrue(fpcr.getPatientConsents().get(4).isObsolete());
	}

	/**
	 * Tests, whether {@link PatientConsent} are correctly sorted by their
	 * {@link PatientConsent#getPolicyType()}.
	 */
	@Test
	public void testConsentSortByPolicy() {
		Patient patient = new Patient("1",
				patientIDAssigningAuthorityUniversalId, "A", "B", new Date(),
				Gender.NOT_APPLICABLE, new PatientAddress());
		CoalaAuthor author = new CoalaAuthor("Dr.", "Keno", "März");

		PatientConsent c0 = new PatientConsent(convertStringToDate("20010101"),
				new Date(), PatientConsentPolicy.ONE, patient, false, author,
				new Date(), TAG_XML);
		PatientConsent c1 = new PatientConsent(convertStringToDate("20010101"),
				new Date(), PatientConsentPolicy.TWO, patient, false, author,
				new Date(), TAG_XML);
		PatientConsent c2 = new PatientConsent(convertStringToDate("20010101"),
				new Date(), PatientConsentPolicy.THREE, patient, false, author,
				new Date(), TAG_XML);
		PatientConsent c3 = new PatientConsent(convertStringToDate("20010101"),
				new Date(), PatientConsentPolicy.FOUR, patient, true, author,
				new Date(), TAG_XML);
		PatientConsent c4 = new PatientConsent(convertStringToDate("20010101"),
				new Date(), PatientConsentPolicy.FIVE, patient, true, author,
				new Date(), TAG_XML);

		FindPatientConsentResult fpcr = new FindPatientConsentResult(patient,
				ConsentSortParameter.POLICY_TYPE_ASCENDING);
		fpcr.addPatientConsent(c3);
		fpcr.addPatientConsent(c2);
		fpcr.addPatientConsent(c0);
		fpcr.addPatientConsent(c4);
		fpcr.addPatientConsent(c1);

		assertTrue(c0 == fpcr.getPatientConsents().get(0));
		assertTrue(c1 == fpcr.getPatientConsents().get(1));
		assertTrue(c2 == fpcr.getPatientConsents().get(2));
		assertTrue(c3 == fpcr.getPatientConsents().get(3));
		assertTrue(c4 == fpcr.getPatientConsents().get(4));
	}

	@Test
	public void testPolicyCodes() {
		assertEquals(
				PatientConsentPolicy.getPolicyType("1.2.840.113619.20.2.9.1"),
				PatientConsentPolicy.ONE);
		assertEquals(
				PatientConsentPolicy.getPolicyType("1.2.840.113619.20.2.9.2"),
				PatientConsentPolicy.TWO);
		assertEquals(
				PatientConsentPolicy.getPolicyType("1.2.840.113619.20.2.9.3"),
				PatientConsentPolicy.THREE);
		assertEquals(
				PatientConsentPolicy.getPolicyType("1.2.840.113619.20.2.9.4"),
				PatientConsentPolicy.FOUR);
		assertEquals(
				PatientConsentPolicy.getPolicyType("1.2.840.113619.20.2.9.5"),
				PatientConsentPolicy.FIVE);

		assertEquals("1.2.840.113619.20.2.9.1",
				PatientConsentPolicy.ONE.getCode());
		assertEquals("1.2.840.113619.20.2.9.2",
				PatientConsentPolicy.TWO.getCode());
		assertEquals("1.2.840.113619.20.2.9.3",
				PatientConsentPolicy.THREE.getCode());
		assertEquals("1.2.840.113619.20.2.9.4",
				PatientConsentPolicy.FOUR.getCode());
		assertEquals("1.2.840.113619.20.2.9.5",
				PatientConsentPolicy.FIVE.getCode());
	}

	/**
	 * Test if {@link Gender#fromString(String)} works as expected.
	 */
	@Test
	public void testGenderFromString() {
		assertEquals(Gender.AMBIGUOUS, Gender.fromString("A"));
		assertEquals(Gender.FEMALE, Gender.fromString("F"));
		assertEquals(Gender.MALE, Gender.fromString("M"));
		assertEquals(Gender.NOT_APPLICABLE, Gender.fromString("N"));
		assertEquals(Gender.UNKNOWN, Gender.fromString("U"));
		assertEquals(Gender.EMPTY, Gender.fromString(""));
		// special case if some bla string is given
		assertEquals(Gender.EMPTY, Gender.fromString("blablabla"));
	}

	/**
	 * Test if {@link CoalaAuthor} basic attributes are handled correctly with
	 * constructor call.
	 */
	@Test
	public void testCoalaAuthorForValidInput() {
		CoalaAuthor pa = new CoalaAuthor("No-Dr.", "Karl-Theodor",
				"zu Guttenberg");
		assertEquals("Karl-Theodor", pa.getGivenName());
		assertEquals("zu Guttenberg", pa.getFamilyName());
		assertEquals("No-Dr.", pa.getTitle());
		assertEquals(pa, new CoalaAuthor("No-Dr.", "Karl-Theodor",
				"zu Guttenberg"));
	}

	/**
	 * Test if {@link CoalaAuthor} basic attributes are handled correctly with
	 * constructor call.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCoalaAuthorForEmptyFamilyName() {
		new CoalaAuthor("No-Dr.", "Karl-Theodor", "");
	}

	/**
	 * Test if {@link CoalaAuthor} basic attributes are handled correctly with
	 * constructor call.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCoalaAuthorForNullFamilyName() {
		new CoalaAuthor("No-Dr.", "Karl-Theodor", null);
	}

	/**
	 * Test if {@link CoalaAuthor} basic attributes are handled correctly with
	 * constructor call.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCoalaAuthorForEmptyGivenName() {
		new CoalaAuthor("No-Dr.", "", "zu Guttenberg");
	}

	/**
	 * Test if {@link CoalaAuthor} basic attributes are handled correctly with
	 * constructor call.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCoalaAuthorForNullGivenName() {
		new CoalaAuthor("No-Dr.", null, "zu Guttenberg");
	}

	/**
	 * Test if {@link PatientAddress} basic attributes are handled correctly
	 * with constructor call.
	 */
	@Test
	public void testPatientAddressForValidInput() {
		PatientAddress pa = new PatientAddress("A", "B", "C");
		assertTrue(pa.getStreetAddress().equals("A"));
		assertTrue(pa.getCity().equals("B"));
		assertTrue(pa.getZipOrPostalCode().equals("C"));
		assertEquals(pa, new PatientAddress("A", "B", "C"));
	}

	/**
	 * Test if {@link PatientAddress} a null value is punished with an
	 * {@link IllegalArgumentException} for a given attribute of
	 * {@link PatientAddress}
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testPatientAddressForInvalidStreet() {
		new PatientAddress(null, "B", "C");
	}

	/**
	 * Test if {@link PatientAddress} a null value is punished with an
	 * {@link IllegalArgumentException} for a given attribute of
	 * {@link PatientAddress}
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testPatientAddressForInvalidCity() {
		new PatientAddress("A", null, "C");
	}

	/**
	 * Test if {@link PatientAddress} a null value is punished with an
	 * {@link IllegalArgumentException} for a given attribute of
	 * {@link PatientAddress}
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testPatientAddressForInvalidPostalCode() {
		new PatientAddress("A", "B", null);
	}

	/**
	 * Converts a PXS date representation into an java Date.
	 * 
	 * @param pxsDate
	 *            the pxs dates string
	 * @return a java date that represents the same time as the date string
	 */
	private static Date convertStringToDate(String pxsDate) {
		Date result;
		try {
			int year = Integer.valueOf(pxsDate.substring(0, 4)); // yyyy
			int month = Integer.valueOf(pxsDate.substring(4, 6)); // MM
			int day = Integer.valueOf(pxsDate.substring(6, 8)); // dd

			GregorianCalendar cal = new GregorianCalendar();
			cal.set(Calendar.YEAR, year);
			// CAVE: -1 is needed to ensure correct months, as
			// months start with 0 = JAN (!)
			cal.set(Calendar.MONTH, month - 1);
			cal.set(Calendar.DAY_OF_MONTH, day);
			result = cal.getTime();
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Could not convert PXS Date representation to java Date. Invalid DateString was: "
							+ pxsDate);
		}
		return result;
	}

}
