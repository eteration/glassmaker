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

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import freemarker.template.Configuration;
import freemarker.template.Template;


@Component("freemarkerMerger")
public class FreemarkerMerger implements TemplateMerger, InitializingBean {


	@Autowired
	private ServletContext context;
	


	@Value("${freemarkerpath:'/WEB-INF/cards'}")
	private String freemarkerpath;

	
	@Value("${cardextension:'.card'}")
	private String cardextension;
	

	private Configuration cfg;

	public FreemarkerMerger() {
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		cfg = new Configuration();
		cfg.setOutputEncoding("UTF-8");
		cfg.setServletContextForTemplateLoading(context, freemarkerpath);

	}

	@Override
	public String merge(String templateName, Map<String, ?> data) throws Exception {
		Template template = cfg.getTemplate(templateName+cardextension,"UTF-8");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Writer out = new OutputStreamWriter(bos,Charset.forName("UTF-8"));
		template.process(data, out);
		return bos.toString("UTF-8");
	}

	public String getFreemarkerpath() {
		return freemarkerpath;
	}

	public void setFreemarkerpath(String freemarkerpath) {
		this.freemarkerpath = freemarkerpath;
	}

	public String getCardextension() {
		return cardextension;
	}

	public void setCardextension(String cardextension) {
		this.cardextension = cardextension;
	}

	public ServletContext getContext() {
		return context;
	}

	public void setContext(ServletContext context) {
		this.context = context;
	}
}
