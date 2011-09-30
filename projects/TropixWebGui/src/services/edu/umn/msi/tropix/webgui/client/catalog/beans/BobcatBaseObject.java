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
public class BobcatBaseObject implements IsSerializable {
  private java.lang.String id;

  private int revision;

  private String created;

  private String lastModified;

  public BobcatBaseObject() {
  }

  public BobcatBaseObject(final java.lang.String id, final int revision, final String created, final String lastModified) {
    this.id = id;
    this.revision = revision;
    this.created = created;
    this.lastModified = lastModified;
  }

  /**
   * Gets the id value for this BobcatBaseObject.
   * 
   * @return id
   */
  public java.lang.String getId() {
    return this.id;
  }

  /**
   * Sets the id value for this BobcatBaseObject.
   * 
   * @param id
   */
  public void setId(final java.lang.String id) {
    this.id = id;
  }

  /**
   * Gets the revision value for this BobcatBaseObject.
   * 
   * @return revision
   */
  public int getRevision() {
    return this.revision;
  }

  /**
   * Sets the revision value for this BobcatBaseObject.
   * 
   * @param revision
   */
  public void setRevision(final int revision) {
    this.revision = revision;
  }

  /**
   * Gets the created value for this BobcatBaseObject.
   * 
   * @return created
   */

  public String getLastModified() {
    return this.lastModified;
  }

  public String getCreated() {
    return this.created;
  }

  public void setLastModified(final String lastModified) {
    this.lastModified = lastModified;
  }

  public void setCreated(final String created) {
    this.created = created;
  }

}
