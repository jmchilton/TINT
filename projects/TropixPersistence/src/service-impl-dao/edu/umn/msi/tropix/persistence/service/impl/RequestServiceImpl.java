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

package edu.umn.msi.tropix.persistence.service.impl;

import javax.annotation.ManagedBean;
import javax.inject.Named;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.InternalRequest;
import edu.umn.msi.tropix.models.Provider;
import edu.umn.msi.tropix.models.Request;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.persistence.service.RequestService;
import edu.umn.msi.tropix.persistence.service.request.RequestSubmission;
import edu.umn.msi.tropix.persistence.service.requestid.RequestId;

@ManagedBean @Named("requestService")
class RequestServiceImpl extends ServiceBase implements RequestService {
  private Request getRequest(final RequestId externalRequestId) {
    return super.getTropixObjectDao().loadRequest(externalRequestId.getRequestHostId(), externalRequestId.getRequestExternalId());
  }

  public Request createOrUpdateRequest(final RequestId requestId, final RequestSubmission requestSubmission) {
    boolean newRequest = false;
    Request request = getRequest(requestId);
    if(request == null) {
      request = new Request();
      request.setCommitted(true);
      request.setState("ACTIVE");
      request.setContents(Sets.<TropixObject>newHashSet());
      request.setExternalId(requestId.getRequestExternalId());
      request.setRequestorId(requestId.getRequestHostId());
      newRequest = true;
    }

    request.setName(requestSubmission.getName());
    request.setDescription(requestSubmission.getDescription());
    request.setContact(requestSubmission.getContact());
    request.setDestination(requestSubmission.getDestination());
    request.setServiceId(requestSubmission.getServiceId());
    request.setServiceInfo(requestSubmission.getServiceInfo());
    final Provider provider = getProviderDao().loadFromCatalogId(requestSubmission.getProviderId());
    request.setProvider(provider);
    if(newRequest) {    
      saveNewObject(request);
    } else {
      updateObject(request);
    }
    provider.getObjects().add(request);
    getProviderDao().saveObject(provider);
    return request;
  }

  public String getReport(final RequestId externalRequestId) {
    final Request request = getRequest(externalRequestId);
    return request.getReport();
  }

  public InternalRequest loadInternalRequest(final RequestId externalRequestId) {
    return super.getTropixObjectDao().loadTropixObject(externalRequestId.getRequestExternalId(), InternalRequest.class);
  }

  public Request loadRequest(final String requestId) {
    return super.getTropixObjectDao().loadTropixObject(requestId, Request.class);
  }

  public InternalRequest loadInternalRequest(final String gridId, final String internalRequestId) {
    return super.getTropixObjectDao().loadTropixObject(internalRequestId, InternalRequest.class);
  }

  public void setupRequestFile(final String requestId, final TropixFile tropixFile) {
    final Request request = super.getTropixObjectDao().loadTropixObject(requestId, Request.class);
    tropixFile.setFileType(super.getFileType(StockFileExtensionEnum.UNKNOWN));
    super.saveNewObjectToDestination(tropixFile, null, request.getId());
  }

  public void updateState(final RequestId requestId, final String state) {
    final InternalRequest iRequest = super.getTropixObjectDao().loadTropixObject(requestId.getRequestExternalId(), InternalRequest.class);
    iRequest.setState(state);
    super.getTropixObjectDao().saveOrUpdateTropixObject(iRequest);
  }

  public Request[] getActiveRequests(final String gridId) {
    final Iterable<Request> requests = getTropixObjectDao().getActiveRequests(gridId);
    return Iterables.toArray(requests, Request.class);
  }

  public InternalRequest[] getOutgoingRequests(final String gridId) {
    final Iterable<InternalRequest> requests = getTropixObjectDao().getInternalRequests(gridId);
    return Iterables.toArray(requests, InternalRequest.class);
  }

  public boolean isInternalRequest(final String requestId) {
    return getTropixObjectDao().isInstance(requestId, InternalRequest.class);
  }

  public InternalRequest setupInternalRequest(final String gridId, final InternalRequest request, final String folderId) {
    final InternalRequest newRequest = new InternalRequest();
    newRequest.setCommitted(false);
    newRequest.setContact(request.getContact());
    newRequest.setDescription(request.getDescription());
    newRequest.setDestination(request.getDestination());
    newRequest.setName(request.getName());
    newRequest.setServiceId(request.getServiceId());
    newRequest.setStorageServiceUrl(request.getStorageServiceUrl());
    newRequest.setServiceInfo(request.getServiceInfo());
    newRequest.setRequestServiceUrl(request.getRequestServiceUrl());
    newRequest.setState(request.getState());
    newRequest.setCommitted(true);
    newRequest.setRequestorId(request.getRequestorId());

    super.saveNewObject(newRequest, gridId);
    newRequest.setExternalId(newRequest.getId());
    newRequest.setDestinationFolder(getTropixObjectDao().loadTropixObject(folderId, Folder.class));
    super.getTropixObjectDao().saveOrUpdateTropixObject(newRequest);
    return newRequest;
  }

  public void setReport(final String gridId, final String requestId, final String report) {
    final Request request = super.getTropixObjectDao().loadTropixObject(requestId, Request.class);
    request.setReport(report);
    super.getTropixObjectDao().saveOrUpdateTropixObject(request);
  }

  public void updateState(final String gridId, final String requestId, final String state) {
    final Request request = getTropixObjectDao().loadTropixObject(requestId, Request.class);
    request.setState(state);
    getTropixObjectDao().saveOrUpdateTropixObject(request);
  }

  public String getContact(final String gridId, final String requestId) {
    final Request request = getTropixObjectDao().loadTropixObject(requestId, Request.class);
    return request.getContact();
  }

  public String getReport(final String gridId, final String requestId) {
    final Request request = getTropixObjectDao().loadTropixObject(requestId, Request.class);
    return request.getReport();
  }

  public String getServiceInfo(final String gridId, final String requestId) {
    final Request request = getTropixObjectDao().loadTropixObject(requestId, Request.class);
    return request.getServiceInfo();
  }

}
