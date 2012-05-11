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
package org.openehealth.coala.domain;

import java.io.Serializable;

/**
 * This class represents a patient address related to HL7 definition (XAD =
 * Extended Address, HL7 2.5 Chapter 2.A.85)
 * 
 * @author ckarmen, hhein
 * 
 */
public class PatientAddress implements Serializable {

	private static final long serialVersionUID = 1L;

	private String streetAddress = "";
	private String otherDesignation = "";
	private String city = "";
	private String stateOrProvince = "";
	private String zipOrPostalCode = "";
	private String country = "";
	private String addressType = "";
	private String otherGeographicDesignation = "";
	private String countryCode = "";
	private String censusTract = "";
	private String addressRepresentationCode = "";
	private String addressValidityRange = "";
	private String effectiveDate = "";
	private String expirationDate = "";
	
	/**
	 * Default constructor which has empty {@link String} instances for all
	 * class attributes after being called.
	 * 
	 */
	
	public PatientAddress() {
		this("","","");
	}
	
	/**
	 * 
	 * @param streetAddress
	 *            May not be null.
	 * @param city
	 *            May not be null.
	 * @param postalCode
	 *            May not be null.
	 * @throws IllegalArgumentException
	 *             Thrown if basic parameter are invalid.
	 */
	public PatientAddress(String streetAddress, String city, String postalCode) {
		if(streetAddress == null) {
			throw new IllegalArgumentException("Parameter streetAddress can not be null");
		}
		if(city == null) {
			throw new IllegalArgumentException("Parameter city can not be null");
		}
		if(postalCode == null) {
			throw new IllegalArgumentException("Parameter postalCode can not be null");
		}
		setStreetAddress(streetAddress);
		setCity(city);
		setZipOrPostalCode(postalCode);
	}
	

	/**
	 * @return the streetAddress
	 */
	public String getStreetAddress() {
		return streetAddress;
	}

	/**
	 * @param streetAddress
	 *            the streetAddress to set
	 */
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	/**
	 * @return the otherDesignation
	 */
	public String getOtherDesignation() {
		return otherDesignation;
	}

