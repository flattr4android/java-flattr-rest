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

import java.util.ArrayList;
import java.util.Date;

import net.pbernard.flattr.rest.FlattrRestException;
import net.pbernard.flattr.rest.Thing;

public class ThingTest extends FlattrRestTestCase {

	public void testBuildOneThing() throws FlattrRestException {
		Thing t = Thing.buildOneThing(null, getClass().getClassLoader()
				.getResourceAsStream("one_thing.xml"));
		assertEquals("bf12b55dc73d89835fff9696b6cc3883", t.getId());
		assertEquals(new Date(1276784931*1000L), t.getCreationDate());
		assertEquals("sv_SE", t.getLanguage());
		assertEquals("http://www.kontilint.se/kontakt", t.getURL());
		assertEquals("Kontakta Kontilint", t.getTitle());
		assertEquals("Kontakta Kontilint", t.getDescription());
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
		ArrayList<Thing> things = Thing.buildThings(null, getClass()
				.getClassLoader().getResourceAsStream("two_things.xml"));

		Thing t = things.get(0);
		assertEquals("bf12b55dc73d89835fff9696b6cc3883", t.getId());
		assertEquals(new Date(1276784931*1000L), t.getCreationDate());
		assertEquals("sv_SE", t.getLanguage());
		assertEquals("http://www.kontilint.se/kontakt", t.getURL());
		assertEquals("Kontakta Kontilint", t.getTitle());
		assertEquals("Kontakta Kontilint", t.getDescription());
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
		assertEquals(new Date(1276784931*1000L), t.getCreationDate());
		assertEquals("us_EN", t.getLanguage());
		assertEquals("http://www.rotatepdf.net", t.getURL());
		assertEquals("Rotate PDF for free", t.getTitle());
		assertEquals("Rotate PDF online for free", t.getDescription());
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

	public void testExtractThingIDFromQRCode() {
		// Classic case
		assertEquals(
				"7e4c65bfab8ee31e7d79f4d3b7bcfe19",
				Thing.extractThingIDFromQRCode("https://flattr.com/thing/7e4c65bfab8ee31e7d79f4d3b7bcfe19"));

		// Extra slash
		assertEquals(
				"7e4c65bfab8ee31e7d79f4d3b7bcfe19",
				Thing.extractThingIDFromQRCode("https://flattr.com/thing/7e4c65bfab8ee31e7d79f4d3b7bcfe19/"));

		// Not a correct format, but it should still work
		assertEquals(
				"7e4c65bfab8ee31e7d79f4d3b7bcfe19",
				Thing.extractThingIDFromQRCode("https://flattr.com/7e4c65bfab8ee31e7d79f4d3b7bcfe19"));

		// Fancy protocol and host
		assertEquals(
				"7e4c65bfab8ee31e7d79f4d3b7bcfe19",
				Thing.extractThingIDFromQRCode("htTtp://mysite.com/thing/7e4c65bfab8ee31e7d79f4d3b7bcfe19"));

		// Bad format
		assertEquals(null, Thing.extractThingIDFromQRCode("This is not a URL"));
	}

}
