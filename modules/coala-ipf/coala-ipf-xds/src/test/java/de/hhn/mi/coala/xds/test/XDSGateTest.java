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
package de.hhn.mi.coala.xds.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openehealth.coala.exception.XDSRequestFailedException;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.AvailabilityStatus;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.DocumentEntry;
import org.openehealth.ipf.commons.ihe.xds.core.responses.RetrievedDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.hhn.mi.coala.xds.XDSGate;
import de.hhn.mi.coala.xds.XDSGateImpl;

/**
 * Tests the {@link XDSGateImpl} for XDS ITI18 and ITI43 functionality.
 * 
 * @author siekmann
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/coala-xds-test-context.xml" })
@Ignore
public class XDSGateTest {

//	private static final Logger LOG = org.slf4j.LoggerFactory
//			.getLogger(XDSGateTest.class);

	protected static final String DEMO_MPI_PID_WITH_CONSENT = "305010";
	protected static final String DEMO_MPI_PID_WITHOUT_CONSENT = "2";

	private ArrayList<AvailabilityStatus> stati;

	@Autowired
	private XDSGate xdsGate;
	
	@Test
	public void testSpringContext() {
		assertNotNull(xdsGate);
	}
	
	@Before
	public void init() {
		stati = new ArrayList<AvailabilityStatus>();
		stati.add(AvailabilityStatus.APPROVED);
		stati.add(AvailabilityStatus.SUBMITTED);
		stati.add(AvailabilityStatus.DEPRECATED);

	}

	/**
	 * Tests that valid request for a patient ID with a consent is processed
	 * correct (at least one consent document).
	 */
	@Test
	public void testRequestConsentDocumentListValidRequestWithConsents() {
		List<DocumentEntry> documents = xdsGate.requestConsents(
				DEMO_MPI_PID_WITH_CONSENT, stati, null);
		assertNotNull(documents);
		assertTrue(documents.size() > 0);
	}

	/**
	 * Tests that valid request for a patient ID with no consent is processed
	 * correct.
	 */
	@Test
	public void testRequestConsentDocumentListValidRequestWithNoConsents() {
		List<DocumentEntry> documents = xdsGate.requestConsents(
				DEMO_MPI_PID_WITHOUT_CONSENT, stati, null);
		assertNotNull(documents);
		assertTrue(documents.size() == 0);
	}

	/**
	 * Tests that a consent request with an invalid MPI PID
	 * (=10000000000000000000000) is processed correct. This means that no
	 * consent documents are found.
	 */
	@Test
	public void testRequestConsentDocumentListInvalidMpiPID() {
		List<DocumentEntry> documents = xdsGate.requestConsents(
				"10000000000000000000000", stati, null);
		assertNotNull(documents);
		assertTrue(documents.size() == 0);
	}

	/**
	 * Tests that a consent request with a <code>null</code> as MPI PID is
	 * throwing a {@link XDSRequestFailedException}.
	 */
	@Test(expected = XDSRequestFailedException.class)
	public void testRequestConsentDocumentListMpiPIDIsNULL() {
		xdsGate.requestConsents(null, stati, null);
	}

	/**
	 * Tests that a consent request with a non-numeric MPI PID (=a) is throwing
	 * a {@link XDSRequestFailedException}.
	 */
	@Test(expected = XDSRequestFailedException.class)
	public void testRequestConsentDocumentListMpiPIDIsNonNumeric() {
		xdsGate.requestConsents("a", stati, null);
	}

	/**
	 * Tests that a consent request with a negative MPI PID is throwing a
	 * {@link XDSRequestFailedException}.
	 */
	@Test(expected = XDSRequestFailedException.class)
	public void testRequestConsentDocumentListMpiPIDIsNegativ() {
		xdsGate.requestConsents("-1", stati, null);
	}

	/**
	 * Tests that a consent request with an empty MPI PID is throwing a
	 * {@link XDSRequestFailedException}.
	 */
	@Test(expected = XDSRequestFailedException.class)
	public void testRequestConsentDocumentListMpiPIDIsEmpty() {
		xdsGate.requestConsents("", stati, null);
	}

	/**
	 * Tests that a consent request with a <code>null</code> as available stati
	 * is throwing a {@link XDSRequestFailedException}.
	 */
	@Test(expected = XDSRequestFailedException.class)
	public void testRequestConsentDocumentListStatiNull() {
		xdsGate.requestConsents(DEMO_MPI_PID_WITH_CONSENT, null, null);
	}

	/**
	 * Tests that a consent request with an empty stati list is throwing a
	 * {@link XDSRequestFailedException}.
	 */
	@Test(expected = XDSRequestFailedException.class)
	public void testRequestConsentDocumentListEmptyStatiList() {
		stati.clear();
		xdsGate.requestConsents(DEMO_MPI_PID_WITH_CONSENT, stati, null);
	}

	/**
	 * Tests that a retrieve document with valid data is working correct.
	 */
	@Test
	public void testRetrieveDocumentSetValid() {
		stati.clear();
		stati.add(AvailabilityStatus.APPROVED);

		List<DocumentEntry> documents = xdsGate.requestConsents(
				DEMO_MPI_PID_WITH_CONSENT, stati, null);
		assertNotNull(documents);
		assertTrue(documents.size() > 0);
		DocumentEntry entry = documents.get(0);
		List<RetrievedDocument> retrievedDocs = xdsGate
				.retrieveDocumentSet(entry);
		assertNotNull(retrievedDocs);
		assertTrue(retrievedDocs.size() > 0);
	}

	/**
	 * Tests that a document retrieve with a <code>null</code> entry is throwing
	 * an {@link XDSRequestFailedException}.
	 */
	@Test(expected = XDSRequestFailedException.class)
	public void testRetrieveDocumentSetWithNullEntry() {
		xdsGate.retrieveDocumentSet(null);
	}

}