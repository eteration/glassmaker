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
package org.glassmaker.login;

import org.eclipse.jface.preference.IPreferenceStore;
import org.glassmaker.ui.GlassmakerUIPlugin;
import org.glassmaker.ui.editor.preferences.PreferenceConstants;

import com.google.gdt.eclipse.login.extensions.IClientProvider;

public class GlassmakerClientProvider implements IClientProvider {

	
	@Override
	public String getId() {
		IPreferenceStore ps = GlassmakerUIPlugin.getDefault().getPreferenceStore();	
		return ps.getString(PreferenceConstants.CLIENT_ID);
	}

	@Override
	public String getSecret() {
		IPreferenceStore ps = GlassmakerUIPlugin.getDefault().getPreferenceStore();	
		return ps.getString(PreferenceConstants.CLIENT_SECRET);
	}

}
