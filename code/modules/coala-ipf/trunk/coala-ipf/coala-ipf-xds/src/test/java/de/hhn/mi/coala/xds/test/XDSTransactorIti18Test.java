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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.AvailabilityStatus;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.DocumentEntry;
import org.slf4j.Logger;

import de.hhn.mi.coala.exception.XDSRequestFailedException;
import de.hhn.mi.coala.xds.XDSTransactorImpl;

/**
 * Class which tests the {@link XDSTransactorImpl} for valid ITI-18 requests.
 * 
 * For testing issues the patient with the ID "305010" is used.
 * 
 * 
 * @author siekmann, mwiesner
 * 
 */
public class XDSTransactorIti18Test extends XDSBaseTest {

	private static final Logger LOG = org.slf4j.LoggerFactory
			.getLogger(XDSTransactorIti18Test.class);

	private ArrayList<AvailabilityStatus> stati;

	@Before
	public void init() {

		stati = new ArrayList<AvailabilityStatus>();
		stati.add(AvailabilityStatus.APPROVED);
		stati.add(AvailabilityStatus.SUBMITTED);
		stati.add(AvailabilityStatus.DEPRECATED);
	}

	/**
	 * Test for basic communication. Sending a request to the PXS endpoint and
	 * expecting a valid response. <br />
	 * <ul>
	 * 1) This is tested by testing .
	 * <li>asserting that response is not NULL</li>
	 * <li>asserting that response contains at least one valid ConsentDocument</li>
	 * </ul>
	 * 
	 * @throws Exception
	 *             Thrown if anything went wrong.
	 */
	@Test
	public void testQueryConsentDocumentForValidMPIPID() throws Exception {

		List<DocumentEntry> documents = xdsTransactor.getConsentDocumentList(
				PID_CONSENT_FIND, stati);
		assertNotNull(documents);

		assertTrue(documents.size() > 0);
		boolean found = false;
		for (DocumentEntry dE : documents) {
			if (dE.getFormatCode()
					.getCode()
					.equalsIgnoreCase(xdsConfiguration.getConsentFormatCodeCode())) {
				assertEquals("Creation time of consent was not as expected.",
						dE.getCreationTime(), "20110707114611");
				found = true;
				break;
			}
		}
		assertTrue(
				"No consent document was found in the response for a FindDocumentQuery",
				found);
	}

	/**
	 * Test that an {@link XDSRequestFailedException} is thrown when calling the
	 * {@link XDSTransactorImpl#getConsentDocumentList(String, List)} with an
	 * empty mpiPID
	 */
	@Test(expected = XDSRequestFailedException.class)
	public void testQueryConsentDocumentForEmptyPID() {
		xdsTransactor.getConsentDocumentList("", stati);
	}

	/**
	 * Test that an {@link XDSRequestFailedException} is thrown when calling the
	 * {@link XDSTransactorImpl#getConsentDocumentList(String, List)} with an
	 * mpiPID that is null
	 */
	@Test(expected = XDSRequestFailedException.class)
	public void testQueryConsentDocumentForNullPID() {
		xdsTransactor.getConsentDocumentList(null, stati);
	}

	/**
	 * Test that an {@link XDSRequestFailedException} is thrown when calling the
	 * {@link XDSTransactorImpl#getConsentDocumentList(String, List)} with an
	 * non-numeric mpiPID
	 */
	@Test(expected = XDSRequestFailedException.class)
	public void testQueryConsentDocumentForCharacterPID() {
		xdsTransactor.getConsentDocumentList("ABC1234", stati);
	}

	/**
	 * Tests the number of received consent documents for the patient with the
	 * mpiPID = 1. In our demo PXS he has one consent with the status
	 * {@link AvailabilityStatus#APPROVED}.<br />
	 * First it is tested that no consent documents are given back for the stati
	 * {@link AvailabilityStatus#SUBMITTED} and
	 * {@link AvailabilityStatus#DEPRECATED}. And then it tested, that only one
	 * consent document is returned with the status
	 * {@link AvailabilityStatus#APPROVED}
	 */
	public void testQueryConsentDocumentForStati() {

		List<AvailabilityStatus> stati = new ArrayList<AvailabilityStatus>();
		stati.add(AvailabilityStatus.SUBMITTED);
		stati.add(AvailabilityStatus.DEPRECATED);

		try {
			List<DocumentEntry> documents = xdsTransactor
					.getConsentDocumentList(PID_CONSENT_FIND, stati);
			assertNotNull(documents);
			assertTrue(documents.size() == 0);

			stati.clear();
			stati.add(AvailabilityStatus.APPROVED);

			documents = xdsTransactor.getConsentDocumentList(PID_CONSENT_FIND,
					stati);
			assertNotNull(documents);
			assertTrue(documents.size() == 1);

		} catch (Throwable t) {
			LOG.error(t.getLocalizedMessage(), t);
		}
	}

}
