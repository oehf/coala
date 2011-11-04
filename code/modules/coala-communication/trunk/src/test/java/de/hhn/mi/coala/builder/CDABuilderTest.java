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
package de.hhn.mi.coala.builder;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.DocumentEntry;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.hhn.mi.coala.domain.CoalaAuthor;
import de.hhn.mi.coala.domain.Gender;
import de.hhn.mi.coala.domain.Patient;
import de.hhn.mi.coala.domain.PatientAddress;
import de.hhn.mi.coala.domain.PatientConsentPolicy;
import de.hhn.mi.coala.interfacing.CDATransformationService;

/**
 * Unit test for {@link CDATransformationService}.
 * 
 * @author hhein, mwiesner
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/coala-communication-test-context.xml" })
public class CDABuilderTest {

	private static final Logger LOG = org.slf4j.LoggerFactory
			.getLogger(CDABuilderTest.class);
	
	@Autowired
	private CDABuilder cdaBuilder;
	@Autowired
	private DocumentEntryBuilder documentEntryBuilder;

	@After
	public void tearDown() {
		cdaBuilder = null;
	}

	@Test
	public void testCDABuilderNonEmptyResult() {
		Patient patient = createPatient();
		DocumentEntry de = documentEntryBuilder.createDocumentEntry(
				new CoalaAuthor("testName", "testName", "testName"), patient,
				PatientConsentPolicy.ONE, new Date(), new Date());

		String cdaResult = cdaBuilder.createConsentCDA(patient,
				PatientConsentPolicy.ONE, de);
		assertNotNull(cdaResult);
		assertTrue(!cdaResult.trim().isEmpty());
		LOG.info("CDA result: " + cdaResult);
	}

	@Test
	public void testCreateConsentCDAValid() {
		Patient patient = createPatient();
		DocumentEntry documentEntry = documentEntryBuilder.createDocumentEntry(
				new CoalaAuthor("testName", "testName", "testName"), patient,
				PatientConsentPolicy.ONE, new Date(), new Date());

		String cdaResult = cdaBuilder.createConsentCDA(patient,
				PatientConsentPolicy.ONE, documentEntry);

		assertTrue(cdaResult.contains(documentEntry.getUniqueId()));
		assertTrue(cdaResult.contains(documentEntry.getTypeCode().getCode()));
		assertTrue(cdaResult.contains(documentEntry.getTypeCode()
				.getDisplayName().getValue()));
		assertTrue(cdaResult.contains(documentEntry.getTypeCode()
				.getSchemeName()));
		assertTrue(cdaResult.contains(documentEntry.getCreationTime()));
		assertTrue(cdaResult.contains(documentEntry.getConfidentialityCodes()
				.get(0).getCode()));
		assertTrue(cdaResult.contains(documentEntry.getConfidentialityCodes()
				.get(0).getDisplayName().getValue()));
		assertTrue(cdaResult.contains(documentEntry.getConfidentialityCodes()
				.get(0).getSchemeName()));
		assertTrue(cdaResult.contains(documentEntry.getLanguageCode()));

		assertTrue(cdaResult.contains(patient.getLastName()));
		assertTrue(cdaResult.contains(patient.getGivenName()));
		assertTrue(cdaResult.contains(patient.getAddress().getCity()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateConsentCDAInvalidParam1() {
		Patient patient = createPatient();
		DocumentEntry documentEntry = documentEntryBuilder.createDocumentEntry(
				new CoalaAuthor("testName", "testName", "testName"), patient,
				PatientConsentPolicy.ONE, new Date(), new Date());
		cdaBuilder.createConsentCDA(null, PatientConsentPolicy.ONE,
				documentEntry);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateConsentCDAInvalidParam2() {
		Patient patient = createPatient();
		DocumentEntry documentEntry = documentEntryBuilder.createDocumentEntry(
				new CoalaAuthor("testName", "testName", "testName"), patient,
				PatientConsentPolicy.ONE, new Date(), new Date());
		cdaBuilder.createConsentCDA(patient, null, documentEntry);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateConsentCDAInvalidParam3() {
		Patient patient = createPatient();
		cdaBuilder.createConsentCDA(patient, PatientConsentPolicy.ONE, null);
	}

	@Test(expected = RuntimeException.class)
	public void testConstructorInvalid1() {
		new CDABuilder(null);
	}

	@Test(expected = RuntimeException.class)
	public void testConstructorInvalid2() {
		new CDABuilder("");
	}

	private Patient createPatient() {

		PatientAddress pA = new PatientAddress();
		pA.setCity("Town");
		pA.setStreetAddress("Street 21");

		Patient p = new Patient("1", "2.16.840.1.113883.3.37.4.1.1.2.2.1",
				"Chuck", "Norris", new Date(), Gender.UNKNOWN, pA);
		return p;
	}

}
