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
package org.openehealth.coala.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;

import org.openehealth.coala.builder.DocumentEntryBuilder;
import org.openehealth.coala.communication.PdqMessageBuilder;
import org.openehealth.coala.converter.ConsentConverter;
import org.openehealth.coala.converter.PdqHL7Converter;
import org.openehealth.coala.converter.PdqHL7ConverterImpl;
import org.openehealth.coala.domain.CoalaAuthor;
import org.openehealth.coala.domain.ConsentSortParameter;
import org.openehealth.coala.domain.FindPatientConsentResult;
import org.openehealth.coala.domain.FindPatientQuery;
import org.openehealth.coala.domain.FindPatientResult;
import org.openehealth.coala.domain.Patient;
import org.openehealth.coala.domain.PatientConsent;
import org.openehealth.coala.domain.PatientConsentPolicy;
import org.openehealth.coala.domain.PatientSortParameter;
import org.openehealth.coala.exception.PDQRequestFailedException;
import org.openehealth.coala.exception.ServiceParameterException;
import org.openehealth.coala.exception.XDSRequestFailedException;
import org.openehealth.coala.interfacing.CDATransformationService;
import org.openehealth.coala.interfacing.ConsentCreationService;
import org.openehealth.coala.interfacing.PatientService;
import org.openehealth.coala.pdq.PDQGate;
import org.openehealth.coala.util.CdaDataSourceImpl;
import org.openehealth.coala.xds.XDSGate;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.AvailabilityStatus;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Document;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.DocumentEntry;
import org.openehealth.ipf.commons.ihe.xds.core.responses.RetrievedDocument;
import org.slf4j.Logger;

/**
 * Provides an implementation for CoALA's communication layer to work with ICW
 * AG's PXS communication server.
 * 
 * @author kmaerz, mwiesner
 * 
 * @FIXME This class apparently does more than one thing. Its responsibility is not clearly defined.
 * 		Code smell! A refactoring is needed here.
 */
public class PXSQueryServiceImpl implements PatientService, ConsentCreationService {
	
	private static final Logger LOG = org.slf4j.LoggerFactory
		.getLogger(PXSQueryServiceImpl.class);
	
	/*
	 * Provides links into the IPF layer
	 */
	private PDQGate pdqGate;
	private XDSGate xdsGate;
	private List<AvailabilityStatus> stati;

	private PdqHL7Converter pdqConverter;

	private CDATransformationService cdaTransformationService;
	private ConsentConverter consentConverter;
	private DocumentEntryBuilder documentEntryBuilder;
	private PdqMessageBuilder pdqMessageBuilder;
	

	public void setPdqMessageBuilder(PdqMessageBuilder pdqMessageBuilder) {
		this.pdqMessageBuilder = pdqMessageBuilder;
	}

	public void setDocumentEntryBuilder(DocumentEntryBuilder documentEntryBuilder) {
		this.documentEntryBuilder = documentEntryBuilder;
	}

	public void setConsentConverter(ConsentConverter consentConverter) {
		this.consentConverter = consentConverter;
	}

	public void setPdqConverter(PdqHL7Converter pdqConverter) {
		this.pdqConverter = pdqConverter;
	}

