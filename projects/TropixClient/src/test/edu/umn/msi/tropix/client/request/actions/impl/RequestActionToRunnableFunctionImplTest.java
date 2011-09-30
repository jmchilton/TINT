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
import info.minnesotapartnership.tropix.request.models.RequestStatus;
import info.minnesotapartnership.tropix.request.models.ServiceResult;

import java.rmi.RemoteException;

import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import edu.umn.msi.tropix.common.concurrent.Repeater;
import edu.umn.msi.tropix.common.io.ByteArrayOutputContextImpl;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.test.MockObjectCollection;
import edu.umn.msi.tropix.grid.GridServiceFactory;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.grid.io.transfer.TransferContextFactory;
import edu.umn.msi.tropix.storage.client.StorageData;
import edu.umn.msi.tropix.storage.client.StorageDataFactory;

public class RequestActionToRunnableFunctionImplTest {
  private Supplier<Repeater<Runnable>> repeaterSupplier;
  private GridServiceFactory<TropixRequestService> tropixRequestServiceFactory;
  private TropixRequestService tropixRequestService;
  private Supplier<Credential> proxySupplier;
  private TransferContextFactory<Credential> uploadContextFactory;
  private StorageDataFactory storageDataFactory;
  private StorageData storageData;
  private Credential credential;
  private RequestActionToRunnableFunctionImpl func;
  private Repeater<Runnable> repeater;
  private Capture<Runnable> runnableCapture;
  private MockObjectCollection mockObjects;
  private final String service = "serv", requestId = "req";

  @BeforeMethod(groups = "unit")
  public void init() {
    this.credential = Credentials.getMock();
    this.tropixRequestServiceFactory = EasyMock.createMock(GridServiceFactory.class);
    this.tropixRequestService = EasyMock.createMock(TropixRequestService.class);
    this.proxySupplier = Suppliers.ofInstance(credential);
    this.repeater = EasyMock.createMock(Repeater.class);
    this.repeaterSupplier = Suppliers.ofInstance(repeater);
    this.uploadContextFactory = EasyMock.createMock(TransferContextFactory.class);
    this.runnableCapture = new Capture<Runnable>();
    this.storageData = EasyMock.createMock(StorageData.class);
    this.storageDataFactory = EasyMock.createMock(StorageDataFactory.class);

    this.func = new RequestActionToRunnableFunctionImpl();
    this.func.setProxySupplier(proxySupplier);
    this.func.setRepeaterSupplier(repeaterSupplier);
    this.func.setStorageDataFactory(storageDataFactory);
    this.func.setTropixRequestServiceFactory(tropixRequestServiceFactory);
    this.func.setUploadContextFactory(uploadContextFactory);

    this.repeater.setBaseRunnable(EasyMock.capture(runnableCapture));
    mockObjects = MockObjectCollection.fromObjects(this.tropixRequestServiceFactory, this.repeater, this.storageData, this.storageDataFactory,
        this.tropixRequestService, this.uploadContextFactory);
  }

  @Test(groups = "unit")
  public void uploadNormally() throws RemoteException {
    upload(false);
  }

  @Test(groups = "unit")
  public void uploadProblem() throws RemoteException {
    upload(true);
  }

  @Test(groups = "unit")
  public void updateNormally() throws RemoteException {
    update(false);
  }

  @Test(groups = "unit")
  public void updateProblem() throws RemoteException {
    update(true);
  }

  @Test(groups = "unit", expectedExceptions = IllegalStateException.class)
  public void unknownRequestAction() {
    mockObjects.replay();
    assert repeater == func.apply(new RequestAction() {
    });
    mockObjects.verifyAndReset();
    runnableCapture.getValue().run();
  }

  public void update(final boolean remoteException) throws RemoteException {
    final RequestStatus status = RequestStatus.COMPLETE;
    final UpdateRequestAction action = new UpdateRequestAction(124L, service, requestId, status.toString());
    mockObjects.replay();
    assert repeater == func.apply(action);
    mockObjects.verifyAndReset();
    EasyMock.expect(this.tropixRequestServiceFactory.getService(service, credential)).andReturn(this.tropixRequestService);
    tropixRequestService.handleStatusUpdate(requestId, status);
    if(remoteException) {
      EasyMock.expectLastCall().andThrow(new RemoteException());
    }
    mockObjects.replay();
    boolean exceptionThrown = false;
    try {
      runnableCapture.getValue().run();
    } catch(final RuntimeException e) {
      exceptionThrown = true;
    }
    assert remoteException == exceptionThrown;
    mockObjects.verifyAndReset();
  }

  private void upload(final boolean remoteException) throws RemoteException {
    final String fileId = "fileid", sservice = "sserv";
    final ServiceResult res = new ServiceResult();
    res.setName("moo");
    final UploadRequestAction action = new UploadRequestAction(123L, service, requestId, fileId, sservice, ServiceResultXmlUtils.toXml(res));
    final TransferServiceContextReference tscRef = new TransferServiceContextReference();
    mockObjects.replay();
    assert repeater == func.apply(action);
    mockObjects.verifyAndReset();
    EasyMock.expect(this.tropixRequestServiceFactory.getService(service, credential)).andReturn(this.tropixRequestService);
    final ByteArrayOutputContextImpl outputContext = new ByteArrayOutputContextImpl();
    this.tropixRequestService.handleResult(res);
    if(remoteException) {
      EasyMock.expectLastCall().andThrow(new RemoteException("Problem"));
    } else {
      EasyMock.expectLastCall().andReturn(tscRef);
      final InputContext inputContext = InputContexts.forString("Moo Cow");
      EasyMock.expect(this.storageDataFactory.getStorageData(fileId, sservice, credential)).andReturn(storageData);
      EasyMock.expect(this.uploadContextFactory.getUploadContext(tscRef, credential)).andReturn(outputContext);
      EasyMock.expect(this.storageData.getDownloadContext()).andReturn(inputContext);
    }
    mockObjects.replay();
    boolean exceptionThrown = false;
    try {
      runnableCapture.getValue().run();
    } catch(final RuntimeException e) {
      exceptionThrown = true;
    }
    assert remoteException == exceptionThrown;
    mockObjects.verifyAndReset();
    if(!remoteException) {
      assert "Moo Cow".equals(new String(outputContext.toByteArray()));
    }
  }
}
