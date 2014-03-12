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
package ${package}.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassmaker.spring.web.MirrorTemplate;
import org.glassmaker.spring.web.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.api.services.mirror.model.Command;
import com.google.api.services.mirror.model.Contact;
import com.google.api.services.mirror.model.MenuItem;
import com.google.api.services.mirror.model.MenuValue;
import com.google.api.services.mirror.model.NotificationConfig;
import com.google.api.services.mirror.model.TimelineItem;
import com.google.api.services.mirror.model.TimelineListResponse;
import com.google.common.collect.Lists;

@Controller
public class MainController {

	private static final Log logger = LogFactory.getLog(MainController.class);

	@Autowired
	private MirrorTemplate mirrorTemplate;
	

	@RequestMapping(value = "/allcards")
	public ModelAndView all() throws IOException {

		TimelineListResponse items = mirrorTemplate.listItems(20);

		// items.getItems().get(0).getText()
		ModelAndView mav = new ModelAndView("items");
		mav.addObject("itemResponse", items);
		return mav;
	}

	@RequestMapping(value = "/subscribe")
	public ModelAndView subcribe(HttpServletRequest req, @RequestParam("collection") String collection) {

		// subscribe (only works deployed to production)
		String message = "";
		try {
			mirrorTemplate.insertSubscription(WebUtil.buildUrl(req, "/notify"), collection);
			message = "Application is now subscribed to updates.";
		} catch (Exception e) {
			logger.error("Could not subscribe " + WebUtil.buildUrl(req, "/notify") + " because " + e.getMessage(), e);
			message = "Failed to subscribe. Check your log for details";
		}

		ModelAndView mav = new ModelAndView("info");
		mav.addObject("message", message);
		return mav;
	}

