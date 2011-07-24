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

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Represent a single category.See <a
 * href="http://developers.flattr.net/doku.php/general_info_methods">Flattr API
 * documentation</a>.
 */
public class Category {

	protected String id;
	protected String name;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return getName() + " (" + getId() + ")";
	}

	public static ArrayList<Category> buildCategories(InputStream xmlDescription)
			throws FlattrRestException {
		ArrayList<Category> al = new ArrayList<Category>();

		try {
			XMLReader parser = SAXParserFactory.newInstance().newSAXParser()
					.getXMLReader();
			parser.setContentHandler(new CategorySAXHandler(al));
			parser.parse(new InputSource(xmlDescription));
		} catch (Exception e) {
			throw new FlattrRestException(e);
		}

		return al;
	}

}

class CategorySAXHandler extends PortableSAXHandler {
	private StringBuilder currentValue = new StringBuilder();

	private ArrayList<Category> categoryList;
	private Category currentCategory = null;

	public CategorySAXHandler(ArrayList<Category> categoryList) {
		this.categoryList = categoryList;
	}

	@Override
	public void startElement(String nsURI, String localName, String qName,
			Attributes attributes) throws SAXException {
		currentValue = new StringBuilder();

		if (qName.equals("category")) {
			currentCategory = new Category();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		String tagName = getTagName(localName, qName);

		String value = currentValue.toString().trim();

		if ((tagName.equals("category")) && (currentCategory != null)) {
			categoryList.add(currentCategory);
		} else if (tagName.equals("id")) {
			currentCategory.id = value;
		} else if (tagName.equals("name")) {
			currentCategory.name = value;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		currentValue.append(ch, start, length);
	}
}
