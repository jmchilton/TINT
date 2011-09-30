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
 */
public class Field extends BobcatBaseObject {
  private java.lang.String name;
  private Attribute attribute;
  //private OntologyConcept[] codes;

  private String type;
  private boolean isMultiSelect;
  private List<FieldValue> fieldValues;
  private String currentValue;
  private List<String> selectedValues = new ArrayList<String>();

  public Field() {
  }

  public Field(final Field other) {
    this(other.getId(), other.getRevision(), other.getCreated(), other.getLastModified(), other.getName(), other.getType(), other.getFieldValues());
  }
    
  public Field(final java.lang.String id, final int revision, final String created, final String lastModified, final java.lang.String name, final String type, final List<FieldValue> fieldValues) {
    super(id, revision, created, lastModified);
    this.name = name;
    //this.codes = codes;
    this.type = type;
    this.fieldValues = fieldValues;
  }

  /**
   * Gets the name value for this Field.
   * 
   * @return name
   */
  public java.lang.String getName() {
    return this.name;
  }

  /**
   * Sets the name value for this Field.
   * 
   * @param name
   */
  public void setName(final java.lang.String name) {
    this.name = name;
  }

  /**
   * Gets the codes value for this Field.
   * 
   * @return codes
   */
  /*
  public OntologyConcept[] getCodes() {
    return this.codes;
  }
  */

  /*
  public void setCodes(final OntologyConcept[] codes) {
    this.codes = codes;
  }

  public OntologyConcept getCodes(final int i) {
    return this.codes[i];
  }

  public void setCodes(final int i, final OntologyConcept value) {
    this.codes[i] = value;
  }
  */

  public String getType() {
    return this.type;
  }

  public void setType(final String type) {
    this.type = type;
  }

  public List<FieldValue> getFieldValues() {
    return this.fieldValues;
  }

  public void setFieldValues(final List<FieldValue> fieldValues) {
    this.fieldValues = fieldValues;
  }

  public Attribute getAttribute() {
    return this.attribute;
  }

  public void setAttribute(final Attribute attribute) {
    this.attribute = attribute;
  }

  public String getCurrentValue() {
    return this.currentValue;
  }

  public void setCurrentValue(final String currentValue) {
    this.currentValue = currentValue;
  }

  public List<String> getSelectedValues() {
    return this.selectedValues;
  }

  public void setSelectedValues(final List<String> selectedValues) {
    this.selectedValues = selectedValues;
  }

  public boolean isMultiSelect() {
    return this.isMultiSelect;
  }

  public void setMultiSelect(final boolean isMultiSelect) {
    this.isMultiSelect = isMultiSelect;
  }

}
