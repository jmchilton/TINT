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

package edu.umn.msi.tropix.webgui.server.progress;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.webgui.server.messages.MessagePusher;
import edu.umn.msi.tropix.webgui.services.message.ProgressMessage;

public class ProgressTrackerImpl implements ProgressTracker {
  private MessagePusher<? super ProgressMessage> cometPusher;
  private Supplier<ProgressMessage> progressMessageSupplier;
  private String userGridId;

  public void update(final float percent) {
    final ProgressMessage progressMessage = this.progressMessageSupplier.get();
    progressMessage.setPercent(percent);
    this.cometPusher.push(this.userGridId, progressMessage);
  }

  public void update(final String stepStatus) {
    final ProgressMessage progressMessage = this.progressMessageSupplier.get();
    progressMessage.setStepStatus(stepStatus);
    this.cometPusher.push(this.userGridId, progressMessage);
  }

  public void update(final String step, final String stepStatus) {
    final ProgressMessage progressMessage = this.progressMessageSupplier.get();
    progressMessage.setStep(step);
    progressMessage.setStepStatus(stepStatus);
    this.cometPusher.push(this.userGridId, progressMessage);
  }

  public void update(final String step, final float percent) {
    final ProgressMessage progressMessage = this.progressMessageSupplier.get();
    progressMessage.setStep(step);
    progressMessage.setPercent(percent);
    this.cometPusher.push(this.userGridId, progressMessage);
  }

  public void complete() {
    final ProgressMessage message = this.progressMessageSupplier.get();
    message.setStep("Complete");
    message.setPercent(1.0f);
    message.setJobStatus(ProgressMessage.JOB_COMPLETE);
    this.cometPusher.push(this.userGridId, message);
  }

  public void fail() {
    final ProgressMessage message = this.progressMessageSupplier.get();
    message.setStep("Failed");
    message.setPercent(0.0f);
    message.setJobStatus(ProgressMessage.JOB_FAILED);
    this.cometPusher.push(this.userGridId, message);
  }

  public void setCometPusher(final MessagePusher<? super ProgressMessage> cometPusher) {
    this.cometPusher = cometPusher;
  }

  public void setProgressMessageSupplier(final Supplier<ProgressMessage> progressMessageSupplier) {
    this.progressMessageSupplier = progressMessageSupplier;
  }

  public void setUserGridId(final String userGridId) {
    this.userGridId = userGridId;
  }

}
