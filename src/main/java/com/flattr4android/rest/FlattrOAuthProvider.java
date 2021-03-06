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
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHttpRequest;

import oauth.signpost.basic.HttpURLConnectionRequestAdapter;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.commonshttp.HttpRequestAdapter;
import oauth.signpost.http.HttpRequest;

@SuppressWarnings("serial")
public class FlattrOAuthProvider extends CommonsHttpOAuthProvider {

	public static final String REQUEST_TOKEN_ENDPOINT_URL = "http://api.flattr.com/oauth/request_token";
	public static final String ACCESS_TOKEN_ENDPOINT_URL = "http://api.flattr.com/oauth/access_token";
	public static final String AUTHORIZATION_WEBSITE_URL = "http://api.flattr.com/oauth/authenticate";

	public FlattrOAuthProvider() {
		super(REQUEST_TOKEN_ENDPOINT_URL, ACCESS_TOKEN_ENDPOINT_URL,
				AUTHORIZATION_WEBSITE_URL);
		// Enable v1.0a to have the verifier/PIN sent
		// See http://forum.flattr.net/showthread.php?tid=486
		setOAuth10a(true);
	}

	@Override
	protected HttpRequest createRequest(String endpointUrl)
			throws MalformedURLException, IOException {
		HttpUriRequest request = new HttpGet(endpointUrl);
		return new HttpRequestAdapter(request);
	}
}
