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
 * -----------------------------------------------------------------------------
 * 
 * </PRE>
 */
public class Category extends BobcatBaseObject {
  private java.lang.String name;

  private java.lang.String description;

  //private CategoryFieldAssociation[] fieldIDs;

  public Category() {
  }

  public Category(final java.lang.String id, final int revision, final String created, final String lastModified, final java.lang.String name, final java.lang.String description, final CategoryFieldAssociation[] fieldIDs) {
    super(id, revision, created, lastModified);
    this.name = name;
    this.description = description;
    //this.fieldIDs = fieldIDs;
  }

  /**
   * Gets the name value for this Category.
   * 
   * @return name
   */
  public java.lang.String getName() {
    return this.name;
  }

  /**
   * Sets the name value for this Category.
   * 
   * @param name
   */
  public void setName(final java.lang.String name) {
    this.name = name;
  }

  /**
   * Gets the description value for this Category.
   * 
   * @return description
   */
  public java.lang.String getDescription() {
    return this.description;
  }

  /**
   * Sets the description value for this Category.
   * 
   * @param description
   */
  public void setDescription(final java.lang.String description) {
    this.description = description;
  }

  /**
   * Gets the fieldIDs value for this Category.
   * 
   * @return fieldIDs
   */
  /*
  public CategoryFieldAssociation[] getFieldIDs() {
    return this.fieldIDs;
  }
  */

  /**
   * Sets the fieldIDs value for this Category.
   * 
   * @param fieldIDs
   */
  /*
  public void setFieldIDs(final CategoryFieldAssociation[] fieldIDs) {
    this.fieldIDs = fieldIDs;
  }
  */

}
