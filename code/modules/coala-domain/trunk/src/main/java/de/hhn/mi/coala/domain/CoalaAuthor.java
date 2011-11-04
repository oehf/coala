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

import java.io.Serializable;

/**
 * Simple carrier class that conveys data relevant to an author
 * 
 * @author kmaerz
 */
public class CoalaAuthor implements Serializable{

	private static final long serialVersionUID = 1L;
	private String title;
	private String givenName;
	private String familyName;
	
	/**
	 * Creates a new Author. the Data herein will show up in the CDA documents
	 * 
	 * @param title the author's title
	 * @param givenName the author's given name
	 * @param familyName the author's last name 
	 */
	public CoalaAuthor(String title, String givenName, String familyName){
		
		if ( (givenName == null) || (givenName.equals("")) ) 
			throw new IllegalArgumentException("The givenName parameter of a CoalaAuthor can not be null or empty, but it was!");
		if ( (familyName == null) || (familyName.equals("")) ) 
			throw new IllegalArgumentException("The familyName parameter of a CoalaAuthor can not be null or empty, but it was!");

		if ( title == null) title = "";
		
		this.title = title;
		this.givenName = givenName;
		this.familyName = familyName;
	}
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((givenName == null) ? 0 : givenName.hashCode());
		result = prime * result
				+ ((familyName == null) ? 0 : familyName.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CoalaAuthor other = (CoalaAuthor) obj;
		if (givenName == null) {
			if (other.givenName != null)
				return false;
		} else if (!givenName.equals(other.givenName))
			return false;
		if (familyName == null) {
			if (other.familyName != null)
				return false;
		} else if (!familyName.equals(other.familyName))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}



	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @return the givenName
	 */
	public String getGivenName() {
		return givenName;
	}
	/**
	 * @return the lastName
	 */
	public String getFamilyName() {
		return familyName;
	}
	
	@Override
	public String toString(){
		return title + " " + familyName + ", " +  givenName;
	}



	public int compareTo(CoalaAuthor other) {
		return this.getFamilyName().compareTo(other.getFamilyName());
	}

	
	
}
