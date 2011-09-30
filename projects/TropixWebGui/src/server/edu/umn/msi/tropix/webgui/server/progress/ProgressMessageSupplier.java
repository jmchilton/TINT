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

import edu.umn.msi.tropix.webgui.services.message.ProgressMessage;

public class ProgressMessageSupplier implements Supplier<ProgressMessage> {
  private String workflowId = null;
  private String id = null;
  private String name = null;
  private String step = null;
  private String stepStatus = null;
  private Float percent = null;
  private Integer jobStatus = null;

  public String getId() {
    return this.id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getStep() {
    return this.step;
  }

  public void setStep(final String step) {
    this.step = step;
  }

  public String getStepStatus() {
    return this.stepStatus;
  }

  public void setStepStatus(final String stepStatus) {
    this.stepStatus = stepStatus;
  }

  public Float getPercent() {
    return this.percent;
  }

  public void setPercent(final Float percent) {
    this.percent = percent;
  }

  public Integer getJobStatus() {
    return this.jobStatus;
  }

  public void setJobStatus(final Integer jobStatus) {
    this.jobStatus = jobStatus;
  }

  public String getWorkflowId() {
    return workflowId;
  }

  public void setWorkflowId(final String workflowId) {
    this.workflowId = workflowId;
  }

  public ProgressMessage get() {
    final ProgressMessage message = new ProgressMessage();
    if(workflowId != null) {
      message.setWorkflowId(workflowId);
    }
    if(id != null) {
      message.setId(id);
    }
    if(name != null) {
      message.setName(name);
    }
    if(step != null) {
      message.setStep(step);
    }
    if(stepStatus != null) {
      message.setStepStatus(stepStatus);
    }
    if(percent != null) {
      message.setStepPercent(percent);
    }
    if(jobStatus != null) {
      message.setJobStatus(jobStatus);
    }
    return message;
  }

}
