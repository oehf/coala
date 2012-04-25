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
package de.hhn.mi.coala.interfacing;

import de.hhn.mi.coala.domain.FindPatientQuery;
import de.hhn.mi.coala.domain.FindPatientResult;
import de.hhn.mi.coala.domain.Patient;
import de.hhn.mi.coala.domain.PatientSortParameter;
import de.hhn.mi.coala.exception.PDQRequestFailedException;


/**
 * This interface models the communication between assembly and communication module.
 * 
 * @author kmaerz
 * 
 */
public interface PXSPatientService {

	/**
	 * Returns a list of results from the MPI with {@link Patient} instances
	 * that match the query.
	 * 
	 * @param query
	 *            the {@link FindPatientQuery} to send to the MPI
	 * @param sortBy
	 *            The parameter by which the results shall be sorted. If this is
	 *            null, results will be sorted by a default as defined in
	 *            {@link PatientSortParameter}.
	 * @return the sorted {@link FindPatientResult} from the MPI with patients that match the query.
	 */
	public FindPatientResult findPatients(FindPatientQuery query,
			PatientSortParameter sortBy) throws PDQRequestFailedException;
}
