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
package de.hhn.mi.coala.beans;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.component.UIExtendedDataTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.hhn.mi.coala.domain.FindPatientQuery;
import de.hhn.mi.coala.domain.FindPatientResult;
import de.hhn.mi.coala.domain.Patient;
import de.hhn.mi.coala.domain.PatientAddress;
import de.hhn.mi.coala.domain.PatientConsent;
import de.hhn.mi.coala.domain.PatientSortParameter;
import de.hhn.mi.coala.exception.ServiceParameterException;
import de.hhn.mi.coala.interfacing.PXSPatientService;

/**
 * This class represents an managedBean for a PatientSearch via PXS-pdqv2.
 * 
 * @author astiefer, mwiesner
 * 
 */
@Component
@Scope("session")
public class PatientBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(PatientBean.class);

	private String patientID;
	private String givenName = new String();
	private String lastName = new String();
	private Date birthdate;
	private PatientAddress address;

	@Transient
	private DataModel<Patient> patients = new ListDataModel<Patient>();
	private List<Patient> patientsResList;
	private boolean initialSearchState = true;

	private Patient selectedPatient;

	private Collection<Object> selectionPatient;

	// SORTING
	private PatientSortParameter sortParameter = PatientSortParameter
			.getDefault();

	// Search String
	private StringBuffer searchString = new StringBuffer();

	/*
	 * Managed bean injection to be capable of resetting old consent result
	 * tables.
	 */
	@Transient
	@Autowired
	private ConsentBean consentBean;

	/*
	 * Managed bean injection to get current locale which is needed for I18n
	 * e.g. error messages/warnings.
	 */
	@Transient
	@Autowired
	private LocaleHandler localeHandler;

	/*
	 * this setter is needed for consentBean inject by Spring/JSF
	 */
	public void setConsentBean(ConsentBean consentBean) {
		this.consentBean = consentBean;
	}

	/*
	 * Communication layer bindings
	 */
	@Transient
	@Autowired
	private PXSPatientService pxsQueryService;

	/**
	 * @return the pxsQueryService
	 */
	public PXSPatientService getPxsQueryService() {
		return pxsQueryService;
	}

	/**
	 * @param pxsQueryService
	 *            the pxsQueryService to set
	 */
	public void setPxsQueryService(PXSPatientService pxsQueryService) {
		this.pxsQueryService = pxsQueryService;
		LOG.info("PXSQueryService configured and ready... [OK]");
	}

	/**
	 * Default Constructor Initializes the PatientBean with default values
	 */
	public PatientBean() {
		patientsResList = new ArrayList<Patient>();
		setPatients(new ListDataModel<Patient>(patientsResList));
	}

	/**
	 * @return the patientID
	 */

	public String getpatientID() {
		return patientID;
	}

	/**
	 * @param patientID
	 *            the patientID to set
	 */
	public void setPatientID(String patientID) {
		this.patientID = patientID;
	}

	/**
	 * @return the givenName
	 */
	public String getGivenName() {
		return givenName;
	}

	/**
	 * @param givenName
	 *            the givenName to set
	 */
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the birthdate
	 */
	public Date getBirthdate() {
		return birthdate;
	}

	/**
	 * @param birthdate
	 *            the birthdate to set
	 */
	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	/**
	 * @return the adress
	 */
	public PatientAddress getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the adress to set
	 */
	public void setAddress(PatientAddress address) {
		this.address = address;
	}

	/**
	 * @return the initalSearchState
	 */
	public boolean isInitialSearchState() {
		return initialSearchState;
	}

	/**
	 * @param initialSearchState
	 *            the initialSearchState to set
	 */
	public void setInitialSearchState(boolean initialSearchState) {
		this.initialSearchState = initialSearchState;
	}

	/**
	 * Method to validate search parameters patientID, givenName, lastName and
	 * dateOfBirth as a group. This is needed to have proper error notification
	 * on failed search field input at search view, if user provides non
	 * matching (invalid) input.
	 * 
	 * @param event
	 */
	public void validateSearchParameters(ComponentSystemEvent event) {
		searchString.delete(0, searchString.length());
		FacesContext fc = FacesContext.getCurrentInstance();
		String messages = fc.getApplication().getMessageBundle();
		Locale locale = new Locale(localeHandler.getLocale());
		ResourceBundle bundle = ResourceBundle.getBundle(messages, locale);

		UIComponent components = event.getComponent();

		UIInput patientIDInput = (UIInput) components
				.findComponent("patientID");
		String patientID = patientIDInput.getLocalValue().toString();
		UIInput givenNameInput = (UIInput) components
				.findComponent("givenName");
		String givenName = givenNameInput.getLocalValue().toString();
		UIInput lastNameInput = (UIInput) components.findComponent("lastName");
		String lastName = lastNameInput.getLocalValue().toString();
		UIInput dateOfBirthInput = (UIInput) components
				.findComponent("dateOfBirth");

		boolean patientIDEmpty = false;
		boolean givenNameEmpty = false;
		boolean lastNameEmpty = false;
		boolean dateOfBirthEmpty = false;
		if (patientIDInput.getLocalValue() == null
				|| patientIDInput.getLocalValue().toString().trim().isEmpty()) {
			patientIDEmpty = true;
		}
		if (givenNameInput.getLocalValue() == null
				|| givenNameInput.getLocalValue().toString().trim().isEmpty()) {
			givenNameEmpty = true;
		}
		if (lastNameInput.getLocalValue() == null
				|| lastNameInput.getLocalValue().toString().trim().isEmpty()) {
			lastNameEmpty = true;
		}
		if (dateOfBirthInput.getLocalValue() == null
				|| dateOfBirthInput.getLocalValue().toString().trim().isEmpty()) {
			dateOfBirthEmpty = true;
		}

		if (patientIDEmpty && givenNameEmpty && lastNameEmpty
				&& dateOfBirthEmpty) {
			FacesMessage msg = new FacesMessage(
					bundle.getString("errors.nonEmptySearchKey"), "");
			msg.setSeverity(FacesMessage.SEVERITY_WARN);
			fc.addMessage(components.getClientId(), msg);
			// passed to the Render Response phase
			fc.renderResponse();
			cleanOutdatedViewState();
			setPatients(new ListDataModel<Patient>());

		} else {
			boolean validationErrorOccured = false;
			// validating correctness of patientID value if it is set
			if (!patientIDEmpty) {
				if (!StringUtils.isNumeric(patientID)) {
					FacesMessage msg = new FacesMessage(
							bundle.getString("errors.numericPatientID"), "");
					msg.setSeverity(FacesMessage.SEVERITY_WARN);
					fc.addMessage(patientIDInput.getClientId(), msg);
					validationErrorOccured = true;
				} else { // ok here -> no actions required, move on now :)
				}
				searchString.append(bundle.getString("search.patient_id")
						+ ": " + patientID + " ");
			}
			// validating correctness of givenName value if it is set
			if (!givenNameEmpty) {

//				String givenNameChecked = checkAndFixWrongCharset(givenName);
				boolean patternOK = Pattern.matches("[a-zA-ZßÜüÖöÄä]*[*]?",
						givenName);
				if (!patternOK) {
					FacesMessage msg = new FacesMessage(
							bundle.getString("errors.nonNumericGivenName"), "");
					msg.setSeverity(FacesMessage.SEVERITY_WARN);
					fc.addMessage(givenNameInput.getClientId(), msg);
					validationErrorOccured = true;
				} else { // ok here -> no actions required, move on now :)
				}
				searchString.append(bundle
						.getString("search.patient_givenName")
						+ ": "
						+ givenName + " ");
			}
			// validating correctness of lastname value if it is set
			if (!lastNameEmpty) {

//				String lastNameChecked = checkAndFixWrongCharset(lastName);
				boolean patternOK = Pattern.matches("[a-zA-ZßÜüÖöÄä]*[*]?",
						lastName);
				if (!patternOK) {
					FacesMessage msg = new FacesMessage(
							bundle.getString("errors.nonNumericLastName"), "");
					msg.setSeverity(FacesMessage.SEVERITY_WARN);
					fc.addMessage(lastNameInput.getClientId(), msg);
					validationErrorOccured = true;
				} else { // ok here -> no actions required, move on now :)
				}
				searchString.append(bundle.getString("search.patient_lastname")
						+ ": " + lastName + " ");
			}
			// validating correctness of dateOfBirth value if it is set
			if (!dateOfBirthEmpty) {
				try {
					// check for a valid date format by casting into a date
					Date dateOfBirth = (Date) dateOfBirthInput.getLocalValue();
					// if we have real Date object -> check for future date
					// value which make no sense here!
					if (dateOfBirth.after(new Date())) {
						FacesMessage msg = new FacesMessage(
								"Date of birth must not be in the future!", "");
						msg.setSeverity(FacesMessage.SEVERITY_WARN);
						fc.addMessage(dateOfBirthInput.getClientId(), msg);
						validationErrorOccured = true;
					} else { // ok here -> no actions required, move on now :)
					}
				} catch (RuntimeException rt) {
					// occurs if no valid Date object was in the input field
					// given by the user input
					FacesMessage msg = new FacesMessage(
							"Please provide a valid date of birth in a useful format. Hint: 'Calendar'!",
							"");
					msg.setSeverity(FacesMessage.SEVERITY_WARN);
					fc.addMessage(dateOfBirthInput.getClientId(), msg);
					validationErrorOccured = true;
				}
			}
			/*
			 * FINALLY CHECK IF ANY ERRORS HAVE OCCURED WHICH CAUSE THE EARLY
			 * RENDER RESPONSE... -> STOP ANY SEARCH!
			 */
			if (validationErrorOccured) {
				// passed to the Render Response phase
				fc.renderResponse();
				cleanOutdatedViewState();
				setPatients(new ListDataModel<Patient>());
			}
		}
	}

	/**
	 * Helper method to display the last valid search string in the results
	 * table.
	 */
	public String getSearchString() {
		if (!(searchString.toString().trim().equals(""))) {
			return "\"" + searchString.toString() + "\"";
		}
		return "";
	}

	/**
	 * Overriding this method ensures that rich:calender validation is not done
	 * by standard validation procedure but by our own
	 * {@link PatientBean#validateSearchParameters(ComponentSystemEvent)}
	 * method.
	 */
	public void validator(javax.faces.context.FacesContext fc,
			javax.faces.component.UIComponent component, java.lang.Object object) {
		// JUST DO NOT VALIDATE ANYTHING HERE -> all done in
		// validateSearchParameters...
	}

	/**
	 * Anchor point to trigger a real search within the coala-communication
	 * layer.
	 * 
	 * @return Always returns an empty string.
	 */
	public String search() {
		FacesContext fc = FacesContext.getCurrentInstance();
		String messages = fc.getApplication().getMessageBundle();
		Locale locale = new Locale(localeHandler.getLocale());
		ResourceBundle bundle = ResourceBundle.getBundle(messages, locale);
		
		//Clean ConsentBean
		consentBean.cleanForNewPatientSearch();

		patientsResList = null;

		try {
			String givenNameCharsetChecked = checkAndFixWrongCharset(givenName);
			String lastNameCharsetChecked = checkAndFixWrongCharset(lastName);
			// generate FindPatientQuery
			FindPatientQuery findPatientQuery = new FindPatientQuery(patientID,
					givenNameCharsetChecked, lastNameCharsetChecked, birthdate);
			LOG.info("Preparing pdqv2 queries against PXS: \n"
					+ findPatientQuery.toString());

			long start = System.currentTimeMillis();
			// perform query against PXS finally
			FindPatientResult findPatientResult = pxsQueryService.findPatients(
					findPatientQuery, sortParameter);
			long end = System.currentTimeMillis();

			LOG.info("PXS pdqv2 query took " + (end - start) + " ms.");

			// process results
			if (findPatientResult != null
					&& findPatientResult.getPatients().size() != 0) {
				patientsResList = new ArrayList<Patient>();
				// all the other dynamically queried patients
				patientsResList.addAll(findPatientResult.getPatients());
				setPatients(new ListDataModel<Patient>(patientsResList));
				/*
				 * cleaning up the old values of the UIInput fields
				 */
				cleanOutdatedViewState();

				/*
				 * resetting all consents which are outdated from now on within
				 * the consent bean
				 */
				consentBean.setConsents(new ListDataModel<PatientConsent>());
				initialSearchState = false;
			} else {
				cleanOutdatedViewState();
				setPatients(new ListDataModel<Patient>());
				FacesMessage msg = new FacesMessage(
						bundle.getString("errors.noPatientFound"));
				msg.setSeverity(FacesMessage.SEVERITY_INFO);
				fc.addMessage(fc.getViewRoot().findComponent("patientResultPanel").getClientId(),msg);
				LOG.warn("PDQ did not result in any hits. FindPatientResult was NULL...!");
			}

		} catch (ServiceParameterException spe) {
			/*
			 * if invalid parameters were given - which should not happen due to
			 * earlier validation, we just set an empty result list
			 */
			setPatients(new ListDataModel<Patient>());
		} catch (RuntimeException rt) {
			LOG.error(rt.getLocalizedMessage(), rt);
		}
		/*
		 * returning an empty string as no navigation rule/case is associated
		 * here
		 */
		return "";
	}

	/*
	 * Charset conversion to UTF-8 from ISO encoding as MPI HL7 queries will
	 * fail if umlauts contained, otherwise!
	 */
	private String checkAndFixWrongCharset(String s) {
		String checkedISO = new String(
				s.getBytes(Charset.forName("ISO-8859-1")));
		LOG.info(s + " indirectly converted to ISO-8859-1: " + checkedISO);
		// return new String(checkedISO.getBytes(), Charset.forName("UTF-8"));
		return checkedISO;
	}

	/**
	 * Helper method to clean some UI input fields and old selection data from
	 * last query request.
	 */
	private void cleanOutdatedViewState() {
		// cleaning search input fields
		setPatientID("");
		setGivenName("");
		setLastName("");
		setBirthdate(null);
		// cleaning potentially selected AND outdated patient
		setPatientSelection(new ArrayList<Object>());
		this.selectedPatient = null;
		// cleaning potentially selected AND outdated consent
		consentBean.cleanOutdatedViewState();
	}

	/**
	 * @param patients
	 *            the patients to set
	 */
	public void setPatients(DataModel<Patient> patients) {
		this.patients = patients;
	}

	/**
	 * @return the patients
	 */
	public DataModel<Patient> getPatients() {
		return patients;
	}

	/**
	 * @return the sortParameter
	 */
	public PatientSortParameter getSortParameter() {
		return sortParameter;
	}

	/**
	 * @param sortParameter
	 *            the sortParameter to set
	 */
	public void setSortParameter(PatientSortParameter sortParameter) {
		this.sortParameter = sortParameter;
	}

	/**
	 * Sorts the patient table by patientID
	 */
	public void sortByPatientID() {
		if (sortParameter.equals(PatientSortParameter.PID_ASCENDING)) {
			setSortParameter(PatientSortParameter.PID_DESCENDING);
		} else {
			setSortParameter(PatientSortParameter.PID_ASCENDING);
		}
		search();
	}

	/**
	 * Sorts the patient table by givenName
	 */
	public void sortByGivenName() {
		if (sortParameter.equals(PatientSortParameter.GIVENNAME_ASCENDING)) {
			setSortParameter(PatientSortParameter.GIVENNAME_DESCENDING);
		} else {
			setSortParameter(PatientSortParameter.GIVENNAME_ASCENDING);
		}
		search();
	}

	/**
	 * Sorts the patient table by lastName
	 */
	public void sortByLastName() {
		if (sortParameter.equals(PatientSortParameter.LASTNAME_ASCENDING)) {
			setSortParameter(PatientSortParameter.LASTNAME_DESCENDING);
		} else {
			setSortParameter(PatientSortParameter.LASTNAME_ASCENDING);
		}
		search();
	}

	/**
	 * Sorts the patient table by birthdate
	 */
	public void sortByBirthdate() {
		if (sortParameter.equals(PatientSortParameter.BIRTHDATE_NEWEST_FIRST)) {
			setSortParameter(PatientSortParameter.BIRTHDATE_OLDEST_FIRST);
		} else {
			setSortParameter(PatientSortParameter.BIRTHDATE_NEWEST_FIRST);
		}
		search();
	}

	/**
	 * Selection-Listener for data table displaying {@link Patient} instances.
	 * 
	 * @param event
	 *            Provided by UI context.
	 */
	public void selectionListenerPatient(AjaxBehaviorEvent event) {
		UIExtendedDataTable dataTable = (UIExtendedDataTable) event
				.getComponent();
		Object originalKey = dataTable.getRowKey();
		for (Object selectionKey : selectionPatient) {
			dataTable.setRowKey(selectionKey);
			if (dataTable.isRowAvailable()) {
				// cleaning potentially selected AND outdated consent
				consentBean.cleanOutdatedViewState();

				selectedPatient = (Patient) dataTable.getRowData();
				consentBean.setSelectedPatient(selectedPatient);

				LOG.info("[PATIENT SELECTED]  "
						+ selectedPatient.getPatientID());
				consentBean.search();

			} else {
				LOG.warn("ROW NOT AVAILABLE");
			}
		}
		dataTable.setRowKey(originalKey);
	}

	/**
	 * @param selection
	 *            the selectionPatient to set
	 */
	public void setPatientSelection(Collection<Object> selection) {
		this.selectionPatient = selection;
	}

	/**
	 * @return the selectionPatient
	 */
	public Collection<Object> getPatientSelection() {
		return selectionPatient;
	}

	/**
	 * @return the selectedPatient
	 */
	public Patient getSelectedPatient() {
		return selectedPatient;
	}
}
