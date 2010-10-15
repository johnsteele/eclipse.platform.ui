/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.internal.quickaccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.eclipse.core.runtime.Assert;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.internal.IWorkbenchGraphicConstants;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.internal.WorkbenchPlugin;

public class SearchField {

	private static final String TEXT_ARRAY = "textArray"; //$NON-NLS-1$
	private static final String TEXT_ENTRIES = "textEntries"; //$NON-NLS-1$
	private static final String ORDERED_PROVIDERS = "orderedProviders"; //$NON-NLS-1$
	private static final String ORDERED_ELEMENTS = "orderedElements"; //$NON-NLS-1$
	private static final int MAXIMUM_NUMBER_OF_ELEMENTS = 60;
	private static final int MAXIMUM_NUMBER_OF_TEXT_ENTRIES_PER_ELEMENT = 3;

	Shell shell;
	private Text text;

	private QuickAccessContents quickAccessContents;
	private MWindow window;

	private Map<String, QuickAccessProvider> providerMap = new HashMap<String, QuickAccessProvider>();

	private Map<String, QuickAccessElement> elementMap = new HashMap<String, QuickAccessElement>();

	private Map<QuickAccessElement, ArrayList<String>> textMap = new HashMap<QuickAccessElement, ArrayList<String>>();

	private LinkedList<QuickAccessElement> previousPicksList = new LinkedList<QuickAccessElement>();

	@PostConstruct
	void createWidget(final Composite parent, MWindow window) {
		this.window = window;
		// borderColor = new Color(parent.getDisplay(), 170, 176, 191);
		final Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout());
		text = new Text(comp, SWT.SEARCH | SWT.ICON_SEARCH);
		GridDataFactory.fillDefaults().hint(130, SWT.DEFAULT).applyTo(text);
		text.setMessage(QuickAccessMessages.QuickAccess_EnterSearch);

		final CommandProvider commandProvider = new CommandProvider();
		QuickAccessProvider[] providers = new QuickAccessProvider[] { new PreviousPicksProvider(),
				new EditorProvider(), new ViewProvider(), new PerspectiveProvider(),
				commandProvider, new ActionProvider(), new WizardProvider(),
				new PreferenceProvider(), new PropertiesProvider() };
		for (int i = 0; i < providers.length; i++) {
			providerMap.put(providers[i].getId(), providers[i]);
		}
		restoreDialog();

