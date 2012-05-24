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

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openehealth.coala.exception.XDSRequestFailedException;
import org.openehealth.coala.xds.XDSTransactorImpl;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.AvailabilityStatus;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.DocumentEntry;
import org.openehealth.ipf.commons.ihe.xds.core.responses.RetrievedDocument;
import org.slf4j.Logger;

/**
 * Class which tests the {@link XDSTransactorImpl} for valid ITI-43 requests.
 * 
 * @author siekmann, mwiesner
 * 
 */
public class XDSTransactorIti43Test extends XDSBaseTest {

	private static final Logger LOG = org.slf4j.LoggerFactory
			.getLogger(XDSTransactorIti43Test.class);

	private List<AvailabilityStatus> stati;

	@Before
	public void init() {
		stati = new ArrayList<AvailabilityStatus>();
		stati.add(AvailabilityStatus.APPROVED);
		stati.add(AvailabilityStatus.SUBMITTED);
		stati.add(AvailabilityStatus.DEPRECATED);
	}

	/**
	 * Tests the retrieval of a consent for a pid that contains a consent
	 */
	@Test
	public void testRetrieveConsentDocumentForPID() {

		List<AvailabilityStatus> stati = new ArrayList<AvailabilityStatus>();

		try {
			stati.add(AvailabilityStatus.APPROVED);

			List<DocumentEntry> documents = xdsTransactor
					.getConsentDocumentList(PID_CONSENT_FIND, stati);
			assertNotNull(documents);
			assertTrue(documents.size() > 0);
			DocumentEntry entry = documents.get(1);
			List<RetrievedDocument> retrievedDocs = xdsTransactor
					.retrieveDocumentSet(entry);
			assertNotNull(retrievedDocs);
			assertTrue(retrievedDocs.size() > 0);

			RetrievedDocument retrievedDoc = retrievedDocs.get(0);
			String xmlContent = (String) retrievedDoc.getDataHandler()
					.getContent();
			assertNotNull(xmlContent);
			assertTrue(xmlContent
					.contains("<code code=\"57016-8\" displayName=\"Privacy Policy Acknowledgement Document\"\n\t\tcodeSystem=\"2.16.840.1.113883.6.1\" codeSystemName=\"LOINC\" />"));
			assertTrue(xmlContent
					.contains("<effectiveTime>\n\t\t\t\t<low value=\"20110705171421\" />\n\t\t\t\t<high value=\"20500805171421\" />\n\t\t\t</effectiveTime>"));

		} catch (Throwable t) {
			LOG.error(t.getLocalizedMessage(), t);
		}
	}

	/**
	 * Tests the retrieval of a consent with wrong parameter <code>null</code>.
	 */
	@Test(expected = XDSRequestFailedException.class)
	public void testRetrieveConsentDocumentForNullEntry() {
		xdsTransactor.retrieveDocumentSet(null);
	}

}
