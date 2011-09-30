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
public class OntologyConcept implements IsSerializable {
  private java.lang.String ontology;

  private java.lang.String code;
  private java.lang.String bioPortalURL;

  public OntologyConcept() {
  }

  public OntologyConcept(final java.lang.String ontology, final java.lang.String code, final String bioPortalURL) {
    this.ontology = ontology;
    this.code = code;
    this.bioPortalURL = bioPortalURL;
  }

  public java.lang.String getBioPortalURL() {
    return this.bioPortalURL;
  }

  public void setBioPortalURL(final java.lang.String bioPortalURL) {
    this.bioPortalURL = bioPortalURL;
  }

  /**
   * Gets the ontology value for this OntologyConcept.
   * 
   * @return ontology
   */
  public java.lang.String getOntology() {
    return this.ontology;
  }

  /**
   * Sets the ontology value for this OntologyConcept.
   * 
   * @param ontology
   */
  public void setOntology(final java.lang.String ontology) {
    this.ontology = ontology;
  }

  /**
   * Gets the code value for this OntologyConcept.
   * 
   * @return code
   */
  public java.lang.String getCode() {
    return this.code;
  }

  /**
   * Sets the code value for this OntologyConcept.
   * 
   * @param code
   */
  public void setCode(final java.lang.String code) {
    this.code = code;
  }

}
