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

package edu.umn.msi.tropix.persistence.service.test;

import java.util.Arrays;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.Provider;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.dao.ProviderDao;
import edu.umn.msi.tropix.persistence.service.LabsService;
import edu.umn.msi.tropix.persistence.service.permission.PermissionReport;
import edu.umn.msi.tropix.persistence.service.permission.PermissionSourceType;
import edu.umn.msi.tropix.persistence.service.permission.PermissionType;

public class LabsServiceTest extends ServiceTest {
  @Autowired
  private LabsService labsService;

  @Autowired
  private ProviderDao providerDao;

  private Provider createTestProvider(final User user) {
    final String providerCatalogId = newId();
    final Provider provider = new Provider();
    provider.setUsers(new HashSet<User>());
    provider.setGroups(new HashSet<Group>());
    provider.setCatalogId(providerCatalogId);
    providerDao.saveObject(provider);
    provider.getUsers().add(user);
    providerDao.saveObject(provider);
    return provider;
  }

  @Test
  public void canModify() {
    final User user = createTempUser();
    final String providerCatalogId = newId();
    final Provider provider = new Provider();
    provider.setUsers(new HashSet<User>());
    provider.setCatalogId(providerCatalogId);
    providerDao.saveObject(provider);

    assert !labsService.canModifyProvider(user.getCagridId(), providerCatalogId);

    provider.getUsers().add(user);
    providerDao.saveObject(provider);

    assert labsService.canModifyProvider(user.getCagridId(), providerCatalogId);

  }

  @Test
  public void addUserProviderPermission() {
    final User user = createTempUser(), otherUser = createTempUser();
    final Provider provider = createTestProvider(user);
    final String providerCatalogId = provider.getCatalogId();

    assert !providerDao.canModify(otherUser.getCagridId(), providerCatalogId);
    labsService.addUserProviderPermission(user.getCagridId(), providerCatalogId, otherUser.getCagridId());

    assert providerDao.canModify(otherUser.getCagridId(), providerCatalogId);
  }

  @Test
  public void removeUserProviderPermission() {
    final User user = createTempUser(), otherUser = createTempUser();
    final Provider provider = createTestProvider(user);
    final String providerCatalogId = provider.getCatalogId();

    provider.getUsers().add(otherUser);
    providerDao.saveObject(provider);

    assert providerDao.canModify(otherUser.getCagridId(), providerCatalogId);
    labsService.removeUserProviderPermission(user.getCagridId(), providerCatalogId, otherUser.getCagridId());
    assert !providerDao.canModify(otherUser.getCagridId(), providerCatalogId);
  }

  @Test
  public void removeGroupProviderPermission() {
    final User user = createTempUser(), otherUser = createTempUser();

    final Provider provider = createTestProvider(user);
    final String providerCatalogId = provider.getCatalogId();

    assert !providerDao.canModify(otherUser.getCagridId(), providerCatalogId);

    final Group group = createTempGroup();
    getUserDao().addToGroup(otherUser.getCagridId(), group.getId());
    provider.getGroups().add(group);
    providerDao.saveObject(provider);

    assert providerDao.canModify(otherUser.getCagridId(), providerCatalogId);
    labsService.removeGroupProviderPermission(user.getCagridId(), providerCatalogId, group.getId());
    assert !providerDao.canModify(otherUser.getCagridId(), providerCatalogId);
  }

  @Test
  public void addGroupProviderPermission() {
    final User user = createTempUser(), otherUser = createTempUser();

    final Provider provider = createTestProvider(user);
    final String providerCatalogId = provider.getCatalogId();

    assert !providerDao.canModify(otherUser.getCagridId(), providerCatalogId);

    final Group group = createTempGroup();
    getUserDao().addToGroup(otherUser.getCagridId(), group.getId());

    assert !providerDao.canModify(otherUser.getCagridId(), providerCatalogId);
    labsService.addGroupProviderPermission(user.getCagridId(), providerCatalogId, group.getId());
    assert providerDao.canModify(otherUser.getCagridId(), providerCatalogId);
  }

