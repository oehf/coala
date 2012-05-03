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
package de.hhn.mi.coala.xds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultExchange;
import org.apache.commons.lang.StringUtils;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.AssigningAuthority;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Association;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.AssociationLabel;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.AssociationType;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Author;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.AvailabilityStatus;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Code;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Document;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.DocumentEntry;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Identifiable;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.LocalizedString;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.SubmissionSet;
import org.openehealth.ipf.commons.ihe.xds.core.requests.ProvideAndRegisterDocumentSet;
import org.openehealth.ipf.commons.ihe.xds.core.requests.QueryRegistry;
import org.openehealth.ipf.commons.ihe.xds.core.requests.RetrieveDocument;
import org.openehealth.ipf.commons.ihe.xds.core.requests.RetrieveDocumentSet;
import org.openehealth.ipf.commons.ihe.xds.core.requests.query.FindDocumentsQuery;
import org.openehealth.ipf.commons.ihe.xds.core.responses.QueryResponse;
import org.openehealth.ipf.commons.ihe.xds.core.responses.RetrievedDocument;
import org.openehealth.ipf.commons.ihe.xds.core.responses.RetrievedDocumentSet;
import org.openehealth.ipf.commons.ihe.xds.core.stub.ebrs30.rs.RegistryError;
import org.openehealth.ipf.commons.ihe.xds.core.stub.ebrs30.rs.RegistryResponseType;
import org.openehealth.ipf.platform.camel.ihe.xds.XdsCamelValidators;
import org.slf4j.Logger;

import de.hhn.mi.coala.exception.XDSConfigurationErrorException;
import de.hhn.mi.coala.exception.XDSConsentConversionException;
import de.hhn.mi.coala.exception.XDSRequestFailedException;
import de.hhn.mi.coala.util.PXSDateConverter;

/**
 * This class provides the basic method to communicate with the XDS endpoint. In
 * our case a ICW PXS is used (http://www.icw-global.com).
 * 
 * @author siekmann, wkais
 * 
 */
public class XDSTransactorImpl implements XDSTransactor {

	private static final Logger LOG = org.slf4j.LoggerFactory
			.getLogger(XDSTransactorImpl.class);

	private static final String XDS_ITI43_ENDPOINT_BEGINNING = "xds-iti43://";
	private static final String XDS_ITI41_ENDPOINT_BEGINNING = "xds-iti41://";
	private static final String XDS_ITI18_ENDPOINT_BEGINNING = "xds-iti18://";
	private String encoding;
	private String xdsIti18endpoint;
	private String xdsIti41Endpoint;
	private String xdsIti43Endpoint;

	private String oidAssigningAuthority;

	private static int submissionSetCounter;
	private static int associationCounter;

	private ProducerTemplate producerTemplate;
	private CamelContext camelContext;
	private PXSDateConverter pxsDateConverter;

	private XDSConfigurationImpl xdsConfiguration;

	/*
	 * Little helping method for checking the XDSTransactorImpl configuration
	 */
	private void checkAndSetXDSConfiguration(XDSConfigurationImpl configuration)
			throws IllegalArgumentException {
		// check ITI-18 endpoint
		if ((configuration.getXdsIti18endpoint() == null)
				|| (configuration.getXdsIti18endpoint().trim().equals(""))
				|| (!configuration.getXdsIti18endpoint().trim()
						.startsWith(XDS_ITI18_ENDPOINT_BEGINNING))) {
			LOG.error("No or wrong XDS-ITI18 endpoint specified!");
			throw new IllegalArgumentException(
					"No or wrong XDS-ITI18 endpoint specified! Contact the developers for fixing the problem!");
		}
		xdsIti18endpoint = configuration.getXdsIti18endpoint();

		// check ITI-41 endpoint
		if ((configuration.getXdsIti41endpoint() == null)
				|| (configuration.getXdsIti41endpoint().trim().equals(""))
				|| (!configuration.getXdsIti41endpoint().trim()
						.startsWith(XDS_ITI41_ENDPOINT_BEGINNING))) {
			LOG.error("No or wrong XDS-ITI41 endpoint specified!");
			throw new IllegalArgumentException(
					"No or wrong XDS-ITI41 endpoint specified! Contact the developers for fixing the problem!");
		}
		xdsIti41Endpoint = configuration.getXdsIti41endpoint();

		// check ITI-43 endpoint
		if ((configuration.getXdsIti43endpoint() == null)
				|| (configuration.getXdsIti43endpoint().trim().equals(""))
				|| (!configuration.getXdsIti43endpoint().trim()
						.startsWith(XDS_ITI43_ENDPOINT_BEGINNING))) {
			LOG.error("No or wrong XDS-ITI43 endpoint specified!");
			throw new IllegalArgumentException(
					"No or wrong XDS-ITI43 endpoint specified! Contact the developers for fixing the problem!");
		}
		xdsIti43Endpoint = configuration.getXdsIti43endpoint();

		// check assigning authority OID
		if ((configuration.getAssigningAuthorityOID() == null)
				|| (configuration.getAssigningAuthorityOID().trim().equals(""))) {
			LOG.error("No assigning authority was specified.");
			throw new IllegalArgumentException(
					"No assigning authority was specified! Contact the developers for fixing the problem!");
		}
		oidAssigningAuthority = configuration.getAssigningAuthorityOID();
	}

