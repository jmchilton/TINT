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

import java.util.List;
import java.util.UUID;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.message.MessageSource;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.DescriptionMessageCode;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.jobs.events.CompletionEvent;
import edu.umn.msi.tropix.jobs.events.EventBase;
import edu.umn.msi.tropix.jobs.events.FailureEvent;
import edu.umn.msi.tropix.jobs.events.WorkflowEvent;
import edu.umn.msi.tropix.jobs.events.dispatcher.EventDispatcher;

public class WorkflowListenerImplTest {
  class MockEventDispatcherImpl implements EventDispatcher {
    private List<EventBase> events = Lists.newArrayList();

    public void fireEvent(final EventBase event) {
      events.add(event);
    }

  }

  private ActivityContext context;
  private JobDescription jobDescription;

  @BeforeMethod(groups = "unit")
  public void init() {
    context = new ActivityContext();
    context.setCredentialStr(Credentials.getMock().toString());
    jobDescription = new JobDescription();
    jobDescription.setName(UUID.randomUUID().toString());
    jobDescription.setId(UUID.randomUUID().toString());
  }

  @DescriptionMessageCode("test_code")
  static class ActivityDescriptionWithMessage extends ActivityDescription {
  }

  static class ActivityDescriptionWithoutMessage extends ActivityDescription {
  }

  @Test(groups = "unit", timeOut = 1000)
  public void testResponseToActivityStart() {
    final MockEventDispatcherImpl dispatcher = new MockEventDispatcherImpl();
    final MessageSource messageSource = EasyMock.createMock(MessageSource.class);
    WorkflowListenerImpl workflowListener = new WorkflowListenerImpl(dispatcher, messageSource);
    final ActivityDescriptionWithMessage activity = new ActivityDescriptionWithMessage();
    activity.setJobDescription(jobDescription);
    EasyMock.expect(messageSource.getMessage("test_code")).andReturn("Test!");
    EasyMock.replay(messageSource);
    workflowListener.activityStarted(context, activity);
    EasyMock.verify(messageSource);
    assert ((WorkflowEvent) dispatcher.events.get(0)).getStep().equals("Test!");
  }

  @Test(groups = "unit", timeOut = 1000)
  public void testResponseToActivityStartNoMessage() {
    final MockEventDispatcherImpl dispatcher = new MockEventDispatcherImpl();
    final MessageSource messageSource = EasyMock.createMock(MessageSource.class);
    WorkflowListenerImpl workflowListener = new WorkflowListenerImpl(dispatcher, messageSource);
    // Both of these activities should not produce events, because there is no job description and because
    // they are not annotated with message code (respectivly)
    final ActivityDescriptionWithMessage activityWithoutJobDescription = new ActivityDescriptionWithMessage();
    final ActivityDescriptionWithoutMessage activityWithoutMessage = new ActivityDescriptionWithoutMessage();
    activityWithoutMessage.setJobDescription(jobDescription);
    EasyMock.replay(messageSource);
    workflowListener.activityStarted(context, activityWithoutJobDescription);
    workflowListener.activityStarted(context, activityWithoutMessage);
    EasyMock.verify(messageSource);
    assert dispatcher.events.isEmpty();
  }

  private void verifyEvent(final EventBase eventBase) {
    assert eventBase.getJobId().equals(jobDescription.getId());
    assert eventBase.getJobName().equals(jobDescription.getName());
    assert eventBase.getUserId().equals(context.getCredential().getIdentity());
  }

  @Test(groups = "unit", timeOut = 1000)
  public void testResponseToCompleteAndFailure() {
    final MockEventDispatcherImpl dispatcher = new MockEventDispatcherImpl();
    final MessageSource messageSource = EasyMock.createMock(MessageSource.class);
    WorkflowListenerImpl workflowListener = new WorkflowListenerImpl(dispatcher, messageSource);
    workflowListener.jobComplete(context, jobDescription, true);
    final CompletionEvent completeEvent = (CompletionEvent) dispatcher.events.get(0);
    verifyEvent(completeEvent);
    workflowListener.jobComplete(context, jobDescription, false);
    final FailureEvent failureEvent = (FailureEvent) dispatcher.events.get(1);
    verifyEvent(failureEvent);

    workflowListener.workflowComplete(context, true);
    workflowListener.workflowComplete(context, false);
  }

}
