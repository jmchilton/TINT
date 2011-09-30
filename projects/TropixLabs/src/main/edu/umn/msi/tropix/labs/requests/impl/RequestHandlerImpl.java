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

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Multimap;

import edu.umn.msi.tropix.labs.requests.RequestHandler;
import edu.umn.msi.tropix.models.Request;
import edu.umn.msi.tropix.persistence.service.RequestService;
import edu.umn.msi.tropix.persistence.service.request.RequestSubmission;
import edu.umn.msi.tropix.persistence.service.requestid.RequestId;

public class RequestHandlerImpl implements RequestHandler {
  private static final Log LOG = LogFactory.getLog(RequestHandlerImpl.class);
  private RequestService requestService;
  private Function<String, String> catalogServiceToProviderFunction;
  private RequestDirectoryCreator requestDirectoryCreator;

  public String getReport(final RequestId requestId) {
    return this.requestService.getReport(requestId);
  }

  private String getServiceInfo(final RequestInfo requestInfo) {
    final Multimap<String, String> fields = requestInfo.getFields();
    final StringBuilder serviceInfo = new StringBuilder();
    if(fields != null) {
      for(final String key : fields.keySet()) {
        final String value = Joiner.on(",").join(fields.get(key)).replace("\n", "");
        serviceInfo.append(key + ":" + value + "\n");
      }
    }
    return serviceInfo.toString();
  }

  public void request(final RequestInfo requestInfo) {
    LOG.info("Request obtained " + requestInfo + " with " + (requestInfo.getFields() != null ? requestInfo.getFields().size() : 0) + " attributes.");
    final String serviceId = requestInfo.getCatalogServiceId();
    final String providerId = this.catalogServiceToProviderFunction.apply(serviceId);
    final String serviceInfo = this.getServiceInfo(requestInfo);
    final String name = requestInfo.getName();
    final String description = requestInfo.getDescription();
    final String destination = requestInfo.getDestination();
    final String contact = requestInfo.getContact();

    final RequestSubmission requestSubmission = new RequestSubmission();
    requestSubmission.setServiceId(serviceId);
    requestSubmission.setProviderId(providerId);
    requestSubmission.setServiceInfo(serviceInfo);
    requestSubmission.setName(name);
    requestSubmission.setDescription(description);
    requestSubmission.setDestination(destination);
    requestSubmission.setContact(contact);

    LOG.info("Creating or updating persistence based on  requestSubmission " + requestSubmission);
    final Request request = this.requestService.createOrUpdateRequest(requestInfo.getRequestId(), requestSubmission);
    LOG.info("Creating request directory for serviceId " + serviceId + " and request id " + request.getId());
    requestDirectoryCreator.createDirectory(serviceId, request.getId());
  }

  public void setRequestService(final RequestService requestService) {
    this.requestService = requestService;
  }

  public void setCatalogServiceToProviderFunction(final Function<String, String> catalogServiceToProviderFunction) {
    this.catalogServiceToProviderFunction = catalogServiceToProviderFunction;
  }

  public void setRequestDirectoryCreator(final RequestDirectoryCreator requestDirectoryCreator) {
    this.requestDirectoryCreator = requestDirectoryCreator;
  }

}
