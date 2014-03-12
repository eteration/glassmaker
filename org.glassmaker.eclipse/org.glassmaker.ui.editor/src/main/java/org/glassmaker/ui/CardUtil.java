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
 *     
 *     Derivative Works 
 *     Parts of this program are derived from content from Eclipse Foundation
 *     that are made available under the terms of the Eclipse Public License v1.0.
 *      http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Naci Dai, Eteration A.S. - initial API, implementation and documentation
 *
 * </copyright>
 *
 *******************************************************************************/

package org.glassmaker.ui;

import com.google.api.services.mirror.model.NotificationConfig;
import com.google.api.services.mirror.model.TimelineItem;

public class CardUtil {
	private static final String PAGE_HEADER ="<!DOCTYPE html>\n<html>\n<head>\n<meta charset=\"UTF-8\">\n<title>Glassmaker Preview</title>\n<link rel=\"stylesheet\" href=\"assets/css/glassmaker.css\">\n<link rel=\"stylesheet\" href=\"assets/css/cardtheme.css\">\n<link rel=\"stylesheet\" href=\"assets/css/base_style.css\">\n</head>\n<body>\n	<div class=\"scalable\" style=\"-webkit-transform: scale(1);\">\n<div class=\"scroll leftscroll\" style=\"display: none;\"></div>\n<div class=\"card\" style=\"left: 0px; width: 640px;\">\n";
	private static final String PAGE_FOOTER ="\n</div>\n<div class=\"scroll rightscroll\" style=\"display: none;\"></div>\n</div>	\n</body>\n</html>";
	private static final String START_ARTICLE = "<article style=\"left: 0px; visibility: visible;\">\n			<section>\n				<p class=\"text-x-large\" id=\"map-text-node\" data-text-autosize=\"true\"\n					contenteditable=\"true\" style=\"\">\n				<div id=\"content\">";
	private static final String END_ARTICLE = "</div>\n				</p>\n			</section>\n\n			<footer id=\"map-time-footer\" class=\"has-brand-icon\">\n				<time>\n					just now<img class=\"footer-brand-icon\"\n						src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAIAAAD8GO2jAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyRpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNiAoTWFjaW50b3NoKSIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDo1Q0E0REVDMzg0NDMxMUUyOUZGNjlDREQ3QjM4QTcyRiIgeG1wTU06RG9jdW1lbnRJRD0ieG1wLmRpZDo1Q0E0REVDNDg0NDMxMUUyOUZGNjlDREQ3QjM4QTcyRiI+IDx4bXBNTTpEZXJpdmVkRnJvbSBzdFJlZjppbnN0YW5jZUlEPSJ4bXAuaWlkOjVDQTRERUMxODQ0MzExRTI5RkY2OUNERDdCMzhBNzJGIiBzdFJlZjpkb2N1bWVudElEPSJ4bXAuZGlkOjVDQTRERUMyODQ0MzExRTI5RkY2OUNERDdCMzhBNzJGIi8+IDwvcmRmOkRlc2NyaXB0aW9uPiA8L3JkZjpSREY+IDwveDp4bXBtZXRhPiA8P3hwYWNrZXQgZW5kPSJyIj8+Z0w5lgAAAlVJREFUeNrsVjFs2kAUBftsE4GEgmiQwkQqoXpIB4Z0SBcGlmRJFxZcFSqVDiViqMUEA1mgUioGAoHAku4MKTAws7kzTM2KWgk1EgPY2Jh+HKlSCcWmPW89MXwk7r3Pu//unnV317u1ZaMoZLVaLfjWfD6XZWUyEdHOziPX9rbFnPXj7g6J4uTbd8kkAlVVCbzKLC0AJywmr/8ELper2Wze3HyezWYwmg9/gP4F3Wazlcvlvb3HUEvSlGFokiSx/QPAOj//uL//9P6roiiqOscpUTqdCQaDoij+Mq/2wUQQj78Nh8Pgo0TiHf5DPjl5kUwmochk0p1OBzPB4eHzbDYLRalUur7+RFEUTgKWZQuFAkKo0Wjk8zlAp2kaG4HX661Uqna7vdvt8vx7kkQ0TREEgYfA6XRWq1dut7vf78fjb8BT0DtMqu5dSRg01MVFyefzDQYDjuPG4zGgI0QauYkJI4bK5z8EAoHRaBSJRIbDoYZu9AXUJ0ilUqFQSJblWCx6e/sVdDeOrk8Qi73muJeg+OlpQhC+QO8wOboHa5Tg6OiY53koYPDb7bbW+2bo6wgODp7lcjkoarVavV6ntEWSGxtz9Qa/318sFkHrVqt1dpaFUAOW2rT3PxJ4PB4wlMPhEAQBpNcMBSP/l/FgxYNzeVkBjl6vF42+MmIouFPXBQuWfbK0GTZMp7KiyJA5jAyloswkaZGsGIYB9y3lO7QyzFAL2EXXoLuuMqAeQN8XhiQCxIdP6/p0tdT4b4e8MgpgjMAEZGDzCAAcQcKGyrz4/lOAAQAsM83mQ6mK6AAAAABJRU5ErkJggg==\">\n				</time>\n			</footer>\n\n		</article>";
	private static final String END_ARTICLE2 = "<footer id=\"map-time-footer\" class=\"has-brand-icon\">\n				<time>\n					just now<img class=\"footer-brand-icon\"\n						src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAIAAAD8GO2jAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyRpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNiAoTWFjaW50b3NoKSIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDo1Q0E0REVDMzg0NDMxMUUyOUZGNjlDREQ3QjM4QTcyRiIgeG1wTU06RG9jdW1lbnRJRD0ieG1wLmRpZDo1Q0E0REVDNDg0NDMxMUUyOUZGNjlDREQ3QjM4QTcyRiI+IDx4bXBNTTpEZXJpdmVkRnJvbSBzdFJlZjppbnN0YW5jZUlEPSJ4bXAuaWlkOjVDQTRERUMxODQ0MzExRTI5RkY2OUNERDdCMzhBNzJGIiBzdFJlZjpkb2N1bWVudElEPSJ4bXAuZGlkOjVDQTRERUMyODQ0MzExRTI5RkY2OUNERDdCMzhBNzJGIi8+IDwvcmRmOkRlc2NyaXB0aW9uPiA8L3JkZjpSREY+IDwveDp4bXBtZXRhPiA8P3hwYWNrZXQgZW5kPSJyIj8+Z0w5lgAAAlVJREFUeNrsVjFs2kAUBftsE4GEgmiQwkQqoXpIB4Z0SBcGlmRJFxZcFSqVDiViqMUEA1mgUioGAoHAku4MKTAws7kzTM2KWgk1EgPY2Jh+HKlSCcWmPW89MXwk7r3Pu//unnV317u1ZaMoZLVaLfjWfD6XZWUyEdHOziPX9rbFnPXj7g6J4uTbd8kkAlVVCbzKLC0AJywmr/8ELper2Wze3HyezWYwmg9/gP4F3Wazlcvlvb3HUEvSlGFokiSx/QPAOj//uL//9P6roiiqOscpUTqdCQaDoij+Mq/2wUQQj78Nh8Pgo0TiHf5DPjl5kUwmochk0p1OBzPB4eHzbDYLRalUur7+RFEUTgKWZQuFAkKo0Wjk8zlAp2kaG4HX661Uqna7vdvt8vx7kkQ0TREEgYfA6XRWq1dut7vf78fjb8BT0DtMqu5dSRg01MVFyefzDQYDjuPG4zGgI0QauYkJI4bK5z8EAoHRaBSJRIbDoYZu9AXUJ0ilUqFQSJblWCx6e/sVdDeOrk8Qi73muJeg+OlpQhC+QO8wOboHa5Tg6OiY53koYPDb7bbW+2bo6wgODp7lcjkoarVavV6ntEWSGxtz9Qa/318sFkHrVqt1dpaFUAOW2rT3PxJ4PB4wlMPhEAQBpNcMBSP/l/FgxYNzeVkBjl6vF42+MmIouFPXBQuWfbK0GTZMp7KiyJA5jAyloswkaZGsGIYB9y3lO7QyzFAL2EXXoLuuMqAeQN8XhiQCxIdP6/p0tdT4b4e8MgpgjMAEZGDzCAAcQcKGyrz4/lOAAQAsM83mQ6mK6AAAAABJRU5ErkJggg==\">\n				</time>\n			</footer>\n\n		</article></div><div class=\"scroll rightscroll\"></div>";

	
	public static String wrapFragmantInPage(String htmlFragment){
		
		
		
		int indexOfArticle = htmlFragment.indexOf("<article");
		int indexEndOfArticle = htmlFragment.indexOf("</article>");
		boolean hasArticle = indexOfArticle >= 0;
		if(hasArticle){
			return PAGE_HEADER + htmlFragment.substring(indexOfArticle,indexEndOfArticle) +END_ARTICLE2+ PAGE_FOOTER;
		}
		
		return PAGE_HEADER +START_ARTICLE + htmlFragment +END_ARTICLE + PAGE_FOOTER ;
	}


	public static TimelineItem createTimeline(String htmlFragment) {
		int indexOfArticle = htmlFragment.indexOf("<article");
		boolean hasArticle = indexOfArticle >= 0;
		TimelineItem timelineItem = new TimelineItem();
		if(hasArticle){
			timelineItem.setHtml(htmlFragment);
		}else{
			timelineItem.setText(htmlFragment);
		}
		// Triggers an audible tone when the timeline item is received
		timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));

		return timelineItem;
	}


	
}
