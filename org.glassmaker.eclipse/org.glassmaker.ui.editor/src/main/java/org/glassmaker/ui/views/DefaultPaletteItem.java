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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.glassmaker.ui.editor.CardEditor;


public class DefaultPaletteItem implements IPaletteItem {
	
	private String name;
	private ImageDescriptor image;
	private String content;
	
	/**
	 * The constructor.
	 * 
	 * @param name     item name
	 * @param image    icon
	 * @param content  insert text
	 */
	public DefaultPaletteItem(String name,ImageDescriptor image,String content){
		this.name    = name;
		this.image   = image;
		this.content = content;
	}
	
	public ImageDescriptor getImageDescriptor() {
		return image;
	}
	
	public String getLabel() {
		return name;
	}
	
	public String getContent(){
		return this.content;
	}
	
	public void execute(CardEditor editor){
		IDocument doc = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		ITextSelection sel = (ITextSelection)editor.getSelectionProvider().getSelection();
		try {
			int caret = content.length();
			if(content.indexOf("></")!=-1){
				caret = content.indexOf("></") + 1;
			}
			doc.replace(sel.getOffset(),sel.getLength(),content);
			editor.selectAndReveal(sel.getOffset() + caret, 0);
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
