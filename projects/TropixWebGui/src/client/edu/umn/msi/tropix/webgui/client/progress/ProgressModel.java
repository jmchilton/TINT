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

package edu.umn.msi.tropix.webgui.client.progress;

import edu.umn.msi.gwt.mvc.Model;
import edu.umn.msi.tropix.webgui.services.message.ProgressMessage;

public class ProgressModel extends Model {
  private static final long serialVersionUID = 1L;

  public void update(final String workflowId, final String id, final String step, final String stepStatus, final float percent, final int jobStatus) {
    this.properties.put("workflowId", workflowId);
    this.properties.put("id", id);
    this.properties.put("step", step);
    this.properties.put("stepStatus", stepStatus);
    this.properties.put("stepPercent", new Float(percent));
    this.properties.put("jobStatus", Integer.valueOf(jobStatus));
    this.fireEvent(Model.Update, this);
  }

  public boolean isCompleted() {
    final int status = ((Integer) this.get("jobStatus")).intValue();
    return ProgressMessage.JOB_COMPLETE == status || ProgressMessage.JOB_FAILED == status;
  }

  public String toString() {
    return this.properties.get("id").toString();
  }
}
