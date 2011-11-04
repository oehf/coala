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
 * Simple Gender enumeration to have a representation for some HL7 variants to
 * express a gender code. [F (Female), M (Male), O (Other), U (Unknown), A
 * (Ambiguous) or N (Not applicable)].
 * 
 */
public enum Gender {
	EMPTY(""),
	UNKNOWN("U"),
	AMBIGUOUS("A"),
	NOT_APPLICABLE("N"),
	MALE("M"),
	FEMALE("F");
	
	private String code;
	
	Gender(String code) {
		this.code = code;
	}
	
	/**
	 * Returns a {@link Gender} for a given HL7 conform code letter. If the
	 * string is invalid {@link Gender#EMPTY} is returned.
	 * 
	 * @param code
	 *            May not be null.
	 * @return The matchign {@link Gender} instance, else {@link Gender#EMPTY}.
	 */
	public static Gender fromString(String code) {
		for(Gender g: Gender.values()) {
			if(g.code.equals(code)) {
				return g;
			}
		}
		return EMPTY;
	}
	
	@Override
	public String toString() {
		return this.code;
	}
}
