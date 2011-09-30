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

package edu.umn.msi.tropix.client.request.actions.impl;

import info.minnesotapartnership.tropix.request.TropixRequestService;

import java.io.File;
import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.concurrent.Repeater;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.grid.GridServiceFactory;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.io.transfer.UploadContextFactory;
import edu.umn.msi.tropix.storage.client.StorageDataFactory;

public class RequestActionToRunnableFunctionImpl implements Function<RequestAction, Runnable> {
  private static final Log LOG = LogFactory.getLog(RequestActionToRunnableFunctionImpl.class);
  private Supplier<Repeater<Runnable>> repeaterSupplier;
  private GridServiceFactory<TropixRequestService> tropixRequestServiceFactory;
  private Supplier<Credential> proxySupplier;
  private UploadContextFactory<Credential> uploadContextFactory;
  private StorageDataFactory storageDataFactory;

  public Runnable apply(final RequestAction action) {
    final Runnable baseRunnable = new Runnable() {
      public void run() {
        try {
          if(action instanceof UpdateRequestAction) {
            update((UpdateRequestAction) action);
          } else if(action instanceof UploadRequestAction) {
            upload((UploadRequestAction) action);
          } else {
            throw new IllegalStateException("Unknown action type " + action);
          }
        } catch(final Throwable e) {
          ExceptionUtils.logAndRethrowUnchecked(LOG, e);
        }
      }
    };
    final Repeater<Runnable> repeater = repeaterSupplier.get();
    repeater.setBaseRunnable(baseRunnable);
    return repeater;
  }

  private TropixRequestService getRequestService(final RequestAction action) {
    final Credential credential = proxySupplier.get();
    LOG.debug("Creating request service instance with url " + action.getRequestService() + " and identity " + credential.getIdentity());
    return tropixRequestServiceFactory.getService(action.getRequestService(), credential);
  }

  private void update(final UpdateRequestAction action) {
    final TropixRequestService service = getRequestService(action);
    try {
      LOG.debug("Calling handleStatusUpdate with request id " + action.getRequestId() + " and status " + action.getRequestStatus());
      service.handleStatusUpdate(action.getRequestId(), action.getRequestStatus());
      LOG.debug("handleStatusUpdate returned");
    } catch(final RemoteException e) {
      throw new RuntimeException("Failed to update status", e);
    }
  }

  private void upload(final UploadRequestAction action) {
    final TropixRequestService service = getRequestService(action);
    TransferServiceContextReference uploadTscRef;
    try {
      LOG.debug("Calling handleStatusRequest with request id " + action.getRequestId() + " and result " + action.getServiceResultXml());
      uploadTscRef = service.handleResult(ServiceResultXmlUtils.fromXml(action.getServiceResultXml()));
      LOG.debug("handleStatusRequest returned");
    } catch(final RemoteException e) {
      throw new RuntimeException(e);
    }
    final Credential credential = proxySupplier.get();
    final File tempFile = FileUtilsFactory.getInstance().createTempFile("tpxreq", "");
    try {
      LOG.debug("Preparing download context to stage results");
      final InputContext downloadContext = storageDataFactory.getStorageData(action.getFileId(), action.getFileStorageService(), credential).getDownloadContext();
      LOG.debug("Staging result into file " + tempFile);
      downloadContext.get(tempFile);
      LOG.debug("Preparing upload context for result");
      final OutputContext uploadContext = uploadContextFactory.getUploadContext(uploadTscRef, credential);
      LOG.debug("Uploading staged result");
      uploadContext.put(tempFile);
      LOG.debug("Transfer complete");
    } finally {
      FileUtilsFactory.getInstance().deleteQuietly(tempFile);
    }
  }

  public void setRepeaterSupplier(final Supplier<Repeater<Runnable>> repeaterSupplier) {
    this.repeaterSupplier = repeaterSupplier;
  }

  public void setTropixRequestServiceFactory(final GridServiceFactory<TropixRequestService> tropixRequestServiceFactory) {
    this.tropixRequestServiceFactory = tropixRequestServiceFactory;
  }

  public void setProxySupplier(final Supplier<Credential> proxySupplier) {
    this.proxySupplier = proxySupplier;
  }

  public void setUploadContextFactory(final UploadContextFactory<Credential> uploadContextFactory) {
    this.uploadContextFactory = uploadContextFactory;
  }

  public void setStorageDataFactory(final StorageDataFactory storageDataFactory) {
    this.storageDataFactory = storageDataFactory;
  }

}
