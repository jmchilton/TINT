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

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.EasyMockUtils.Reference;
import edu.umn.msi.tropix.labs.requests.RequestHandler.RequestInfo;
import edu.umn.msi.tropix.models.Request;
import edu.umn.msi.tropix.persistence.service.RequestService;
import edu.umn.msi.tropix.persistence.service.request.RequestSubmission;
import edu.umn.msi.tropix.persistence.service.requestid.RequestId;

public class RequestHandlerImplTest {

  /**
   * Tests RequestHandlerImplTest.getReport(RequestId).
   */
  @Test(groups = "unit")
  public void getReport() {
    final RequestHandlerImpl handler = new RequestHandlerImpl();
    final RequestId id = new RequestId("moo", "cow");

    final RequestService requestService = EasyMock.createMock(RequestService.class);
    EasyMock.expect(requestService.getReport(id)).andReturn("the report");
    handler.setRequestService(requestService);
    EasyMock.replay(requestService);
    assert "the report".equals(handler.getReport(id));
    EasyMock.verify(requestService);
  }

  /**
   * Tests RequestHandlerImplTest.request(RequestInfo).
   */
  @Test(groups = "unit")
  public void request() {
    request(null, "");
    request(HashMultimap.<String, String>create(), "");
    request(ImmutableMultimap.<String, String>builder().put("Moo", "Cow").build(), "Moo\\s*:\\s*Cow\n");

  }

  private void request(final Multimap<String, String> fields, final String infoRegex) {
    final RequestHandlerImpl handler = new RequestHandlerImpl();
    final RequestId id = new RequestId("moo", "cow");
    final String requestId = UUID.randomUUID().toString();

    final RequestService requestService = EasyMock.createMock(RequestService.class);
    final Reference<RequestSubmission> subRef = EasyMockUtils.newReference();
    final Request request = new Request();
    request.setId(requestId);

    EasyMock.expect(requestService.createOrUpdateRequest(EasyMock.eq(id), EasyMockUtils.record(subRef))).andReturn(request);

    final String serviceId = UUID.randomUUID().toString();
    final String providerId = UUID.randomUUID().toString();

    final RequestDirectoryCreator creator = EasyMock.createMock(RequestDirectoryCreator.class);
    creator.createDirectory(serviceId, requestId);

    final Function<String, String> providerIdFunction = EasyMockUtils.createMockFunction();
    EasyMock.expect(providerIdFunction.apply(serviceId)).andReturn(providerId);

    final RequestInfo requestInfo = new RequestInfo();
    requestInfo.setCatalogServiceId(serviceId);
    requestInfo.setContact("contact");
    requestInfo.setDescription("description");
    requestInfo.setDestination("dest");
    requestInfo.setFields(fields);
    requestInfo.setName("name");
    requestInfo.setRequestId(id);

    handler.setCatalogServiceToProviderFunction(providerIdFunction);
    handler.setRequestDirectoryCreator(creator);
    handler.setRequestService(requestService);

    EasyMockUtils.replayAll(requestService, creator, providerIdFunction);
    handler.request(requestInfo);
    EasyMockUtils.verifyAndReset(requestService, creator, providerIdFunction);

    final RequestSubmission requestSubmission = subRef.get();
    assert requestSubmission.getServiceId().equals(serviceId);
    assert requestSubmission.getProviderId().equals(providerId);
    assert requestSubmission.getContact().equals("contact");
    assert requestSubmission.getDescription().equals("description");
    assert requestSubmission.getDestination().equals("dest");
    assert requestSubmission.getServiceInfo().matches(infoRegex);

  }

}
