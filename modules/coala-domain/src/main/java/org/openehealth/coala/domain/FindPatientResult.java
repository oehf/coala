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
import java.util.Iterator;
import java.util.List;

/**
 * Encapsulates a list of patients. Once the list is read the first time, it will be locked and
 * cannot be modified anymore. You can also manually lock an instance by calling <code>lock()</code>
 * 
 * @author kmaerz, mwiesner
 */
public class FindPatientResult implements Serializable, Iterable<Patient> {

	private static final long serialVersionUID = 1L;

	/**
     * The internal list of query results
     */
    private List<Patient> patientList = new ArrayList<Patient>();

    /** The query that generated this result list */
    private FindPatientQuery query;

    /** if true, the result cannot be modified anymore */
    private boolean locked = false;

	/** this is the parameter the consents will be sorted by */
	private PatientSortParameter sortBy;

    /**
     * Creates a new Query which can then be filled with results. Don't forget to lock the query
     * when done filling in results.
	 *
	 * @param sortBy
	 *            The sort criterion used for result list order.
     * @param originalQuery the original query string that lead to these results.
     */
    public FindPatientResult(FindPatientQuery originalQuery, PatientSortParameter sortBy) {
        if ((originalQuery == null))
            throw new IllegalArgumentException(
                    "Upon creation, one must specify the original query for this set of results");
		if ((sortBy == null) || (sortBy.equals(PatientSortParameter.UNSORTED)))
		{
			sortBy = PatientSortParameter.getDefault();
		}
		this.sortBy = sortBy;
        this.query = originalQuery;
    }

    /**
     * Returns the original String that produces these results
     * @return the original String that produces these results
     */
    public FindPatientQuery getOriginalQuery() {
        return query;
    }

    /**
     * Adds a patient to the result. Adding patients can only happen while the result remains
     * unlocked. Once it is read in anyway, the list will be locked and trying to add a patient
     * results in an IllegalOperationExcaption.
     * 
     * @param patient The patient to add.
     */
    public void addPatient(Patient patient) {
        if (locked)
            throw new IllegalArgumentException(
                    "Cannot add a patient to a locked list. Has the list been accessd already?");
    	// insert sorted in place!
		if (patientList.isEmpty()) {
			patientList.add(patient);
			return;
		}
		else
		{	
			for (int i = 0; i < patientList.size(); i++) {
				if (comparePatients(patient, patientList.get(i)) < 0) {
					patientList.add(i, patient);
					return; // exit, when added
				}
			}
		}
		patientList.add(patient); // if consent has never evaluated to "smaller", add to end of list
    }

    /**
     * Adds a set of patients to the result. Adding patients can only happen while the result
     * remains unlocked. Once it is read in anyway, the list will be locked and trying to add a
     * patient results in an IllegalOperationExcaption.
     * 
     * @param patients The set of patients to add.
     */
    public void addAll(Collection<Patient> patients) {
        if (locked)
            throw new IllegalArgumentException(
                    "Cannot add a patient to a locked list. Has the list been accessd already?");
		for (Patient p : patients) addPatient(p);
    }
    
    /**
	 * Compares two {@link Patient} instances and returns a negative number, if c1 before c2, 0
	 * if c1 ~ c2 and a positive number if c1 after c2. Comparison is ordinal.
	 * Do not use output to calculate anything else than sort order.
	 * 
	 * @param c1
	 *            First {@link Patient} to compare
	 * @param c2
	 *            First {@link Patient} to compare
	 * @return returns a negative number, if c1 < c2, 0 if c1 ~ c2 and a
	 *         positive number if c1 > c2. (Comparison is ordinal)
	 */
	private int comparePatients(Patient c1, Patient c2) {
		if (sortBy == PatientSortParameter.UNSORTED)
			return 0;

		else if (sortBy == PatientSortParameter.BIRTHDATE_NEWEST_FIRST)
			return c1.getBirthdate().compareTo(c2.getBirthdate());
		else if (sortBy == PatientSortParameter.BIRTHDATE_OLDEST_FIRST)
			return -1 * c1.getBirthdate().compareTo(c2.getBirthdate());
		else if (sortBy == PatientSortParameter.GIVENNAME_ASCENDING)
			return c1.getGivenName().compareTo(c2.getGivenName());
		else if (sortBy == PatientSortParameter.GIVENNAME_DESCENDING)
			return -1 * c1.getGivenName().compareTo(c2.getGivenName());
		else if (sortBy == PatientSortParameter.LASTNAME_ASCENDING)
			return c1.getLastName().compareTo(c2.getLastName());
		else if (sortBy == PatientSortParameter.LASTNAME_DESCENDING)
			return -1 * c1.getLastName().compareTo(c2.getLastName());
		else if (sortBy == PatientSortParameter.PID_ASCENDING)
			return new Long(c1.getPatientID()).compareTo(new Long(c2.getPatientID()));
		else if (sortBy == PatientSortParameter.PID_DESCENDING)
			return  -1 * new Long(c1.getPatientID()).compareTo(new Long(c2.getPatientID()));

		throw new RuntimeException(
				"All possibilities checked. This line should never be reached, unless new sort Options have been introduced. Please update sorting algorithm.");
	}
    
    /**
     * Returns the list of patients as an unmodifiable List.
     * @return the list of patients as an unmodifiable List.
     */
    public List<Patient> getPatients() {    
        return Collections.unmodifiableList(patientList);
    }
    
    /**
     * Locks this result set. After locking it cannot be unlocked again.
     */
    public void lock(){
        locked = true;
    }

    // ===========================================================//
    // INTERNAL LOGIC ENDS HERE, ITERATION CODE AHEAD //
    // ===========================================================//

    /**
     * Returns an iterator to access all patients from the result
     */
    public Iterator<Patient> iterator() {
        return new PatientIterator();

    }

    /**
     * Private inner iterator to safely iterate over all patients in the search result. It does not
     * support removing patients.
     * 
     * @author kmaerz
     */
    private class PatientIterator implements Iterator<Patient> {

        private int i = 0; // the next element to return

        /**
         * True, if the next() method can return another patient
         */
        public boolean hasNext() {
            return (i < patientList.size());
        }

        /**
         * Returns the next patient
         */
        public Patient next() {
            locked = true; // lock list forever when it is being read.
            Patient result = patientList.get(i);
            i++;
            return result;
        }

        /**
         * Not supported for security reasons
         */
        public void remove() {
            throw new UnsupportedOperationException(
                    "Removing patients from a search result is not supported.");
        }

    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FindPatientResult [patientList=");
		builder.append(patientList);
		builder.append(", query=");
		builder.append(query);
		builder.append(", locked=");
		builder.append(locked);
		builder.append("]");
		return builder.toString();
	}

}
