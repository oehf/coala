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
package de.hhn.mi.coala.util;

import java.util.Date;

/**
 * This class offers conversion Methods for PXS Dates. Define the patterns used
 * in coala-document.properties.
 * 
 * @author kmaerz
 */
public interface PXSDateConverter  {

	
	/**
	 * Converts a PXS date representation into an {@link Date}. The String needs
	 * to be at least the length of shortpattern (defined in
	 * coala-document.properties).
	 * 
	 * @param pxsDate
	 *            the pxs dates string. Must be at least the length of
	 *            shortpattern (defined in coala-document.properties) only
	 *            contain numbers
	 * @return A {@link Date} that represents the same time as the date string
	 */
	public Date stringToDate(String pxsDate); 

	/**
	 * Formats a {@link Date} into a String according to the PXS specification.
	 * The pattern can be specified in the coala-document.properties file.
	 * 
	 * @param pxsDate
	 *            the {@link Date} that needs to be converted to a String
	 * @return the String representing the given {@link Date}
	 */
	public String DateToString(Date pxsDate);

	/**
	 * Formats a {@link Date} into a String according to the PXS specification.
	 * The pattern can be specified in the coala-document.properties file. This
	 * Method uses the short string version, e.g. for birthDates
	 * 
	 * @param pxsDate
	 *            the {@link Date} that needs to be converted to a String
	 * @return the String representing the given {@link Date}
	 */
	public String DateToShortString(Date pxsDate);

}
