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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

public class OAuth2LogoutFilter extends LogoutFilter {
	private static final Log logger = LogFactory.getLog(OAuth2EntryPoint.class);

	protected String cookieName;

	public OAuth2LogoutFilter(String logoutSuccessUrl, LogoutHandler... handlers) {
		super(logoutSuccessUrl, handlers);
	}

	public String getCookieName() {
		return cookieName;
	}

	public void setCookieName(String cookieName) {
		this.cookieName = cookieName;
	}

	@Override
	protected boolean requiresLogout(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("OAuth2LogoutFilter.requiresLogout");
		// Normal logout processing (i.e. detect logout URL)
		if (super.requiresLogout(request, response))
			return true;
		// If SSO cookie is stale, clear session contents
		String cookieName = getCookieName();

		HttpSession session = request.getSession();
		String sessionSso = (String) request.getSession().getAttribute(cookieName);
		if (sessionSso != null) {
			String browserSso = getCookieValue(request, cookieName);
			if (!sessionSso.equals(browserSso)) {
				logger.debug("Invalidating stale session: " + sessionSso);
				session.invalidate();
			}
		}
		return false;
	}

	protected String getCookieValue(HttpServletRequest request, String cookieName) {
		String cookieValue = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null)
			for (Cookie cookie : cookies)
				if (cookie.getName().equals(cookieName)) {
					cookieValue = cookie.getValue();
					break;
				}
		return cookieValue;
	}
}
