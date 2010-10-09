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
package net.pbernard.flattr.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

/**
 * Represent a thing. See <a
 * href="http://developers.flattr.net/doku.php/thing_methods">Flattr API
 * documentation</a>.
 */
public class Thing {

	public static final String STATUS_OK = "ok";
	public static final String STATUS_OWNER = "owner";
	public static final String STATUS_INACTIVE = "inactive";
	public static final String STATUS_CLICKED = "clicked";

	String id;
	Date created;
	String language;
	String url;
	String title;
	String story;
	int clicks;
	int userId;
	String userName;
	ArrayList<String> tags = new ArrayList<String>();
	String categoryId;
	String categoryName;
	String status;

	FlattrRestClient fr;

	public Thing(FlattrRestClient fr) {
		this.fr = fr;
	}

	/**
	 * Build a thing for registration purpose.
	 */
	public Thing() {
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setCategory(String category) {
		this.categoryName = category;
	}

	public void setDescription(String description) {
		this.story = description;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void addTag(String tag) {
		this.tags.add(tag);
	}

	public String getId() {
		return id;
	}

	public Date getCreationDate() {
		return created;
	}

	public String getLanguage() {
		return language;
	}

	public String getURL() {
		return url;
	}

	public String getTitle() {
		return title;
	}

	public String getStory() {
		return story;
	}

	public int getClicks() {
		return clicks;
	}

	public int getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	public ArrayList<String> getTags() {
		return tags;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public String getStatus() {
		return status;
	}

	public String toString() {
		return getTitle() + " - by " + getUserName();
	}

	public void click() throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			FlattrServerResponseException, ParserConfigurationException,
			SAXException, IOException {
		fr.clickThing(getId());
	}

	public static ArrayList<Thing> buildThings(FlattrRestClient fr,
			InputStream xmlDescription) throws FlattrRestException {
		ArrayList<Thing> al = new ArrayList<Thing>();

		try {
			XMLReader parser = SAXParserFactory.newInstance().newSAXParser()
					.getXMLReader();
			parser.setContentHandler(new ThingSAXHandler(fr, al));
			parser.parse(new InputSource(xmlDescription));
		} catch (Exception e) {
			throw new FlattrRestException(e);
		}

		return al;
	}

	public static Thing buildOneThing(FlattrRestClient fr, InputStream is)
			throws FlattrRestException {
		ArrayList<Thing> al = buildThings(fr, is);
		if (al.size() != 1) {
			throw new FlattrRestException(
					"Unexpected amount of things in the stream: " + al.size());
		}
		return al.get(0);
	}

}

class ThingSAXHandler extends DefaultHandler {
	private boolean inUser = false;
	private boolean inCategory = false;
	private StringBuilder currentValue = new StringBuilder();

	private FlattrRestClient fr;
	private ArrayList<Thing> thingList;
	private Thing currentThing = null;

	public ThingSAXHandler(FlattrRestClient fr, ArrayList<Thing> thingList) {
		this.thingList = thingList;
		this.fr = fr;
	}

	@Override
	public void startElement(String nsURI, String localName, String qName,
			Attributes attributes) throws SAXException {
		currentValue = new StringBuilder();

		if (qName.equals("thing")) {
			currentThing = new Thing(fr);
		} else if (qName.equals("user")) {
			inUser = true;
		} else if (qName.equals("category")) {
			inCategory = true;
		}
	}

	@Override
	public void endElement(String uri, String localName, String tagName)
			throws SAXException {
		String value = currentValue.toString().trim();

		if (tagName.equals("user")) {
			inUser = false;
		} else if (tagName.equals("category")) {
			inCategory = false;
		} else if (tagName.equals("thing")) {
			thingList.add(currentThing);
			currentThing = null;
		} else {
			if (inUser) {
				if (tagName.equals("id")) {
					currentThing.userId = Integer.parseInt(value);
				} else if (tagName.equals("username")) {
					currentThing.userName = value;
				}
			} else if (inCategory) {
				if (tagName.equals("id")) {
					currentThing.categoryId = value;
				} else if (tagName.equals("name")) {
					currentThing.categoryName = value;
				}
			} else {
				if (tagName.equals("tag")) {
					currentThing.tags.add(value);
				} else if (tagName.equals("id")) {
					currentThing.id = value;
				} else if (tagName.equals("created")) {
					currentThing.created = new Date(Long.parseLong(value));
				} else if (tagName.equals("language")) {
					currentThing.language = value;
				} else if (tagName.equals("url")) {
					currentThing.url = value;
				} else if (tagName.equals("title")) {
					currentThing.title = value;
				} else if (tagName.equals("story")) {
					currentThing.story = value;
				} else if (tagName.equals("clicks")) {
					currentThing.clicks = Integer.parseInt(value);
				} else if (tagName.equals("username")) {
					currentThing.userName = value;
				} else if (tagName.equals("tag")) {
					currentThing.tags.add(value);
				} else if (tagName.equals("status")) {
					currentThing.status = value;
				}
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		currentValue.append(ch, start, length);
	}
}
