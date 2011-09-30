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

package edu.umn.msi.tropix.webgui.server;

import java.util.Collection;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.client.request.RequestBean;
import edu.umn.msi.tropix.client.request.RequestManager;
import edu.umn.msi.tropix.models.InternalRequest;
import edu.umn.msi.tropix.models.Request;
import edu.umn.msi.tropix.persistence.service.RequestService;
import edu.umn.msi.tropix.webgui.server.aop.ServiceMethod;
import edu.umn.msi.tropix.webgui.server.models.BeanSanitizer;
import edu.umn.msi.tropix.webgui.server.security.UserSession;

@ManagedBean
public class RequestServiceImpl implements edu.umn.msi.tropix.webgui.services.tropix.RequestService {
  private final UserSession userSession;
  private final RequestService requestService;
  private final RequestManager requestManager;
  private final BeanSanitizer beanSanitizer;

  @Inject
  RequestServiceImpl(final UserSession userSession, final RequestService requestService, final RequestManager requestManager, final BeanSanitizer beanSanitizer) {
    this.userSession = userSession;
    this.requestService = requestService;
    this.requestManager = requestManager;
    this.beanSanitizer = beanSanitizer;
  }

  @ServiceMethod(readOnly = true)
  public Request getExpandedRequest(final String requestId) {
    final Request request = requestService.loadRequest(requestId);
    final String report = requestService.getReport(userSession.getGridId(), requestId);
    request.setReport(report);

    final String contact = requestService.getContact(userSession.getGridId(), requestId);
    request.setContact(contact);

    final String rawServiceInfo = requestService.getServiceInfo(userSession.getGridId(), requestId);
    request.setServiceInfo(rawServiceInfo);

    return beanSanitizer.sanitize(request);
  }

  @ServiceMethod
  public void completeRequest(final String requestId) {
    requestManager.completeRequest(userSession.getGridId(), requestId);
  }

  @ServiceMethod(readOnly = true)
  public Collection<Request> getActiveRequests() {
    final Request[] requestArray = requestService.getActiveRequests(userSession.getGridId());
    final List<Request> requests = Lists.newArrayListWithCapacity(requestArray.length);
    for(final Request request : requestArray) {
      requests.add(beanSanitizer.sanitize(request));
    }
    return requests;
  }

  @ServiceMethod
  public void setReport(final String requestId, final String report) {
    requestService.setReport(userSession.getGridId(), requestId, report);
  }

  @ServiceMethod
  public void request(final RequestBean requestBean) {
    requestManager.request(requestBean, userSession.getGridId());
  }

  @ServiceMethod(readOnly = true)
  public String getContact(final String requestId) {
    return requestService.getContact(userSession.getGridId(), requestId);
  }

  @ServiceMethod(readOnly = true)
  public Collection<InternalRequest> getOutgoingRequests() {
    final InternalRequest[] requestArray = requestService.getOutgoingRequests(userSession.getGridId());
    final List<InternalRequest> requests = Lists.newArrayListWithCapacity(requestArray.length);
    for(final InternalRequest request : requestArray) {
      requests.add(beanSanitizer.sanitize(request));
    }
    return requests;

  }

  @ServiceMethod(readOnly = true)
  public String getLocalStatusReport(final String requestId) {
    return requestService.getReport(userSession.getGridId(), requestId);
  }

  @ServiceMethod(readOnly = true)
  public String getStatusReport(final String requestId) {
    return requestManager.getStatusReport(userSession.getGridId(), requestId);
  }

}
