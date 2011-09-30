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

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.umn.msi.tropix.common.jobqueue.QueueStage;
import edu.umn.msi.tropix.common.jobqueue.status.QueuePosition;
import edu.umn.msi.tropix.common.jobqueue.status.Status;
import edu.umn.msi.tropix.common.jobqueue.status.StatusUtils;
import edu.umn.msi.tropix.jobs.events.CompletionEvent;
import edu.umn.msi.tropix.jobs.events.EventBase;
import edu.umn.msi.tropix.jobs.events.EventHandler;
import edu.umn.msi.tropix.jobs.events.FailureEvent;
import edu.umn.msi.tropix.jobs.events.GridStatusEvent;
import edu.umn.msi.tropix.jobs.events.ObjectAddedEvent;
import edu.umn.msi.tropix.jobs.events.WorkflowEvent;
import edu.umn.msi.tropix.webgui.services.message.Message;
import edu.umn.msi.tropix.webgui.services.message.ObjectAddedMessage;
import edu.umn.msi.tropix.webgui.services.message.ProgressMessage;

@ManagedBean @Named("jobsEventHandler")
public class MessageJobsEventHandlerImpl implements EventHandler {
  private static final Log LOG = LogFactory.getLog(MessageJobsEventHandlerImpl.class);
  private final MessagePusher<? super Message> messagePusher;

  @Inject
  public MessageJobsEventHandlerImpl(final MessagePusher<? super Message> messagePusher) {
    LOG.info("Constructing an instance of MessageJobEventHandlerImpl.");
    this.messagePusher = messagePusher;
  }
  
  public void handleEvent(final EventBase event) {
    LOG.trace("Handling job update : " + event.toString());
    if(event instanceof ObjectAddedEvent) {
      final ObjectAddedMessage message = new ObjectAddedMessage();
      message.setDestinationId(((ObjectAddedEvent) event).getDestinationId());
      message.setObjectId(((ObjectAddedEvent) event).getObjectId());
      messagePusher.push(event.getUserId(), message);
    } else {    
      final ProgressMessage message = new ProgressMessage();
      message.setWorkflowId(event.getWorkflowId());
      message.setName(event.getJobName());
      message.setId(event.getJobId());
      if(event instanceof CompletionEvent) {
        message.setStep("Complete");
        message.setPercent(1.0f);
        message.setJobStatus(ProgressMessage.JOB_COMPLETE);
      } else if(event instanceof FailureEvent) {
        message.setStep("Failed");
        message.setPercent(0.0f);
        message.setJobStatus(ProgressMessage.JOB_FAILED);
      } else if(event instanceof WorkflowEvent) {
        final WorkflowEvent workflowEvent = (WorkflowEvent) event;
        message.setStep(workflowEvent.getStep());
        message.setStepStatus("Executing");
      } else if(event instanceof GridStatusEvent) {
        final Status status = ((GridStatusEvent) event).getStatus();
        final QueueStage stage = QueueStage.fromStatusUpdateList(status);
        String step = "Queued with status " + stage.toString();
        if(stage.equals(QueueStage.PENDING) && status.getQueuePosition() != null) {
          final QueuePosition pos = status.getQueuePosition();
          step = step + " (" + pos.getValue() + " / " + pos.getQueueSize() + ")";
        }
        message.setStep(step);
        final Double percentComplete = StatusUtils.getPercentComplete(status);
        if(percentComplete != null && stage.equals(QueueStage.RUNNING)) {
          message.setPercent(percentComplete.floatValue());
        } else {
          message.setStepStatus("Executing");
        }
      }
      this.messagePusher.push(event.getUserId(), message);
    }
  }

}