	/**
	 * This method provides the basic communication with the XDS endpoint to
	 * receive all available with the given <code>availibilityStati</code>
	 * documents for a patient with the given <code>mpiPID</code>. The response
	 * is a list of DocumentEntries.
	 * 
	 * @param pid
	 *            the patient ID to use for the document search <br />
	 *            mpiPID must not be null or empty and mpiPID has to be numeric
	 * @param availabilityStati
	 *            Possible stati are: AvailabilityStatus.APPROVED,
	 *            AvailabilityStatus.SUBMITTED, AvailabilityStatus.DEPRECATED
	 * @return List of DocumentEntries that contains all consent documents for
	 *         the patient with the id <code>mpiPID</code>
	 * @throws XDSRequestFailedException
	 *             Is thrown if invalid parameters were given, the consent
	 *             format code is not configured or the request could not be
	 *             validated
	 */
	public List<DocumentEntry> getConsentDocumentList(String pid,
			List<AvailabilityStatus> availabilityStati)
			throws XDSRequestFailedException {
		// check parameters
		checkParametersGetConsentList(pid, availabilityStati);
		// prepare request
		Exchange requestExchange = prepareConsentListRequest(pid,
				availabilityStati);
		// send request
		Exchange responseExchange = producerTemplate.send(xdsIti18endpoint,
				requestExchange);

		// process response
		if (responseExchange != null && responseExchange.getOut() != null
				&& responseExchange.getOut().getBody() != null) {
			QueryResponse resp = responseExchange.getOut().getBody(
					QueryResponse.class);
			if (resp != null) {
				return filterConsentDocuments(resp.getDocumentEntries());
			}
		} else {
			throw new XDSRequestFailedException(
					"Received response for consent document list could not be processed.");
		}
		return Collections.emptyList();
	}

	/*
	 * Little helping method to create a Exchange-object for the IT-18 request
	 */
	private Exchange prepareConsentListRequest(String pid,
			List<AvailabilityStatus> availabilityStati)
			throws XDSRequestFailedException {
		Exchange requestExchange = new DefaultExchange(camelContext);

		FindDocumentsQuery query = new FindDocumentsQuery();
		Identifiable pat = new Identifiable(pid, new AssigningAuthority(
				oidAssigningAuthority));
		query.setPatientId(pat);

		// setting status of documents as given by parameter (method caller)
		query.setStatus(availabilityStati);

		// Prepare request
		QueryRegistry queryRegistry = new QueryRegistry(query);
		queryRegistry.setReturnLeafObjects(true);
		requestExchange.getIn().setBody(queryRegistry);

		try {
			XdsCamelValidators.iti18RequestValidator().process(requestExchange);
			LOG.info("Validating ITI18 request successful!");
		} catch (Exception e) {
			LOG.error("Validating an outgoing XDS-iti18 request failed: "
					+ e.getMessage());
			throw new XDSRequestFailedException(
					"Validating an outgoing XDS-iti18 request failed.", e);
		}
		return requestExchange;
	}

