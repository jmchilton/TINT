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

import edu.umn.msi.gwt.mvc.AppEvent;

public class ProgressAppEvent {
  public static final int PROGRESS_APP_EVENT_BASE = 3000;

  public static class CancelAppEvent extends AppEvent {
    private final String jobId;
    private final String workflowId;
    
    public CancelAppEvent(final String workflowId, final String jobId) {
      super(ProgressAppEvent.PROGRESS_APP_EVENT_BASE + 1);
      this.jobId = jobId;
      this.workflowId = workflowId;
    }

    public String getJobId() {
      return jobId;
    }
    
    public String getWorkflowId() {
      return workflowId;
    }
  }

  public static final AppEvent LOGIN_EVENT = new AppEvent(ProgressAppEvent.PROGRESS_APP_EVENT_BASE + 0);
}
