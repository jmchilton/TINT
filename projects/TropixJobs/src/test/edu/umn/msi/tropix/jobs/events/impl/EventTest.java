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

import java.util.UUID;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.status.Stage;
import edu.umn.msi.tropix.common.jobqueue.status.StageEnumeration;
import edu.umn.msi.tropix.common.jobqueue.status.Status;
import edu.umn.msi.tropix.jobs.events.EventBase;
import edu.umn.msi.tropix.jobs.events.WorkflowEvent.StepPercentStatus;
import edu.umn.msi.tropix.jobs.events.WorkflowEvent.StepUpdateStatus;

public class EventTest {

  @Test(groups = "unit", timeOut = 1000)
  public void baseSetters() {
    final EventBaseImpl baseEvent = getEventBase();
    verify(baseEvent);
  }

  private EventBaseImpl getEventBase() {
    final EventBaseImpl baseEvent = new EventBaseImpl();
    baseEvent.setJobId("123");
    baseEvent.setJobName("Job name.");
    baseEvent.setUserId("john");
    return baseEvent;
  }

  private void verify(final EventBaseImpl baseEvent) {
    assert baseEvent.getJobId().equals("123");
    assert baseEvent.getJobName().equals("Job name.");
    assert baseEvent.getUserId().equals("john");
    baseEvent.toString();
  }

  @Test(groups = "unit", timeOut = 1000)
  public void failureConstructors() {
    final FailureEventImpl event = new FailureEventImpl(getEventBase());
    verify(event);
    verifyEmpty(new FailureEventImpl());
  }

  @Test(groups = "unit", timeOut = 1000)
  public void completionConstructors() {
    final CompletionEventImpl event = new CompletionEventImpl(getEventBase());
    verify(event);
    verifyEmpty(new CompletionEventImpl());
  }

  @Test(groups = "unit", timeOut = 1000)
  public void workflow() {
    final WorkflowEventImpl event = new WorkflowEventImpl(getEventBase());
    final String workflowId = UUID.randomUUID().toString();
    event.setWorkflowId(workflowId);
    assert event.getWorkflowId().equals(workflowId);
    event.setStepStatus(StepUpdateStatus.get("the update"));
    assert event.getStepStatus() instanceof StepUpdateStatus;
    final String step = UUID.randomUUID().toString();
    event.setStep(step);
    assert event.getStep().equals(step);
    event.toString();
    event.setStepStatus(StepPercentStatus.get(0.1f));
    assert event.getStepStatus() instanceof StepPercentStatus;
    event.toString();
  }

  @Test(groups = "unit", timeOut = 1000)
  public void statusConstructors() {
    final Status status = new Status(null, new Stage(StageEnumeration.Absent), null);
    final GridStatusEventImpl event = new GridStatusEventImpl(getEventBase(), status);
    verify(event);
    assert event.getStatus() == status;
  }

  @Test(groups = "unit", timeOut = 1000)
  public void failureOps() {
    final FailureEventImpl event = new FailureEventImpl();
    event.setStackTrace("stack trace...");
    event.setReason("A cow");
    final Throwable t = new NullPointerException("Null.");
    event.setThrowable(t);
    assert event.getReason().equals("A cow");
    assert event.getStackTrace().equals("stack trace...");
    assert event.getThrowable().equals(t);
  }

  private void verifyEmpty(final EventBase event) {
    assert event.getJobId() == null;
    assert event.getJobName() == null;
    assert event.getUserId() == null;
    event.toString();
  }

}
