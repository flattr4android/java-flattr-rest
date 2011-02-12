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

public class Click implements ThingOverview {

	protected int id;
	protected Date date;
	protected String thingId, thingTitle, thingUrl;

	private FlattrRestClient fr;

	public Click(FlattrRestClient fr) {
		this.fr = fr;
	}

	public int getId() {
		return id;
	}

	public Date getDate() {
		return date;
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
	
	@Override
	public String toString() {
		return getThingTitle() + " - clicked on " + getDate();
	}

	public Thing getThing() throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			FlattrRestException, IOException {
		return fr.getThingById(getThingId());
	}

	public static ArrayList<Click> buildClicks(FlattrRestClient fr,
			InputStream xmlDescription) throws FlattrRestException {
		ArrayList<Click> al = new ArrayList<Click>();

		try {
			XMLReader parser = SAXParserFactory.newInstance().newSAXParser()
					.getXMLReader();
			parser.setContentHandler(new ClickSAXHandler(fr, al));
			parser.parse(new InputSource(xmlDescription));
		} catch (Exception e) {
			throw new FlattrRestException(e);
		}

		return al;
	}

}

class ClickSAXHandler extends PortableSAXHandler {
	private boolean inThing = false;
	private StringBuilder currentValue = new StringBuilder();

	private FlattrRestClient fr;
	private ArrayList<Click> clickList;
	private Click currentClick = null;

	public ClickSAXHandler(FlattrRestClient fr, ArrayList<Click> clickList) {
		this.clickList = clickList;
		this.fr = fr;
	}

	@Override
	public void startElement(String nsURI, String localName, String qName,
			Attributes attributes) throws SAXException {
		String tagName = getTagName(localName, qName);

		currentValue = new StringBuilder();

		if (tagName.equals("click")) {
			currentClick = new Click(fr);
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
		} else if (tagName.equals("click")) {
			clickList.add(currentClick);
			currentClick = null;
		} else {
			if (inThing) {
				if (tagName.equals("id")) {
					currentClick.thingId = value;
				} else if (tagName.equals("title")) {
					currentClick.thingTitle = value;
				} else if (tagName.equals("url")) {
					currentClick.thingUrl = value;
				}
			} else {
				if (tagName.equals("id")) {
					currentClick.id = Integer.parseInt(value);
				} else if (tagName.equals("click_time")) {
					currentClick.date = new Date(Long.parseLong(value) * 1000L);
				}
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		currentValue.append(ch, start, length);
	}
}
