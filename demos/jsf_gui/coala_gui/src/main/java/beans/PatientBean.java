package beans;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * This class represents an managedBean of a patient.
 * 
 * @author astiefer
 * 
 */
@ManagedBean
@SessionScoped
public class PatientBean {

	private String id;
	private String givenName = new String();
	private String lastName = new String();
	private Date birthDate;
	private DataModel<Patient> patients;
	private ArrayList<Patient> patientsResList;
	private ArrayList<Patient> patientList;

	public PatientBean() {
		patientsResList = new ArrayList<Patient>();
		setPatients(new ListDataModel<Patient>(patientsResList));
		patientList = new ArrayList<Patient>();
		patientList.add(new Patient("1", "Fritz", "Meier", new Date()));
		patientList.add(new Patient("2", "Karl", "Müller", new Date()));
		patientList.add(new Patient("3", "Jonas", "Nold", new Date()));
		patientList.add(new Patient("4", "Martin", "Wiesner", new Date()));
		patientList.add(new Patient("5", "Jens", "Müller", new Date()));
		patientList.add(new Patient("6", "Nina", "Bougatf", new Date()));
		patientList.add(new Patient("7", "Anja-Janina", "Stiefermann", new Date()));
		patientList.add(new Patient("8", "Keno", "März", new Date()));
		patientList.add(new Patient("9", "Marco", "Kuballa", new Date()));
		patientList.add(new Patient("10", "Holger", "Hein", new Date()));
		patientList.add(new Patient("11", "Sven", "Siekmann", new Date()));
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String search() {
		// TODO: Call search method implemented in service
		// TEST-Method
		patientsResList.clear();
		for (Patient patient : patientList) {
			if (id != null && !id.trim().equals("")) {
				if (patient.getId().equals(id)) {
					if (!patientsResList.contains(patient)) {
						patientsResList.add(patient);
						setPatients(new ListDataModel<Patient>(patientsResList));
						return null;
					}
				}
			}
			if (givenName != null && !givenName.trim().equals("")) {
				if (patient.getGivenName().startsWith(givenName)) {
					if (!patientsResList.contains(patient)) {
						patientsResList.add(patient);
						setPatients(new ListDataModel<Patient>(patientsResList));
					}
				}
			}
			if (lastName != null && !lastName.trim().equals("")) {
				if (patient.getLastName().contains(lastName)) {
					if (!patientsResList.contains(patient)) {
						patientsResList.add(patient);
						setPatients(new ListDataModel<Patient>(patientsResList));
					}
				}
			}
		}
		return null;
	}

	public void setPatients(DataModel<Patient> patients) {
		this.patients = patients;
	}

	public DataModel<Patient> getPatients() {
		return patients;
	}

}
