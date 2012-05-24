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
package org.openehealth.coala.transformer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openehealth.coala.builder.CDABuilder;
import org.openehealth.coala.builder.DocumentEntryBuilder;
import org.openehealth.coala.domain.CoalaAuthor;
import org.openehealth.coala.domain.Gender;
import org.openehealth.coala.domain.Patient;
import org.openehealth.coala.domain.PatientAddress;
import org.openehealth.coala.domain.PatientConsentPolicy;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.DocumentEntry;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test if {@link XmlTransformer} provides valid HTML for certain XSLT/XML use
 * cases.
 * 
 * @author hhein
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/coala-communication-test-context.xml" })
public class XMLTransformerTest {

	private static final Logger LOG = org.slf4j.LoggerFactory
			.getLogger(XMLTransformerTest.class);

	private String xmlStringInvalid = "";
	private String xmlStringValid = "";
	private String htmlResultExpectedWinows = "";
	private String htmlResultExpectedUnix = "";
	private String xmlString = "";
	private InputStream xsltStream = null;
	
	@Autowired
	private DocumentEntryBuilder documentEntryBuilder;
	@Autowired
	private CDABuilder builder;

	@Before
	public void init() {
		// Initial new before every test
		xmlStringInvalid = "<?xml version=\"1.0\"?>\r\n<ClinicalDocument xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">\r\n    <typeId root=\"2.16.840.1.113883.1.3\" extension=\"POCD_HD000040\"/>\r\n    <templateId root=\"1.3.6.1.4.1.19376.1.5.3.1.1.1\"/>\r\n    <templateId root=\"1.3.6.1.4.1.19376.1.5.3.1.1.7\"/>\r\n    <id root=\"1.2.3.4.281.371\"/>\r\n    <code code=\"57016-8\" displayName=\"Consultation Note\" codeSystem=\"2.16.840.1.113883.6.1\" codeSystemName=\"LOINC\"/>\r\n    <titleConsent to Share Information</title>\r\n    <effectiveTime value=\"20110705112420\"/>\r\n    <confidentialityCode code=\"N\" displayName=\"Normal\" codeSystem=\"1.2.840.113619.20.2.5.2\" codeSystemName=\"Connect-a-thon confidentialityCodes\"/>\r\n    <languageCode code=\"en-US\"/>\r\n    <recordTarget>\r\n        <patientRole>\r\n            <id extension=\"1\" root=\"myUID\"/>\r\n            <addr>\r\n                <streetAddressLine></streetAddressLine>\r\n                <city></city>\r\n                <state></state>\r\n                <postalCode></postalCode>\r\n                <country></country>\r\n            </addr>\r\n            <patient>\r\n                <name>\r\n                    <family>Norris</family>\r\n                    <given>Chuck</given>\r\n                </name>\r\n                <administrativeGenderCode code=\"F\" codeSystem=\"2.16.840.1.113883.5.1\"/>\r\n                <birthTime value=\"20110705\"/>\r\n            </patient>\r\n        </patientRole>\r\n    </recordTarget>\r\n    <author>\r\n        <time value=\"20070118181526-0600\"/>\r\n        <assignedAuthor>\r\n            <assignedPerson>\r\n                <name>\r\n                \t<title>Title</title>\r\n                    <family>Family</family>\r\n                    <given>Given</given>\r\n                </name>\r\n            </assignedPerson>\r\n           \r\n        </assignedAuthor>\r\n    </author>\r\n    <documentationOf typeCode=\"DOC\">\r\n        <serviceEvent classCode=\"ACT\" moodCode=\"EVN\">\r\n            <templateId root=\"1.3.6.1.4.1.19376.1.5.3.1.2.6\"/>\r\n            <id root=\"1.2.3.4.291.300000\"/>\r\n            <code code=\"1.2.840.113619.20.2.9.3\" codeSystemName=\"Default Privacy Policies\"/>\r\n            <effectiveTime>\r\n                <low value=\"$low value\"/>\r\n                <high value=\"$high value\"/>\r\n            </effectiveTime>\r\n        </serviceEvent>\r\n    </documentationOf>\r\n    <component>\r\n        <structuredBody>\r\n            <component>\r\n                <section>\r\n                    <templateId root=\"1.3.6.1.4.1.19376.1.5.3.1.3.23\"/>\r\n                    <code code=\"19826-7\" codeSystem=\"2.16.840.1.113883.6.1\" codeSystemName=\"LOINC\" displayName=\"Informed Consent Obtained\"/>\r\n                    <title>Publish</title>\r\n                    <text>\r\n                  \tONE\r\n                    </text>\r\n                </section>\r\n            </component>\r\n        </structuredBody>\r\n    </component>\r\n</ClinicalDocument>";
		xmlStringValid = "<?xml version=\"1.0\"?><ClinicalDocument xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\"><realmCode code=\"US\"/><typeId root=\"2.16.840.1.113883.1.3\" extension=\"POCD_HD000040\"/><templateId root=\"1.3.6.1.4.1.19376.1.5.3.1.1.1\"/><templateId root=\"1.3.6.1.4.1.19376.1.5.3.1.1.7\"/><id root=\"1.2.3.4.281.371\"/><code code=\"11488-4\" displayName=\"Consultation Note\" codeSystem=\"2.16.840.1.113883.6.1\" codeSystemName=\"LOINC\"/><title>Consent to Share Information</title><effectiveTime value=\"20100215060000\"/><confidentialityCode code=\"N\" displayName=\"Normal\" codeSystem=\"1.2.840.113619.20.2.5.2\" codeSystemName=\"Connect-a-thon confidentialityCodes\"/><languageCode code=\"en-US\"/><recordTarget><patientRole><id extension=\"15\" root=\"2.16.840.1.113883.3.37.4.1.1.2.2.1\"/><addr><streetAddressLine>22 Victoria Ave</streetAddressLine><city>Philadelphia</city><state>PA</state><postalCode>19108</postalCode><country>USA</country></addr><patient><name><family>O'Connor</family><given>Mary</given></name><administrativeGenderCode code=\"F\" codeSystem=\"2.16.840.1.113883.5.1\"/><birthTime value=\"19800131\"/></patient></patientRole></recordTarget><author><time value=\"20070118181526-0600\"/><assignedAuthor><id root=\"2.16.840.1.113883.3.106.1.1251425083\" extension=\"123\"/><addr><streetAddressLine>123 Main ln</streetAddressLine><city>Philadelphia</city><state>PA</state><postalCode>19020</postalCode><country>USA</country></addr><assignedPerson><name><family>Meyers</family><given>Jim</given></name></assignedPerson><representedOrganization nullFlavor=\"NA\"/></assignedAuthor></author><documentationOf typeCode=\"DOC\"><serviceEvent classCode=\"ACT\" moodCode=\"EVN\"><templateId root=\"1.3.6.1.4.1.19376.1.5.3.1.2.6\"/><id root=\"1.2.3.4.291.300000\"/><code code=\"1.2.840.113619.20.2.9.3\" codeSystemName=\"Default Privacy Policies\"/><effectiveTime><low value=\"20100211070000\"/><high value=\"20110211070000\"/></effectiveTime></serviceEvent></documentationOf><component><structuredBody><component><section><templateId root=\"1.3.6.1.4.1.19376.1.5.3.1.3.23\"/><code code=\"19826-7\" codeSystem=\"2.16.840.1.113883.6.1\" codeSystemName=\"LOINC\" displayName=\"Informed Consent Obtained\"/><title>Acknowledged Patient Privacy Consent Policy</title><text>Normal Sharing with authorized users; emergency sharing with all users; publish</text></section></component></structuredBody></component></ClinicalDocument>";
		htmlResultExpectedWinows = "\r\n<h2 align=\"center\">Consent for305009</h2>\r\n<br>\r\n<h3>\r\n<span style=\"font-weight:bold;\">ONE</span>\r\n</h3>\r\n                  \tPublish\r\n                    ";
		htmlResultExpectedUnix = "\n      <h2 align=\"center\">Consent for305009</h2><br><h3><span style=\"font-weight:bold;\">ONE</span></h3>\n      \tPublish\n      \n   ";

		// XSL
		xsltStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("text_xml-urn_ihe_iti_bppc_2007.xsl");

		// XML
		Date actTime = new Date(java.lang.System.currentTimeMillis());
		Patient patient = new Patient("305009", "myUID", "ChuckFind", "Norris",
				actTime, Gender.FEMALE, new PatientAddress());
		DocumentEntry documentEntry = documentEntryBuilder.createDocumentEntry(
				new CoalaAuthor("", "Der", "Autor"), patient,
				PatientConsentPolicy.ONE, actTime, actTime);
		String xmlStringCurrent = builder.createConsentCDA(patient,
				PatientConsentPolicy.ONE, documentEntry);

		/** Which XML-String should be tested? **/
		xmlString = xmlStringCurrent;
	}