		quickAccessContents = new QuickAccessContents(providers) {
			void updateFeedback(boolean filterTextEmpty, boolean showAllMatches) {
			}

			void doClose() {
			}

			QuickAccessElement getPerfectMatch(String filter) {
				return elementMap.get(filter);
			}

			void handleElementSelected(String string, Object selectedElement) {
				if (selectedElement instanceof QuickAccessElement) {
					QuickAccessElement element = (QuickAccessElement) selectedElement;
					addPreviousPick(string, element);
					text.setText(""); //$NON-NLS-1$
					element.execute();
				}
			}
		};
		quickAccessContents.hookFilterText(text);
		shell = new Shell(parent.getShell(), SWT.RESIZE | SWT.ON_TOP);
		shell.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				text.setText(""); //$NON-NLS-1$
				e.doit = false;
			}
		});
		GridLayoutFactory.fillDefaults().applyTo(shell);
		final Table table = quickAccessContents.createTable(shell, Window.getDefaultOrientation());
		text.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				checkFocusLost(table, text);
			}

			public void focusGained(FocusEvent e) {
				IHandlerService hs = SearchField.this.window.getContext()
						.get(IHandlerService.class);
				if (commandProvider.getContextSnapshot() == null) {
					commandProvider.setSnapshot(hs.createContextSnapshot(true));
				}
			}
		});
		shell.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				checkFocusLost(table, text);
			}

			public void focusGained(FocusEvent e) {
			}
		});
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				boolean wasVisible = shell.getVisible();
				boolean nowVisible = text.getText().length() > 0;
				if (!wasVisible && nowVisible) {
					layoutShell();
				}
				shell.setVisible(nowVisible);
			}
		});
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.ESC) {
					text.setText(""); //$NON-NLS-1$
				}
			}
		});
	}

	private void layoutShell() {
		Composite parent = text.getParent();
		Rectangle tempBounds = parent.getBounds();
		Rectangle compBounds = text.getDisplay().map(parent, null, tempBounds);
		Rectangle monitorBounds = text.getMonitor().getBounds();
		int width = Math.max(350, compBounds.width);
		int height = 250;

		if (compBounds.x + width > monitorBounds.width) {
			compBounds.x = monitorBounds.width - width;
		}

		if (compBounds.y + height > monitorBounds.height) {
			compBounds.y = compBounds.y - tempBounds.height - height;
		}

		shell.setBounds(compBounds.x, compBounds.y + compBounds.height, width, height);
		shell.layout();
	}

	public void activate() {
		if (!shell.isVisible()) {
			layoutShell();
			shell.setVisible(true);
			quickAccessContents.refresh(text.getText().toLowerCase());
		}
	}

	/**
	 * @param table
	 * @param text
	 */
	protected void checkFocusLost(final Table table, final Text text) {
		table.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (!table.isDisposed() && !text.isDisposed()) {
					if (!table.isFocusControl() && !text.isFocusControl()) {
						text.setText(""); //$NON-NLS-1$
						quickAccessContents.resetProviders();
					}
				}
			}
		});
	}

	private void restoreDialog() {
		IDialogSettings dialogSettings = getDialogSettings();
		if (dialogSettings != null) {
			String[] orderedElements = dialogSettings.getArray(ORDERED_ELEMENTS);
			String[] orderedProviders = dialogSettings.getArray(ORDERED_PROVIDERS);
			String[] textEntries = dialogSettings.getArray(TEXT_ENTRIES);
			String[] textArray = dialogSettings.getArray(TEXT_ARRAY);
			if (orderedElements != null && orderedProviders != null && textEntries != null
					&& textArray != null) {
				int arrayIndex = 0;
				for (int i = 0; i < orderedElements.length; i++) {
					QuickAccessProvider quickAccessProvider = providerMap.get(orderedProviders[i]);
					int numTexts = Integer.parseInt(textEntries[i]);
					if (quickAccessProvider != null) {
						QuickAccessElement quickAccessElement = quickAccessProvider
								.getElementForId(orderedElements[i]);
						if (quickAccessElement != null) {
							ArrayList<String> arrayList = new ArrayList<String>();
							for (int j = arrayIndex; j < arrayIndex + numTexts; j++) {
								String text = textArray[j];
								// text length can be zero for old workspaces,
								// see bug 190006
								if (text.length() > 0) {
									arrayList.add(text);
									elementMap.put(text, quickAccessElement);
								}
							}
							textMap.put(quickAccessElement, arrayList);
							previousPicksList.add(quickAccessElement);
						}
					}
					arrayIndex += numTexts;
				}
			}
		}
	}

	@PreDestroy
	void dispose() {
		storeDialog();
	}

	private void storeDialog() {
		String[] orderedElements = new String[previousPicksList.size()];
		String[] orderedProviders = new String[previousPicksList.size()];
		String[] textEntries = new String[previousPicksList.size()];
		ArrayList<String> arrayList = new ArrayList<String>();
		for (int i = 0; i < orderedElements.length; i++) {
			QuickAccessElement quickAccessElement = previousPicksList.get(i);
			ArrayList<String> elementText = textMap.get(quickAccessElement);
			Assert.isNotNull(elementText);
			orderedElements[i] = quickAccessElement.getId();
			orderedProviders[i] = quickAccessElement.getProvider().getId();
			arrayList.addAll(elementText);
			textEntries[i] = elementText.size() + ""; //$NON-NLS-1$
		}
		String[] textArray = arrayList.toArray(new String[arrayList.size()]);
		IDialogSettings dialogSettings = getDialogSettings();
		dialogSettings.put(ORDERED_ELEMENTS, orderedElements);
		dialogSettings.put(ORDERED_PROVIDERS, orderedProviders);
		dialogSettings.put(TEXT_ENTRIES, textEntries);
		dialogSettings.put(TEXT_ARRAY, textArray);
	}

	private IDialogSettings getDialogSettings() {
		final IDialogSettings workbenchDialogSettings = WorkbenchPlugin.getDefault()
				.getDialogSettings();
		IDialogSettings result = workbenchDialogSettings.getSection(getId());
		if (result == null) {
			result = workbenchDialogSettings.addNewSection(getId());
		}
		return result;
	}

	private String getId() {
		return "org.eclipse.ui.internal.QuickAccess"; //$NON-NLS-1$
	}

	/**
	 * @param element
	 */
	private void addPreviousPick(String text, QuickAccessElement element) {
		// previousPicksList:
		// Remove element from previousPicksList so there are no duplicates
		// If list is max size, remove last(oldest) element
		// Remove entries for removed element from elementMap and textMap
		// Add element to front of previousPicksList
		previousPicksList.remove(element);
		if (previousPicksList.size() == MAXIMUM_NUMBER_OF_ELEMENTS) {
			Object removedElement = previousPicksList.removeLast();
			ArrayList<String> removedList = textMap.remove(removedElement);
			for (int i = 0; i < removedList.size(); i++) {
				elementMap.remove(removedList.get(i));
			}
		}
		previousPicksList.addFirst(element);

		// textMap:
		// Get list of strings for element from textMap
		// Create new list for element if there isn't one and put
		// element->textList in textMap
		// Remove rememberedText from list
		// If list is max size, remove first(oldest) string
		// Remove text from elementMap
		// Add rememberedText to list of strings for element in textMap
		ArrayList<String> textList = textMap.get(element);
		if (textList == null) {
			textList = new ArrayList<String>();
			textMap.put(element, textList);
		}

		textList.remove(text);
		if (textList.size() == MAXIMUM_NUMBER_OF_TEXT_ENTRIES_PER_ELEMENT) {
			Object removedText = textList.remove(0);
			elementMap.remove(removedText);
		}

		if (text.length() > 0) {
			textList.add(text);

			// elementMap:
			// Put rememberedText->element in elementMap
			// If it replaced a different element update textMap and
			// PreviousPicksList
			QuickAccessElement replacedElement = elementMap.put(text, element);
			if (replacedElement != null && !replacedElement.equals(element)) {
				textList = textMap.get(replacedElement);
				if (textList != null) {
					textList.remove(text);
					if (textList.isEmpty()) {
						textMap.remove(replacedElement);
						previousPicksList.remove(replacedElement);
					}
				}
			}
		}
	}

	private class PreviousPicksProvider extends QuickAccessProvider {

		public QuickAccessElement getElementForId(String id) {
			return null;
		}

		public QuickAccessElement[] getElements() {
			return previousPicksList.toArray(new QuickAccessElement[previousPicksList.size()]);
		}

		public QuickAccessElement[] getElementsSorted() {
			return getElements();
		}

		public String getId() {
			return "org.eclipse.ui.previousPicks"; //$NON-NLS-1$
		}

		public ImageDescriptor getImageDescriptor() {
			return WorkbenchImages.getImageDescriptor(IWorkbenchGraphicConstants.IMG_OBJ_NODE);
		}

		public String getName() {
			return QuickAccessMessages.QuickAccess_Previous;
		}

		protected void doReset() {
			// operation not applicable for this provider
		}

		public boolean isAlwaysPresent() {
			return true;
		}
	}

}