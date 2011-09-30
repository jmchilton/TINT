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

import java.util.List;

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
public class Attribute extends BobcatBaseObject {
  private java.lang.String entryID;
  private String name;

  private java.lang.String fieldID;

  private List<FieldValue> values;
  private String bioportalURL;

  public Attribute() {
  }

  public Attribute(final java.lang.String id, final int revision, final String created, final String lastModified, final String entryID, final String fieldID, final List<FieldValue> values, final String name, final String bioportalURL) {
    super(id, revision, created, lastModified);
    this.entryID = entryID;
    this.fieldID = fieldID;
    this.values = values;
    this.name = name;
    this.bioportalURL = bioportalURL;
  }

  /**
   * Gets the entryID value for this Attribute.
   * 
   * @return entryID
   */
  public java.lang.String getEntryID() {
    return this.entryID;
  }

  /**
   * Sets the entryID value for this Attribute.
   * 
   * @param entryID
   */
  public void setEntryID(final java.lang.String entryID) {
    this.entryID = entryID;
  }

  /**
   * Gets the fieldID value for this Attribute.
   * 
   * @return fieldID
   */
  public java.lang.String getFieldID() {
    return this.fieldID;
  }

  /**
   * Sets the fieldID value for this Attribute.
   * 
   * @param fieldID
   */
  public void setFieldID(final java.lang.String fieldID) {
    this.fieldID = fieldID;
  }

  /**
   * Gets the type value for this Attribute.
   * 
   * @return type
   */

  /**
   * Gets the value value for this Attribute.
   * 
   * @return value
   */
  public List<FieldValue> getValues() {
    return this.values;
  }

  /**
   * Sets the value value for this Attribute.
   * 
   * @param value
   */
  public void setValues(final List<FieldValue> values) {
    this.values = values;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getBioPortalURL() {
    return this.bioportalURL;
  }

  public void setBioPortalURL(final String bioportalURL) {
    this.bioportalURL = bioportalURL;
  }

}
