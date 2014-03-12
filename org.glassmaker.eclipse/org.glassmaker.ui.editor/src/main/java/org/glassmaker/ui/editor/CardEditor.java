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
package org.glassmaker.ui.editor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.glassmaker.ui.CardUtil;
import org.glassmaker.ui.GlassmakerUIPlugin;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.mirror.Mirror;
import com.google.api.services.mirror.model.TimelineItem;
import com.google.gdt.eclipse.login.GoogleLogin;

/**
 * Multi-page editor to create Card templates. This example has 3 pages:
 * <ul>
 * <li>page 0 contains a nested StructuredTextEditor.
 * <li>page 1 allows you to preview content of the card in a local browser
 * <li>page 2 previews the page on a glass
 * </ul>
 */
public class CardEditor extends MultiPageEditorPart implements IResourceChangeListener {


	/** The text editor. */
	private StructuredTextEditor textEditor;

	/** The Preview Browser */
	private Browser browser;

	/**
	 * Creates a multi-page editor example.
	 */
	public CardEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	/**
	 * Creates Structured Twext Editot of the multi-page editor, which contains an HTML editor.
	 */
	void createPage0() {
		try {
			textEditor = new StructuredTextEditor();
			int index = addPage(textEditor, getEditorInput());
			setPageText(index, "Source");
			setPartName(textEditor.getTitle());
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(), "Error creating nested text editor", null, e.getStatus());
		}
	}


	/**
	 * Creates page 1 of the multi-page editor, which is used to preview the page .
	 */
	void createPage1() {
		Composite composite = new Composite(getContainer(), SWT.NONE);
		FillLayout layout = new FillLayout();
		composite.setLayout(layout);
		browser = new Browser(composite, SWT.H_SCROLL | SWT.V_SCROLL);

		int index = addPage(composite);
		setPageText(index, "Preview");
	}

	/**
	 * Creates page 1 of the multi-page editor, which allows you to change the
	 * font used in page 2.
	 */
	void createPage2() {

		Composite composite = new Composite(getContainer(), SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		layout.numColumns = 2;

		Button previewOnGlass = new Button(composite, SWT.NONE);
		GridData gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan = 2;
		previewOnGlass.setLayoutData(gd);
		previewOnGlass.setText("Preview on glass...");

		previewOnGlass.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (!GoogleLogin.getInstance().isLoggedIn()) {
					GoogleLogin.promptToLogIn("Glassmaker");
				}			
				if (GoogleLogin.getInstance().isLoggedIn()) 
					previewOnGlass();
			}
		});

		int index = addPage(composite);
		setPageText(index, "Glass");
	}

	
	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() {
		createPage0();
		createPage1();
		createPage2();
	}

	/**
	 * The <code>MultiPageEditorPart</code> implementation of this
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor) {
		getEditor(0).doSave(monitor);
	}

	/**
	 * Saves the multi-page editor's document as another file. Also updates the
	 * text for page 0's tab, and updates this multi-page editor's input to
	 * correspond to the nested editor's.
	 */
	public void doSaveAs() {
		IEditorPart editor = getEditor(0);
		editor.doSaveAs();
		setPageText(0, editor.getTitle());
		setInput(editor.getEditorInput());
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}

	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		super.init(site, editorInput);
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * Calculates the contents of page 2 when the it is activated.
	 */
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		if (newPageIndex == 1) {
			showPage();
		}
	}

	/**
	 * Closes all project files on project close.
	 */
	public void resourceChanged(final IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for (int i = 0; i < pages.length; i++) {
						if (((FileEditorInput) textEditor.getEditorInput()).getFile().getProject().equals(event.getResource())) {
							IEditorPart editorPart = pages[i].findEditor(textEditor.getEditorInput());
							pages[i].closeEditor(editorPart, true);
						}
					}
				}
			});
		}
	}

	public IDocumentProvider getDocumentProvider() {
		return textEditor.getDocumentProvider();
	}

	public ISelectionProvider getSelectionProvider() {
		return textEditor.getSelectionProvider();
	}

	public void selectAndReveal(int i, int j) {
		textEditor.selectAndReveal(i, j);
	}

	/**
	 * Sorts the words in page 0, and shows them in page 2.
	 */
	void showPage() {

		String editorText = getDocumentProvider().getDocument(textEditor.getEditorInput()).get();
		deletePreviewFiles();

		File file = toPreviewFile(editorText);
		if (file != null) {
			PREVIEW_FILES_LIST.add(file);
			String s = "file://" + file.getAbsolutePath(); //$NON-NLS-1$
			browser.setJavascriptEnabled(true);
			browser.setUrl(s);

		} else {
			browser.setText(editorText, true);
		}

	}

	private List<File> PREVIEW_FILES_LIST = new ArrayList<File>();

	private void deletePreviewFiles() {
		for (File file : PREVIEW_FILES_LIST) {
			if (file != null && file.exists()) {
				file.delete();
			}
		}
		PREVIEW_FILES_LIST.clear();
	}

	public File getTempFile() {
		IFile file = ((FileEditorInput) this.getEditorInput()).getFile();
		return GlassmakerUIPlugin.getPreviewFile("." + file.getName() + ".html");
		//return new File(file.getLocation().makeAbsolute().toFile().getParentFile(), "." + file.getName() + ".html");
	}

	public File toPreviewFile(String result) {
		try {
			File file = getTempFile();
			if(file == null)
				return null;
			if (!file.exists()) {
				file.createNewFile();
			}

			String charset = "UTF-8"; 

			FileOutputStream fos = new FileOutputStream(file);
			PrintStream ps = new PrintStream(fos, true, charset);
			String wrappedHTML = CardUtil.wrapFragmantInPage(result.toString());
			ps.print(wrappedHTML);
			ps.close();
			fos.close();
			return file;
		} catch (IOException e) {
			return null;
		}
	}
	
	private void previewOnGlass() {
		try {
			Credential cred = GoogleLogin.getInstance().getCredential();
			cred.getAccessToken();
			Mirror m = new Mirror.Builder(GoogleNetHttpTransport.newTrustedTransport(), new JacksonFactory(), cred)
			.setApplicationName("Glassmaker Plugin")
			.build();

			String editorText = getDocumentProvider().getDocument(textEditor.getEditorInput()).get();
			deletePreviewFiles();
			TimelineItem timelineItem =  CardUtil.createTimeline(editorText);			
			Mirror.Timeline timeline = m.timeline();
			timeline.insert(timelineItem).execute();
			
		} catch (Exception e) {
			GlassmakerUIPlugin.logError(e.getMessage(), e);
		}
	}

}
