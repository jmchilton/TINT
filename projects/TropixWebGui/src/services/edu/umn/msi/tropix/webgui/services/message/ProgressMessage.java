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

package edu.umn.msi.tropix.webgui.services.message;


public class ProgressMessage extends Message {
  private static final long serialVersionUID = 1L;
  public static final int JOB_RUNNING = 1;
  public static final int JOB_COMPLETE = 2;
  public static final int JOB_FAILED = 3;
  private static ProgressController progressController;

  private String id;
  private String workflowId;
  private String name;
  private String step = "";
  private String stepStatus = null; // If status isn't null, this overrides
  // percent
  private float stepPercent = 0.0f;
  private int jobStatus = ProgressMessage.JOB_RUNNING;

  public static void setProgressController(final ProgressController progressController) {
    ProgressMessage.progressController = progressController;
  }
  
  public void respond() {
    System.out.println("Responding to message " + this);
    progressController.handleProgressMessage(this);
  }

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

  public float getPercent() {
    return this.stepPercent;
  }

  public void setPercent(final float percent) {
    this.stepPercent = percent;
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

  public float getStepPercent() {
    return this.stepPercent;
  }

  public void setStepPercent(final float stepPercent) {
    this.stepPercent = stepPercent;
  }

  public int getJobStatus() {
    return this.jobStatus;
  }

  public void setJobStatus(final int jobStatus) {
    this.jobStatus = jobStatus;
  }

  public String getWorkflowId() {
    return workflowId;
  }

  public void setWorkflowId(final String workflowId) {
    this.workflowId = workflowId;
  }

  @Override
  public String toString() {
    return "ProgressMessage [id=" + id + ", jobStatus=" + jobStatus + ", name=" + name + ", step=" + step 
           + ", stepPercent=" + stepPercent + ", stepStatus=" + stepStatus + ", workflowId=" + workflowId + "]";
  }

}
