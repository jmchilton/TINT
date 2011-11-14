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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;

import edu.umn.msi.tropix.client.request.RequestBean;
import edu.umn.msi.tropix.client.request.actions.impl.PersistentActionManager;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.MockObjectCollection;
import edu.umn.msi.tropix.grid.GridServiceFactory;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.models.InternalRequest;
import edu.umn.msi.tropix.models.Request;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.persistence.service.FileService;
import edu.umn.msi.tropix.persistence.service.RequestService;

public class RequestManagerImplTest {

  @Test(groups = "unit")
  public void complete() {
    final RequestManagerImpl manager = new RequestManagerImpl();
    final Requestor requestor = EasyMock.createMock(Requestor.class);
    manager.setRequestor(requestor);
    final RequestBean requestBean = new RequestBean();
    final String gridId = UUID.randomUUID().toString();
    requestor.request(EasyMock.same(requestBean), EasyMock.eq(gridId));
    EasyMock.replay(requestor);
    manager.request(requestBean, gridId);
    EasyMock.verify(requestor);
  }

  @Test(groups = "unit")
  public void externalRequest() {
    final FileService fileService = EasyMock.createMock(FileService.class);
    final PersistentActionManager persistentActionManager = EasyMock.createMock(PersistentActionManager.class);
    final RequestService requestService = EasyMock.createMock(RequestService.class);

    final MockObjectCollection mockObjects = MockObjectCollection.fromObjects(fileService, persistentActionManager, requestService);

    final RequestManagerImpl manager = new RequestManagerImpl();
    manager.setFileService(fileService);
    manager.setPersistentActionManager(persistentActionManager);
    manager.setRequestService(requestService);

    final String userId = UUID.randomUUID().toString();
    final String requestId = UUID.randomUUID().toString();
    final String requestExternalId = UUID.randomUUID().toString();
    final String requestSerivceUrl = UUID.randomUUID().toString();
    final Request request = new Request();
    request.setDestination(requestSerivceUrl);
    request.setExternalId(requestExternalId);
    request.setId(requestId);

    EasyMock.expect(requestService.isInternalRequest(requestId)).andReturn(false);
    EasyMock.expect(requestService.loadRequest(requestId)).andReturn(request);

    final TropixFile tf1 = new TropixFile();
    tf1.setCreationTime(new Date().getTime() + "");
    tf1.setName(UUID.randomUUID().toString());
    tf1.setDescription(UUID.randomUUID().toString());
    tf1.setStorageServiceUrl(UUID.randomUUID().toString());
    tf1.setFileId(UUID.randomUUID().toString());
    final TropixFile tf2 = new TropixFile();
    tf2.setCreationTime(new Date().getTime() + "");
    tf2.setName(UUID.randomUUID().toString());
    tf2.setDescription(UUID.randomUUID().toString());
    tf2.setStorageServiceUrl(UUID.randomUUID().toString());
    tf2.setFileId(UUID.randomUUID().toString());
    final TropixFile[] files = new TropixFile[] {tf1, tf2};
    EasyMock.expect(fileService.getFiles(EasyMock.eq(userId), EasyMock.aryEq(new String[] {requestId}))).andReturn(files);
    for(final TropixFile file : files) {
      final String fileId = file.getFileId();
      final String storageServiceUrl = file.getStorageServiceUrl();
      final Map<String, Object> properties = new HashMap<String, Object>();
      properties.put("name", file.getName());
      properties.put("description", file.getDescription());
      persistentActionManager.upload(EasyMock.eq(requestSerivceUrl), EasyMock.eq(requestExternalId), EasyMock.eq(fileId),
          EasyMock.eq(storageServiceUrl), EasyMockUtils.<ServiceResult>isBeanWithProperties(properties));
    }
    persistentActionManager.update(requestSerivceUrl, requestExternalId, RequestStatus.COMPLETE);
    mockObjects.replay();
    manager.completeRequest(userId, requestId);
    mockObjects.verifyAndReset();

  }

  @Test(groups = "unit")
  public void request() throws RemoteException {
    final RequestService requestService = EasyMock.createMock(RequestService.class);
    @SuppressWarnings("unchecked")
    final GridServiceFactory<TropixRequestService> tropixRequestServiceFactory = EasyMock.createMock(GridServiceFactory.class);
    final TropixRequestService tropixRequestService = EasyMock.createMock(TropixRequestService.class);
    final Credential credential = Credentials.getMock();

    final MockObjectCollection mockObjects = MockObjectCollection.fromObjects(requestService, tropixRequestServiceFactory, tropixRequestService);

    final RequestManagerImpl manager = new RequestManagerImpl();
    manager.setRequestService(requestService);
    manager.setTropixRequestServiceFactory(tropixRequestServiceFactory);
    manager.setCredentialSupplier(Suppliers.ofInstance(credential));

    final String userId = UUID.randomUUID().toString();
    final String requestId = UUID.randomUUID().toString();
    final String requestExternalId = UUID.randomUUID().toString();
    final String requestSerivceUrl = UUID.randomUUID().toString();

    final InternalRequest request = new InternalRequest();
    request.setRequestServiceUrl(requestSerivceUrl);
    request.setExternalId(requestExternalId);
    request.setId(requestId);

    EasyMock.expect(requestService.loadInternalRequest(userId, requestId)).andReturn(request);
    EasyMock.expect(tropixRequestServiceFactory.getService(requestSerivceUrl, credential)).andReturn(tropixRequestService);
    EasyMock.expect(tropixRequestService.getReport(requestExternalId)).andReturn("Moo cow");
    mockObjects.replay();
    assert "Moo cow".equals(manager.getStatusReport(userId, requestId));
    mockObjects.verifyAndReset();

  }

}
