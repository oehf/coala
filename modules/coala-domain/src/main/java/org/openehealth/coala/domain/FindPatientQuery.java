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
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import org.openehealth.coala.exception.ServiceParameterException;


/**
 * This class encapsulates a query with simple String parameters. It might implement input validation at a later point.
 * 
 * @author kmaerz
 */
public class FindPatientQuery implements Serializable {

	private static final long serialVersionUID = 1L;
	
    private String givenName;

    private String lastName;

    private String patientID;

    private Date birthdate;
    
    /**
     * This query encapsulates all the necessary information to search for patients. In general, an
     * empty or null String denotes that this field be not included in the search.
     * 
     * @param patientID the patient's ID, must not be null or empty AND strictly numeric.
     * @param givenName the patient's first name, must not be null or empty.
     * @param lastName the patient's last name, must not be null or empty.
     * @param birthdate the patient's date of birth, must not be null or empty.
     */
    public FindPatientQuery(String patientID, String givenName, String lastName, Date birthdate){
		if ((!validate(patientID)) && (!validate(givenName))
				&& (!validate(lastName)) && (birthdate == null))
			throw new ServiceParameterException(
					"Cannot create a query that doesn't contain any values (All parameters were either null or empty)");
		if (!(patientID == null) && !StringUtils.isNumeric(patientID))
			throw new ServiceParameterException(
					"PatientID must be numeric but was: " + patientID);
		// set corresponding fields
        this.patientID = patientID;
        this.givenName = givenName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        
    }
    
    /**
     * If a String is empty, it makes it null. Also, it returns true, if the String contains at least one character
     * @param s the string to check
     * @return true, if the String contains at least one character (e.g. is neither null nor empty)
     */
    private boolean validate(String s) {
		if (s == null) return false;
		if (s.trim().isEmpty()) {
			s = null;
			return false;
		}
		return true;
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
     * @return the birthdate
     */
    public Date getBirthdate() {
        return birthdate;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FindPatientQuery [givenName=");
		builder.append(givenName);
		builder.append(", lastName=");
		builder.append(lastName);
		builder.append(", patientID=");
		builder.append(patientID);
		builder.append(", birthdate=");
		builder.append(birthdate);
		builder.append("]");
		return builder.toString();
	}
           
}
