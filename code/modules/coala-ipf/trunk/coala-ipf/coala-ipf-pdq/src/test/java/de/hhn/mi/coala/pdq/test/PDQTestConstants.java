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
package de.hhn.mi.coala.pdq.test;

public interface PDQTestConstants {

	String VALID_REQUEST_HEADER = "MSH|^~\\&|OHFConsumer|OHFFacility|OTHER_KIOSK|HIMSSSANDIEGO|20070108145322-0800||"
		+ "QBP^Q22|9416994431147258002|P|2.5|\nQPD|Q22^Find Candidates|"
		+ "7891956360974608557281601076319|@PID.5.1^M*|\nRCP|I|2^RD";
	
	String INVALID_REQUEST_HEADER = "MSH|^~\\&||HIMSSSANDIEGO|0||"
		+ "QBP^Q22|9416994431147258002|P|2.5|\n|"
		+ "7891956360974608557281601076319|@PID.5.1^M*|\nRCP|I|2^RD";
}
