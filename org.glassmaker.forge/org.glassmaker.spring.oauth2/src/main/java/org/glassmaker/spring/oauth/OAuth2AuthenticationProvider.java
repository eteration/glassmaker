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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component("oAuth2AuthenticationProvider")
public class OAuth2AuthenticationProvider implements AuthenticationProvider, ApplicationListener<InteractiveAuthenticationSuccessEvent>{

	@Autowired
	protected UserDetailsService userDetailsService;



	@Override
	public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
		System.out.println(event);
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		if (!supports(authentication.getClass())) {
			return null;
		}
		if (authentication instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken)authentication;
			UserDetails userDetails = userDetailsService.loadUserByUsername((String)token.getPrincipal());			
			UsernamePasswordAuthenticationToken newToken = new UsernamePasswordAuthenticationToken(token.getPrincipal(),token.getCredentials(),userDetails.getAuthorities());
			newToken.setDetails(token.getDetails());
			return newToken;
		}
		return null;
	}

	@Override
	public boolean supports(Class<?> authenticationclaz) {
		return authenticationclaz == UsernamePasswordAuthenticationToken.class;
	}

	public UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}
	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

}
