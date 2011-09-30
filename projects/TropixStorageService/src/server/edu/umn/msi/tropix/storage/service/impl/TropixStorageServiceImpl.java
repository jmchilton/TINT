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

package edu.umn.msi.tropix.storage.service.impl;

import java.io.File;
import java.io.InputStream;
import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResource;
import org.cagrid.transfer.context.service.helper.DataStagedCallback;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.cagrid.transfer.descriptor.DataDescriptor;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.io.FileCoercible;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.HasStreamInputContext;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.common.logging.LogExceptions;
import edu.umn.msi.tropix.storage.core.StorageManager;
import edu.umn.msi.tropix.storage.core.StorageManager.UploadCallback;
import edu.umn.msi.tropix.storage.service.TropixStorageService;

public class TropixStorageServiceImpl implements TropixStorageService {
  private static final Log LOG = LogFactory.getLog(TropixStorageServiceImpl.class);
  private TransferServiceContextReferenceFactory tscrFactory;
  private Supplier<String> identitySupplier;
  private StorageManager storageManager;

  @LogExceptions
  public boolean canDelete(final String id) throws RemoteException {
    final String callerIdentity = getCallerIdentity();
    return storageManager.canDelete(id, callerIdentity);
  }

  @LogExceptions
  public boolean canDownload(final String id) throws RemoteException {
    final String callerIdentity = getCallerIdentity();
    return storageManager.canDownload(id, callerIdentity);
  }

  @LogExceptions
  public boolean canUpload(final String id) throws RemoteException {
    final String callerIdentity = getCallerIdentity();
    return storageManager.canUpload(id, callerIdentity);
  }

  @LogExceptions
  public TransferServiceContextReference prepareDownload(final String id) throws RemoteException {
    LOG.info("Download attempt of file id " + id);
    final String callerIdentity = getCallerIdentity();
    final HasStreamInputContext downloadFile = storageManager.download(id, callerIdentity);
    final DataDescriptor dataDescriptor = new DataDescriptor();
    dataDescriptor.setName(id);
    final TransferServiceContextReference reference;
    // Prefer file, otherwise an additional copy of the data will be made on hard drive
    if(downloadFile instanceof FileCoercible) {
      reference = tscrFactory.createTransferContext(((FileCoercible) downloadFile).asFile(), dataDescriptor, false);      
    } else {
      reference = tscrFactory.createTransferContext(downloadFile.asInputStream(), dataDescriptor);      
    }
    return reference;
  }

  @LogExceptions
  public TransferServiceContextReference prepareUpload(final String id) throws RemoteException {
    LOG.info("Upload attempt of file id " + id);
    final String callerIdentity = getCallerIdentity();
    final UploadCallback uploadCallback = storageManager.upload(id, callerIdentity);
    final DataDescriptor dataDescriptor = new DataDescriptor();
    dataDescriptor.setName(id);
    final DataStagedCallback callback = new DataStagedCallback() {
      public void dataStaged(final TransferServiceContextResource resource) {
        try {
          final String location = resource.getDataStorageDescriptor().getLocation();
          final InputStream inputStream = FileUtilsFactory.getInstance().getFileInputStream(new File(location));
          uploadCallback.onUpload(inputStream);
        } catch(RuntimeException e) {
          ExceptionUtils.logQuietly(LOG, e);
        }
      }
    };
    TransferServiceContextReference reference;
    LOG.debug("About to create transfer context");
    reference = tscrFactory.createTransferContext(dataDescriptor, callback);
    LOG.debug("Transfer context created");
    return reference;
  }

  @LogExceptions
  public boolean exists(final String id) throws RemoteException {
    return storageManager.exists(id);
  }

  @LogExceptions
  public boolean delete(final String id) throws RemoteException {
    return storageManager.delete(id, getCallerIdentity());
  }

  private String getCallerIdentity() throws RemoteException {
    return identitySupplier.get();
  }

  public void setTscrFactory(final TransferServiceContextReferenceFactory tscrFactory) {
    this.tscrFactory = tscrFactory;
  }

  public void setIdentitySupplier(final Supplier<String> identitySupplier) {
    this.identitySupplier = identitySupplier;
  }

  public void setStorageManager(final StorageManager storageManager) {
    this.storageManager = storageManager;
  }

}
