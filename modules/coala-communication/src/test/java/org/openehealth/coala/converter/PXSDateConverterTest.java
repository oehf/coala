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
package org.openehealth.coala.converter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openehealth.coala.util.PXSDateConverter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests the converter for correctness. This Test assumes the default pattern.
 * If this is changed, please update the test accordingly
 * 
 * @author kmaerz
 *
 */
public class PXSDateConverterTest {

	private String longPattern;
	private String shortPattern;
	private Date referenceDateLong;
	private Date referenceDateShort;
	private String referenceStringLong = "20110115033650";
	private String referenceStringShort = "20110115";
	
	@Autowired
	private PXSDateConverter pxsDateConverter;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		ResourceBundle properties = ResourceBundle.getBundle("coala-document");
		longPattern = properties.getString("coala.consent.longdatepattern");
		shortPattern = properties.getString("coala.consent.shortdatepattern");
		
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Calendar.YEAR, 2011);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DAY_OF_MONTH, 15);
		cal.set(Calendar.HOUR, 3);
		cal.set(Calendar.MINUTE, 36);
		cal.set(Calendar.SECOND, 50);
		cal.set(Calendar.MILLISECOND, 0);
		referenceDateLong = cal.getTime();
		
		cal = new GregorianCalendar();
		cal.set(Calendar.YEAR, 2011);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DAY_OF_MONTH, 15);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		referenceDateShort = cal.getTime();
		
	}
	
	/**
	 * Make sure the pattern is stored in the config correctly
	 */
	@Test
	public void testConfig(){
		assertTrue(longPattern != null);
		assertFalse(longPattern.equals(""));
		assertTrue(shortPattern != null);
		assertFalse(shortPattern.equals(""));
	}

	/**
	 * Tests for correct conversion from String to Date
	 * Test method for {@link org.openehealth.coala.util.PXSDateConverter#stringToDate(java.lang.String)}.
	 */
	@Test
	@Ignore
	public void testStringToDate() {
		
		Date pxsDate = pxsDateConverter.stringToDate(referenceStringLong);
		assertTrue (pxsDate.getTime() == referenceDateLong.getTime());
		
		
		pxsDate = pxsDateConverter.stringToDate(referenceStringShort);
		assertTrue (pxsDate.equals(referenceDateShort));
	}

	/**
	 * Tests for correct conversion from Date to String
	 * Test method for {@link org.openehealth.coala.util.PXSDateConverter#DateToString(java.util.Date)}.
	 */
	@Test
	@Ignore
	public void testDateToString() {
		
		String s = pxsDateConverter.DateToString(referenceDateLong);
		assertTrue (s.equals(referenceStringLong));
	}
	
	/**
	 * Tests for correct conversion from Date to String
	 * Test method for {@link org.openehealth.coala.util.PXSDateConverter#DateToString(java.util.Date)}.
	 */
	@Test
	@Ignore
	public void testDateToShortString() {
		
		String s = pxsDateConverter.DateToShortString(referenceDateLong);
		assertTrue (s.equals(referenceStringShort));
	}

}
