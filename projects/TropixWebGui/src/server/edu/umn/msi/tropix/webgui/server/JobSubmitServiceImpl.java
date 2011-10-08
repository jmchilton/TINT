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

package edu.umn.msi.tropix.webgui.server;

import java.util.Set;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.UploadFileDescription;
import edu.umn.msi.tropix.jobs.client.ActivityClient;
import edu.umn.msi.tropix.webgui.server.aop.ServiceMethod;
import edu.umn.msi.tropix.webgui.server.security.UserSession;
import edu.umn.msi.tropix.webgui.server.storage.TempFileStore;
import edu.umn.msi.tropix.webgui.services.jobs.JobSubmitService;

@ManagedBean
public class JobSubmitServiceImpl implements JobSubmitService {
  private final ActivityClient activityClient;
  private final TempFileStore tempFileStore;
  private final UserSession userSession;

  @Inject
  JobSubmitServiceImpl(final ActivityClient activityClient, 
                       final TempFileStore tempFileStore, 
                       final UserSession userSession) {
    this.tempFileStore = tempFileStore;
    this.userSession = userSession;
    this.activityClient = activityClient;
  }

  @ServiceMethod
  public void submit(final Set<ActivityDescription> activityDescriptions) {
    for(final ActivityDescription activityDescription : activityDescriptions) {
      if(activityDescription instanceof UploadFileDescription) {
        final UploadFileDescription uploadDescription = (UploadFileDescription) activityDescription;
        final String fakePath = uploadDescription.getInputFilePath();
        final String realPath = tempFileStore.recoverTempFileInfo(fakePath).getTempLocation().getAbsolutePath();
        uploadDescription.setInputFilePath(realPath);
      }
    }
    activityClient.submit(activityDescriptions, userSession.getProxy());
  }

}
