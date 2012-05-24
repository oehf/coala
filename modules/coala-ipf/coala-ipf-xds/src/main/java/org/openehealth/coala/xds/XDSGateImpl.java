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
package org.openehealth.coala.xds;

import java.util.List;

import org.openehealth.ipf.commons.ihe.xds.core.metadata.AvailabilityStatus;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Document;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.DocumentEntry;
import org.openehealth.ipf.commons.ihe.xds.core.responses.RetrievedDocument;
import org.slf4j.Logger;

import org.openehealth.coala.domain.ConsentSortParameter;
import org.openehealth.coala.exception.XDSConfigurationErrorException;
import org.openehealth.coala.exception.XDSConsentConversionException;
import org.openehealth.coala.exception.XDSRequestFailedException;

/**
 * This class implements {@link XDSGate} to provide the communication to a XDS
 * server via IHE-XDS.
 * 
 * @author kmaerz, siekmann
 */
public class XDSGateImpl implements XDSGate {

	private static final Logger LOG = org.slf4j.LoggerFactory
			.getLogger(XDSGateImpl.class);

	private XDSTransactor xdsTransactor;

	/**
	 * Creates a new XDSGate for XDS communication with a PXS.
	 * 
	 * @param transactor
	 *            The {@link XDSTransactor} to use in this implementation. It
	 *            must not be <code>null</code>
	 * @throws XDSConfigurationErrorException
	 *             Is thrown if the configuration object in
	 *             {@link XDSTransactor} is misconfigured
	 */
	public XDSGateImpl(XDSTransactor transactor)
			throws XDSConfigurationErrorException {
		this.xdsTransactor = transactor;
	}

	/**
	 * Returns a List of DocumentEntries that are consents for the patient with
	 * the <code>pid</code> and the <code>availibilityStati</code>.
	 * 
	 * @param pid
	 *            The patient ID consent documents should be searched for. This
	 *            parameter MUST NOT be <code>null</code>.
	 * @param availabilityStati
	 *            List of document stati that should be returned. Possible stati
	 *            are: AvailabilityStatus.APPROVED,
	 *            AvailabilityStatus.SUBMITTED, AvailabilityStatus.DEPRECATED <br />
	 *            This list must contain at least one entry.
	 * @return List of {@link DocumentEntry} that are consent documents.
	 * @throws XDSRequestFailedException
	 *             If any error occurred during processing of the request
	 */
	public List<DocumentEntry> requestConsents(String pid,
			List<AvailabilityStatus> availabilityStati,
			ConsentSortParameter sortby) throws XDSRequestFailedException {
		List<DocumentEntry> documents;
		try {
			documents = xdsTransactor.getConsentDocumentList(pid,
					availabilityStati);
			return documents;
		} catch (XDSRequestFailedException e) {
			throw e;
		} catch (Throwable e) {
			LOG.error(e.getLocalizedMessage(), e);
			throw new XDSRequestFailedException(
					"Communication with XDS failed. See log for details", e);
		}
	}

	/**
	 * Returns a list of RetrieveDocument that are associated with the given
	 * <code>documentEntry</code> or an empty List.
	 * 
	 * @param documentEntry
	 *            The document entry that should be processed.
	 * @return The list of associated documents
	 * @throws XDSRequestFailedException
	 *             Is thrown if anything went wrong during retrieving the
	 *             document set.
	 * @throws XDSConfigurationErrorException
	 *             Is thrown if the ITI-43 endpoint is misconfigured
	 */
	public List<RetrievedDocument> retrieveDocumentSet(
			DocumentEntry documentEntry) throws XDSRequestFailedException,
			XDSConfigurationErrorException {
		List<RetrievedDocument> documents;
		try {
			documents = xdsTransactor.retrieveDocumentSet(documentEntry);
			return documents;
		} catch (XDSRequestFailedException e) {
			throw e;
		} catch (Throwable e) {
			LOG.error(e.getLocalizedMessage(), e);
			throw new XDSRequestFailedException(
					"Communication with XDS failed. See log for details", e);
		}
	}

	/**
	 * This method registers a new consent document for the given patient
	 * 
	 * @param document
	 *            The document object that contains the new CDA consent data. It
	 *            has to contain at least one author in the documentEntry and
	 *            the patientIdentifiable has to be set. This parameter must not
	 *            be <code>null</code>.
	 * @param replaceActiveConsents
	 *            Indicates whether the new consent should replace all active
	 *            consents in repository or not.
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
	public String registerNewConsent(Document document,
			boolean replaceActiveConsents) throws XDSRequestFailedException,
			XDSConsentConversionException, XDSConfigurationErrorException {
		try {
			return xdsTransactor.provideAndRegisterNewConsent(document,
					replaceActiveConsents);
		} catch (XDSConfigurationErrorException e) {
			throw e;
		} catch (XDSRequestFailedException e) {
			throw e;
		} catch (Throwable e) {
			LOG.error(e.getLocalizedMessage(), e);
			throw new XDSRequestFailedException(
					"Communication with XDS failed. See log for details.", e);
		}
	}
}
