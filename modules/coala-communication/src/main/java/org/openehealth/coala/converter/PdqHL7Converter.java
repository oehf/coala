/*******************************************************************************
 * 
 *  * Licensed to the Apache Software Foundation (ASF) under one or more
 *  * contributor license agreements. See the NOTICE file distributed with
 *  * this work for additional information regarding copyright ownership.
 *  * The ASF licenses this file to You under the Apache License, Version 2.0
 *  * (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 * 
 *******************************************************************************/
package org.openehealth.coala.converter;

import java.util.List;

import org.openehealth.coala.communication.PdqMessageBuilder;
import org.openehealth.coala.domain.Patient;
import org.openehealth.coala.util.PXSDateConverter;

public interface PdqHL7Converter extends PXSDateConverter {

	public abstract void setPdqMessageBuilder(
			PdqMessageBuilder pdqMessageBuilder);

	/**
	 * Converts a HL7 PDQ message to a FindPatientResult object.
	 * 
	 * @param hl7message
	 *            the hl7 message
	 */
	public abstract List<Patient> convertPdqToPatients(String hl7message);

	/**
	 * Converts a patient object to a HL7 PDQ message object. If any parameter
	 * is empty or null, there will no request parameter created for that one.
	 * 
	 * @param thePatient
	 *            the patient query information
	 * @return HL7 PDQ request message according to patient parameter.
	 */
	public abstract String convertObjectToMessage(Patient thePatient);

}