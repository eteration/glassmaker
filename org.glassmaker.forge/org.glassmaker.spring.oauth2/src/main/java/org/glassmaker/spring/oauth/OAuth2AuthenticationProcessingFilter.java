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
import java.util.Collection;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;

public class OAuth2AuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

	private OAuth2EntryPoint authenticationEntryPoint;
	private OAuth2Util oAuth2Util;

	private RequestCache requestCache = new HttpSessionRequestCache();

	public OAuth2AuthenticationProcessingFilter() {
		this("/glassmaker/oauth/token");
	}

	public OAuth2AuthenticationProcessingFilter(String path) {
		super(path);

	}

	/**
	 * @param authenticationEntryPoint
	 *            the authentication entry point to set
	 */
	public void setAuthenticationEntryPoint(OAuth2EntryPoint authenticationEntryPoint) {
		this.authenticationEntryPoint = authenticationEntryPoint;
	}

	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();

		((SavedRequestAwareAuthenticationSuccessHandler) getSuccessHandler()).setAlwaysUseDefaultTargetUrl(false);

		setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
			public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
				requestCache.saveRequest(request, response);
				authenticationEntryPoint.commence(request, response, exception);
			}
		});
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

		// If the request is already authenticated we can assume that this
		// filter is not needed

		Authentication authentication = getAuthentication(request);
		if (authentication != null && authentication.isAuthenticated()) {
			return authentication;
		}

		if (response.isCommitted())
			logger.info("RESPONSE IS COMMITTED!");

		return createAuthentication(request);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

		super.successfulAuthentication(request, response, chain, authResult);

		if (!response.isCommitted()) {
			//chain.doFilter(request, response);
		}
	}

	@Override
	protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {

		return getAuthentication(request) == null;
	}

	private Authentication getAuthentication(HttpServletRequest request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		HttpSession session = request.getSession();
		if (session != null) {
			SecurityContext securityContext = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
			if (securityContext != null) {
				auth = securityContext.getAuthentication();
				SecurityContextHolder.getContext().setAuthentication(auth);
			}
		}

		return auth;
	}

	private Authentication createAuthentication(HttpServletRequest request) throws BadCredentialsException {
		try {
			if (request.getParameter("code") != null) {
				AuthorizationCodeFlow flow = oAuth2Util.newAuthorizationCodeFlow();
				TokenResponse tokenResponse = null;

				try {
					tokenResponse = oAuth2Util.newTokenRequest(flow, request.getParameter("code")).execute();
				} catch (TokenResponseException e) {
					if (e.getDetails().getError().contains("invalid_grant")) {
						logger.warn("User disabled Glassware. Attempting to re-authenticate");
						throw new BadCredentialsException("Start Login flow");
					}
				}

				// Extract the Google User ID from the ID token in the auth
				// response
				// String userId = ((GoogleTokenResponse)
				// tokenResponse).parseIdToken().getPayload().getUserId();
				String subject = ((GoogleTokenResponse) tokenResponse).parseIdToken().getPayload().getSubject();
				// String email = (String) ((GoogleTokenResponse)
				// tokenResponse).parseIdToken().getPayload().get("email");

				logger.info("Code exchange worked. User " + subject + " logged in.");
				Object mirrorCre = flow.createAndStoreCredential(tokenResponse, subject);

				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(subject, mirrorCre, (Collection<? extends GrantedAuthority>) new ArrayList<GrantedAuthority>());
				auth.setDetails(tokenResponse.getAccessToken());
				return this.getAuthenticationManager().authenticate(auth);
			}
			if (request.getParameter("error") != null) {
				logger.error("Something went wrong during auth: " + request.getParameter("error"));
				throw new AccessDeniedException(request.getParameter("error"));
			} else {
				Authentication auth = getAuthentication(request);
				if (auth == null)
					throw new BadCredentialsException("Start Login flow");
				else
					return auth;
			}
		} catch (IOException e) {
			logger.error(e);
			throw new BadCredentialsException("CreateAuthentication Failed", e);
		}
	}

	public OAuth2Util getoAuth2Util() {
		return oAuth2Util;
	}

	public void setoAuth2Util(OAuth2Util oAuth2Util) {
		this.oAuth2Util = oAuth2Util;
	}

	
}
