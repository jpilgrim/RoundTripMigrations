/*
 * Copyright (c) 2018 NumberFour AG, Luca Beurer-Kellner
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Luca Beurer-Kellner - Initial API and implementation
 */
package com.enfore.n4js.n4idl.roundtrip.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Storage for a list of most-recently run execution results
 * of the RoundTrip Runner.
 */
public class RoundTripResultStorage {
	private static List<RoundTripResult> results = new ArrayList<>();
	private static Map<String, RoundTripResult> mappedResults = new HashMap<>();

	private static final int MAX_HISTORY = 20;

	/**
	 * Add a new result.
	 */
	public static void addResult(String module, String data) {
		RoundTripResult result = new RoundTripResult(module, data);
		results.add(result);
		mappedResults.put(module, result);

		// Only store up to {@link RoundTripResultStorage.MAX_HISTORY} many
		// result elements.
		while (results.size() > MAX_HISTORY) {
			RoundTripResult front = results.get(0);
			// remove from map iff back is most recent result for back.module
			if (mappedResults.get(front.module) == front) {
				mappedResults.remove(front.module);
			}
			results.remove(front);
		}
	}

	/**
	 * Returns the most-recent execution result.
	 */
	public static RoundTripResult getLatest() {
		return results.isEmpty() ? null : results.get(results.size() - 1);
	}

	private RoundTripResultStorage() {
		// static class
	}

	/**
	 * A RoundTrip Runner result.
	 */
	public static class RoundTripResult {
		private String module;
		private String data;

		public RoundTripResult(String module, String data) {
			this.module = module;
			this.data = data;
		}
		/**
		 * @return the module
		 */
		public String getModule() {
			return module;
		}
		/**
		 * @param module the module to set
		 */
		public void setModule(String module) {
			this.module = module;
		}
		/**
		 * @return the data
		 */
		public String getData() {
			return data;
		}
		/**
		 * @param data the data to set
		 */
		public void setData(String data) {
			this.data = data;
		}
	}
}
