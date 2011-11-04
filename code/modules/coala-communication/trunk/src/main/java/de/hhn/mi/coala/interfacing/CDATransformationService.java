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
package de.hhn.mi.coala.interfacing;

import java.util.Date;

import javax.xml.transform.TransformerException;

import org.openehealth.ipf.commons.ihe.xds.core.metadata.DocumentEntry;

import de.hhn.mi.coala.domain.CoalaAuthor;
import de.hhn.mi.coala.domain.Patient;
import de.hhn.mi.coala.domain.PatientConsent;
import de.hhn.mi.coala.domain.PatientConsentPolicy;
import de.hhn.mi.coala.exception.CdaXmlTransformerException;

/**
 * This interface models the transformation/conversion for various usage
 * scenarios within UI-frontend to communication layer. It possible create
 * (<b>BUT NOT PERSIST</b>) a new instance of {@link PatientConsent} for other
 * purposes. Additionally, CDA2HTML conversions are possible here.
 * 
 * @author mwiesner, astiefer, ckarmen
 */
public interface CDATransformationService {

	/**
	 * Transforms {@link PatientConsent} which contains a valid CDA document
	 * (via {@link PatientConsent#getXml()} into a pretty and well formed HTML
	 * output.
	 * <p>
	 * Note well: NO HTML/HEAD/BODY html tags are allowed in the resulting HTML
	 * string. Only tags like P/B/H1/H2...
	 * 
	 * @param consent
	 *            A {@link PatientConsent} which contains a valid CDA document.
	 * @return A transformed html result string for the BPPC acknowledgment
	 *         information.
	 * @throws TransformerException
	 *             Thrown when transformer was not able to process.
	 */
	@Deprecated
	String transformToHTML(PatientConsent consent)
			throws CdaXmlTransformerException;

	/**
	 * Transforms a given XML input string which represents a valid CDA document
	 * into a pretty and well formed HTML output.
	 * <p>
	 * Note well: NO HTML/HEAD/BODY html tags are allowed in the resulting HTML
	 * string. Only tags like P/B/H1/H2...
	 * 
	 * @param cdaDocumentContent
	 *            A well formed xml CDA data string.
	 * @return A transformed html result string for the BPPC acknowledgment
	 *         information.
	 * @throws TransformerException
	 *             Thrown when transformer was not able to process.
	 */
	@Deprecated
	String transformToHTML(String cdaDocumentContent)
			throws CdaXmlTransformerException;

	/**
	 * Transforms CDA relevant (meta) data into a pretty and well formed HTML
	 * output.
	 * <p>
	 * Note well: NO HTML/HEAD/BODY html tags are allowed in the resulting HTML
	 * string. Only tags like P/B/H1/H2...
	 * 
	 * @param patient
	 *            The {@link Patient} instance for which the new
	 *            {@link PatientConsent} shall be created. Must not be null.
	 * @param validFrom
	 *            A {@link Date} instance <b>from</b> when a
	 *            {@link PatientConsent} shall be active. Must not be null or in
	 *            the future.
	 * @param validUntil
	 *            A {@link Date} instance <b>until</b> when a
	 *            {@link PatientConsent} shall be active. Must not be null AND
	 *            at least six months after <code>validFrom</code>.
	 * @param policy
	 *            One out of five values for {@link PatientConsentPolicy}.
	 * @param cauthor
	 *            A {@link CoalaAuthor} instance for informations about the
	 *            CDA's author.
	 * @return A transformed html result string for the BPPC acknowledgment
	 *         information.
	 * @throws TransformerException
	 *             Thrown when transformer was not able to process.
	 */
	String transformToHTML(Patient patient, Date validFrom, Date validUntil,
			PatientConsentPolicy policy, CoalaAuthor cauthors)
			throws CdaXmlTransformerException;

	/**
	 * TODO Creates a new and well formed {@link PatientConsent} instance including
	 * CDA information which needs to be created ad hoc on the basis of the
	 * provided information.
	 * 
	 * @param p
	 *            The {@link Patient} instance for which the new
	 *            {@link PatientConsent} shall be created. Must not be null.
	 * @param policy
	 *            One out of five values for {@link PatientConsentPolicy}.
	 * @param cauthor
	 *            A {@link CoalaAuthor} instance for informations about the
	 *            CDA's author.
	 * @param documentEntry
	 *            A {@link DocumentEntry} instance for further information, like
	 *            creation date and author.
	 * @return A prepared {@link PatientConsent} instance containing all CDA
	 *         information for the BPPC acknowledgment information.
	 * @throws IllegalArgumentException
	 *             Thrown if parameters were invalid
	 */
	String transformToValidPatientConsent(Patient p,
			PatientConsentPolicy policy, CoalaAuthor cauthor,
			DocumentEntry documentEntry);

}
