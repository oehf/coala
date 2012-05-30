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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Manages and initializes the available locales of coala
 * @author astiefer
 */
@Component
@Scope("session")
public class LocaleHandler implements Serializable {

	private static final long serialVersionUID = 1L;
	private String locale;

	
	/**
	 * Default constructor
	 * Sets the locale to the applications default locale
	 */
	public LocaleHandler() {
		locale = FacesContext.getCurrentInstance().getApplication()
				.getDefaultLocale().toString();
	}

	/**
	 * @return the available locales 
	 */
	public List<String> getLocales() {
		List<String> l = new ArrayList<String>();
		for (Iterator<Locale> it = FacesContext.getCurrentInstance()
				.getApplication().getSupportedLocales(); it.hasNext();) {
			l.add(it.next().toString());

		}
		return l;
	}

	/**
	 * Changes the locale
	 * @return 
	 */
	public String changeLocale() {
		FacesContext.getCurrentInstance().getViewRoot()
				.setLocale(new Locale(locale));
		return null;
	}

	/**
	 * @return the locale
	 */
	public String getLocale() {
		return locale;
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}
}
