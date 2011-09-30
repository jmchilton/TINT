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

import java.util.UUID;

import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.labs.requests.RequestUpdateHandler.RequestResult;
import edu.umn.msi.tropix.labs.requests.RequestUpdateHandler.Status;
import edu.umn.msi.tropix.models.InternalRequest;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.persistence.service.RequestService;
import edu.umn.msi.tropix.persistence.service.requestid.RequestId;
import edu.umn.msi.tropix.storage.client.CredentialBoundModelStorageDataFactory;
import edu.umn.msi.tropix.storage.client.ModelStorageData;
import edu.umn.msi.tropix.transfer.types.CaGridTransferResource;

public class RequestUpdateHandlerImplTest {

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void handleResultNoId() {
    final RequestUpdateHandlerImpl handler = new RequestUpdateHandlerImpl();
    final RequestId requestId = new RequestId("a", "b");
    final RequestResult result = new RequestResult(requestId, "fName", "fDesc");
    final RequestService service = EasyMock.createMock(RequestService.class);
    handler.setRequestService(service);
    EasyMock.expect(service.loadInternalRequest(requestId)).andReturn(null);
    EasyMock.replay(service);
    handler.handleResult(result);
  }

  @Test(groups = "unit")
  public void handleResult() {
    final RequestUpdateHandlerImpl handler = new RequestUpdateHandlerImpl();
    final RequestId requestId = new RequestId("a", "b");
    final RequestResult result = new RequestResult(requestId, "fName", "fDesc");

    final CredentialBoundModelStorageDataFactory sFactory = EasyMock.createMock(CredentialBoundModelStorageDataFactory.class);
    final RequestService service = EasyMock.createMock(RequestService.class);
    handler.setModelStorageDataFactory(sFactory);
    handler.setRequestService(service);
    final InternalRequest iRequest = new InternalRequest();
    iRequest.setId("id");
    iRequest.setStorageServiceUrl("url");
    EasyMock.expect(service.loadInternalRequest(requestId)).andReturn(iRequest);
    final Capture<TropixFile> fileCapture = new Capture<TropixFile>();
    service.setupRequestFile(EasyMock.eq("id"), EasyMock.capture(fileCapture));
    final ModelStorageData data = EasyMock.createMock(ModelStorageData.class);

    final TropixFile file = new TropixFile();
    file.setStorageServiceUrl("url");
    file.setFileId(UUID.randomUUID().toString());
    EasyMock.expect(data.getTropixFile()).andReturn(file);
    final TransferServiceContextReference ref = new TransferServiceContextReference();
    final CaGridTransferResource resource = new CaGridTransferResource(ref);
    EasyMock.expect(data.prepareUploadResource()).andReturn(resource);
    EasyMock.expect(sFactory.getStorageData("url")).andReturn(data);
    EasyMock.replay(sFactory, service, data);
    assert resource == handler.handleResult(result);
    EasyMock.verify(sFactory, service, data);

    final TropixFile savedFile = fileCapture.getValue();
    assert savedFile.getFileId().equals(file.getFileId());
    assert savedFile.getName().equals("fName");
    assert savedFile.getDescription().equals("fDesc");
  }

  @Test(groups = "unit")
  public void handleStatus() {
    final RequestUpdateHandlerImpl handler = new RequestUpdateHandlerImpl();
    final RequestId requestId = new RequestId("a", "b");
    final RequestService service = EasyMock.createMock(RequestService.class);
    handler.setRequestService(service);
    service.updateState(requestId, "COMPLETE");
    EasyMock.replay(service);
    handler.handleStatus(requestId, Status.COMPLETE);
    EasyMock.verify(service);
  }

}