	/*
	 * Little helping method to check given parameters to retrieve consent
	 * document list
	 */
	private void checkParametersGetConsentList(String pid,
			List<AvailabilityStatus> availabilityStati)
			throws XDSRequestFailedException {
		if (pid == null) {
			LOG.error("Parameter 'pid' must not be null, but it was!!");
			throw new XDSRequestFailedException(
					"Parameter 'pid' must not be null, but it was!!");
		}
		if (pid.trim().isEmpty()) {
			LOG.error("Parameter 'pid' must not be an empty String, but it was!!");
			throw new XDSRequestFailedException(
					"Parameter 'mpiPID' must not be an empty String, but it was!!");
		}
		if (!StringUtils.isNumeric(pid)) {
			LOG.error("Parameter 'pid' must only contain numbers, but it did not!!");
			throw new XDSRequestFailedException(
					"Parameter 'pid' must only contain numbers, but it did not!!");
		}

		if (availabilityStati == null || availabilityStati.size() == 0) {
			LOG.error("Parameter 'availabilityStati' must contain at least one valid status.");
			throw new XDSRequestFailedException(
					"Parameter 'availabilityStati' must contain at least one valid status.");
		}
	}

	/*
	 * Little helper method to filter for Consent Documents by "hand". At the
	 * moment it seems as if it is not possible to filter over IPF / PXS
	 */
	private List<DocumentEntry> filterConsentDocuments(
			List<DocumentEntry> documentEntries) {
		List<DocumentEntry> filteredDocuments = new ArrayList<DocumentEntry>();

		for (DocumentEntry dE : documentEntries) {
			if (dE.getFormatCode()
					.getCode()
					.equalsIgnoreCase(
							xdsConfiguration.getConsentFormatCodeCode())) {
				filteredDocuments.add(dE);
			}
		}
		LOG.debug("Filtering consent documents finished! Found "
				+ filteredDocuments.size() + "consents for request!");
		return filteredDocuments;
	}

	/**
	 * This method prepares, sends a XDS request, processes the response and
	 * retrieve a documentSet
	 * 
	 * @param documentEntry
	 *            The DocumentEntry the documents should be retrieved for Must
	 *            not be null.
	 * @return Returns a list with the Documents for the given DocumentEntry
	 * @throws XDSRequestFailedException
	 *             Thrown if any problems occured processing the XDS request.
	 */
	public List<RetrievedDocument> retrieveDocumentSet(
			DocumentEntry documentEntry) throws XDSRequestFailedException {
		checkParametersRetrieveDocumentSet(documentEntry);

		// prepare request
		Exchange requestExchange = prepareRetrieveDocumentSet(documentEntry);
		// send request
		Exchange responseExchange = producerTemplate.send(xdsIti43Endpoint,
				requestExchange);

		// process response
		if (responseExchange != null && responseExchange.getOut() != null
				&& responseExchange.getOut().getBody() != null) {
			RetrievedDocumentSet resp = responseExchange.getOut().getBody(
					RetrievedDocumentSet.class);
			if (resp != null) {
				LOG.info("Received response for ITI43 request. "
						+ resp.getDocuments().size() + " documents found!");
				return resp.getDocuments();
			}
		} else {
			throw new XDSRequestFailedException(
					"Received response for document set could not be processed.");
		}
		return Collections.emptyList();
	}

	/*
	 * Little helping method to create an Exchange-object to retrieve the
	 * consent documents for the given DocumentEntry
	 */
	private Exchange prepareRetrieveDocumentSet(DocumentEntry documentEntry)
			throws XDSRequestFailedException {
		Exchange requestExchange = new DefaultExchange(camelContext);

		RetrieveDocumentSet queryRegistry = new RetrieveDocumentSet();
		queryRegistry.getDocuments().add(
				new RetrieveDocument(documentEntry.getRepositoryUniqueId(),
						documentEntry.getUniqueId(), documentEntry
								.getHomeCommunityId()));

		requestExchange.getIn().setBody(queryRegistry);

		try {
			XdsCamelValidators.iti43RequestValidator().process(requestExchange);
			LOG.info("Validating ITI43 request successful!");
		} catch (Exception e) {
			LOG.error("Validating an outgoing XDS-iti43 request failed: "
					+ e.getMessage());
			throw new XDSRequestFailedException(
					"Validating an outgoing XDS-iti43 request failed.", e);
		}
		return requestExchange;
	}

	/*
	 * Little helping method to check parameters for document set retrieval
	 */
	private void checkParametersRetrieveDocumentSet(DocumentEntry documentEntry)
			throws XDSRequestFailedException {
		if ((xdsIti43Endpoint == null)
				|| (xdsIti43Endpoint.trim().equals(""))
				|| (!xdsIti43Endpoint.trim().startsWith(
						XDS_ITI43_ENDPOINT_BEGINNING))) {
			LOG.error("No or wrong XDS-ITI43 endpoint specified! Can not process getting consent document list");
			throw new XDSRequestFailedException(
					"No or wrong XDS-ITI43 endpoint specified! Contact the developers for fixing the problem!");
		}

		if (documentEntry == null) {
			LOG.error("Parameter 'documentEntry' must not be null.");
			throw new XDSRequestFailedException(
					"Parameter 'documentEntry' must not be null.");
		}
	}

