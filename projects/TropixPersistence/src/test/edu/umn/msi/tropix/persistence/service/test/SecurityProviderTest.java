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

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.persistence.dao.Dao;
import edu.umn.msi.tropix.persistence.service.security.SecurityProvider;

public class SecurityProviderTest extends ServiceTest {
  @Autowired
  private SecurityProvider securityProvider;

  // This method may seem to have a huge number of redundant tests, but
  // each test a small variation, and I think all of the them are good.
  @Test
  public void readWrite() {
    TropixObject object;

    final User user = createTempUser();
    final Dao<Group> groupDao = getDaoFactory().getDao(Group.class);
    final Group group = new Group();
    group.setUsers(new HashSet<User>());
    groupDao.saveObject(group);

    // Check with no user role,read,write,owner
    object = new TropixObject();
    saveNewTropixObject(object);

    assert !securityProvider.canRead(object.getId(), user.getCagridId());
    assert !securityProvider.canModify(object.getId(), user.getCagridId());

    object = new TropixObject();
    saveNewTropixObject(object);
    getTropixObjectDao().addRole(object.getId(), "read", user);

    assert securityProvider.canRead(object.getId(), user.getCagridId());
    assert !securityProvider.canModify(object.getId(), user.getCagridId());

    object = new TropixObject();
    saveNewTropixObject(object);
    getTropixObjectDao().addRole(object.getId(), "write", user);

    assert securityProvider.canRead(object.getId(), user.getCagridId());
    assert securityProvider.canModify(object.getId(), user.getCagridId());

    object = new TropixObject();
    saveNewTropixObject(object);
    getTropixObjectDao().addRole(object.getId(), "owner", user);

    assert securityProvider.canRead(object.getId(), user.getCagridId());
    assert securityProvider.canModify(object.getId(), user.getCagridId());

    // Check with no user role,read,write,owner
    // with a group
    object = new TropixObject();
    saveNewTropixObject(object);
    getTropixObjectDao().addGroupRole(object.getId(), "write", group);

    assert !securityProvider.canRead(object.getId(), user.getCagridId());
    assert !securityProvider.canModify(object.getId(), user.getCagridId());

    object = new TropixObject();
    saveNewTropixObject(object);
    getTropixObjectDao().addGroupRole(object.getId(), "read", group);
    getTropixObjectDao().addRole(object.getId(), "read", user);

    assert securityProvider.canRead(object.getId(), user.getCagridId());
    assert !securityProvider.canModify(object.getId(), user.getCagridId());

    object = new TropixObject();
    saveNewTropixObject(object);
    getTropixObjectDao().addGroupRole(object.getId(), "write", group);
    getTropixObjectDao().addRole(object.getId(), "write", user);

    assert securityProvider.canRead(object.getId(), user.getCagridId());
    assert securityProvider.canModify(object.getId(), user.getCagridId());

    object = new TropixObject();
    saveNewTropixObject(object);
    getTropixObjectDao().addGroupRole(object.getId(), "read", group);
    getTropixObjectDao().addRole(object.getId(), "owner", user);

    assert securityProvider.canRead(object.getId(), user.getCagridId());
    assert securityProvider.canModify(object.getId(), user.getCagridId());

    // Check with no group role,read,write
    getUserDao().addToGroup(user.getCagridId(), group.getId());

    object = new TropixObject();
    saveNewTropixObject(object);

    assert !securityProvider.canRead(object.getId(), user.getCagridId());
    assert !securityProvider.canModify(object.getId(), user.getCagridId());

    object = new TropixObject();
    saveNewTropixObject(object);
    getTropixObjectDao().addGroupRole(object.getId(), "read", group);

    assert securityProvider.canRead(object.getId(), user.getCagridId());
    assert !securityProvider.canModify(object.getId(), user.getCagridId());

    object = new TropixObject();
    saveNewTropixObject(object);
    getTropixObjectDao().addGroupRole(object.getId(), "write", group);

    assert securityProvider.canRead(object.getId(), user.getCagridId());
    assert securityProvider.canModify(object.getId(), user.getCagridId());

    // No where group has some role somewhere
    final TropixObject otherObject = new TropixObject();
    saveNewTropixObject(otherObject);
    getTropixObjectDao().addGroupRole(otherObject.getId(), "edit", group);

    object = new TropixObject();
    saveNewTropixObject(object);

    assert !securityProvider.canRead(object.getId(), user.getCagridId());
    assert !securityProvider.canModify(object.getId(), user.getCagridId());

    object = new TropixObject();
    saveNewTropixObject(object);
    getTropixObjectDao().addGroupRole(object.getId(), "read", group);

    assert securityProvider.canRead(object.getId(), user.getCagridId());
    assert !securityProvider.canModify(object.getId(), user.getCagridId());

    object = new TropixObject();
    saveNewTropixObject(object);
    getTropixObjectDao().addGroupRole(object.getId(), "write", group);

    assert securityProvider.canRead(object.getId(), user.getCagridId());
    assert securityProvider.canModify(object.getId(), user.getCagridId());

    object = new TropixObject();
    saveNewTropixObject(object);

  }

