package com.ixenit.membersort.handlers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jface.preference.IPreferenceStore;

import com.ixenit.membersort.Activator;
import com.ixenit.membersort.preferences.PreferenceConstants;

/**
 *
 * @author Benjámin Hajnal <benjamin.hajnal@ixenit.com>
 *
 */
class MemberComparator implements Comparator<BodyDeclaration> {

	@Override
	public int compare(BodyDeclaration o1, BodyDeclaration o2) {
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

		String savedOrder = preferenceStore.getString(PreferenceConstants.P_ORDER);
		String[] order = savedOrder.split(",");

		boolean orderByName = preferenceStore.getBoolean(PreferenceConstants.P_ORDER_BY_NAME);

		Member member1 = _createMember(o1);
		Member member2 = _createMember(o2);

		int modifierOrder1 = _computeOrderByModifiers(member1.modifiers, order);
		int modifierOrder2 = _computeOrderByModifiers(member2.modifiers, order);

		// If they have the same modifiers then compare them by name
		if (modifierOrder1 == modifierOrder2) {
			return orderByName ? member1.name.compareTo(member2.name) : 0;
		}

		// Compare them by modifiers
		return (modifierOrder1 < modifierOrder2) ? -1 : 1;
	}

	private boolean _appendIfExist(StringBuilder sb, String modifier, List<String> modifiers) {
		if (!modifiers.contains(modifier)) {
			return false;
		}

		sb.append("_").append(modifier);

		return true;
	}

	private int _computeOrderByModifiers(String name, String[] order) {
		for (int i = 0; i < order.length; i++) {

			if (name.equals(order[i])) {
				return i;
			}
		}

		System.err.println("Not found:" + name);

		return -1;
	}

	private Member _createMember(BodyDeclaration declaration) {
		List<String> modifiers = new ArrayList<>();

		for (Object modifier : declaration.modifiers()) {
			modifiers.add(modifier.toString());
		}

		StringBuilder sb = new StringBuilder();

		_getVisibility(sb, modifiers);
		_getModifiers(sb, modifiers);

		if (declaration instanceof FieldDeclaration) {
			return _getFieldType((FieldDeclaration)declaration, sb);
		}

		if (declaration instanceof MethodDeclaration) {
			return _getMethodType((MethodDeclaration)declaration, sb);
		}

		if (declaration instanceof TypeDeclaration) {
			return _getClassType((TypeDeclaration)declaration, sb);
		}

		if (declaration instanceof Initializer) {
			return _getInitializerType((Initializer)declaration, sb);
		}

		if (declaration instanceof EnumDeclaration) {
			return _getEnumType((EnumDeclaration)declaration, sb);
		}

		return new Member("-nodata", "-nodata");
	}

	private Member _getClassType(TypeDeclaration declaration, StringBuilder sb) {
		String name = declaration.getName().getIdentifier();

		sb.append("_class");

		return new Member(name, sb.toString());
	}

	private Member _getEnumType(EnumDeclaration declaration, StringBuilder sb) {
		String name = declaration.getName().getIdentifier();

		sb.append("_enum");

		return new Member(name, sb.toString());
	}

	private Member _getFieldType(FieldDeclaration declaration, StringBuilder sb) {
		String name = _readFieldName(declaration);

		sb.append("_variable");

		return new Member(name, sb.toString());
	}

	private Member _getInitializerType(Initializer declaration, StringBuilder sb) {
		sb.append("_init");

		return new Member("", sb.toString().replace("package_", ""));
	}

	private Member _getMethodType(MethodDeclaration declaration, StringBuilder sb) {
		String name = declaration.getName().getIdentifier();

		if (declaration.isConstructor()) {
			sb.append("_constructor");
		}
		else {
			sb.append("_method");
		}

		return new Member(name, sb.toString());
	}

	private void _getModifiers(StringBuilder sb, List<String> modifiers) {
		for (String modifier : PreferenceConstants.MODIFIERS) {
			_appendIfExist(sb, modifier, modifiers);
		}
	}

	private void _getVisibility(StringBuilder sb, List<String> modifiers) {
		if (!_appendIfExist(sb, "public", modifiers) && !_appendIfExist(sb, "protected", modifiers)
			&& !_appendIfExist(sb, "private", modifiers)) {

			sb.append("_package");
		}

		sb.deleteCharAt(0);
	}

	private String _readFieldName(FieldDeclaration declaration) {
		String name = null;

		Object o = declaration.fragments().get(0);

		if (o instanceof VariableDeclarationFragment) {
			name = ((VariableDeclarationFragment)o).getName().toString();
		}

		return name;
	}

	private class Member {

		private Member(String name, String type) {
			this.name = name;
			this.modifiers = type;
		}

		private String modifiers;

		private String name;

	}

}