	/**
	 * This method provides and registers the documentSet.
	 * 
	 * @param document
	 *            The document object that contains the new CDA consent data. It
	 *            has to contain at least one author in the documentEntry and
	 *            the patientIdentifiable has to be set. This parameter must not
	 *            be <code>null</code>
	 * @return The uniqueID the new consent can be found with or
	 *         <code>null</code> if something went wrong
	 * @throws XDSRequestFailedException
	 *             If wrong parameters were given or if an endpoint is empty
	 *             (wrong constructor call by programmer!!!)
	 * @throws XDSConsentConversionException
	 *             If an old consent document could not be parsed to check
	 *             validity of the consent
	 * @throws XDSConfigurationErrorException
	 *             If the configuration file does not contain required keys
	 */
	public String provideAndRegisterNewConsent(Document document,
			boolean replaceOld) throws XDSRequestFailedException,
			XDSConsentConversionException, XDSConfigurationErrorException {

		checkParametersNewConsent(document);
		// reset counter
		submissionSetCounter = 1;
		associationCounter = 1;

		Exchange requestExchange = createNewConsentRequest(document, replaceOld);
		// send request
		Exchange responseExchange = producerTemplate.send(xdsIti41Endpoint,
				requestExchange);

		// process response
		Message resp = responseExchange.getOut();
		RegistryResponseType r;
		if (resp != null)
			r = (RegistryResponseType) resp.getBody();
		else {
			LOG.error("RegistryResponse body was null!");
			throw new XDSRequestFailedException(
					"RegistryResponse body was NULL!");
		}
		// Check response and return uniqueID of request
		return checkRegistryResponseAndGetConsentUniqueId(responseExchange,
				resp, r);
	}

	/*
	 * Little helping method for checking response of consent registration and
	 * extracting the uniqueID
	 */
	private String checkRegistryResponseAndGetConsentUniqueId(
			Exchange responseExchange, Message resp, RegistryResponseType r)
			throws XDSRequestFailedException {
		if (r.getStatus().endsWith("Success")) {
			if (resp.getExchange() != null
					&& resp.getExchange().getIn() != null
					&& resp.getExchange().getIn().getBody() != null) {
				ProvideAndRegisterDocumentSet docSet = (ProvideAndRegisterDocumentSet) resp
						.getExchange().getIn().getBody();
				if (docSet.getDocuments().size() > 0
						&& docSet.getDocuments().get(0).getDocumentEntry() != null) {
					String uniquRetID = docSet.getDocuments().get(0)
							.getDocumentEntry().getUniqueId();
					return uniquRetID;
				} else {
					LOG.error("Response body of ProvideAndRegisterDocumentSet corrupted!");
					throw new XDSRequestFailedException(
							"Response body of ProvideAndRegisterDocumentSet corrupted!");
				}

			} else {
				LOG.error("Response body of ProvideAndRegisterDocumentSet request corrupted!");
				throw new XDSRequestFailedException(
						"Response body of ProvideAndRegisterDocumentSet request corrupted!");
			}
		} else if (responseExchange.getException() != null) {
			LOG.error("Error ITI-41: "
					+ responseExchange.getException().getMessage());
			throw new XDSRequestFailedException(
					"An error occured during proccessing of ITI-41 request. ",
					responseExchange.getException());
		} else if (r.getRegistryErrorList() != null) {
			LOG.error("Registry error(s) occured while processing ITI-41 request:");
			String errorText = "";
			for (RegistryError e : r.getRegistryErrorList().getRegistryError()) {
				LOG.error("Registry error: " + e.getCodeContext());
				errorText += e.getCodeContext() + "\n";
			}
			throw new XDSRequestFailedException(
					"An error occured during proccessing of ITI-41 request. "
							+ errorText);
		}
		return null;
	}

