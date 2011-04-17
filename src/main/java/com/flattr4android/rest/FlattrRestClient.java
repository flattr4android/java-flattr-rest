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
import java.util.Calendar;
import java.util.Date;

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

	public static final String API_PATH_PREFIX = "/rest/0.5/";

	private OAuthConsumer consumer;
	private boolean demoMode = false;
	private Thing demoSampleThing;
	private ThingCache thingCache = new ThingCache();

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

	public ThingCache getThingCache() {
		return thingCache;
	}

	/**
	 * Return the user representing the authenticated user.
	 */
	public User getMe() throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			ParserConfigurationException, SAXException, IOException,
			FlattrRestException {
		return User.buildUser(this, getResourceInputStream(API_PATH_PREFIX
				+ "user/me"));
	}

	/**
	 * Return a user by Id.
	 */
	public User getUser(int id) throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			ParserConfigurationException, SAXException, IOException,
			FlattrRestException {
		return User.buildUser(this, getResourceInputStream(API_PATH_PREFIX
				+ "user/get/id/" + id));
	}

	/**
	 * Return a user by name.
	 */
	public User getUser(String userName) throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			ParserConfigurationException, SAXException, IOException,
			FlattrRestException {
		return User.buildUser(this, getResourceInputStream(API_PATH_PREFIX
				+ "user/get/name/" + userName));
	}

	/**
	 * Return a thing by its ID.
	 * 
	 * @deprecated Use {@link FlattrRestClient#getThingById(String)}
	 */
	public Thing getThing(String id) throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			FlattrRestException, IOException {
		return getThingById(id);
	}

	/**
	 * Return a thing by its ID. This method only look in the cache (and
	 * potentialy returns the demo thing) and does not perform any network
	 * access.
	 */
	public Thing getCachedThingById(String id) {
		if (isDemoMode() && id.equals(DEMO_SAMPLE_THING_ID)) {
			if (demoSampleThing == null) {
				demoSampleThing = new SampleThing();
			}
			return demoSampleThing;
		}
		return thingCache.getThingById(id);
	}

	/**
	 * Return a thing by its ID.
	 */
	public Thing getThingById(String id) throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			FlattrRestException, IOException {
		// First, look in the cache
		Thing thing = getCachedThingById(id);
		// Not found: network to the rescue
		if (thing == null) {
			thing = Thing.buildOneThing(this,
					getResourceInputStream(API_PATH_PREFIX + "thing/get/id/"
							+ id));
			thingCache.addOrRefheshThing(thing);
		}
		return thing;
	}

	/**
	 * Return a thing by its URL.
	 */
	public Thing getThingByURL(String url) throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			FlattrRestException, IOException {
		return Thing.buildOneThing(this, getResourceInputStream(API_PATH_PREFIX
				+ "thing/get/url/" + url));
	}

	/**
	 * Search things.
	 * 
	 * @see http://developers.flattr.net/doku.php/thing_methods_0.5
	 */
	public ArrayList<Thing> browseThings(String... query)
			throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			FlattrRestException, IOException {
		String uri = "thing/browse";
		for (int i = 0; i < query.length; i++) {
			uri += query[i];
		}
		return Thing.buildThings(this, getResourceInputStream(API_PATH_PREFIX
				+ uri));
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
		return Thing.buildThings(this, getResourceInputStream(API_PATH_PREFIX
				+ "thing/browse/user/" + userId));
	}

	/**
	 * Return the list of clicks performed by the authenticated user.
	 */
	public ArrayList<Click> getMyClicks() throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			FlattrServerResponseException, FlattrRestException, IOException {
		return getMyClicks("");
	}

	/**
	 * Return the list of clicks performed during a specific month by the
	 * authenticated user.
	 * 
	 * @param period
	 *            The targeted period (format: "yyyymm", eg. "201012").
	 */
	public ArrayList<Click> getMyClicks(String period)
			throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			FlattrServerResponseException, FlattrRestException, IOException {
		return Click.buildClicks(this, getResourceInputStream(API_PATH_PREFIX
				+ "user/clicks/period/" + period));
	}

	public ArrayList<Click> getMyClicks(Date startDate, Date stopDate)
			throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			FlattrServerResponseException, FlattrRestException, IOException {
		ArrayList<Click> clicks = new ArrayList<Click>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		int startYear = cal.get(Calendar.YEAR);
		int startMonth = cal.get(Calendar.MONTH);
		int startDay = cal.get(Calendar.DAY_OF_MONTH);

		cal.setTime(stopDate);
		int stopYear = cal.get(Calendar.YEAR);
		int stopMonth = cal.get(Calendar.MONTH);
		int stopDay = cal.get(Calendar.DAY_OF_MONTH);

		int currentYear = startYear;
		int currentMonth = startMonth;
		Calendar currentClickDate = Calendar.getInstance();

		while ((currentYear < stopYear)
				|| ((currentYear == stopYear) && (currentMonth <= stopMonth))) {
			String period = Integer.toString(currentYear * 100
					+ (currentMonth + 1));
			ArrayList<Click> current = getMyClicks(period);
			// If we are in the first month of the interval,
			// be careful not to add clicks too early in the month.
			boolean checkLow = ((currentYear == startYear) && (currentMonth == startMonth));
			boolean checkHigh = ((currentYear == stopYear) && (currentMonth == stopMonth));
			if ((!checkLow) && (!checkHigh)) {
				// Clicks in the middle of the interval: add all!
				clicks.addAll(current);
			} else {
				// Check one by one
				for (Click click : current) {
					currentClickDate.setTime(click.getDate());
					int currentDay = currentClickDate
							.get(Calendar.DAY_OF_MONTH);
					if (((!checkLow) || (currentDay >= startDay))
							&& ((!checkHigh) || (currentDay <= stopDay))) {
						clicks.add(click);
					}
				}
			}

			currentMonth++;
			if (currentMonth > Calendar.DECEMBER) {
				currentMonth = Calendar.JANUARY;
				currentYear++;
			}
		}

		return clicks;
	}

	/**
	 * Return the list of ongoing subscriptions of the authenticated user.
	 */
	public ArrayList<Subscription> getMySubscriptions() throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			FlattrServerResponseException, FlattrRestException, IOException {
		return Subscription.buildSubscriptions(this, getResourceInputStream(API_PATH_PREFIX
				+ "subscription/list"));
	}

	/**
	 * Click a thing by its ID.
	 */
	public void clickThing(String id) throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			ParserConfigurationException, SAXException, IOException,
			FlattrServerResponseException {
		if (!isDemoMode()) {
			sendRequest(API_PATH_PREFIX + "thing/click/id/" + id);
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

		sendRequest(API_PATH_PREFIX + "thing/register", "POST", content);
	}

	/**
	 * Return the supported languages.
	 */
	public ArrayList<Language> getLanguages()
			throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			FlattrServerResponseException, FlattrRestException, IOException {
		return Language.buildLanguages(getResourceInputStream(API_PATH_PREFIX
				+ "feed/languages"));
	}

	/**
	 * Return the existing thing categories.
	 */
	public ArrayList<Category> getCategories()
			throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			FlattrServerResponseException, FlattrRestException, IOException {
		return Category.buildCategories(getResourceInputStream(API_PATH_PREFIX
				+ "feed/categories"));
	}

	protected HttpURLConnection sendRequest(String uri)
			throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			FlattrServerResponseException, IOException {
		return sendRequest(uri, "GET", null);
	}

	protected HttpURLConnection sendRequest(String uri, String method,
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

	protected InputStream getResourceInputStream(String uri) throws IOException,
			OAuthMessageSignerException, OAuthExpectationFailedException,
			OAuthCommunicationException, FlattrServerResponseException {
		return (InputStream) sendRequest(uri).getContent();
	}

}
