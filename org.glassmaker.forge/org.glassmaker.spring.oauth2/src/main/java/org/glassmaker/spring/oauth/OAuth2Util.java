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
 * Contributors:
 *    Naci Dai, Eteration A.S. - initial API, implementation and documentation
 *
 * </copyright>
 *
 *******************************************************************************/
package org.glassmaker.spring.oauth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.MemoryDataStoreFactory;

@Component("oAuth2Util")
public class OAuth2Util {
	private static final Log logger = LogFactory.getLog(OAuth2EntryPoint.class);

	private DataStore<StoredCredential> inmemoryDatastore;

	@Value("${client_id}")
	private String clientId;
	@Value("${client_secret}")
	private String clientSecret;
	@Value("${oauthscope}")
	private String scope;
	@Value("${accesstype}")
	private String accessType;
	@Value("${callback}")
	private String callback;

	/**
	 * Creates and returns a new {@link AuthorizationCodeFlow} for this app.
	 */
	public AuthorizationCodeFlow newAuthorizationCodeFlow() throws IOException {

		return new GoogleAuthorizationCodeFlow.Builder(new NetHttpTransport(), new JacksonFactory(), clientId, clientSecret, Collections.singleton(scope)).setAccessType(accessType).setCredentialDataStore(getInMemoryDatastore()).build();
	}

	public AuthorizationCodeTokenRequest newTokenRequest(AuthorizationCodeFlow flow, String code) throws IOException {
		AuthorizationCodeTokenRequest tr = flow.newTokenRequest(code).setRedirectUri(callback);
		return tr;
	}

	/**
	 * Get the current user's ID from the session
	 * 
	 * @return string user id or null if no one is logged in
	 */
	public String getUserId(HttpServletRequest request) {
		HttpSession session = request.getSession();
		return (String) session.getAttribute("userId");
	}

	public void setUserId(HttpServletRequest request, String userId) {
		HttpSession session = request.getSession();
		session.setAttribute("userId", userId);
	}

	public void clearUserId(HttpServletRequest request) throws IOException {
		// Delete the credential in the credential store
		String userId = getUserId(request);
		inmemoryDatastore.delete(userId);
		// Remove their ID from the local session
		request.getSession().removeAttribute("userId");
	}

	public Credential getCredential(String userId) throws IOException {
		if (userId == null) {
			return null;
		} else {
			return newAuthorizationCodeFlow().loadCredential(userId);
		}
	}

	public Credential getCredential(HttpServletRequest req) throws IOException {
		return newAuthorizationCodeFlow().loadCredential(getUserId(req));
	}

	public List<String> getAllUserIds() throws IOException {
		List<String> allUsers = new ArrayList<String>();
		for (String user : inmemoryDatastore.keySet()) {
			allUsers.add(user);
		}
		return allUsers;
	}

	public DataStore<StoredCredential> getInMemoryDatastore() {
		if (inmemoryDatastore == null) {
			try {
				inmemoryDatastore = MemoryDataStoreFactory.getDefaultInstance().getDataStore("ListableMemoryCredentialStore");
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return inmemoryDatastore;
	}

	public boolean requiresAuthentication(HttpServletRequest request) {

		HttpSession session = request.getSession();
		if (session != null) {
			SecurityContext securityContext = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
			if (securityContext != null) {
				Authentication auth = securityContext.getAuthentication();
				if (auth != null && auth.isAuthenticated())
					return false;
			}
		}

		String code = request.getParameter("code");
		// If we have a code, finish the OAuth 2.0 dance
		if (code == null) {
			return true;
		}
		return false;
	}
}
