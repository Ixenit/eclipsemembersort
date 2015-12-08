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
package com.ixenit.membersort.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.ixenit.membersort.Activator;

/**
 * Class used to initialize default preference values.
 *
 * @author Benjámin Hajnal <benjamin.hajnal@ixenit.com>
 *
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		StringBuilder sb = new StringBuilder();

		for (String string : _DEFAULT_ORDER) {
			sb.append(PreferenceConstants.SEPARATOR).append(string);
		}

		sb.deleteCharAt(0);

		store.setDefault(PreferenceConstants.P_ORDER, sb.toString());
		store.setDefault(PreferenceConstants.P_ORDER_BY_NAME, true);
	}

	private static String[] _DEFAULT_ORDER = {

			// PUBLIC
			"public static final variable", "public static variable",

			"public static method", "public constructor", "public method",

			// PACKAGE
			"package static method", "package constructor", "package method",

			"package static final variable", "package static variable", "package variable", "package final variable",

			// PROTECTED
			"protected static method", "protected constructor", "protected method",

			"protected static final variable", "protected static variable", "protected variable", "protected final variable",

			// PRIVATE
			"private static method", "private constructor", "private method",

			"public variable", "public final variable",

			// "private static final log", "private static final instance",

			"private static final variable", "private static variable", "private variable", "private final variable",

			"static init",

			// CLASSES

			"public static class", "public class", "protected static class", "protected class", "private static class", "private class",

			// ENUMS
			"public enum", "package enum", "protected enum", "private enum" };

}