	/**
	 * @param otherDesignation
	 *            the otherDesignation to set
	 */
	public void setOtherDesignation(String otherDesignation) {
		this.otherDesignation = otherDesignation;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the stateOrProvince
	 */
	public String getStateOrProvince() {
		return stateOrProvince;
	}

	/**
	 * @param stateOrProvince
	 *            the stateOrProvince to set
	 */
	public void setStateOrProvince(String stateOrProvince) {
		this.stateOrProvince = stateOrProvince;
	}

	/**
	 * @return the zipOrPostalCode
	 */
	public String getZipOrPostalCode() {
		return zipOrPostalCode;
	}

	/**
	 * @param zipOrPostalCode
	 *            the zipOrPostalCode to set
	 */
	public void setZipOrPostalCode(String zipOrPostalCode) {
		this.zipOrPostalCode = zipOrPostalCode;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the addressType
	 */
	public String getAddressType() {
		return addressType;
	}

	/**
	 * @param addressType
	 *            the addressType to set
	 */
	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	/**
	 * @return the otherGeographicDesignation
	 */
	public String getOtherGeographicDesignation() {
		return otherGeographicDesignation;
	}

	/**
	 * @param otherGeographicDesignation
	 *            the otherGeographicDesignation to set
	 */
	public void setOtherGeographicDesignation(String otherGeographicDesignation) {
		this.otherGeographicDesignation = otherGeographicDesignation;
	}

	/**
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * @param countryCode
	 *            the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * @return the censusTract
	 */
	public String getCensusTract() {
		return censusTract;
	}

	/**
	 * @param censusTract
	 *            the censusTract to set
	 */
	public void setCensusTract(String censusTract) {
		this.censusTract = censusTract;
	}

	/**
	 * @return the addressRepresentationCode
	 */
	public String getAddressRepresentationCode() {
		return addressRepresentationCode;
	}

	/**
	 * @param addressRepresentationCode
	 *            the addressRepresentationCode to set
	 */
	public void setAddressRepresentationCode(String addressRepresentationCode) {
		this.addressRepresentationCode = addressRepresentationCode;
	}

	/**
	 * @return the addressValidityRange
	 */
	public String getAddressValidityRange() {
		return addressValidityRange;
	}

	/**
	 * @param addressValidityRange
	 *            the addressValidityRange to set
	 */
	public void setAddressValidityRange(String addressValidityRange) {
		this.addressValidityRange = addressValidityRange;
	}

	/**
	 * @return the effectiveDate
	 */
	public String getEffectiveDate() {
		return effectiveDate;
	}

	/**
	 * @param effectiveDate
	 *            the effectiveDate to set
	 */
	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	/**
	 * @return the expirationDate
	 */
	public String getExpirationDate() {
		return expirationDate;
	}

	/**
	 * @param expirationDate
	 *            the expirationDate to set
	 */
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((addressRepresentationCode == null) ? 0
						: addressRepresentationCode.hashCode());
		result = prime * result
				+ ((addressType == null) ? 0 : addressType.hashCode());
		result = prime
				* result
				+ ((addressValidityRange == null) ? 0 : addressValidityRange
						.hashCode());
		result = prime * result
				+ ((censusTract == null) ? 0 : censusTract.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result
				+ ((countryCode == null) ? 0 : countryCode.hashCode());
		result = prime * result
				+ ((effectiveDate == null) ? 0 : effectiveDate.hashCode());
		result = prime * result
				+ ((expirationDate == null) ? 0 : expirationDate.hashCode());
		result = prime
				* result
				+ ((otherDesignation == null) ? 0 : otherDesignation.hashCode());
		result = prime
				* result
				+ ((otherGeographicDesignation == null) ? 0
						: otherGeographicDesignation.hashCode());
		result = prime * result
				+ ((stateOrProvince == null) ? 0 : stateOrProvince.hashCode());
		result = prime * result
				+ ((streetAddress == null) ? 0 : streetAddress.hashCode());
		result = prime * result
				+ ((zipOrPostalCode == null) ? 0 : zipOrPostalCode.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
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
		PatientAddress other = (PatientAddress) obj;
		if (addressRepresentationCode == null) {
			if (other.addressRepresentationCode != null)
				return false;
		} else if (!addressRepresentationCode
				.equals(other.addressRepresentationCode))
			return false;
		if (addressType == null) {
			if (other.addressType != null)
				return false;
		} else if (!addressType.equals(other.addressType))
			return false;
		if (addressValidityRange == null) {
			if (other.addressValidityRange != null)
				return false;
		} else if (!addressValidityRange.equals(other.addressValidityRange))
			return false;
		if (censusTract == null) {
			if (other.censusTract != null)
				return false;
		} else if (!censusTract.equals(other.censusTract))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (countryCode == null) {
			if (other.countryCode != null)
				return false;
		} else if (!countryCode.equals(other.countryCode))
			return false;
		if (effectiveDate == null) {
			if (other.effectiveDate != null)
				return false;
		} else if (!effectiveDate.equals(other.effectiveDate))
			return false;
		if (expirationDate == null) {
			if (other.expirationDate != null)
				return false;
		} else if (!expirationDate.equals(other.expirationDate))
			return false;
		if (otherDesignation == null) {
			if (other.otherDesignation != null)
				return false;
		} else if (!otherDesignation.equals(other.otherDesignation))
			return false;
		if (otherGeographicDesignation == null) {
			if (other.otherGeographicDesignation != null)
				return false;
		} else if (!otherGeographicDesignation
				.equals(other.otherGeographicDesignation))
			return false;
		if (stateOrProvince == null) {
			if (other.stateOrProvince != null)
				return false;
		} else if (!stateOrProvince.equals(other.stateOrProvince))
			return false;
		if (streetAddress == null) {
			if (other.streetAddress != null)
				return false;
		} else if (!streetAddress.equals(other.streetAddress))
			return false;
		if (zipOrPostalCode == null) {
			if (other.zipOrPostalCode != null)
				return false;
		} else if (!zipOrPostalCode.equals(other.zipOrPostalCode))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PatientAddress [StreetAddress=" + streetAddress
				+ ", OtherDesignation=" + otherDesignation + ", City=" + city
				+ ", StateOrProvince=" + stateOrProvince + ", ZipOrPostalCode="
				+ zipOrPostalCode + ", Country=" + country + ", AddressType="
				+ addressType + ", OtherGeographicDesignation="
				+ otherGeographicDesignation + ", CountryCode=" + countryCode
				+ ", CensusTract=" + censusTract
				+ ", AddressRepresentationCode=" + addressRepresentationCode
				+ ", AddressValidityRange=" + addressValidityRange
				+ ", EffectiveDate=" + effectiveDate + ", ExpirationDate="
				+ expirationDate + "]";
	}

}
