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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.activation.DataSource;



/**
 * This utility class provides a means to stream xml content to a DocumentHandler.
 * 
 * @author kmaerz
 */
public class CdaDataSourceImpl implements DataSource{

	private InputStream stream;
	
	public CdaDataSourceImpl (String xml){
		if ( (xml == null) || (xml.isEmpty()) ) throw new IllegalArgumentException("The xml content can be neither null nor empty");
		stream = new ByteArrayInputStream( xml.getBytes( Charset.defaultCharset() ) );
	}
	
	@Override
	public InputStream getInputStream() throws IOException {
		return stream;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		throw new UnsupportedOperationException("A CdaDataSource cannot output any data.");
	}

	@Override
	public String getContentType() {
		return "text/xml";
	}

	@Override
	public String getName() {
		return "A Patient Consent";
	}

}
