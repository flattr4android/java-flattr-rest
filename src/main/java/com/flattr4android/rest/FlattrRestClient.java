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
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.xml.sax.SAXException;

import com.flattr4android.rest.demo.SampleThing;

/**
 * <code>FlattrRestClient</code> is the main entry point to the API.
 */
public class FlattrRestClient {

	public static final String DEMO_SAMPLE_THING_ID = "demo_thing";

	private OAuthConsumer consumer;
	private boolean demoMode = false;
	private Thing demoSampleThing;

	public FlattrRestClient(OAuthConsumer consumer) {
		this.consumer = consumer;
	}

	public FlattrRestClient(String consumerKey, String consumerSecret,
			String accessToken, String tokenSecret) {
		consumer = new DefaultOAuthConsumer(consumerKey, consumerSecret);
		consumer.setTokenWithSecret(accessToken, tokenSecret);
	}

	/**
	 * @see FlattrRestClient#setDemoMode(boolean)
	 */
	public boolean isDemoMode() {
		return demoMode;
	}

	/**
	 * Enable or disable the Demo mode.
	 * 
	 * When enabled, the demo mode has a couple of side effects:
	 * <ul>
	 * <li>The things are always "clickable" and click is always successful but
	 * does <i>nothing</i>. This is useful to demonstrate the flattring of a
	 * thing without actually clicking it, and with the ability to click it
	 * several times (to repeat the demo as much as needed).</li>
	 * <li>The URL stored in the DEMO_SAMPLE_THING_URL variable</li>
	 * </ul>
	 */
	public void setDemoMode(boolean demoMode) {
		this.demoMode = demoMode;
	}

	/**
	 * Return the user representing the authenticated user.
	 */
	public User getMe() throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			ParserConfigurationException, SAXException, IOException,
			FlattrRestException {
		return User.buildUser(this,
				getResourceInputStream("/rest/0.0.1/user/me"));
	}

	/**
	 * Return a user by his ID.
	 */
	public User getUser(int id) throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			ParserConfigurationException, SAXException, IOException,
			FlattrRestException {
		return User.buildUser(this,
				getResourceInputStream("/rest/0.0.1/user/get/id/" + id));
	}

	/**
	 * Return a thing by its ID.
	 */
	public Thing getThing(String id) throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			FlattrRestException, IOException {
		if (isDemoMode() && id.equals(DEMO_SAMPLE_THING_ID)) {
			if (demoSampleThing == null) {
				demoSampleThing = new SampleThing();
			}
			return demoSampleThing;
		}
		return Thing.buildOneThing(this,
				getResourceInputStream("/rest/0.0.1/thing/get/id/" + id));
	}

	public void setDemoSampleThing(Thing model) {
		demoSampleThing = new SampleThing(model);
	}

	/**
	 * Get the things of a user.
	 */
	public ArrayList<Thing> getUserThings(int userId)
			throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			FlattrRestException, IOException {
		return Thing.buildThings(this,
				getResourceInputStream("/rest/0.0.1/thing/listbyuser/id/"
						+ userId));
	}

	/**
	 * Click a thing by its ID.
	 */
	public void clickThing(String id) throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			ParserConfigurationException, SAXException, IOException,
			FlattrServerResponseException {
		if (!isDemoMode()) {
			sendRequest("/rest/0.0.1/thing/click/id/" + id);
		}
		// If demo mode is on, click always works
	}

	/**
	 * Register a new thing.
	 */
	public void register(Thing thing) throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			FlattrServerResponseException, IOException {
		String content = "<thing>" + "<url>" + thing.getURL() + "</url>"
				+ "<title><![CDATA[" + thing.getTitle() + "]]></title>"
				+ "<category>" + thing.getCategoryName() + "</category>"
				+ "<description><![CDATA[" + thing.getDescription()
				+ "]]></description>" + "<language>" + thing.getLanguage()
				+ "</language>" + "<hidden>1</hidden>" + "<tags>";
		for (String tag : thing.getTags()) {
			content += "<tag>" + tag + "</tag>";
		}
		content += "</tags>" + "</thing>";

		sendRequest("/rest/0.0.1/thing/register", "POST", content);
	}

	/**
	 * Return the supported languages.
	 */
	public ArrayList<Language> getLanguages()
			throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			FlattrServerResponseException, FlattrRestException, IOException {
		return Language
				.buildLanguages(getResourceInputStream("/rest/0.0.1/feed/languages"));
	}

	/**
	 * Return the existing thing categories.
	 */
	public ArrayList<Category> getCategories()
			throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			FlattrServerResponseException, FlattrRestException, IOException {
		return Category
				.buildCategories(getResourceInputStream("/rest/0.0.1/feed/categories"));
	}

	private HttpURLConnection sendRequest(String uri)
			throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			FlattrServerResponseException, IOException {
		return sendRequest(uri, "GET", null);
	}

	private HttpURLConnection sendRequest(String uri, String method,
			String content) throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			IOException, FlattrServerResponseException {
		URL url = new URL("http://api.flattr.com" + uri);
		HttpURLConnection request = (HttpURLConnection) url.openConnection();
		consumer.sign(request);
		request.setRequestMethod(method);
		if (content != null) {
			request.setDoOutput(true);
			PrintWriter pw = new PrintWriter(request.getOutputStream());
			pw.write(content);
		}
		request.connect();
		int reqCode;
		try {
			reqCode = request.getResponseCode();
		} catch (IOException e) {
			// See
			// http://stackoverflow.com/questions/1357372/ioexception-received-authentication-challenge-is-null-apache-harmony-android
			if (e.getMessage().equals(
					"Received authentication challenge is null")) {
				throw new AuthenticationException(request);
			} else {
				throw e;
			}
		}
		if (reqCode != 200) {
			if (reqCode == 401) {
				throw new AuthenticationException(request);
			} else {
				throw new FlattrServerResponseException(request);
			}
		}
		return request;
	}

	private InputStream getResourceInputStream(String uri) throws IOException,
			OAuthMessageSignerException, OAuthExpectationFailedException,
			OAuthCommunicationException, FlattrServerResponseException {
		return (InputStream) sendRequest(uri).getContent();
	}

}
