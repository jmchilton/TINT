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

package edu.umn.msi.tropix.storage.client.impl;

import java.rmi.RemoteException;

import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.io.transfer.TransferContextFactory;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.storage.cagrid.client.TropixStorageInterfacesClient;
import edu.umn.msi.tropix.storage.client.ModelStorageData;
import edu.umn.msi.tropix.storage.service.TropixStorageService;
import edu.umn.msi.tropix.transfer.types.CaGridTransferResource;
import edu.umn.msi.tropix.transfer.types.TransferResource;

public class TropixFileFactoryGridImpl implements TropixFileFactory {
  private TransferContextFactory<Credential> factory;

  public ModelStorageData getStorageData(final TropixFile tropixFile, final Credential proxy) {
    TropixStorageService client;
    try {
      client = new TropixStorageInterfacesClient(tropixFile.getStorageServiceUrl(), proxy.getGlobusCredential());
    } catch(final Exception e) {
      throw new IllegalStateException("Failed to produce storage service client", e);
    }
    final StorageDataGridImpl storageDataImpl = new StorageDataGridImpl(tropixFile, client, proxy);
    return storageDataImpl;
  }

  final class StorageDataGridImpl extends BaseStorageDataImpl {
    private final TropixStorageService client;

    private StorageDataGridImpl(final TropixFile tropixFile, final TropixStorageService client, final Credential credential) {
      super(tropixFile, credential);
      this.client = client;
    }

    public InputContext getDownloadContext() {
      return factory.getDownloadContext(prepareDownload(), getCredential());
    }

    public OutputContext getUploadContext() {
      return factory.getUploadContext(prepareUpload(), getCredential());
    }
    
    public TransferServiceContextReference prepareDownload() {
      try {
        return client.prepareDownload(getFileId());
      } catch(final RemoteException e) {
        throw new IllegalStateException(e);
      }
    }

    public TransferServiceContextReference prepareUpload() {
      try {
        return client.prepareUpload(getFileId());
      } catch(final RemoteException e) {
        throw new IllegalStateException(e);
      }
    }
    
    public TransferResource prepareDownloadResource() {
      return new CaGridTransferResource(prepareDownload());
    }

    public TransferResource prepareUploadResource() {
      return new CaGridTransferResource(prepareUpload());
    }
  }

  public void setTransferContextFactory(final TransferContextFactory<Credential> factory) {
    this.factory = factory;
  }
}
