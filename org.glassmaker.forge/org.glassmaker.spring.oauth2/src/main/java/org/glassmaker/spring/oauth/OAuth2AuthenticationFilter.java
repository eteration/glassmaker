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
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.filter.GenericFilterBean;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;

public class OAuth2AuthenticationFilter extends GenericFilterBean implements InitializingBean {

    AuthenticationManager authManager;

    private OAuth2Util oAuth2Util;

    public OAuth2AuthenticationFilter(AuthenticationManager authManager) {
        this.authManager = authManager;
    }
    
    @Override
    public void afterPropertiesSet() throws ServletException {
    	super.afterPropertiesSet();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
  
       if(request.getParameter("code") != null ) {
			AuthorizationCodeFlow flow = oAuth2Util.newAuthorizationCodeFlow();
			TokenResponse tokenResponse = oAuth2Util.newTokenRequest(flow,request.getParameter("code")).execute();

			// Extract the Google User ID from the ID token in the auth
			// response
			//String userId = ((GoogleTokenResponse) tokenResponse).parseIdToken().getPayload().getUserId();
			String subject = ((GoogleTokenResponse) tokenResponse).parseIdToken().getPayload().getSubject();
			//String email = (String) ((GoogleTokenResponse) tokenResponse).parseIdToken().getPayload().get("email");

			logger.info("Code exchange worked. User " + subject + " logged in.");
			flow.createAndStoreCredential(tokenResponse, subject);

			
			Authentication auth = new UsernamePasswordAuthenticationToken(subject, tokenResponse.getAccessToken(), (Collection<? extends GrantedAuthority>) new ArrayList<GrantedAuthority>());
			authManager.authenticate(auth);
			SecurityContextHolder.getContext().setAuthentication(authManager.authenticate(auth)); 
			((HttpServletRequest)request).getSession().setAttribute(
					HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
					SecurityContextHolder.getContext());
			
			if(auth != null){
				onAuthenticationSuccess((HttpServletRequest)request,(HttpServletResponse)response,auth);
			}
           
        }
       chain.doFilter(request, response);
        
    }
    

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
   
    	SavedRequest savedRequest = 
    		    new HttpSessionRequestCache().getRequest(request, response);
    	
       if (savedRequest == null) {
             return;
        }
       HttpSession session = request.getSession();
       session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

        
        // Use the DefaultSavedRequest URL
        String targetUrl = savedRequest.getRedirectUrl();
        logger.debug("Redirecting to DefaultSavedRequest Url: " + targetUrl);
        response.sendRedirect(targetUrl);
    }
}
