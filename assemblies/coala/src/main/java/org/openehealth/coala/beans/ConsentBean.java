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
package org.openehealth.coala.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIOutput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.component.UIExtendedDataTable;
import org.richfaces.component.UIPopupPanel;
import org.richfaces.component.UIRegion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.openehealth.coala.domain.CoalaAuthor;
import org.openehealth.coala.domain.ConsentSortParameter;
import org.openehealth.coala.domain.FindPatientConsentResult;
import org.openehealth.coala.domain.Patient;
import org.openehealth.coala.domain.PatientConsent;
import org.openehealth.coala.domain.PatientConsentPolicy;
import org.openehealth.coala.exception.CdaXmlTransformerException;
import org.openehealth.coala.interfacing.CDATransformationService;
import org.openehealth.coala.interfacing.ConsentCreationService;

/**
 * Represents a patients privacy consent. It will be used if a new consent is
 * created, or an existing gets deleted.
 * 
 * @author astiefer, mkuballa
 */
@Component
@Scope("session")
public class ConsentBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(ConsentBean.class);

	private PatientConsentPolicy policy = PatientConsentPolicy.ONE; // default value
	private Date validFrom;
	private Date validUntil;
	private CoalaAuthor author;
	@Transient
	private DataModel<PatientConsent> consents = new ListDataModel<PatientConsent>();
	private List<PatientConsent> consentList;

	private Collection<Object> selectionConsent;
	private PatientConsent selectedConsent;

	private Patient selectedPatient;
	private String patientString = "";
	// SORTING
	private ConsentSortParameter sortParameter = ConsentSortParameter.CREATION_DATE_NEWEST_FIRST;

	// SELECTING POLICIES VIA SELECT-MENU.
	private static Map<String, PatientConsentPolicy> selectablePolicies;
	private String selectedPolicy = policy.getShortName(); 

	private boolean successfulRegistration;
	private boolean errorfulRegistration;
	private boolean validationSuccessful;
	
	/*
	 * Handle on the coala-communication layer
	 */
	@Transient
	@Autowired
	private ConsentCreationService consentCreationService;
	
	/*
	 * Handle on the CDA/Consent service layer
	 */
	@Transient
	@Autowired
	private CDATransformationService cdaService;

	/*
	 * Managed bean injection to get current locale which is needed for I18n
	 * e.g. error messages/warnings.
	 */
	@Transient
	@Autowired
	private LocaleHandler localeHandler;

	/**
	 * @return the ConsentCreationService
	 */
	public ConsentCreationService getConsentCreationService() {
		return consentCreationService;
	}

	
	/**
	 * @param sets the ConsentCreationService
	 */
	public void setConsentCreationService(ConsentCreationService consentCreationService) {
		this.consentCreationService = consentCreationService;
		LOG.info("ConsentCreationService configured and ready... [OK]");
	}

	/**
	 * @return the policy
	 */
	public PatientConsentPolicy getPolicy() {
		return policy;
	}

	/**
	 * @param policy the policy to set
	 */
	public void setPolicy(PatientConsentPolicy policy) {
		this.policy = policy;
	}

	/**
	 * @return the validFrom
	 */
	public Date getValidFrom() {
		return validFrom;
	}

	/**
	 * @param validFrom the validFrom to set
	 */
	public void setValidFrom(Date validFrom) {
		/*
		 *  HACK HERE! we get invalid Date object from RichFaces Calendar component which is exactly 1 Day in the past! so we fix it to have the proper date instance that is shown/selected by the user!
		 */
		if(validFrom!=null) {
			Calendar cal = new GregorianCalendar();
			cal.setTime(validFrom);
			cal.add(Calendar.DAY_OF_MONTH, 1);			
			this.validFrom = cal.getTime();
		}
		else {
			this.validFrom = validFrom;
		}
	}

	/**
	 * @return the validUntil
	 */
	public Date getValidUntil() {
		return validUntil;
	}

	/**
	 * @param validUntil the validUntil to set
	 */
	public void setValidUntil(Date validUntil) {
		/*
		 *  HACK HERE! we get invalid Date object from RichFaces Calendar component which is exactly 1 Day in the past! so we fix it to have the proper date instance that is shown/selected by the user!
		 */
		if(validUntil!=null) {
			Calendar cal = new GregorianCalendar();
			cal.setTime(validUntil);
			cal.add(Calendar.DAY_OF_MONTH, 1);			
			this.validUntil = cal.getTime();
		}
		else {
			this.validUntil = validUntil;
		}
	}

	/**
	 * Selections-Listener for data table displaying {@link PatientConsent}
	 * instances.
	 * 
	 * @param event
	 *            Provided by UI context.
	 */
	public void selectionListenerConsent(AjaxBehaviorEvent event) {
		FacesContext fc = FacesContext.getCurrentInstance();
		String messages = fc.getApplication().getMessageBundle();
		Locale locale = new Locale(localeHandler.getLocale());
		ResourceBundle bundle = ResourceBundle.getBundle(messages, locale);
		
		UIExtendedDataTable dataTable = (UIExtendedDataTable) event
				.getComponent();
		Object originalKey = dataTable.getRowKey();
		for (Object selectionKey : selectionConsent) {
			dataTable.setRowKey(selectionKey);
			if (dataTable.isRowAvailable()) {
				selectedConsent = (PatientConsent) dataTable.getRowData();
				LOG.info("[CONSENT SELECTED]  by "
						+ selectedConsent.getAuthor());
				LOG.info("[CONSENT SELECTED] Patient"
						+ selectedPatient.getLastName());

				UIRegion consentResultPanel = (UIRegion) dataTable.getParent();

				UIOutput renderedConsentText = (UIOutput) consentResultPanel
						.findComponent("renderedConsentText");
				String consentAsHTML = "";
				try {
					consentAsHTML = cdaService.transformToHTML(selectedPatient, selectedConsent.getValidFrom(), selectedConsent.getValidUntil(), selectedConsent.getPolicyType(), selectedConsent.getAuthor());
				} catch (IllegalArgumentException e) {
					consentAsHTML = bundle.getString("errors.htmlTransformationFailed");
					LOG.warn(e.getMessage());
				} catch (CdaXmlTransformerException e) {
					consentAsHTML = bundle.getString("errors.htmlTransformationFailed");
					LOG.warn(e.getMessage());
				}
				renderedConsentText.setValue(consentAsHTML);
				UIPopupPanel consentDisplayPanel = (UIPopupPanel) consentResultPanel
						.findComponent("consentDisplayPanel");
				LOG.info("[CONSENT SELECTED] setShow");
				consentDisplayPanel.setShow(true);
			} else {
				LOG.warn("ROW NOT AVAILABLE");
			}
		}
		dataTable.setRowKey(originalKey);
	}

	/**
	 * Searches the {@link PatientConsent} of the {@link Patient} currently
	 * selected in the patient data table (see
	 * {@link ConsentBean#selectionListener(AjaxBehaviorEvent)}) and sets them
	 * in the consent data table of the UI.
	 * 
	 * @return Always returns "patientSeach"
	 */
	public String search() {
		consentList = null;
		consentList = new ArrayList<PatientConsent>();

		long start = System.currentTimeMillis();

		// perform query against PXS finally
		FindPatientConsentResult findPatientConsentResult = consentCreationService.getPatientConsents(selectedPatient, sortParameter);
		long end = System.currentTimeMillis();

		LOG.info("PXS iti-18-43 query took " + (end - start) + " ms.");

		if (findPatientConsentResult != null) {
			consentList = findPatientConsentResult.getPatientConsents();
			setConsents(new ListDataModel<PatientConsent>(consentList));

		}
		buildPatientFullName();
		return "patientSearch";
	}

	
	/**
	 * Builds the fullname of the patient for displaying in the UI
	 */
	private void buildPatientFullName() {
		setPatientString(selectedPatient.getLastName() + ", "
				+ selectedPatient.getGivenName() + " ("
				+ selectedPatient.getPatientID() + ")");
	}

	/**
	 * @param consents the consents to set
	 */
	public void setConsents(DataModel<PatientConsent> consents) {
		this.consents = consents;
	}

	/**
	 * @return the consents
	 */
	public DataModel<PatientConsent> getConsents() {
		return consents;
	}

	/**
	 * @param selection sets the selectionConset
	 */
	public void setConsentSelection(Collection<Object> selection) {
		this.selectionConsent = selection;
	}

	/**
	 * @return the selectionConsent
	 */
	public Collection<Object> getConsentSelection() {
		return selectionConsent;
	}

	/**
	 * @return the sortParameter
	 */
	public ConsentSortParameter getSortParameter() {
		return sortParameter;
	}

	/**
	 * @param sortParameter the sortParameter to set
	 */
	public void setSortParameter(ConsentSortParameter sortParameter) {
		this.sortParameter = sortParameter;
	}

	/**
	 * Sorts the table of consents by start date
	 */
	public void sortByStartDate() {
		if (sortParameter.equals(ConsentSortParameter.START_DATE_NEWEST_FIRST)) {
			setSortParameter(ConsentSortParameter.START_DATE_OLDEST_FIRST);
		} else {
			setSortParameter(ConsentSortParameter.START_DATE_NEWEST_FIRST);
		}
		search();
	}

	/**
	 * Sorts the table of consents by end date
	 */
	public void sortByEndDate() {
		if (sortParameter.equals(ConsentSortParameter.END_DATE_NEWEST_FIRST)) {
			setSortParameter(ConsentSortParameter.END_DATE_OLDEST_FIRST);
		} else {
			setSortParameter(ConsentSortParameter.END_DATE_NEWEST_FIRST);
		}
		search();
	}

	/**
	 * Sorts the table of consents by policy
	 */
	public void sortByPolicy() {
		if (sortParameter.equals(ConsentSortParameter.POLICY_TYPE_ASCENDING)) {
			setSortParameter(ConsentSortParameter.POLICY_TYPE_DESCENDING);
		} else {
			setSortParameter(ConsentSortParameter.POLICY_TYPE_ASCENDING);
		}
		search();
	}

	/**
	 * Sorts the table of consents by creation date
	 */
	public void sortByCreationDate() {
		if (sortParameter
				.equals(ConsentSortParameter.CREATION_DATE_NEWEST_FIRST)) {
			setSortParameter(ConsentSortParameter.CREATION_DATE_OLDEST_FIRST);
		} else {
			setSortParameter(ConsentSortParameter.CREATION_DATE_NEWEST_FIRST);
		}
		search();
	}

	/**
	 * Sorts the table of consents by author
	 */
	public void sortByAuthor() {
		if (sortParameter.equals(ConsentSortParameter.AUTHOR_ASCENDING)) {
			setSortParameter(ConsentSortParameter.AUTHOR_DESCENDING);
		} else {
			setSortParameter(ConsentSortParameter.AUTHOR_ASCENDING);
		}
		search();
	}

	/**
	 * Sorts the table of consents by activ
	 */
	public void sortByActive() {
		if (sortParameter.equals(ConsentSortParameter.EFFECTIVE_TRUE_FIRST)) {
			setSortParameter(ConsentSortParameter.EFFECTIVE_FALSE_FIRST);
		} else {
			setSortParameter(ConsentSortParameter.EFFECTIVE_TRUE_FIRST);
		}
		search();
	}

	/**
	 * @param patientString the patientString to set
	 */
	public void setPatientString(String patientString) {
		this.patientString = patientString;
	}

	/**
	 * @return the patientString
	 */
	public String getPatientString() {
		return patientString;
	}

	static {
		selectablePolicies = new LinkedHashMap<String, PatientConsentPolicy>();
		selectablePolicies.put(PatientConsentPolicy.ONE.getShortName(), PatientConsentPolicy.ONE);
		selectablePolicies.put(PatientConsentPolicy.TWO.getShortName(), PatientConsentPolicy.TWO);
		selectablePolicies.put(PatientConsentPolicy.THREE.getShortName(), PatientConsentPolicy.THREE);
		selectablePolicies.put(PatientConsentPolicy.FOUR.getShortName(), PatientConsentPolicy.FOUR);
		selectablePolicies.put(PatientConsentPolicy.FIVE.getShortName(), PatientConsentPolicy.FIVE);
	}

	/**
	 * Method gets called when the user selects a policy via the select-menu. It
	 * is needed to view the current policy in the draft view.
	 * 
	 * @param e
	 */
	public void policyChanged(ValueChangeEvent e) {
		// assign new policy
		policy = getPolicyByNr(e.getNewValue().toString());
		selectedPolicy = policy.getShortName();
	}

	/**
	 * @return the selectablePolicies
	 */
	public Map<String, PatientConsentPolicy> getSelectablePolicyInMap() {
		return ConsentBean.selectablePolicies;
	}

	/**
	 * @return the selectedPolicy
	 */
	public String getSelectablePolicy() {
		return selectedPolicy;
	}

	/**
	 * @param policy the policy to set
	 */
	public void setSelectablePolicy(String policy) {
		selectedPolicy = getPolicyByNr(policy).getShortName();
	}

	/**
	 * Cleans the outdated view state
	 */
	public void cleanOutdatedViewState() {
		this.selectionConsent = new ArrayList<Object>();
		this.selectedConsent = null;
		this.selectedPatient = null;
	}
	
	/**
	 * Cleans the ConsentBean for a new PatientSearch
	 */
	public void cleanForNewPatientSearch() {
		this.consentList = new ArrayList<PatientConsent>();
		this.consents = new ListDataModel<PatientConsent>();
		this.patientString = "";
		this.selectedConsent = null;
		this.selectionConsent = new ArrayList<Object>();

	}

	
	/**
	 * Cleans the current selection
	 * @param event 
	 */
	public void cleanCurrentSelection(AjaxBehaviorEvent event) {
		this.selectionConsent = new ArrayList<Object>();
		this.selectedConsent = null;
		LOG.info("[BAEBAEMMMM!]: " + event.getComponent().getClientId());
	}

	/**
	 * @param selectedPatient the selectedPatient to set
	 */
	public void setSelectedPatient(Patient selectedPatient) {
		this.selectedPatient = selectedPatient;
	}

	/**
	 * Method to validate input parameters (validFrom and validUntil) of consent
	 * creation. This is needed to have proper error notification on missing
	 * date input at consent creation view.
	 * 
	 * @param event
	 */
	public void validateCreateConsentParameters(ComponentSystemEvent event) {
		FacesContext fc = FacesContext.getCurrentInstance();
		String messages = fc.getApplication().getMessageBundle();
		Locale locale = new Locale(localeHandler.getLocale());
		ResourceBundle bundle = ResourceBundle.getBundle(messages, locale);

		UIComponent components = event.getComponent();

		UIInput validFromDateInput = (UIInput) components
				.findComponent("validFromInput");
		UIInput validUntilDateInput = (UIInput) components
				.findComponent("validUntilInput");

		boolean validFromDateEmpty = false;
		boolean validUntilDateEmpty = false;

		if (validFromDateInput.getLocalValue() == null
				|| validFromDateInput.getLocalValue().toString().trim().isEmpty()) {
			validFromDateEmpty = true;
		}
		if (validUntilDateInput.getLocalValue() == null
				|| validUntilDateInput.getLocalValue().toString().trim().isEmpty()) {
			validUntilDateEmpty = true;
		}

		if (validFromDateEmpty || validUntilDateEmpty) {
			validationSuccessful = false;
			FacesMessage msg = new FacesMessage(
					bundle.getString("errors.nonEmptyValidationDates"), "");
			msg.setSeverity(FacesMessage.SEVERITY_WARN);
			fc.addMessage(components.getClientId(), msg);
			// passed to the Render Response phase
			fc.renderResponse();
		}
		
		//validates if validFrom is after or equals validUntil
		Date from = (Date) validFromDateInput.getValue();
		Date until = (Date) validUntilDateInput.getValue();
		if(from==null || until == null) {
			validationSuccessful = false;
			FacesMessage msg = new FacesMessage("Please provide valid date input values for From AND Until.", "");
			msg.setSeverity(FacesMessage.SEVERITY_WARN);
			fc.addMessage(components.getClientId(), msg);
			// passed to the Render Response phase
			fc.renderResponse();
		}
		else if(from != null && until != null) {
			if (from.after(until) || from.equals(until)){
				validationSuccessful = false;
				FacesMessage msg = new FacesMessage(
						bundle.getString("errors.fromBeforeUntil"), "");
				msg.setSeverity(FacesMessage.SEVERITY_WARN);
				fc.addMessage(components.getClientId(), msg);
				// passed to the Render Response phase
				fc.renderResponse();
			}
			// all checks passed now, set validationSuccessful to true, which causes the "Register" button to become active!
			else {
				LOG.info("Consent creation validation has passed now.");
				validationSuccessful = true;
			}
		}
		else {
			validationSuccessful = false;
			FacesMessage msg = new FacesMessage("Please provide valid date input values for From AND Until.", "");
			msg.setSeverity(FacesMessage.SEVERITY_WARN);
			fc.addMessage(components.getClientId(), msg);
			// passed to the Render Response phase
			fc.renderResponse();
		}
	}

	/**
	 * Overriding this method ensures that rich:calender validation is not done
	 * by standard validation procedure but by our own
	 * {@link ConsentBean#validateCreateConsentParameters(ComponentSystemEvent)}
	 * method.
	 */
	public void validator(javax.faces.context.FacesContext fc,
			javax.faces.component.UIComponent component, java.lang.Object object) {
		// JUST DO NOT VALIDATE ANYTHING HERE -> all done in
		// validateCreateConsentParameters...
	}

	/**
	 * Gets the logged-in user and sets him as an author.
	 * Then takes the 5 parameters(patient, validFrom, validUntil, policy, author) which are
	 * given by the user and puts them into a patient consent and tries to send it to pxsQuery.
	 * 
	 * @return
	 */
	public String registerConsent() {
		FacesContext fc = FacesContext.getCurrentInstance();
		
		// Replaced dependency to eHF with fixed mocks for the time being.
		author = new CoalaAuthor("testTitle", "testFirstName", "testLastName");

		UIViewRoot uiRoot = FacesContext.getCurrentInstance().getViewRoot();
		
		try {		
			// try to register now, as we have all relevant data for it
			consentCreationService.createPatientConsent(
					selectedPatient, validFrom,validUntil, policy, author);
			setSuccessfulRegistration(true);
			setErrorfulRegistration(false);
			// show successful creation panel to the user
			UIPopupPanel consentCreationSuccessDialog = (UIPopupPanel) uiRoot.findComponent("consentCreationSuccessDialog");
			if(consentCreationSuccessDialog!=null) {
				consentCreationSuccessDialog.setShow(true);
				LOG.info("[CONSENT CREATION] ok, showing success PopUpPanel");				
			}
			// cleanup selected values here, as they are outdated now.
			setValidFrom(null);
			setValidUntil(null);
			setPolicy(PatientConsentPolicy.ONE);
			setValidationSuccessful(false);
			return "patientSearch";

		} catch (Throwable t) {
			setErrorfulRegistration(true);
			setSuccessfulRegistration(false);
			UIPopupPanel consentCreationErrorDialog = (UIPopupPanel) uiRoot.findComponent("consentCreationErrorDialog");
			if(consentCreationErrorDialog != null) {
				consentCreationErrorDialog.setShow(true);
				LOG.warn("[CONSENT CREATION] failed, showing error PopUpPanel");				
			}
			setValidationSuccessful(false);
			
			FacesMessage msg = new FacesMessage("Could not create consent.");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			fc.addMessage(null,msg);
			
			LOG.error(t.getLocalizedMessage(), t);
			// TODO Maybe add Facesmessage for display in ErrorPanelDialog
		}
		return "createConsent";
	}
	
	/**
	 * Returns the selected policy by the number.
	 * @param nr number of the selected policy (ONE, TWO, THREE, FOUR or FIVE)
	 * @return selected policy
	 */
	public PatientConsentPolicy getPolicyByNr(String nr){
		if (nr.equals("ONE")) {
			return PatientConsentPolicy.ONE;
		} else if (nr.equals("TWO")) {
			return PatientConsentPolicy.TWO;
		} else if (nr.equals("THREE")) {
			return PatientConsentPolicy.THREE;
		} else if (nr.equals("FOUR")) {
			return PatientConsentPolicy.FOUR;
		} 
		else {
			return PatientConsentPolicy.FIVE;
		}

	}
	
	/**
	 * @return the selectedConsent
	 */
	public PatientConsent getSelectedConsent() {
		return selectedConsent;
	}

	/**
	 * @param selectedConsent the selectedConsent to set
	 */
	public void setSelectedConsent(PatientConsent selectedConsent) {
		this.selectedConsent = selectedConsent;
	}


	public boolean isSuccessfulRegistration() {
		return successfulRegistration;
	}


	public void setSuccessfulRegistration(boolean successfulRegistration) {
		this.successfulRegistration = successfulRegistration;
	}


	public boolean isErrorfulRegistration() {
		return errorfulRegistration;
	}


	public void setErrorfulRegistration(boolean errorfulRegistration) {
		this.errorfulRegistration = errorfulRegistration;
	}


	public void setValidationSuccessful(boolean validationSuccessful) {
		this.validationSuccessful = validationSuccessful;
	}


	public boolean isValidationSuccessful() {
		return validationSuccessful;
	}

}
