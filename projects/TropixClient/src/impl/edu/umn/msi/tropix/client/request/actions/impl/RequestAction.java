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

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * 
 * Represents an action to perform and must be persisted.
 * 
 * @author John Chilton
 * 
 */
@Entity
@Table(name = "ACTION")
@Inheritance(strategy = InheritanceType.JOINED)
abstract class RequestAction {
  @Nonnull
  @Column(name = "SERVICE", nullable = false)
  private String requestService;

  @Nonnull
  @Column(name = "REQUEST_ID", nullable = false)
  private String requestId;

  @Id
  @Nonnull
  @Column(name = "ID", nullable = false)
  private long actionId;

  /**
   * Default constructor required by Hibernate, it does not leave the object in a consistent state. DO NOT USE!
   */
  protected RequestAction() {
  }

  protected RequestAction(@Nonnull final long actionId, @Nonnull final String requestService, @Nonnull final String requestId) {
    this.actionId = actionId;
    this.requestService = requestService;
    this.requestId = requestId;
  }

  @Nonnull
  public long getActionId() {
    return actionId;
  }

  @Nonnull
  public String getRequestService() {
    return requestService;
  }

  @Nonnull
  public String getRequestId() {
    return requestId;
  }

}
