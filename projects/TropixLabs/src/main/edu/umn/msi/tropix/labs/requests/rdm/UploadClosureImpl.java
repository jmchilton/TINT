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

package edu.umn.msi.tropix.labs.requests.rdm;

import java.io.File;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.persistence.service.RequestService;
import edu.umn.msi.tropix.storage.client.ModelStorageData;

public class UploadClosureImpl implements Closure<File> {
  // Must be set
  private Supplier<ModelStorageData> modelStorageDataSupplier = null;
  private RequestService requestService = null;

  public void apply(final File file) {
    final String fileName = file.getName();
    final String requestId = getIdFromPath(file);
    final ModelStorageData storageData = modelStorageDataSupplier.get();
    final TropixFile tFile = storageData.getTropixFile();
    tFile.setName(fileName);
    tFile.setCommitted(false);
    requestService.setupRequestFile(requestId, tFile);
    storageData.getUploadContext().put(file);
  }

  private String getIdFromPath(final File file) {
    final String requestId = file.getParentFile().getName();
    return requestId;
  }

  public void setModelStorageDataSupplier(final Supplier<ModelStorageData> modelStorageDataSupplier) {
    this.modelStorageDataSupplier = modelStorageDataSupplier;
  }

  public void setRequestService(final RequestService requestService) {
    this.requestService = requestService;
  }
}
