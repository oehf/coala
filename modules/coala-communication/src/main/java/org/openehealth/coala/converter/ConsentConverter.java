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
package org.openehealth.coala.converter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;

import org.openehealth.coala.domain.CoalaAuthor;
import org.openehealth.coala.domain.Patient;
import org.openehealth.coala.domain.PatientConsent;
import org.openehealth.coala.domain.PatientConsentPolicy;
import org.openehealth.coala.util.PXSDateConverter;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Author;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.AvailabilityStatus;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.DocumentEntry;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Person;
import org.openehealth.ipf.commons.ihe.xds.core.responses.RetrievedDocument;

/**
 * This class handles domain conversion from XDS to Coala and back.
 * 
 * @author kmaerz
 */
public class ConsentConverter {

	private PXSDateConverter pxsDateConverter;

	/**
	 * Converts the given XDS-Data to a Patient Consent of the Coala Domain
	 * 
	 * @param metadataEntry
	 *            The meta data entry retrieved by the ITI-18 request
	 * @param document
	 *            The document, retrieved by the ITI-43 request
	 * @param patient
	 *            the patient whom this consent belongs to
	 * @return a PatientConsent with the corresponding data
	 */
	public PatientConsent transformToCoalaConsent(DocumentEntry metadataEntry,
			RetrievedDocument document, Patient patient) {

		// test for Obsoleteness
		boolean obsolete = false;
		if (!metadataEntry.getAvailabilityStatus().equals(
				AvailabilityStatus.APPROVED)) {
			obsolete = true;
		}

		
		//Retrieve Author
		String title =  null;
		String given =  null;
		String family = null;
		
		Author author = metadataEntry.getAuthor();
		if (author != null) {
			Person authorPerson = author.getAuthorPerson();
			if (authorPerson != null) {
				if (authorPerson.getName() != null){
					title = authorPerson.getName().getPrefix();
					given = authorPerson.getName().getGivenName();
					family = authorPerson.getName().getFamilyName();
				}
			}
		}
		
		CoalaAuthor cAuthor = createAuthor(title, given, family);

		
		// retrieve relevant dates
		String creationTime = metadataEntry.getCreationTime();
		String validFrom = metadataEntry.getServiceStartTime();
		String validUntil = metadataEntry.getServiceStopTime();
		
		Date creationDate;
		Date validFromDate;
		Date validUntilDate;
		Boolean invalid = false;
		try {
			creationDate = pxsDateConverter.stringToDate(creationTime);
			validFromDate = pxsDateConverter.stringToDate(validFrom);
			validUntilDate = pxsDateConverter.stringToDate(validUntil);
		} catch (RuntimeException rt) {
			creationDate = pxsDateConverter.stringToDate("19700101");
			validFromDate = pxsDateConverter.stringToDate("19700101");
			validUntilDate = pxsDateConverter.stringToDate("19700101");
			invalid = true;
		}

		if (metadataEntry.getEventCodeList().size() != 1)
			throw new RuntimeException(
					"Fatal Error During conversion of Policy TypeCodes: Assuming a list of size 1, but list was of size "
							+ metadataEntry.getEventCodeList().size());
		String code = metadataEntry.getEventCodeList().get(0).getCode();
		PatientConsentPolicy policy = PatientConsentPolicy.getPolicyType(code);

		BufferedReader reader;
		
		String xml = "";
		String buffer = null;

		try {
			// Try to read out the stream

			// See if source can be established and has bytes
			reader = new BufferedReader(new InputStreamReader(document
					.getDataHandler().getInputStream()));

			buffer = reader.readLine();
			while (buffer != null) {
				xml += buffer + "\n";
				buffer = reader.readLine();
			}
		

		} catch (Exception e) {
			// consent will not contain a cda
			xml = "";
		}

		PatientConsent consent = new PatientConsent(validFromDate, validUntilDate, policy,
				patient, obsolete, cAuthor, creationDate, xml);
		
		if (invalid) consent.invalidate();
		return consent;
	}


	public void setPxsDateConverter(PXSDateConverter pxsDateConverter) {
		this.pxsDateConverter = pxsDateConverter;
	}


	/**
	 * Creates a new CoalaAuthor
	 * @param title
	 * @param given
	 * @param family
	 * @return
	 */
	private static CoalaAuthor createAuthor(String title, String given, String family) {
	

		if ((title == null) || (title.equals("")))
			title = "";
		if ((given == null) || (given.equals("")))
			given = "unknown";
		if ((family == null) || (family.equals("")))
			family = "unknown";

		CoalaAuthor result = new CoalaAuthor(title, given, family);
		return result;
	}

}
