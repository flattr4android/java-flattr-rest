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

public class SubscriptionTest extends FlattrRestTestCase {

	public void testBuildOneSubscription() throws FlattrRestException {
		Subscription s = Subscription.buildOneSubscription(null, getClass()
				.getClassLoader().getResourceAsStream("one_subscription.xml"));
		assertEquals(12345, s.getId());
		assertEquals(6, s.getMonths());
		assertEquals(4, s.getMonthsLeft());
		assertEquals(new Date(28734787237L * 1000L), s.getCreationDate());
		assertEquals("0282de399c3892aa19165e1a0baaccdc", s.getThingId());
		assertEquals("My little pony fan club", s.getThingTitle());
		assertEquals("http://mylittleponyfanclub.com", s.getThingURL());
	}

	public void testBuildSubscriptions() throws FlattrRestException {
		ArrayList<Subscription> list = Subscription.buildSubscriptions(
				null,
				getClass().getClassLoader().getResourceAsStream(
						"two_subscriptions.xml"));

		assertEquals(2, list.size());

		Subscription s = list.get(0);
		assertEquals(12345, s.getId());
		assertEquals(6, s.getMonths());
		assertEquals(4, s.getMonthsLeft());
		assertEquals(new Date(28734787237L * 1000L), s.getCreationDate());
		assertEquals("0282de399c3892aa19165e1a0baaccdc", s.getThingId());
		assertEquals("My little pony fan club", s.getThingTitle());
		assertEquals("http://mylittleponyfanclub.com", s.getThingURL());

		s = list.get(1);
		assertEquals(54321, s.getId());
		assertEquals(7, s.getMonths());
		assertEquals(3, s.getMonthsLeft());
		assertEquals(new Date(1276784931L * 1000L), s.getCreationDate());
		assertEquals("bf12b55dc73d89835fff9696b6cc3883", s.getThingId());
		assertEquals("Kontakta Kontilint", s.getThingTitle());
		assertEquals("http://www.kontilint.se/kontakt", s.getThingURL());
	}
}
