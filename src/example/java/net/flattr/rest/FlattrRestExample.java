/* Copyright (c) 2010 Philippe Bernard
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
package net.flattr.rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;

public class FlattrRestExample {

	public static void main(String[] args) {
		try {
			OAuthConsumer consumer = new DefaultOAuthConsumer("<consumer key>",
					"<consumer secret>");

			OAuthProvider provider = new DefaultOAuthProvider(
					FlattrRestClient.REQUEST_TOKEN_ENDPOINT_URL,
					FlattrRestClient.ACCESS_TOKEN_ENDPOINT_URL,
					FlattrRestClient.AUTHORIZATION_WEBSITE_URL);
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

			// Enable v1.0a to have the verifier/PIN sent
			// See http://forum.flattr.net/showthread.php?tid=486
			provider.setOAuth10a(true);
			provider.retrieveAccessToken(consumer, pin);

			System.out.println("Access token: " + consumer.getToken());
			System.out.println("Token secret: " + consumer.getTokenSecret());

			FlattrRestClient fr = new FlattrRestClient(consumer);
			User u = fr.getMe();
			System.out.println("Authenticatd as " + u);
			System.out.println("Your things:");
			ArrayList<Thing> things = u.getThings();
			for (Thing thing : things) {
				System.out.println("\t" + thing);
			}

			System.out.println("Register a new thing");
			Thing t = new Thing();
			t.setTitle("My sample thing");
			t.setDescription("This is my sample thing....");
			t.setLanguage("en_US");
			t.setCategory("rest");
			t.setUrl("http://some_url.net");
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