  @Test
  public void getProviderPermissionReport() {
    final User user = createTempUser();
    final Provider provider = createTestProvider(user);
    final String providerCatalogId = provider.getCatalogId();

    PermissionReport[] reports;
    PermissionReport report;
    reports = labsService.getProviderPermissionReport(providerCatalogId);
    assert reports.length == 1;
    report = getReport(user.getCagridId(), reports);
    assert report.getPermission().equals(PermissionType.Write);
    assert report.getPermissionSource().equals(PermissionSourceType.User);

    final User otherUser = createTempUser();
    provider.getUsers().add(otherUser);
    providerDao.saveObject(provider);
    reports = labsService.getProviderPermissionReport(providerCatalogId);
    assert reports.length == 2;
    report = getReport(otherUser.getCagridId(), reports);
    assert report.getPermission().equals(PermissionType.Write);
    assert report.getPermissionSource().equals(PermissionSourceType.User);

    final Group group = createTempGroup();
    provider.getGroups().add(group);
    providerDao.saveObject(provider);
    reports = labsService.getProviderPermissionReport(providerCatalogId);
    assert reports.length == 3;
    report = getReport(group.getId(), reports);
    assert report.getPermission().equals(PermissionType.Write);
    assert report.getPermissionSource().equals(PermissionSourceType.Group);

  }

  private PermissionReport getReport(final String id, final PermissionReport[] reportArray) {
    PermissionReport report = null;
    for(final PermissionReport possibleReport : reportArray) {
      report = possibleReport;
    }
    return report;
  }

  @Test
  public void create() {
    final String providerCatalogId = newId();
    assert providerDao.loadFromCatalogId(providerCatalogId) == null;
    final User user = createTempUser();
    labsService.createProvider(user.getCagridId(), providerCatalogId);
    assert providerDao.loadFromCatalogId(providerCatalogId) != null;
    assert providerDao.canModify(user.getCagridId(), providerCatalogId);
  }

  @Test
  public void getCatalogProviderIds() {
    final User user = createTempUser();

    String[] ids;
    ids = labsService.getProviderIds(user.getCagridId());
    assert ids.length == 0;

    final Provider p1 = createTestProvider(user);
    ids = labsService.getProviderIds(user.getCagridId());
    assert ids.length == 1 : ids.length;
    assert ids[0].equals(p1.getCatalogId());

    final Provider p2 = createTestProvider(user);
    ids = labsService.getProviderIds(user.getCagridId());
    assert ids.length == 2;
    assert Arrays.asList(ids).contains(p1.getCatalogId());
    assert Arrays.asList(ids).contains(p2.getCatalogId());

    Group group = createTempGroup();
    getUserDao().addToGroup(user.getCagridId(), group.getId());
    p2.getGroups().add(group);
    providerDao.saveObject(p2);

    ids = labsService.getProviderIds(user.getCagridId());
    assert ids.length == 2;
    assert Arrays.asList(ids).contains(p1.getCatalogId());
    assert Arrays.asList(ids).contains(p2.getCatalogId());

    group = createTempGroup();
    getUserDao().addToGroup(user.getCagridId(), group.getId());
    final Provider p3 = createTestProvider(createTempUser());
    p3.getGroups().add(group);
    providerDao.saveObject(p3);

    ids = labsService.getProviderIds(user.getCagridId());
    assert ids.length == 3 : ids.length;
    assert Arrays.asList(ids).contains(p1.getCatalogId());
    assert Arrays.asList(ids).contains(p2.getCatalogId());
    assert Arrays.asList(ids).contains(p3.getCatalogId());

    group = createTempGroup();
    getUserDao().addToGroup(user.getCagridId(), group.getId());
    p3.getGroups().add(group);
    providerDao.saveObject(p3);

    ids = labsService.getProviderIds(user.getCagridId());
    assert ids.length == 3 : ids.length;
    assert Arrays.asList(ids).contains(p1.getCatalogId());
    assert Arrays.asList(ids).contains(p2.getCatalogId());
    assert Arrays.asList(ids).contains(p3.getCatalogId());

  }

}
