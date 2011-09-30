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
import info.minnesotapartnership.tropix.request.models.KeyValuePair;
import info.minnesotapartnership.tropix.request.models.RequestStatus;
import info.minnesotapartnership.tropix.request.models.ServiceRequest;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

import edu.umn.msi.tropix.client.request.RequestBean;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.grid.GridServiceFactory;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.models.InternalRequest;
import edu.umn.msi.tropix.persistence.service.RequestService;

public class RequestorImpl implements Requestor {
  private static final Log LOG = LogFactory.getLog(RequestManagerImpl.class);
  private static final String NEWLINE = System.getProperty("line.separator");
  private GridServiceFactory<TropixRequestService> tropixRequestServiceFactory;
  private Supplier<Credential> credentialSupplier;
  private Function<String, String> catalogIdToRequestServiceFunction;
  private String localRequestServiceUrl, storageServiceUrl;
  private RequestService requestService;

  // TODO: Make this safe.
  private static String getContact(final RequestBean requestBean) {
    final StringBuilder builder = new StringBuilder();
    builder.append("Name: ");
    builder.append(requestBean.getName());
    builder.append(NEWLINE);

    builder.append("E-Mail:");
    builder.append(requestBean.getEmail());
    builder.append(NEWLINE);

    builder.append("Phone: ");
    builder.append(requestBean.getPhone());
    builder.append(NEWLINE);

    return builder.toString();
  }

  public void request(final RequestBean requestBean, final String gridId) {
    final String requestServiceUrl = catalogIdToRequestServiceFunction.apply(requestBean.getCatalogId());
    final Credential credential = credentialSupplier.get();
    final TropixRequestService tropixRequestService = tropixRequestServiceFactory.getService(requestServiceUrl, credential);

    final InternalRequest request = new InternalRequest();
    final String contact = getContact(requestBean);
    final String requestDescription = requestBean.getRequestDescription();
    final String requestName = requestBean.getRequestName();
    final String serviceId = requestBean.getServiceId();

    request.setRequestorId(credential.getIdentity());
    request.setName(requestName);
    request.setDescription(requestDescription);
    request.setContact(contact);
    request.setDestination(this.localRequestServiceUrl);
    request.setServiceId(serviceId);
    request.setRequestServiceUrl(requestServiceUrl);
    request.setStorageServiceUrl(this.storageServiceUrl);
    request.setState(RequestStatus.ACTIVE.toString());
    final String folderId = requestBean.getDestinationId();
    final InternalRequest savedRequest = requestService.setupInternalRequest(gridId, request, folderId);
    final ServiceRequest serviceRequest = new ServiceRequest();
    serviceRequest.setCatalogId(serviceId);
    serviceRequest.setContact(contact);
    serviceRequest.setDescription(requestDescription);
    serviceRequest.setName(requestName);
    serviceRequest.setRecipientId(gridId);
    serviceRequest.setRequestId(savedRequest.getId());
    serviceRequest.setRequestService(this.localRequestServiceUrl);
    final Map<String, String> attributeMap = requestBean.getInputs();
    final KeyValuePair[] attributes = new KeyValuePair[attributeMap.size()];
    int i = 0;
    for(final Map.Entry<String, String> attributeEntry : attributeMap.entrySet()) {
      LOG.debug("Adding attribute " + attributeEntry.getKey() + " " + attributeEntry.getValue());
      final KeyValuePair pair = new KeyValuePair();
      pair.setKey(attributeEntry.getKey());
      pair.setValue(new String[] {attributeEntry.getValue()});
      attributes[i++] = pair;
    }
    serviceRequest.setAttributes(attributes);
    try {
      tropixRequestService.request(serviceRequest);
    } catch(final Exception e) {
      // TODO: Rollback...
      ExceptionUtils.logAndConvert(LOG, e);
    }
  }

  public void setLocalRequestServiceUrl(final String localRequestServiceUrl) {
    this.localRequestServiceUrl = localRequestServiceUrl;
  }

  public void setStorageServiceUrl(final String storageServiceUrl) {
    this.storageServiceUrl = storageServiceUrl;
  }

  public void setCatalogIdToRequestServiceFunction(final Function<String, String> catalogIdToRequestServiceFunction) {
    this.catalogIdToRequestServiceFunction = catalogIdToRequestServiceFunction;
  }

  public void setRequestService(final RequestService requestService) {
    this.requestService = requestService;
  }

  public void setCredentialSupplier(final Supplier<Credential> credentialSupplier) {
    this.credentialSupplier = credentialSupplier;
  }

  public void setTropixRequestServiceFactory(final GridServiceFactory<TropixRequestService> tropixRequestServiceFactory) {
    this.tropixRequestServiceFactory = tropixRequestServiceFactory;
  }

}
