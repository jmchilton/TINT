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
import info.minnesotapartnership.tropix.request.models.ServiceRequest;

import java.rmi.RemoteException;
import java.util.UUID;

import org.easymock.Capture;
import org.easymock.classextension.EasyMock;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.client.request.RequestBean;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.grid.GridServiceFactory;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.models.InternalRequest;
import edu.umn.msi.tropix.persistence.service.RequestService;

public class RequestorImplTest {

  @Test(groups = "unit")
  public void normalExecution() throws RemoteException {
    final String gridId = UUID.randomUUID().toString();
    final String folderId = UUID.randomUUID().toString();
    final String catalogId = UUID.randomUUID().toString();
    final String serviceId = UUID.randomUUID().toString();
    final String storageServiceUrl = UUID.randomUUID().toString();
    final String localRequestServiceUrl = UUID.randomUUID().toString();
    final String requestServiceUrl = UUID.randomUUID().toString();
    final Credential credential = Credentials.getMock();

    final InternalRequest savedRequest = new InternalRequest();
    savedRequest.setId(UUID.randomUUID().toString());

    final RequestorImpl requestorImpl = new RequestorImpl();
    requestorImpl.setLocalRequestServiceUrl(localRequestServiceUrl);
    requestorImpl.setStorageServiceUrl(storageServiceUrl);
    final Function<String, String> catalogIdToRequestServiceFunction = EasyMockUtils.createMockFunction();
    requestorImpl.setCatalogIdToRequestServiceFunction(catalogIdToRequestServiceFunction);
    requestorImpl.setCredentialSupplier(Suppliers.ofInstance(credential));
    final RequestService persistentRequestService = EasyMock.createMock(RequestService.class);
    requestorImpl.setRequestService(persistentRequestService);
    @SuppressWarnings("unchecked")
    final GridServiceFactory<TropixRequestService> tropixRequestServiceFactory = EasyMock.createMock(GridServiceFactory.class);
    requestorImpl.setTropixRequestServiceFactory(tropixRequestServiceFactory);
    final TropixRequestService tropixRequestService = EasyMock.createMock(TropixRequestService.class);

    final Capture<InternalRequest> internalRequestCapture = new Capture<InternalRequest>();
    EasyMock.expect(
        persistentRequestService.setupInternalRequest(EasyMock.eq(gridId), EasyMock.capture(internalRequestCapture), EasyMock.eq(folderId)))
        .andReturn(savedRequest);
    EasyMock.expect(catalogIdToRequestServiceFunction.apply(EasyMock.eq(catalogId))).andReturn(requestServiceUrl);

    EasyMock.expect(tropixRequestServiceFactory.getService(requestServiceUrl, credential)).andReturn(tropixRequestService);
    final Capture<ServiceRequest> serviceRequestCapture = new Capture<ServiceRequest>();
    tropixRequestService.request(EasyMock.capture(serviceRequestCapture));

    final RequestBean requestBean = new RequestBean();
    requestBean.setCatalogId(catalogId);
    requestBean.setDestinationId(folderId);
    requestBean.setEmail("moo@cow.net");
    requestBean.setName("john");
    requestBean.setPhone("612-555-9229");
    requestBean.setRequestDescription("rdescr");
    requestBean.setRequestName("rname");
    requestBean.setServiceId(serviceId);
    requestBean.setInputs(Maps.<String, String>newHashMap());
    EasyMock.replay(catalogIdToRequestServiceFunction, tropixRequestServiceFactory, persistentRequestService, tropixRequestService);
    requestorImpl.request(requestBean, gridId);
    EasyMock.verify(catalogIdToRequestServiceFunction, tropixRequestServiceFactory, persistentRequestService, tropixRequestService);

    final InternalRequest submittedInternalRequest = internalRequestCapture.getValue();
    assert submittedInternalRequest.getContact().contains("moo@cow.net");
    assert submittedInternalRequest.getContact().contains("612-555-9229");
    assert submittedInternalRequest.getContact().contains("john");
    assert submittedInternalRequest.getDescription().equals("rdescr");
    assert submittedInternalRequest.getDestination().equals(localRequestServiceUrl);
    assert submittedInternalRequest.getName().equals("rname") : submittedInternalRequest.getName();
    assert submittedInternalRequest.getServiceId().equals(serviceId);
    assert submittedInternalRequest.getRequestServiceUrl().equals(requestServiceUrl);
    assert submittedInternalRequest.getState().equals(RequestStatus.ACTIVE.toString());

    final ServiceRequest serviceRequest = serviceRequestCapture.getValue();
    assert serviceRequest.getName().equals("rname");
    assert serviceRequest.getContact().equals(submittedInternalRequest.getContact());
    assert serviceRequest.getDescription().equals("rdescr");
    assert serviceRequest.getRecipientId().equals(gridId);
    assert serviceRequest.getRequestId().equals(savedRequest.getId());
    assert serviceRequest.getRequestService().equals(localRequestServiceUrl);

  }
}
