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
 * This enum defines all the Sort parameters a Patient is sortable by.
 * 
 * @author kmaerz
 *
 */
public enum PatientSortParameter {
	
	UNSORTED(),
	PID_ASCENDING(),
	PID_DESCENDING(),
	GIVENNAME_ASCENDING(),
	GIVENNAME_DESCENDING(),
	LASTNAME_DESCENDING(),
	LASTNAME_ASCENDING(),
	BIRTHDATE_NEWEST_FIRST(),
	BIRTHDATE_OLDEST_FIRST();

	/**
	 * Returns the default Sort Order.
	 * @return the default Sort Order.
	 */
	public static PatientSortParameter getDefault() {
		return LASTNAME_ASCENDING;
	}
}
