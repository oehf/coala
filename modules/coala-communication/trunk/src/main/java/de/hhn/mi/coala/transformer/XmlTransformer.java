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
package de.hhn.mi.coala.transformer;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import de.hhn.mi.coala.exception.CdaXmlTransformerException;
import de.hhn.mi.coala.exception.XslTransformerException;

/**
 * Transforms a XML file with a XSL file into HTML and represents it as a
 * string.
 * 
 * @author hhein
 */
public class XmlTransformer {

	private Transformer transformer = null;

	/**
	 * Transform a XSL InputStream into a StreamSource and set it as data field
	 * ({@link XmlTransformer#xslStreamSource}).
	 * 
	 * @param inputStream
	 *            required: the InputStream of a XSL file.
	 * 
	 * @throws IllegalArgumentException
	 *             if the XSL InputStream isn't valid.
	 * @throws XslTransformerException
	 *             if the XSL InputStream can't be transformed to a
	 *             StreamSource.
	 */
	public XmlTransformer(InputStream inputStream)
			throws IllegalArgumentException, XslTransformerException {
		if (inputStream == null)
			throw new IllegalArgumentException("XSL InputStream is required.");

		try {
			StreamSource xslStreamSource = new StreamSource(inputStream);
			TransformerFactory transFact = TransformerFactory.newInstance();
			transformer = transFact.newTransformer(xslStreamSource);
		} catch (Exception e) {
			throw new XslTransformerException(
					"Can't transform a XSL InputStream into StreamSource.", e);
		}
	}

	/**
	 * Transforms a XML string with XSL into HTML and represents the code
	 * between body tag as string.
	 * 
	 * @param xmlString
	 *            the XML file as string.
	 * @return htmlString the HTML code between the body tags from the XML and
	 *         XSL ( {@link XmlTransformer#XmlTransformer(InputStream)}) file.
	 * @throws IllegalArgumentException
	 *             if the XML string is null or not valid.
	 * @throws CdaXmlTransformerException
	 *             if their is a error on transforming XML and XSL into HTML.
	 */
	public String transformXmlIntoHtml(String xmlString)
			throws IllegalArgumentException, CdaXmlTransformerException {

		xmlString = deliverRelevantXML(xmlString);
		String htmlString = transformXmlAndXslIntoHtml(xmlString);
		htmlString = deliverRelevantHTML(htmlString);

		return htmlString;
	}

	/**
	 * Deliver the relevant XML part from the hole XML string.
	 * 
	 * @param xmlString
	 *            required: the XML string
	 * @return xmlString a valid XML string
	 * @throws IllegalArgumentException
	 *             if the XML string is null or not valid.
	 */
	private String deliverRelevantXML(String xmlString) {
		if (xmlString == null || xmlString.isEmpty())
			throw new IllegalArgumentException("No XML string given.");

		try {
			if (!xmlString.startsWith("<?xml")) {
				xmlString = xmlString.substring(xmlString.indexOf("<?xml"));
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"No valid XML string given. File didn't contain \"<?xml...\"");
		}

		return xmlString;
	}

	/**
	 * Transform the XML and XSL into HTML and return the result it as string.
	 * 
	 * @param xmlString
	 *            the valid XML string.
	 * @return htmlString the HTML code.
	 * @throws CdaXmlTransformerException
	 *             if their is a error on transforming XML and XSL into HTML.
	 */
	private String transformXmlAndXslIntoHtml(String xmlString)
			throws CdaXmlTransformerException {
		String htmlString = null;
		try {
			StringWriter stringWriter = new StringWriter();
			StreamSource xmlStreamSource = new StreamSource(new StringReader(
					xmlString));
			StreamResult htmlStreamResult = new StreamResult(stringWriter);

			transformer.transform(xmlStreamSource, htmlStreamResult);

			htmlString = stringWriter.toString();
		} catch (Exception e) {
			throw new CdaXmlTransformerException(
					"Couldn't transform a CDA XML string into valid HTML via XSLT.",
					e);
		}

		return htmlString;
	}

	/**
	 * Deliver the relevant HTML part from the hole HTML code. This is
	 * necessary, because we take a general XSL file.
	 * 
	 * @param htmlString
	 *            the hole HTML string.
	 * @return htmlString the relevant HTML string (code between body tags).
	 */
	public String deliverRelevantHTML(String htmlString) {
		if (htmlString.contains("<body>") && htmlString.contains("</body>")) {
			htmlString = htmlString.substring(htmlString.indexOf("<body>") + 6,
					htmlString.indexOf("</body>"));
		}

		return htmlString;
	}
}
