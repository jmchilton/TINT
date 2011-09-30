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

package edu.umn.msi.tropix.jobs.activities.factories;


import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.events.EventBase;
import edu.umn.msi.tropix.jobs.events.impl.EventBaseImpl;
import edu.umn.msi.tropix.jobs.events.impl.WorkflowEventImpl;

abstract class BaseActivityImpl<T extends ActivityDescription> implements Activity {
  private final T activityDescription;
  private final ActivityContext activityContext;
  
  protected BaseActivityImpl(final T activityDescription, final ActivityContext activityContext) {
    this.activityDescription = activityDescription;
    this.activityContext = activityContext;
  }
  
  protected T getDescription() {
    return activityDescription;
  }
  
  protected Credential getCredential() {
    return activityContext.getCredential();
  }
  
  protected String getUserId() {
    return getCredential().getIdentity();
  }
  
  protected String getWorkflowId() {
    return activityContext.getId();
  }
  
  protected EventBase getEventBase() {
    final EventBaseImpl event = new EventBaseImpl();
    event.setWorkflowId(activityContext.getId());
    final JobDescription jobDescription = activityDescription.getJobDescription();
    if(jobDescription != null) {
      event.setJobId(jobDescription.getId());
      event.setJobName(jobDescription.getName());
    }
    event.setUserId(getUserId());
    return event;
  }
  
  protected WorkflowEventImpl newWorkflowEvent() {
    return new WorkflowEventImpl(getEventBase());
  }
  
}
