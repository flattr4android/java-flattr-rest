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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.flattr4android.rest.Category;
import com.flattr4android.rest.FlattrOAuthConsumer;
import com.flattr4android.rest.FlattrOAuthProvider;
import com.flattr4android.rest.FlattrRestClient;
import com.flattr4android.rest.Language;
import com.flattr4android.rest.Thing;
import com.flattr4android.rest.User;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;

public class FlattrRestExample {

	public static void main(String[] args) {
		try {
			// This consumer uses java-flattr-rest Sample Application key.
			// This application is *supposed* to be used only
			// in this example. But as this source code is open source,
			// keep in mind that anybody can reuse it for any purpose
			// (understand: bad purpose).
			// If someone manage to steal the token and token secret you
			// are going to generate while running this program,
			// he will be able to view/click/submit in your name.
			// In 99% cases, that's ok, nobody while get your token/secret.
			// If you're paranoid, replace the application key above
			// with your own application key.
			OAuthConsumer consumer = new FlattrOAuthConsumer(
					"FdngHnEZdggU1zoohFuZ8wFrLR9AjeaPinVQBpGEwMoocDDj8X96jEoVlolvtmaB",
					"AVi1bOvxrKLtefCc2C9c6nXhB9CUlCmLouDp8ZMpzoIDtbGmDFozTbHkaa5mDhYR");
			OAuthProvider provider = new FlattrOAuthProvider();
			System.out.println("Fetching request token from Flattr...");

			// No callback here, pass an empty string.
			// See http://forum.flattr.net/showthread.php?tid=486
			String authUrl = provider.retrieveRequestToken(consumer, "");

			System.out.println("Request token: " + consumer.getToken());
			System.out.println("Token secret: " + consumer.getTokenSecret());

			System.out.println("Now visit:\n" + authUrl
					+ "&access_scope=extendedread,click,publish"
					+ "\n... and grant this app authorization");
			System.out
					.println("Enter the PIN code and hit ENTER when you're done:");

			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			String pin = br.readLine();

			System.out.println("Fetching access token from Flattr...");
			provider.retrieveAccessToken(consumer, pin);

			// Not printed for safety reasons. If you encounter a problem
			// with this program, ask for help on a forum or whatever and
			// copy/paste your traces, a malicious user could use your generated
			// token and token secret to click or submit in your name.
			// System.out.println("Access token: " + consumer.getToken());
			// System.out.println("Token secret: " + consumer.getTokenSecret());

			FlattrRestClient fr = new FlattrRestClient(consumer);
			User u = fr.getMe();
			System.out.println("Authenticatd as " + u);
			System.out.println("Your things:");
			ArrayList<Thing> things = u.getThings();
			for (Thing thing : things) {
				System.out.println("\t" + thing);
			}

			System.out.println("Your subscriptions:");
			ArrayList<Subscription> subscriptions = fr.getMySubscriptions();
			for (Subscription s : subscriptions) {
				System.out.println("\t" + s);
			}

			System.out.println("Your clicks:");
			Date today = new Date(System.currentTimeMillis());
			Calendar oneMonthAgo = Calendar.getInstance();
			oneMonthAgo.setTime(today);
			oneMonthAgo.add(Calendar.MONTH, -1);

			ArrayList<Click> clicks = fr.getMyClicks(oneMonthAgo.getTime(),
					today);
			for (Click click : clicks) {
				System.out.println("\t" + click);
			}

			System.out.println("Register a new thing");
			Thing t = new Thing();
			t.setTitle("My sample thing");
			t.setDescription("This is my sample thing....");
			t.setLanguage("en_US");
			t.setCategoryName("rest");
			t.setURL("http://some_url.net");
			fr.register(t);

			System.out.println("Languages:");
			ArrayList<Language> langs = fr.getLanguages();
			for (Language lang : langs) {
				System.out.println("\t" + lang);
			}

			System.out.println("Categories:");
			ArrayList<Category> cats = fr.getCategories();
			for (Category cat : cats) {
				System.out.println("\t" + cat);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
