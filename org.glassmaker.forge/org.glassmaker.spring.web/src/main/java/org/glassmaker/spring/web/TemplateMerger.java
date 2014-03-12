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

import java.util.Map;

public interface TemplateMerger
{ 
    /**
     * Merge a group of data objects into a template, specified by name. 
     * @param templateName name of an object (e.g. file) which contains the template to be filled
     * @return Merged data
     * @throws MergeException if the merge fails for any reason (e.g. IOException reading a file).
     * Implementations should probably instead throw RuntimeExceptions for cases which are
     * caused by coding bugs, such as specifying a particular template but failing to provide
     * a data object required by that template.
     */
    String merge(String templateName, Map<String, ?> dataObjects) throws Exception; 

}