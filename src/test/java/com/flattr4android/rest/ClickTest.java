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

import java.util.ArrayList;
import java.util.Date;

public class ClickTest extends FlattrRestTestCase {

	public void testBuildClicks() throws FlattrRestException {
		ArrayList<Click> clicks = Click.buildClicks(null, getClass()
				.getClassLoader().getResourceAsStream("three_clicks.xml"));

		Click c = clicks.get(0);
		assertEquals(12345, c.getId());
		assertEquals(new Date(1291133253 * 1000L), c.getDate());
		assertEquals("0282de399c3892aa19165e1a0baaccdc", c.getThingId());
		assertEquals("Free Brokep", c.getThingTitle());
		assertEquals("http://blog.flattr.net/2010/11/free-brokep/",
				c.getThingURL());

		c = clicks.get(1);
		assertEquals(12346, c.getId());
		assertEquals(new Date(1291133271 * 1000L), c.getDate());
		assertEquals("1e3337f323197c97814dc807eff39aa5", c.getThingId());
		assertEquals("Rotate PDF for free", c.getThingTitle());
		assertEquals("http://www.rotatepdf.net", c.getThingURL());

		c = clicks.get(2);
		assertEquals(12349, c.getId());
		assertEquals(new Date(1290135221 * 1000L), c.getDate());
		assertEquals("bf12b55dc73d89835fff9696b6cc3883", c.getThingId());
		assertEquals("Kontakta Kontilint", c.getThingTitle());
		assertEquals("http://www.kontilint.se/kontakt", c.getThingURL());
	}

}
