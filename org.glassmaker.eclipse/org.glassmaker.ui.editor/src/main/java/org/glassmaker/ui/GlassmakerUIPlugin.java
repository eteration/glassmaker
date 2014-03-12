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


package org.glassmaker.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.glassmaker.ui.editor.CardContextType;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class GlassmakerUIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.glassmaker.ui.editor"; //$NON-NLS-1$


	// The shared instance
	private static GlassmakerUIPlugin plugin;

	/**
	 * The template store for the card editor.
	 */
	private TemplateStore fTemplateStore;

	/**
	 * The template context type registry for the card editor.
	 */
	private ContextTypeRegistry fContextTypeRegistry;

	
	/**
	 * The constructor
	 */
	public GlassmakerUIPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static GlassmakerUIPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		reg.put("org.glassmaker.ui.templates.text", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/card_text.gif")));
		reg.put("org.glassmaker.ui.templates.autoresize", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/card_autosize.gif")));
		reg.put("org.glassmaker.ui.templates.hybrid", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/card_hybrid.gif")));
		reg.put("org.glassmaker.ui.templates.hybridmosaic", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/card_hybridmosaic.gif")));
		reg.put("org.glassmaker.ui.templates.multipage", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/card_multipage.gif")));
		reg.put("org.glassmaker.ui.templates.simpleevent", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/card_simpleevent.gif")));
		reg.put("org.glassmaker.ui.templates.list", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/card_list.gif")));
		reg.put("org.glassmaker.ui.templates.knowledge", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/card_knowledge.gif")));
		reg.put("org.glassmaker.ui.templates.knowledgemosaic", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/card_knowledgemosaic.gif")));
		reg.put("org.glassmaker.ui.templates.stock", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/card_stock.gif")));
		reg.put("org.glassmaker.ui.templates.sports", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/card_sports.gif")));
		reg.put("org.glassmaker.ui.templates.flight", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/card_flight.gif")));
		reg.put("org.glassmaker.ui.templates.movie", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/card_movie.gif")));
		reg.put("org.glassmaker.ui.templates.transit", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/card_transit.gif")));
		reg.put("org.glassmaker.ui.templates.author", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/card_author.gif")));
		reg.put("org.glassmaker.ui.templates.simplemessage", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/card_simplemessage.gif")));
	}

	public static void log(int severity, String msg, Throwable t) {
		plugin.getLog().log(new Status(severity, PLUGIN_ID, msg, t));
	}

	public static void logError(String msg, Exception t) {
		log(Status.ERROR, msg, t);
	}

	public static void logWarning(String msg) {
		log(Status.WARNING, msg, null);
	}

	public static void logInfo(String msg) {
		log(Status.INFO, msg, null);
	}

	public static void logWarning(String msg, Throwable t) {
		log(Status.WARNING, msg, t);
	}

	public static File getPreviewFile(String name) {

		File previewFile = null;
		URL previewPath = FileLocator.find(plugin.getBundle(), new Path("/preview"), null);
		try {
			previewFile = new File(FileLocator.resolve(previewPath).getPath()+"/"+name);
		} catch (Exception e) {
		    logError(e.getMessage(), e);
		} 
		
		
		return previewFile;

	}
	
	
	/**
	 * Returns the template store for the html editor templates.
	 * 
	 * @return the template store for the html editor templates
	 */
	public TemplateStore getTemplateStore() {
		if (fTemplateStore == null) {
			fTemplateStore = new ContributionTemplateStore(getTemplateContextRegistry(), getPreferenceStore(), "org.eclipse.wst.sse.ui.custom_templates");

			try {
				fTemplateStore.load();
			} catch (IOException e) {
				logError("",e);
			}
		}
		return fTemplateStore;
	}

	/**
	 * Returns the template context type registry for the html plugin.
	 * 
	 * @return the template context type registry for the html plugin
	 */
	public ContextTypeRegistry getTemplateContextRegistry() {
		if (fContextTypeRegistry == null) {
			ContributionContextTypeRegistry registry = new ContributionContextTypeRegistry();
			registry.addContextType(CardContextType.CONTEXT_TYPE);

			fContextTypeRegistry = registry;
		}

		return fContextTypeRegistry;
	}

}
