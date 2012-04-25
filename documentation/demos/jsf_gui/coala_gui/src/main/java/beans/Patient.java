package beans;
import java.util.ArrayList;
import java.util.Date;


public class Patient {

	private String id;
	private String givenName;
	private String lastName;
	private Date birthDate;
	public Date getBirthDate() {
		return birthDate;
	}


	private ArrayList<Patient> patientList;
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


	public ArrayList<Patient> getPatientList() {
		return patientList;
	}


	public void setPatientList(ArrayList<Patient> patientList) {
		this.patientList = patientList;
	}


	
	
	public Patient(String id, String givenName, String lastName, Date birthDate){
		setId(id);
		setGivenName(givenName);
		setLastName(lastName);
		setBirthDate(birthDate);
//		addPatients();
	}


	private void setBirthDate(Date birthDate2) {
		birthDate = birthDate2;
	}
	
//	public void addPatients(){
//		patientList.add(new Patient("1", "Martin", "Wiesner"));
//		patientList.add(new Patient("2", "Marco", "Kuballa"));
//		patientList.add(new Patient("3", "Hans", "Müller"));
//		patientList.add(new Patient("4", "Karl", "Müller"));
//		patientList.add(new Patient("5", "Karl", "Schmidt"));
//	}
	
//	public ArrayList<Patient> getList(){
//		return patientList;
//	}
}
