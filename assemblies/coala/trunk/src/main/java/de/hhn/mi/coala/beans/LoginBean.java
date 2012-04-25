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
import java.security.PrivilegedAction;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.persistence.Transient;
import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.icw.ehf.authentication.principal.RolePrincipal;
import com.icw.ehf.authentication.principal.SessionPrincipal;
import com.icw.ehf.authentication.principal.UserPrincipal;
import com.icw.ehf.commons.uuid.UUIDFactory;
import com.icw.ehf.usermgnt.domain.User;
import com.icw.ehf.usermgnt.domain.UserIdentifier;
import com.icw.ehf.usermgnt.domain.UserSecret;
import com.icw.ehf.usermgnt.passwd.PasswordDigest;
import com.icw.ehf.usermgnt.service.UserService;

import de.hhn.mi.coala.constants.IntegrationConstants;

/**
 * Represents a managed Login bean component for a user and its password.
 * 
 * @author mkuballa, astiefer, mwiesner, nbougatf
 */
@Component
@Scope("request")
public class LoginBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(LoginBean.class);

	private String username;
	private String password;
	
	/*
	 * Username of active (=logged in) user for later display if login was
	 * successful.
	 */
	private String activeUser;
	
	/*
	 * EHF user management service layer
	 */
	@Transient
    @Autowired
    private UserService userService;

	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param activeUser the activeUser to set
	 */
	public void setActiveUser(String activeUser) {
		this.activeUser = activeUser;
	}

	/**
	 * @return the activeUser
	 */
	public String getActiveUser() {
		activeUser = "unknown";
		if(userService !=null) {
			activeUser = userService.findCurrent(false).getIdentifier(UserIdentifier.TYPE_USERNAME).getValue();
		}
		return activeUser;
	}

	/**
	 * Method to validate login parameters j_username and j_password as a group.
	 * This is needed to have proper error notification on failed credentials at
	 * login view, if user provides non matching (invalid) input.
	 * 
	 * @param event
	 */
	public void validateLoginParameters(ComponentSystemEvent event) {
		FacesContext fc = FacesContext.getCurrentInstance();

		UIComponent components = event.getComponent();

		// get textbox1 value
		UIInput j_usernameInput = (UIInput) components.findComponent("j_username");
		String j_username = j_usernameInput.getLocalValue().toString();

		// get textbox2 value
		UIInput j_passwordInput = (UIInput) components.findComponent("j_password");
		String j_password = j_passwordInput.getLocalValue().toString();

		boolean emptyCredentials = false;
		if (j_usernameInput.getLocalValue() == null
				|| j_usernameInput.getLocalValue().toString().trim().isEmpty()) {
			FacesMessage msg = new FacesMessage("Please provide non empty username.","");
			msg.setSeverity(FacesMessage.SEVERITY_WARN);
			fc.addMessage(j_usernameInput.getClientId(), msg);
			emptyCredentials = true;
		}
		if (j_passwordInput.getLocalValue() == null
				|| j_passwordInput.getLocalValue().toString().trim().isEmpty()) {
			FacesMessage msg = new FacesMessage("Please provide non empty password","");
			msg.setSeverity(FacesMessage.SEVERITY_WARN);
			fc.addMessage(j_passwordInput.getClientId(), msg);
			emptyCredentials = true;
		}
		if(emptyCredentials) {
			// passed to the Render Response phase
			fc.renderResponse();
		}
		else {
			
			if(validCredentials(j_username, j_password)) {
				// validation is OK -> no action to stop login process
				// action should now proceed to patientSearch.xhtml :-)
			}
			else {
				LOG.warn("Invalid login attempt for username: " + j_username
						+ " due to invalid credentials.");
				FacesMessage msg = new FacesMessage("Login failed.",
						"Please provide valid user credentials.");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				// components.getClientId() = textPanel
				fc.addMessage(components.getClientId(), msg);
				// passed to the Render Response phase
				fc.renderResponse();
			}
		}
	}

	/**
	 * Helper method to check the given login credentials against EHF
	 * UserManagementService for validity.
	 */
	private boolean validCredentials(String j_username, String j_password) {
		final UserIdentifier ui = new UserIdentifier(UserIdentifier.TYPE_USERNAME, j_username);
		Subject subject = createSubject();
		PrivilegedAction<User> action = new PrivilegedAction<User>(){
			public User run(){
				return userService.findByIdentifier(ui, false);
			}
		};
		final User u = Subject.doAsPrivileged(subject, action, null);
		/*
		 *  if user can not be found -> skip further processing and return false
		 */
		if(u==null) {
			return false;
		}
		else {
			final String uPW = j_password;
			Subject subject2 = createSubject();
			PrivilegedAction<Boolean> action2 = new PrivilegedAction<Boolean>(){
				public Boolean run(){
					PasswordDigest pwDigest = new PasswordDigest(UserSecret.DIGEST_SHA1);
					String digestedPW = pwDigest.digest(uPW);
					char[] secret = digestedPW.toCharArray();
					int[] positions = new int[secret.length];
					for(int i=0; i<secret.length; i++) {
						positions[i] = i;
					}
					return userService.verifySecret(u.getGuid(), 
							UserSecret.PASSWORD,positions,secret);
				}
			};
			return Subject.doAsPrivileged(subject2, action2, null);			
		}
	}
	
    /**
     * Creates a subject for internal testing
     * @return Creates a subject for internal testing
     */
    private Subject createSubject() {
        Subject user1 = new Subject();
        UserPrincipal userPrincipal = new UserPrincipal(IntegrationConstants.REGADMIN_ID);
        RolePrincipal userRolePrincipal = new RolePrincipal(IntegrationConstants.ROLE_USR_ID,
                IntegrationConstants.ROLE_USR_ID);
        RolePrincipal adminRolePrincipal = new RolePrincipal(IntegrationConstants.ROLE_ADM_ID,
                IntegrationConstants.ROLE_ADM_ID);
        SessionPrincipal sessionPrincipal = new SessionPrincipal(UUIDFactory.createUUID());
        user1.getPrincipals().add(userPrincipal);
        user1.getPrincipals().add(userRolePrincipal);
        user1.getPrincipals().add(adminRolePrincipal);
        user1.getPrincipals().add(sessionPrincipal);
        return user1;
    }

	/**
	 * Logs out the active user. If successful the logout-process is redirected
	 * to the Coala-Logout page via the EHF-LogoutFilter (webgui/bye.xhtml).
	 * 
	 * @return Always returns logoutPerformed.
	 */
	public String logout() {
		LOG.info("Logging out " + getActiveUser());

		FacesContext fc = FacesContext.getCurrentInstance();
		// say goodbye via eHF URL for "invalidating" everything in terms of
		// Spring context handlers
		try {
			ExternalContext context = fc.getExternalContext();
			context.invalidateSession();
			context.redirect(context.getRequestContextPath()
					+ "/webgui/bye.xhtml");
		} catch (Throwable e) {
			LOG.error("Exception during logout(): ", e);
		}
		return "logoutPerformed";
	}


}
