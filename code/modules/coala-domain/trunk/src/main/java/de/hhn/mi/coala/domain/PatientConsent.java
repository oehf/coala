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
package de.hhn.mi.coala.domain;

import java.io.Serializable;
import java.util.Date;
import de.hhn.mi.coala.domain.CoalaAuthor;

/**
 * This class represents a patient consent document
 * @author kmaerz
 *
 */
public class PatientConsent implements Serializable {

	private static final long serialVersionUID = 1L;
	private Date validFrom;
	private Date validUntil;
	private PatientConsentPolicy policy;
	private Patient patient;
	private boolean obsolete;
	private CoalaAuthor author;
	private Date creationDate;
	private boolean active;
	private String cda;
	/** This is true if the consent does not contain all of the necessary data **/
	private boolean invalid;
	


	/**
	 * Creates a new Patient Consent
	 * 
	 * @param validFrom This marks the starting time of this consent's validity
	 * @param validUntil This marks the end time of this consent's validity
	 * @param policy identifies the policy contained within. 
	 * @param patient the patient to whom this consent belongs
	 * @param obsolete A consent is obsolete, if it has been replaced by another consent in the pxs
	 * @param author the creator of this consent
	 * @param creationDate the date this consent was written into the pxs database
	 * @param cda the cda representation of this consent
	 */
	public PatientConsent(Date validFrom, Date validUntil,
			PatientConsentPolicy policy, Patient patient, boolean obsolete, CoalaAuthor author, Date creationDate, String cda) {
		if (validFrom == null)
			throw new IllegalArgumentException("validFrom cannot be null.");
		if (creationDate == null)
			throw new IllegalArgumentException("creationDate cannot be null.");
		if (validUntil == null)
			throw new IllegalArgumentException("validUntil cannot be null.");
		if (policy == null)
			throw new IllegalArgumentException("policy cannot be null.");
		if (patient == null)
			throw new IllegalArgumentException("patient cannot be null.");
		if (validUntil.before(validFrom))
			throw new IllegalArgumentException(
					"validUntil cannot be before validFrom");
		if (author == null) throw new IllegalArgumentException("author cannot be null.");
		this.author = author;
		this.validFrom = validFrom;
		this.validUntil = validUntil;
		this.policy = policy;
		this.patient = patient;
		this.obsolete = obsolete;
		this.cda = cda;
		this.active = isActive();
		this.creationDate = creationDate;
	}

	/**
	 * Returns true, if the agreement is valid (startDate < now < endDate) and
	 * it is not marked as obsolete. It does NOT evaluate data of other
	 * acknowledgments and accordingly may not recognize conflicts with other
	 * acknowledgments.
	 * 
	 * @return true, if (startDate < now < endDate) AND (obsolete == false),
	 *         false otherwise
	 */
	public boolean isActive() {
		Date now = new Date();
		active =((!obsolete) && (validFrom.before(now)) && (now.before(validUntil)));
		return active;
	}

	/**
	 * Returns the date that marks the beginning of the validity period for this consent
	 * @return the startDate
	 */
	public Date getValidFrom() {
		return validFrom;
	}
	

	/**
	 * Returns the date that marks the end of the validity period for this consent
	 * 
	 * @return the endDate
	 */
	public Date getValidUntil() {
		return validUntil;
	}


	/**
	 * @return the type of policy that this instance represents
	 */
	public PatientConsentPolicy getPolicyType() {
		return policy;
	}

	/**
	 * @return the patient to whom this policy belongs
	 */
	public Patient getPatient() {
		return patient;
	}
	
	/**
	 * @return the author of this consent
	 */
	public CoalaAuthor getAuthor() {
		return author;
	}

	/**
	 * Returns the date that marks the the time of creation of this consent
	 * 
	 * @return this consent's date of creation
	 */
	public Date getCreationDate() {
		return creationDate;
	}
	
	/**
	 * Returns this consent's cda representation
	 * @return this consent's cda representation
	 */
	public String getCda() {
		return cda;
	}

	/**
	 * @return true, if this document is obsolete, meaning it is overidden by another document.
	 */
	public boolean isObsolete() {
		return obsolete;
	}

	/**
	 * @return true, if the consent did not contain all necessary information. As such, this consent is invalid and should not be considered.
	 */
	public boolean isInvalid() {
		return invalid;
	}

	/**
	 * Invalidates this consent. 
	 */
	public void invalidate() {
		this.invalid = true;
	}
	
}