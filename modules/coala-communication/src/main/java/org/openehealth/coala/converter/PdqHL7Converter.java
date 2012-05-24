package org.openehealth.coala.converter;

import java.util.List;

import org.openehealth.coala.communication.PdqMessageBuilder;
import org.openehealth.coala.domain.Patient;
import org.openehealth.coala.util.PXSDateConverter;

public interface PdqHL7Converter extends PXSDateConverter {

	public abstract void setPdqMessageBuilder(
			PdqMessageBuilder pdqMessageBuilder);

	/**
	 * Converts a HL7 PDQ message to a FindPatientResult object.
	 * 
	 * @param hl7message
	 *            the hl7 message
	 */
	public abstract List<Patient> convertPdqToPatients(String hl7message);

	/**
	 * Converts a patient object to a HL7 PDQ message object. If any parameter
	 * is empty or null, there will no request parameter created for that one.
	 * 
	 * @param thePatient
	 *            the patient query information
	 * @return HL7 PDQ request message according to patient parameter.
	 */
	public abstract String convertObjectToMessage(Patient thePatient);

}