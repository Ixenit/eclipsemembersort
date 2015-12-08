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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.ixenit.membersort.Activator;
import com.ixenit.membersort.preferences.converter.OrderConverter;
import com.ixenit.membersort.preferences.editor.ListEditor;

/**
 *
 * @author Benjámin Hajnal <benjamin.hajnal@ixenit.com>
 *
 */
public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	public void createFieldEditors() {
		Composite parent = getFieldEditorParent();

		addField(
			new BooleanFieldEditor(PreferenceConstants.P_ORDER_BY_NAME, "Sort by name", parent));

		addField(new ListEditor(PreferenceConstants.P_ORDER, "Order of members", parent) {

			@Override
			protected String createList(String[] items) {
				return OrderConverter.convert(items);
			}

			@Override
			protected String[] parseString(String stringList) {
				return OrderConverter.convert(stringList);
			}

		});

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
	}

}