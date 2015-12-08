/*******************************************************************************
 * Copyright 2015 Ixenit
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package com.ixenit.membersort.preferences.converter;

import com.ixenit.membersort.preferences.PreferenceConstants;

/**
 *
 * @author bhajnal
 *
 */
public class OrderConverter {

	public static String convert(String[] items) {
		StringBuilder sb = new StringBuilder();

		for (String item : items) {
			sb.append(PreferenceConstants.SEPARATOR);
			sb.append(item);
		}

		sb.deleteCharAt(0);

		return sb.toString();
	}

	public static String[] convert(String stringList) {
		return stringList.split(PreferenceConstants.SEPARATOR);
	}

	private OrderConverter() {
		// Needed for instantialization
	}

}
