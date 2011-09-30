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
public class HitInstance implements IsSerializable {
  public static final java.lang.String NAME = "NAME";
  public static final java.lang.String DESCRIPTION = "DESCRIPTION";
  public static final java.lang.String PUBLISHER = "PUBLISHER";
  public static final java.lang.String STATUS = "STATUS";
  public static final java.lang.String PROVIDER_ID = "PROVIDER_ID";
  public static final java.lang.String CATEGORY_ID = "CATEGORY_ID";
  public static final java.lang.String ATTRIBUTE = "ATTRIBUTE";
  public static final java.lang.String REFERENCE = "REFERENCE";

  private float score;

  private String text;

  private List<Highlight> highlights;

  private String source;

  private String attributeID;

  public HitInstance() {
  }

  public HitInstance(final float score, final java.lang.String text, final List<Highlight> highlights, final String source, final java.lang.String attributeID) {
    this.score = score;
    this.text = text;
    this.highlights = highlights;
    this.source = source;
    this.attributeID = attributeID;
  }

  /**
   * Gets the score value for this HitInstance.
   * 
   * @return score
   */
  public float getScore() {
    return this.score;
  }

  /**
   * Sets the score value for this HitInstance.
   * 
   * @param score
   */
  public void setScore(final float score) {
    this.score = score;
  }

  /**
   * Gets the text value for this HitInstance.
   * 
   * @return text
   */
  public java.lang.String getText() {
    return this.text;
  }

  /**
   * Sets the text value for this HitInstance.
   * 
   * @param text
   */
  public void setText(final java.lang.String text) {
    this.text = text;
  }

  /**
   * Gets the highlights value for this HitInstance.
   * 
   * @return highlights
   */
  public List<Highlight> getHighlights() {
    return this.highlights;
  }

  /**
   * Sets the highlights value for this HitInstance.
   * 
   * @param highlights
   */
  public void setHighlights(final List<Highlight> highlights) {
    this.highlights = highlights;
  }

  public Highlight getHighlights(final int i) {
    return this.highlights.get(i);
  }

  public void setHighlights(final int i, final Highlight value) {
    this.highlights.set(i, value);
  }

  /**
   * Gets the source value for this HitInstance.
   * 
   * @return source
   */
  public String getSource() {
    return this.source;
  }

  /**
   * Sets the source value for this HitInstance.
   * 
   * @param source
   */
  public void setSource(final String source) {
    this.source = source;
  }

  /**
   * Gets the attributeID value for this HitInstance.
   * 
   * @return attributeID
   */
  public java.lang.String getAttributeID() {
    return this.attributeID;
  }

  /**
   * Sets the attributeID value for this HitInstance.
   * 
   * @param attributeID
   */
  public void setAttributeID(final java.lang.String attributeID) {
    this.attributeID = attributeID;
  }

}
