/* Copyright (c) 2010-2011 Flattr4Android
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flattr4android.rest;

import java.io.IOException;
import java.net.HttpURLConnection;

@SuppressWarnings("serial")
public class FlattrServerResponseException extends FlattrRestException {

	private int status;
	private String rspMsg;

	public FlattrServerResponseException(int status, String msg) {
		super("Server response: " + status + " (" + msg + ")");
	}

	public FlattrServerResponseException(HttpURLConnection connection)
			throws IOException {
		this(connection.getResponseCode(), connection.getResponseMessage());
	}

	public int getStatus() {
		return status;
	}

	public String getResponseMessage() {
		return rspMsg;
	}

}