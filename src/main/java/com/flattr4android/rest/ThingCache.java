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

import java.util.Hashtable;
import java.util.Map.Entry;

public class ThingCache {

	private Hashtable<String, CacheEntry> cache = new Hashtable<String, CacheEntry>();
	private boolean enabled = false;
	private int defaultMaxAge = 300;

	/**
	 * Add of refresh the cache with this thing.
	 */
	public void addOrRefheshThing(Thing thing) {
		if (enabled) {
			cache.put(thing.getId(),
					new CacheEntry(thing, System.currentTimeMillis() / 1000));
		}
	}

	/**
	 * Clear all entries.
	 */
	public void clear() {
		cache.clear();
	}

	/**
	 * Clear old entries.
	 * 
	 * @param maxAge
	 *            Maximum cache entry age in seconds.
	 */
	public void clear(long maxAge) {
		long currentTime = System.currentTimeMillis() / 1000;
		for (Entry<String, CacheEntry> entry : cache.entrySet()) {
			if (entry.getValue().cacheDate < currentTime - maxAge) {
				cache.remove(entry.getKey());
			}
		}
	}

	/**
	 * Returned a cached thing.
	 * 
	 * @see ThingCache#getThingById(String, long)
	 * @see ThingCache#getDefaultMaxAge()
	 */
	public Thing getThingById(String id) {
		return getThingById(id, getDefaultMaxAge());
	}

	/**
	 * Return a cached thing.
	 * 
	 * @param id
	 * @param maxAge
	 *            Maximum cache entry age in seconds.
	 * 
	 * @return The cached thing, or null if the cache is not enabled or if the
	 *         thing was never cached or if the entry is too old.
	 */
	public Thing getThingById(String id, long maxAge) {
		if (!enabled) {
			return null;
		}

		CacheEntry entry = cache.get(id);
		if (entry == null) {
			return null;
		}
		if (entry.cacheDate < System.currentTimeMillis() / 1000 - maxAge) {
			return null;
		}
		return entry.thing;
	}

	/**
	 * When the cache is not enabled, it does not keep trace of things.
	 * 
	 * @see ThingCache#addOrRefheshThing(Thing)
	 * @see ThingCache#getThingById(String, long)
	 */
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Return the age used when none is specified.
	 * 
	 * @see ThingCache#getThingById(String)
	 */
	public int getDefaultMaxAge() {
		return defaultMaxAge;
	}

	public void setDefaultMaxAge(int defaultMaxAge) {
		this.defaultMaxAge = defaultMaxAge;
	}

}

class CacheEntry {
	public Thing thing;
	public long cacheDate;

	public CacheEntry(Thing thing, long cacheDate) {
		this.thing = thing;
		this.cacheDate = cacheDate;
	}
}
