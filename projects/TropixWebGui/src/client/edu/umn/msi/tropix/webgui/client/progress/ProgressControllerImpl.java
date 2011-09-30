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

import com.google.common.base.Supplier;
import com.google.inject.Inject;
import com.smartgwt.client.widgets.tab.Tab;

import edu.umn.msi.gwt.mvc.AppEvent;
import edu.umn.msi.gwt.mvc.Controller;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.mediators.LoginMediator;
import edu.umn.msi.tropix.webgui.client.modules.RequiresModule;
import edu.umn.msi.tropix.webgui.client.progress.ProgressAppEvent.CancelAppEvent;
import edu.umn.msi.tropix.webgui.services.jobs.JobCancelService;
import edu.umn.msi.tropix.webgui.services.message.ProgressController;
import edu.umn.msi.tropix.webgui.services.message.ProgressMessage;
import edu.umn.msi.tropix.webgui.services.session.LoginListener;
import edu.umn.msi.tropix.webgui.services.session.Module;

public class ProgressControllerImpl extends Controller implements LoginListener, Supplier<Tab>, RequiresModule, ProgressController {
  private ProgressCollectionView progressCollectionView;
  private ProgressCollectionModel progressCollectionModel = new ProgressCollectionModel();

  @Inject
  public ProgressControllerImpl(final LoginMediator loginMediator) {
    this.progressCollectionModel = new ProgressCollectionModel();
    this.progressCollectionView = new ProgressCollectionView(this, this.progressCollectionModel);
    loginMediator.addListener(this);
    ProgressMessage.setProgressController(this);
  }

  public Tab get() {
    return this.progressCollectionView.get();
  }

  /* (non-Javadoc)
   * @see edu.umn.msi.tropix.webgui.client.progress.ProgressController#handleProgressMessage(edu.umn.msi.tropix.webgui.services.message.ProgressMessage)
   */
  public void handleProgressMessage(final ProgressMessage progressMessage) {
    final String id = progressMessage.getId();
    ProgressModel progressModel = this.progressCollectionModel.getProgressModel(id);
    if(progressModel != null) {
      progressModel.update(progressMessage.getWorkflowId(), progressMessage.getId(), progressMessage.getStep(), progressMessage.getStepStatus(), progressMessage.getPercent(), progressMessage.getJobStatus());
    } else {
      progressModel = new ProgressModel();
      progressModel.set("workflowId", progressMessage.getWorkflowId());
      progressModel.set("id", progressMessage.getId());
      progressModel.set("name", progressMessage.getName());
      progressModel.set("step", progressMessage.getStep());
      progressModel.set("stepStaus", progressMessage.getStepStatus());
      progressModel.set("stepPercent", new Float(progressMessage.getPercent()));
      progressModel.set("jobStatus", Integer.valueOf(progressMessage.getJobStatus()));
      this.progressCollectionModel.add(progressModel);
    }
  }

  public void handleEvent(final AppEvent event) {
    if(event instanceof CancelAppEvent) {
      final CancelAppEvent cancelEvent = (CancelAppEvent) event;
      JobCancelService.Util.getInstance().cancel(cancelEvent.getWorkflowId(), cancelEvent.getJobId(), new AsyncCallbackImpl<Void>());
    }
  }

  public void loginEvent() {
    Controller.forwardToView(this.progressCollectionView, ProgressAppEvent.LOGIN_EVENT);
  }

  public void logoutEvent() {
    this.progressCollectionModel.clear();
  }

  public void loginFailedEvent() {
  }

  public Module requiresModule() {
    return Module.USER;
  }

}
