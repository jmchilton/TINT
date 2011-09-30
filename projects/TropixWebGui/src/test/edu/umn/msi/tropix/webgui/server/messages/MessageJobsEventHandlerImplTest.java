/*******************************************************************************
 * Copyright 2009 Regents of the University of Minnesota. All rights
 * reserved.
 * Copyright 2009 Mayo Foundation for Medical Education and Research.
 * All rights reserved.
 *
 * This program is made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED INCLUDING, WITHOUT LIMITATION, ANY WARRANTIES OR CONDITIONS
 * OF TITLE, NON-INFRINGEMENT, MERCHANTABILITY OR FITNESS FOR A
 * PARTICULAR PURPOSE.  See the License for the specific language
 * governing permissions and limitations under the License.
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 ******************************************************************************/

package edu.umn.msi.tropix.webgui.server.messages;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.status.PercentComplete;
import edu.umn.msi.tropix.common.jobqueue.status.QueuePosition;
import edu.umn.msi.tropix.common.jobqueue.status.Stage;
import edu.umn.msi.tropix.common.jobqueue.status.StageEnumeration;
import edu.umn.msi.tropix.common.jobqueue.status.Status;
import edu.umn.msi.tropix.common.jobqueue.status.StatusEntry;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.jobs.events.CompletionEvent;
import edu.umn.msi.tropix.jobs.events.EventBase;
import edu.umn.msi.tropix.jobs.events.FailureEvent;
import edu.umn.msi.tropix.jobs.events.GridStatusEvent;
import edu.umn.msi.tropix.jobs.events.ObjectAddedEvent;
import edu.umn.msi.tropix.jobs.events.WorkflowEvent;
import edu.umn.msi.tropix.webgui.services.message.Message;
import edu.umn.msi.tropix.webgui.services.message.ObjectAddedMessage;
import edu.umn.msi.tropix.webgui.services.message.ProgressMessage;

public class MessageJobsEventHandlerImplTest {
  private MessagePusher<Message> messagePusher;
  private MessageJobsEventHandlerImpl handler;

  private final String jobId = "job1";
  private final String jobName = "jobName1";
  private final String userId = "userXX1";
  private final String workflowId = "1";

  @BeforeMethod(groups = "unit")
  public void init() {
    messagePusher = EasyMock.createMock(MessagePusher.class);
    handler = new MessageJobsEventHandlerImpl(messagePusher);
  }

  private Message verifyEvent(final EventBase event) {
    final Capture<Message> messageCapture = EasyMockUtils.newCapture();
    messagePusher.push(EasyMock.eq(userId), EasyMock.capture(messageCapture));
    EasyMock.replay(messagePusher);
    handler.handleEvent(event);
    EasyMockUtils.verifyAndReset(messagePusher);
    return messageCapture.getValue();
  }

  private ProgressMessage verifyProgressEvent(final EventBase event) {
    final ProgressMessage message = (ProgressMessage) verifyEvent(event);
    assert message.getWorkflowId().equals(workflowId);
    assert message.getId().equals(jobId);
    return message;
  }

  private class EventBaseImpl implements EventBase {
    public String getJobId() {
      return jobId;
    }

    public String getJobName() {
      return jobName;
    }

    public String getUserId() {
      return userId;
    }

    public String getWorkflowId() {
      return workflowId;
    }
  }

  private class CompletionEventImpl extends EventBaseImpl implements CompletionEvent {
  }

  private class WorkflowEventImpl extends EventBaseImpl implements WorkflowEvent {

    public String getStep() {
      return "thestep";
    }

  }

  private class FailureEventImpl extends EventBaseImpl implements FailureEvent {

    public String getReason() {
      return null;
    }

    public String getStackTrace() {
      return null;
    }

    public Throwable getThrowable() {
      return null;
    }
  }

  private class ObjectAddedEventImpl extends EventBaseImpl implements ObjectAddedEvent {

    public String getDestinationId() {
      return "destination123";
    }

    public String getObjectId() {
      return "object456";
    }

  }

  private class GridEventImpl extends EventBaseImpl implements GridStatusEvent {
    private final Status status;

    GridEventImpl(final Status status) {
      this.status = status;
    }

    public Status getStatus() {
      return status;
    }

  }

  @Test(groups = "unit")
  public void testWorkflowEvent() {
    final ProgressMessage message = verifyProgressEvent(new WorkflowEventImpl());
    assert message.getStep().equals("thestep");
  }

  @Test(groups = "unit")
  public void testObjectAddedEvent() {
    final ObjectAddedMessage message = (ObjectAddedMessage) verifyEvent(new ObjectAddedEventImpl());
    assert message.getDestinationId().equals("destination123");
    assert message.getObjectId().equals("object456");
  }

  @Test(groups = "unit")
  public void testGridEvent() {
    final Status status = new Status();
    QueuePosition pos = new QueuePosition();
    pos.setQueueSize(12);
    pos.setValue(1);
    Stage stage = new Stage(StageEnumeration.Pending);
    status.setStage(stage);
    status.setQueuePosition(pos);

    ProgressMessage progressMessage = verifyProgressEvent(new GridEventImpl(status));
    assert progressMessage.getPercent() == 0.0f;

    status.setStage(new Stage(StageEnumeration.Running));
    status.setStatusEntry(new StatusEntry[] {new PercentComplete(.45f)});
    progressMessage = verifyProgressEvent(new GridEventImpl(status));
    assert progressMessage.getPercent() == .45f;

  }

  @Test(groups = "unit")
  public void testCompletionEvent() {
    final ProgressMessage message = verifyProgressEvent(new CompletionEventImpl());
    assert message.getJobStatus() == ProgressMessage.JOB_COMPLETE;
    assert message.getPercent() == 1.0f;
  }

  @Test(groups = "unit")
  public void testFailureEvent() {
    final ProgressMessage message = verifyProgressEvent(new FailureEventImpl());
    assert message.getJobStatus() == ProgressMessage.JOB_FAILED;
    assert message.getPercent() == 0.0f;

  }

}
