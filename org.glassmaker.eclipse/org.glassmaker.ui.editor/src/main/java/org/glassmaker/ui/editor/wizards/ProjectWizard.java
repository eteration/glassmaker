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

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;

import org.apache.maven.archetype.catalog.Archetype;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * This is a simple new wizard. Its role is to create a new project.
 */

public class ProjectWizard extends Wizard implements INewWizard {
	private WizardNewProjectCreationPage projectPage;
	private ProjectWizardParametersPage parametersPage;
	private ISelection selection;
	protected ProjectImportConfiguration importConfiguration = new ProjectImportConfiguration();

	/**
	 * Constructor for ProjectWizard.
	 */
	public ProjectWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		projectPage = new WizardNewProjectCreationPage("Glassmaker Project");
		parametersPage = new ProjectWizardParametersPage();
		addPage(projectPage);
		addPage(parametersPage);
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if(page == projectPage)
			parametersPage.setProjectName(getProjectName());
		return super.getNextPage(page);
	}
	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 */
	public boolean performFinish() {
		final String containerName = getProjectName();
		final IPath location = projectPage.useDefaults() ? null : projectPage.getLocationPath();

		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(containerName, location, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}

	public String getProjectName() {
		return projectPage.getProjectName();
	}

	/**
	 * The worker method. It will find the container, create the file if missing
	 * or just replace its contents, and open the editor on the newly created
	 * file.
	 */

	private void doFinish(String containerName, IPath location, IProgressMonitor monitor) throws CoreException {
		// create a sample file
		monitor.beginTask("Creating " + containerName, 2);

		monitor.worked(1);

		final Archetype archetype = new Archetype();
		archetype.setGroupId("org.glassmaker");
		archetype.setArtifactId("org.glassmaker.archetype.basic");
		archetype.setVersion("0.0.1");
		ProjectParameters params = parametersPage.getParams();

		final String groupId = params.getGroupId();
		final String artifactId = params.getArtifactId();
		final String version = params.getVersion();
		final String javaPackage = params.getPackageName();
		final Properties properties = params.getProperties();
		properties.setProperty("oauth2callbackurl", properties.getProperty(ProjectWizardParametersPage.O_AUTH_CALLBACK));
		properties.setProperty("clientid", properties.getProperty(ProjectWizardParametersPage.CLIENT_ID));
		properties.setProperty("clientsecret", properties.getProperty(ProjectWizardParametersPage.CLIENT_SECRET));

		List<IProject> projects = MavenPlugin.getProjectConfigurationManager().createArchetypeProjects(location, archetype, groupId, artifactId, version, javaPackage, properties, importConfiguration, monitor);

	}

	/**
	 * We will accept the selection in the workbench to see if we can initialize
	 * from it.
	 * 
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}