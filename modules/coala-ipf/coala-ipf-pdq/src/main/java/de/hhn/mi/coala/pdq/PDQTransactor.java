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

import de.hhn.mi.coala.exception.PDQRequestFailedException;

/**
 * This interface contains a method to interact with a PDQ endpoint.
 * 
 * @author siekmann
 */
public interface PDQTransactor {
	/**
	 * This method provides the basic communication with the PDQ endpoint. It
	 * gets a HL7 request string and returns the HL7 response.<br />
	 * In case of a exception during processing the request, the exception is
	 * thrown.
	 * 
	 * @param request
	 *            HL7 request message for PDQ endpoint
	 * @return HL7 response from PDQ endpoint
	 * @throws Exception
	 *             Exception that is thrown during processing of the request
	 */
	public String sendPDQRequest(String request)
			throws PDQRequestFailedException;
}
