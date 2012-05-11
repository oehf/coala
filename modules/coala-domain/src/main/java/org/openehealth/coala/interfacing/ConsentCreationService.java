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
package org.openehealth.coala.interfacing;

import java.util.Date;

import org.openehealth.coala.domain.CoalaAuthor;
import org.openehealth.coala.domain.ConsentSortParameter;
import org.openehealth.coala.domain.FindPatientConsentResult;
import org.openehealth.coala.domain.Patient;
import org.openehealth.coala.domain.PatientConsent;
import org.openehealth.coala.domain.PatientConsentPolicy;
import org.openehealth.coala.exception.XDSRequestFailedException;

/**
 * This interface models the communication between assembly and communication
 * module in order handle the creation of new {@link PatientConsent} within a
 * XDS registry/repository server.
 * 
 * @author mwiesner, kmaerz
 * 
 */
public interface ConsentCreationService {

	/**
	 * Creates a new {@link PatientConsent} instance with the related
	 * {@link Patient} within an XDS registry/repository.
	 * 
	 * @param patient
	 *            The patient who should be associated with this Consent
	 * @param validFrom
	 *            The date that marks the start of the validity period
	 * @param validUntil
	 *            The date that marks the end of the validity period
	 * @param policy
	 *            The policy that defines the restrictions of this consent.
	 * @throws XDSRequestFailedException
	 *             Thrown if any problems occurred during validation or
	 *             processing of the related ITI-41/42 request.
	 */
	public void createPatientConsent(Patient patient, Date validFrom,
			Date validUntil, PatientConsentPolicy policy, CoalaAuthor author) throws XDSRequestFailedException;

	/**
	 * Creates a new {@link PatientConsent} instance with the related
	 * {@link Patient} within an XDS registry/repository. The specified old
	 * consent will be marked as obsolete.
	 * 
	 * @param patient
	 *            The patient who should be associated with this Consent
	 * @param validFrom
	 *            The date that marks the start of the validity period
	 * @param validUntil
	 *            The date that marks the end of the validity period
	 * @param policy
	 *            The policy that defines the restrictions of this consent.
	 * @param oldConsent
	 *            The consent that should be marked as obsolete in the process
	 * @throws XDSRequestFailedException
	 *             Thrown if any problems occurred during validation or
	 *             processing of the related ITI-41/42 request.
	 */
	public void replacePatientConsent(Patient patient, Date validFrom,
			Date validUntil, PatientConsentPolicy policy, CoalaAuthor author, PatientConsent oldConsent) throws XDSRequestFailedException;

	/**
	 * Creates a new {@link PatientConsent} instance with the related
	 * {@link Patient} within an XDS registry/repository. All other consents of
	 * this patient will be marked as obsolete.
	 * 
	 * @param patient
	 *            The patient who should be associated with this Consent
	 * @param validFrom
	 *            The date that marks the start of the validity period
	 * @param validUntil
	 *            The date that marks the end of the validity period
	 * @param policy
	 *            The policy that defines the restrictions of this consent.
	 * @throws XDSRequestFailedException
	 *             Thrown if any problems occurred during validation or
	 *             processing of the related ITI-41/42 request.
	 */
	public void replaceAllPatientConsents(Patient patient, Date validFrom,
			Date validUntil, PatientConsentPolicy policy, CoalaAuthor author) throws XDSRequestFailedException;
	
	
	/**
	 * Returns the list of {@link PatientConsent} corresponding to this
	 * {@link Patient}.
	 * 
	 * @param patient
	 *            The {@link Patient} whose {@link PatientConsent} instances are of
	 *            interest.
	 * @param sortBy
	 *            The parameter by which the results shall be sorted. If this is
	 *            null, results will be sorted by a default as defined in
	 *            {@link ConsentSortParameter}.
	 * @return the sorted {@link FindPatientConsentResult} of consents corresponding to this patient.
	 */
	public FindPatientConsentResult getPatientConsents(Patient patient,
			ConsentSortParameter sortBy) throws XDSRequestFailedException;

}
