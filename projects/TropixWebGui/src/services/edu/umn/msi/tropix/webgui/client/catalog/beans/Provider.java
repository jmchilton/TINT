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
public class Provider extends BobcatBaseObject {
  private java.lang.String name;

  private java.lang.String contact;

  private java.lang.String address;

  private java.lang.String phone;

  private java.lang.String email;

  private java.lang.String website;

  //private TemplateAssociation[] templates;

  private String catalogId; // Which catalog service this instance came from

  public Provider() {
  }

  public Provider(final java.lang.String id, final int revision, final String created, final String lastModified, final java.lang.String name, final java.lang.String contact, final java.lang.String address, final java.lang.String phone, final java.lang.String email,
      final java.lang.String website, final TemplateAssociation[] templates) {
    super(id, revision, created, lastModified);
    this.name = name;
    this.contact = contact;
    this.address = address;
    this.phone = phone;
    this.email = email;
    this.website = website;
    //this.templates = templates;
  }

  /**
   * Gets the name value for this Provider.
   * 
   * @return name
   */
  public java.lang.String getName() {
    return this.name;
  }

  /**
   * Sets the name value for this Provider.
   * 
   * @param name
   */
  public void setName(final java.lang.String name) {
    this.name = name;
  }

  /**
   * Gets the contact value for this Provider.
   * 
   * @return contact
   */
  public java.lang.String getContact() {
    return this.contact;
  }

  /**
   * Sets the contact value for this Provider.
   * 
   * @param contact
   */
  public void setContact(final java.lang.String contact) {
    this.contact = contact;
  }

  /**
   * Gets the address value for this Provider.
   * 
   * @return address
   */
  public java.lang.String getAddress() {
    return this.address;
  }

  /**
   * Sets the address value for this Provider.
   * 
   * @param address
   */
  public void setAddress(final java.lang.String address) {
    this.address = address;
  }

  /**
   * Gets the phone value for this Provider.
   * 
   * @return phone
   */
  public java.lang.String getPhone() {
    return this.phone;
  }

  /**
   * Sets the phone value for this Provider.
   * 
   * @param phone
   */
  public void setPhone(final java.lang.String phone) {
    this.phone = phone;
  }

  /**
   * Gets the email value for this Provider.
   * 
   * @return email
   */
  public java.lang.String getEmail() {
    return this.email;
  }

  /**
   * Sets the email value for this Provider.
   * 
   * @param email
   */
  public void setEmail(final java.lang.String email) {
    this.email = email;
  }

  /**
   * Gets the website value for this Provider.
   * 
   * @return website
   */
  public java.lang.String getWebsite() {
    return this.website;
  }

  /**
   * Sets the website value for this Provider.
   * 
   * @param website
   */
  public void setWebsite(final java.lang.String website) {
    this.website = website;
  }

  /*
  public TemplateAssociation[] getTemplates() {
    return this.templates;
  }

  public void setTemplates(final TemplateAssociation[] templates) {
    this.templates = templates;
  }
  */

  public String getCatalogId() {
    return this.catalogId;
  }

  public void setCatalogId(final String catalogId) {
    this.catalogId = catalogId;
  }
}