	/*
	 * Little helping method to create a new Exchange-object for registration of
	 * a new consent
	 */
	private Exchange createNewConsentRequest(Document document,
			boolean replaceOld) throws XDSConfigurationErrorException {
		DocumentEntry newConsentDocEntry = document.getDocumentEntry();
		Author author = newConsentDocEntry.getAuthor();
		Identifiable regFor = newConsentDocEntry.getPatientId();
		SubmissionSet submissionSet = createSubmissionSet(regFor, author);
		Association docAssociation = createAssociationDocEntryToSubmissionSet(
				submissionSet, newConsentDocEntry);
		// The original consent is part of the request
		docAssociation.setLabel(AssociationLabel.ORIGINAL);

		// Create request
		ProvideAndRegisterDocumentSet request = new ProvideAndRegisterDocumentSet();
		request.setSubmissionSet(submissionSet);
		request.getDocuments().add(document);
		request.getAssociations().add(docAssociation);
		// Replace old consents?
		if (replaceOld) {
			List<Association> rplcAssociations = createConsentReplaceAssociations(
					submissionSet, newConsentDocEntry);
			request.getAssociations().addAll(rplcAssociations);
		}

		Exchange requestExchange = new DefaultExchange(camelContext);

		requestExchange.getIn().setBody(request);

		// Request validation
		try {
			XdsCamelValidators.iti41RequestValidator().process(requestExchange);
			LOG.info("Validating ITI41 request successful!");
		} catch (Exception e) {
			LOG.error("Validating an outgoing XDS-iti41 request failed: "
					+ e.getMessage());
			throw new XDSRequestFailedException(
					"Validating an outgoing XDS-iti41 request failed", e);
		}
		return requestExchange;
	}

	/*
	 * Little helping method for checking parameters of
	 * provideAndRegisterNewConsent(document, replaceOld)
	 */
	private void checkParametersNewConsent(Document document)
			throws XDSConfigurationErrorException {
		if ((xdsIti41Endpoint == null)
				|| (xdsIti41Endpoint.trim().equals(""))
				|| (!xdsIti41Endpoint.trim().startsWith(
						XDS_ITI41_ENDPOINT_BEGINNING))) {
			LOG.error("No or wrong XDS-ITI41 endpoint specified! Can not process getting consent document list");
			throw new XDSConfigurationErrorException(
					"No or wrong XDS-ITI41 endpoint specified! Contact the developers for fixing the problem!");
		}
		if (document == null) {
			LOG.error("Parameter 'document' must not be null, but it was!!");
			throw new XDSConfigurationErrorException(
					"Parameter 'document' must not be null, but it was!!");
		}
		if (document.getDocumentEntry() == null) {
			LOG.error("DocumentEntry of document-object is empty!!");
			throw new XDSConfigurationErrorException(
					"DocumentEntry of document-object is empty!!");
		}
	}

	/*
	 * Little helping method for creation of a new submissionset to register new
	 * consent document
	 */
	private SubmissionSet createSubmissionSet(Identifiable patientID,
			Author author) throws XDSConfigurationErrorException {

		try {
			SubmissionSet submissionSet = new SubmissionSet();
			submissionSet.getAuthors().add(author);
			submissionSet.setAvailabilityStatus(AvailabilityStatus.APPROVED);

			String langCode = xdsConfiguration.getConsentLanguageCode();

			Code code = new Code(
					xdsConfiguration.getConsentDocumentTypeCodeCode(),
					new LocalizedString(xdsConfiguration
							.getConsentDocumentTypeCodeDisplayname(), langCode,
							encoding),
					xdsConfiguration.getConsentDocumentTypeCodeSchemename());

			submissionSet.setContentTypeCode(code);
			submissionSet.setEntryUuid("submissionSet" + submissionSetCounter);
			submissionSetCounter++;
			submissionSet.setPatientId(patientID);
			submissionSet.setSourceId(xdsConfiguration.getDocumentsourceOid());
			// Actual time as creation time
			submissionSet.setSubmissionTime(pxsDateConverter
					.DateToString(new Date()));
			submissionSet.setTitle(new LocalizedString(xdsConfiguration
					.getDefaultSubmissionSetTitle(), langCode, encoding));
			// Generate unique ID: subbase + patid + date
			String uniqueSubId = xdsConfiguration
					.getSubmissionSetBaseUniqueId();
			uniqueSubId += "." + patientID.getId() + "."
					+ pxsDateConverter.DateToString(new Date());
			submissionSet.setUniqueId(uniqueSubId);
			return submissionSet;
		} catch (MissingResourceException e) {
			throw new XDSConfigurationErrorException(
					"Configuration file seems to be corrupted. Check for missing properties.",
					e);
		} catch (ClassCastException e2) {
			throw new XDSConfigurationErrorException(
					"Configuration file seems to be corrupted. Check for missing properties.",
					e2);
		}
	}

