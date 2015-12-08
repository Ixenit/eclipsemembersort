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
package com.ixenit.membersort.handlers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

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
import org.eclipse.jdt.core.dom.CompilationUnit;
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
 *
 * @author Benjámin Hajnal <benjamin.hajnal@ixenit.com>
 *
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
		// then the sorting should be applied to this selection
		if (selection != null && (selection instanceof IStructuredSelection)) {
			try {
				_sortSelection((IStructuredSelection)selection);
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

		return null;
	}

	private void _processUnit(ICompilationUnit cu)
		throws JavaModelException, MalformedTreeException, BadLocationException {

		// Parse the javacode to be able to modify it
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(cu);

		// Create a copy of the CompilationUnit to work on
		CompilationUnit copyOfUnit = (CompilationUnit)parser.createAST(null);

		MemberComparator comparator = new MemberComparator();

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

	private void _showError(IWorkbenchWindow window, Exception e) {
		StringWriter sw = new StringWriter();

		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);

		String msg = e.getMessage() + "\n" + sw.toString();

		MessageDialog.openError(window.getShell(), "SortMembers", msg);
	}

	private void _sortSelection(IStructuredSelection selection)
		throws JavaModelException, MalformedTreeException, BadLocationException {

		// Iterate through the selected elements
		for (Iterator<?> iterator = selection.iterator(); iterator.hasNext();) {
			Object fragment = iterator.next();

			// If the current element is a java package the retrieve
			// its compilation units and process them
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

}
