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
