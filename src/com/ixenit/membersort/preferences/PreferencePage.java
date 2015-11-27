package com.ixenit.membersort.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.ixenit.membersort.Activator;
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

		addField(new ListEditor(PreferenceConstants.P_ORDER, "Test", parent) {

			@Override
			protected String createList(String[] items) {
				StringBuilder sb = new StringBuilder();

				for (String item : items) {
					sb.append(PreferenceConstants.SEPARATOR);
					sb.append(item);

				}

				sb.deleteCharAt(0);

				return sb.toString();
			}

			@Override
			protected String[] parseString(String stringList) {
				return stringList.split(PreferenceConstants.SEPARATOR);
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