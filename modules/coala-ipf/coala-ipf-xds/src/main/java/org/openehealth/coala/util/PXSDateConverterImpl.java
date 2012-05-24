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
package org.openehealth.coala.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.openehealth.coala.xds.XDSConfigurationImpl;

/**
 * This class offers conversion Methods for PXS Dates. Define the patterns used
 * in coala-document.properties.
 * 
 * @author kmaerz
 */
public class PXSDateConverterImpl implements PXSDateConverter {

	private XDSConfigurationImpl xdsConfiguration;
	
	/**
	 * The pattern to use for Date formatting
	 */
	private static String longPattern = "yyyyMMddHHmmss";
	private static String shortPattern = "yyyyMMdd";

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
	public Date stringToDate(String pxsDate) {
		// Basic Checks
		if (pxsDate == null)
			throw new IllegalArgumentException("pxsDate cannot be null");
		if (pxsDate.length() < shortPattern.length())
			throw new IllegalArgumentException(
					"pxsDate must be at least eight digits long");
		if (!StringUtils.isNumeric(pxsDate))
			throw new IllegalArgumentException(
					"pxsDate must be strictly numeric");

		// Fill pxsDate with trailing zeroes if too short
		while (pxsDate.length() < longPattern.length())
			pxsDate += "0";

		try {
			SimpleDateFormat sdf = new SimpleDateFormat(longPattern,
					Locale.GERMAN);
			Date result = sdf.parse(pxsDate);
			return result;
		} catch (Exception e) {
			throw new RuntimeException(
					"Could not convert PXS Date representation to java Date. Invalid DateString was: "
							+ pxsDate, e);
		}

	}

	/**
	 * Formats a {@link Date} into a String according to the PXS specification.
	 * The pattern can be specified in the coala-document.properties file.
	 * 
	 * @param pxsDate
	 *            the {@link Date} that needs to be converted to a String
	 * @return the String representing the given {@link Date}
	 */
	public String DateToString(Date pxsDate) {
		SimpleDateFormat formatter = new SimpleDateFormat(longPattern);
		return formatter.format(pxsDate);
	}

	/**
	 * Formats a {@link Date} into a String according to the PXS specification.
	 * The pattern can be specified in the coala-document.properties file. This
	 * Method uses the short string version, e.g. for birthDates
	 * 
	 * @param pxsDate
	 *            the {@link Date} that needs to be converted to a String
	 * @return the String representing the given {@link Date}
	 */
	public String DateToShortString(Date pxsDate) {
		SimpleDateFormat formatter = new SimpleDateFormat(shortPattern);
		return formatter.format(pxsDate);
	}

	
	/*
	 * Needed for Spring
	 */
	public void setXdsConfiguration(XDSConfigurationImpl xdsConfiguration) {
		this.xdsConfiguration = xdsConfiguration;
		if (xdsConfiguration.getLongDatePattern() != null) PXSDateConverterImpl.longPattern = xdsConfiguration.getLongDatePattern();
		if (xdsConfiguration.getShortDatePattern() != null) PXSDateConverterImpl.shortPattern = xdsConfiguration.getShortDatePattern();
	}

	/*
	 * Needed for Spring
	 */
	public XDSConfigurationImpl getXdsConfiguration() {
		return xdsConfiguration;
	}

}
