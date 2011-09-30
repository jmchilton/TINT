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

package edu.umn.msi.tropix.client.request.impl;

import info.minnesotapartnership.tropix.request.TropixRequestService;
import info.minnesotapartnership.tropix.request.models.RequestStatus;
import info.minnesotapartnership.tropix.request.models.ServiceResult;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.client.request.RequestBean;
import edu.umn.msi.tropix.client.request.RequestManager;
import edu.umn.msi.tropix.client.request.actions.impl.PersistentActionManager;
import edu.umn.msi.tropix.grid.GridServiceFactory;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.models.InternalRequest;
import edu.umn.msi.tropix.models.Request;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.persistence.service.FileService;
import edu.umn.msi.tropix.persistence.service.RequestService;

public class RequestManagerImpl implements RequestManager {
  private static final Log LOG = LogFactory.getLog(RequestManagerImpl.class);
  private RequestService requestService;
  private FileService fileService;
  private PersistentActionManager persistentActionManager;
  private Requestor requestor;
  private GridServiceFactory<TropixRequestService> tropixRequestServiceFactory;
  private Supplier<Credential> credentialSupplier;

  public void completeRequest(final String userGridId, final String requestId) {
    LOG.info("Completing request with id " + requestId + " as signaled by user " + userGridId);
    final Request request = requestService.loadRequest(requestId);
    final String requestServiceUrl = request.getDestination();
    final String externalRequestId = request.getExternalId();
    if(!requestService.isInternalRequest(requestId)) {
      final List<TropixFile> files = Arrays.asList(fileService.getFiles(userGridId, new String[] {requestId}));
      LOG.info("Request with id " + requestId + " for completion is an external request, sending " + files.size() + " files.");
      for(final TropixFile file : files) {
        final String fileId = file.getFileId();
        final String storageServiceUrl = file.getStorageServiceUrl();
        final ServiceResult serviceResult = new ServiceResult();
        serviceResult.setName(file.getName());
        serviceResult.setDescription(file.getDescription());
        final Date creationDate = new Date(Long.parseLong(file.getCreationTime()));
        serviceResult.setDateCreated(creationDate);
        serviceResult.setRequestId(externalRequestId);
        LOG.info("Uploading file with fileId " + fileId + " at storaging service " + storageServiceUrl);
        persistentActionManager.upload(requestServiceUrl, externalRequestId, fileId, storageServiceUrl, serviceResult);
      }
    } else {
      // Should I update the state of external requests as well
      requestService.updateState(userGridId, requestId, RequestStatus.COMPLETE.toString());
    }
    LOG.info("Sending complete state for request with id " + requestId + " to requestServiceUrl " + requestServiceUrl + " with external id " + externalRequestId);
    persistentActionManager.update(requestServiceUrl, externalRequestId, RequestStatus.COMPLETE);
  }

  public String getStatusReport(final String userGridId, final String requestId) {
    final InternalRequest request = requestService.loadInternalRequest(userGridId, requestId);
    final String requestServiceUrl = request.getRequestServiceUrl();
    final String externalRequestId = request.getExternalId();
    final TropixRequestService tropixRequestService = tropixRequestServiceFactory.getService(requestServiceUrl, credentialSupplier.get());
    try {
      return tropixRequestService.getReport(externalRequestId);
    } catch(final RemoteException e) {
      throw new RuntimeException(e);
    }
  }

  public void request(final RequestBean requestBean, final String gridId) {
    requestor.request(requestBean, gridId);
  }

  public void setRequestService(final RequestService requestService) {
    this.requestService = requestService;
  }

  public void setFileService(final FileService fileService) {
    this.fileService = fileService;
  }

  public void setPersistentActionManager(final PersistentActionManager persistentActionManager) {
    this.persistentActionManager = persistentActionManager;
  }

  public void setRequestor(final Requestor requestor) {
    this.requestor = requestor;
  }

  public void setTropixRequestServiceFactory(final GridServiceFactory<TropixRequestService> tropixRequestServiceFactory) {
    this.tropixRequestServiceFactory = tropixRequestServiceFactory;
  }

  public void setCredentialSupplier(final Supplier<Credential> credentialSupplier) {
    this.credentialSupplier = credentialSupplier;
  }
}
