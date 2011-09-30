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

import info.minnesotapartnership.tropix.request.models.RequestStatus;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "UPDATE_ACTION")
class UpdateRequestAction extends RequestAction {
  @Nonnull
  @Column(name = "STATUS", nullable = false)
  private String status;

  /**
   * Default constructor required by Hibernate, it does not leave the object in a consistent state. DO NOT USE!
   */
  protected UpdateRequestAction() {
    super();
  }

  UpdateRequestAction(@Nonnull final long actionId, @Nonnull final String requestService, @Nonnull final String requestId, @Nonnull final String status) {
    super(actionId, requestService, requestId);
    this.status = status;
  }

  @Nonnull
  public RequestStatus getRequestStatus() {
    return RequestStatus.fromString(status);
  }

}
