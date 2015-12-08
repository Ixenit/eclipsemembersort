/*******************************************************************************
 * Copyright 2015 Ixenit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.ixenit.membersort.preferences.editor;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import com.ixenit.membersort.preferences.dialog.ModifierDialog;

/**
 *
 * @author Benjámin Hajnal <benjamin.hajnal@ixenit.com>
 *
 */
public abstract class ListEditor extends FieldEditor {

	/**
	 * Creates a selection listener.
	 */
	public void createSelectionListener() {
		_selectionListener = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				Widget widget = event.widget;
				if (widget == _addButton) {
					_addPressed();
				}
				else if (widget == _removeButton) {
					_removePressed();
				}
				else if (widget == _modifyButton) {
					_modifyPressed();
				}
				else if (widget == _upButton) {
					_upPressed();
				}
				else if (widget == _downButton) {
					_downPressed();
				}
				else if (widget == _list) {
					selectionChanged();
				}
			}
		};
	}

	/**
	 * Returns this field editor's button box containing the Add, Remove,
	 * Up, and Down button.
	 *
	 * @param parent
	 *        the parent control
	 * @return the button box
	 */
	public Composite getButtonBoxControl(Composite parent) {
		if (_buttonBox == null) {
			_buttonBox = new Composite(parent, SWT.NULL);
			GridLayout layout = new GridLayout();
			layout.marginWidth = 0;
			_buttonBox.setLayout(layout);
			_createButtons(_buttonBox);
			_buttonBox.addDisposeListener(new DisposeListener() {

				@Override
				public void widgetDisposed(DisposeEvent paramDisposeEvent) {
					_addButton = null;
					_removeButton = null;
					_modifyButton = null;
					_upButton = null;
					_downButton = null;
					_buttonBox = null;
				}
			});
		}
		else {
			checkParent(_buttonBox, parent);
		}

		selectionChanged();
		return _buttonBox;
	}

	/**
	 * Returns this field editor's list control.
	 *
	 * @param parent
	 *        the parent control
	 * @return the list control
	 */
	public List getListControl(Composite parent) {
		if (_list == null) {
			_list = new List(parent, SWT.NONE | SWT.SINGLE);
			_list.setFont(parent.getFont());
			_list.addSelectionListener(_getSelectionListener());
			_list.addDisposeListener(new DisposeListener() {

				@Override
				public void widgetDisposed(DisposeEvent paramDisposeEvent) {
					_list = null;
				}
			});
		}
		else {
			checkParent(_list, parent);
		}
		return _list;
	}

	@Override
	public int getNumberOfControls() {
		return 2;
	}

	/*
	 * @see FieldEditor.setEnabled(boolean,Composite).
	 */
	@Override
	public void setEnabled(boolean enabled, Composite parent) {
		super.setEnabled(enabled, parent);
		getListControl(parent).setEnabled(enabled);
		_addButton.setEnabled(enabled);
		_removeButton.setEnabled(enabled);
		_modifyButton.setEnabled(enabled);
		_upButton.setEnabled(enabled);
		_downButton.setEnabled(enabled);
	}

	@Override
	public void setFocus() {
		if (_list != null) {
			_list.setFocus();
		}
	}

	/**
	 * Creates a list field editor.
	 *
	 * @param name
	 *        the name of the preference this field editor works on
	 * @param labelText
	 *        the label text of the field editor
	 * @param parent
	 *        the parent of the field editor's control
	 */
	protected ListEditor(String name, String labelText, Composite parent) {
		init(name, labelText);
		createControl(parent);
	}

	/**
	 * Combines the given list of items into a single string.
	 * This method is the converse of <code>parseString</code>.
	 * <p>
	 * Subclasses must implement this method.
	 * </p>
	 *
	 * @param items
	 *        the list of items
	 * @return the combined string
	 * @see #parseString
	 */
	protected abstract String createList(String[] items);

	/**
	 * Splits the given string into a list of strings.
	 * This method is the converse of <code>createList</code>.
	 * <p>
	 * Subclasses must implement this method.
	 * </p>
	 *
	 * @param stringList
	 *        the string
	 * @return an array of <code>String</code>
	 * @see #createList
	 */
	protected abstract String[] parseString(String stringList);

	@Override
	protected void adjustForNumColumns(int numColumns) {
		Control control = getLabelControl();
		((GridData)control.getLayoutData()).horizontalSpan = numColumns;
		((GridData)_list.getLayoutData()).horizontalSpan = numColumns - 1;
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		Control control = getLabelControl(parent);
		GridData gd = new GridData();
		gd.horizontalSpan = numColumns;
		control.setLayoutData(gd);

		_list = getListControl(parent);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalSpan = numColumns - 1;
		gd.grabExcessHorizontalSpace = true;
		_list.setLayoutData(gd);

		_buttonBox = getButtonBoxControl(parent);
		gd = new GridData();
		gd.verticalAlignment = GridData.BEGINNING;
		_buttonBox.setLayoutData(gd);
	}

	@Override
	protected void doLoad() {
		if (_list != null) {
			String s = getPreferenceStore().getString(getPreferenceName());
			String[] array = parseString(s);
			for (int i = 0; i < array.length; i++) {
				_list.add(array[i]);
			}
		}
	}

	@Override
	protected void doLoadDefault() {
		if (_list != null) {
			_list.removeAll();
			String s = getPreferenceStore().getDefaultString(getPreferenceName());
			String[] array = parseString(s);
			for (int i = 0; i < array.length; i++) {
				_list.add(array[i]);
			}
		}
	}

	@Override
	protected void doStore() {
		String s = createList(_list.getItems());
		if (s != null) {
			getPreferenceStore().setValue(getPreferenceName(), s);
		}
	}

	/**
	 * Return the List.
	 *
	 * @return the list
	 * @since 3.5
	 */
	protected List getList() {
		return _list;
	}

	/**
	 * Returns this field editor's shell.
	 * <p>
	 * This method is internal to the framework; subclassers should not call
	 * this method.
	 * </p>
	 *
	 * @return the shell
	 */
	protected Shell getShell() {
		if (_addButton == null) {
			return null;
		}
		return _addButton.getShell();
	}

	/**
	 * Invoked when the selection in the list has changed.
	 *
	 * <p>
	 * The default implementation of this method utilizes the selection index
	 * and the size of the list to toggle the enablement of the up, down and
	 * remove buttons.
	 * </p>
	 *
	 * <p>
	 * Sublcasses may override.
	 * </p>
	 *
	 * @since 3.5
	 */
	protected void selectionChanged() {
		int index = _list.getSelectionIndex();
		int size = _list.getItemCount();

		_removeButton.setEnabled(index >= 0);
		_modifyButton.setEnabled(index >= 0);
		_upButton.setEnabled(size > 1 && index > 0);
		_downButton.setEnabled(size > 1 && index >= 0 && index < size - 1);
	}

	/**
	 * Notifies that the Add button has been pressed.
	 *
	 * @param event
	 */
	private void _addPressed() {
		ModifierDialog modifierDialog = new ModifierDialog(getShell(), null);
		modifierDialog.open();

		String input = modifierDialog.getResult();

		if (input != null) {
			int index = _list.getSelectionIndex();
			if (index >= 0) {
				_list.add(input, index + 1);
			}
			else {
				_list.add(input, 0);
			}
			selectionChanged();
		}
	}

	/**
	 * Creates the Add, Remove, Up, and Down button in the given button box.
	 *
	 * @param box
	 *        the box for the buttons
	 */
	private void _createButtons(Composite box) {
		_addButton = _createPushButton(box, "ListEditor.add");
		_removeButton = _createPushButton(box, "ListEditor.remove");
		_modifyButton = _createPushButton(box, "Edit");
		_upButton = _createPushButton(box, "ListEditor.up");
		_downButton = _createPushButton(box, "ListEditor.down");
	}

	/**
	 * Helper method to create a push button.
	 *
	 * @param parent
	 *        the parent control
	 * @param key
	 *        the resource name used to supply the button's label text
	 * @return Button
	 */
	private Button _createPushButton(Composite parent, String key) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText(JFaceResources.getString(key));
		button.setFont(parent.getFont());
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		int widthHint = convertHorizontalDLUsToPixels(button, IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		button.setLayoutData(data);
		button.addSelectionListener(_getSelectionListener());
		return button;
	}

	/**
	 * Notifies that the Down button has been pressed.
	 */
	private void _downPressed() {
		_swap(false);
	}

	/**
	 * Returns this field editor's selection listener.
	 * The listener is created if nessessary.
	 *
	 * @return the selection listener
	 */
	private SelectionListener _getSelectionListener() {
		if (_selectionListener == null) {
			createSelectionListener();
		}
		return _selectionListener;
	}

	private void _modifyPressed() {
		setPresentsDefaultValue(false);

		String[] selection = _list.getSelection();

		if (selection == null || selection.length == 0) {
			return;
		}

		ModifierDialog modifierDialog = new ModifierDialog(getShell(), selection[0]);
		modifierDialog.open();

		String input = modifierDialog.getResult();

		if (input == null) {
			return;
		}

		int index = _list.getSelectionIndex();
		_list.remove(index);
		_list.add(input, index);

		_list.setSelection(index);
		selectionChanged();
	}

	/**
	 * Notifies that the Remove button has been pressed.
	 */
	private void _removePressed() {
		setPresentsDefaultValue(false);
		int index = _list.getSelectionIndex();
		if (index >= 0) {
			_list.remove(index);
			_list.select(index >= _list.getItemCount() ? index - 1 : index);
			selectionChanged();
		}
	}

	/**
	 * Moves the currently selected item up or down.
	 *
	 * @param up
	 *        <code>true</code> if the item should move up,
	 *        and <code>false</code> if it should move down
	 */
	private void _swap(boolean up) {
		setPresentsDefaultValue(false);
		int index = _list.getSelectionIndex();
		int target = up ? index - 1 : index + 1;

		if (index >= 0) {
			String[] selection = _list.getSelection();
			Assert.isTrue(selection.length == 1);
			_list.remove(index);
			_list.add(selection[0], target);
			_list.setSelection(target);
		}
		selectionChanged();
	}

	/**
	 * Notifies that the Up button has been pressed.
	 */
	private void _upPressed() {
		_swap(true);
	}

	/**
	 * The Add button.
	 */
	private Button _addButton;

	/**
	 * The button box containing the Add, Remove, Up, and Down buttons;
	 * <code>null</code> if none (before creation or after disposal).
	 */
	private Composite _buttonBox;

	/**
	 * The Down button.
	 */
	private Button _downButton;

	/**
	 * The list widget; <code>null</code> if none
	 * (before creation or after disposal).
	 */
	private List _list;

	/**
	 * The Modify button.
	 */
	private Button _modifyButton;

	/**
	 * The Remove button.
	 */
	private Button _removeButton;

	/**
	 * The selection listener.
	 */
	private SelectionListener _selectionListener;

	/**
	 * The Up button.
	 */
	private Button _upButton;
}