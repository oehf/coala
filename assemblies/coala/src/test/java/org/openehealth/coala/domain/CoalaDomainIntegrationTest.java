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

import static org.junit.Assert.assertNotNull;

import java.util.Date;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.openehealth.coala.builder.CDABuilder;
import org.openehealth.coala.domain.FindPatientQuery;
import org.openehealth.coala.domain.FindPatientResult;
import org.openehealth.coala.domain.Gender;
import org.openehealth.coala.domain.Patient;
import org.openehealth.coala.domain.PatientAddress;
import org.openehealth.coala.domain.PatientSortParameter;

/**
 * Tests the integration of the coala domain module.
 * @author astiefer
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/ehf-system-context.xml"})
public class CoalaDomainIntegrationTest {

	private String patientIDAssigningAuthorityUniversalId = "2.16.840.1.113883.3.37.4.1.1.2.2.1";

	@Test
	public void testCreationOfDomainObjects() {
		Patient p = new Patient("123", patientIDAssigningAuthorityUniversalId, "John", "Doe", new Date(), Gender.MALE, new PatientAddress());
		assertNotNull(p);
		FindPatientQuery q = new FindPatientQuery("123", "John", "Doe", null);
		assertNotNull(q);
		FindPatientResult r = new FindPatientResult(q, PatientSortParameter.getDefault());
		assertNotNull(r);
	}
	
	/**
	 * Simple test to show that we have all dependencies required for templating
	 * mechanism for {@link CDABuilder} stuff.
	 */
	@Test
	public void testVelocityLibraryViaCDABuilder() {
		CDABuilder builder = new CDABuilder("CDATemplate.txt");
		assertNotNull(builder);
	}
	
	public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(CoalaDomainIntegrationTest.class);
    }
}