	/*
	 * Little helping method to create new association between a DocumentEntry
	 * and a SubmissionSet
	 */
	private Association createAssociationDocEntryToSubmissionSet(
			SubmissionSet src, DocumentEntry dest) {
		if (src != null && dest != null && dest.getEntryUuid() != null) {
			Association docAssociation = new Association();
			docAssociation.setAssociationType(AssociationType.HAS_MEMBER);
			docAssociation.setSourceUuid(src.getEntryUuid());
			docAssociation.setTargetUuid(dest.getEntryUuid());
			docAssociation.setLabel(AssociationLabel.REFERENCE);
			docAssociation.setEntryUuid("consentAssociation"
					+ associationCounter);
			associationCounter++;
			return docAssociation;
		} else
			return null;
	}

	/*
	 * Little helping method to create all replace association for a new consent
	 * document
	 */
	private List<Association> createConsentReplaceAssociations(
			SubmissionSet src, DocumentEntry newConsent)
			throws XDSConsentConversionException, XDSRequestFailedException {
		if (src != null && newConsent != null
				&& newConsent.getPatientId() != null
				&& newConsent.getPatientId().getId() != null
				&& newConsent.getEntryUuid() != null
				&& src.getEntryUuid() != null) {
			// Get all active consents
			String patId = newConsent.getPatientId().getId();
			String newConsentEntryUuid = newConsent.getEntryUuid();
			ArrayList<AvailabilityStatus> availableStatus = new ArrayList<AvailabilityStatus>();
			availableStatus.add(AvailabilityStatus.APPROVED);
			List<DocumentEntry> oldConsents = getConsentDocumentList(patId,
					availableStatus);
			// findActiveConsents
			List<DocumentEntry> oldActiveConsents = findActiveConsents(oldConsents);
			// create RPLC associations
			ArrayList<Association> rplcAssociations = createReplaceAssociations(
					newConsentEntryUuid, oldActiveConsents);
			return rplcAssociations;
		} else
			throw new XDSRequestFailedException(
					"An error occured during creation of consent replacement associations. Invalid parameters!");
	}

	/*
	 * Little helping method for createConsentReplaceAssociations( SubmissionSet
	 * src, DocumentEntry newConsent)
	 */
	private ArrayList<Association> createReplaceAssociations(
			String newConsentEntryUuid, List<DocumentEntry> oldActiveConsents) {
		ArrayList<Association> rplcAssociations = new ArrayList<Association>();
		if (oldActiveConsents.size() > 0) {
			for (DocumentEntry doc : oldActiveConsents) {
				// create RPLC association for old consent
				Association rplcAssociation = new Association();
				rplcAssociation.setAssociationType(AssociationType.REPLACE);
				rplcAssociation.setSourceUuid(newConsentEntryUuid);
				rplcAssociation.setTargetUuid(doc.getEntryUuid());
				rplcAssociations.add(rplcAssociation);
			}
		}
		return rplcAssociations;
	}

	/*
	 * Little helping method for extracting active consent documents
	 */
	private List<DocumentEntry> findActiveConsents(
			List<DocumentEntry> oldConsents)
			throws XDSConsentConversionException {
		List<DocumentEntry> oldActiveConsents = new ArrayList<DocumentEntry>();
		for (DocumentEntry doc : oldConsents) {
			Date now = new Date();
			Date validFrom;
			Date validUntil;
			try {
				validFrom = pxsDateConverter.stringToDate(doc
						.getServiceStartTime());
				validUntil = pxsDateConverter.stringToDate(doc
						.getServiceStopTime());
				// If exception is thrown before hand, the consent is invalid
				if ((validFrom.before(now)) && (now.before(validUntil)))
					oldActiveConsents.add(doc);
			} catch (Exception e) {
				// just don't add it, it's invalid anyway
				// this catch block is intentionally left blank.
			}

		}
		return oldActiveConsents;
	}

	public void setProducerTemplate(ProducerTemplate producerTemplate) {
		this.producerTemplate = producerTemplate;
	}

	public void setCamelContext(CamelContext camelContext) {
		this.camelContext = camelContext;
	}

	public void setXdsConfiguration(XDSConfigurationImpl xdsConfiguration) {
		this.xdsConfiguration = xdsConfiguration;
		checkAndSetXDSConfiguration(xdsConfiguration);
		this.encoding = xdsConfiguration.getConsentEncoding();
	}

	public void setPxsDateConverter(PXSDateConverter pxsDateConverter) {
		this.pxsDateConverter = pxsDateConverter;
	}

}
