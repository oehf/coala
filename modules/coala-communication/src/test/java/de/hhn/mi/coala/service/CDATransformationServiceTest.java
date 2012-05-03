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
package de.hhn.mi.coala.service;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.DocumentEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.hhn.mi.coala.builder.DocumentEntryBuilder;
import de.hhn.mi.coala.domain.CoalaAuthor;
import de.hhn.mi.coala.domain.Gender;
import de.hhn.mi.coala.domain.Patient;
import de.hhn.mi.coala.domain.PatientAddress;
import de.hhn.mi.coala.domain.PatientConsentPolicy;
import de.hhn.mi.coala.exception.CdaXmlTransformerException;
import de.hhn.mi.coala.exception.XslTransformerException;
import de.hhn.mi.coala.interfacing.CDATransformationService;
import de.hhn.mi.coala.util.PXSDateConverter;

/**
 * Unit tests for {@link CDATransformationServiceImpl}.
 * 
 * @author ckarmen
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/coala-communication-test-context.xml" })
public class CDATransformationServiceTest {

	// private static final Logger LOG = org.slf4j.LoggerFactory
	// .getLogger(PXSQueryServiceTest.class);

	// TODO Auslagern in globale Konfiguration (Interface, Spring, etc.)
	private static final String HL7_PatientIdAssigningAuthorityUniversalId = "2.16.840.1.113883.3.37.4.1.1.2.2.1";

	private Integer thePatientID = 1;
	private static Patient thePatient;
	private static DocumentEntry theDocumentEntry;
	private static Date theCurrentDate = new Date(999);
	private static Date theValidFromDate = new Date(999);
	private static Date theValidToDate = new Date();
	private static CoalaAuthor theCoalaAuthor = new CoalaAuthor("Dr", "Steel",
			"Hammer");
	
	@Autowired
	private CDATransformationService theCdaTransService;
	@Autowired
	private DocumentEntryBuilder documentEntryBuilder;
	@Autowired
	private PXSDateConverter pxsDateConverter;

	/**
	 * Test method for
	 * {@link de.hhn.mi.coala.service.CDATransformationServiceImpl#CDATransformationServiceImpl(java.lang.String)}
	 * .
	 */
	@Test
	public final void testCDATransformationServiceImpl() {
		CDATransformationServiceImpl cdaTransformationServiceImpl = new CDATransformationServiceImpl();
		try {
			cdaTransformationServiceImpl.setNameOfXSLTSheet("");
			fail("Creation of CDATransformationService should have failed, but succeeded");
		} catch (IllegalArgumentException e) {
		}

		try {
			cdaTransformationServiceImpl.setNameOfXSLTSheet(null);
			fail("Creation of CDATransformationService should have failed, but succeeded");
		} catch (IllegalArgumentException e) {
		}

		cdaTransformationServiceImpl.setNameOfXSLTSheet(
		"text_xml-urn_ihe_iti_bppc_2007.xsl");

	}

	/**
	 * Test method for
	 * {@link de.hhn.mi.coala.service.CDATransformationServiceImpl#transformToHTML(de.hhn.mi.coala.domain.Patient, java.util.Date, java.util.Date, de.hhn.mi.coala.domain.PatientConsentPolicy, de.hhn.mi.coala.domain.CoalaAuthor)}
	 * .
	 * 
	 * @throws IllegalArgumentException
	 *             thrown when XSL file is not available
	 * @throws XslTransformerException
	 *             thrown when XSL data is invalid
	 * @throws CdaXmlTransformerException
	 *             thrown when XML transformation fails
	 */
	@Test
	public final void testTransformToHTMLPatientDateDatePatientConsentPolicyCoalaAuthorDocumentEntry()
			throws IllegalArgumentException, XslTransformerException,
			CdaXmlTransformerException {

		PatientConsentPolicy policy = PatientConsentPolicy.ONE;

		thePatient = new Patient((thePatientID++).toString(),
				HL7_PatientIdAssigningAuthorityUniversalId, "Michael",
				"Mustermann", theCurrentDate, Gender.MALE, new PatientAddress());

		theDocumentEntry = documentEntryBuilder.createDocumentEntry(
				theCoalaAuthor, thePatient, policy, theValidFromDate,
				theValidToDate);

		String html = theCdaTransService.transformToHTML(thePatient,
				theValidFromDate, theValidToDate, policy, theCoalaAuthor);

		assertTrue("The generated HTML code is missing some data (policy)!",
				html.contains(policy.getShortName()));
		assertTrue(
				"The generated HTML code is missing some data (document title)!",
				html.contains(theDocumentEntry.getTitle().getValue()));
	}

	/**
	 * Test method for
	 * {@link de.hhn.mi.coala.service.CDATransformationServiceImpl#transformToValidPatientConsent(de.hhn.mi.coala.domain.Patient, de.hhn.mi.coala.domain.PatientConsentPolicy, de.hhn.mi.coala.domain.CoalaAuthor, org.openehealth.ipf.commons.ihe.xds.core.metadata.DocumentEntry)}
	 * .
	 * 
	 * @throws IllegalArgumentException
	 *             thrown when XSL file is not available
	 * @throws XslTransformerException
	 *             thrown when XSL data is invalid
	 */
	@Test
	public final void testTransformToValidPatientConsent()
			throws IllegalArgumentException, XslTransformerException {

		thePatient = new Patient((thePatientID++).toString(),
				HL7_PatientIdAssigningAuthorityUniversalId, "Michael",
				"Mustermann", theCurrentDate, Gender.MALE, new PatientAddress());

		theDocumentEntry = documentEntryBuilder.createDocumentEntry(
				theCoalaAuthor, thePatient, PatientConsentPolicy.ONE,
				theValidFromDate, theValidToDate);

		String cda = theCdaTransService.transformToValidPatientConsent(
				thePatient, PatientConsentPolicy.ONE, theCoalaAuthor,
				theDocumentEntry);

		assertTrue("Some consent data is invalid (patient last name)",
				cda.contains(thePatient.getLastName()));
		assertTrue("Some consent data is invalid (author last name)",
				cda.contains(theCoalaAuthor.getFamilyName()));
		assertTrue("Some consent data is invalid (policy name)",
				cda.contains(PatientConsentPolicy.ONE.getShortName()));
		assertTrue("Some consent data is invalid (valid from)",
				cda.contains(pxsDateConverter
						.DateToShortString(theValidFromDate)));
	}

}
