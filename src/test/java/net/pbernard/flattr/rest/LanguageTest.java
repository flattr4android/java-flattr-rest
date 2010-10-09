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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import net.pbernard.flattr.rest.FlattrRestException;
import net.pbernard.flattr.rest.Language;

public class LanguageTest extends FlattrRestTestCase {

	public void testBuildLanguages() throws FlattrRestException {
		String content = 
				"<?xml version=\"1.0\" encoding=\"utf-8\"?>" + 
				"<flattr>" + 
				"  <version>0.0.1</version>" + 
				"  <languages>" + 
				"    <language>" + 
				"      <id>sq_AL</id>" + 
				"      <name>Albanian</name>" + 
				"    </language>" + 
				"    <language>" + 
				"      <id>ar_DZ</id>" + 
				"      <name>Arabic</name>" + 
				"    </language>" + 
				"  </languages>" + 
				"</flattr>";
		ArrayList<Language> langs = Language
				.buildLanguages(new ByteArrayInputStream(content.getBytes()));
		assertEquals(2, langs.size());
		assertEquals("sq_AL", langs.get(0).getId());
		assertEquals("Albanian", langs.get(0).getName());
		assertEquals("ar_DZ", langs.get(1).getId());
		assertEquals("Arabic", langs.get(1).getName());
	}
}
