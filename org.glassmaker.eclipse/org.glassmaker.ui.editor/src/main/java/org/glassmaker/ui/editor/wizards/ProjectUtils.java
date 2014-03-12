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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.ModuleCoreNature;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;

public class ProjectUtils {
	public IContentType fContentType;
	public List fValidExtensions;

	public ProjectUtils(List fValidExtensions) {
		this.fValidExtensions = fValidExtensions;
	}
	

	static IPath getRootContainerForPath(IProject project, IPath path) {
		if (ModuleCoreNature.isFlexibleProject(project)) {
			IVirtualFolder componentFolder = ComponentCore.createFolder(project, Path.ROOT);
			if (componentFolder != null && componentFolder.exists()) {
				IContainer[] workspaceFolders = componentFolder.getUnderlyingFolders();
				for (int i = 0; i < workspaceFolders.length; i++) {
					if (workspaceFolders[i].getFullPath().isPrefixOf(path)) {
						return workspaceFolders[i].getFullPath();
					}
				}
			}
		}
		return null;
	}

	public static IPath getDefaultRootContainer(IProject project) {
		if (project == null)
			return null;
		IPath root = null;
		try {
			root = getDefaultRoot(project);
		} catch (NoClassDefFoundError e) {
			return null;
		}
		return root;
	}

	static IPath getDefaultRoot(IProject project) {
		if (ModuleCoreNature.isFlexibleProject(project)) {
			IVirtualFolder componentFolder = ComponentCore.createFolder(project, Path.ROOT);
			if (componentFolder != null && componentFolder.exists()) {
				return componentFolder.getWorkspaceRelativePath();
			}
		}
		return null;
	}
	static final String META_INF_RESOURCES = "META-INF/resources/"; //$NON-NLS-1$
	static final IPath META_INF_RESOURCES_PATH = new Path(META_INF_RESOURCES);

	public static IPath[] getAcceptableRootPaths(IProject project) {
		if (project == null)
			return null;
		IPath[] paths = null;
		try {
			paths = getAcceptableRootPathsP(project);
		}
		catch (NoClassDefFoundError e) {
			return new IPath[]{project.getFullPath()};
		}
		return paths;
	}

	static IPath[] getAcceptableRootPathsP(IProject project) {
		if (!ModuleCoreNature.isFlexibleProject(project)) {
			return new IPath[] { project.getFullPath() };
		}

		List paths = new ArrayList();
		IVirtualFolder componentFolder = ComponentCore.createFolder(project, Path.ROOT);
		if (componentFolder != null && componentFolder.exists()) {
			IContainer[] workspaceFolders = componentFolder.getUnderlyingFolders();
			for (int i = 0; i < workspaceFolders.length; i++) {
				if (workspaceFolders[i].getFolder(META_INF_RESOURCES_PATH).isAccessible())
					paths.add(workspaceFolders[i].getFullPath().append(META_INF_RESOURCES_PATH));
				else
					paths.add(workspaceFolders[i].getFullPath());
			}

			IVirtualReference[] references = ComponentCore.createComponent(project).getReferences();
			if (references != null) {
				for (int i = 0; i < references.length; i++) {
					IVirtualComponent referencedComponent = references[i].getReferencedComponent();
					if (referencedComponent == null)
						continue;
					IVirtualComponent component = referencedComponent.getComponent();
					if (component == null)
						continue;
					IVirtualFolder rootFolder = component.getRootFolder();
					if (rootFolder == null)
						continue;
					IPath referencedPathRoot = rootFolder.getWorkspaceRelativePath();
					/* http://bugs.eclipse.org/410161 */
					if (referencedPathRoot != null) {
						/*
						 * See Servlet 3.0, section 4.6 ; this is the only
						 * referenced module/component type we support
						 */
						IPath resources = referencedPathRoot.append(META_INF_RESOURCES);
						if (resources != null && component.getProject().findMember(resources.removeFirstSegments(1)) != null) {
							paths.add(resources);
						}
					}
				}
			}

		} else {
			paths.add(new IPath[] { project.getFullPath() });
		}
		return (IPath[]) paths.toArray(new IPath[paths.size()]);
	}
	
	public static boolean isWebProject(IProject project) {
		if (project == null)
			return false;
		try {
			return isWebProjectP(project);
		}
		catch (NoClassDefFoundError e) {
			e.printStackTrace();
		}
		return true;
	}
	
	static boolean isWebProjectP(IProject project) {
		return true;
	}
}