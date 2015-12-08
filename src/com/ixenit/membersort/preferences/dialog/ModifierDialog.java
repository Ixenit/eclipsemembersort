package com.ixenit.membersort.preferences.dialog;

import static com.ixenit.membersort.preferences.PreferenceConstants.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.ixenit.membersort.preferences.PreferenceConstants;

/**
 *
 * @author Benjámin Hajnal <benjamin.hajnal@ixenit.com>
 *
 */
public class ModifierDialog extends TitleAreaDialog {

	private abstract class CustomSelectionListener implements SelectionListener {

		public abstract boolean isSelected(String value);

		@Override
		public void widgetDefaultSelected(SelectionEvent arg0) {
			// Not used
		}

	}

	public ModifierDialog(Shell parentShell, String lastState) {
		super(parentShell);

		_initOptions(lastState);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Edit your member's properties");
	}

	public String getResult() {
		return _result;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite)super.createDialogArea(parent);

		Composite container = new Composite(area, SWT.NONE);

		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// (1)Label: (2)values
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		_createVisibiltyChooser(container);
		_createModifierChooser(container);
		_createTypeChooser(container);

		return area;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {
		_convertSelectedOptions();

		super.okPressed();
	}

	private void _convertSelectedOptions() {
		StringBuilder sb = new StringBuilder();

		sb.append(_visibility);

		for (String m : _modifiers) {
			sb.append(PreferenceConstants.VISIBLE_SEPARATOR).append(m);
		}

		sb.append(PreferenceConstants.VISIBLE_SEPARATOR).append(_type);

		_result = sb.toString();
	}

	private void _createModifierChooser(Composite container) {
		CustomSelectionListener modifierSelectionListener = new CustomSelectionListener() {

			@Override
			public boolean isSelected(String value) {
				return _modifiers.contains(value);
			}

			@Override
			public void widgetSelected(SelectionEvent event) {
				Button button = (Button)event.widget;

				String text = button.getText();

				if (button.getSelection()) {
					_modifiers.add(text);

					return;
				}

				_modifiers.remove(text);
			}

		};

		_createOptions(container, modifierSelectionListener, "Modifiers:", MODIFIERS, SWT.CHECK);
	}

	private void _createOptions(
		Composite container, CustomSelectionListener sListener, String labelText, String[] array,
		int type) {

		Label label = new Label(container, SWT.NONE);
		label.setText(labelText);

		Composite modifiersGroup = new Composite(container, SWT.NONE);
		GridLayout layout = new GridLayout(array.length, false);
		modifiersGroup.setLayout(layout);

		for (String v : array) {

			Button mButton = new Button(modifiersGroup, type);

			mButton.setText(v);
			mButton.addSelectionListener(sListener);

			if (sListener.isSelected(v)) {
				mButton.setSelection(true);
			}
		}

	}

	private void _createTypeChooser(Composite container) {
		CustomSelectionListener typeSelectionListener = new CustomSelectionListener() {

			@Override
			public boolean isSelected(String value) {
				return _type.equals(value);
			}

			@Override
			public void widgetSelected(SelectionEvent event) {
				Button button = (Button)event.widget;

				_type = button.getText();
			}

		};

		_createOptions(container, typeSelectionListener, "Type:", TYPES, SWT.RADIO);
	}

	private void _createVisibiltyChooser(Composite container) {
		CustomSelectionListener visibilitySelectionListener = new CustomSelectionListener() {

			@Override
			public boolean isSelected(String value) {
				return _visibility.equals(value);
			}

			@Override
			public void widgetSelected(SelectionEvent event) {
				Button button = (Button)event.widget;

				_visibility = button.getText();
			}

		};

		_createOptions(container, visibilitySelectionListener, "Visibility:", VISIBILITIES,
			SWT.RADIO);
	}

	private void _initOptions(String lastState) {
		_modifiers = new ArrayList<>();

		// The new button was pressed before
		if (lastState == null) {
			_visibility = VISIBILITIES[0];
			_type = TYPES[0];

			return;
		}

		String[] state = lastState.split(PreferenceConstants.VISIBLE_SEPARATOR);

		_visibility = state[0];

		for (int i = 1; i < state.length - 1; i++) {
			_modifiers.add(state[i]);
		}

		_type = state[state.length - 1];
	}

	// checkbox: static, final,
	private List<String> _modifiers;

	private String _result;

	// radio: class, method, variable, enum, init
	private String _type;

	// radio: public, package, protected, private
	private String _visibility;

}