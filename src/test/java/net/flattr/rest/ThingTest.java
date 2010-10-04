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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;

public class ThingTest extends FlattrRestTestCase {

	public void testBuildOneThing() throws FlattrRestException {
		String xmlDoc = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<flattr>" + "<version>0.0.1</version>" + "<thing>"
				+ "<id>bf12b55dc73d89835fff9696b6cc3883</id>"
				+ "<created>1276784931</created>"
				+ "<language>sv_SE</language>"
				+ "<url>http://www.kontilint.se/kontakt</url>"
				+ "<title>Kontakta Kontilint</title>"
				+ "<story><![CDATA[Kontakta Kontilint]]></story>"
				+ "<clicks>0</clicks>" + "<user>" + "<id>244</id>"
				+ "<username>Bomelin</username>" + "</user>" + "<tags>"
				+ "<tag>asd</tag>" + "<tag>fgh</tag>" + "<tag>ert</tag>"
				+ "</tags>" + "<category>" + "<id>text</id>"
				+ "<name>Written text</name>" + "</category>"
				+ "<status>owner</status>" + "</thing>" + "</flattr>";
		InputStream is = new ByteArrayInputStream(xmlDoc.getBytes());
		Thing t = Thing.buildOneThing(null, is);
		assertEquals("bf12b55dc73d89835fff9696b6cc3883", t.getId());
		assertEquals(new Date(1276784931), t.getCreationDate());
		assertEquals("sv_SE", t.getLanguage());
		assertEquals("http://www.kontilint.se/kontakt", t.getURL());
		assertEquals("Kontakta Kontilint", t.getTitle());
		assertEquals("Kontakta Kontilint", t.getStory());
		assertEquals(0, t.getClicks());
		assertEquals(244, t.getUserId());
		assertEquals("Bomelin", t.getUserName());
		ArrayList<String> tags = new ArrayList<String>();
		tags.add("asd");
		tags.add("fgh");
		tags.add("ert");
		assertEquals(tags, t.getTags());
		assertEquals("text", t.getCategoryId());
		assertEquals("Written text", t.getCategoryName());
		assertEquals("owner", t.getStatus());
	}

	public void testBuildThings() throws FlattrRestException {
		String xmlDoc = 
			"<?xml version=\"1.0\" encoding=\"utf-8\"?>" + 
			"<flattr>" + 
			"  <version>0.0.1</version>" + 
			"  <thing>" + 
			"    <id>bf12b55dc73d89835fff9696b6cc3883</id>" + 
			"    <created>1276784931</created>" + 
			"    <language>sv_SE</language>" + 
			"    <url>http://www.kontilint.se/kontakt</url>" + 
			"    <title>Kontakta Kontilint</title>" + 
			"    <story><![CDATA[Kontakta Kontilint]]></story>" + 
			"    <clicks>0</clicks>" + 
			"    <user>" + 
			"      <id>244</id>" + 
			"      <username>Bomelin</username>" + 
			"    </user>" + 
			"    <tags>" + 
			"      <tag>asd</tag>" + 
			"      <tag>fgh</tag>" + 
			"      <tag>ert</tag>" + 
			"    </tags>" + 
			"    <category>" + 
			"      <id>text</id>" + 
			"      <name>Written text</name>" + 
			"    </category>" + 
			"    <status>owner</status>" + 
			"  </thing>" + 
			"  <thing>" + 
			"    <id>1e3337f323197c97814dc807eff39aa5</id>" + 
			"    <created>1276784931</created>" + 
			"    <language>us_EN</language>" + 
			"    <url>http://www.rotatepdf.net</url>" + 
			"    <title>Rotate PDF for free</title>" + 
			"    <story><![CDATA[Rotate PDF online for free]]></story>" + 
			"    <clicks>3</clicks>" + 
			"    <user>" + 
			"      <id>12345</id>" + 
			"      <username>pbernard</username>" + 
			"    </user>" + 
			"    <tags>" + 
			"      <tag>rotate</tag>" + 
			"      <tag>pdf</tag>" +  
			"    </tags>" + 
			"    <category>" + 
			"      <id>rest</id>" + 
			"      <name>The rest</name>" + 
			"    </category>" + 
			"    <status>ok</status>" + 
			"  </thing>" + 
			"</flattr>";
		InputStream is = new ByteArrayInputStream(xmlDoc.getBytes());

		ArrayList<Thing> things = Thing.buildThings(null, is);

		Thing t = things.get(0);
		assertEquals("bf12b55dc73d89835fff9696b6cc3883", t.getId());
		assertEquals(new Date(1276784931), t.getCreationDate());
		assertEquals("sv_SE", t.getLanguage());
		assertEquals("http://www.kontilint.se/kontakt", t.getURL());
		assertEquals("Kontakta Kontilint", t.getTitle());
		assertEquals("Kontakta Kontilint", t.getStory());
		assertEquals(0, t.getClicks());
		assertEquals(244, t.getUserId());
		assertEquals("Bomelin", t.getUserName());
		ArrayList<String> tags = new ArrayList<String>();
		tags.add("asd");
		tags.add("fgh");
		tags.add("ert");
		assertEquals(tags, t.getTags());
		assertEquals("text", t.getCategoryId());
		assertEquals("Written text", t.getCategoryName());
		assertEquals("owner", t.getStatus());

		t = things.get(1);
		assertEquals("1e3337f323197c97814dc807eff39aa5", t.getId());
		assertEquals(new Date(1276784931), t.getCreationDate());
		assertEquals("us_EN", t.getLanguage());
		assertEquals("http://www.rotatepdf.net", t.getURL());
		assertEquals("Rotate PDF for free", t.getTitle());
		assertEquals("Rotate PDF online for free", t.getStory());
		assertEquals(3, t.getClicks());
		assertEquals(12345, t.getUserId());
		assertEquals("pbernard", t.getUserName());
		tags = new ArrayList<String>();
		tags.add("rotate");
		tags.add("pdf");
		assertEquals(tags, t.getTags());
		assertEquals("rest", t.getCategoryId());
		assertEquals("The rest", t.getCategoryName());
		assertEquals("ok", t.getStatus());
	}
}
