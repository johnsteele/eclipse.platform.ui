/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.presentations.PresentableEditorPart;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.ui.presentations.IPresentablePart;

/**
 * An EditorPane is a subclass of PartPane offering extended
 * behavior for workbench editors.
 */
public class EditorPane extends PartPane {
	private PresentableEditorPart presentableAdapter = new PresentableEditorPart(this);	
	
	private EditorWorkbook workbook;

/**
 * Constructs an editor pane for an editor part.
 */
public EditorPane(IEditorReference ref, WorkbenchPage page, EditorWorkbook workbook) {
	super(ref, page);
	this.workbook = workbook;
}
protected WorkbenchPart createErrorPart(WorkbenchPart oldPart) {
	class ErrorEditorPart extends EditorPart {
		private Text text;
		public void doSave(IProgressMonitor monitor) {}
		public void doSaveAs() {}
		public void init(IEditorSite site, IEditorInput input) {
			setSite(site);
			setInput(input);
		}
		public boolean isDirty() {return false;}
		public boolean isSaveAsAllowed() {return false;}
		public void createPartControl(Composite parent) {
			text = new Text(parent,SWT.MULTI|SWT.READ_ONLY|SWT.WRAP);
			text.setForeground(JFaceColors.getErrorText(text.getDisplay()));
			text.setBackground(text.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
			text.setText(WorkbenchMessages.getString("EditorPane.errorMessage")); //$NON-NLS-1$
		}
		public void setFocus() {
			if (text != null) text.setFocus();
		}
		protected void setTitle(String title) {
			super.setTitle(title);
		}
		protected void setTitleToolTip(String text) {
			super.setTitleToolTip(text);
		}
	}
	IEditorPart oldEditorPart = (IEditorPart)oldPart;
	EditorSite oldEditorSite = (EditorSite)oldEditorPart.getEditorSite();
	ErrorEditorPart newPart = new ErrorEditorPart();
	newPart.setTitle(oldPart.getTitle());
	newPart.setTitleToolTip(oldPart.getTitleToolTip());
	oldEditorSite.setPart(newPart);
	newPart.init(oldEditorSite, oldEditorPart.getEditorInput());
	return newPart;
}
/**
 * Editor panes do not need a title bar. The editor
 * title and close icon are part of the tab containing
 * the editor. Tools and menus are added directly into
 * the workbench toolbar and menu bar.
 */
protected void createTitleBar() {
	// do nothing
}
/**
 * @see PartPane::doHide
 */
public void doHide() {
	getPage().closeEditor(getEditorReference(), true);
}

/**
 * User has requested to close all but the current pane.
 * Take appropriate action depending on type.
 */
public void doHideOthers() {
	IEditorReference editor = getEditorReference();
	IEditorReference[] allEditors = getPage().getEditorReferences();
	IEditorReference[] otherEditors = new IEditorReference[allEditors.length - 1];
	int j = 0;
	for (int i = 0; i < allEditors.length; i++) {
		IEditorReference reference = allEditors[i];
		if (!reference.equals(editor) && j < otherEditors.length) {
			otherEditors[j++] = reference;
		}
	}
	getPage().closeEditors(otherEditors, true);
}

public void doHideAll() {
    getPage().closeEditors(getPage().getEditorReferences(), true);
}

/**
 * Answer the editor part child.
 */
public IEditorReference getEditorReference() {
	return (IEditorReference)getPartReference();
}
/**
 * Answer the SWT widget style.
 */
int getStyle() {
	return SWT.NONE;
}
/**
 * Answer the editor workbook container
 */
public EditorWorkbook getWorkbook() {
	return workbook;
}

/**
 * Notify the workbook page that the part pane has
 * been activated by the user.
 */
protected void requestActivation() {
	// By clearing the active workbook if its not the one
	// associated with the editor, we reduce draw flicker
	if (!workbook.isActiveWorkbook())
		workbook.getEditorArea().setActiveWorkbook(null, false);
		
	super.requestActivation();
}
/**
 * Set the editor workbook container
 */
public void setWorkbook(EditorWorkbook editorWorkbook) {
	workbook = editorWorkbook;
}
/* (non-Javadoc)
 * Method declared on PartPane.
 */
/* package */ void shellActivated() {
	//this.workbook.drawGradient();
}

/* (non-Javadoc)
 * Method declared on PartPane.
 */
/* package */ void shellDeactivated() {
	//this.workbook.drawGradient();
}
/**
 * Indicate focus in part.
 */
public void showFocus(boolean inFocus) {
	if (inFocus)
		this.workbook.becomeActiveWorkbook(true);
	else
		this.workbook.tabFocusHide();
}

/**
 * Adds the Close Others menu item.
 */
protected void addCloseOthersItem (Menu menu) {
	MenuItem item = new MenuItem(menu, SWT.NONE);
	item.setText(WorkbenchMessages.getString("PartPane.closeOthers")); //$NON-NLS-1$
	item.addSelectionListener(new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			doHideOthers();
		}
	});	
	item.setEnabled(getPage().getEditorReferences().length > 1);
}

/**
 * Adds the Close All menu item.
 */
protected void addCloseAllItem (Menu menu) {
	MenuItem item = new MenuItem(menu, SWT.NONE);
	item.setText(WorkbenchMessages.getString("PartPane.closeAll")); //$NON-NLS-1$
	item.addSelectionListener(new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			doHideAll();
		}
	});	
	item.setEnabled(getPage().getEditorReferences().length >= 1);
}

/**
 * Add the pin menu item on the editor system menu
 */
protected void addPinEditorItem(Menu parent) {
	boolean reuseEditor = WorkbenchPlugin.getDefault().getPreferenceStore().getBoolean(IPreferenceConstants.REUSE_EDITORS_BOOLEAN);
	if (!reuseEditor) {
		return;
	}

	IWorkbenchPart part = getPartReference().getPart(false);
	if (part == null) {
		return;
	}
	
	final MenuItem item = new MenuItem(parent, SWT.CHECK);
	item.setText(WorkbenchMessages.getString("EditorPane.pinEditor")); //$NON-NLS-1$
	item.addSelectionListener(new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			IWorkbenchPart part = getPartReference().getPart(true);
			if (part == null) {
				// this should never happen
				item.setSelection(false);
				item.setEnabled(false);
			} else {
				((EditorSite) part.getSite()).setReuseEditor(!item.getSelection());
			}
		}
	});
	item.setEnabled(true);
	item.setSelection(!((EditorSite) part.getSite()).getReuseEditor());
}

/**
 * Return the sashes around this part.
 */
protected Sashes findSashes() {
	Sashes result = new Sashes();
	workbook.getEditorArea().findSashes(workbook,result);
	return result;
}
/**
 * Update the title attributes for the pane.
 */
public void updateTitles() {
//	  TODO commented during presentation refactor 	workbook.updateEditorTab(getEditorReference());
}
/**
 * Show a title label menu for this pane.
 */
public void showPaneMenu() {
	ILayoutContainer container = getContainer();
	
	if (container instanceof EditorWorkbook) {
		EditorWorkbook folder = (EditorWorkbook) container;
		
		folder.showSystemMenu();
	}
}
/**
 * Show the context menu for this part.
 */
public void showViewMenu(){
	//Do nothing. Editors do not have menus
}

/* (non-Javadoc)
 * @see org.eclipse.ui.internal.LayoutPart#getPresentablePart()
 */
public IPresentablePart getPresentablePart() {
	return presentableAdapter;
}
}
