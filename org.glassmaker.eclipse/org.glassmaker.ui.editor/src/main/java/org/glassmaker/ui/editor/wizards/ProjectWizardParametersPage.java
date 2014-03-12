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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.ibm.icu.lang.UCharacter;

/**
 * Wizard page responsible for gathering information about the Maven2 artifact
 * when an archetype is being used to create a project (thus the class name
 * pun).
 */
public class ProjectWizardParametersPage extends WizardPage {

	public static final String O_AUTH_CALLBACK = "OAuth Callback";
	public static final String CLIENT_SECRET = "Client Secret";
	public static final String CLIENT_ID = "Client Id";
	public static final String DEFAULT_VERSION = "0.0.1-SNAPSHOT"; //$NON-NLS-1$
	public static final String DEFAULT_PACKAGE = "foo"; //$NON-NLS-1$

	Table propertiesTable;

	TableViewer propertiesViewer;

	final public static String KEY_PROPERTY = "key"; //$NON-NLS-1$

	final public static int KEY_INDEX = 0;

	final public static String VALUE_PROPERTY = "value"; //$NON-NLS-1$

	final public static int VALUE_INDEX = 1;

	/** group id text field */
	protected Combo groupIdCombo;

	/** artifact id text field */
	protected Combo artifactIdCombo;

	/** version text field */
	protected Combo versionCombo;

	/** package text field */
	protected Combo packageCombo;

	protected Button removeButton;

	private boolean isUsed = true;

	protected Set<String> requiredProperties;

	protected Set<String> optionalProperties;

	/** shows if the package has been customized by the user */
	protected boolean packageCustomized = false;

	private ProjectParameters params;

	/** Creates a new page. */
	public ProjectWizardParametersPage() {

		super("wizardPage");
		setTitle("Glassmaker Project Parameters");
		setDescription("Glassmaker Project Parameters");
		setPageComplete(false);

		requiredProperties = new HashSet<String>();
		optionalProperties = new HashSet<String>();
	}

