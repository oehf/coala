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
package de.hhn.mi.coala.domain;

/**
 * This Enum models the policy that the patient chose to adapt when he signed the paper. 
 * 
 * @author kmaerz
 */
public enum PatientConsentPolicy {
    ONE  (1,"Publish","1.2.840.113619.20.2.9.1",true,false,false),
    TWO  (2,"No Publish or Share","1.2.840.113619.20.2.9.2", false, false, false),
    THREE(3,"Publish with Override","1.2.840.113619.20.2.9.3", true, false, true),
    FOUR (4,"Publish and Share","1.2.840.113619.20.2.9.4", true, true, false),
    FIVE (5,"Publish and Share with Override","1.2.840.113619.20.2.9.5", true, true, true);
    
    private int number;
    private String consentCode;
    private String shortName;
    private boolean publish;
    private boolean share;
    private boolean override;
    
    /**
     * Creates a new ConsentPolicy
     * @param number The number, corresponding to the Use Case description
     * @param display The Text to display explaining the type of of policy
     * @param consentCode The OID code which corresponds to a {@link PatientConsentPolicy}.
     * @param publish True, if publishing of data should be allowed, false otherwise
     * @param share true, if sharing of data should be allowed, false otherwise
     * @param override true, if emergency override is allowed, false otherwise
     */
    PatientConsentPolicy(int number, String display, String consentCode, boolean publish, boolean share, boolean override){
        this.number = number;
        this.shortName = display;
        this.consentCode = consentCode;
        this.publish = publish;
        this.share = share;
        this.override = override;
    }

    /**
     * Returns this policy's consentCode
     * @return this policy's consentCode
     */
	public PatientConsentPolicy getPolicyType() {
		return getPolicyType(this.consentCode);
	}
    
	/**
	 * This Method converts a policyCode to a PolicyType
	 * @param consentCode the code to convert
	 * @return the PolicyType corresponding to the code
	 */
	public static PatientConsentPolicy getPolicyType(String consentCode) {
		
		if (consentCode.equals(ONE.getCode()))
			return PatientConsentPolicy.ONE;
		else if (consentCode.equals(TWO.getCode()))
			return PatientConsentPolicy.TWO;
		else if (consentCode.equals(THREE.getCode()))
			return PatientConsentPolicy.THREE;
		else if (consentCode.equals(FOUR.getCode()))
			return PatientConsentPolicy.FOUR;
		else if (consentCode.equals(FIVE.getCode()))
			return PatientConsentPolicy.FIVE;
		else
			throw new IllegalArgumentException(
					"Received an invalid ConsentPolicyCode. OID code set was: '"
							+ consentCode + "'.");
	}
    
    /**
     * A numerical representation of the enum
     * @return A numerical representation of the enum
     */
    public int getNumber() {
        return number;
    }
    
    /**
     * The text to be displayed when showing consents to a user.
     * @return The text to be displayed when showing consents to a user.
     */
    public String getShortName() {
        return shortName;
    }
    
    /**
     * True, if this consent type allows the publishing of patient data, false otherwise.
     * @return True, if this consent type allows the publishing of patient data, false otherwise.
     */
    public boolean allowsPublishing() {
        return publish;
    }
    
    /**
     * True, if this consent type allows the sharing of patient data, false otherwise.
     * @return True, if this consent type allows the sharing of patient data, false otherwise.
     */
    public boolean allowsSharing() {
        return share;
    }
    
    /**
     * True, if this consent type allows overriding in ace of emergency, false otherwise.
     * @return True, if this consent type allows overriding in ace of emergency, false otherwise.
     */
    public boolean allowsOverriding() {
        return override;
    }
    
    /**
     * Returns the IHE-Code for this policy
     * @return the IHE-Code for this policy
     */
    public String getCode(){
    	return consentCode;
    }

}
