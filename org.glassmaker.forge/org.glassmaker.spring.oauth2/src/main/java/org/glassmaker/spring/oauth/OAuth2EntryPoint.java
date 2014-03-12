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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Component;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.http.GenericUrl;

@Component("oAuth2EntryPoint")
public class OAuth2EntryPoint implements AuthenticationEntryPoint, InitializingBean {
	// ~ Static fields/initializers
	// =====================================================================================

	private static final Log logger = LogFactory.getLog(OAuth2EntryPoint.class);

	// ~ Instance fields
	// ================================================================================================
	@Value("${callback}")
	private String callbackUrl;
	
	@Autowired
	private OAuth2Util oAuth2Util;

	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	public OAuth2EntryPoint() {
	}

	public void afterPropertiesSet() throws Exception {
	}

	/**
	 * Performs the redirect (or forward) to the login form URL.
	 */
	public void commence(HttpServletRequest req, HttpServletResponse resp, AuthenticationException authException) throws IOException, ServletException {

		String redirectUrl = null;
		logger.debug("No auth context found. Kicking off a new auth flow.");

		AuthorizationCodeFlow flow = oAuth2Util.newAuthorizationCodeFlow();
		GenericUrl url = flow.newAuthorizationUrl().setRedirectUri(getCallbackUrl());
		url.set("approval_prompt", "force");
		redirectUrl = url.build();
		redirectStrategy.sendRedirect(req, resp, redirectUrl);

	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public OAuth2Util getoAuth2Util() {
		return oAuth2Util;
	}

	public void setoAuth2Util(OAuth2Util oAuth2Util) {
		this.oAuth2Util = oAuth2Util;
	}
}
