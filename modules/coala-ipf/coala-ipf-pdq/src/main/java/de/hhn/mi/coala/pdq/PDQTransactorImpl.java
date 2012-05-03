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

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultExchange;
import org.openehealth.ipf.platform.camel.ihe.pixpdq.PixPdqCamelValidators;
import org.slf4j.Logger;

import de.hhn.mi.coala.exception.PDQConfigurationErrorException;
import de.hhn.mi.coala.exception.PDQRequestFailedException;

/**
 * This class provides the basic method to communicate with the PDQ endpoint
 * (MPI). <br />
 * The method {@code public static String sendPDQRequest(String request)} should
 * be used to send a HL7 request to the PDQ endpoint. The answer is the
 * corresponding HL7 answer
 * 
 * @author siekmann, wkais
 */
public class PDQTransactorImpl implements PDQTransactor {

	public void setCamelContext(CamelContext camelContext) {
		this.camelContext = camelContext;
	}

	public void setProducerTemplate(ProducerTemplate producerTemplate) {
		this.producerTemplate = producerTemplate;
	}

	private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(PDQTransactorImpl.class);
	private final String PDQ_ENDPOINT_BEGINNIG = "pdq-iti21://";
	
	private String pdqEndpoint;
	private CamelContext camelContext;
	private ProducerTemplate producerTemplate;
	
	public PDQTransactorImpl() {

	}
	
	/**
	 * @return the pdqEndpoint
	 */
	public String getPdqEndpoint() {
		return pdqEndpoint;
	}

	/**
	 * @param pdqEndpoint the pdqEndpoint to set
	 */
	public void setPdqEndpoint(String pdqEndpoint) {
		this.pdqEndpoint = pdqEndpoint;
	}

	
	

	/**
	 * This constructor initializes the PDQTransactor with the given
	 * <code>pdqEndpoint</code>.
	 * 
	 * @param pdqEndpoint
	 *            Endpoint for PDQ-transactions with a MPI. Has to start with
	 *            'pdq-itit21://'
	 */
	public PDQTransactorImpl(String pdqEndpoint, CamelContext camelContext, ProducerTemplate producerTemplate)
			throws PDQConfigurationErrorException {
		checkPDQEndpoint(pdqEndpoint);
		this.pdqEndpoint = pdqEndpoint;
		this.camelContext = camelContext;
		this.producerTemplate = producerTemplate;
	}

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
			throws PDQRequestFailedException {

		LOG.info("Received the following PDQv2 request:\n" + request);

		// prepare request
		Exchange requestExchange = prepareRequest(request, camelContext);

		// send request
		Exchange responseExchange = producerTemplate.send(pdqEndpoint,
				requestExchange);
		Exception e = responseExchange.getException();
		if (e != null) {
			LOG.error("Error during validating PDQ request: " + e.getMessage(), e);
			throw new PDQRequestFailedException(
					"A problem occured sending the PDQ request. See log for more information", e);
		}

		// validate response
		try {
			PixPdqCamelValidators.iti21ResponseValidator().process(
					responseExchange);
		} catch (Exception ex) {
			LOG.error("Error during PDQ request: " + ex.getMessage());
			throw new PDQRequestFailedException(
					"A problem occured during validating PDQ-ITI21 response. See log for more information");
		}
		// get response
		String response = responseExchange.getOut().getBody(String.class);

		LOG.info("Received the following PDQv2 response:\n" + response);
		return response;
	}

	private Exchange prepareRequest(String request, CamelContext camelContext)
			throws PDQRequestFailedException {
		Exchange requestExchange = new DefaultExchange(camelContext);
		requestExchange.getIn().setBody(request);
		// validating request
		try {
			PixPdqCamelValidators.iti21RequestValidator().process(
					requestExchange);
		} catch (Exception e) {
			LOG.error("Validating an outgoing PDQ-ITI21 request failed", e);
			throw new PDQRequestFailedException(
					"Validating an outgoing PDQ-ITI21 request failed - Please check your request: " + e.getMessage(), e);
		}
		return requestExchange;
	}

	private void checkPDQEndpoint(String pdqEndpoint)
			throws PDQConfigurationErrorException {
		if ((pdqEndpoint == null) || (pdqEndpoint.trim().equals(""))
				|| (!pdqEndpoint.trim().startsWith(PDQ_ENDPOINT_BEGINNIG))) {
			LOG.error("Wrong PDQ-ITI21 endpoint specified! Endpoint has to start with \""
					+ PDQ_ENDPOINT_BEGINNIG + "\" and must not be null");
			throw new PDQConfigurationErrorException(
					"Wrong PDQ-ITI21 endpoint specified! Endpoint has to start with \""
							+ PDQ_ENDPOINT_BEGINNIG + "\" and must not be null");
		}
	}
}