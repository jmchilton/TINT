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

package edu.umn.msi.tropix.common.jobqueue.description;

import javax.annotation.Nonnull;

public abstract class BaseInProcessJobDescriptionImpl implements InProcessJobDescription {
  private final String jobType;
  private String ticket;

  protected BaseInProcessJobDescriptionImpl(@Nonnull final String jobType) {
    this.jobType = jobType;
  }

  public abstract void execute();

  public String getJobType() {
    return jobType;
  }

  public String getTicket() {
    return ticket;
  }

  public String getExecutionType() {
    return "inprocess";
  }

  public void setTicket(final String ticket) {
    this.ticket = ticket;
  }

}
