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
package de.hhn.mi.coala.xds.test;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.AssigningAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.hhn.mi.coala.xds.XDSConfigurationImpl;
import de.hhn.mi.coala.xds.XDSTransactor;

/**
 * This class provides the basic initialization of the XDS endpoints ITI-18,
 * ITI-41 and ITI-43, the {@link AssigningAuthority} and loading the
 * RessourceBundle that contains the needed configuration keys for testing
 * issues.
 * 
 * @author mwiesner
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/coala-xds-test-context.xml" })
public abstract class XDSBaseTest {

	protected static final String PID_CONSENT_FIND = "305010";

	@Autowired
	protected XDSTransactor xdsTransactor;

	@Autowired
	protected XDSConfigurationImpl xdsConfiguration;

	protected String encoding;
	
	@Before
	public void baseInit() {
		encoding = xdsConfiguration.getConsentEncoding();

	}
}