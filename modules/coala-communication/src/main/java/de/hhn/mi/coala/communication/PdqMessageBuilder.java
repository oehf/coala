package de.hhn.mi.coala.communication;

import java.util.Date;

import de.hhn.mi.coala.util.PXSDateConverter;

public interface PdqMessageBuilder {

	public static final String HL7_VERSION = "2.5";

	public abstract void setPxsDateConverter(PXSDateConverter pxsDateConverter);

	/**
	 * Builds a PDQ request. If any parameter is empty or null, there will no
	 * request parameter created for that one.
	 * 
	 * @param thePatientId
	 *            Patient's ID. Can be null/empty, than the search is without
	 *            the PID parameter.
	 * @param theGivenName
	 *            Patient's given name. Wildcard (*) is allowed. Can be
	 *            null/empty, than the search is without this parameter.
	 * @param theLastName
	 *            Patient's last name. Wildcard (*) is allowed. Can be
	 *            null/empty, than the search is without this parameter.
	 * @param theDoB
	 *            Patient's date of birth
	 * @return HL7 PDQ request message. Can be null/empty, than the search is
	 *         without this parameter.
	 */
	public abstract String buildPdqRequest(String thePatientId,
			String theGivenName, String theLastName, Date theDoB);

	/**
	 * Builds a PDQ request. If any parameter is empty or null, there will no
	 * request parameter created for that one. The
	 * PatientIdAssigningAuthorityUniversalId is set to a default value.
	 * 
	 * @param thePatientId
	 *            Patient's ID. Can be null/empty, than the search is without
	 *            the PID parameter.
	 * @param thePatientIdAssigningAuthorityUniversalId
	 *            Patient's ID Assigning Authority Universal ID. Can be
	 *            null/empty, than the search is without the PID parameter.
	 * @param theGivenName
	 *            Patient's given name. Wildcard (*) is allowed. Can be
	 *            null/empty, than the search is without this parameter.
	 * @param theLastName
	 *            Patient's last name. Wildcard (*) is allowed. Can be
	 *            null/empty, than the search is without this parameter.
	 * @param theDoB
	 *            Patient's date of birth
	 * @return HL7 PDQ request message. Can be null/empty, than the search is
	 *         without this parameter.
	 */
	public abstract String buildPdqRequest(String thePatientId,
			String thePatientIdAssigningAuthorityUniversalId,
			String theGivenName, String theLastName, Date theDoB);

}