	/**
	 * Test if the result is correct, if a XML and XSL is given.
	 */
	@Test
	public void testTransformXmlIntoHtmlByXMLyesXSLyesCorrect() {
		try {
			XmlTransformer xmlTransformer = new XmlTransformer(xsltStream);
			String htmlResult = xmlTransformer.transformXmlIntoHtml(xmlString);

			htmlResult = htmlResult.trim();
			htmlResultExpectedUnix = htmlResultExpectedUnix.trim();
			htmlResultExpectedWinows = htmlResultExpectedWinows.trim();

			assertTrue(htmlResult.equals(htmlResultExpectedUnix)
					|| htmlResult.equals(htmlResultExpectedWinows));
		} catch (Exception e) {
			LOG.error(e.getLocalizedMessage(), e);
			fail(e.getLocalizedMessage());
		}
	}

	/**
	 * Test if the method failed, if no XML is given.
	 */
	@Test
	public void testTransformXmlIntoHtmlByXMLnoXSLyes() {
		boolean isException = false;
		try {
			xmlString = "";
			XmlTransformer xmlTransformer = new XmlTransformer(xsltStream);
			xmlTransformer.transformXmlIntoHtml(xmlString);
		} catch (Exception e) {
			isException = true;
		}

		assertTrue(isException);
	}

