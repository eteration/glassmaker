/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2014 Eteration A.S.
 * All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *     
 *     
 *     Derivative Works 
 *     Parts of this program are derived from content from Eclipse Foundation
 *     that are made available under the terms of the Eclipse Public License v1.0.
 *      http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Naci Dai, Eteration A.S. - initial API, implementation and documentation
 *
 * </copyright>
 *
 *******************************************************************************/
package org.glassmaker.ui.editor.wizards;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.glassmaker.ui.GlassmakerUIPlugin;

public class NewCardWizard extends Wizard implements INewWizard {
	
	private NewCardFileWizardPage fNewFilePage;
	private NewCardTemplatesWizardPage fNewFileTemplatesPage;
	private IStructuredSelection fSelection;

	public void addPages() {
		fNewFilePage = new NewCardFileWizardPage("HTMLWizardNewFileCreationPage", new StructuredSelection(IDE.computeSelectedResources(fSelection))); //$NON-NLS-1$
		fNewFilePage.setTitle("Card File");
		fNewFilePage.setDescription("New Glassware Timeline Item Template");
		addPage(fNewFilePage);

		fNewFileTemplatesPage = new NewCardTemplatesWizardPage();
		addPage(fNewFileTemplatesPage);
	}
	
	private String applyLineDelimiter(IFile file, String text) {
		String lineDelimiter = Platform.getPreferencesService().getString(Platform.PI_RUNTIME, Platform.PREF_LINE_SEPARATOR, System.getProperty("line.separator"), new IScopeContext[] {new ProjectScope(file.getProject()), new InstanceScope() });//$NON-NLS-1$
		String convertedText = StringUtils.replace(text, "\r\n", "\n");
		convertedText = StringUtils.replace(convertedText, "\r", "\n");
		convertedText = StringUtils.replace(convertedText, "\n", lineDelimiter);
		return convertedText;
	}

	public void init(IWorkbench aWorkbench, IStructuredSelection aSelection) {
		fSelection = aSelection;
		setWindowTitle("New Card");
	}

	private void openEditor(final IFile file) {
		if (file != null) {
			getShell().getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
						IDE.openEditor(page, file, true);
					}
					catch (PartInitException e) {
						GlassmakerUIPlugin.logError(e.getMessage(), e);
					}
				}
			});
		}
	}

	public boolean performFinish() {
		boolean performedOK = false;
		// save user options for next use
		fNewFileTemplatesPage.saveLastSavedPreferences();

		// no file extension specified so add default extension
		String fileName = fNewFilePage.getFileName();
		if (fileName.lastIndexOf('.') == -1) {
			String newFileName = fNewFilePage.addDefaultExtension(fileName);
			fNewFilePage.setFileName(newFileName);
		}

		// create a new empty file
		IFile file = fNewFilePage.createNewFile();

		// if there was problem with creating file, it will be null, so make
		// sure to check
		if (file != null) {
			if (!file.isLinked()) {
				// put template contents into file
				String templateString = fNewFileTemplatesPage.getTemplateString();
				if (templateString != null) {
					templateString = applyLineDelimiter(file, templateString);
					// determine the encoding for the new file
					String charSet = "UTF-8";
	
					try {
						ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
						OutputStreamWriter outputStreamWriter = null;
						if (charSet == null || charSet.trim().equals("")) { //$NON-NLS-1$
							// just use default encoding
							outputStreamWriter = new OutputStreamWriter(outputStream);
						}
						else {
							outputStreamWriter = new OutputStreamWriter(outputStream, charSet);
						}
						outputStreamWriter.write(templateString);
						outputStreamWriter.flush();
						outputStreamWriter.close();
						ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
						file.setContents(inputStream, true, false, null);
						inputStream.close();
					}
					catch (Exception e) {
						GlassmakerUIPlugin.logError(e.getMessage(), e);
					}
				}
			}
			// open the file in editor
			openEditor(file);

			// everything's fine
			performedOK = true;
		}
		return performedOK;
	}
}