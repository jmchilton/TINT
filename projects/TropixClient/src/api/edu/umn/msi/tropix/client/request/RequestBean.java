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

package edu.umn.msi.tropix.client.request;

import java.io.Serializable;
import java.util.Map;

/**
 * 
 * @author John Chilton
 * 
 */
public class RequestBean implements Serializable {
  private static final long serialVersionUID = 1L;
  private String name;
  private String phone;
  private String email;
  private String destinationId;
  private String catalogId;
  private String serviceId;
  private String requestName;
  private String requestDescription;
  private Map<String, String> inputs;

  public Map<String, String> getInputs() {
    return inputs;
  }

  public void setInputs(final Map<String, String> inputs) {
    this.inputs = inputs;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(final String phone) {
    this.phone = phone;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(final String email) {
    this.email = email;
  }

  public String getDestinationId() {
    return destinationId;
  }

  public void setDestinationId(final String destinationId) {
    this.destinationId = destinationId;
  }

  public String getCatalogId() {
    return catalogId;
  }

  public void setCatalogId(final String catalogId) {
    this.catalogId = catalogId;
  }

  public String getServiceId() {
    return serviceId;
  }

  public void setServiceId(final String serviceId) {
    this.serviceId = serviceId;
  }

  public String getRequestName() {
    return requestName;
  }

  public void setRequestName(final String requestName) {
    this.requestName = requestName;
  }

  public String getRequestDescription() {
    return requestDescription;
  }

  public void setRequestDescription(final String requestDescription) {
    this.requestDescription = requestDescription;
  }

}
