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

import org.globus.exec.generated.JobDescriptionType;

import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.common.jobqueue.utils.JobDescriptionUtils;

public class ExecutableJobDescriptions {
  public static ExecutableJobDescription forJobDescriptionType(final JobDescriptionType jobDescriptionType) {
    return new ExecutableJobDescriptionImpl(jobDescriptionType);
  }

  private static class ExecutableJobDescriptionImpl implements ExecutableJobDescription {
    private final JobDescriptionType jobDescriptionType;

    ExecutableJobDescriptionImpl(final JobDescriptionType jobDescriptionType) {
      Preconditions.checkNotNull(jobDescriptionType);
      this.jobDescriptionType = jobDescriptionType;
    }

    public String getJobType() {
      return JobDescriptionUtils.getJobType(jobDescriptionType);
    }

    public String getTicket() {
      return JobDescriptionUtils.getLocalJobId(jobDescriptionType);
    }

    public void setTicket(final String ticket) {
      JobDescriptionUtils.setLocalJobId(jobDescriptionType, ticket);
    }

    public JobDescriptionType getJobDescriptionType() {
      return jobDescriptionType;
    }

    public String getExecutionType() {
      return JobDescriptionUtils.getExecutionType(jobDescriptionType);
    }
    
    public String toString() {
      return "ExecutableJobDescriptionImpl[ticket=" + getTicket() + ",executionType=" + getExecutionType() + ",jobType=" + getJobType() + ",jobDescriptionType=" + jobDescriptionType + "]";
    }
  }
}
