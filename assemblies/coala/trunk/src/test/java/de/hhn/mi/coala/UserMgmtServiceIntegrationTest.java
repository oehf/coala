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
package de.hhn.mi.coala;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.security.PrivilegedAction;

import javax.security.auth.Subject;

import junit.framework.JUnit4TestAdapter;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.icw.ehf.authentication.principal.RolePrincipal;
import com.icw.ehf.authentication.principal.SessionPrincipal;
import com.icw.ehf.authentication.principal.UserPrincipal;
import com.icw.ehf.commons.uuid.UUIDFactory;
import com.icw.ehf.usermgnt.domain.Person;
import com.icw.ehf.usermgnt.domain.User;
import com.icw.ehf.usermgnt.domain.UserIdentifier;
import com.icw.ehf.usermgnt.domain.UserSecret;
import com.icw.ehf.usermgnt.passwd.PasswordDigest;
import com.icw.ehf.usermgnt.service.PersonService;
import com.icw.ehf.usermgnt.service.UserService;

import de.hhn.mi.coala.constants.IntegrationConstants;

/**
 * Tests the integration of the ehf user management service in coala assembly
 * 
 * @author kmaerz, mnachtma, mwiesner
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/ehf-system-context.xml"})
public class UserMgmtServiceIntegrationTest {

	private static final Log LOG = LogFactory.getLog(UserMgmtServiceIntegrationTest.class);
	
    @Autowired 
    private UserService userService;
    
    @Autowired
    private PersonService personService;

    private final String[] teamUsernames = { "astiefer", "mnachtma", "wkais",
			"kmaerz", "hhein", "siekmann", "mkuballa", "ckarmen", "nbougatf",
			"mwiesner", "nbougatf", "tidris", "bmehner" };
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testUserServiceNotNull(){
        assertNotNull (userService); 
    }
    
    @Test
    public void testPersonServiceNotNull(){
    	assertNotNull (personService); 
    }
    
	/**
	 * Tests if all team members are found as {@link User} in the bootstrapped
	 * DB via UserService access.
	 * 
	 * @throws Exception
	 */
    @Test
    public void testFindUserByName() throws Exception {
    	for(String username: teamUsernames) {
    		final UserIdentifier ui = new UserIdentifier(UserIdentifier.TYPE_USERNAME, username);
    		Subject subject = createSubject();
    		PrivilegedAction<User> action = new PrivilegedAction<User>(){
    			public User run(){
    				return userService.findByIdentifier(ui, false);
    			}
    		};
    		User u = Subject.doAsPrivileged(subject, action, null);
    		
    		assertNotNull(u);
			// as we bootstrapped PWs that are equal to the ldap username, we
			// can simply check with the same string literal
    		String pw = username;
    		assertEquals(pw, u.getIdentifier(UserIdentifier.TYPE_USERNAME).getValue());
    	}
    }
    
	/**
	 * Tests if all team members are found as {@link Person} in the bootstrapped
	 * DB via PersonService access.
	 * 
	 * @throws Exception
	 */
    @Test
    public void testFindPersonByName() throws Exception {
    	for(String username: teamUsernames) {
    		final UserIdentifier ui = new UserIdentifier(UserIdentifier.TYPE_USERNAME, username);
    		Subject subject = createSubject();
    		PrivilegedAction<Person> action = new PrivilegedAction<Person>(){
    			public Person run(){
    				return personService.findByUserIdentifier(ui, false);
    			}
    		};
    		final Person p = Subject.doAsPrivileged(subject, action, null);
    		assertNotNull(p);
    		assertTrue(!p.getFirstName().trim().isEmpty());
    		assertTrue(!p.getLastName().trim().isEmpty());
    	}
    }
    
	/**
	 * Tests if all team members (usernames) can be verified with their
	 * corresponding password!
	 * 
	 * @throws Exception
	 */
    @Test
    public void testVerifyUserSecrets() throws Exception {
    	for(String username: teamUsernames) {
    		final UserIdentifier ui = new UserIdentifier(UserIdentifier.TYPE_USERNAME, username);
    		Subject subject = createSubject();
    		PrivilegedAction<User> action = new PrivilegedAction<User>(){
    			public User run(){
    				return userService.findByIdentifier(ui, false);
    			}
    		};
    		final User u = Subject.doAsPrivileged(subject, action, null);
    		
    		final String uPW = username;
    		Subject subject2 = createSubject();
    		PrivilegedAction<Boolean> action2 = new PrivilegedAction<Boolean>(){
    			public Boolean run(){
    				LOG.debug("GUID: "+u.getGuid());
    				PasswordDigest pwDigest = new PasswordDigest(UserSecret.DIGEST_SHA1);
					String digestedPW = pwDigest.digest(uPW);
					LOG.debug("SHA1: "+digestedPW);
    				char[] secret = digestedPW.toCharArray();
    				int[] positions = new int[secret.length];
    				for(int i=0; i<secret.length; i++) {
    					positions[i] = i;
    				}
    				return userService.verifySecret(u.getGuid(), 
    						UserSecret.PASSWORD,positions,secret);
    			}
    		};
    		Boolean verified = Subject.doAsPrivileged(subject2, action2, null);
    		assertTrue(verified);
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

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(UserMgmtServiceIntegrationTest.class);
    }
    
}
