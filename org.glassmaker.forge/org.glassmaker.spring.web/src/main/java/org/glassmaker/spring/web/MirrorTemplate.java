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
package org.glassmaker.spring.web;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.mirror.Mirror;
import com.google.api.services.mirror.model.Attachment;
import com.google.api.services.mirror.model.Contact;
import com.google.api.services.mirror.model.ContactsListResponse;
import com.google.api.services.mirror.model.Subscription;
import com.google.api.services.mirror.model.SubscriptionsListResponse;
import com.google.api.services.mirror.model.TimelineItem;
import com.google.api.services.mirror.model.TimelineListResponse;
import com.google.common.io.ByteStreams;

@Component("mirrorTemplate")
public class MirrorTemplate {
	private static final Log logger = LogFactory.getLog(MirrorTemplate.class);

	@Autowired
	private TemplateMerger templateMerger;

	public Mirror getMirror() {
		return new Mirror.Builder(new NetHttpTransport(), new JacksonFactory(), getCredential())
		.setApplicationName("Glassmaker Plugin")
		.build();
	}

	public Contact insertContact(Contact contact) throws IOException {
		Mirror.Contacts contacts = getMirror().contacts();
		return contacts.insert(contact).execute();
	}

	public void deleteContact(String contactId) throws IOException {
		Mirror.Contacts contacts = getMirror().contacts();
		contacts.delete(contactId).execute();
	}

	public ContactsListResponse listContacts() throws IOException {
		Mirror.Contacts contacts = getMirror().contacts();
		return contacts.list().execute();
	}

	public Contact getContact(String id) throws IOException {
		try {
			Mirror.Contacts contacts = getMirror().contacts();
			return contacts.get(id).execute();
		} catch (GoogleJsonResponseException e) {
			logger.warn("Could not find contact with ID " + id);
			return null;
		}
	}

	public TimelineListResponse listItems(long count) throws IOException {
		Mirror.Timeline timelineItems = getMirror().timeline();
		Mirror.Timeline.List list = timelineItems.list();
		list.setMaxResults(count);
		return list.execute();
	}

	public Subscription insertSubscription(String callbackUrl, String collection) throws IOException {
		String userId = getUserId();

		logger.info("Attempting to subscribe verify_token " + userId + " with callback " + callbackUrl);

		// Rewrite "appspot.com" to "Appspot.com" as a workaround for
		// http://b/6909300.
		callbackUrl = callbackUrl.replace("appspot.com", "Appspot.com");

		Subscription subscription = new Subscription();
		// Alternatively, subscribe to "locations"
		subscription.setCollection(collection);
		subscription.setCallbackUrl(callbackUrl);
		subscription.setUserToken(userId);

		return getMirror().subscriptions().insert(subscription).execute();
	}

	/**
	 * Subscribes to notifications on the user's timeline.
	 */
	public void deleteSubscription(String id) throws IOException {
		getMirror().subscriptions().delete(id).execute();
	}

	public SubscriptionsListResponse listSubscriptions(Credential credential) throws IOException {
		Mirror.Subscriptions subscriptions = getMirror().subscriptions();
		return subscriptions.list().execute();
	}

	/**
	 * Inserts a simple timeline item.
	 * 
	 * @param getCredential
	 *            (credential) the user's credential
	 * @param item
	 *            the item to insert
	 */
	public TimelineItem insertTimelineItem(TimelineItem item) throws IOException {
		return getMirror().timeline().insert(item).execute();
	}

	/**
	 * Inserts an item with an attachment provided as a byte array.
	 * 
	 * @param item
	 *            the item to insert
	 * @param attachmentContentType
	 *            the MIME type of the attachment (or null if none)
	 * @param attachmentData
	 *            data for the attachment (or null if none)
	 */
	public void insertTimelineItem(TimelineItem item, String attachmentContentType, byte[] attachmentData) throws IOException {
		Mirror.Timeline timeline = getMirror().timeline();
		timeline.insert(item, new ByteArrayContent(attachmentContentType, attachmentData)).execute();

	}

	/**
	 * Inserts an item with an attachment provided as an input stream.
	 * 
	 * @param item
	 *            the item to insert
	 * @param attachmentContentType
	 *            the MIME type of the attachment (or null if none)
	 * @param attachmentInputStream
	 *            input stream for the attachment (or null if none)
	 */
	public void insertTimelineItem(TimelineItem item, String attachmentContentType, InputStream attachmentInputStream) throws IOException {
		insertTimelineItem(item, attachmentContentType, ByteStreams.toByteArray(attachmentInputStream));
	}

	public InputStream getAttachmentInputStream(String timelineItemId, String attachmentId) throws IOException {
		Mirror mirrorService = getMirror();
		Mirror.Timeline.Attachments attachments = mirrorService.timeline().attachments();
		Attachment attachmentMetadata = attachments.get(timelineItemId, attachmentId).execute();
		HttpResponse resp = mirrorService.getRequestFactory().buildGetRequest(new GenericUrl(attachmentMetadata.getContentUrl())).execute();
		return resp.getContent();
	}

	public String getAttachmentContentType(String timelineItemId, String attachmentId) throws IOException {
		Mirror.Timeline.Attachments attachments = getMirror().timeline().attachments();
		Attachment attachmentMetadata = attachments.get(timelineItemId, attachmentId).execute();
		return attachmentMetadata.getContentType();
	}

	public void deleteTimelineItem(String timelineItemId) throws IOException {
		getMirror().timeline().delete(timelineItemId).execute();
	}

	private Credential getCredential() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) auth;
		String accessToken = (String) token.getDetails();
		GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
		return credential;
	}

	private String getUserId() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) auth;
		String userId = (String) token.getPrincipal();
		return userId;
	}

	public TimelineItem render(ModelAndView mav) throws Exception {
		TimelineItem timelineItem = new TimelineItem();
		String article = templateMerger.merge(mav.getViewName(), mav.getModelMap());
		timelineItem.setHtml(article);
		return timelineItem;
	}

//	public void batchSend(TimelineItem allUsersItem) throws IOException {
//		BatchRequest batch = getMirror().batch();
//
////		BatchCallback callback = new BatchCallback();
////		for (String user : users) {
////			Credential userCredential = AuthUtil.getCredential(user);
////			MirrorClient.getMirror(userCredential).timeline().insert(allUsersItem).queue(batch, callback);
////		}
//
//		batch.execute();
//	}
//
//	private final class BatchCallback extends JsonBatchCallback<TimelineItem> {
//
//		@Override
//		public void onSuccess(TimelineItem item, HttpHeaders headers) throws IOException {
//			logger.info("Successfully sent an item: ");
//		}
//
//		@Override
//		public void onFailure(GoogleJsonError error, HttpHeaders headers) throws IOException {
//			logger.info("Failed to insert item: " + error.getMessage());
//		}
//	}

}
