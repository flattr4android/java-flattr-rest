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
import java.util.ArrayList;

public class CategoryTest extends FlattrRestTestCase {
	
	public void testBuildCategories() throws FlattrRestException {
		String content = 
				"<?xml version=\"1.0\" encoding=\"utf-8\"?>" + 
				"<flattr>" +
				"  <version>0.0.1</version>" +
				"  <categories>" +
				"    <category>" +
				"      <id>text</id>" +
				"      <name>Written text</name>" +
				"    </category>" +
				"    <category>" +
				"      <id>images</id>" +
				"      <name>Images</name>" +
				"    </category>" +
				"  </categories>" +
				"</flattr>";
		ArrayList<Category> langs = Category
				.buildCategories(new ByteArrayInputStream(content.getBytes()));
		assertEquals(2, langs.size());
		assertEquals("text", langs.get(0).getId());
		assertEquals("Written text", langs.get(0).getName());
		assertEquals("images", langs.get(1).getId());
		assertEquals("Images", langs.get(1).getName());
	}

}
