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
public class FieldValue extends BobcatBaseObject {
  private java.lang.String fieldID;

  private java.lang.String value;

  //private OntologyConcept[] codes;
  private boolean isSelected;
  private String bioportalURL;

  public FieldValue() {
  }

  public FieldValue(final java.lang.String id, final int revision, final String created, final String lastModified, final java.lang.String fieldID, final java.lang.String value, final OntologyConcept[] codes, final String bioportalURL) {
    super(id, revision, created, lastModified);
    this.fieldID = fieldID;
    this.value = value;
    //this.codes = codes;
    this.bioportalURL = bioportalURL;
  }

  /**
   * Gets the fieldID value for this FieldValue.
   * 
   * @return fieldID
   */
  public java.lang.String getFieldID() {
    return this.fieldID;
  }

  /**
   * Sets the fieldID value for this FieldValue.
   * 
   * @param fieldID
   */
  public void setFieldID(final java.lang.String fieldID) {
    this.fieldID = fieldID;
  }

  /**
   * Gets the value value for this FieldValue.
   * 
   * @return value
   */
  public java.lang.String getValue() {
    return this.value;
  }

  /**
   * Sets the value value for this FieldValue.
   * 
   * @param value
   */
  public void setValue(final java.lang.String value) {
    this.value = value;
  }

  /**
   * Gets the codes value for this FieldValue.
   * 
   * @return codes
   */
  /*
  public OntologyConcept[] getCodes() {
    return this.codes;
  }
  */

  /**
   * Sets the codes value for this FieldValue.
   * 
   * @param codes
   */
  /*
  public void setCodes(final OntologyConcept[] codes) {
    this.codes = codes;
  }
  */

  public boolean isSelected() {
    return this.isSelected;
  }

  public void setSelected(final boolean isSelected) {
    this.isSelected = isSelected;
  }

  public String getBioPortalURL() {
    return this.bioportalURL;
  }

  public void setBioPortalURL(final String bioportalURL) {
    this.bioportalURL = bioportalURL;
  }

}
