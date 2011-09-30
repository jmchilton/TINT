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
public class CustomQueryBean implements IsSerializable {
  private String catalogId;
  private String freeText;
  private String author;
  private String categoryID;
  private String proivderID;
  private String status;

  public String getCatalogId() {
    return this.catalogId;
  }

  public void setCatalogId(final String catalogId) {
    this.catalogId = catalogId;
  }

  private List<String> valueIDs = new ArrayList<String>();
  private boolean isIntersect;

  public boolean isIntersect() {
    return this.isIntersect;
  }

  public void setIntersect(final boolean isIntersect) {
    this.isIntersect = isIntersect;
  }

  public String getFreeText() {
    return this.freeText;
  }

  public void setFreeText(final String freeText) {
    this.freeText = freeText;
  }

  public String getAuthor() {
    return this.author;
  }

  public void setAuthor(final String author) {
    this.author = author;
  }

  public String getCategoryID() {
    return this.categoryID;
  }

  public void setCategoryID(final String categoryID) {
    this.categoryID = categoryID;
  }

  public String getProivderID() {
    return this.proivderID;
  }

  public void setProivderID(final String proivderID) {
    this.proivderID = proivderID;
  }

  public String getStatus() {
    return this.status;
  }

  public void setStatus(final String status) {
    this.status = status;
  }

  public List<String> getValueIDs() {
    return this.valueIDs;
  }

  public void setValueIDs(final List<String> valueIDs) {
    this.valueIDs = valueIDs;
  }

}
