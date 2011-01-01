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
package com.flattr4android.rest;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Represent a single language. See <a
 * href="http://developers.flattr.net/doku.php/general_info_methods">Flattr API
 * documentation</a>.
 */
public class Language {

	protected String id;
	protected String name;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return getId() + " " + getName();
	}

	public static ArrayList<Language> buildLanguages(InputStream xmlDescription)
			throws FlattrRestException {
		ArrayList<Language> al = new ArrayList<Language>();

		try {
			XMLReader parser = SAXParserFactory.newInstance().newSAXParser()
					.getXMLReader();
			parser.setContentHandler(new LanguageSAXHandler(al));
			parser.parse(new InputSource(xmlDescription));
		} catch (Exception e) {
			throw new FlattrRestException(e);
		}

		return al;
	}

}

class LanguageSAXHandler extends PortableSAXHandler {
	private StringBuilder currentValue = new StringBuilder();

	private ArrayList<Language> languageList;
	private Language currentLanguage = null;

	public LanguageSAXHandler(ArrayList<Language> languageList) {
		this.languageList = languageList;
	}

	@Override
	public void startElement(String nsURI, String localName, String tagName,
			Attributes attributes) throws SAXException {
		currentValue = new StringBuilder();

		if (tagName.equals("language")) {
			currentLanguage = new Language();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		String tagName = getTagName(localName, qName);
		
		String value = currentValue.toString().trim();

		if ((tagName.equals("language")) && (currentLanguage != null)) {
			languageList.add(currentLanguage);
		} else if (tagName.equals("id")) {
			currentLanguage.id = value;
		} else if (tagName.equals("name")) {
			currentLanguage.name = value;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		currentValue.append(ch, start, length);
	}
}
