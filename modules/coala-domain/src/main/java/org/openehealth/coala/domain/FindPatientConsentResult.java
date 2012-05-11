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
package org.openehealth.coala.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Holds all consents for a given patient. This set locks itself once it is
 * being accessed the first time.
 * 
 * @author kmaerz
 */
public class FindPatientConsentResult implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The internal list of query results
	 */
	private List<PatientConsent> consentList = new ArrayList<PatientConsent>();

	/** The patient to whom this set belongs */
	private Patient patient;

	/** if true, the result cannot be modified anymore */
	private boolean locked = false;

	/** this is the parameter the consents will be sorted by */
	private ConsentSortParameter sortBy;

	/**
	 * The time that this consent set has been created
	 */
	private Date timeOfCreation;

	/**
	 * Creates a new set of consents. Add Results via addConsent() and lock
	 * afterwards. Once locked or accessed, the list of consents is irrevocably
	 * immutable.
	 * 
	 * @param sortBy
	 *            The sort criterion used for result list order.
	 * @param patient
	 *            The patient to whom this set of consents belongs.
	 */
	public FindPatientConsentResult(Patient patient, ConsentSortParameter sortBy) {
		if (patient == null)
			throw new IllegalArgumentException(
					"Upon creation, one must specify the patientID of the patient to whom these results belong");
		this.patient = patient;

		if ((sortBy == null) || (sortBy.equals(ConsentSortParameter.UNSORTED)))
			sortBy = ConsentSortParameter.getDefault();
		this.sortBy = sortBy;

		this.timeOfCreation = new Date();
	}

	/**
	 * Returns the {@link Patient} instance to whom this {@link FindPatientConsentResult} belongs
	 * 
	 * @return the {@link Patient} instance to whom this {@link FindPatientConsentResult} belongs
	 */
	public Patient getPatient() {
		return patient;
	}

	/**
	 * Adds a consent to the result. Adding consents can only take place while
	 * the result remains unlocked. Once it is read in anyway, the list will be
	 * locked and trying to add a consent results in an
	 * IllegalOperationExcaption.
	 * 
	 * @param consent
	 *            The consent to add.
	 */
	public void addPatientConsent(PatientConsent consent) {
		if (locked)
			throw new IllegalArgumentException(
					"Cannot add a consent to a locked list. Has the list been accessd already?");

		// insert sorted in place!
		if (consentList.isEmpty()) {
			consentList.add(consent);
			return;
		}
		else
			for (int i = 0; i < consentList.size(); i++) {
				if (compareConsents(consent, consentList.get(i)) < 0) {
					consentList.add(i, consent);
					return; // exit, when added
				}
			}
		consentList.add(consent); // if consent has never evaluated to "smaller", add to end of list
	}

	/**
	 * Adds a set of consents to the result. Adding consents can only happen
	 * while the result remains unlocked. Once it is read in anyway, the list
	 * will be locked and trying to add a consent results in an
	 * IllegalOperationExcaption.
	 * 
	 * @param consents
	 *            The set of consents to add.
	 */
	public void addAll(Collection<PatientConsent> consents) {
		if (locked)
			throw new IllegalArgumentException(
					"Cannot add a consent to a locked list. Has the list been accessd already?");
		for (PatientConsent c : consents) addPatientConsent(c);
	}

	/**
	 * Returns the list of consents as an unmodifiable List.
	 * 
	 * @return the list of consents as an unmodifiable List.
	 */
	public List<PatientConsent> getPatientConsents() {

		return Collections.unmodifiableList(consentList);
	}

	/**
	 * Locks this result set. After locking it cannot be unlocked again.
	 */
	public void lock() {
		locked = true;
	}

	/**
	 * Returns the time that this set of results has been created (time of
	 * calling it's constructor)
	 * 
	 * @return the time that this set of results has been created (time of
	 *         calling it's constructor)
	 */
	public Date getTimeOfCreation() {
		return this.timeOfCreation;
	}

	/**
	 * Compares two consents and returns a negative number, if c1 before c2, 0
	 * if c1 ~ c2 and a positive number if c1 after c2. Comparison is ordinal.
	 * Do not use output to calculate anything else than sort order.
	 * 
	 * @param c1
	 *            First consent to compare
	 * @param c2
	 *            First consent to compare
	 * @return returns a negative number, if c1 < c2, 0 if c1 ~ c2 and a
	 *         positive number if c1 > c2. (Comparison is ordinal)
	 */
	private int compareConsents(PatientConsent c1, PatientConsent c2) {
		if (sortBy == ConsentSortParameter.UNSORTED)
			return 0;

		else if (sortBy == ConsentSortParameter.END_DATE_NEWEST_FIRST)
			return c1.getValidUntil().compareTo(c2.getValidUntil());
		else if (sortBy == ConsentSortParameter.END_DATE_OLDEST_FIRST)
			return -1 * c1.getValidUntil().compareTo(c2.getValidUntil());
		
		else if (sortBy == ConsentSortParameter.CREATION_DATE_NEWEST_FIRST)
			return c1.getCreationDate().compareTo(c2.getCreationDate());
		else if (sortBy == ConsentSortParameter.CREATION_DATE_OLDEST_FIRST)
			return -1 * c1.getCreationDate().compareTo(c2.getCreationDate());
		
		else if (sortBy == ConsentSortParameter.AUTHOR_ASCENDING)
			return c1.getAuthor().compareTo(c2.getAuthor());
		else if (sortBy == ConsentSortParameter.AUTHOR_DESCENDING)
			return -1 * c1.getAuthor().compareTo(c2.getAuthor());

		else if (sortBy == ConsentSortParameter.START_DATE_NEWEST_FIRST)
			return c1.getValidFrom().compareTo(c2.getValidFrom());
		else if (sortBy == ConsentSortParameter.START_DATE_OLDEST_FIRST)
			return -1 * c1.getValidFrom().compareTo(c2.getValidFrom());

		else if (sortBy == ConsentSortParameter.OBSOLETE_TRUE_FIRST)
			return -1 * compareBool(c1.isObsolete(), c2.isObsolete());
		else if (sortBy == ConsentSortParameter.OBSOLETE_FALSE_FIRST)
			return compareBool(c1.isObsolete(), c2.isObsolete());

		else if (sortBy == ConsentSortParameter.EFFECTIVE_TRUE_FIRST)
			return -1 * compareBool(c1.isActive(), c2.isActive());
		else if (sortBy == ConsentSortParameter.EFFECTIVE_FALSE_FIRST)
			return compareBool(c1.isActive(), c2.isActive());

		else if (sortBy == ConsentSortParameter.POLICY_TYPE_ASCENDING)
			return c1.getPolicyType().getNumber()
					- c2.getPolicyType().getNumber();
		else if (sortBy == ConsentSortParameter.POLICY_TYPE_DESCENDING)
			return -1
					* (c1.getPolicyType().getNumber() - c2.getPolicyType()
							.getNumber());

		throw new RuntimeException(
				"All possibilities checked. This line should never be reached, unless new sort Options have been introduced. Please update sorting algorithm.");
	}

	/**
	 * Compare two boolean parameters. Consider comparison b1 - b2 with True = 1
	 * and False = 0;
	 * 
	 * @param b1
	 *            first boolean
	 * @param b2
	 *            second boolean
	 * @return b1 - b2, where True = 1, False = 0;
	 */
	private int compareBool(boolean b1, boolean b2) {
		if (b1 == b2)
			return 0; // Both equal
		if (b1)
			return 1; // b1 = true, b2 = false
		return -1; // b1 = false, b2 = true;
	}

	// ===========================================================//
	// INTERNAL LOGIC ENDS HERE, ITERATION CODE AHEAD //
	// ===========================================================//

	/**
	 * Returns an iterator to access all patients from the result
	 */
	public Iterator<PatientConsent> iterator() {
		return new PatientConsentIterator();
	}

	/**
	 * Private inner iterator to safely iterate over all patients in the search
	 * result. It does not support removing patients.
	 * 
	 * @author kmaerz
	 */
	private class PatientConsentIterator implements Iterator<PatientConsent> {

		private int i = 0; // the next element to return

		/**
		 * True, if the next() method can return another patient
		 */
		public boolean hasNext() {
			return (i < consentList.size());
		}

		/**
		 * Returns the next patient
		 */
		public PatientConsent next() {
			locked = true; // lock list forever when it is being read.
			PatientConsent result = consentList.get(i);
			i++;
			return result;
		}

		/**
		 * Not supported for security reasons
		 */
		public void remove() {
			throw new UnsupportedOperationException(
					"Removing consents from a search result is not supported.");
		}

	}

}
