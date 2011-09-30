/*******************************************************************************
 * Copyright 2009 Regents of the University of Minnesota. All rights
 * reserved.
 * Copyright 2009 Mayo Foundation for Medical Education and Research.
 * All rights reserved.
 *
 * This program is made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED INCLUDING, WITHOUT LIMITATION, ANY WARRANTIES OR CONDITIONS
 * OF TITLE, NON-INFRINGEMENT, MERCHANTABILITY OR FITNESS FOR A
 * PARTICULAR PURPOSE.  See the License for the specific language
 * governing permissions and limitations under the License.
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 ******************************************************************************/

package edu.umn.msi.tropix.webgui.client.catalog.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * Copyright: (c) 2004-2007 Mayo Foundation for Medical Education and Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the triple-shield Mayo logo are trademarks and service marks of MFMER.
 * 
 * Except as contained in the copyright notice above, or as used to identify MFMER as the author of this software, the trade names, trademarks, service marks, or product names of the copyright holder shall not be used in advertising, promotion or otherwise in connection with this
 * software without prior written authorization of the copyright holder.
 * 
 * Licensed under the Eclipse Public License, Version 1.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * 
 * 
 * @author Asif Hossain <br>
 * 
 * <br>
 *         Created for Division of Biomedical Informatics, Mayo Foundation Create Date: Sep 4, 2008
 * 
 * @version 1.0
 * 
 *          <p>
 *          Change Log
 * 
 *          <PRE>
 * 
 * -----------------------------------------------------------------------------
 * 
 * </PRE>
 */
public class ServiceBean implements IsSerializable {
  private static final long serialVersionUID = 1L;
  private String catalogId;
  private String id;
  private String code;
  private String name;
  private String revision;
  private String provider;
  private String publisher;
  private Date dateServiceCreated;
  private String serviceDefVersion;
  private Date dateLastModified;
  private String status;
  private String category;
  private String summary;
  private String description;
  private boolean isPopulated;
  private boolean removeFromList;
  private String providerID;

  /* The category of the entry. */
  private String categoryID;

  private TemplateAssociation templateAssoc;
  private List<Field> fieldList;
  private float luceneScore;
  private List<Attribute> attributes = new ArrayList<Attribute>();
  private List<ReferenceBean> references = new ArrayList<ReferenceBean>();
  private List<ServiceInputBean> inputs = new ArrayList<ServiceInputBean>();
  private List<HitInstance> searchHits;

  public String getCode() {
    return this.code;
  }

  public void setCode(final String code) {
    this.code = code;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getProvider() {
    return this.provider;
  }

  public void setProvider(final String provider) {
    this.provider = provider;
  }

  public String getServiceDefVersion() {
    return this.serviceDefVersion;
  }

  public void setServiceDefVersion(final String serviceDefVersion) {
    this.serviceDefVersion = serviceDefVersion;
  }

  public String getStatus() {
    return this.status;
  }

  public void setStatus(final String status) {
    this.status = status;
  }

  public String getCategory() {
    return this.category;
  }

  public void setCategory(final String category) {
    this.category = category;
  }

  public String getSummary() {
    return this.summary;
  }

  public void setSummary(final String summary) {
    this.summary = summary;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public List<ReferenceBean> getReferences() {
    return this.references;
  }

  public void setReferences(final List<ReferenceBean> references) {
    this.references = references;
  }

  public String getId() {
    return this.id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public String getRevision() {
    return this.revision;
  }

  public void setRevision(final String revision) {
    this.revision = revision;
  }

  public Date getDateServiceCreated() {
    return new Date(this.dateServiceCreated.getTime());
  }

  public void setDateServiceCreated(final Date dateServiceCreated) {
    this.dateServiceCreated = new Date(dateServiceCreated.getTime());
  }

  public Date getDateLastModified() {
    return new Date(this.dateLastModified.getTime());
  }

  public void setDateLastModified(final Date dateLastModified) {
    this.dateLastModified = new Date(dateLastModified.getTime());
  }

  public boolean isPopulated() {
    return this.isPopulated;
  }

  public void setPopulated(final boolean isPopulated) {
    this.isPopulated = isPopulated;
  }

  public float getLuceneScore() {
    return this.luceneScore;
  }

  public void setLuceneScore(final float luceneScore) {
    this.luceneScore = luceneScore;
  }

  public String toString() {

    return " id: " + this.id + "code  " + this.code + " name  " + this.name + " revision  " + this.revision + " provider  " + this.provider + " author  " + this.publisher + " dateServiceCreated  " + this.dateServiceCreated + "serviceDefVersion  " + this.serviceDefVersion
        + " dateLastModified " + this.dateLastModified + " status  " + this.status + " category " + this.category + " summary " + this.summary + " description " + this.description + " isPopulated " + this.isPopulated + " luceneScore " + this.luceneScore;
  }

  public boolean isRemoveFromList() {
    return this.removeFromList;
  }

  public void setRemoveFromList(final boolean removeFromList) {
    this.removeFromList = removeFromList;
  }

  public List<ServiceInputBean> getInputs() {
    return this.inputs;
  }

  public void setInputs(final List<ServiceInputBean> inputs) {
    this.inputs = inputs;
  }

  public TemplateAssociation getTemplateAssoc() {
    return this.templateAssoc;
  }

  public void setTemplateAssoc(final TemplateAssociation templateAssoc) {
    this.templateAssoc = templateAssoc;
  }

  public List<Attribute> getAttributes() {
    return this.attributes;
  }

  public void setAttributes(final List<Attribute> attributes) {
    this.attributes = attributes;
  }

  public String getProviderID() {
    return this.providerID;
  }

  public void setProviderID(final String providerID) {
    this.providerID = providerID;
  }

  public String getCategoryID() {
    return this.categoryID;
  }

  public void setCategoryID(final String categoryID) {
    this.categoryID = categoryID;
  }

  public String getPublisher() {
    return this.publisher;
  }

  public void setPublisher(final String publisher) {
    this.publisher = publisher;
  }

  public List<Field> getFieldList() {
    return this.fieldList;
  }

  public void setFieldList(final List<Field> fieldList) {
    this.fieldList = fieldList;
  }

  public List<HitInstance> getSearchHits() {
    return this.searchHits;
  }

  public void setSearchHits(final List<HitInstance> searchHits) {
    this.searchHits = searchHits;
  }

  public String getCatalogId() {
    return this.catalogId;
  }

  public void setCatalogId(final String catalogId) {
    this.catalogId = catalogId;
  }
}
