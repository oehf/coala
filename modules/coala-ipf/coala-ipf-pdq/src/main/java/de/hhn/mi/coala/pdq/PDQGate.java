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
package de.hhn.mi.coala.pdq;

import org.openehealth.coala.exception.PDQRequestFailedException;

/**
 * This interface functions as a gate to the communication with the PDQ endpoint.
 * 
 * @author siekmann
 */
public interface PDQGate {
	/**
	 * Returns a result String from the PDQ which contains the data relating to
	 * the found patients.
	 * 
	 * @param hl7ReqString
	 *            the Search string for the PDQ Server
	 * @return the result String from the PDQ
	 * 
	 * @throws PDQRequestFailedException
	 *             Is thrown if an error occurred during the PDQ communication.
	 */
	public String requestPatients(String hl7ReqString)
			throws PDQRequestFailedException;
}
