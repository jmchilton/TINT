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

import edu.umn.msi.tropix.models.proteomics.IdentificationType;

@Entity
public class SubmitIdentificationAnalysisDescription extends SubmitJobDescription {
  private String databaseId;
  private String runId;
  private String parametersId;
  private String parameterType;

  public String getParameterType() {
    return parameterType;
  }

  @Consumes
  public void setParameterType(final String identificationType) {
    this.parameterType = identificationType;
  }

  public IdentificationType getType() {
    return IdentificationType.fromParameterType(parameterType);
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

  public String getParametersId() {
    return parametersId;
  }

  @Consumes
  public void setParametersId(final String parametersId) {
    this.parametersId = parametersId;
  }

}
