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
package de.hhn.mi.coala.pdq.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.hhn.mi.coala.exception.PDQRequestFailedException;
import de.hhn.mi.coala.pdq.PDQGate;

/**
 * This test tests the PDQGateImpl.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/coala-pdq-test-context.xml" })
public class PDQGateTest implements PDQTestConstants {

	@Autowired
	private PDQGate pdqGate;

	@Test
	public void testSpringContext() {
		assertNotNull(pdqGate);
	}

	/**
	 * Test for basic communication. Sending a request to endpoint and expecting
	 * a valid response. <br />
	 * <ul>
	 * This is tested by testing .
	 * <li>asserting that response is not NULL</li>
	 * <li>asserting that response starts with MSH</li>
	 * </ul>
	 * 
	 * @throws PDQRequestFailedException
	 *             Thrown if anything went wrong.
	 */
	@Test
	public void testRequestPatients() {
		String retString = pdqGate.requestPatients(VALID_REQUEST_HEADER);
		assertNotNull(retString);
		assertTrue(retString.startsWith("MSH|"));
	}

	/**
	 * Test that an exception is thrown when an invalid PDQ request is send
	 * 
	 * @exception A
	 *                PDQRequestFailedException is thrown if any error occurred
	 *                during sending the PDQ-ITI21 request
	 */
	@Test(expected = PDQRequestFailedException.class)
	public void testMPIConnectionWithInvalidRequestHeader() {
		pdqGate.requestPatients(INVALID_REQUEST_HEADER);

	}
}
