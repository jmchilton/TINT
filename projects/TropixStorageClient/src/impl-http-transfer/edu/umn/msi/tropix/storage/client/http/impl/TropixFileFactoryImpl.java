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

package edu.umn.msi.tropix.storage.client.http.impl;

import java.io.InputStream;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.common.io.StreamOutputContextImpl;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.storage.client.ModelStorageData;
import edu.umn.msi.tropix.storage.client.impl.BaseStorageDataImpl;
import edu.umn.msi.tropix.storage.client.impl.TropixFileFactory;
import edu.umn.msi.tropix.storage.core.FileMapper;
import edu.umn.msi.tropix.storage.core.StorageManager;
import edu.umn.msi.tropix.storage.core.StorageManager.UploadCallback;
import edu.umn.msi.tropix.transfer.types.HttpTransferResource;
import edu.umn.msi.tropix.transfer.types.TransferResource;

public class TropixFileFactoryImpl implements TropixFileFactory {
  private StorageManager storageManager;
  private FileMapper fileMapper;

  public ModelStorageData getStorageData(final TropixFile tropixFile, final Credential proxy) {
    return new StorageDataGridImpl(tropixFile, proxy);
  }

  public void setStorageManager(final StorageManager storageManager) {
    this.storageManager = storageManager;
  }

  public void setFileMapper(final FileMapper fileMapper) {
    this.fileMapper = fileMapper;
  }

  class StorageDataGridImpl extends BaseStorageDataImpl {

    StorageDataGridImpl(final TropixFile tropixFile, final Credential credential) {
      super(tropixFile, credential);
    }

    public InputContext getDownloadContext() {
      return storageManager.download(getFileId(), getIdentity());
    }

    public OutputContext getUploadContext() {
      final UploadCallback callback = storageManager.upload(getFileId(), getIdentity());
      return new StreamOutputContextImpl() {
        public void put(final InputStream inputStream) {
          callback.onUpload(inputStream);
        }
      };
    }

    public TransferResource prepareDownloadResource() {
      final InputContext downloadFile = storageManager.download(getFileId(), getIdentity());
      final String url = fileMapper.prepareDownload(downloadFile);
      return new HttpTransferResource(url);
    }

    public TransferResource prepareUploadResource() {
      final UploadCallback uploadCallback = storageManager.upload(getFileId(), getIdentity());
      final String url = fileMapper.prepareUpload(uploadCallback);
      return new HttpTransferResource(url);
    }

  }

}