	/*
	 * This default constructor is needed for Spring bean instantiation don't
	 * remove me!
	 */
	public PXSQueryServiceImpl() {
		stati = new ArrayList<AvailabilityStatus>();
		stati.add(AvailabilityStatus.APPROVED);
		stati.add(AvailabilityStatus.SUBMITTED);
		stati.add(AvailabilityStatus.DEPRECATED);
		pdqConverter = new PdqHL7ConverterImpl();
	
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openehealth.coala.interfacing.QueryInterface#findPatients(org.openehealth.coala
	 * .domain.FindPatientQuery, org.openehealth.coala.domain.PatientSortParameter)
	 */
	@Override
	public FindPatientResult findPatients(FindPatientQuery query,
			PatientSortParameter sortBy) throws PDQRequestFailedException {

		if (query == null)
			throw new ServiceParameterException(
					"Tried to call findPatients, but did not provide an argument (query is null).");
		// Transform Query to String request to package for PDQ Layer
		String request = pdqMessageBuilder.buildPdqRequest(
				query.getPatientID(), query.getGivenName(),
				query.getLastName(), query.getBirthdate());
		String hl7response = pdqGate.requestPatients(request);
		FindPatientResult result = new FindPatientResult(query, sortBy);
		result.addAll(pdqConverter.convertPdqToPatients(hl7response));
		result.lock();
		return result;

	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openehealth.coala.interfacing.QueryInterface#getPatientConsents(de.hhn.
	 * mi.coala.domain.Patient, org.openehealth.coala.domain.ConsentSortParameter)
	 */
	@Override
	public FindPatientConsentResult getPatientConsents(Patient patient,
			ConsentSortParameter sortBy) throws XDSRequestFailedException {
		if (patient == null)
			throw new IllegalArgumentException("patient cannot be null.");

		// The code here may not be intuitively understandable due to it being
		// an interwoven sets of ITI 18 and 43 requests. First, a metadata set
		// of all consents is retrieved for a given patient using ITI-18. After
		// that, for each metadata set,we retrieve the actual document, and pass
		// it to the converter, which converts it to a PatientConsent of our
		// Coala-Domain.
		// Sorting happens automagically in the FindPatientConsentResult object.

		// 1) get List of documents
		List<DocumentEntry> metadataList = xdsGate.requestConsents(
				patient.getPatientID(), stati, sortBy);
		FindPatientConsentResult result = new FindPatientConsentResult(patient,
				sortBy);
		// 2) For each metadataSet, query appropriate Document
		for (DocumentEntry entry : metadataList) {

			List<RetrievedDocument> consentList = xdsGate
					.retrieveDocumentSet(entry);
			LOG.debug(consentList.get(0).getRequestData().toString());

			// Check if there is only exactly one object in the list.
			if (consentList.size() != 1)
				throw new RuntimeException(
						"Fatal Error during conversion of consent: Assuming a retrieved document list of size 1, but list was of size "
								+ consentList.size());

			// 3) convert data to PatientConsents
			PatientConsent consent = consentConverter.transformToCoalaConsent(
					entry, consentList.get(0), patient);

			result.addPatientConsent(consent);
		}

		result.lock();
		return result;
	}

	//TODO Javadoc @see...
	@Override
	public void createPatientConsent(Patient patient, Date validFrom,
			Date validUntil, PatientConsentPolicy policy, CoalaAuthor author)
			throws XDSRequestFailedException {
		
		//forward, indicate that no other consents be overwritten
		this.createConsent(patient, validFrom, validUntil, policy, author, false);

	}

	//TODO Javadoc @see...
	@Override
	public void replacePatientConsent(Patient patient, Date validFrom,
			Date validUntil, PatientConsentPolicy policy, CoalaAuthor author,
			PatientConsent oldConsent) throws XDSRequestFailedException {
		throw new UnsupportedOperationException(
				"This call is not yet supported. Lazy Programmers, get to work!");

	}

	//TODO Javadoc @see...
	@Override
	public void replaceAllPatientConsents(Patient patient, Date validFrom,
			Date validUntil, PatientConsentPolicy policy, CoalaAuthor author)
			throws XDSRequestFailedException {
		
		//forward, indicate that all other consents be overwritten
		this.createConsent(patient, validFrom, validUntil, policy, author, true);
		
	}
	
	/**
	 * Basic Method to create a consent
	 * @param patient Patient for consent
	 * @param validFrom validity duration start
	 * @param validUntil validity duration end
	 * @param policy the type of policy for this consent
	 * @param author the author of this consent
	 * @param replaceAllOther whether other consents should be replaced or not
	 * @throws XDSRequestFailedException if something goes wrong 
	 */
	private void createConsent(Patient patient, Date validFrom,
			Date validUntil, PatientConsentPolicy policy, CoalaAuthor author, boolean replaceAllOther)
			throws XDSRequestFailedException {
		
		// Basic checks
		if (patient == null)
			throw new IllegalArgumentException("Patient cannot be null.");
		if (validFrom == null)
			throw new IllegalArgumentException("startDate cannot be null.");
		if (validUntil == null)
			throw new IllegalArgumentException("endDate cannot be null.");
		if (policy == null)
			throw new IllegalArgumentException("policy cannot be null.");

		
		// Create metadata
		DocumentEntry entry = documentEntryBuilder.createDocumentEntry(author, patient, policy, validFrom, validUntil);
		
		//Create a cda
		String cda = cdaTransformationService.transformToValidPatientConsent(patient, policy, author, entry);
				
		
		// Create a DataHandler for the document
		DataHandler handler = new DataHandler(new CdaDataSourceImpl(
				cda));
		
		// Register the Consent
		xdsGate.registerNewConsent(new Document(entry, handler), replaceAllOther);
	}
	

	public void setPdqGate(PDQGate pdqGate) {
		this.pdqGate = pdqGate;
	}

	public void setXdsGate(XDSGate xdsGate) {
		this.xdsGate = xdsGate;
	}

	public void setCdaService(CDATransformationService cdaService) {
		this.cdaTransformationService = cdaService;
	}

}
