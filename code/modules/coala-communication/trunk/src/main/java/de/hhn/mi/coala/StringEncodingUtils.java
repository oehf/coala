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
package de.hhn.mi.coala;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;

/**
 * Helper class to convert between different encodings of {@link String}
 * instances.
 * 
 * @author mwiesner
 * 
 * FIXME move this to an utils package. Consider using a seperate tools project for things like this.
 */
public class StringEncodingUtils {

	private static final Charset utf8charset = Charset.forName("UTF-8");
	private static final Charset iso88591charset = Charset.forName("ISO-8859-1");

	/**
	 * Converts a UTF-8 encoded {@link String} to ISO-LATIN-1 bytes via a ByteBuffer.
	 * 
	 * @param inUTF8
	 *            Any {@link String} that was created holding UTF-8 characters.
	 * @return A {@link String} holding <code>inUTF8</code> characters coded as
	 *         ISO-8859-1.
	 * @throws CharacterCodingException 
	 */
	public static String convertUTF8ToISO (String inUTF8) throws CharacterCodingException {

		ByteBuffer inputBuffer = utf8charset.newEncoder().encode(CharBuffer.wrap(inUTF8));

		// decode UTF-8
		CharBuffer data = utf8charset.decode(inputBuffer);

		// encode ISO-8559-1
		ByteBuffer outputBuffer = iso88591charset.encode(data);
		byte[] outputData = outputBuffer.array();
		return new String(outputData);
	}
	
	/**
	 * Converts a ISO-LATIN-1 encoded {@link String} to UTF-8 bytes via a ByteBuffer.
	 * 
	 * @param inISO
	 *            Any {@link String} that was created holding ISO-LATIN-1 characters.
	 * @return A {@link String} holding <code>inISO</code> characters coded as
	 *         UTF-8.
	 * @throws CharacterCodingException 
	 */
	public static String convertISOToUTF8 (String inISO) throws CharacterCodingException {
		
		ByteBuffer inputBuffer = iso88591charset.newEncoder().encode(CharBuffer.wrap(inISO));
		
		// decode ISO-8559-1
		CharBuffer data = iso88591charset.decode(inputBuffer);
		
		// encode UTF-8
		ByteBuffer outputBuffer = utf8charset.encode(data);
		byte[] outputData = outputBuffer.array();
		return new String(outputData);
	}
}
