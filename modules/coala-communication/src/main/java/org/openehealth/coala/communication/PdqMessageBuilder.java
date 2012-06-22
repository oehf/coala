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
package org.openehealth.coala.communication;

import java.util.Date;

import org.openehealth.coala.util.PXSDateConverter;

public interface PdqMessageBuilder {

	public static final String HL7_VERSION = "2.5";

	public abstract void setPxsDateConverter(PXSDateConverter pxsDateConverter);

	/**
	 * Builds a PDQ request. If any parameter is empty or null, there will no
	 * request parameter created for that one.
	 * 
	 * @param thePatientId
	 *            Patient's ID. Can be null/empty, than the search is without
	 *            the PID parameter.
	 * @param theGivenName
	 *            Patient's given name. Wildcard (*) is allowed. Can be
	 *            null/empty, than the search is without this parameter.
	 * @param theLastName
	 *            Patient's last name. Wildcard (*) is allowed. Can be
	 *            null/empty, than the search is without this parameter.
	 * @param theDoB
	 *            Patient's date of birth
	 * @return HL7 PDQ request message. Can be null/empty, than the search is
	 *         without this parameter.
	 */
	public abstract String buildPdqRequest(String thePatientId,
			String theGivenName, String theLastName, Date theDoB);

	/**
	 * Builds a PDQ request. If any parameter is empty or null, there will no
	 * request parameter created for that one. The
	 * PatientIdAssigningAuthorityUniversalId is set to a default value.
	 * 
	 * @param thePatientId
	 *            Patient's ID. Can be null/empty, than the search is without
	 *            the PID parameter.
	 * @param thePatientIdAssigningAuthorityUniversalId
	 *            Patient's ID Assigning Authority Universal ID. Can be
	 *            null/empty, than the search is without the PID parameter.
	 * @param theGivenName
	 *            Patient's given name. Wildcard (*) is allowed. Can be
	 *            null/empty, than the search is without this parameter.
	 * @param theLastName
	 *            Patient's last name. Wildcard (*) is allowed. Can be
	 *            null/empty, than the search is without this parameter.
	 * @param theDoB
	 *            Patient's date of birth
	 * @return HL7 PDQ request message. Can be null/empty, than the search is
	 *         without this parameter.
	 */
	public abstract String buildPdqRequest(String thePatientId,
			String thePatientIdAssigningAuthorityUniversalId,
			String theGivenName, String theLastName, Date theDoB);

}