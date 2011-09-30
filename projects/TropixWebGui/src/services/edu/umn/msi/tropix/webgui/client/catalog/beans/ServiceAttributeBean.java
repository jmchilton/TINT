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

import java.io.Serializable;
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
 *          <PRE>
 * 
 * -----------------------------------------------------------------------------
 * 
 * </PRE>
 */
public class ServiceAttributeBean implements Serializable {
  private static final long serialVersionUID = 1L;
  private String name;
  private String valueType;
  private boolean isSelected;
  private ArrayList<String> allowedValues = new ArrayList<String>();
  private String attributeType;
  private boolean required = false;

  public static final String ATTRIBUTE = "attribute";
  public static final String INPUT = "input";
  public static final String OUTPUT = "output";
  public static final String TEXT = "text";
  public static final String COMBO = "combo";
  public static final String MULTI_COMBO = "multicombo";

  public ServiceAttributeBean() {

  }

  /*
  public ServiceAttributeBean(final String name, final String valueType, final List<String> allowedValues, final String attributeType, final boolean isRequired) {
    this.name = name;
    this.allowedValues = allowedValues;
    this.attributeType = attributeType;
    this.valueType = valueType;
    this.required = isRequired;
  }

  public ServiceAttributeBean(final String name, final String valueType, final List<String> allowedValues, final String attributeType) {
    this.name = name;
    this.allowedValues = allowedValues;
    this.attributeType = attributeType;
    this.valueType = valueType;
  }
  */

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getValueType() {
    return this.valueType;
  }

  public void setValueType(final String valueType) {
    this.valueType = valueType;
  }

  public List<String> getAllowedValues() {
    return this.allowedValues;
  }

  /*
  public void setAllowedValues(final List<String> allowedValues) {
    this.allowedValues = allowedValues;
  }
  */
  
  public String getAttributeType() {
    return this.attributeType;
  }

  public void setAttributeType(final String attributeType) {
    this.attributeType = attributeType;
  }

  public boolean required() {
    return this.required;
  }

  public void setRequired(final boolean required) {
    this.required = required;
  }

  public boolean isSelected() {
    return this.isSelected;
  }

  public void setSelected(final boolean isSelected) {
    this.isSelected = isSelected;
  }

}
