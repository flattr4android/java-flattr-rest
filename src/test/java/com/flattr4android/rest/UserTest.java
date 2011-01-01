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

import net.pbernard.flattr.rest.FlattrRestException;
import net.pbernard.flattr.rest.User;

public class UserTest extends FlattrRestTestCase {

	public void testBuildUser() throws FlattrRestException {
		User u = User.buildUser(null, getClass().getClassLoader()
				.getResourceAsStream("one_user.xml"));
		assertEquals(244, u.getId());
		assertEquals("bomelin", u.getUserName());
		assertEquals("Mattias", u.getFirstName());
		assertEquals("Bomelin", u.getLastName());
		assertEquals("Skurup", u.getCity());
		assertEquals("Sweden", u.getCountry());
		assertEquals(
				"https://secure.gravatar.com/avatar/59bc275a1d17a4f2ec448538426803bf?s=120&r=pg",
				u.getAvatarUrl());
		assertEquals("mattias@flattr.com", u.getEmail());
		assertEquals("Flattr meee", u.getDescription());
		assertEquals(10, u.getThingCount());
	}
}
