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

package edu.umn.msi.tropix.labs.requests;

import com.google.common.collect.Multimap;

import edu.umn.msi.tropix.persistence.service.requestid.RequestId;

public interface RequestHandler {
  public static class RequestInfo {
    private RequestId requestId;
    private Multimap<String, String> fields;
    private String catalogServiceId;
    private String name;
    private String description;
    private String contact;
    private String destination;

    public RequestId getRequestId() {
      return requestId;
    }

    public void setRequestId(final RequestId requestId) {
      this.requestId = requestId;
    }

    public Multimap<String, String> getFields() {
      return fields;
    }

    public void setFields(final Multimap<String, String> fields) {
      this.fields = fields;
    }

    public String getCatalogServiceId() {
      return catalogServiceId;
    }

    public void setCatalogServiceId(final String catalogServiceId) {
      this.catalogServiceId = catalogServiceId;
    }

    public String getName() {
      return name;
    }

    public void setName(final String name) {
      this.name = name;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(final String description) {
      this.description = description;
    }

    public String getContact() {
      return contact;
    }

    public void setContact(final String contact) {
      this.contact = contact;
    }

    public String getDestination() {
      return destination;
    }

    public void setDestination(final String destination) {
      this.destination = destination;
    }
  }

  void request(RequestInfo requestInfo);

  String getReport(RequestId requestId);

}
