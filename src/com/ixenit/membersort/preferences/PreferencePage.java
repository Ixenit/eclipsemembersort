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