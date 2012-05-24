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

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openehealth.coala.util.PXSDateConverter;
import org.slf4j.Logger;

/**
 * Class to build HL7 (v2.5) PDQ (Patient Demographic Queries) requests.
 * 
 * @author ckarmen, hhein
 * 
 */
public class PdqMessageBuilderImpl implements PdqMessageBuilder {

	private static final Logger LOG = org.slf4j.LoggerFactory
			.getLogger(PdqMessageBuilderImpl.class);
	// TODO Auslagern in globale Konfiguration (Interface, Spring, etc.)
	private static final String HL7_PatientIdAssigningAuthorityUniversalId = "2.16.840.1.113883.3.37.4.1.1.2.2.1";
	private static final String HL7_PatientIdAssigningAuthorityUniversalIdType = "ISO";
	private static final String HL7_SendingFacility = "SendingFacility";
	private static final String HL7_ReceivingFacility = "ReceivingFacility";
	private static final String HL7_SendingApplication = "CoALA";
	private static final String HL7_ReceivingApplication = "PXS";

	// HL7 static segments
	private static final String PID_PatientID = "@PID.3.1^";
	private static final String PID_LastName = "@PID.5.1^";
	private static final String PID_GivenName = "@PID.5.2^";
	private static final String PID_DoB = "@PID.7^";
	private static final String PID_PatientIdAssigningAuthorityUniversalId = "@PID.3.4.2^";
	private static final String PID_PatientIdAssigningAuthorityUniversalIdType = "@PID.3.4.3^";

	private static final String HL7_SEPERATOR = "|";
	

	private PXSDateConverter pxsDateConverter;

	/* (non-Javadoc)
	 * @see org.openehealth.coala.communication.PdqMessageBuilder#setPxsDateConverter(org.openehealth.coala.util.PXSDateConverter)
	 */
	@Override
	public void setPxsDateConverter(PXSDateConverter pxsDateConverter) {
		this.pxsDateConverter = pxsDateConverter;
	}

	/* (non-Javadoc)
	 * @see org.openehealth.coala.communication.PdqMessageBuilder#buildPdqRequest(java.lang.String, java.lang.String, java.lang.String, java.util.Date)
	 */
	@Override
	public  String buildPdqRequest(String thePatientId,
			String theGivenName, String theLastName, Date theDoB) {
		return buildPdqRequest(thePatientId,
				HL7_PatientIdAssigningAuthorityUniversalId, theGivenName,
				theLastName, theDoB);
	}

	/* (non-Javadoc)
	 * @see org.openehealth.coala.communication.PdqMessageBuilder#buildPdqRequest(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Date)
	 */
	@Override
	public String buildPdqRequest(String thePatientId,
			String thePatientIdAssigningAuthorityUniversalId,
			String theGivenName, String theLastName, Date theDoB) {

		String thePatientIdStr = "", theGivenNameStr = "", theLastNameStr = "", theDoBStr = "", isAnd = "";

		if (!isNullorEmpty(thePatientId)
				&& !isNullorEmpty(thePatientIdAssigningAuthorityUniversalId)) {
			thePatientIdStr = PID_PatientID + thePatientId + "~"
					+ PID_PatientIdAssigningAuthorityUniversalId
					+ thePatientIdAssigningAuthorityUniversalId + "~"
					+ PID_PatientIdAssigningAuthorityUniversalIdType
					+ HL7_PatientIdAssigningAuthorityUniversalIdType;
			isAnd = "~";
		}
		Charset charset = Charset.forName("ISO-8859-1");
		if (!isNullorEmpty(theGivenName)) {
			/*
			 * Charset conversion from UTF-8 to ISO encoding as MPI HL7 queries
			 * will fail for umlauts otherwise!
			 */
			String givenNameISO = new String(theGivenName.getBytes(), charset);
			theGivenName = givenNameISO;
			theGivenNameStr = isAnd + PID_GivenName + theGivenName;
			isAnd = "~";
		}
		if (!isNullorEmpty(theLastName)) {
			/*
			 * Charset conversion from UTF-8 to ISO encoding as MPI HL7 queries
			 * will fail for umlauts otherwise!
			 */
			String lastNameISO = new String(theLastName.getBytes(), charset);
			theLastName = lastNameISO;

			theLastNameStr = isAnd + PID_LastName + theLastName;
			isAnd = "~";
		}
		if (theDoB != null) {
			theDoBStr = isAnd + PID_DoB
					+ pxsDateConverter.DateToShortString(theDoB);
		}

		final String QBP_MESSAGE_TYPE = "QBP^Q22";
		String aMessageHeader = generateMSH(QBP_MESSAGE_TYPE);

		String aQPDSegment = "QPD|Q22^Find Candidates" + HL7_SEPERATOR
				+ getRandomNumber(17) + getRandomNumber(18) + HL7_SEPERATOR
				+ thePatientIdStr + theGivenNameStr + theLastNameStr
				+ theDoBStr + HL7_SEPERATOR;

		// Response Control Parameter: "I" means that response should come
		// immediately
		final String RPC_SEGMENT = "RCP" + HL7_SEPERATOR + "I" + HL7_SEPERATOR;

		String hl7request = aMessageHeader + "\n" + aQPDSegment + "\n"
				+ RPC_SEGMENT;

		LOG.info(hl7request);
		return hl7request;
	}

	/**
	 * Generates a generic HL7 header segment.
	 * 
	 * @param theMessageType
	 *            HL7 message type
	 * @return HL7 header segment
	 */
	private  String generateMSH(String theMessageType) {
		// HINT: PXSDateConverted() is not used for the purpose that missing time
		// AND timezone may cause timing issues for HL7 server.
		SimpleDateFormat aSDF = new SimpleDateFormat("yyyyMMddHHmmssSSZ");
		long aRandom = getRandomNumber(19);

		StringBuilder hl7header = new StringBuilder("MSH" + HL7_SEPERATOR);
		hl7header.append("^~\\&" + HL7_SEPERATOR);
		hl7header.append(HL7_SendingApplication + HL7_SEPERATOR);
		hl7header.append(HL7_SendingFacility + HL7_SEPERATOR);
		hl7header.append(HL7_ReceivingApplication + HL7_SEPERATOR);
		hl7header.append(HL7_ReceivingFacility + HL7_SEPERATOR);
		hl7header.append(aSDF.format(new Date()) + HL7_SEPERATOR);
		hl7header.append(HL7_SEPERATOR);
		hl7header.append(theMessageType + HL7_SEPERATOR);
		hl7header.append(aRandom + HL7_SEPERATOR);
		hl7header.append("P" + HL7_SEPERATOR);
		hl7header.append(HL7_VERSION + HL7_SEPERATOR);
		hl7header.append(HL7_SEPERATOR + HL7_SEPERATOR + HL7_SEPERATOR
				+ HL7_SEPERATOR + HL7_SEPERATOR + HL7_SEPERATOR);

		return hl7header.toString();
	}

	/**
	 * Generates a random number.
	 * 
	 * @param theLength
	 *            Length of digits (max. 19!)
	 * @return the random number
	 */
	private long getRandomNumber(int theLength) {
		return (long) (Math.random() * Math.pow(10, theLength));
	}

	/**
	 * Checks if given String is "" or null.
	 * 
	 * @param theString
	 *            String to check
	 * @return true when string is "" or null
	 */
	private  boolean isNullorEmpty(String theString) {
		return (theString == null || theString.equals(""));
	}

}
