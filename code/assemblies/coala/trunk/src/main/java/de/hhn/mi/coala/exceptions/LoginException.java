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
package de.hhn.mi.coala.exceptions;


/**
 * This exception is thrown whenever a technical error (that is not related to username/password mismatches) occurs 
 * 
 * @author kmaerz, mnachtma
 */
//TODO If unused: remove, if used: copy to domain module
@Deprecated
public class LoginException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
     * This exception is thrown whenever a technical error (that is not related to username/password mismatches) occurs
     * Note: Do ONLY use for system failures like DB not found and NOT for user not found etc (mind Security!)
     * Also, enclose the original exception or write a meaningful message.
     */
    public LoginException() {
    }

    /**
     * This exception is thrown whenever a technical error (that is not related to username/password mismatches) occurs
     * Note: Do ONLY use for system failures like DB not found and NOT for user not found etc (mind Security!)
     * Also, enclose the original exception or write a meaningful message.
     * 
     * @param message
     */
    public LoginException(String message) {
        super(message);
    }

    /**
     * This exception is thrown whenever a technical error (that is not related to username/password mismatches) occurs
     * Note: Do ONLY use for system failures like DB not found and NOT for user not found etc (mind Security!)
     * Also, enclose the original exception or write a meaningful message.
     * 
     * @param cause
     */
    public LoginException(Throwable cause) {
        super(cause);
    }

    /**
     * This exception is thrown whenever a technical error (that is not related to username/password mismatches) occurs
     * Note: Do ONLY use for system failures like DB not found and NOT for user not found etc (mind Security!)
     * Also, enclose the original exception or write a meaningful message.
     * 
     * @param message
     * @param cause
     */
    public LoginException(String message, Throwable cause) {
        super(message, cause);
    }

}
