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

package edu.umn.msi.tropix.webgui.server.download;

import java.io.OutputStream;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.files.PersistentModelStorageDataFactory;
import edu.umn.msi.tropix.storage.client.ModelStorageData;
import edu.umn.msi.tropix.webgui.server.security.UserSession;

@ManagedBean
@FileDownloadHandlerType("simple")
class TropixStorageDownloadHandlerImpl implements FileDownloadHandler {
  private static final Log LOG = LogFactory.getLog(TropixStorageDownloadHandlerImpl.class);
  private final PersistentModelStorageDataFactory storageDataFactory;
  private final UserSession userSession;

  @Inject
  TropixStorageDownloadHandlerImpl(final PersistentModelStorageDataFactory storageDataFactory, final UserSession userSession) {
    this.storageDataFactory = storageDataFactory;
    this.userSession = userSession;
  }

  public void processDownloadRequest(final OutputStream outputStream, final Function<String, String> accessor) {
    final String id = accessor.apply("id");
    Preconditions.checkNotNull(id);
    LOG.debug("Attempting download of TropixFile with id" + id);
    final ModelStorageData storageData = storageDataFactory.getPersistedStorageData(id, userSession.getProxy());
    storageData.getDownloadContext().get(outputStream);
  }

}