	/** Creates page controls. */
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(3, false));

		createArtifactGroup(composite);
		createPropertiesGroup(composite);
		setArchetype() ;
		validate();
		
		setControl(composite);

	}

	private void createArtifactGroup(Composite parent) {

		Label groupIdlabel = new Label(parent, SWT.NONE);
		groupIdlabel.setText("Group Id:");

		groupIdCombo = new Combo(parent, SWT.BORDER);
		groupIdCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		groupIdCombo.setData("name", "groupId"); //$NON-NLS-1$ //$NON-NLS-2$
		groupIdCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateJavaPackage();
				validate();
			}
		});

		Label artifactIdLabel = new Label(parent, SWT.NONE);
		artifactIdLabel.setText("Artifact Id:");

		artifactIdCombo = new Combo(parent, SWT.BORDER);
		artifactIdCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		artifactIdCombo.setData("name", "artifactId"); //$NON-NLS-1$ //$NON-NLS-2$
		artifactIdCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateJavaPackage();
				validate();
			}
		});

		Label versionLabel = new Label(parent, SWT.NONE);
		versionLabel.setText("Version");

		versionCombo = new Combo(parent, SWT.BORDER);
		GridData gd_versionCombo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_versionCombo.widthHint = 150;
		versionCombo.setLayoutData(gd_versionCombo);
		versionCombo.setText(DEFAULT_VERSION);
		versionCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});

		Label packageLabel = new Label(parent, SWT.NONE);
		packageLabel.setText("Package");

		packageCombo = new Combo(parent, SWT.BORDER);
		packageCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		packageCombo.setData("name", "package"); //$NON-NLS-1$ //$NON-NLS-2$
		packageCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (!packageCustomized && !packageCombo.getText().equals(getDefaultJavaPackage())) {
					packageCustomized = true;
				}
				validate();
			}
		});
	}

	private void createPropertiesGroup(Composite composite) {
		Label propertiesLabel = new Label(composite, SWT.NONE);
		propertiesLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		propertiesLabel.setText("Properties");

		propertiesViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		propertiesTable = propertiesViewer.getTable();
		propertiesTable.setLinesVisible(true);
		propertiesTable.setHeaderVisible(true);
		propertiesTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 2));

		TableColumn propertiesTableNameColumn = new TableColumn(propertiesTable, SWT.NONE);
		propertiesTableNameColumn.setWidth(130);
		propertiesTableNameColumn.setText("Name");

		TableColumn propertiesTableValueColumn = new TableColumn(propertiesTable, SWT.NONE);
		propertiesTableValueColumn.setWidth(230);
		propertiesTableValueColumn.setText("Value");

		propertiesViewer.setColumnProperties(new String[] { KEY_PROPERTY, VALUE_PROPERTY });

		propertiesViewer.setCellEditors(new CellEditor[] { new TextCellEditor(propertiesTable, SWT.NONE), new TextCellEditor(propertiesTable, SWT.NONE) });
		propertiesViewer.setCellModifier(new ICellModifier() {
			public boolean canModify(Object element, String property) {
				return true;
			}

			public void modify(Object element, String property, Object value) {
				if (element instanceof TableItem) {
					((TableItem) element).setText(getTextIndex(property), String.valueOf(value));
					validate();
				}
			}

			public Object getValue(Object element, String property) {
				if (element instanceof TableItem) {
					return ((TableItem) element).getText(getTextIndex(property));
				}
				return null;
			}
		});

	}

	/**
	 * Validates the contents of this wizard page.
	 * <p>
	 * Feedback about the validation is given to the user by displaying error
	 * messages or informative messages on the wizard page. Depending on the
	 * provided user input, the wizard page is marked as being complete or not.
	 * <p>
	 * If some error or missing input is detected in the user input, an error
	 * message or informative message, respectively, is displayed to the user.
	 * If the user input is complete and correct, the wizard page is marked as
	 * begin complete to allow the wizard to proceed. To that end, the following
	 * conditions must be met:
	 * <ul>
	 * <li>The user must have provided a valid group ID.</li>
	 * <li>The user must have provided a valid artifact ID.</li>
	 * <li>The user must have provided a version for the artifact.</li>
	 * </ul>
	 * </p>
	 * 
	 * @see org.eclipse.jface.dialogs.DialogPage#setMessage(java.lang.String)
	 * @see org.eclipse.jface.wizard.WizardPage#setErrorMessage(java.lang.String)
	 * @see org.eclipse.jface.wizard.WizardPage#setPageComplete(boolean)
	 */
	void validate() {
		if (isVisible()) {
			String error = validateInput();
			setErrorMessage(error);
			setPageComplete(error == null);
		}
	}

	private String validateInput() {
		String error = validateGroupIdInput(groupIdCombo.getText().trim());
		if (error != null) {
			return error;
		}

		error = validateArtifactIdInput(artifactIdCombo.getText().trim());
		if (error != null) {
			return error;
		}

		String versionValue = versionCombo.getText().trim();
		if (versionValue.length() == 0) {
			return "Must have a version";
		}
		// TODO: check validity of version?

		String packageName = packageCombo.getText();
		if (packageName.trim().length() != 0) {
			if (!Pattern.matches("[A-Za-z_$][A-Za-z_$\\d]*(?:\\.[A-Za-z_$][A-Za-z_$\\d]*)*", packageName)) { //$NON-NLS-1$
				return "Must have a valid Java package name";
			}
		}

//		// validate project name
//		IStatus nameStatus = validateProjectName(getModel());
//		if (!nameStatus.isOK()) {
//			return "Project name is not valid";
//		}

		return null;
	}

	/** Ends the wizard flow chain. */
	public IWizardPage getNextPage() {
		return null;
	}

	public void setArchetype() {
		propertiesTable.removeAll();
		requiredProperties.clear();
		optionalProperties.clear();

		Properties properties = new Properties();
		properties.setProperty(CLIENT_ID, "ENTER YOUR GOOGLE API CLIENT ID");
		properties.setProperty(CLIENT_SECRET, "ENTER YOUR GOOGLE API CLIENT SECRET");
		properties.setProperty(O_AUTH_CALLBACK,"https://localhost:8888/oauth/callback" );

		if (properties != null) {
			for (Iterator<Map.Entry<Object, Object>> it = properties.entrySet().iterator(); it.hasNext();) {
				Map.Entry<?, ?> e = it.next();
				String key = (String) e.getKey();
				addTableItem(key, (String) e.getValue());
				optionalProperties.add(key);
			}
		}

	}

	/**
	 * @param key
	 * @param value
	 */
	TableItem addTableItem(String key, String value) {
		TableItem item = new TableItem(propertiesTable, SWT.NONE);
		item.setData(item);
		item.setText(KEY_INDEX, key);
		item.setText(VALUE_INDEX, value == null ? "" : value); //$NON-NLS-1$
		return item;
	}

	/**
	 * Updates the properties when a project name is set on the first page of
	 * the wizard.
	 */
	public void setProjectName(String projectName) {
		if (artifactIdCombo.getText().equals(groupIdCombo.getText())) {
			groupIdCombo.setText(projectName);
		}
		artifactIdCombo.setText(projectName);
		packageCombo.setText("org." + projectName.replace('-', '.')); //$NON-NLS-1$
		validate();
	}

	/**
	 * Updates the properties when a project name is set on the first page of
	 * the wizard.
	 */
	public void setParentProject(String groupId, String artifactId, String version) {
		groupIdCombo.setText(groupId);
		versionCombo.setText(version);
		validate();
	}

	/** Enables or disables the artifact id text field. */
	public void setArtifactIdEnabled(boolean b) {
		artifactIdCombo.setEnabled(b);
	}

	/** Returns the package name. */
	public String getJavaPackage() {
		if (packageCombo.getText().length() > 0) {
			return packageCombo.getText();
		}
		return getDefaultJavaPackage();
	}

	/** Updates the package name if the related fields changed. */
	protected void updateJavaPackage() {
		if (packageCustomized) {
			return;
		}

		String defaultPackageName = getDefaultJavaPackage();
		packageCombo.setText(defaultPackageName);
	}

	/** Returns the default package name. */
	protected String getDefaultJavaPackage() {
		return ProjectWizardParametersPage.getDefaultJavaPackage(groupIdCombo.getText().trim(), artifactIdCombo.getText().trim());
	}


	public void setUsed(boolean isUsed) {
		this.isUsed = isUsed;
	}

	public boolean isPageComplete() {
		return !isUsed || super.isPageComplete();
	}

	/** Loads the group value when the page is displayed. */
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		boolean shouldValidate = false;

		if (visible) {



			if (groupIdCombo.getText().length() == 0 && groupIdCombo.getItemCount() > 0) {
				groupIdCombo.setText(groupIdCombo.getItem(0));
				packageCombo.setText(getDefaultJavaPackage());
				packageCustomized = false;
			}

			if (shouldValidate) {
				validate();
			}
		}
	}

	public Properties getProperties() {
		if (propertiesViewer.isCellEditorActive()) {
			propertiesTable.setFocus();
		}
		Properties properties = new Properties();
		for (int i = 0; i < propertiesTable.getItemCount(); i++) {
			TableItem item = propertiesTable.getItem(i);
			properties.put(item.getText(KEY_INDEX), item.getText(VALUE_INDEX));
		}
		return properties;
	}

	public int getTextIndex(String property) {
		return KEY_PROPERTY.equals(property) ? KEY_INDEX : VALUE_INDEX;
	}

	public static String getDefaultJavaPackage(String groupId, String artifactId) {
		StringBuffer sb = new StringBuffer(groupId);

		if (sb.length() > 0 && artifactId.length() > 0) {
			sb.append('.');
		}

		sb.append(artifactId);

		if (sb.length() == 0) {
			sb.append(DEFAULT_PACKAGE);
		}

		boolean isFirst = true;
		StringBuffer pkg = new StringBuffer();
		for (int i = 0; i < sb.length(); i++) {
			char c = sb.charAt(i);
			if (c == '-') {
				pkg.append('_');
				isFirst = false;
			} else {
				if (isFirst) {
					if (UCharacter.isJavaIdentifierStart(c)) {
						pkg.append(c);
						isFirst = false;
					}
				} else {
					if (c == '.') {
						pkg.append('.');
						isFirst = true;
					} else if (UCharacter.isJavaIdentifierPart(c)) {
						pkg.append(c);
					}
				}
			}
		}

		return pkg.toString();
	}

	private boolean isVisible() {
		return getControl() != null && getControl().isVisible();
	}

	protected String validateArtifactIdInput(String text) {
		return validateIdInput(text, true);
	}

	protected String validateGroupIdInput(String text) {
		return validateIdInput(text, false);
	}

	private String validateIdInput(String text, boolean artifact) {
		if (text == null || text.length() == 0) {
			return artifact ? "Must have an Artifact Id" : "Must have a Group Id";
		}

		if (text.contains(" ")) { //$NON-NLS-1$
			return artifact ? "Artifact Id cannot have spaces" : "Group Id cannot have spaces";
		}

		IStatus nameStatus = ResourcesPlugin.getWorkspace().validateName(text, IResource.PROJECT);
		if (!nameStatus.isOK()) {
			return artifact ? "Invalid Artifact Id" : "Invalid Group Id";
		}

		if (!text.matches("[A-Za-z0-9_\\-.]+")) { //$NON-NLS-1$
			return artifact ? "Invalid Artifact Id" : "Invalid Group Id";
		}
        params = initProjectParameters();
		return null;
	}
	
	
	public ProjectParameters initProjectParameters() {
		
		ProjectParameters params = new ProjectParameters();
		params.setGroupId(groupIdCombo.getText());
		params.setArtifactId(artifactIdCombo.getText());
		params.setVersion(versionCombo.getText());
		params.setProperties(getProperties());
		params.setPackageName(getJavaPackage());
		
		return params;
	
	}

	public ProjectParameters getParams() {
		return params;
	}

	public void setParams(ProjectParameters params) {
		this.params = params;
	}
	
	
}
