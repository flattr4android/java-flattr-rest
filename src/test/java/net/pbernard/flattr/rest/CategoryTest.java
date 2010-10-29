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

import net.pbernard.flattr.rest.Category;
import net.pbernard.flattr.rest.FlattrRestException;

public class CategoryTest extends FlattrRestTestCase {

	public void testBuildCategories() throws FlattrRestException {
		ArrayList<Category> langs = Category.buildCategories(getClass()
				.getClassLoader().getResourceAsStream("one_category.xml"));
		assertEquals(2, langs.size());
		assertEquals("text", langs.get(0).getId());
		assertEquals("Written text", langs.get(0).getName());
		assertEquals("images", langs.get(1).getId());
		assertEquals("Images", langs.get(1).getName());
	}
}
