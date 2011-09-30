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
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.ActivityDirector;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.ConsumesStorageServiceUrl;
import edu.umn.msi.tropix.jobs.activities.descriptions.UploadFileDescription;
import edu.umn.msi.tropix.webgui.server.aop.ServiceMethod;
import edu.umn.msi.tropix.webgui.server.security.UserSession;
import edu.umn.msi.tropix.webgui.server.storage.TempFileStore;
import edu.umn.msi.tropix.webgui.services.jobs.JobSubmitService;

@ManagedBean
public class JobSubmitServiceImpl implements JobSubmitService {
  private static final Log LOG = LogFactory.getLog(JobSubmitServiceImpl.class);
  private final ActivityDirector activityDirector;
  private final TempFileStore tempFileStore;
  private final Supplier<String> storageServiceUrlSupplier;
  private final UserSession userSession;

  @Inject
  JobSubmitServiceImpl(final ActivityDirector activityDirector, final TempFileStore tempFileStore, @Named("storageServiceUrlSupplier") final Supplier<String> storageServiceUrl, final UserSession userSession) {
    this.activityDirector = activityDirector;
    this.tempFileStore = tempFileStore;
    this.storageServiceUrlSupplier = storageServiceUrl;
    this.userSession = userSession;
  }

  @ServiceMethod
  public void submit(final Set<ActivityDescription> activityDescriptions) {
    for(final ActivityDescription activityDescription : activityDescriptions) {
      if(activityDescription instanceof ConsumesStorageServiceUrl) {
        final ConsumesStorageServiceUrl consumer = (ConsumesStorageServiceUrl) activityDescription;
        consumer.setStorageServiceUrl(storageServiceUrlSupplier.get());
      }
      if(activityDescription instanceof UploadFileDescription) {
        final UploadFileDescription uploadDescription = (UploadFileDescription) activityDescription;
        final String fakePath = uploadDescription.getInputFilePath();
        final String realPath = tempFileStore.recoverTempFileInfo(fakePath).getTempLocation().getAbsolutePath();
        uploadDescription.setInputFilePath(realPath);
      }
    }
    final ActivityContext activityContext = new ActivityContext();
    activityContext.setCredentialStr(userSession.getProxy().toString());
    activityContext.setActivityDescription(activityDescriptions);
    LOG.debug("Executing activity context " + activityContext);
    activityDirector.execute(activityContext);
  }

}
