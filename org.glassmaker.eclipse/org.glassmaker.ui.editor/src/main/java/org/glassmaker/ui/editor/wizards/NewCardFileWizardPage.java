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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public class NewCardFileWizardPage extends WizardNewFileCreationPage {

	private static final String defaultName = "NewFile"; //$NON-NLS-1$
	private IContentType fContentType;

	public NewCardFileWizardPage(String pageName, IStructuredSelection selection) {
		super(pageName, selection);
	}

	public void createControl(Composite parent) {
		// inherit default container and name specification widgets
		super.createControl(parent);
		setFileName(computeDefaultFileName());
		setPageComplete(validatePage());
	}

	protected String computeDefaultFileName() {
		int count = 0;
		String fileName = addDefaultExtension(defaultName);
		IPath containerFullPath = getContainerFullPath();
		if (containerFullPath != null) {
			while (true) {
				IPath path = containerFullPath.append(fileName);
				if (ResourcesPlugin.getWorkspace().getRoot().exists(path)) {
					count++;
					fileName = addDefaultExtension(defaultName + count);
				} else {
					break;
				}
			}
		}
		return fileName;
	}

	/**
	 * This method is overridden to set the selected folder to web contents
	 * folder if the current selection is outside the web contents folder.
	 */
	protected void initialPopulateContainerNameField() {
		super.initialPopulateContainerNameField();

		IPath fullPath = getContainerFullPath();
		IProject project = getProjectFromPath(fullPath);
		IPath root = ProjectUtils.getRootContainerForPath(project, fullPath);
		if (root != null) {
			return;
		}
		root = ProjectUtils.getDefaultRootContainer(project);
		if (root != null) {
			setContainerFullPath(root);
			return;
		}
	}

	/**
	 * This method is overridden to set additional validation specific to html
	 * files.
	 */
	protected boolean validatePage() {
		setMessage(null);
		setErrorMessage(null);

		if (!super.validatePage()) {
			return false;
		}

		String fileName = getFileName();
		IPath fullPath = getContainerFullPath();
		if ((fullPath != null) && (fullPath.isEmpty() == false) && (fileName != null)) {
			// check that filename does not contain invalid extension
			if (!extensionValidForContentType(fileName)) {
				setErrorMessage("Filename must end with .card");
				return false;
			}
			// no file extension specified so check adding default
			// extension doesn't equal a file that already exists
			if (fileName.lastIndexOf('.') == -1) {
				String newFileName = addDefaultExtension(fileName);
				IPath resourcePath = fullPath.append(newFileName);

				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IStatus result = workspace.validatePath(resourcePath.toString(), IResource.FOLDER);
				if (!result.isOK()) {
					// path invalid
					setErrorMessage(result.getMessage());
					return false;
				}

				if ((workspace.getRoot().getFolder(resourcePath).exists() || workspace.getRoot().getFile(resourcePath).exists())) {
					setErrorMessage("There is a card with that name");
					return false;
				}
			}

			// get the IProject for the selection path
			IProject project = getProjectFromPath(fullPath);
			// if inside web project, check if inside webContent folder
			if (project != null && isWebProject(project)) {
				// check that the path is inside the webContent folder
				IPath[] webContentPaths = ProjectUtils.getAcceptableRootPaths(project);
				boolean isPrefix = false;
				for (int i = 0; !isPrefix && i < webContentPaths.length; i++) {
					isPrefix |= webContentPaths[i].isPrefixOf(fullPath);
				}
				if (!isPrefix) {
					setMessage("Cards must be inside the web contents", WARNING);
				}
			}
		}

		return true;
	}

	/**
	 * Get content type associated with this new file wizard
	 * 
	 * @return IContentType
	 */
	private IContentType getContentType() {
		if (fContentType == null)
			fContentType = Platform.getContentTypeManager().getContentType("org.glassmaker.ui.editor.CardContent");
		return fContentType;
	}

	/**
	 * Verifies if fileName is valid name for content type. Takes base content
	 * type into consideration.
	 * 
	 * @param fileName
	 * @return true if extension is valid for this content type
	 */
	private boolean extensionValidForContentType(String fileName) {
		boolean valid = false;

		IContentType type = getContentType();
		// there is currently an extension
		if (fileName.lastIndexOf('.') != -1) {
			// check what content types are associated with current extension
			IContentType[] types = Platform.getContentTypeManager().findContentTypesFor(fileName);
			int i = 0;
			while (i < types.length && !valid) {
				valid = types[i].isKindOf(type);
				++i;
			}
		} else
			valid = true; // no extension so valid
		return valid;
	}

	/**
	 * Adds default extension to the filename
	 * 
	 * @param filename
	 * @return
	 */
	String addDefaultExtension(String filename) {
		StringBuffer newFileName = new StringBuffer(filename);

		String ext = "card";

		newFileName.append("."); //$NON-NLS-1$
		newFileName.append(ext);

		return newFileName.toString();
	}

	/**
	 * Returns the project that contains the specified path
	 * 
	 * @param path
	 *            the path which project is needed
	 * @return IProject object. If path is <code>null</code> the return value is
	 *         also <code>null</code>.
	 */
	private IProject getProjectFromPath(IPath path) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject project = null;

		if (path != null) {
			if (workspace.validatePath(path.toString(), IResource.PROJECT).isOK()) {
				project = workspace.getRoot().getProject(path.toString());
			} else {
				project = workspace.getRoot().getFile(path).getProject();
			}
		}

		return project;
	}

	/**
	 * Checks if the specified project is a web project.
	 * 
	 * @param project
	 *            project to be checked
	 * @return true if the project is web project, otherwise false
	 */
	private boolean isWebProject(IProject project) {
		return ProjectUtils.isWebProject(project);
	}

}