  @Test
  public void virtualReadWrite() {
    VirtualFolder object;

    final User user = createTempUser();
    final Dao<Group> groupDao = getDaoFactory().getDao(Group.class);
    final Group group = new Group();
    group.setUsers(new HashSet<User>());
    groupDao.saveObject(group);

    // Check with no user role,read,write,owner
    object = new VirtualFolder();
    saveNewTropixObject(object);
    getTropixObjectDao().createVirtualPermission(object.getId(), "read");
    getTropixObjectDao().createVirtualPermission(object.getId(), "write");

    assert !securityProvider.canRead(object.getId(), user.getCagridId());
    assert !securityProvider.canModify(object.getId(), user.getCagridId());

    getTropixObjectDao().addVirtualPermissionUser(object.getId(), "read", user.getCagridId());
    assert securityProvider.canRead(object.getId(), user.getCagridId());
    assert !securityProvider.canModify(object.getId(), user.getCagridId());

    object = new VirtualFolder();
    saveNewTropixObject(object);
    getTropixObjectDao().createVirtualPermission(object.getId(), "read");
    getTropixObjectDao().createVirtualPermission(object.getId(), "write");

    getTropixObjectDao().addVirtualPermissionUser(object.getId(), "write", user.getCagridId());
    assert securityProvider.canRead(object.getId(), user.getCagridId());
    assert securityProvider.canModify(object.getId(), user.getCagridId());

    // Ensure adding read permission for a group you don't belong to doesn't grant access
    object = new VirtualFolder();
    saveNewTropixObject(object);
    getTropixObjectDao().createVirtualPermission(object.getId(), "read");
    getTropixObjectDao().createVirtualPermission(object.getId(), "write");

    getTropixObjectDao().addVirtualPermissionGroup(object.getId(), "read", group.getId());
    assert !securityProvider.canRead(object.getId(), user.getCagridId());
    assert !securityProvider.canModify(object.getId(), user.getCagridId());

    // Ensure adding write permission for a group you don't belong to doesn't grant access
    object = new VirtualFolder();
    saveNewTropixObject(object);
    getTropixObjectDao().createVirtualPermission(object.getId(), "read");
    getTropixObjectDao().createVirtualPermission(object.getId(), "write");

    getTropixObjectDao().addVirtualPermissionGroup(object.getId(), "write", group.getId());
    assert !securityProvider.canRead(object.getId(), user.getCagridId());
    assert !securityProvider.canModify(object.getId(), user.getCagridId());

    group.getUsers().add(user);
    user.getGroups().add(group);
    groupDao.saveObject(group);
    getUserDao().saveOrUpdateUser(user);

    // Ensure adding read permission for a group you belong to adds read but not write access
    object = new VirtualFolder();
    saveNewTropixObject(object);
    getTropixObjectDao().createVirtualPermission(object.getId(), "read");
    getTropixObjectDao().createVirtualPermission(object.getId(), "write");

    getTropixObjectDao().addVirtualPermissionGroup(object.getId(), "read", group.getId());
    assert securityProvider.canRead(object.getId(), user.getCagridId());
    assert !securityProvider.canModify(object.getId(), user.getCagridId());

    // Ensure adding write permission for a group you belong to adds both read and write access
    object = new VirtualFolder();
    saveNewTropixObject(object);
    getTropixObjectDao().createVirtualPermission(object.getId(), "read");
    getTropixObjectDao().createVirtualPermission(object.getId(), "write");

    getTropixObjectDao().addVirtualPermissionGroup(object.getId(), "write", group.getId());
    assert securityProvider.canRead(object.getId(), user.getCagridId());
    assert securityProvider.canModify(object.getId(), user.getCagridId());

  }

  @Test
  public void access() {
    final TropixObject object = new TropixObject();
    saveNewTropixObject(object);
    final User newUser = createTempUser();

    getTropixObjectDao().setOwner(object.getId(), newUser);

    assert securityProvider.canRead(object.getId(), newUser.getCagridId());
    assert securityProvider.canModify(object.getId(), newUser.getCagridId());

    assert !securityProvider.canRead(object.getId(), "23423");
    assert !securityProvider.canModify(object.getId(), newUser.getCagridId() + "2345");
  }

}
