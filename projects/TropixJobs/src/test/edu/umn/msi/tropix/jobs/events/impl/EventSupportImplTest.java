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

import org.easymock.EasyMock;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.status.Status;
import edu.umn.msi.tropix.common.message.MessageSource;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.MockObjectCollection;
import edu.umn.msi.tropix.jobs.events.EventBase;
import edu.umn.msi.tropix.jobs.events.GridStatusEvent;
import edu.umn.msi.tropix.jobs.events.ObjectAddedEvent;
import edu.umn.msi.tropix.jobs.events.WorkflowEvent;
import edu.umn.msi.tropix.jobs.events.dispatcher.EventDispatcher;
import edu.umn.msi.tropix.models.User;

public class EventSupportImplTest {
  private final MockObjectCollection collection = new MockObjectCollection();
  private EventSupportImpl support;
  private EventDispatcher eventDispatcher;
  private MessageSource messageSource;
  private EventBase event;
  private EventBaseImpl template = new EventBaseImpl();
  private final String jobId = "53453453";
  private final String jobName = "job name";
  private final String userId = "1231231";
  private final String workflowId = UUID.randomUUID().toString();

  @Test(groups = "unit", timeOut = 1000)
  public void objectAdded() {
    final String destinationId = UUID.randomUUID().toString();
    final String objectId = UUID.randomUUID().toString();
    support.objectAdded(template, objectId, destinationId);

    final ObjectAddedEvent addedEvent = (ObjectAddedEvent) this.event;
    assert addedEvent.getDestinationId().equals(destinationId);
    assert addedEvent.getObjectId().equals(objectId);

  }

  @Test(groups = "unit", timeOut = 1000)
  public void testGridStatus() {
    final Status status = new Status();
    support.gridStatus(template, status);

    final GridStatusEvent statusEvent = (GridStatusEvent) this.event;
    assert statusEvent.getStatus() == status;
  }

  @Test(groups = "unit", timeOut = 1000)
  public void testWorkflow() {
    final WorkflowEventImpl event = new WorkflowEventImpl(template);
    support.workflowUpdate(event, "code", null);

    final WorkflowEvent workflowEvent = (WorkflowEvent) this.event;
    assert workflowEvent.getStep().equals("message");
  }

  @BeforeMethod(groups = "unit")
  public void init() {
    support = new EventSupportImpl();

    eventDispatcher = EasyMock.createMock(EventDispatcher.class);
    messageSource = EasyMock.createMock(MessageSource.class);

    collection.add(eventDispatcher);
    collection.add(messageSource);

    support.setEventDispatcher(eventDispatcher);
    support.setMessageSource(messageSource);
    event = null;
    eventDispatcher.fireEvent(EasyMockUtils.record(new EasyMockUtils.Recorder<EventBase>() {
      public void record(final EventBase object) {
        event = object;
      }
    }));

    EasyMock.expect(messageSource.getMessage("code", null)).andStubReturn("message");

    collection.replay();
    final User user = new User();
    user.setCagridId(userId);

    template = new EventBaseImpl();
    template.setJobId(jobId);
    template.setUserId(userId);
    template.setJobName(jobName);
    template.setWorkflowId(workflowId);
  }

  @AfterMethod(groups = "unit")
  public void verifyDispatched() {
    collection.verifyAndReset();
    verifyEvent();
  }

  private void verifyEvent() {
    assert event != null;
    assert event.getUserId().equals(userId);
    assert event.getJobId().equals(jobId);
    assert event.getJobName().equals(jobName);
    assert event.getWorkflowId().equals(workflowId);
  }

}
