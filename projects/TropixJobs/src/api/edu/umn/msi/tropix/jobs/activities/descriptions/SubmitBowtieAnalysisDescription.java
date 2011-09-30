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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

@Entity
public class SubmitBowtieAnalysisDescription extends SubmitJobDescription {
  private String inputFromat;
  private String inputType;
  @OneToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
  private StringParameterSet parameterSet;
  @OneToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
  private IdList databaseIds;
  private String indexId;
  
  public String getInputFromat() {
    return inputFromat;
  }
  
  @Consumes
  public void setInputFormat(final String inputFromat) {
    this.inputFromat = inputFromat;
  }

  public String getInputType() {
    return inputType;
  }
  
  @Consumes
  public void setInputType(final String inputType) {
    this.inputType = inputType;
  }

  public StringParameterSet getParameterSet() {
    return parameterSet;
  }
  
  @Consumes  
  public void setParameterSet(final StringParameterSet parameterSet) {
    this.parameterSet = parameterSet;
  }
  
  public IdList getDatabaseIds() {
    return databaseIds;
  }
  
  @Consumes
  public void setDatabaseIds(final IdList databaseIds) {
    this.databaseIds = databaseIds;
  }
  
  public String getIndexId() {
    return indexId;
  }
  
  @Consumes
  public void setIndexId(final String indexId) {
    this.indexId = indexId;
  }
   
}
