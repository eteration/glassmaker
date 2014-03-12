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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassmaker.spring.web.MirrorTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.api.services.mirror.model.Command;
import com.google.api.services.mirror.model.Contact;
import com.google.common.collect.Lists;

@Component
public class NewUserBootstrapper implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {

	@Autowired
	MirrorTemplate mirrorTemplate;
	
	@Value("${symbol_dollar}{notifyprefix}")
	private String notifyprefix;


	private static final Log logger = LogFactory.getLog(NewUserBootstrapper.class);

	private static final String CONTACT_ID = "com.eteration.glassmaker.contact.${package}";

	private static final String CONTACT_NAME = "Glassmaker Plugin TestOAuth";


	@Override
	public void onApplicationEvent(InteractiveAuthenticationSuccessEvent appEvent) {
		String userId = (String) appEvent.getAuthentication().getPrincipal();
		ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (sra != null) {
			HttpServletRequest req = sra.getRequest();
			try {
				this.bootstrapNewUser(req, userId);
			} catch (IOException e) {
				e.printStackTrace();
				logger.error(e);
			}
		}

	}
	
	private void bootstrapNewUser(HttpServletRequest req, String userId) throws IOException {

		// Create contact
		Contact starterProjectContact = new Contact();
		starterProjectContact.setId(CONTACT_ID);
		starterProjectContact.setDisplayName(CONTACT_NAME);
		starterProjectContact.setImageUrls(Lists.newArrayList("http://www.eteration.com/wp-content/themes/responsive/images/banner/slider-bg-academy.png"));
		starterProjectContact.setAcceptCommands(Lists.newArrayList(new Command().setType("TAKE_A_NOTE")));
		Contact insertedContact = mirrorTemplate.insertContact(starterProjectContact);
		logger.info("Bootstrapper inserted contact " + insertedContact.getId() + " for user " + userId);

	}


}
