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
public class CreateScaffoldAnalysisDescription extends TropixObjectDescription {
  @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
  private IdList identificationIds;
  private String outputFileId;
  private String driverFileId;
  private String scaffoldVersion;

  public String getDriverFileId() {
    return driverFileId;
  }

  @Consumes
  public void setDriverFileId(final String driverFileId) {
    this.driverFileId = driverFileId;
  }

  public IdList getIdentificationIds() {
    return identificationIds;
  }

  @Consumes
  public void setIdentificationIds(final IdList identificationIds) {
    this.identificationIds = identificationIds;
  }

  public String getOutputFileId() {
    return outputFileId;
  }

  @Consumes
  public void setOutputFileId(final String outputFileId) {
    this.outputFileId = outputFileId;
  }

  public String getScaffoldVersion() {
    return scaffoldVersion;
  }

  @Consumes
  public void setScaffoldVersion(final String scaffoldVersion) {
    this.scaffoldVersion = scaffoldVersion;
  }

}
