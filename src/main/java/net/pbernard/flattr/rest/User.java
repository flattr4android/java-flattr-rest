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

import javax.xml.parsers.SAXParserFactory;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Represent a user. See <a
 * href="http://developers.flattr.net/doku.php/user_methods"> Flattr API
 * documentation</a>.
 */
public class User {
	protected int id;
	protected String userName;
	protected String firstName, lastName;
	protected String city;
	protected String country;
	protected String avatarUrl;
	protected String email;
	protected String description;
	protected int thingCount;

	FlattrRestClient fr;

	private User(FlattrRestClient fr) {
		this.fr = fr;
	}

	public ArrayList<Thing> getThings() throws OAuthMessageSignerException,
			OAuthExpectationFailedException, OAuthCommunicationException,
			FlattrRestException, IOException {
		return fr.getUserThings(getId());
	}

	public int getId() {
		return id;
	}

	public String getUserName() {
		return userName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getCity() {
		return city;
	}

	public String getCountry() {
		return country;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public String getDescription() {
		return description;
	}

	public String getEmail() {
		return email;
	}

	public int getThingCount() {
		return thingCount;
	}

	public String toString() {
		return getUserName()
				+ (((getFirstName() != null) && (getLastName() != null)) ? (" ("
						+ getFirstName() + " " + getLastName() + ")")
						: "");
	}

	public static User buildUser(FlattrRestClient fr, InputStream xmlDescription)
			throws FlattrRestException {
		User user = new User(fr);

		try {
			XMLReader parser = SAXParserFactory.newInstance().newSAXParser()
					.getXMLReader();
			parser.setContentHandler(new UserSAXHandler(user));
			parser.parse(new InputSource(xmlDescription));
		} catch (Exception e) {
			throw new FlattrRestException(e);
		}

		return user;
	}
}

// using SAX
class UserSAXHandler extends DefaultHandler {
	private User user;
	private StringBuilder currentValue = new StringBuilder();

	public UserSAXHandler(User user) {
		this.user = user;
	}

	@Override
	public void startElement(String nsURI, String localName, String tagName,
			Attributes attributes) throws SAXException {
		currentValue = new StringBuilder();
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		currentValue.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String tagName)
			throws SAXException {
		String value = currentValue.toString().trim();

		if (tagName.equalsIgnoreCase("id")) {
			user.id = Integer.parseInt(value);
		} else if (tagName.equalsIgnoreCase("username")) {
			user.userName = value;
		} else if (tagName.equalsIgnoreCase("firstname")) {
			user.firstName = value;
		} else if (tagName.equalsIgnoreCase("lastname")) {
			user.lastName = value;
		} else if (tagName.equalsIgnoreCase("city")) {
			user.city = value;
		} else if (tagName.equalsIgnoreCase("country")) {
			user.country = value;
		} else if (tagName.equalsIgnoreCase("gravatar")) {
			user.avatarUrl = value;
		} else if (tagName.equalsIgnoreCase("email")) {
			user.email = value;
		} else if (tagName.equalsIgnoreCase("description")) {
			user.description = value;
		} else if (tagName.equalsIgnoreCase("thingcount")) {
			user.thingCount = Integer.parseInt(value);
		}
	}
}
