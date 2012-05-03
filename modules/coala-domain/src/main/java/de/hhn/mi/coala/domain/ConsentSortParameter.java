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
 * This simple enum is used to identify by which parameters a search should be sorted by.
 * The default SearchOrder to use if none is specified can be defined here as well.
 * 
 * IF UNSORTED is used, request ConsentSearchParameter.getDefault() to receive the default parameter.
 * 
 * @author kmaerz
 */
public enum ConsentSortParameter {
	UNSORTED(),
	START_DATE_NEWEST_FIRST(),
	START_DATE_OLDEST_FIRST(),
	END_DATE_NEWEST_FIRST(),
	END_DATE_OLDEST_FIRST(),
	CREATION_DATE_NEWEST_FIRST(),
	CREATION_DATE_OLDEST_FIRST(),
	POLICY_TYPE_ASCENDING(),
	POLICY_TYPE_DESCENDING(),
	OBSOLETE_TRUE_FIRST(),
	OBSOLETE_FALSE_FIRST(),
	EFFECTIVE_TRUE_FIRST(),
	EFFECTIVE_FALSE_FIRST(),
	AUTHOR_ASCENDING(),
	AUTHOR_DESCENDING();

	/**
	 * Returns the default Sort Order.
	 * @return the default Sort Order.
	 */
	public static ConsentSortParameter getDefault() {
		return CREATION_DATE_NEWEST_FIRST;
	}
}
