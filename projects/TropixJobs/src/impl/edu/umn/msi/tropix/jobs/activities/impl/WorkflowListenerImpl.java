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

package edu.umn.msi.tropix.jobs.activities.impl;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;

import edu.umn.msi.tropix.common.message.MessageSource;
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.DescriptionMessageCode;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.jobs.events.EventBase;
import edu.umn.msi.tropix.jobs.events.dispatcher.EventDispatcher;
import edu.umn.msi.tropix.jobs.events.impl.CompletionEventImpl;
import edu.umn.msi.tropix.jobs.events.impl.EventBaseImpl;
import edu.umn.msi.tropix.jobs.events.impl.FailureEventImpl;
import edu.umn.msi.tropix.jobs.events.impl.WorkflowEventImpl;

@ManagedBean
class WorkflowListenerImpl implements WorkflowListener {
  private final EventDispatcher eventDispatcher;
  private final MessageSource messageSource;
  
  @Inject
  WorkflowListenerImpl(final EventDispatcher eventDispatcher, @Named("jobsMessageSource") final MessageSource messageSource) {
    this.eventDispatcher = eventDispatcher;
    this.messageSource = messageSource;
  }
  
  private EventBase getEventBase(final ActivityContext activityContext, final JobDescription jobDescription) {
    final EventBaseImpl eventBase = new EventBaseImpl();
    eventBase.setWorkflowId(activityContext.getId());
    eventBase.setJobId(jobDescription.getId());
    eventBase.setJobName(jobDescription.getName());
    eventBase.setUserId(activityContext.getCredential().getIdentity());    
    return eventBase;
  }

  public void jobComplete(final ActivityContext activityContext, final JobDescription jobDescription, final boolean finishedProperly) {
    final EventBase eventBase;
    if(finishedProperly) {
      eventBase = new CompletionEventImpl(getEventBase(activityContext, jobDescription));
    } else {
      eventBase = new FailureEventImpl(getEventBase(activityContext, jobDescription));
    }
    eventDispatcher.fireEvent(eventBase);
  }

  public void workflowComplete(final ActivityContext activityContext, final boolean finishedProperly) {

  }

  public void activityComplete(final ActivityContext activityContext, final ActivityDescription activityDescription, final boolean finishedProperly) {
    
  }

  public void activityStarted(final ActivityContext activityContext, final ActivityDescription activityDescription) { 
    final DescriptionMessageCode codeAnnotation = activityDescription.getClass().getAnnotation(DescriptionMessageCode.class);
    final JobDescription jobDescription = activityDescription.getJobDescription();
    if(codeAnnotation != null && jobDescription != null) {
      final String message = messageSource.getMessage(codeAnnotation.value());
      final WorkflowEventImpl workflowEvent = new WorkflowEventImpl(getEventBase(activityContext, jobDescription));
      workflowEvent.setStep(message);
      eventDispatcher.fireEvent(workflowEvent);
    }
  }
}
