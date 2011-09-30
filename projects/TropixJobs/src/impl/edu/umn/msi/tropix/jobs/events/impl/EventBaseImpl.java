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

package edu.umn.msi.tropix.jobs.events.impl;

import edu.umn.msi.tropix.jobs.events.EventBase;

public class EventBaseImpl implements EventBase {
  private String workflowId;
  private String jobId;
  private String jobName;
  private String userId;

  public EventBaseImpl() {
  }

  public EventBaseImpl(final EventBase other) {
    this.workflowId = other.getWorkflowId();
    this.jobId = other.getJobId();
    this.jobName = other.getJobName();
    this.userId = other.getUserId();
  }
  
  public String getWorkflowId() {
    return workflowId;
  }

  public String getJobId() {
    return jobId;
  }

  public String getJobName() {
    return jobName;
  }

  public String getUserId() {
    return userId;
  }

  public void setJobId(final String jobId) {
    this.jobId = jobId;
  }

  public void setJobName(final String jobName) {
    this.jobName = jobName;
  }

  public void setUserId(final String userId) {
    this.userId = userId;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + fieldsToString() + "]";
  }

  protected String fieldsToString() {
    return "jobName=" + jobName + ",jobId=" + jobId + ",workflowId=" + workflowId + ",userId=" + userId;
  }

  public void setWorkflowId(final String workflowId) {
    this.workflowId = workflowId;
  }
}
