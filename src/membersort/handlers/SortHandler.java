package membersort.handlers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.util.CompilationUnitSorter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 *
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SortHandler extends AbstractHandler {

	/**
	 * Constructor is needed for Eclipse
	 */
	public SortHandler() {
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		// Get the current window
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		// Get the actual selection
		ISelectionService selectionService = window.getSelectionService();
		ISelection selection = selectionService.getSelection();

		// If anything is selected and the selection is not TextSelection
		if (selection != null && (selection instanceof IStructuredSelection)) {
			try {
				_sortSelectedFiles((IStructuredSelection)selection);
			}
			catch (Exception e) {
				_showError(window, e);
			}

			return null;
		}

		// If nothing is selected then get the page and the editor
		IWorkbenchPage page = window.getActivePage();
		IEditorPart editor = page.getActiveEditor();

		// If the editor is not opened
		if (editor == null) {
			return null;
		}

		IEditorInput editorInput = editor.getEditorInput();

		// Get the file open in the editor
		IFile file = Platform.getAdapterManager().getAdapter(editorInput, IFile.class);

		// Create a Java compilation unit from the file
		ICompilationUnit cu = JavaCore.createCompilationUnitFrom(file);

		try {
			_processUnit(cu);
		}
		catch (Exception e) {
			_showError(window, e);
		}

		// From eclipse: the result of the execution.
		// Reserved for future use, must be null.
		return null;
	}

	private void _processUnit(ICompilationUnit cu)
		throws JavaModelException, MalformedTreeException, BadLocationException {

		// Parse the javacode to be able to modify it
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(cu);

		// Create a copy of the CompilationUnit to work on
		CompilationUnit copyOfUnit = (CompilationUnit)parser.createAST(null);

		IxenitMemberComparator comparator = new IxenitMemberComparator();

		// This helper method will sort our java code with the given comparator
		TextEdit edits = CompilationUnitSorter.sort(copyOfUnit, comparator, 0, null, null);

		// The sort method gives us null if there weren't any changes
		if (edits != null) {
			ICompilationUnit workingCopy = cu.getWorkingCopy(new WorkingCopyOwner() {}, null);

			workingCopy.applyTextEdit(edits, null);

			// Commit changes
			workingCopy.commitWorkingCopy(true, null);
		}
	}

	private void _sortSelectedFiles(IStructuredSelection selection)
		throws JavaModelException, MalformedTreeException, BadLocationException {

		for (Iterator<?> iterator = selection.iterator(); iterator.hasNext();) {
			Object fragment = iterator.next();

			if (fragment instanceof IPackageFragment) {
				IPackageFragment pkg = (IPackageFragment)fragment;

				ICompilationUnit[] compilationUnits = pkg.getCompilationUnits();

				for (ICompilationUnit iCompilationUnit : compilationUnits) {
					_processUnit(iCompilationUnit);
				}
			}
			else if (fragment instanceof ICompilationUnit) {
				_processUnit((ICompilationUnit)fragment);
			}
		}
	}

	private void _showError(IWorkbenchWindow window, Exception e) {
		e.printStackTrace();
		MessageDialog.openInformation(window.getShell(), "SortMembers", e.getMessage());

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String msg = e.getMessage() + "\n" + sw.toString();

		MessageDialog.openInformation(window.getShell(), "SortMembers", msg);
	}

	private static String[] _ORDER = {

			// PUBLIC
			"public_static_final_variable",
			"public_static_variable",

			"public_static_method",
			"public_constructor",
			"public_method",

			// PACKAGE
			"package_static_method",
			"package_constructor",
			"package_method",

			"package_static_final_variable",
			"package_static_variable",
			"package_variable",
			"package_final_variable",

			// PROTECTED
			"protected_static_method",
			"protected_constructor",
			"protected_method",

			"protected_static_final_variable",
			"protected_static_variable",
			"protected_variable",
			"protected_final_variable",

			// PRIVATE
			"private_static_method",
			"private_constructor",
			"private_method",

			"public_variable", // kakukktojás
			"public_final_variable", // kakukktojás

			"private_static_final_log", // kiegészítés
			"private_static_final_instance", // kiegészítés

			"private_static_final_variable",
			"private_static_variable",
			"private_variable",
			"private_final_variable",

			"static_init",

			// CLASSES

			"public_static_class",
			"public_class",
			"protected_static_class",
			"protected_class",
			"private_static_class",
			"private_class",

			// ENUMS
			"public_enum",
			"package_enum",
			"protected_enum",
			"private_enum" };

	private class IxenitMemberComparator implements Comparator<BodyDeclaration> {

		@Override
		public int compare(BodyDeclaration o1, BodyDeclaration o2) {
			Score o1Type = _getMemberType(o1);
			Score o2Type = _getMemberType(o2);

			int order1 = _findOrder(o1Type.modifiers);
			int order2 = _findOrder(o2Type.modifiers);

			if (order1 == order2) {
				return o1Type.name.compareTo(o2Type.name);
			}

			return (order1 < order2) ? -1 : 1;
		}

		private boolean _appendIfExist(StringBuilder sb, String modifier, List<String> modifiers) {
			if (!modifiers.contains(modifier)) {
				return false;
			}

			sb.append("_").append(modifier);

			return true;
		}

		private StringBuilder _buildType(List<String> modifiers) {
			StringBuilder sb = new StringBuilder();

			if (!_appendIfExist(sb, "public", modifiers)
				&& !_appendIfExist(sb, "protected", modifiers)
				&& !_appendIfExist(sb, "private", modifiers)) {

				sb.append("_package");
			}

			_appendIfExist(sb, "static", modifiers);
			_appendIfExist(sb, "final", modifiers);

			sb.deleteCharAt(0);

			return sb;
		}

		private int _findOrder(String name) {
			for (int i = 0; i < _ORDER.length; i++) {

				if (name.equals(_ORDER[i])) {
					return i;
				}
			}

			return -1;
		}

		private Score _getClassType(TypeDeclaration declaration, List<String> modifiers) {
			String name = declaration.getName().getIdentifier();

			StringBuilder sb = _buildType(modifiers);

			sb.append("_class");

			return new Score(name, sb.toString());
		}

		private Score _getEnumType(EnumDeclaration declaration, List<String> modifiers) {
			String name = declaration.getName().getIdentifier();

			StringBuilder sb = _buildType(modifiers);

			sb.append("_enum");

			return new Score(name, sb.toString());
		}

		private String _getFieldName(FieldDeclaration declaration) {
			String name = null;

			Object o = declaration.fragments().get(0);

			if (o instanceof VariableDeclarationFragment) {
				name = ((VariableDeclarationFragment)o).getName().toString();
			}

			return name;
		}

		private Score _getFieldType(FieldDeclaration declaration, List<String> modifiers) {
			String name = _getFieldName(declaration);

			if ("_instance".equals(name)) {
				return new Score(name, "private_static_final_instance");
			}

			if ("_log".equals(name)) {
				return new Score(name, "private_static_final_log");
			}

			StringBuilder sb = _buildType(modifiers);

			sb.append("_variable");

			return new Score(name, sb.toString());
		}

		private Score _getInitializerType(Initializer declaration, List<String> modifiers) {
			StringBuilder sb = new StringBuilder();

			_appendIfExist(sb, "static", modifiers);

			sb.append("_init");
			sb.deleteCharAt(0);

			return new Score("", sb.toString());
		}

		/**
		 * TypeDeclaration modifiers, isInterface, name, superclass, superInterfaces, typeParameters
		 *
		 * FieldDeclaration modifiers, type, fragments (VariableDeclarationFragments with name only)
		 *
		 * MethodDeclaration modifiers, isConstructor, returnType, name, typeParameters, parameters
		 * (SingleVariableDeclarations with name, type, and modifiers only), thrownExceptions
		 *
		 * Initializer modifiers
		 *
		 * AnnotationTypeDeclaration modifiers, name
		 *
		 * AnnotationTypeMemberDeclaration modifiers, name, type, default
		 *
		 * EnumDeclaration modifiers, name, superInterfaces
		 *
		 * EnumConstantDeclaration modifiers, name, arguments
		 *
		 */
		private Score _getMemberType(BodyDeclaration declaration) {
			List<String> modifiers = _getModifiers(declaration);

			if (declaration instanceof FieldDeclaration) {
				return _getFieldType((FieldDeclaration)declaration, modifiers);
			}

			if (declaration instanceof MethodDeclaration) {
				return _getMethodType((MethodDeclaration)declaration, modifiers);
			}

			if (declaration instanceof TypeDeclaration) {
				return _getClassType((TypeDeclaration)declaration, modifiers);
			}

			if (declaration instanceof Initializer) {
				return _getInitializerType((Initializer)declaration, modifiers);
			}

			if (declaration instanceof EnumDeclaration) {
				return _getEnumType((EnumDeclaration)declaration, modifiers);
			}

			System.out.println("Unknown member: " + declaration);

			return new Score("-nodata", "-nodata");
		}

		private Score _getMethodType(MethodDeclaration declaration, List<String> modifiers) {
			String name = declaration.getName().getIdentifier();

			StringBuilder sb = _buildType(modifiers);

			sb.append("_method");

			return new Score(name, sb.toString());
		}

		private List<String> _getModifiers(BodyDeclaration declaration) {
			List<String> modifiers = new ArrayList<>();

			for (Object modifier : declaration.modifiers()) {
				modifiers.add(modifier.toString());
			}

			return modifiers;
		}

	}

	private class Score {

		private Score(String name, String type) {
			this.name = name;
			this.modifiers = type;
		}

		private String modifiers;

		private String name;

	}
}
