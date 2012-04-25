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

/**
 * This class contains all information related to patients. This class is
 * immutable except for the consentList, which can be exchanged for a new set
 * but will be checked against being added to the wrong patient.
 * <p>
 * Patients can be compared by using patient.equals() which will compare Patient
 * IDs.
 * 
 * @author kmaerz, hhein
 * 
 */
public class Patient implements Serializable {

	private static final long serialVersionUID = 1L;

	private String givenName;
	private String lastName;
	private String patientID;
	private String patientIDAssigningAuthorityUniversalId;
	private Date birthdate;
	private Gender sex;
	private PatientAddress address;

	/**
	 * @param patientID
	 *            Identifier (MPI-ID), may not be null or empty.
	 * @param patientIDAssigningAuthorityUniversalId
	 *            May not be null or empty.
	 * @param givenName
	 *            May not be null or empty.
	 * @param lastName
	 *            May not be null or empty.
	 * @param birthdate
	 *            May not be null.
	 * @param gender
	 *            May be null.
	 * @param address
	 *            May not be null.
	 */

	public Patient(String patientID,
			String patientIDAssigningAuthorityUniversalId, String givenName,
			String lastName, Date birthdate, Gender gender, PatientAddress address) {

		// Assure that parameters are neither null nor empty
		if (!checkString(patientID))
			throw new IllegalArgumentException(
					"patientID can neither be null nor empty.");
		if (!checkString(patientIDAssigningAuthorityUniversalId))
			throw new IllegalArgumentException(
					"patientIDAssigningAuthorityUniversalId can neither be null nor empty.");
		if (!checkString(givenName))
			throw new IllegalArgumentException(
					"givenName can neither be null nor empty.");
		if (!checkString(lastName))
			throw new IllegalArgumentException(
					"lastName can neither be null nor empty.");
		if (birthdate == null)
			throw new IllegalArgumentException("birthdate cannot be null.");
		if (address == null)
			throw new IllegalArgumentException("address cannot be null.");
		if (gender == null)
			throw new IllegalArgumentException("gender cannot be null.");
		
		// set corresponding fields
		this.patientID = patientID;
		this.patientIDAssigningAuthorityUniversalId = patientIDAssigningAuthorityUniversalId;
		this.givenName = givenName;
		this.lastName = lastName;
		this.birthdate = birthdate;
		this.sex = gender;
		this.address = address;
	}

	/**
	 * @return the GivenName
	 */
	public String getGivenName() {
		return givenName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @return the patientID
	 */
	public String getPatientID() {
		return patientID;
	}

	/**
	 * @return the assigningAuthority
	 */
	public String getPatientIDAssigningAuthorityUniversalId() {
		return patientIDAssigningAuthorityUniversalId;
	}

	/**
	 * @return the birthDate
	 */
	public Date getBirthdate() {
		return birthdate;
	}

	/**
	 * @return the sex
	 */
	public String getSex() {
		return sex.toString();
	}

	/**
	 * @return the address
	 */
	public PatientAddress getAddress() {
		return address;
	}

	/**
	 * Checks if a given String s is either null or empty and returns false if
	 * either is true.
	 * 
	 * @param s
	 *            the String to check
	 * @return true, if string is neither null nor empty, false otherwise.
	 */
	private boolean checkString(String s) {
		if ((s == null) || (s.isEmpty()))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result
				+ ((birthdate == null) ? 0 : birthdate.hashCode());
		result = prime * result
				+ ((givenName == null) ? 0 : givenName.hashCode());
		result = prime * result
				+ ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result
				+ ((patientID == null) ? 0 : patientID.hashCode());
		result = prime
				* result
				+ ((patientIDAssigningAuthorityUniversalId == null) ? 0
						: patientIDAssigningAuthorityUniversalId.hashCode());
		result = prime * result + ((sex == null) ? 0 : sex.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Patient other = (Patient) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (birthdate == null) {
			if (other.birthdate != null)
				return false;
		} else if (!birthdate.equals(other.birthdate))
			return false;
		if (givenName == null) {
			if (other.givenName != null)
				return false;
		} else if (!givenName.equals(other.givenName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (patientID == null) {
			if (other.patientID != null)
				return false;
		} else if (!patientID.equals(other.patientID))
			return false;
		if (patientIDAssigningAuthorityUniversalId == null) {
			if (other.patientIDAssigningAuthorityUniversalId != null)
				return false;
		} else if (!patientIDAssigningAuthorityUniversalId
				.equals(other.patientIDAssigningAuthorityUniversalId))
			return false;
		if (sex == null) {
			if (other.sex != null)
				return false;
		} else if (!sex.equals(other.sex))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Patient [givenName=" + givenName + ", lastName=" + lastName
				+ ", patientID=" + patientID
				+ ", patientIDAssigningAuthorityUniversalId="
				+ patientIDAssigningAuthorityUniversalId + ", birthdate="
				+ birthdate + ", sex=" + sex + ", address=" + address + "]";
	}

}