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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.openehealth.coala.domain.ConsentSortParameter;
import org.openehealth.coala.domain.FindPatientConsentResult;
import org.openehealth.coala.domain.FindPatientQuery;
import org.openehealth.coala.domain.FindPatientResult;
import org.openehealth.coala.domain.Gender;
import org.openehealth.coala.domain.Patient;
import org.openehealth.coala.domain.PatientAddress;
import org.openehealth.coala.domain.PatientSortParameter;
import org.openehealth.coala.interfacing.ConsentCreationService;
import org.openehealth.coala.interfacing.PatientService;

/**
 * Simple JUnit test, which ensures the availability of the Coala-Communication
 * and Coala-IPF layer by using the PXSQueryService.
 * 
 * <p>
 * CAVE: You have to use a VPN connection needed for HHN environment setup.
 * 
 * @author mwiesner, astiefer
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/ehf-system-context.xml" })
public class PXSQueryServiceIntegrationTest {

	private static final String PID_FOR_PATIENT_FIND = "305010";

	private static final Logger LOG = org.slf4j.LoggerFactory
			.getLogger(PXSQueryServiceIntegrationTest.class);

	private String patientIDAssigningAuthorityUniversalId = "2.16.840.1.113883.3.37.4.1.1.2.2.1";
	
	@Autowired
	private PatientService pXSQueryService;

	@Autowired
	private ConsentCreationService consentCreationService;
	
	@Before
	public void setup() {
	}

	@After
	public void tearDown() {
	}

	/**
	 * Simply checks if it is possible to retrieve any information from the PXS
	 * MPI via coala-ipf-pdq module.
	 */
	@Test
	public void testMPIQuery() {
		FindPatientQuery findPatientQuery = new FindPatientQuery("", "ChuckFIND",
				"Norris", null);

		FindPatientResult findPatientResult = pXSQueryService
				.findPatients(findPatientQuery, PatientSortParameter.getDefault());
		assertNotNull(findPatientResult);
		assertNotNull(findPatientResult.getPatients());
		assertTrue(findPatientResult.getPatients().size() > 0);
		PatientAddress pa = findPatientResult.getPatients().get(0).getAddress();
		assertNotNull(pa);
		assertEquals("Max-Planck-Str. 39", pa.getStreetAddress());
		assertEquals("Heilbronn", pa.getCity());
		assertEquals("74081", pa.getZipOrPostalCode());
	}

	/**
	 * Simply checks if it is possible to retrieve any information from the PXS
	 * XDS repos via coala-ipf-xds module.
	 */
	@Test
	public void testXDSIti18Query() {
		// primarily the ID = 1 is important here.
		Patient chuck = new Patient(PID_FOR_PATIENT_FIND,
				patientIDAssigningAuthorityUniversalId, "ChuckFIND",
				"Norris", new Date(), Gender.MALE, new PatientAddress());

		LOG.info("Fetching consent documents for Mr. Norris (ChuckFIND) from within WEB-Layer (assembly)");
		// fetching Mr. Beckenbauer's consent documents - expected only one here
		FindPatientConsentResult consents = consentCreationService.getPatientConsents(
				chuck, ConsentSortParameter.START_DATE_NEWEST_FIRST);
		assertNotNull(consents);
		assertTrue(
				"The number of consents is not as expected for the response for a FindDocumentQuery",
				consents.getPatientConsents().size() > 0);
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(PXSQueryServiceIntegrationTest.class);
	}
}
