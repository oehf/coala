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
package org.openehealth.coala.xds;

/**
 * This class is the base class to encapsulates all configuration details
 * (endpoints and OID of the assigning authority) for the XDSGate. This class
 * does not check any of its values and is only used to hold the data.
 * 
 * @author siekmann
 * 
 */
public abstract class XDSBaseConfiguration {
	protected String assigningAuthorityOID;

	public XDSBaseConfiguration() {
		assigningAuthorityOID = "";
	}

	/**
	 * Returns the OID of the assigning authority
	 * 
	 * @return the assigningAuthorityOID
	 */
	public String getAssigningAuthorityOID() {
		return assigningAuthorityOID;
	}

	/**
	 * Set the OID of the assigning authority
	 * 
	 * @param assigningAuthorityOID
	 *            the assigningAuthorityOID to set
	 */
	public void setAssigningAuthorityOID(String assigningAuthorityOID) {
		this.assigningAuthorityOID = assigningAuthorityOID;
	}

}
