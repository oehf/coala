package beans;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletResponse;

import org.apache.myfaces.shared_impl.view.HttpServletResponseSwitch;


/**
 * An object of this class represents a Managed Bean of an user and its password.
 * @author mkuballa, astiefer
 */
@ManagedBean
@SessionScoped
public class LoginBean {
	
	private String username;
	private String password;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	
	/**
	 * Tries to login the user. If username as well as password are
	 * correctly spelled this method returns the PatientSearch-Site, 
	 * if not the Login-Failed Site will be returned.
	 * @return correct login: PatientSearch, else LoginFailed
	 * @throws Exception 
	 */
	public String login() throws Exception{	
//		FacesContext fc = FacesContext.getCurrentInstance().getExternalContext().getResponse();
//		HttpServletResponse resp = (HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();
//		resp.
//		fc.getMessages().
//		throw new Exception();
		//TODO: facesContext - register new Error
		try {
				LoginContext lc = new LoginContext("Test");
				lc.login();
			} 
		catch (LoginException e) {
				e.printStackTrace();
			}
		finally{
			return "/errorPage.xhtml";
		}
//		File f = null;
//		f.getName();
			
//		if(findUser(username, password)){
//			return "loginAccepted";
//		}
//		else{
//			return "loginDenied";
//		}
//		if(findUser(username, password)){
//			return "/patientSearch.xhtml";			
//		}
//		else{
//			return "/loginFalse.xhtml";	
//		}
	}
	
	public String logout(){
		username = null;
		password = null;
		return "/login.xhtml";
	}
	
	public boolean findUser(String username, String password){
		if(username.equals("Marco") && password.equals("123")){
			return true;
		}
		else{
			return false;
		}
	}
	
}
