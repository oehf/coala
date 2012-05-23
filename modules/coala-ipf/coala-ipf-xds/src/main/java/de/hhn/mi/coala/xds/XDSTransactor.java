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

import java.util.List;

import org.openehealth.ipf.commons.ihe.xds.core.metadata.AvailabilityStatus;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Document;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.DocumentEntry;
import org.openehealth.ipf.commons.ihe.xds.core.responses.RetrievedDocument;

import org.openehealth.coala.exception.XDSConfigurationErrorException;
import org.openehealth.coala.exception.XDSConsentConversionException;
import org.openehealth.coala.exception.XDSRequestFailedException;

/**
 * This interface provides basic operations to handle XDS {@link Document} and
 * related {@link DocumentEntry}s.
 * 
 * @author siekmann
 * 
 */
public interface XDSTransactor {
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
			throws XDSRequestFailedException;

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
			DocumentEntry documentEntry) throws XDSRequestFailedException;

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
			XDSConsentConversionException, XDSConfigurationErrorException;
}
