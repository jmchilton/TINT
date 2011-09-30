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

import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

@Entity
public class SubmitITraqQuantitationAnalysisDescription extends SubmitJobDescription {
  @OneToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
  private IdList runIdList;
  private String reportFileId;
  private String quantificationType;
  @Nullable private String trainingId;

  public IdList getRunIdList() {
    return runIdList;
  }

  @Consumes
  public void setRunIdList(final IdList runIdList) {
    this.runIdList = runIdList;
  }
  
  public String getReportFileId() {
    return reportFileId;
  }
  
  @Consumes
  public void setReportFileId(final String reportFileId) {
    this.reportFileId = reportFileId;
  }
  
  public String getQuantificationType() {
    return quantificationType;
  }
  
  @Consumes
  public void setQuantificationType(final String quantificationType) {
    this.quantificationType = quantificationType;
  }

  public String getTrainingId() {
    return trainingId;
  }

  @Consumes
  public void setTrainingId(final String trainingId) {
    this.trainingId = trainingId;
  }
  
}
