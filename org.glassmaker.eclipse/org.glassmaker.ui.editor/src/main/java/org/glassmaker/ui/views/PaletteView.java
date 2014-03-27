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
package org.glassmaker.ui.views;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Tool;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.glassmaker.ui.GlassmakerUIPlugin;
import org.glassmaker.ui.editor.CardEditor;



public class PaletteView extends ViewPart {
	
	private PaletteViewer viewer;
	private TreeMap<String, List<IPaletteItem>> items = new TreeMap<String, List<IPaletteItem>>();
	private HashMap<HTMLPaletteEntry, IPaletteItem> tools = new HashMap<HTMLPaletteEntry, IPaletteItem>();

	public PaletteView() {
		
		Template[] templates = GlassmakerUIPlugin.getDefault().getTemplateStore().getTemplates();
		ImageRegistry imageRegistry = GlassmakerUIPlugin.getDefault().getImageRegistry();
		for(Template t: templates){
			String imageId = "org.glassmaker.ui.templates."+t.getName().replace(" ", "").toLowerCase();
			addPaletteItem("Card Types",new DefaultPaletteItem(t.getName(),
					imageRegistry.getDescriptor(imageId),
					t.getPattern()));
		}
	}
	

	/**
	 * create controls and apply configurations.
	 */
	public void createPartControl(Composite parent) {
		viewer = new PaletteViewer();
		viewer.createControl(parent);
		
		PaletteRoot root = new PaletteRoot();
		
		String[] category = getCategories();
		for(int i=0;i<category.length;i++){
			PaletteDrawer group = new PaletteDrawer(category[i]);
			IPaletteItem[] items = getPaletteItems(category[i]);
			for(int j=0;j<items.length;j++){
				HTMLPaletteEntry entry = new HTMLPaletteEntry(items[j].getLabel(),null,items[j].getImageDescriptor());
				tools.put(entry,items[j]);
				group.add(entry);
			}
			root.add(group);
		}
		
		viewer.setPaletteRoot(root);
		
		viewer.getControl().addMouseListener(new MouseAdapter(){
			@Override
			public void mouseDoubleClick(MouseEvent e) {

//				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(); 
//				IEditorPart editorPart = page.getActiveEditor();
//				if(editorPart!=null){
//					editorPart.setFocus();
//				}
				if (e.button == 1) {
					EditPart part = PaletteView.this.viewer.findObjectAt(new Point(e.x, e.y));
					IPaletteItem item = null;
					if (part != null) {
						if (part.getModel() instanceof HTMLPaletteEntry)
							item = tools.get(part.getModel());
					}
					if (item != null)
						insert(item);
				}
				
			}
		});
	
	}
	
	private void insert(IPaletteItem item) {
		if(item == null)
			return;
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(); 
		IEditorPart editorPart = page.getActiveEditor();
		// execute processing of the palette item
		if(editorPart!=null){
			if(editorPart instanceof CardEditor){
				item.execute((CardEditor)editorPart);
			}
			else if(editorPart instanceof IPaletteTarget){
				item.execute(((IPaletteTarget)editorPart).getPaletteTarget());
			}
		}
	}
	
	/**
	 * Adds PaletteItem to the specified category.
	 * 
	 * @param category the category
	 * @param item the item
	 */
	private void addPaletteItem(String category,IPaletteItem item){
		if(items.get(category)==null){
			List<IPaletteItem> list = new ArrayList<IPaletteItem>();
			items.put(category,list);
		}
		List<IPaletteItem> list = items.get(category);
		list.add(item);
	}
	

	/**
	 * Returns PaletteItems which are contained by the specified category.
	 * 
	 * @param category the category
	 * @return the array of items which are contained by the category
	 */
	private IPaletteItem[] getPaletteItems(String category){
		List<IPaletteItem> list = items.get(category);
		if(list==null){
			return new IPaletteItem[0];
		}
		return list.toArray(new IPaletteItem[list.size()]);
	}
	
	/**
	 * Returns all categories.
	 * 
	 * @return the array which contains all categories
	 */
	private String[] getCategories(){
		return items.keySet().toArray(new String[0]);
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	/** ToolEntry for HTML tag palette */
	private class HTMLPaletteEntry extends ToolEntry {
		
		public HTMLPaletteEntry(String label, String shortDescription, ImageDescriptor icon) {
			super(label, shortDescription, icon, icon);
		}
		
		public Tool createTool() {
			return null;
		}
	}
	

}