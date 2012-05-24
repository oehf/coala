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
package org.openehealth.coala.communication;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit test for {@link PdqMessageBuilderImpl}.
 * 
 * @author ckarmen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/coala-communication-test-context.xml" })
public class PdqMessageBuilderTest {

	// private static final Logger LOG = org.slf4j.LoggerFactory
	// .getLogger(PXSQueryServiceTest.class);

	private static final String PID_PatientID = "@PID.3.1";
	private static final String PID_LastName = "@PID.5.1";
	private static final String PID_GivenName = "@PID.5.2";
	private static final String PID_DoB = "@PID.7";

	private static final String HEADER = "MSH|^~\\&|";
	private static final String FOOTER = "RCP";
	
	@Autowired
	private PdqMessageBuilder pdqMessageBuilder;

	/**
	 * Test if PdqRequest (HL7 message) contains only expected PID values.
	 * {@link PdqMessageBuilderImpl}.
	 */
	@Test
	public void testPdqMessage() {
		// LOG.info("");

		String hl7Message, thePatientId = "123", theGivenName = "John", theLastName = "Doe";
		Date theDoB = new Date();
		
		// Example result:
		// MSH|^~\\&|CoALA|SendingFacility|PXS|ReceivingFacility|20110707092807101+0200||QBP^Q22|6326568982331147264|P|2.5|||||||\nQPD|Q22^Find Candidates|54152038513763936462005704923487680|@PID.3.1^123~@PID.3.4.2^2.16.840.1.113883.3.37.4.1.1.2.2.1~@PID.3.4.3^ISO~@PID.5.2^John~@PID.5.1^Doe~@PID.7^20110707|\nRCP|I|
		
		hl7Message = pdqMessageBuilder.buildPdqRequest(thePatientId,
				theGivenName, theLastName, theDoB);
		assertTrue(
				"HL7 message has invalid data!",
				hl7Message.contains(HEADER) && hl7Message.contains(FOOTER)
						&& hl7Message.contains(PID_PatientID)
						&& hl7Message.contains(PID_LastName)
						&& hl7Message.contains(PID_GivenName)
						&& hl7Message.contains(PID_DoB));

		hl7Message = pdqMessageBuilder.buildPdqRequest(null, null, null, null);
		assertTrue(
				"HL7 message has invalid data!",
				hl7Message.contains(HEADER) && hl7Message.contains(FOOTER)
						&& !hl7Message.contains(PID_PatientID)
						&& !hl7Message.contains(PID_LastName)
						&& !hl7Message.contains(PID_GivenName)
						&& !hl7Message.contains(PID_DoB));

		hl7Message = pdqMessageBuilder.buildPdqRequest("", "", "", null);
		assertTrue(
				"HL7 message has invalid data!",
				hl7Message.contains(HEADER) && hl7Message.contains(FOOTER)
						&& !hl7Message.contains(PID_PatientID)
						&& !hl7Message.contains(PID_LastName)
						&& !hl7Message.contains(PID_GivenName)
						&& !hl7Message.contains(PID_DoB));

		hl7Message = pdqMessageBuilder.buildPdqRequest(null, theGivenName,
				theLastName, theDoB);
		assertTrue(
				"HL7 message has invalid data!",
				hl7Message.contains(HEADER) && hl7Message.contains(FOOTER)
						&& !hl7Message.contains(PID_PatientID)
						&& hl7Message.contains(PID_LastName)
						&& hl7Message.contains(PID_GivenName)
						&& hl7Message.contains(PID_DoB));

		hl7Message = pdqMessageBuilder.buildPdqRequest("", theGivenName,
				theLastName, theDoB);
		assertTrue(
				"HL7 message has invalid data!",
				hl7Message.contains(HEADER) && hl7Message.contains(FOOTER)
						&& !hl7Message.contains(PID_PatientID)
						&& hl7Message.contains(PID_LastName)
						&& hl7Message.contains(PID_GivenName)
						&& hl7Message.contains(PID_DoB));

		hl7Message = pdqMessageBuilder.buildPdqRequest(thePatientId, null,
				theLastName, theDoB);
		assertTrue(
				"HL7 message has invalid data!",
				hl7Message.contains(HEADER) && hl7Message.contains(FOOTER)
						&& hl7Message.contains(PID_PatientID)
						&& hl7Message.contains(PID_LastName)
						&& !hl7Message.contains(PID_GivenName)
						&& hl7Message.contains(PID_DoB));

		hl7Message = pdqMessageBuilder.buildPdqRequest(thePatientId, "",
				theLastName, theDoB);
		assertTrue(
				"HL7 message has invalid data!",
				hl7Message.contains(HEADER) && hl7Message.contains(FOOTER)
						&& hl7Message.contains(PID_PatientID)
						&& hl7Message.contains(PID_LastName)
						&& !hl7Message.contains(PID_GivenName)
						&& hl7Message.contains(PID_DoB));

		hl7Message = pdqMessageBuilder.buildPdqRequest(thePatientId,
				theGivenName, null, theDoB);
		assertTrue(
				"HL7 message has invalid data!",
				hl7Message.contains(HEADER) && hl7Message.contains(FOOTER)
						&& hl7Message.contains(PID_PatientID)
						&& !hl7Message.contains(PID_LastName)
						&& hl7Message.contains(PID_GivenName)
						&& hl7Message.contains(PID_DoB));

		hl7Message = pdqMessageBuilder.buildPdqRequest(thePatientId,
				theGivenName, "", theDoB);
		assertTrue(
				"HL7 message has invalid data!",
				hl7Message.contains(HEADER) && hl7Message.contains(FOOTER)
						&& hl7Message.contains(PID_PatientID)
						&& !hl7Message.contains(PID_LastName)
						&& hl7Message.contains(PID_GivenName)
						&& hl7Message.contains(PID_DoB));

		hl7Message = pdqMessageBuilder.buildPdqRequest(thePatientId,
				theGivenName, theLastName, null);
		assertTrue(
				"HL7 message has invalid data!",
				hl7Message.contains(HEADER) && hl7Message.contains(FOOTER)
						&& hl7Message.contains(PID_PatientID)
						&& hl7Message.contains(PID_LastName)
						&& hl7Message.contains(PID_GivenName)
						&& !hl7Message.contains(PID_DoB));

	}
}
