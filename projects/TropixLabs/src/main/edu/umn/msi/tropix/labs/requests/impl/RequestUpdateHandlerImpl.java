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

package edu.umn.msi.tropix.labs.requests.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;

import edu.umn.msi.tropix.labs.requests.RequestUpdateHandler;
import edu.umn.msi.tropix.models.InternalRequest;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.persistence.service.RequestService;
import edu.umn.msi.tropix.persistence.service.requestid.RequestId;
import edu.umn.msi.tropix.storage.client.CredentialBoundModelStorageDataFactory;
import edu.umn.msi.tropix.storage.client.ModelStorageData;
import edu.umn.msi.tropix.transfer.types.CaGridTransferResource;
import edu.umn.msi.tropix.transfer.types.TransferResource;

public class RequestUpdateHandlerImpl implements RequestUpdateHandler {
  private static final Log LOG = LogFactory.getLog(RequestUpdateHandlerImpl.class);
  private RequestService requestService;
  private CredentialBoundModelStorageDataFactory modelStorageDataFactory;
    
  public TransferResource handleResult(final RequestResult result) {
    LOG.info("Handling requestResult " + result);
    final String fileName = result.getFileName();
    final String fileDescription = result.getFileDescription();
    final InternalRequest iRequest = requestService.loadInternalRequest(result.getRequestId());
    if(iRequest == null) {
      throw new RuntimeException("No known or valid request with id " + result.getRequestId());
    }
    final String storageServiceUrl = iRequest.getStorageServiceUrl();
    LOG.info("Preparing location for " + result + " at storage service with url " + storageServiceUrl);
    final ModelStorageData storageData = modelStorageDataFactory.getStorageData(storageServiceUrl);
    final TransferResource uploadResource = storageData.prepareUploadResource();
    final TropixFile file = storageData.getTropixFile();
    file.setName(fileName);
    file.setDescription(fileDescription);
    file.setCommitted(false);
    LOG.info("Setting up request file");
    requestService.setupRequestFile(iRequest.getId(), file);
    return uploadResource;
  }

  public void handleStatus(final RequestId requestId, final Status status) {
    LOG.info("Handling status update to " + status);
    if(status == Status.COMPLETE) {
      requestService.updateState(requestId, "COMPLETE");
    }
    LOG.info("Completed status update");
  }

  public void setRequestService(final RequestService requestService) {
    this.requestService = requestService;
  }

  public void setModelStorageDataFactory(final CredentialBoundModelStorageDataFactory modelStorageDataFactory) {
    this.modelStorageDataFactory = modelStorageDataFactory;
  }

}
