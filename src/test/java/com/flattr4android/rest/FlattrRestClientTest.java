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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

public class FlattrRestClientTest extends FlattrRestTestCase {

	public void testGetMyClicks() throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			FlattrServerResponseException, FlattrRestException, IOException {
		MockFlattrRestClient client = new MockFlattrRestClient();
		ArrayList<Click> clicks;

		clicks = new ArrayList<Click>();
		clicks.add(new MockClick(2009, Calendar.OCTOBER, 2));
		clicks.add(new MockClick(2009, Calendar.OCTOBER, 16));
		clicks.add(new MockClick(2009, Calendar.OCTOBER, 17));
		client.addClicks("200910", clicks);

		clicks = new ArrayList<Click>();
		clicks.add(new MockClick(2010, Calendar.NOVEMBER, 5));
		clicks.add(new MockClick(2010, Calendar.NOVEMBER, 9));
		clicks.add(new MockClick(2010, Calendar.NOVEMBER, 24));
		client.addClicks("201011", clicks);

		clicks = new ArrayList<Click>();
		clicks.add(new MockClick(2010, Calendar.DECEMBER, 1));
		clicks.add(new MockClick(2010, Calendar.DECEMBER, 29));
		client.addClicks("201012", clicks);

		clicks = new ArrayList<Click>();
		clicks.add(new MockClick(2011, Calendar.JANUARY, 3));
		clicks.add(new MockClick(2011, Calendar.JANUARY, 8));
		clicks.add(new MockClick(2011, Calendar.JANUARY, 15));
		client.addClicks("201101", clicks);

		Calendar start = Calendar.getInstance();
		Calendar stop = Calendar.getInstance();

		// No things in the interval
		start.set(2011, Calendar.MARCH, 5);
		stop.set(2011, Calendar.MARCH, 9);
		assertClicks(new int[] {},
				client.getMyClicks(start.getTime(), stop.getTime()));

		// start > stop
		start.set(2011, Calendar.JANUARY, 1);
		stop.set(2010, Calendar.NOVEMBER, 1);
		assertClicks(new int[] {},
				client.getMyClicks(start.getTime(), stop.getTime()));

		// One month
		start.set(2011, Calendar.JANUARY, 1);
		stop.set(2011, Calendar.JANUARY, 31);
		assertClicks(new int[] { 20110103, 20110108, 20110115 },
				client.getMyClicks(start.getTime(), stop.getTime()));

		// Exact day - One result
		start.set(2011, Calendar.JANUARY, 8);
		stop.set(2011, Calendar.JANUARY, 8);
		assertClicks(new int[] { 20110108 },
				client.getMyClicks(start.getTime(), stop.getTime()));

		// Exact day - No result
		start.set(2011, Calendar.JANUARY, 9);
		stop.set(2011, Calendar.JANUARY, 9);
		assertClicks(new int[] {},
				client.getMyClicks(start.getTime(), stop.getTime()));

		// Low bound - One day before
		start.set(2011, Calendar.JANUARY, 2);
		stop.set(2011, Calendar.JANUARY, 31);
		assertClicks(new int[] { 20110103, 20110108, 20110115 },
				client.getMyClicks(start.getTime(), stop.getTime()));

		// Low bound - Exact day
		start.set(2011, Calendar.JANUARY, 3);
		stop.set(2011, Calendar.JANUARY, 31);
		assertClicks(new int[] { 20110103, 20110108, 20110115 },
				client.getMyClicks(start.getTime(), stop.getTime()));

		// Low bound - One day after
		start.set(2011, Calendar.JANUARY, 4);
		stop.set(2011, Calendar.JANUARY, 31);
		assertClicks(new int[] { 20110108, 20110115 },
				client.getMyClicks(start.getTime(), stop.getTime()));

		// High bound - One day after
		start.set(2011, Calendar.JANUARY, 2);
		stop.set(2011, Calendar.JANUARY, 16);
		assertClicks(new int[] { 20110103, 20110108, 20110115 },
				client.getMyClicks(start.getTime(), stop.getTime()));

		// High bound - Exact day
		start.set(2011, Calendar.JANUARY, 2);
		stop.set(2011, Calendar.JANUARY, 15);
		assertClicks(new int[] { 20110103, 20110108, 20110115 },
				client.getMyClicks(start.getTime(), stop.getTime()));

		// High bound - One day before
		start.set(2011, Calendar.JANUARY, 2);
		stop.set(2011, Calendar.JANUARY, 14);
		assertClicks(new int[] { 20110103, 20110108 },
				client.getMyClicks(start.getTime(), stop.getTime()));

		// Two months
		start.set(2010, Calendar.DECEMBER, 1);
		stop.set(2011, Calendar.JANUARY, 31);
		assertClicks(new int[] {
				// 2010/12
				20101201, 20101229,
				// 2011/01
				20110103, 20110108, 20110115 },
				client.getMyClicks(start.getTime(), stop.getTime()));

		// Two months but one day
		start.set(2010, Calendar.DECEMBER, 2);
		stop.set(2011, Calendar.JANUARY, 31);
		assertClicks(new int[] {
				// 2010/12
				20101229,
				// 2011/01
				20110103, 20110108, 20110115 },
				client.getMyClicks(start.getTime(), stop.getTime()));

		// Three months
		start.set(2010, Calendar.NOVEMBER, 1);
		stop.set(2011, Calendar.JANUARY, 31);
		assertClicks(new int[] {
				// 2010/11
				20101105, 20101109, 20101124,
				// 2010/12
				20101201, 20101229,
				// 2011/01
				20110103, 20110108, 20110115 },
				client.getMyClicks(start.getTime(), stop.getTime()));

		// Three months - No impact on "middle month"
		start.set(2010, Calendar.NOVEMBER, 3);
		stop.set(2011, Calendar.JANUARY, 25);
		assertClicks(new int[] {
				// 2010/11
				20101105, 20101109, 20101124,
				// 2010/12
				20101201, 20101229,
				// 2011/01
				20110103, 20110108, 20110115 },
				client.getMyClicks(start.getTime(), stop.getTime()));

		// Several years
		start.set(2009, Calendar.OCTOBER, 1);
		stop.set(2011, Calendar.JANUARY, 31);
		assertClicks(new int[] {
				// 2009/10
				20091002, 20091016, 20091017,
				// 2010/11
				20101105, 20101109, 20101124,
				// 2010/12
				20101201, 20101229,
				// 2011/01
				20110103, 20110108, 20110115 },
				client.getMyClicks(start.getTime(), stop.getTime()));

		// Several years - Large interval
		start.set(2005, Calendar.JANUARY, 1);
		stop.set(2015, Calendar.DECEMBER, 31);
		assertClicks(new int[] {
				// 2009/10
				20091002, 20091016, 20091017,
				// 2010/11
				20101105, 20101109, 20101124,
				// 2010/12
				20101201, 20101229,
				// 2011/01
				20110103, 20110108, 20110115 },
				client.getMyClicks(start.getTime(), stop.getTime()));
	}

	public void assertClicks(int[] excepted, ArrayList<Click> observed) {
		String exp = "";
		for (int id : excepted) {
			exp += id + "\n";
		}
		String obs = "";
		for (Click click : observed) {
			obs += Integer.toString(click.getId()) + "\n";
		}
		assertEquals(exp, obs);
	}

	class MockFlattrRestClient extends FlattrRestClient {

		private Hashtable<String, ArrayList<Click>> myClicks = new Hashtable<String, ArrayList<Click>>();

		public MockFlattrRestClient() {
			super(null);
		}

		public void addClicks(String period, ArrayList<Click> clicks) {
			myClicks.put(period, clicks);
		}

		public ArrayList<Click> getMyClicks(String period) {
			ArrayList<Click> clicks = myClicks.get(period);
			if (clicks == null) {
				clicks = new ArrayList<Click>();
			}
			return clicks;
		}
	}

	class MockClick extends Click {

		public MockClick(int year, int month, int day) {
			super(null);
			this.id = year * 10000 + (month + 1) * 100 + day;
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month);
			cal.set(Calendar.DAY_OF_MONTH, day);
			this.date = cal.getTime();
		}

	}
}
