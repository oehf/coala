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
package de.hhn.mi.coala.converter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;

import de.hhn.mi.coala.communication.PdqMessageBuilder;
import de.hhn.mi.coala.domain.Gender;
import de.hhn.mi.coala.domain.Patient;
import de.hhn.mi.coala.domain.PatientAddress;
import de.hhn.mi.coala.util.PXSDateConverter;
import de.hhn.mi.coala.util.PXSDateConverterImpl;

/**
 * Class to convert HL7 PDQ message <-> result object.
 * 
 * @author C. Karmen
 * @author H. Hein
 * @author kmaerz
 */
public class PdqHL7Converter extends PXSDateConverterImpl implements PXSDateConverter {

	private static final Logger LOG = org.slf4j.LoggerFactory
			.getLogger(PdqHL7Converter.class);
	
	private PdqMessageBuilder pdqMessageBuilder;

	public void setPdqMessageBuilder(PdqMessageBuilder pdqMessageBuilder) {
		this.pdqMessageBuilder = pdqMessageBuilder;
	}

	/**
	 * Default Construktor.
	 */
	public PdqHL7Converter() {
		super();
	}

	/**
	 * Converts a HL7 PDQ message to a FindPatientResult object.
	 * 
	 * @param hl7message
	 *            the hl7 message
	 */
	public List<Patient> convertPdqToPatients(String hl7message) {

		LOG.info("Converting a HL7 PDQ-22 response: \n" + hl7message);

		List<Patient> patients = new ArrayList<Patient>();
		if (hl7message != null) {

			String[] hl7msgs = hl7message.split("\\|");
			for (int i = 0; hl7msgs.length > i; i++) {
				if (isNewPatientSegment(hl7msgs, i)) {

					// PID and Assigning Authority
					String[] pidComplete = hl7msgs[i + 3].split("\\^");
					String pid = pidComplete[0];
					String[] pidAssigningAuthorityUniversalIds = pidComplete[3]
							.split("&");
					String pidAssigningAuthorityUniversalId = pidAssigningAuthorityUniversalIds[1];

					// LastName and GivenName
					String[] patientName = hl7msgs[i + 5].split("\\^");
					String lastName = patientName[1];
					String givenName = patientName[0];

					// Date of Birth
					String dateOfBirth = hl7msgs[i + 7];
					Date dateDOB = stringToDate(dateOfBirth);

					// Sex
					String sex = hl7msgs[i + 8];

					// Address
					String adress = hl7msgs[i + 11];
					PatientAddress anAddress = new PatientAddress();
					String[] addressElements = adress.split("\\^");
					if (addressElements.length > 0)
						anAddress.setStreetAddress(addressElements[0]);
					if (addressElements.length > 1)
						anAddress.setOtherDesignation(addressElements[1]);
					if (addressElements.length > 2)
						anAddress.setCity(addressElements[2]);
					if (addressElements.length > 3)
						anAddress.setStateOrProvince(addressElements[3]);
					if (addressElements.length > 4)
						anAddress.setZipOrPostalCode(addressElements[4]);
					if (addressElements.length > 5)
						anAddress.setCountry(addressElements[5]);
					if (addressElements.length > 6)
						anAddress.setAddressType(addressElements[6]);
					if (addressElements.length > 7)
						anAddress
								.setOtherGeographicDesignation(addressElements[7]);
					if (addressElements.length > 8)
						anAddress.setCountryCode(addressElements[8]);
					if (addressElements.length > 9)
						anAddress.setCensusTract(addressElements[9]);
					if (addressElements.length > 10)
						anAddress
								.setAddressRepresentationCode(addressElements[10]);
					if (addressElements.length > 11)
						anAddress.setAddressValidityRange(addressElements[11]);
					if (addressElements.length > 12)
						anAddress.setEffectiveDate(addressElements[12]);
					if (addressElements.length > 13)
						anAddress.setExpirationDate(addressElements[13]);

					// new Patient
					Patient aPatient = new Patient(pid,
							pidAssigningAuthorityUniversalId, lastName,
							givenName, dateDOB, Gender.fromString(sex), anAddress);

					LOG.debug(aPatient.toString());
					patients.add(aPatient);
				}
			}
		} else {
			LOG.warn("Received NULL as value for 'hl7message' param. Bad things are happening!");
		}
		LOG.info("Converted PDQ-22 response contained " + patients.size()
				+ " patients.");

		return patients;
	}

	/**
	 * Checks if it's a patient segment (PID).
	 * 
	 * @param hl7msgs
	 *            the hl7 message
	 * @param i
	 *            the position
	 * @return true, if it's a patient segment (PID), else return false.
	 */
	private boolean isNewPatientSegment(String[] hl7msgs, int i) {
		// \r because a PID seqment has to start in a new line.
		if (hl7msgs[i].contains("\rPID") && (hl7msgs.length >= i + 5))
			return true;
		return false;
	}

	/**
	 * Converts a patient object to a HL7 PDQ message object. If any parameter
	 * is empty or null, there will no request parameter created for that one.
	 * 
	 * @param thePatient
	 *            the patient query information
	 * @return HL7 PDQ request message according to patient parameter.
	 */
	public String convertObjectToMessage(Patient thePatient) {
		return pdqMessageBuilder.buildPdqRequest(thePatient.getPatientID(),
				thePatient.getPatientIDAssigningAuthorityUniversalId(),
				thePatient.getGivenName(), thePatient.getLastName(),
				thePatient.getBirthdate());
	}

}
