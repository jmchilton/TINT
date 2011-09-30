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

import javax.persistence.Entity;

@Entity
public class CreateIdentificationAnalysisDescription extends TropixObjectDescription {
  private String analysisType;
  private String parametersId;
  private String databaseId;
  private String runId;
  private String analysisFileId;  

  public String getAnalysisType() {
    return analysisType;
  }

  @Consumes
  public void setAnalysisType(final String analysisType) {
    this.analysisType = analysisType;
  }
  
  public String getParametersId() {
    return parametersId;
  }
  
  @Consumes
  public void setParametersId(final String parametersId) {
    this.parametersId = parametersId;
  }
  
  public String getDatabaseId() {
    return databaseId;
  }
  
  @Consumes
  public void setDatabaseId(final String databaseId) {
    this.databaseId = databaseId;
  }
  
  public String getRunId() {
    return runId;
  }
  
  @Consumes
  public void setRunId(final String runId) {
    this.runId = runId;
  }

  public String getAnalysisFileId() {
    return analysisFileId;
  }
  
  @Consumes
  public void setAnalysisFileId(final String analysisFileId) {
    this.analysisFileId = analysisFileId;
  }
}
