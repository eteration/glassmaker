#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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

package ${package}.auth;

import java.util.Collection;
import java.util.LinkedList;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component("oAuth2UserDetailsService")
public class InMemoryUserDetailsService implements UserDetailsService, ApplicationListener<InteractiveAuthenticationSuccessEvent>{
    public static class CustomUserDetails implements UserDetails {
            private static final long serialVersionUID = -1L;
            String userName;
            String token;
 
            Collection<GrantedAuthority> fakeAuthorities = new LinkedList<GrantedAuthority>();
            
            public CustomUserDetails(String userName, String password) {
            		this.userName = userName;
            		this.token = password;
                    fakeAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                    //fakeAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }

			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				return fakeAuthorities;
			}

			@Override
			public String getPassword() {
				return token;
			}

			@Override
			public String getUsername() {
				return userName;
			}

			@Override
			public boolean isAccountNonExpired() {
				return true;
			}

			@Override
			public boolean isAccountNonLocked() {
				return true;
			}

			@Override
			public boolean isCredentialsNonExpired() {
				return true;
			}

			@Override
			public boolean isEnabled() {
				return true;
			}
    }
    public UserDetails loadUserByUsername(String username)
                    throws UsernameNotFoundException {
           
            return new CustomUserDetails(username, "RANDOMPASSWORD");
    }
    

    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
            Authentication auth = event.getAuthentication();
            if(auth instanceof UsernamePasswordAuthenticationToken) {
            	UsernamePasswordAuthenticationToken oAuth2 = (UsernamePasswordAuthenticationToken)auth;
                    if(oAuth2.getDetails() != null) {
                            // This is a google user
                            
                            //CustomUserDetails userDetails = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                            //updateDbUser(userDetails);
                    }
            }
    }
    
    void updateDbUser(CustomUserDetails userDetails) {
          
    }
}