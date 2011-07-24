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

public class LanguageTest extends FlattrRestTestCase {

	public void testBuildLanguages() throws FlattrRestException {
		ArrayList<Language> langs = Language.buildLanguages(getClass()
				.getClassLoader().getResourceAsStream("one_language.xml"));
		assertEquals(2, langs.size());
		assertEquals("sq_AL", langs.get(0).getId());
		assertEquals("Albanian", langs.get(0).getName());
		assertEquals("ar_DZ", langs.get(1).getId());
		assertEquals("Arabic", langs.get(1).getName());
	}
}
