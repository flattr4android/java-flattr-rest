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
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.SAXParserFactory;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Represent a user. See <a
 * href="http://developers.flattr.net/doku.php/user_methods_0.5">Flattr API
 * documentation</a>.
 */
public class Subscription implements ThingOverview {

	protected int id;
	protected int months, monthsLeft;
	protected Date added;
	protected String thingId, thingTitle, thingUrl;
	private Thing thing;

	FlattrRestClient fr;

	public Subscription(FlattrRestClient fr) {
		this.fr = fr;
	}

	public int getId() {
		return id;
	}

	public int getMonths() {
		return months;
	}

	public int getMonthsLeft() {
		return monthsLeft;
	}

	public Date getCreationDate() {
		return added;
	}

	@Override
	public String getThingId() {
		return thingId;
	}

	@Override
	public String getThingTitle() {
		return thingTitle;
	}

	@Override
	public String getThingURL() {
		return thingUrl;
	}

	public Thing getThing() throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			FlattrRestException, IOException {
		if (thing == null) {
			thing = fr.getThingById(getThingId());
		}
		return thing;
	}

	@Override
	public String toString() {
		return getThingTitle() + " - Suscribed for " + getMonths()
				+ " months, ends in " + getMonthsLeft() + " months";
	}

	public static ArrayList<Subscription> buildSubscriptions(
			FlattrRestClient fr, InputStream xmlDescription)
			throws FlattrRestException {
		ArrayList<Subscription> al = new ArrayList<Subscription>();

		try {
			XMLReader parser = SAXParserFactory.newInstance().newSAXParser()
					.getXMLReader();
			parser.setContentHandler(new SubscriptionSAXHandler(fr, al));
			parser.parse(new InputSource(xmlDescription));
		} catch (Exception e) {
			throw new FlattrRestException(e);
		}

		return al;
	}

	public static Subscription buildOneSubscription(FlattrRestClient fr,
			InputStream is) throws FlattrRestException {
		ArrayList<Subscription> al = buildSubscriptions(fr, is);
		if (al.size() != 1) {
			throw new FlattrRestException(
					"Unexpected amount of things in the stream: " + al.size());
		}
		return al.get(0);
	}
}

class SubscriptionSAXHandler extends PortableSAXHandler {
	private boolean inThing = false;
	private StringBuilder currentValue = new StringBuilder();

	private FlattrRestClient fr;
	private ArrayList<Subscription> subscriptionList;
	private Subscription currentSubscription = null;

	public SubscriptionSAXHandler(FlattrRestClient fr,
			ArrayList<Subscription> subscriptionList) {
		this.subscriptionList = subscriptionList;
		this.fr = fr;
	}

	@Override
	public void startElement(String nsURI, String localName, String qName,
			Attributes attributes) throws SAXException {
		String tagName = getTagName(localName, qName);

		currentValue = new StringBuilder();

		if (tagName.equals("subscription")) {
			currentSubscription = new Subscription(fr);
		} else if (tagName.equals("thing")) {
			inThing = true;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		String tagName = getTagName(localName, qName);

		String value = currentValue.toString().trim();

		if (tagName.equals("thing")) {
			inThing = false;
		} else if (tagName.equals("subscription")) {
			subscriptionList.add(currentSubscription);
			currentSubscription = null;
		} else {
			if (inThing) {
				if (tagName.equals("id")) {
					currentSubscription.thingId = value;
				} else if (tagName.equals("title")) {
					currentSubscription.thingTitle = value;
				} else if (tagName.equals("url")) {
					currentSubscription.thingUrl = value;
				}
			} else {
				if (tagName.equals("id")) {
					currentSubscription.id = Integer.parseInt(value);
				} else if (tagName.equals("added")) {
					// Convert seconds (UNIX/Flattr format) to milliseconds
					// (Java Date's API)
					currentSubscription.added = new Date(
							Long.parseLong(value) * 1000L);
				} else if (tagName.equals("months")) {
					currentSubscription.months = Integer.parseInt(value);
				} else if (tagName.equals("monthsleft")) {
					currentSubscription.monthsLeft = Integer.parseInt(value);
				}
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		currentValue.append(ch, start, length);
	}
}
