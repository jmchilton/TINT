/********************************************************************************
 * Copyright (c) 2009 Regents of the University of Minnesota
 *
 * This Software was written at the Minnesota Supercomputing Institute
 * http://msi.umn.edu
 *
 * All rights reserved. The following statement of license applies
 * only to this file, and and not to the other files distributed with it
 * or derived therefrom.  This file is made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 *******************************************************************************/

package edu.umn.msi.tropix.jobs.activities.descriptions;

import java.io.Serializable;

import javax.annotation.Nullable;
import javax.persistence.Entity;

@Entity
public class CreateProteomicsRunDescription extends TropixObjectDescription implements Serializable {
  private String mzxmlId;
  @Nullable private String sampleId;
  @Nullable private String sourceId;
  
  public String getMzxmlFileId() {
    return mzxmlId;
  }
  
  @Consumes
  public void setMzxmlFileId(final String mzxmlId) {
    this.mzxmlId = mzxmlId;
  }
  
  public String getSampleId() {
    return sampleId;
  }
  
  @Consumes
  public void setSampleId(final String sampleId) {
    this.sampleId = sampleId;
  }
  
  public String getSourceId() {
    return sourceId;
  }
  
  @Consumes
  public void setSourceId(final String sourceId) {
    this.sourceId = sourceId;
  }
  
}
