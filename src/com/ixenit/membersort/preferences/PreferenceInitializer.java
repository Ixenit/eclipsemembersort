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
	}

	private static String[] _DEFAULT_ORDER = {

			// PUBLIC
			"public_static_final_variable", "public_static_variable",

			"public_static_method", "public_constructor", "public_method",

			// PACKAGE
			"package_static_method", "package_constructor", "package_method",

			"package_static_final_variable", "package_static_variable", "package_variable", "package_final_variable",

			// PROTECTED
			"protected_static_method", "protected_constructor", "protected_method",

			"protected_static_final_variable", "protected_static_variable", "protected_variable", "protected_final_variable",

			// PRIVATE
			"private_static_method", "private_constructor", "private_method",

			"public_variable", "public_final_variable",

			// "private_static_final_log", "private_static_final_instance",

			"private_static_final_variable", "private_static_variable", "private_variable", "private_final_variable",

			"static_init",

			// CLASSES

			"public_static_class", "public_class", "protected_static_class", "protected_class", "private_static_class", "private_class",

			// ENUMS
			"public_enum", "package_enum", "protected_enum", "private_enum" };

}
