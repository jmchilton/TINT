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

package edu.umn.msi.tropix.persistence.service;

import edu.umn.msi.tropix.persistence.aop.AutoUser;
import edu.umn.msi.tropix.persistence.aop.PersistenceMethod;
import edu.umn.msi.tropix.persistence.aop.UserId;
import edu.umn.msi.tropix.persistence.service.permission.PermissionReport;

public interface LabsService {
  @PersistenceMethod void createProvider(@UserId String userId, String providerCatalogId);

  @PersistenceMethod boolean canModifyProvider(@UserId String userId, String providerCatalogId);

  @PersistenceMethod PermissionReport[] getProviderPermissionReport(@UserId String providerCatalogId);

  @PersistenceMethod void addUserProviderPermission(@UserId String adderId, String providerCatalogId, @AutoUser String userId);

  @PersistenceMethod void removeUserProviderPermission(@UserId String removerId, String providerCatalogId, String userId);

  @PersistenceMethod void addGroupProviderPermission(@UserId String adderId, String providerCatalogId, String groupId);

  @PersistenceMethod void removeGroupProviderPermission(@UserId String userId, String providerCatalogId, String groupId);

  @PersistenceMethod String[] getProviderIds(@UserId String cagridId);

}