	/**
	 * Test if the method failed, if no XSL is given.
	 */
	@Test
	public void testTransformXmlIntoHtmlByXMLyesXSLno() {
		boolean isException = false;
		try {
			xsltStream = null;
			XmlTransformer xmlTransformer = new XmlTransformer(xsltStream);
			xmlTransformer.transformXmlIntoHtml(xmlString);
		} catch (Exception e) {
			isException = true;
		}

		assertTrue(isException);
	}

	/**
	 * Test if the method worked with two calls
	 */
	@Test
	public void testTransformXmlIntoHtmlByValidWithTwoCalls() {
		boolean isException = false;
		try {
			htmlResultExpectedUnix = htmlResultExpectedUnix.trim();
			htmlResultExpectedWinows = htmlResultExpectedWinows.trim();

			XmlTransformer xmlTransformer = new XmlTransformer(xsltStream);
			String htmlResult = xmlTransformer.transformXmlIntoHtml(xmlString);
			String htmlResult2 = xmlTransformer.transformXmlIntoHtml(xmlString);
			htmlResult = htmlResult.trim();
			htmlResult2 = htmlResult2.trim();

			assertTrue(htmlResult.equals(htmlResultExpectedUnix)
					|| htmlResult.equals(htmlResultExpectedWinows));
			assertTrue(htmlResult.equals(htmlResultExpectedUnix)
					|| htmlResult.equals(htmlResultExpectedWinows));
		} catch (Exception e) {
			isException = true;
		}

		assertFalse(isException);
	}

}
