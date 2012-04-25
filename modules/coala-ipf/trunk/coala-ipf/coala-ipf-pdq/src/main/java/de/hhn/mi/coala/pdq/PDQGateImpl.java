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

import org.slf4j.Logger;
import de.hhn.mi.coala.exception.PDQRequestFailedException;

/**
 * This class functions as a gate to the communication with the PDQ endpoint.
 * 
 * @author kmaerz, siekmann
 */
public class PDQGateImpl implements PDQGate {

	private static final Logger LOG = org.slf4j.LoggerFactory
			.getLogger(PDQGateImpl.class);

	private PDQTransactor pdqTransactor;

	/**
	 * @return the pdqTransactor
	 */
	public PDQTransactor getPdqTransactor() {
		return pdqTransactor;
	}

	/**
	 * @param pdqTransactor the pdqTransactor to set
	 */
	public void setPdqTransactor(PDQTransactor pdqTransactor) {
		this.pdqTransactor = pdqTransactor;
	}

	public PDQGateImpl() {
	}

	/**
	 * Returns a result String from the PDQ which contains the data relating to
	 * the found patients.
	 * 
	 * @param hl7ReqString
	 *            The search string for the PDQ Server
	 * @return The result String from the PDQ
	 * @throws PDQRequestFailedException
	 *             Is thrown if an error occurred during the PDQ communication.
	 */
	public String requestPatients(String hl7ReqString)
			throws PDQRequestFailedException {
		try {
			return pdqTransactor.sendPDQRequest(hl7ReqString);
		} catch (Exception e) {
			LOG.error(e.getLocalizedMessage(), e);
			throw new PDQRequestFailedException(
					"Communication with PDQ failed. See enclosed Exception for details",
					e);
		}
	}
}
