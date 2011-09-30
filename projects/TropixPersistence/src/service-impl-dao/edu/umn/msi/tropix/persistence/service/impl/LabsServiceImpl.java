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

package edu.umn.msi.tropix.persistence.service.impl;

import java.util.HashSet;
import java.util.LinkedList;

import javax.annotation.ManagedBean;
import javax.inject.Named;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.Provider;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.service.LabsService;
import edu.umn.msi.tropix.persistence.service.permission.PermissionReport;
import edu.umn.msi.tropix.persistence.service.permission.PermissionSourceType;
import edu.umn.msi.tropix.persistence.service.permission.PermissionType;

@ManagedBean @Named("labsService")
class LabsServiceImpl extends ServiceBase implements LabsService {

  public void addGroupProviderPermission(final String adderId, final String providerCatalogId, final String groupId) {
    if(!getProviderDao().canModify(adderId, providerCatalogId)) {
      throw new RuntimeException();
    }
    final Provider provider = getProviderDao().loadFromCatalogId(providerCatalogId);
    final Group group = getDaoFactory().getDao(Group.class).load(groupId);
    provider.getGroups().add(group);
    getProviderDao().saveObject(provider);
  }

  public void addUserProviderPermission(final String adderId, final String providerCatalogId, final String userId) {
    if(!getProviderDao().canModify(adderId, providerCatalogId)) {
      throw new RuntimeException();
    }
    final Provider provider = getProviderDao().loadFromCatalogId(providerCatalogId);
    final User user = getUserDao().loadUser(userId);
    provider.getUsers().add(user);
    getProviderDao().saveObject(provider);
  }

  public boolean canModifyProvider(final String userId, final String providerCatalogId) {
    return getProviderDao().canModify(userId, providerCatalogId);
  }

  public PermissionReport[] getProviderPermissionReport(final String providerCatalogId) {
    final Provider provider = getProviderDao().loadFromCatalogId(providerCatalogId);
    final LinkedList<PermissionReport> reports = Lists.newLinkedList();

    for(final User user : provider.getUsers()) {
      final PermissionReport report = new PermissionReport();
      report.setPermission(PermissionType.Write);
      report.setPermissionSource(PermissionSourceType.User);
      final String userId = user.getCagridId();
      report.setId(userId);
      report.setName(userId.substring(userId.lastIndexOf('=') + 1));
      reports.add(report);
    }

    for(final Group group : provider.getGroups()) {
      final PermissionReport report = new PermissionReport();
      report.setPermission(PermissionType.Write);
      report.setPermissionSource(PermissionSourceType.Group);
      report.setId(group.getId());
      report.setName(group.getName());
      reports.add(report);
    }
    return reports.toArray(new PermissionReport[0]);
  }

  public void removeGroupProviderPermission(final String userId, final String providerCatalogId, final String groupId) {
    if(!getProviderDao().canModify(userId, providerCatalogId)) {
      throw new RuntimeException();
    }
    final Provider provider = getProviderDao().loadFromCatalogId(providerCatalogId);
    final Group group = getDaoFactory().getDao(Group.class).load(groupId);
    provider.getGroups().remove(group);
    getProviderDao().saveObject(provider);
  }

  public void removeUserProviderPermission(final String removerId, final String providerCatalogId, final String userId) {
    if(!getProviderDao().canModify(removerId, providerCatalogId)) {
      throw new RuntimeException();
    }
    final Provider provider = getProviderDao().loadFromCatalogId(providerCatalogId);
    final User user = getUserDao().loadUser(userId);
    provider.getUsers().remove(user);
    getProviderDao().saveObject(provider);
  }

  public void createProvider(final String userId, final String providerCatalogId) {
    final Provider provider = new Provider();
    provider.setGroups(new HashSet<Group>());
    provider.setUsers(new HashSet<User>());
    provider.setObjects(new HashSet<TropixObject>());
    provider.setCatalogId(providerCatalogId);
    provider.setRole("write");
    final User user = getUserDao().loadUser(userId);
    provider.getUsers().add(user);
    getProviderDao().saveObject(provider);
  }

  public String[] getProviderIds(final String cagridId) {
    return Iterables.toArray(getProviderDao().getProviderCatalogIds(cagridId), String.class);
  }

}
