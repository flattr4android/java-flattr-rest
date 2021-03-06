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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
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
	private DefaultHttpClient client;

	public FlattrRestClient(OAuthConsumer consumer) {
		this.consumer = consumer;
	}

	public FlattrRestClient(String consumerKey, String consumerSecret,
			String accessToken, String tokenSecret) {
		consumer = new FlattrOAuthConsumer(consumerKey, consumerSecret);
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
	 * Return a thing by its numeric ID.
	 */
	public Thing getThingById(int id) throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			FlattrRestException, IOException {
		return getThingById(Integer.toString(id));
	}

	/**
	 * Return a thing by its numeric or MD5 ID.
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
	 * 
	 * @see FlattrRestClient#browse(String, List, List, List, List)
	 */
	public ArrayList<Thing> getUserThings(int userId)
			throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			FlattrRestException, IOException {
		return Thing.buildThings(this, getResourceInputStream(API_PATH_PREFIX
				+ "thing/browse/user/" + userId));
	}

	/**
	 * Get the things by a single tag.
	 * 
	 * @see FlattrRestClient#browse(String, List, List, List, List)
	 */
	public ArrayList<Thing> getThingsByTag(String tag)
			throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			FlattrRestException, IOException {
		return Thing.buildThings(this, getResourceInputStream(API_PATH_PREFIX
				+ "thing/browse/tag/" + tag));
	}

	/**
	 * Browse things.
	 * 
	 * @param searchedText
	 *            Text to be present in the thing's title, or <code>null</code>
	 *            if nothing is expected.
	 * @param tags
	 *            Searched tags, or <code>null</code> or empty list if nothing
	 *            is expected.
	 * @param categories
	 *            Searched categories, or <code>null</code> or empty list if
	 *            nothing is expected.
	 * @param languages
	 *            Searched languages, or <code>null</code> or empty list if
	 *            nothing is expected.
	 * @param users
	 *            Searched users, or <code>null</code> or empty list if nothing
	 *            is expected.
	 * 
	 * @see FlattrRestClient#getCategories()
	 * @see FlattrRestClient#getLanguages()
	 */
	public ArrayList<Thing> browse(String searchedText, List<String> tags,
			List<String> categories, List<String> languages, List<String> users)
			throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			FlattrRestException, IOException {

		return Thing.buildThings(
				this,
				getResourceInputStream(getBrowseURI(searchedText, tags,
						categories, languages, users)));
	}

	static String getBrowseURI(String searchedText, List<String> tags,
			List<String> categories, List<String> languages, List<String> users) {
		String uri = API_PATH_PREFIX + "thing/browse";
		if (searchedText != null) {
			uri += "/query/" + searchedText;
		}
		uri += getParameterString("tag", tags);
		uri += getParameterString("category", categories);
		uri += getParameterString("language", languages);
		uri += getParameterString("user", users);
		return uri;
	}

	private static String getParameterString(String paramName,
			List<String> params) {
		if ((params == null) || (params.size() <= 0)) {
			return "";
		}
		StringBuffer result = new StringBuffer("/");
		result.append(paramName);
		result.append("/");
		for (String s : params) {
			result.append(s);
			result.append(",");
		}
		return result.substring(0, result.length() - 1);
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
	public ArrayList<Subscription> getMySubscriptions()
			throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			FlattrServerResponseException, FlattrRestException, IOException {
		return Subscription.buildSubscriptions(this,
				getResourceInputStream(API_PATH_PREFIX + "subscription/list"));
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
	public Thing register(Thing thing) throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			IOException, IllegalStateException, FlattrRestException {
		String content = "<thing>" + "<url>" + thing.getURL() + "</url>"
				+ "<title><![CDATA[" + thing.getTitle() + "]]></title>"
				+ "<category>" + thing.getCategoryName() + "</category>"
				+ "<description><![CDATA[" + thing.getDescription()
				+ "]]></description>" + "<language>" + thing.getLanguage()
				+ "</language>" + "<hidden>0</hidden>" + "<tags>";
		for (String tag : thing.getTags()) {
			content += "<tag>" + tag + "</tag>";
		}
		content += "</tags>" + "</thing>";

		HttpResponse response = sendRequest(API_PATH_PREFIX + "thing/register",
				"POST", content);
		return Thing.buildOneThing(this, (InputStream) response.getEntity()
				.getContent());
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

	protected HttpResponse sendRequest(String uri)
			throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			FlattrServerResponseException, IOException {
		return sendRequest(uri, "GET", null);
	}

	protected HttpResponse sendRequest(String uri, String method, String content)
			throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			IOException, FlattrServerResponseException {
		HttpRequestBase req;
		if (method.equals("GET")) {
			req = new HttpGet("http://api.flattr.com" + uri);
		} else {
			req = new HttpPost("http://api.flattr.com" + uri);
			if (content != null) {
				StringEntity body = new StringEntity("data="
						+ URLEncoder.encode(content, "utf-8"));
				body.setContentType("application/x-www-form-urlencoded");
				((HttpPost) req).setEntity(body);
			}
		}
		consumer.sign(req);
		HttpResponse resp = getHttpClient().execute(req);
		int reqCode;
		reqCode = resp.getStatusLine().getStatusCode();
		if (reqCode != 200) {
			if (reqCode == 401) {
				throw new AuthenticationException(resp);
			} else {
				throw new FlattrServerResponseException(resp);
			}
		}
		return resp;
	}

	protected InputStream getResourceInputStream(String uri)
			throws IOException, OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			FlattrServerResponseException {
		return (InputStream) sendRequest(uri).getEntity().getContent();
	}

	private HttpClient getHttpClient() {
		if (client == null) {
			client = new DefaultHttpClient();
		}
		return client;
	}

}