	@RequestMapping(value = "/greet")
	public ModelAndView welcome(HttpServletRequest req, @RequestParam("greeting") String greeting) {

		// subscribe (only works deployed to production)
		String message = "";
		ModelAndView mav = new ModelAndView("info");
		try {

			mav.setViewName("greeting");
			mav.addObject("greeting", greeting);
			TimelineItem timelineItem  = mirrorTemplate.render(mav);			
			timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));
			TimelineItem insertedItem = mirrorTemplate.insertTimelineItem(timelineItem);
			message = "Inserted welcome message: " + insertedItem.getText();
			logger.info("Inserted welcome message " + greeting);
		} catch (Exception e) {
			logger.error("Could not send welcome  because " + e.getMessage(), e);
			message = "Could not send welcome. Check your log for details";
		}

		mav = new ModelAndView("info");
		mav.addObject("message", message);
		return mav;
	}

	@RequestMapping(value = "/deleteSubscription")
	public ModelAndView deleteSubscription(@RequestParam("subscriptionId") String subscriptionId) throws IOException {
		String message = "";
		mirrorTemplate.deleteSubscription(subscriptionId);
		ModelAndView mav = new ModelAndView("info");
		mav.addObject("message", message);
		return mav;
	}

	@RequestMapping(value = "/insertItem")
	public ModelAndView insertItem(@RequestParam("message") String message, @RequestParam("imageUrl") String imageUrl, @RequestParam("contentType") String contentType) throws IOException {

		String info = "";
		logger.info("Inserting Timeline Item");
		TimelineItem timelineItem = new TimelineItem();

		if (message != null) {
			timelineItem.setText(message);
		}

		// Triggers an audible tone when the timeline item is received
		timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));

		if (imageUrl != null) {
			// Attach an image, if we have one
			URL url = new URL(imageUrl);

			mirrorTemplate.insertTimelineItem(timelineItem, contentType, url.openStream());
		} else {
			mirrorTemplate.insertTimelineItem(timelineItem);
		}

		info = "A timeline item has been inserted.";
		ModelAndView mav = new ModelAndView("info");
		mav.addObject("message", info);
		return mav;
	}

	@RequestMapping(value = "/insertPaginatedItem")
	public ModelAndView insertPaginatedItem(HttpServletRequest req, @RequestParam("message") String message, @RequestParam("imageUrl") String imageUrl, @RequestParam("contentType") String contentType) throws Exception {

		String info = "";
		logger.info("Inserting Timeline Item");
		ModelAndView mav = new ModelAndView("samplePage");
		mav.addObject("hello", "Merhaba!");
		TimelineItem timelineItem = mirrorTemplate.render(mav);
		List<MenuItem> menuItemList = new ArrayList<MenuItem>();
		menuItemList.add(new MenuItem().setAction("OPEN_URI").setPayload(WebUtil.buildUrl(req, "/demo")));
		timelineItem.setMenuItems(menuItemList);

		// Triggers an audible tone when the timeline item is received
		timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));

		mirrorTemplate.insertTimelineItem(timelineItem);

		info = "A timeline item has been inserted.";
		mav = new ModelAndView("info");
		mav.addObject("message", info);
		return mav;
	}

	@RequestMapping(value = "/insertItemWithAction")
	public ModelAndView insertItemWithAction(HttpServletRequest req, @RequestParam("question") String question) throws Exception {

		String info = "";
		logger.info("Inserting Timeline Item");

		ModelAndView mav = new ModelAndView("samplePage");
		mav.addObject("hello", "Merhaba!");
		TimelineItem timelineItem = mirrorTemplate.render(mav);

		List<MenuItem> menuItemList = new ArrayList<MenuItem>();
		// Built in actions
		menuItemList.add(new MenuItem().setAction("REPLY"));
		menuItemList.add(new MenuItem().setAction("READ_ALOUD"));

		// And custom actions
		List<MenuValue> menuValues = new ArrayList<MenuValue>();
		menuValues.add(new MenuValue().setIconUrl(WebUtil.buildUrl(req, "/assets/images/menu_icons/ic_person_50.png")).setDisplayName("Ask Us"));
		menuItemList.add(new MenuItem().setValues(menuValues).setId("askus").setAction("CUSTOM"));
		timelineItem.setMenuItems(menuItemList);
		timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));

		mirrorTemplate.insertTimelineItem(timelineItem);

		info = "A timeline item has been inserted.";
		mav = new ModelAndView("info");
		mav.addObject("message", info);
		return mav;
	}

	@RequestMapping(value = "/insertContact")
	public ModelAndView insertContact(@RequestParam("id") String id, @RequestParam("name") String name, @RequestParam("name") String iconUrl) throws IOException {

		String info = "";
		logger.info("Inserting Contact");
		Contact contact = new Contact();
		contact.setId(id);
		contact.setDisplayName(name);
		contact.setImageUrls(Lists.newArrayList(iconUrl));
		contact.setAcceptCommands(Lists.newArrayList(new Command().setType("TAKE_A_NOTE")));

		mirrorTemplate.insertContact(contact);

		info = "Contact has been inserted:" + name;
		ModelAndView mav = new ModelAndView("info");
		mav.addObject("message", info);
		return mav;
	}

	@RequestMapping(value = "/deleteContact")
	public ModelAndView deleteContact(@RequestParam("id") String id) throws IOException {

		String info = "";
		logger.info("Deleting Contact");

		mirrorTemplate.deleteContact(id);

		info = "Contact has been deleted:" + id;
		ModelAndView mav = new ModelAndView("info");
		mav.addObject("message", info);
		return mav;
	}

	@RequestMapping(value = "/deleteTimelineItem")
	public ModelAndView deleteTimelineItem(@RequestParam("id") String id) throws IOException {

		String info = "";
		logger.info("Deleting TimelineItem");

		mirrorTemplate.deleteTimelineItem(id);

		info = "TimelineItem has been deleted:" + id;
		ModelAndView mav = new ModelAndView("info");
		mav.addObject("message", info);
		return mav;
	}

}
