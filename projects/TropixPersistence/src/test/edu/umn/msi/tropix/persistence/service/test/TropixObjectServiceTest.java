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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.TissueSample;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.persistence.dao.Dao;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;
import edu.umn.msi.tropix.persistence.service.permission.PermissionReport;
import edu.umn.msi.tropix.persistence.service.permission.PermissionSourceType;
import edu.umn.msi.tropix.persistence.service.permission.PermissionType;
import edu.umn.msi.tropix.persistence.service.security.SecurityProvider;

// Test ancestor move...
public class TropixObjectServiceTest extends ServiceTest {
  @Autowired
  private TropixObjectService tropixObjectService;

  @Autowired
  private SecurityProvider securityProvider;

  private String fileId;
  private String sampleId;

  @Test
  public void commit() {
    final User user1 = createTempUser();
    final TropixObject o1 = saveNewUncommitted(new TropixObject(), user1);
    final TropixObject o2 = saveNewCommitted(new TropixObject(), user1);
    final TropixObject o3 = saveNewUncommitted(new TropixObject(), user1);
    final TropixObject o4 = saveNewUncommitted(new TropixObject(), user1);
    getTropixObjectDao().addPermissionParent(o2.getId(), o1.getId());
    getTropixObjectDao().addPermissionParent(o4.getId(), o1.getId());
    getTropixObjectDao().addPermissionParent(o3.getId(), o2.getId());

    tropixObjectService.commit(user1.getCagridId(), o1.getId());
    assert getTropixObjectDao().loadTropixObject(o1.getId()).getCommitted();
    assert getTropixObjectDao().loadTropixObject(o2.getId()).getCommitted();
    assert getTropixObjectDao().loadTropixObject(o3.getId()).getCommitted();
    assert getTropixObjectDao().loadTropixObject(o4.getId()).getCommitted();

  }

  @Test
  public void loadObjects() {
    final User user1 = createTempUser(), user2 = createTempUser();

    final TropixObject object1 = new TropixObject();
    object1.setCommitted(true);
    super.saveNewTropixObject(object1, user1);

    final TropixObject object2 = new TropixObject();
    object2.setCommitted(false);
    super.saveNewTropixObject(object2, user1);

    final TropixObject object3 = new TropixObject();
    object3.setCommitted(true);
    object3.setDeletedTime("" + System.currentTimeMillis());
    super.saveNewTropixObject(object3, user1);

    TropixObject[] objects;
    objects = tropixObjectService.load(user1.getCagridId(), new String[] {object1.getId(), object2.getId(), object3.getId()},
        TropixObjectTypeEnum.TROPIX_OBJECT);
    assert objects.length == 1;
    assert objects[0].equals(object1);

    final TropixObject object4 = new TropixObject();
    object4.setCommitted(true);
    super.saveNewTropixObject(object4, user2);
    super.getTropixObjectDao().addRole(object4.getId(), "read", user1);

    objects = tropixObjectService.load(user1.getCagridId(), new String[] {object1.getId(), object2.getId(), object3.getId(), object4.getId()},
        TropixObjectTypeEnum.TROPIX_OBJECT);
    assert objects.length == 2;
    assert objects[0].equals(object1);
    assert objects[1].equals(object4);
  }

  @Test
  public void loadFileWithFileId() {
    final User user1 = createTempUser();
    final String id1 = newId(), id2 = newId();

    final TropixFile file1 = new TropixFile();
    file1.setFileId(id1);
    saveNewTropixObject(file1, user1);
    final TropixFile file2 = new TropixFile();
    file2.setFileId(id2);
    saveNewTropixObject(file2, user1);
    assert securityProvider.canRead(file1.getId(), user1.getCagridId());
    assert tropixObjectService.loadFileWithFileId(user1.getCagridId(), id1).getId().equals(file1.getId());
  }

  @Test(expectedExceptions = RuntimeException.class)
  public void loadFileWithFileIdInvalid() {
    final User user1 = createTempUser(), user2 = createTempUser();
    final String id1 = newId(), id2 = newId();

    final TropixFile file1 = new TropixFile();
    file1.setFileId(id1);
    saveNewTropixObject(file1, user1);
    final TropixFile file2 = new TropixFile();
    file2.setFileId(id2);
    saveNewTropixObject(file2, user1);
    assert tropixObjectService.loadFileWithFileId(user2.getCagridId(), id1).getId().equals(file1.getId());
  }

  @Test(expectedExceptions = RuntimeException.class)
  public void deleteVirtualFolderOnRoot() {
    final VirtualFolder root = createTempRootVirtualFolder();
    final User user1 = createTempUser();
    getTropixObjectDao().setOwner(root.getId(), user1);
    tropixObjectService.deleteVirtualFolder(user1.getCagridId(), root.getId());
  }

  @Test
  public void removeVirtualFolder() {
    final VirtualFolder root = createTempRootVirtualFolder();
    final VirtualFolder child1 = createTempChildVirtualFolder(root);

    final User owner = createTempUser();
    final User reader = createTempUser(), writer = createTempUser();

    final TropixObject rootObject = saveNewCommitted(new TropixObject(), writer);
    final TropixObject childObject = saveNewCommitted(new TropixObject(), writer);

    getTropixObjectDao().addToVirtualFolder(root.getId(), rootObject.getId());
    getTropixObjectDao().addToVirtualFolder(child1.getId(), childObject.getId());

    getTropixObjectDao().setOwner(root.getId(), owner);
    getTropixObjectDao().addVirtualPermissionUser(root.getId(), "read", reader.getCagridId());
    getTropixObjectDao().addVirtualPermissionUser(root.getId(), "write", writer.getCagridId());

    reader.getSharedFolders().add(root);
    getUserDao().saveOrUpdateUser(reader);

    writer.getSharedFolders().add(root);
    getUserDao().saveOrUpdateUser(writer);

    assert reader.getSharedFolders().contains(root);
    tropixObjectService.removeSharedFolder(reader.getCagridId(), root.getId(), false);
    assert !reader.getSharedFolders().contains(root);

    assert writer.getSharedFolders().contains(root);
    tropixObjectService.removeSharedFolder(writer.getCagridId(), root.getId(), false);
    assert !writer.getSharedFolders().contains(root);

    assert root.getContents().contains(rootObject);
    assert child1.getContents().contains(childObject);

    writer.getSharedFolders().add(root);
    getUserDao().saveOrUpdateUser(writer);

    assert root.getContents().contains(rootObject);
    assert child1.getContents().contains(childObject);

    assert writer.getSharedFolders().contains(root);
    tropixObjectService.removeSharedFolder(writer.getCagridId(), root.getId(), true);
    assert !writer.getSharedFolders().contains(root);

    assert !root.getContents().contains(rootObject);
    assert !child1.getContents().contains(childObject);

  }

  @Test
  public void deleteVirtualFolder() {
    final User user1 = createTempUser();
    final VirtualFolder root = super.createTempRootVirtualFolder();
    getTropixObjectDao().addVirtualPermissionUser(root.getId(), "write", user1.getCagridId());

    final VirtualFolder sub1 = new VirtualFolder(), sub2 = new VirtualFolder();
    sub1.setRoot(false);
    sub2.setRoot(false);
    sub1.setContents(new HashSet<TropixObject>());
    sub2.setContents(new HashSet<TropixObject>());
    saveNewTropixObject(sub1);
    saveNewTropixObject(sub2);
    getTropixObjectDao().copyVirtualPermissions(root.getId(), sub1.getId());
    getTropixObjectDao().copyVirtualPermissions(root.getId(), sub2.getId());

    getTropixObjectDao().addToVirtualFolder(root.getId(), sub1.getId());
    getTropixObjectDao().addToVirtualFolder(sub1.getId(), sub2.getId());

    tropixObjectService.deleteVirtualFolder(user1.getCagridId(), sub2.getId());
    assert sub2.getDeletedTime() != null;

    sub2.setDeletedTime(null);
    getTropixObjectDao().saveOrUpdateTropixObject(sub2);

    tropixObjectService.deleteVirtualFolder(user1.getCagridId(), sub1.getId());
    assert sub1.getDeletedTime() != null;
    assert sub2.getDeletedTime() != null;
  }

  @Test
  public void deleteVirtualFolderPermissions() {
    final User user1 = createTempUser(), user2 = createTempUser();
    final VirtualFolder root = super.createTempRootVirtualFolder();
    getTropixObjectDao().addVirtualPermissionUser(root.getId(), "write", user1.getCagridId());
    getTropixObjectDao().addVirtualPermissionUser(root.getId(), "read", user2.getCagridId());

    final VirtualFolder sub1 = new VirtualFolder(), sub2 = new VirtualFolder();
    sub1.setRoot(false);
    sub2.setRoot(false);
    sub1.setContents(new HashSet<TropixObject>());
    sub2.setContents(new HashSet<TropixObject>());
    saveNewTropixObject(sub1);
    saveNewTropixObject(sub2);
    getTropixObjectDao().copyVirtualPermissions(root.getId(), sub1.getId());
    getTropixObjectDao().copyVirtualPermissions(sub1.getId(), sub2.getId());

    final TropixObject object = new TropixObject();
    object.setCommitted(true);
    saveNewTropixObject(object);

    assert !securityProvider.canRead(object.getId(), user2.getCagridId());

    getTropixObjectDao().addToVirtualFolder(sub2.getId(), object.getId());
    getTropixObjectDao().addToVirtualFolder(sub1.getId(), object.getId());
    getTropixObjectDao().copyVirtualPermissions(sub1.getId(), object.getId());
    getTropixObjectDao().copyVirtualPermissions(sub2.getId(), object.getId());

    assert securityProvider.canRead(object.getId(), user2.getCagridId());
    tropixObjectService.deleteVirtualFolder(user1.getCagridId(), sub2.getId());
    assert securityProvider.canRead(object.getId(), user2.getCagridId());
    tropixObjectService.deleteVirtualFolder(user1.getCagridId(), sub1.getId());
    assert !securityProvider.canRead(object.getId(), user2.getCagridId());
  }

  @Test
  public void getOwnerId() {
    final User user1 = createTempUser();

    final TropixObject object = new TropixObject();
    object.setName("Moo");
    saveNewTropixObject(object);
    final String objectId = object.getId();
    getTropixObjectDao().setOwner(objectId, user1);

    assert tropixObjectService.getOwnerId(object.getId()).equals(user1.getCagridId());

  }
  
  @Test
  public void addToGroupSharedFolder() {
    final User user = createTempUser();
    final Group group = createTempGroup(user);
    final VirtualFolder root = createTempRootVirtualFolder();
    final Collection<VirtualFolder> sharedFolders = group.getSharedFolders();
    assert sharedFolders == null || sharedFolders.isEmpty();
    getTropixObjectDao().addVirtualPermissionUser(root.getId(), "read", user.getCagridId());
    tropixObjectService.addGroupSharedFolder(user.getCagridId(), group.getId(), root.getId());
    assert group.getSharedFolders().contains(root);
  }


  @Test
  public void addSharedFolder() {
    final User user = createTempUser();
    final VirtualFolder root = createTempRootVirtualFolder();

    getTropixObjectDao().addVirtualPermissionUser(root.getId(), "read", user.getCagridId());
    tropixObjectService.addSharedFolder(user.getCagridId(), root.getId());

    assert getUserDao().loadUser(user.getCagridId()).getSharedFolders().contains(root);
  }
  
  @Test
  public void hideGroupSharedFolder() {
    final User user = createTempUser();
    final Group group = createTempGroup(user);
    final VirtualFolder root = createTempRootVirtualFolder();
    getTropixObjectDao().addVirtualPermissionUser(root.getId(), "read", user.getCagridId());
    group.getSharedFolders().add(root);
    
    tropixObjectService.hideGroupSharedFolder(user.getCagridId(), group.getId(), root.getId());
    assert !group.getSharedFolders().contains(root);    
  }
  
  @Test
  public void hideSharedFolder() {
    final User user = createTempUser();
    final VirtualFolder root = createTempRootVirtualFolder();
    getTropixObjectDao().addVirtualPermissionUser(root.getId(), "read", user.getCagridId());
    user.getSharedFolders().add(root);
    
    tropixObjectService.hideSharedFolder(user.getCagridId(), root.getId());
    assert !getUserDao().loadUser(user.getCagridId()).getSharedFolders().contains(root);
  }

  @Test
  public void getRoot() {
    final User user1 = createTempUser();
    final VirtualFolder root = super.createTempRootVirtualFolder();
    getTropixObjectDao().addVirtualPermissionUser(root.getId(), "read", user1.getCagridId());

    final VirtualFolder sub1 = new VirtualFolder(), sub2 = new VirtualFolder();
    sub1.setRoot(false);
    sub2.setRoot(false);
    sub1.setContents(new HashSet<TropixObject>());
    sub2.setContents(new HashSet<TropixObject>());
    saveNewTropixObject(sub1);
    saveNewTropixObject(sub2);
    getTropixObjectDao().copyVirtualPermissions(root.getId(), sub1.getId());
    getTropixObjectDao().copyVirtualPermissions(sub1.getId(), sub2.getId());

    for(final VirtualFolder folder : Arrays.asList(root, sub1, sub2)) {
      assert tropixObjectService.getRoot(user1.getCagridId(), folder.getId()).getId().equals(root.getId());
    }
  }

  @Test
  public void getSharedFoldersGroup() {
    final User user1 = createTempUser(), user2 = createTempUser();

    final VirtualFolder root = super.createTempRootVirtualFolder();
    getTropixObjectDao().setOwner(root.getId(), user1);

    final Group group = getNewGroup();
    addUserToGroup(user2, group);

    assert tropixObjectService.getSharedFolders(user2.getCagridId()).length == 0;
    getTropixObjectDao().addVirtualPermissionGroup(root.getId(), "read", group.getId());
    getUserDao().addToGroup(user2.getCagridId(), group.getId());
    assert tropixObjectService.getSharedFolders(user2.getCagridId()).length > 0;

  }

  @Test
  public void getSharedFolders() {
    final User user1 = createTempUser(), user2 = createTempUser();
    final VirtualFolder root = new VirtualFolder();
    root.setRoot(true);
    root.setContents(new HashSet<TropixObject>());
    saveNewTropixObject(root);
    getTropixObjectDao().setOwner(root.getId(), user1);
    getTropixObjectDao().createVirtualPermission(root.getId(), "read");
    getTropixObjectDao().createVirtualPermission(root.getId(), "write");

    assert tropixObjectService.getSharedFolders(user2.getCagridId()).length == 0;
    getTropixObjectDao().addVirtualPermissionUser(root.getId(), "read", user2.getCagridId());
    assert tropixObjectService.getSharedFolders(user2.getCagridId()).length > 0;

  }

  @Test
  public void addRemoveUserPermissionsRecursively() {
    final User user1 = createTempUser(), user2 = createTempUser();

    final TropixObject object = new TropixObject();
    object.setName("Moo");
    saveNewTropixObject(object);
    final String objectId = object.getId();
    getTropixObjectDao().setOwner(objectId, user1);

    final TropixObject child1 = new TropixObject();
    object.setName("Child1");
    saveNewTropixObject(child1);
    getTropixObjectDao().setOwner(child1.getId(), user1);
    final TropixObject child2 = new TropixObject();
    object.setName("Child2");
    saveNewTropixObject(child2);
    getTropixObjectDao().setOwner(child2.getId(), user1);

    final String child1Id = child1.getId(), child2Id = child2.getId();
    getTropixObjectDao().addPermissionParent(child1Id, object.getId());
    getTropixObjectDao().addPermissionParent(child2Id, object.getId());

    // Add read permission for user 2
    tropixObjectService.addPermissionForUser(user1.getCagridId(), objectId, user2.getCagridId(), PermissionType.Read);
    assert securityProvider.canRead(child1Id, user2.getCagridId()); // make sure it granted read permission
    assert !securityProvider.canModify(child1Id, user2.getCagridId()); // make sure didn't add write permission
    assert securityProvider.canRead(child2Id, user2.getCagridId()); // make sure it granted read permission
    assert !securityProvider.canModify(child2Id, user2.getCagridId()); // make sure didn't add write permission

    // Try removing non-existent write permissions, ensure it doesn't break read permissions
    tropixObjectService.removePermissionForUser(user1.getCagridId(), objectId, user2.getCagridId(), PermissionType.Write);
    assert securityProvider.canRead(child1Id, user2.getCagridId()); // make sure it granted read permission
    assert !securityProvider.canModify(child1Id, user2.getCagridId()); // make sure didn't add write permission
    assert securityProvider.canRead(child2Id, user2.getCagridId()); // make sure it granted read permission
    assert !securityProvider.canModify(child2Id, user2.getCagridId()); // make sure didn't add write permission

    // Try removing read permissions
    tropixObjectService.removePermissionForUser(user1.getCagridId(), objectId, user2.getCagridId(), PermissionType.Read);
    assert !securityProvider.canRead(child1Id, user2.getCagridId()); // make sure it removed read permission
    assert !securityProvider.canModify(child1Id, user2.getCagridId()); // make sure didn't add write permission
    assert !securityProvider.canRead(child2Id, user2.getCagridId()); // make sure it removed read permission
    assert !securityProvider.canModify(child2Id, user2.getCagridId()); // make sure didn't add write permission

    // Add write permission for user 2
    tropixObjectService.addPermissionForUser(user1.getCagridId(), objectId, user2.getCagridId(), PermissionType.Write);
    assert securityProvider.canRead(child1Id, user2.getCagridId()); // make sure it granted read permission
    assert securityProvider.canModify(child1Id, user2.getCagridId()); // make sure it granted write permission
    assert securityProvider.canRead(child2Id, user2.getCagridId()); // make sure it granted read permission
    assert securityProvider.canModify(child2Id, user2.getCagridId()); // make sure it granted write permission

    // Remove write permission (read should remain...)
    tropixObjectService.removePermissionForUser(user1.getCagridId(), objectId, user2.getCagridId(), PermissionType.Write);
    assert securityProvider.canRead(child1Id, user2.getCagridId()); // make sure it granted read permission
    assert !securityProvider.canModify(child1Id, user2.getCagridId()); // make sure didn't add write permission
    assert securityProvider.canRead(child2Id, user2.getCagridId()); // make sure it granted read permission
    assert !securityProvider.canModify(child2Id, user2.getCagridId()); // make sure didn't add write permission

    // Remove read permission
    tropixObjectService.removePermissionForUser(user1.getCagridId(), objectId, user2.getCagridId(), PermissionType.Read);
    assert !securityProvider.canRead(child1Id, user2.getCagridId()); // make sure remove read permission
    assert !securityProvider.canModify(child1Id, user2.getCagridId()); // make sure it removed write permission
    assert !securityProvider.canRead(child2Id, user2.getCagridId()); // make sure remove read permission
    assert !securityProvider.canModify(child2Id, user2.getCagridId()); // make sure it removed write permission

    // Add write permission for user 2 (this time we will remove Read outright)
    tropixObjectService.addPermissionForUser(user1.getCagridId(), objectId, user2.getCagridId(), PermissionType.Write);
    assert securityProvider.canRead(child1Id, user2.getCagridId()); // make sure it granted read permission
    assert securityProvider.canModify(child1Id, user2.getCagridId()); // make sure it granted write permission
    assert securityProvider.canRead(child2Id, user2.getCagridId()); // make sure it granted read permission
    assert securityProvider.canModify(child2Id, user2.getCagridId()); // make sure it granted write permission

    // Remove read permission
    tropixObjectService.removePermissionForUser(user1.getCagridId(), objectId, user2.getCagridId(), PermissionType.Read);
    assert !securityProvider.canRead(child1Id, user2.getCagridId()); // make sure remove read permission
    assert !securityProvider.canModify(child1Id, user2.getCagridId()); // make sure it removed write permission
    assert !securityProvider.canRead(child2Id, user2.getCagridId()); // make sure remove read permission
    assert !securityProvider.canModify(child2Id, user2.getCagridId()); // make sure it removed write permission

  }

  @Test
  public void addRemoveUserPermissions() {
    final User user1 = createTempUser(), user2 = createTempUser();

    final TropixObject object = new TropixObject();
    object.setName("Moo");
    saveNewTropixObject(object);
    final String objectId = object.getId();
    getTropixObjectDao().setOwner(objectId, user1);

    // Add read permission for user 2
    tropixObjectService.addPermissionForUser(user1.getCagridId(), objectId, user2.getCagridId(), PermissionType.Read);

    // Try removing non-existent write permissions, ensure it doesn't break read permissions
    tropixObjectService.removePermissionForUser(user1.getCagridId(), objectId, user2.getCagridId(), PermissionType.Write);
    assert securityProvider.canRead(objectId, user2.getCagridId()); // make sure it granted read permission
    assert !securityProvider.canModify(objectId, user2.getCagridId()); // make sure didn't add write permission
    assert securityProvider.canModify(objectId, user1.getCagridId()); // make sure didn't affect permission for others

    // Try removing read permissions
    tropixObjectService.removePermissionForUser(user1.getCagridId(), objectId, user2.getCagridId(), PermissionType.Read);
    assert !securityProvider.canRead(objectId, user2.getCagridId()); // make sure it removed read permission
    assert !securityProvider.canModify(objectId, user2.getCagridId()); // make sure didn't add write permission
    assert securityProvider.canModify(objectId, user1.getCagridId()); // make sure didn't affect permission for others

    // Add write permission for user 2
    tropixObjectService.addPermissionForUser(user1.getCagridId(), objectId, user2.getCagridId(), PermissionType.Write);
    assert securityProvider.canRead(objectId, user2.getCagridId()); // make sure it granted read permission
    assert securityProvider.canModify(objectId, user2.getCagridId()); // make sure it granted write permission
    assert securityProvider.canModify(objectId, user1.getCagridId()); // make sure didn't affect permission for others

    // Remove write permission (read should remain...)
    tropixObjectService.removePermissionForUser(user1.getCagridId(), objectId, user2.getCagridId(), PermissionType.Write);
    assert securityProvider.canRead(objectId, user2.getCagridId()); // make sure it granted read permission
    assert !securityProvider.canModify(objectId, user2.getCagridId()); // make sure didn't add write permission
    assert securityProvider.canModify(objectId, user1.getCagridId()); // make sure didn't affect permission for others

    // Remove read permission
    tropixObjectService.removePermissionForUser(user1.getCagridId(), objectId, user2.getCagridId(), PermissionType.Read);
    assert !securityProvider.canRead(objectId, user2.getCagridId()); // make sure remove read permission
    assert !securityProvider.canModify(objectId, user2.getCagridId()); // make sure it removed write permission
    assert securityProvider.canModify(objectId, user1.getCagridId()); // make sure didn't affect permission for others

    // Add write permission for user 2 (this time we will remove Read outright)
    tropixObjectService.addPermissionForUser(user1.getCagridId(), objectId, user2.getCagridId(), PermissionType.Write);
    assert securityProvider.canRead(objectId, user2.getCagridId()); // make sure it granted read permission
    assert securityProvider.canModify(objectId, user2.getCagridId()); // make sure it granted write permission
    assert securityProvider.canModify(objectId, user1.getCagridId()); // make sure didn't affect permission for others

    // Remove read permission
    tropixObjectService.removePermissionForUser(user1.getCagridId(), objectId, user2.getCagridId(), PermissionType.Read);
    assert !securityProvider.canRead(objectId, user2.getCagridId()); // make sure remove read permission
    assert !securityProvider.canModify(objectId, user2.getCagridId()); // make sure it removed write permission
    assert securityProvider.canModify(objectId, user1.getCagridId()); // make sure didn't affect permission for others
  }

  @Test
  public void addRemoveGroupPermissions() {
    final User user1 = createTempUser(), user2 = createTempUser();

    final TropixObject object = new TropixObject();
    object.setName("Moo");
    saveNewTropixObject(object);
    final String objectId = object.getId();
    getTropixObjectDao().setOwner(objectId, user1);

    final Group group = getNewGroup();
    addUserToGroup(user2, group);

    // Add read permission for group
    tropixObjectService.addPermissionForGroup(user1.getCagridId(), objectId, group.getId(), PermissionType.Read);

    // Try removing non-existent write permissions, ensure it doesn't break read permissions
    tropixObjectService.removePermissionForGroup(user1.getCagridId(), objectId, group.getId(), PermissionType.Write);
    assert securityProvider.canRead(objectId, user2.getCagridId()); // make sure it granted read permission
    assert !securityProvider.canModify(objectId, user2.getCagridId()); // make sure didn't add write permission
    assert securityProvider.canModify(objectId, user1.getCagridId()); // make sure didn't affect permission for others

    // Try removing read permissions
    tropixObjectService.removePermissionForGroup(user1.getCagridId(), objectId, group.getId(), PermissionType.Read);
    assert !securityProvider.canRead(objectId, user2.getCagridId()); // make sure it removed read permission
    assert !securityProvider.canModify(objectId, user2.getCagridId()); // make sure didn't add write permission
    assert securityProvider.canModify(objectId, user1.getCagridId()); // make sure didn't affect permission for others

    // Add write permission for group
    tropixObjectService.addPermissionForGroup(user1.getCagridId(), objectId, group.getId(), PermissionType.Write);
    assert securityProvider.canRead(objectId, user2.getCagridId()); // make sure it granted read permission
    assert securityProvider.canModify(objectId, user2.getCagridId()); // make sure it granted write permission
    assert securityProvider.canModify(objectId, user1.getCagridId()); // make sure didn't affect permission for others

    // Remove write permission (read should remain...)
    tropixObjectService.removePermissionForGroup(user1.getCagridId(), objectId, group.getId(), PermissionType.Write);
    assert securityProvider.canRead(objectId, user2.getCagridId()); // make sure it granted read permission
    assert !securityProvider.canModify(objectId, user2.getCagridId()); // make sure didn't add write permission
    assert securityProvider.canModify(objectId, user1.getCagridId()); // make sure didn't affect permission for others

    // Remove read permission
    tropixObjectService.removePermissionForGroup(user1.getCagridId(), objectId, group.getId(), PermissionType.Read);
    assert !securityProvider.canRead(objectId, user2.getCagridId()); // make sure remove read permission
    assert !securityProvider.canModify(objectId, user2.getCagridId()); // make sure it removed write permission
    assert securityProvider.canModify(objectId, user1.getCagridId()); // make sure didn't affect permission for others

    // Add write permission for group (this time we will remove Read outright)
    tropixObjectService.addPermissionForGroup(user1.getCagridId(), objectId, group.getId(), PermissionType.Write);
    assert securityProvider.canRead(objectId, user2.getCagridId()); // make sure it granted read permission
    assert securityProvider.canModify(objectId, user2.getCagridId()); // make sure it granted write permission
    assert securityProvider.canModify(objectId, user1.getCagridId()); // make sure didn't affect permission for others

    // Remove read permission
    tropixObjectService.removePermissionForGroup(user1.getCagridId(), objectId, group.getId(), PermissionType.Read);
    assert !securityProvider.canRead(objectId, user2.getCagridId()); // make sure remove read permission
    assert !securityProvider.canModify(objectId, user2.getCagridId()); // make sure it removed write permission
    assert securityProvider.canModify(objectId, user1.getCagridId()); // make sure didn't affect permission for others

  }

  @Test
  public void addGroupPermission() {
    final User user1 = createTempUser(), user2 = createTempUser();

    final Group group = getNewGroup();

    final TropixObject object = new TropixObject();
    object.setName("Moo");
    saveNewTropixObject(object);
    final String objectId = object.getId();
    getTropixObjectDao().setOwner(objectId, user1);

    // Make sure user2 has no permission to object
    assert !securityProvider.canRead(objectId, user2.getCagridId());
    assert !securityProvider.canModify(objectId, user2.getCagridId());

    // Add read permission for group, make sure user 2 is unaffected
    tropixObjectService.addPermissionForGroup(user1.getCagridId(), objectId, group.getId(), PermissionType.Read);
    assert !securityProvider.canRead(objectId, user2.getCagridId()); // make sure it didn't granted read permission to user not in group
    assert !securityProvider.canModify(objectId, user2.getCagridId()); // make sure didn't add write permission
    assert securityProvider.canModify(objectId, user1.getCagridId()); // make sure didn't affect permission for others

    // Add user2 to group and make sure can read but not write
    this.addUserToGroup(user2, group);
    assert securityProvider.canRead(objectId, user2.getCagridId()); // make sure read permission unaffected
    assert !securityProvider.canModify(objectId, user2.getCagridId()); // make sure didn't add write permission
    assert securityProvider.canModify(objectId, user1.getCagridId()); // make sure didn't affect permission for others

    // Add write permission
    tropixObjectService.addPermissionForGroup(user1.getCagridId(), objectId, group.getId(), PermissionType.Write);
    assert securityProvider.canRead(objectId, user2.getCagridId()); // make sure read permission unaffected
    assert securityProvider.canModify(objectId, user2.getCagridId()); // make sure added write permission
    assert securityProvider.canModify(objectId, user1.getCagridId()); // make sure didn't affect permission for others

    // Try readding read permission (make sure it doesn't affect anything)
    tropixObjectService.addPermissionForGroup(user1.getCagridId(), objectId, group.getId(), PermissionType.Read);
    assert securityProvider.canRead(objectId, user2.getCagridId()); // make sure read permission unaffected
    assert securityProvider.canModify(objectId, user2.getCagridId()); // make sure didn't add write permission
    assert securityProvider.canModify(objectId, user1.getCagridId()); // make sure didn't affect permission for others

    // Try readding write permission (make sure it doesn't affect anything)
    tropixObjectService.addPermissionForGroup(user1.getCagridId(), objectId, group.getId(), PermissionType.Write);
    assert securityProvider.canRead(objectId, user2.getCagridId()); // make sure read permission unaffected
    assert securityProvider.canModify(objectId, user2.getCagridId()); // make sure didn't add write permission
    assert securityProvider.canModify(objectId, user1.getCagridId()); // make sure didn't affect permission for others

  }

  @Test
  public void addAutoUserPermission() {
    final User user1 = createTempUser();
    final String user2Id = newId();

    final TropixObject object = new TropixObject();
    object.setName("Moo");
    saveNewTropixObject(object);
    final String objectId = object.getId();
    getTropixObjectDao().setOwner(objectId, user1);

    // Make sure user2 has no permission to object
    assert !securityProvider.canRead(objectId, user2Id);
    assert !securityProvider.canModify(objectId, user2Id);

    tropixObjectService.addPermissionForUser(user1.getCagridId(), objectId, user2Id, PermissionType.Read);
    assert securityProvider.canRead(objectId, user2Id); // make sure it granted read permission
    assert !securityProvider.canModify(objectId, user2Id); // make sure didn't add write permission
  }

  @Test
  public void addUserPermission() {
    final User user1 = createTempUser(), user2 = createTempUser();

    final TropixObject object = new TropixObject();
    object.setName("Moo");
    saveNewTropixObject(object);
    final String objectId = object.getId();
    getTropixObjectDao().setOwner(objectId, user1);

    // Make sure user2 has no permission to object
    assert !securityProvider.canRead(objectId, user2.getCagridId());
    assert !securityProvider.canModify(objectId, user2.getCagridId());

    // Add read permission for user 2
    tropixObjectService.addPermissionForUser(user1.getCagridId(), objectId, user2.getCagridId(), PermissionType.Read);
    assert securityProvider.canRead(objectId, user2.getCagridId()); // make sure it granted read permission
    assert !securityProvider.canModify(objectId, user2.getCagridId()); // make sure didn't add write permission
    assert securityProvider.canModify(objectId, user1.getCagridId()); // make sure didn't affect permission for others

    // Try readding read permission
    tropixObjectService.addPermissionForUser(user1.getCagridId(), objectId, user2.getCagridId(), PermissionType.Read);
    assert securityProvider.canRead(objectId, user2.getCagridId()); // make sure read permission unaffected
    assert !securityProvider.canModify(objectId, user2.getCagridId()); // make sure didn't add write permission
    assert securityProvider.canModify(objectId, user1.getCagridId()); // make sure didn't affect permission for others

    // Add write permission
    tropixObjectService.addPermissionForUser(user1.getCagridId(), objectId, user2.getCagridId(), PermissionType.Write);
    assert securityProvider.canRead(objectId, user2.getCagridId()); // make sure read permission unaffected
    assert securityProvider.canModify(objectId, user2.getCagridId()); // make sure added write permission
    assert securityProvider.canModify(objectId, user1.getCagridId()); // make sure didn't affect permission for others

    // Try readding read permission (make sure it doesn't affect anything)
    tropixObjectService.addPermissionForUser(user1.getCagridId(), objectId, user2.getCagridId(), PermissionType.Read);
    assert securityProvider.canRead(objectId, user2.getCagridId()); // make sure read permission unaffected
    assert securityProvider.canModify(objectId, user2.getCagridId()); // make sure didn't add write permission
    assert securityProvider.canModify(objectId, user1.getCagridId()); // make sure didn't affect permission for others

    // Try readding write permission (make sure it doesn't affect anything)
    tropixObjectService.addPermissionForUser(user1.getCagridId(), objectId, user2.getCagridId(), PermissionType.Write);
    assert securityProvider.canRead(objectId, user2.getCagridId()); // make sure read permission unaffected
    assert securityProvider.canModify(objectId, user2.getCagridId()); // make sure didn't add write permission
    assert securityProvider.canModify(objectId, user1.getCagridId()); // make sure didn't affect permission for others

  }

  @Test
  public void addGroupVirtualPermission() {
    final User user1 = createTempUser(), user2 = createTempUser();

    final Group group = getNewGroup();

    final VirtualFolder root = new VirtualFolder();
    saveNewTropixObject(root);
    getTropixObjectDao().setOwner(root.getId(), user1);
    getTropixObjectDao().createVirtualPermission(root.getId(), "read");
    getTropixObjectDao().createVirtualPermission(root.getId(), "write");

    assert !securityProvider.canRead(root.getId(), user2.getCagridId());
    assert !securityProvider.canModify(root.getId(), user2.getCagridId());

    tropixObjectService.addVirtualPermissionForGroup(user1.getCagridId(), root.getId(), group.getId(), PermissionType.Read);
    assert !securityProvider.canRead(root.getId(), user2.getCagridId());
    assert !securityProvider.canModify(root.getId(), user2.getCagridId());

    // Add user2 to group and make sure can read but not write
    addUserToGroup(user2, group);

    assert securityProvider.canRead(root.getId(), user2.getCagridId());
    assert !securityProvider.canModify(root.getId(), user2.getCagridId());

    tropixObjectService.addVirtualPermissionForGroup(user1.getCagridId(), root.getId(), group.getId(), PermissionType.Write);
    assert securityProvider.canRead(root.getId(), user2.getCagridId());
    assert securityProvider.canModify(root.getId(), user2.getCagridId());

    tropixObjectService.addVirtualPermissionForGroup(user1.getCagridId(), root.getId(), group.getId(), PermissionType.Read);
    assert securityProvider.canRead(root.getId(), user2.getCagridId());
    assert securityProvider.canModify(root.getId(), user2.getCagridId());
  }

  @Test
  public void addAutoUserVirtualPermission() {
    final User user1 = createTempUser();
    final String user2Id = newId();

    final VirtualFolder root = new VirtualFolder();
    saveNewTropixObject(root);
    getTropixObjectDao().setOwner(root.getId(), user1);
    getTropixObjectDao().createVirtualPermission(root.getId(), "read");
    getTropixObjectDao().createVirtualPermission(root.getId(), "write");

    assert !securityProvider.canRead(root.getId(), user2Id);
    assert !securityProvider.canModify(root.getId(), user2Id);

    tropixObjectService.addVirtualPermissionForUser(user1.getCagridId(), root.getId(), user2Id, PermissionType.Read);
    assert securityProvider.canRead(root.getId(), user2Id);
    assert !securityProvider.canModify(root.getId(), user2Id);
  }

  @Test
  public void addUserVirtualPermission() {
    final User user1 = createTempUser(), user2 = createTempUser();

    final VirtualFolder root = new VirtualFolder();
    saveNewTropixObject(root);
    getTropixObjectDao().setOwner(root.getId(), user1);
    getTropixObjectDao().createVirtualPermission(root.getId(), "read");
    getTropixObjectDao().createVirtualPermission(root.getId(), "write");

    assert !securityProvider.canRead(root.getId(), user2.getCagridId());
    assert !securityProvider.canModify(root.getId(), user2.getCagridId());

    tropixObjectService.addVirtualPermissionForUser(user1.getCagridId(), root.getId(), user2.getCagridId(), PermissionType.Read);
    assert securityProvider.canRead(root.getId(), user2.getCagridId());
    assert !securityProvider.canModify(root.getId(), user2.getCagridId());

    tropixObjectService.addVirtualPermissionForUser(user1.getCagridId(), root.getId(), user2.getCagridId(), PermissionType.Read);
    assert securityProvider.canRead(root.getId(), user2.getCagridId());
    assert !securityProvider.canModify(root.getId(), user2.getCagridId());

    tropixObjectService.addVirtualPermissionForUser(user1.getCagridId(), root.getId(), user2.getCagridId(), PermissionType.Write);
    assert securityProvider.canRead(root.getId(), user2.getCagridId());
    assert securityProvider.canModify(root.getId(), user2.getCagridId());

    tropixObjectService.addVirtualPermissionForUser(user1.getCagridId(), root.getId(), user2.getCagridId(), PermissionType.Read);
    assert securityProvider.canRead(root.getId(), user2.getCagridId());
    assert securityProvider.canModify(root.getId(), user2.getCagridId());
  }

  @Test
  public void removeFromSharedFolder() {
    final User user1 = createTempUser(), user2 = createTempUser(), user3 = createTempUser();
    final VirtualFolder root = new VirtualFolder();
    root.setContents(new HashSet<TropixObject>());
    saveNewTropixObject(root);
    getTropixObjectDao().setOwner(root.getId(), user1);
    getTropixObjectDao().createVirtualPermission(root.getId(), "read");
    getTropixObjectDao().createVirtualPermission(root.getId(), "write");

    final TropixObject object = new TropixObject();
    saveNewTropixObject(object);
    getTropixObjectDao().setOwner(object.getId(), user2);
    getTropixObjectDao().addToVirtualFolder(root.getId(), object.getId());
    getTropixObjectDao().copyVirtualPermissions(root.getId(), object.getId());

    getTropixObjectDao().addVirtualPermissionUser(root.getId(), "read", user3.getCagridId());
    assert securityProvider.canRead(object.getId(), user3.getCagridId());
    tropixObjectService.removeFromSharedFolder(user1.getCagridId(), root.getId(), object.getId());
    assert !securityProvider.canRead(object.getId(), user3.getCagridId());

    assert root.getContents().isEmpty();
  }

  @Test
  public void removeFromSharedFolderRecursive() {
    final User user1 = createTempUser(), user2 = createTempUser(), user3 = createTempUser();
    final VirtualFolder root = new VirtualFolder();
    root.setContents(new HashSet<TropixObject>());
    saveNewTropixObject(root);
    getTropixObjectDao().setOwner(root.getId(), user1);
    getTropixObjectDao().createVirtualPermission(root.getId(), "read");
    getTropixObjectDao().createVirtualPermission(root.getId(), "write");

    final TropixObject object = new TropixObject(), childObject = new TropixObject();
    saveNewTropixObject(object);
    saveNewTropixObject(childObject);
    getTropixObjectDao().setOwner(object.getId(), user2);
    getTropixObjectDao().setOwner(childObject.getId(), user2);
    getTropixObjectDao().addPermissionParent(childObject.getId(), object.getId());
    getTropixObjectDao().addToVirtualFolder(root.getId(), object.getId());
    getTropixObjectDao().copyVirtualPermissions(root.getId(), object.getId());
    getTropixObjectDao().copyVirtualPermissions(root.getId(), childObject.getId());

    getTropixObjectDao().addVirtualPermissionUser(root.getId(), "read", user3.getCagridId());

    assert securityProvider.canRead(childObject.getId(), user3.getCagridId());
    tropixObjectService.removeFromSharedFolder(user1.getCagridId(), root.getId(), object.getId());
    assert !securityProvider.canRead(childObject.getId(), user3.getCagridId());
  }

  @Test
  public void removeFromSharedFolderMultipleRecursive() {
    final User user1 = createTempUser(), user2 = createTempUser(), user3 = createTempUser();
    final VirtualFolder root = new VirtualFolder();
    root.setContents(new HashSet<TropixObject>());
    saveNewTropixObject(root);
    getTropixObjectDao().setOwner(root.getId(), user1);
    getTropixObjectDao().createVirtualPermission(root.getId(), "read");
    getTropixObjectDao().createVirtualPermission(root.getId(), "write");

    final VirtualFolder sub1 = new VirtualFolder(), sub2 = new VirtualFolder();
    sub1.setContents(new HashSet<TropixObject>());
    sub2.setContents(new HashSet<TropixObject>());
    saveNewTropixObject(sub1);
    saveNewTropixObject(sub2);
    getTropixObjectDao().addToVirtualFolder(root.getId(), sub1.getId());
    getTropixObjectDao().copyVirtualPermissions(root.getId(), sub1.getId());
    getTropixObjectDao().addToVirtualFolder(sub1.getId(), sub2.getId());
    getTropixObjectDao().copyVirtualPermissions(sub1.getId(), sub2.getId());

    final TropixObject object = new TropixObject(), childObject = new TropixObject();
    saveNewTropixObject(object);
    saveNewTropixObject(childObject);
    getTropixObjectDao().setOwner(object.getId(), user2);
    getTropixObjectDao().setOwner(childObject.getId(), user2);
    getTropixObjectDao().addPermissionParent(childObject.getId(), object.getId());
    getTropixObjectDao().addToVirtualFolder(root.getId(), object.getId());
    getTropixObjectDao().copyVirtualPermissions(root.getId(), object.getId());
    getTropixObjectDao().copyVirtualPermissions(root.getId(), childObject.getId());

    // Add object to two places...
    getTropixObjectDao().addToVirtualFolder(root.getId(), object.getId());
    getTropixObjectDao().copyVirtualPermissions(root.getId(), object.getId());

    getTropixObjectDao().addToVirtualFolder(sub2.getId(), object.getId());
    getTropixObjectDao().copyVirtualPermissions(sub2.getId(), object.getId());

    getTropixObjectDao().addVirtualPermissionUser(root.getId(), "read", user3.getCagridId());
    getTropixObjectDao().addVirtualPermissionUser(root.getId(), "write", user1.getCagridId());

    assert securityProvider.canRead(childObject.getId(), user3.getCagridId());
    tropixObjectService.removeFromSharedFolder(user1.getCagridId(), root.getId(), object.getId());
    assert securityProvider.canRead(childObject.getId(), user3.getCagridId());

    assert root.getContents().size() == 1;
    assert !sub2.getContents().isEmpty();

    tropixObjectService.removeFromSharedFolder(user1.getCagridId(), sub2.getId(), object.getId());

    assert !securityProvider.canRead(childObject.getId(), user3.getCagridId());
    assert sub2.getContents().isEmpty();

  }

  @Test
  public void removeFromSharedFolderMultiple() {
    final User user1 = createTempUser(), user2 = createTempUser(), user3 = createTempUser();
    final VirtualFolder root = new VirtualFolder();
    root.setContents(new HashSet<TropixObject>());
    saveNewTropixObject(root);
    getTropixObjectDao().setOwner(root.getId(), user1);
    getTropixObjectDao().createVirtualPermission(root.getId(), "read");
    getTropixObjectDao().createVirtualPermission(root.getId(), "write");

    final VirtualFolder sub1 = new VirtualFolder(), sub2 = new VirtualFolder();
    sub1.setContents(new HashSet<TropixObject>());
    sub2.setContents(new HashSet<TropixObject>());
    saveNewTropixObject(sub1);
    saveNewTropixObject(sub2);
    getTropixObjectDao().addToVirtualFolder(root.getId(), sub1.getId());
    getTropixObjectDao().copyVirtualPermissions(root.getId(), sub1.getId());
    getTropixObjectDao().addToVirtualFolder(sub1.getId(), sub2.getId());
    getTropixObjectDao().copyVirtualPermissions(sub1.getId(), sub2.getId());

    final TropixObject object = new TropixObject();
    saveNewTropixObject(object);
    getTropixObjectDao().setOwner(object.getId(), user2);

    // Add object to two places...
    getTropixObjectDao().addToVirtualFolder(root.getId(), object.getId());
    getTropixObjectDao().copyVirtualPermissions(root.getId(), object.getId());

    getTropixObjectDao().addToVirtualFolder(sub2.getId(), object.getId());
    getTropixObjectDao().copyVirtualPermissions(sub2.getId(), object.getId());

    getTropixObjectDao().addVirtualPermissionUser(root.getId(), "read", user3.getCagridId());
    getTropixObjectDao().addVirtualPermissionUser(root.getId(), "write", user1.getCagridId());

    assert securityProvider.canRead(object.getId(), user3.getCagridId());
    tropixObjectService.removeFromSharedFolder(user1.getCagridId(), root.getId(), object.getId());
    assert securityProvider.canRead(object.getId(), user3.getCagridId());

    assert root.getContents().size() == 1;
    assert !sub2.getContents().isEmpty();

    tropixObjectService.removeFromSharedFolder(user1.getCagridId(), sub2.getId(), object.getId());

    assert !securityProvider.canRead(object.getId(), user3.getCagridId());
    assert sub2.getContents().isEmpty();

  }

  @Test
  public void addRemoveUserVirtualPermissionRemovesData() {
    final User user1 = createTempUser(), user2 = createTempUser(), user3 = createTempUser();

    final VirtualFolder root = new VirtualFolder();
    root.setContents(new HashSet<TropixObject>());
    saveNewTropixObject(root);
    getTropixObjectDao().setOwner(root.getId(), user1);
    getTropixObjectDao().createVirtualPermission(root.getId(), "read");
    getTropixObjectDao().createVirtualPermission(root.getId(), "write");

    final TropixObject object = new TropixObject();
    saveNewTropixObject(object);
    getTropixObjectDao().setOwner(object.getId(), user2);
    getTropixObjectDao().addToVirtualFolder(root.getId(), object.getId());
    getTropixObjectDao().copyVirtualPermissions(root.getId(), object.getId());

    tropixObjectService.addVirtualPermissionForUser(user1.getCagridId(), root.getId(), user2.getCagridId(), PermissionType.Write);
    tropixObjectService.addVirtualPermissionForUser(user1.getCagridId(), root.getId(), user3.getCagridId(), PermissionType.Read);

    assert securityProvider.canRead(object.getId(), user3.getCagridId());

    tropixObjectService.removeVirtualPermissionForUser(user1.getCagridId(), root.getId(), user2.getCagridId(), PermissionType.Write);

    assert root.getContents().isEmpty();
    assert !securityProvider.canRead(object.getId(), user3.getCagridId());

  }

  @Test
  public void addRemoveUserVirtualPermissionRemovesSavedFolder() {
    final User user1 = createTempUser(), user2 = createTempUser(), user3 = createTempUser();

    final VirtualFolder root = new VirtualFolder();
    root.setContents(new HashSet<TropixObject>());
    saveNewTropixObject(root);
    getTropixObjectDao().setOwner(root.getId(), user1);
    getTropixObjectDao().createVirtualPermission(root.getId(), "read");
    getTropixObjectDao().createVirtualPermission(root.getId(), "write");

    final TropixObject object = new TropixObject();
    saveNewTropixObject(object);
    getTropixObjectDao().setOwner(object.getId(), user2);
    getTropixObjectDao().addToVirtualFolder(root.getId(), object.getId());
    getTropixObjectDao().copyVirtualPermissions(root.getId(), object.getId());

    tropixObjectService.addVirtualPermissionForUser(user1.getCagridId(), root.getId(), user2.getCagridId(), PermissionType.Write);
    tropixObjectService.addVirtualPermissionForUser(user1.getCagridId(), root.getId(), user3.getCagridId(), PermissionType.Read);

    getUserDao().addVirtualFolder(user2.getCagridId(), root.getId());

    assert getUserDao().getUsersWithVirtualFolder(root.getId()).contains(user2);

    tropixObjectService.removeVirtualPermissionForUser(user1.getCagridId(), root.getId(), user2.getCagridId(), PermissionType.Write);

    assert getUserDao().getUsersWithVirtualFolder(root.getId()).contains(user2);

    tropixObjectService.removeVirtualPermissionForUser(user1.getCagridId(), root.getId(), user2.getCagridId(), PermissionType.Read);

    assert !getUserDao().getUsersWithVirtualFolder(root.getId()).contains(user2);

    tropixObjectService.addVirtualPermissionForUser(user1.getCagridId(), root.getId(), user2.getCagridId(), PermissionType.Write);

    getUserDao().addVirtualFolder(user2.getCagridId(), root.getId());

    tropixObjectService.removeVirtualPermissionForUser(user1.getCagridId(), root.getId(), user2.getCagridId(), PermissionType.Read);

    assert !getUserDao().getUsersWithVirtualFolder(root.getId()).contains(user2);
  }
  

  @Test
  public void addToSharedFolder() {
    final User user1 = createTempUser(), user2 = createTempUser(), user3 = createTempUser();
    final VirtualFolder root = createTempRootVirtualFolder();
    getTropixObjectDao().setOwner(root.getId(), user1);

    tropixObjectService.addVirtualPermissionForUser(user1.getCagridId(), root.getId(), user2.getCagridId(), PermissionType.Write);
    tropixObjectService.addVirtualPermissionForUser(user1.getCagridId(), root.getId(), user3.getCagridId(), PermissionType.Read);

    final TropixObject object = new TropixObject();
    saveNewTropixObject(object);
    getTropixObjectDao().setOwner(object.getId(), user2);

    assert !securityProvider.canRead(object.getId(), user3.getCagridId());
    assert root.getContents().isEmpty();
    tropixObjectService.addToSharedFolder(user2.getCagridId(), object.getId(), root.getId(), false);

    assert root.getContents().contains(object);
    assert securityProvider.canRead(object.getId(), user3.getCagridId());
  }

  private boolean canRead(final TropixObject object, final User user) {
    return securityProvider.canRead(object.getId(), user.getCagridId());
  }

  @Test
  public void addFolderToSharedFolder() {
    final User user1 = createTempUser(), user2 = createTempUser(), user3 = createTempUser();

    VirtualFolder root = super.createTempRootVirtualFolder();
    getTropixObjectDao().setOwner(root.getId(), user1);
    tropixObjectService.addVirtualPermissionForUser(user1.getCagridId(), root.getId(), user2.getCagridId(), PermissionType.Write);
    tropixObjectService.addVirtualPermissionForUser(user1.getCagridId(), root.getId(), user3.getCagridId(), PermissionType.Read);

    // System.out.println(String.format("Root id is %s ", root.getId()));

    final Folder folder = saveNewCommitted(newFolder(), user1);
    getTropixObjectDao().addToFolder(user1.getHomeFolder().getId(), folder.getId());
    final TropixObject object = saveNewCommitted(new TropixObject(), user1);
    getTropixObjectDao().addToFolder(folder.getId(), object.getId());

    assert canRead(object, user1);
    assert !canRead(object, user2);

    root = getTropixObjectDao().loadTropixObject(root.getId(), VirtualFolder.class);
    assert root.getContents().isEmpty();
    tropixObjectService.addToSharedFolder(user1.getCagridId(), folder.getId(), root.getId(), true);
    root = getTropixObjectDao().loadTropixObject(root.getId(), VirtualFolder.class);
    assert root.getContents().size() == 1;

    assert canRead(object, user1);
    assert canRead(object, user2);
  }

  @Test
  public void addToShraedFolderRecursive() {
    final User user1 = createTempUser(), user2 = createTempUser(), user3 = createTempUser();
    final VirtualFolder root = super.createTempRootVirtualFolder();
    getTropixObjectDao().setOwner(root.getId(), user1);
    tropixObjectService.addVirtualPermissionForUser(user1.getCagridId(), root.getId(), user2.getCagridId(), PermissionType.Write);
    tropixObjectService.addVirtualPermissionForUser(user1.getCagridId(), root.getId(), user3.getCagridId(), PermissionType.Read);

    final TropixObject object = new TropixObject(), childObject = new TropixObject();
    saveNewTropixObject(object);
    saveNewTropixObject(childObject);
    getTropixObjectDao().setOwner(object.getId(), user2);
    getTropixObjectDao().setOwner(childObject.getId(), user2);
    getTropixObjectDao().addPermissionParent(childObject.getId(), object.getId());

    assert !securityProvider.canRead(childObject.getId(), user3.getCagridId());
    tropixObjectService.addToSharedFolder(user2.getCagridId(), object.getId(), root.getId(), false);
    assert securityProvider.canRead(childObject.getId(), user3.getCagridId());
  }

  @Test
  public void addRemoveUserVirtualPermission() {
    final User user1 = createTempUser(), user2 = createTempUser();

    final VirtualFolder root = super.createTempRootVirtualFolder();
    getTropixObjectDao().setOwner(root.getId(), user1);

    assert !securityProvider.canRead(root.getId(), user2.getCagridId());
    assert !securityProvider.canModify(root.getId(), user2.getCagridId());

    tropixObjectService.addVirtualPermissionForUser(user1.getCagridId(), root.getId(), user2.getCagridId(), PermissionType.Read);
    assert securityProvider.canRead(root.getId(), user2.getCagridId());
    assert !securityProvider.canModify(root.getId(), user2.getCagridId());

    tropixObjectService.removeVirtualPermissionForUser(user1.getCagridId(), root.getId(), user2.getCagridId(), PermissionType.Read);
    assert !securityProvider.canRead(root.getId(), user2.getCagridId());
    assert !securityProvider.canModify(root.getId(), user2.getCagridId());

    tropixObjectService.addVirtualPermissionForUser(user1.getCagridId(), root.getId(), user2.getCagridId(), PermissionType.Write);
    assert securityProvider.canRead(root.getId(), user2.getCagridId());
    assert securityProvider.canModify(root.getId(), user2.getCagridId());

    tropixObjectService.removeVirtualPermissionForUser(user1.getCagridId(), root.getId(), user2.getCagridId(), PermissionType.Write);
    assert securityProvider.canRead(root.getId(), user2.getCagridId());
    assert !securityProvider.canModify(root.getId(), user2.getCagridId());

    tropixObjectService.addVirtualPermissionForUser(user1.getCagridId(), root.getId(), user2.getCagridId(), PermissionType.Write);
    assert securityProvider.canRead(root.getId(), user2.getCagridId());
    assert securityProvider.canModify(root.getId(), user2.getCagridId());

    tropixObjectService.removeVirtualPermissionForUser(user1.getCagridId(), root.getId(), user2.getCagridId(), PermissionType.Read);
    assert !securityProvider.canRead(root.getId(), user2.getCagridId());
    assert !securityProvider.canModify(root.getId(), user2.getCagridId());

  }

  private Group getNewGroup() {
    final Dao<Group> groupDao = getDaoFactory().getDao(Group.class);
    final Group group = new Group();
    group.setName("Group");
    group.setUsers(new HashSet<User>());
    groupDao.saveObject(group);
    return group;
  }

  private void addUserToGroup(final User user, final Group group) {
    final Dao<Group> groupDao = getDaoFactory().getDao(Group.class);
    group.getUsers().add(user);
    user.getGroups().add(group);
    getUserDao().saveOrUpdateUser(user);
    groupDao.saveObject(group);
  }

  @Test
  public void addRemoveGroupVirtualPermission() {
    final User user1 = createTempUser(), user2 = createTempUser();

    final Group group = getNewGroup();
    addUserToGroup(user2, group);

    final VirtualFolder root = new VirtualFolder();
    root.setContents(new HashSet<TropixObject>());
    saveNewTropixObject(root);
    getTropixObjectDao().setOwner(root.getId(), user1);
    getTropixObjectDao().createVirtualPermission(root.getId(), "read");
    getTropixObjectDao().createVirtualPermission(root.getId(), "write");

    assert !securityProvider.canRead(root.getId(), user2.getCagridId());
    assert !securityProvider.canModify(root.getId(), user2.getCagridId());

    tropixObjectService.addVirtualPermissionForGroup(user1.getCagridId(), root.getId(), group.getId(), PermissionType.Read);
    assert securityProvider.canRead(root.getId(), user2.getCagridId());
    assert !securityProvider.canModify(root.getId(), user2.getCagridId());

    tropixObjectService.removeVirtualPermissionForGroup(user1.getCagridId(), root.getId(), group.getId(), PermissionType.Read);
    assert !securityProvider.canRead(root.getId(), user2.getCagridId());
    assert !securityProvider.canModify(root.getId(), user2.getCagridId());

    tropixObjectService.addVirtualPermissionForGroup(user1.getCagridId(), root.getId(), group.getId(), PermissionType.Write);
    assert securityProvider.canRead(root.getId(), user2.getCagridId());
    assert securityProvider.canModify(root.getId(), user2.getCagridId());

    tropixObjectService.removeVirtualPermissionForGroup(user1.getCagridId(), root.getId(), group.getId(), PermissionType.Write);
    assert securityProvider.canRead(root.getId(), user2.getCagridId());
    assert !securityProvider.canModify(root.getId(), user2.getCagridId());

    tropixObjectService.addVirtualPermissionForGroup(user1.getCagridId(), root.getId(), group.getId(), PermissionType.Write);
    assert securityProvider.canRead(root.getId(), user2.getCagridId());
    assert securityProvider.canModify(root.getId(), user2.getCagridId());

    tropixObjectService.removeVirtualPermissionForGroup(user1.getCagridId(), root.getId(), group.getId(), PermissionType.Read);
    assert !securityProvider.canRead(root.getId(), user2.getCagridId());
    assert !securityProvider.canModify(root.getId(), user2.getCagridId());

  }

  private static PermissionReport getReport(final PermissionReport[] reports, final String id) {
    PermissionReport report = null;
    for(final PermissionReport possibleReport : reports) {
      if(possibleReport.getId().equals(id)) {
        report = possibleReport;
        break;
      }
    }
    return report;
  }

  @Test()
  public void getPermissionReportsShared() {
    final User userA = createTempUser(), userB = createTempUser();

    final VirtualFolder object = new VirtualFolder();
    object.setRoot(true);
    saveNewTropixObject(object);
    getTropixObjectDao().setOwner(object.getId(), userA);

    final String id = object.getId();

    PermissionReport[] reports;

    getTropixObjectDao().createVirtualPermission(id, "read");
    getTropixObjectDao().createVirtualPermission(id, "write");

    getTropixObjectDao().addVirtualPermissionUser(id, "read", userA.getCagridId());
    reports = tropixObjectService.getPermissionReports(userA.getCagridId(), object.getId());

    assert reports.length == 1 : reports.length;
    PermissionReport report = getReport(reports, userA.getCagridId());
    assert report.getPermissionSource().equals(PermissionSourceType.User);
    assert report.getPermission().equals(PermissionType.Owner);

    getTropixObjectDao().addVirtualPermissionUser(id, "read", userB.getCagridId());

    reports = tropixObjectService.getPermissionReports(userA.getCagridId(), object.getId());
    assert reports.length == 2 : reports.length;
    report = getReport(reports, userB.getCagridId());
    assert report.getPermissionSource().equals(PermissionSourceType.User);
    assert report.getPermission().equals(PermissionType.Read) : report.getPermission();

    getTropixObjectDao().addVirtualPermissionUser(id, "write", userB.getCagridId());
    reports = tropixObjectService.getPermissionReports(userA.getCagridId(), object.getId());
    assert reports.length == 2;
    report = getReport(reports, userB.getCagridId());
    assert report.getPermissionSource().equals(PermissionSourceType.User);
    assert report.getPermission().equals(PermissionType.Write);

    final Group group = getNewGroup();
    getTropixObjectDao().addVirtualPermissionGroup(id, "read", group.getId());

    reports = tropixObjectService.getPermissionReports(userA.getCagridId(), object.getId());
    assert reports.length == 3 : reports.length;
    report = getReport(reports, group.getId());
    assert report.getPermissionSource().equals(PermissionSourceType.Group);
    assert report.getPermission().equals(PermissionType.Read);

    getTropixObjectDao().addVirtualPermissionGroup(id, "write", group.getId());
    reports = tropixObjectService.getPermissionReports(userA.getCagridId(), object.getId());

    assert reports.length == 3;
    report = getReport(reports, group.getId());
    assert report.getPermissionSource().equals(PermissionSourceType.Group);
    assert report.getPermission().equals(PermissionType.Write);

  }

  @Test()
  public void getPermissionReportsFromShared() {
    final User newUser = createTempUser();

    final TropixObject object = new TropixObject();
    object.setName("MooCow");
    saveNewTropixObject(object);
    final String id = object.getId();
    getTropixObjectDao().setOwner(object.getId(), newUser);

    final VirtualFolder root = new VirtualFolder();
    root.setName("Moo");
    root.setRoot(true);
    root.setContents(new HashSet<TropixObject>());
    saveNewTropixObject(root);

    getTropixObjectDao().createVirtualPermission(root.getId(), "read");
    getTropixObjectDao().createVirtualPermission(root.getId(), "write");

    getTropixObjectDao().addToVirtualFolder(root.getId(), id);

    final PermissionReport[] reports = tropixObjectService.getPermissionReports(newUser.getCagridId(), id);
    final PermissionReport report = getReport(reports, root.getId());
    assert root != null;
    assert report.getName().equals("Moo") : report.getName();
    assert report.getPermissionSource().equals(PermissionSourceType.SharedFolder);

  }

  @Test()
  public void getPermissionReportsFromSharedMultiple() {
    final User newUser = createTempUser();

    final TropixObject object = new TropixObject();
    object.setName("MooCow");
    saveNewTropixObject(object);
    final String id = object.getId();
    getTropixObjectDao().setOwner(object.getId(), newUser);

    final VirtualFolder root = new VirtualFolder();
    root.setName("Moo");
    root.setRoot(true);
    root.setContents(new HashSet<TropixObject>());
    saveNewTropixObject(root);

    getTropixObjectDao().createVirtualPermission(root.getId(), "read");
    getTropixObjectDao().createVirtualPermission(root.getId(), "write");

    final VirtualFolder sub = new VirtualFolder();
    sub.setName("Cow");
    sub.setRoot(false);
    sub.setContents(new HashSet<TropixObject>());
    saveNewTropixObject(sub);
    getTropixObjectDao().addToVirtualFolder(root.getId(), sub.getId());

    getTropixObjectDao().addToVirtualFolder(sub.getId(), id);

    getTropixObjectDao().addToVirtualFolder(root.getId(), id);

    final PermissionReport[] reports = tropixObjectService.getPermissionReports(newUser.getCagridId(), id);
    PermissionReport report = getReport(reports, root.getId());
    assert report != null;
    assert report.getName().equals("Moo") : report.getName();
    assert report.getPermissionSource().equals(PermissionSourceType.SharedFolder);

    report = getReport(reports, sub.getId());
    assert report != null;
    assert report.getName().equals("Moo > Cow") : report.getName();
    assert report.getPermissionSource().equals(PermissionSourceType.SharedFolder);

  }

  @Test()
  public void getPermissionReportsFromShareNested() {
    final User newUser = createTempUser();

    final TropixObject object = new TropixObject();
    object.setName("MooCow");
    saveNewTropixObject(object);
    final String id = object.getId();
    getTropixObjectDao().setOwner(object.getId(), newUser);

    final VirtualFolder root = new VirtualFolder();
    root.setName("Moo");
    root.setRoot(true);
    root.setContents(new HashSet<TropixObject>());
    saveNewTropixObject(root);

    getTropixObjectDao().createVirtualPermission(root.getId(), "read");
    getTropixObjectDao().createVirtualPermission(root.getId(), "write");

    final VirtualFolder sub = new VirtualFolder();
    sub.setName("Cow");
    sub.setRoot(false);
    sub.setContents(new HashSet<TropixObject>());
    saveNewTropixObject(sub);
    getTropixObjectDao().addToVirtualFolder(root.getId(), sub.getId());

    getTropixObjectDao().addToVirtualFolder(sub.getId(), id);

    final PermissionReport[] reports = tropixObjectService.getPermissionReports(newUser.getCagridId(), id);
    final PermissionReport report = getReport(reports, sub.getId());
    assert root != null;
    assert report.getName().equals("Moo > Cow") : report.getName();
    assert report.getPermissionSource().equals(PermissionSourceType.SharedFolder);

  }

  @Test()
  public void getPermissionReports() {
    final User newUser = createTempUser();

    final Folder object = new Folder();
    saveNewTropixObject(object);
    final String id = object.getId();
    getTropixObjectDao().setOwner(object.getId(), newUser);

    PermissionReport[] reports = tropixObjectService.getPermissionReports(newUser.getCagridId(), object.getId());
    assert reports.length == 1;
    assert reports[0].getPermission().equals(PermissionType.Owner);
    assert reports[0].getPermissionSource().equals(PermissionSourceType.User);

    final User userA = createTempUser();
    getTropixObjectDao().addRole(id, "read", userA);
    getTropixObjectDao().addRole(id, "read", userA);
    getTropixObjectDao().addRole(id, "write", userA);

    reports = tropixObjectService.getPermissionReports(newUser.getCagridId(), object.getId());
    assert reports.length == 2 : reports.length;
    PermissionReport report = getReport(reports, userA.getCagridId());
    assert report.getPermissionSource().equals(PermissionSourceType.User);
    assert report.getPermission().equals(PermissionType.Write);

    final User userB = createTempUser();
    getTropixObjectDao().addRole(id, "read", userB);

    reports = tropixObjectService.getPermissionReports(newUser.getCagridId(), object.getId());
    assert reports.length == 3 : reports.length;
    report = getReport(reports, userB.getCagridId());
    assert report.getPermissionSource().equals(PermissionSourceType.User);
    assert report.getPermission().equals(PermissionType.Read);

    // TODO: Test groups
    final Group group = getNewGroup();
    final User userC = createTempUser();
    addUserToGroup(userC, group);

    getTropixObjectDao().addGroupRole(object.getId(), "read", group);
    reports = tropixObjectService.getPermissionReports(newUser.getCagridId(), object.getId());
    assert reports.length == 4 : reports.length;
    report = getReport(reports, group.getId());
    assert report.getPermissionSource().equals(PermissionSourceType.Group);
    assert report.getPermission().equals(PermissionType.Read);

    getTropixObjectDao().addGroupRole(object.getId(), "write", group);
    reports = tropixObjectService.getPermissionReports(newUser.getCagridId(), object.getId());
    assert reports.length == 4 : reports.length;
    report = getReport(reports, group.getId());
    assert report.getPermissionSource().equals(PermissionSourceType.Group);
    assert report.getPermission().equals(PermissionType.Write);

  }

  @Test
  public void deleteHomeFolder() {
    final User user = createTempUser();

    RuntimeException e = null;
    try {
      tropixObjectService.delete(user.getCagridId(), user.getHomeFolder().getId());
    } catch(final RuntimeException exception) {
      e = exception;
    }
    assert e != null;
  }

  @Test()
  public void delete() {
    final User user1 = createTempUser();

    final TropixObject object = new TropixObject();
    object.setCommitted(true);
    saveNewTropixObject(object);
    getTropixObjectDao().setOwner(object.getId(), user1);

    tropixObjectService.delete(user1.getCagridId(), object.getId());

    assert getTropixObjectDao().loadTropixObject(object.getId()).getDeletedTime() != null;
  }

  @Test(expectedExceptions = RuntimeException.class)
  public void deleteNotOwner() {
    final User user1 = createTempUser(), user2 = createTempUser();

    final TropixObject object = new TropixObject();
    object.setCommitted(true);
    saveNewTropixObject(object);
    getTropixObjectDao().setOwner(object.getId(), user1);

    tropixObjectService.delete(user2.getCagridId(), object.getId());
  }

  @Test()
  public void deleteRecursive() {
    final User user1 = createTempUser();

    final TropixObject object1 = new TropixObject();
    object1.setCommitted(true);
    saveNewTropixObject(object1);
    getTropixObjectDao().setOwner(object1.getId(), user1);

    final TropixObject object2 = new TropixObject();
    object2.setCommitted(true);
    saveNewTropixObject(object2);
    getTropixObjectDao().setOwner(object2.getId(), user1);

    getTropixObjectDao().addPermissionParent(object2.getId(), object1.getId());
    tropixObjectService.delete(user1.getCagridId(), object1.getId());

    assert getTropixObjectDao().loadTropixObject(object2.getId()).getDeletedTime() != null;
  }

  @Test(expectedExceptions = RuntimeException.class)
  public void moveAncestor() {
    final User newUser = createTempUser();

    final Folder folder1 = saveNewCommitted(new Folder(), newUser);
    getTropixObjectDao().addToFolder(newUser.getHomeFolder().getId(), folder1.getId());

    final Folder folder2 = saveNewCommitted(new Folder(), newUser);
    getTropixObjectDao().addToFolder(folder1.getId(), folder2.getId());

    final Folder folder3 = new Folder();
    saveNewTropixObject(folder1);
    getTropixObjectDao().addToFolder(folder2.getId(), folder3.getId());

    tropixObjectService.move(newUser.getCagridId(), folder1.getId(), folder3.getId());
  }

  @Test(expectedExceptions = {RuntimeException.class})
  public void moveHomeFolder() {
    final User newUser = createTempUser();

    final Folder folder1 = new Folder();
    saveNewTropixObject(folder1);
    getTropixObjectDao().setOwner(folder1.getId(), newUser);

    tropixObjectService.move(newUser.getCagridId(), newUser.getHomeFolder().getId(), folder1.getId());
  }

  @Test
  public void move() {
    final User newUser = createTempUser();

    final Folder folder1 = saveNewCommitted(newFolder(), newUser);
    final Folder folder2 = saveNewCommitted(newFolder(), newUser);

    getTropixObjectDao().addToFolder(newUser.getHomeFolder().getId(), folder1.getId());
    getTropixObjectDao().addToFolder(newUser.getHomeFolder().getId(), folder2.getId());

    final TropixFile file = new TropixFile();
    file.setPermissionParents(new HashSet<TropixObject>());
    file.setName("New File");
    file.setFileId("1");
    saveNewTropixObject(file);
    getTropixObjectDao().addToFolder(folder1.getId(), file.getId());
    getTropixObjectDao().setOwner(file.getId(), newUser);
    getTropixObjectDao().addPermissionParent(file.getId(), folder1.getId());

    assert folder1.getPermissionChildren().iterator().next().equals(file);
    assert file.getPermissionParents().iterator().next().equals(folder1);

    assert getTropixObjectDao().getFolderContents(folder1.getId()).size() == 1;
    assert getTropixObjectDao().getFolderContents(folder2.getId()).size() == 0 : getTropixObjectDao().getFolderContents(folder2.getId()).size();

    tropixObjectService.move(newUser.getCagridId(), file.getId(), folder2.getId());

    assert getTropixObjectDao().getFolderContents(folder1.getId()).size() == 0;
    assert getTropixObjectDao().getFolderContents(folder2.getId()).size() == 1;

    assert !folder1.getPermissionChildren().iterator().hasNext();
    assert file.getPermissionParents().iterator().next().equals(folder2);
    assert folder2.getPermissionChildren().iterator().next().equals(file);

  }

  @Test
  public void types() {
    final User user1 = createTempUser();

    final TropixFile file = new TropixFile();
    file.setName("A file");
    file.setFileId("123456");
    file.setCommitted(true);
    saveNewTropixObject(file, user1);

    final TropixFile loadedFile = (TropixFile) tropixObjectService.load(user1.getCagridId(), file.getId());
    assert loadedFile.getName().equals("A file");
    assert loadedFile.getFileId().equals("123456");

  }

  @Test
  public void getAssociations() {
    final User u1 = createTempUser(), u2 = createTempUser();
    final TissueSample sample = new TissueSample();
    sample.setCommitted(true);
    saveNewTropixObject(sample);
    getTropixObjectDao().setOwner(sample.getId(), u1);
    getTropixObjectDao().addRole(sample.getId(), "read", u2);

    final Set<ProteomicsRun> runs = new HashSet<ProteomicsRun>();
    final ProteomicsRun run1 = new ProteomicsRun(), run2 = new ProteomicsRun();
    run1.setCommitted(true);
    run2.setCommitted(true);
    saveNewTropixObject(run1);
    getTropixObjectDao().setOwner(run1.getId(), u1);
    saveNewTropixObject(run2);
    getTropixObjectDao().setOwner(run2.getId(), u2);

    runs.add(run1);
    runs.add(run2);
    sample.setProteomicsRuns(runs);
    saveNewTropixObject(sample);
    TropixObject[] objects = tropixObjectService.getAssociations(u1.getCagridId(), sample.getId(), "proteomicsRuns");
    assert objects.length == 1 : objects.length;
    assert objects[0].getId().equals(run1.getId());

    objects = tropixObjectService.getAssociations(u2.getCagridId(), sample.getId(), "proteomicsRuns");
    assert objects.length == 1 : objects.length;
    assert objects[0].getId().equals(run2.getId());

    getTropixObjectDao().addRole(run2.getId(), "read", u1);
    objects = tropixObjectService.getAssociations(u1.getCagridId(), sample.getId(), "proteomicsRuns");
    assert objects.length == 2 : objects.length;
  }

  @Test
  public void getAssociation() {
    final User u1 = createTempUser(), u2 = createTempUser();
    final Database database = new Database();
    database.setCommitted(true);
    saveNewTropixObject(database);
    getTropixObjectDao().setOwner(database.getId(), u1);
    getTropixObjectDao().addRole(database.getId(), "read", u2);

    final IdentificationAnalysis analysis = new IdentificationAnalysis();
    analysis.setCommitted(true);
    analysis.setDatabase(database);
    saveNewTropixObject(analysis);
    getTropixObjectDao().setOwner(analysis.getId(), u1);
    getTropixObjectDao().addRole(analysis.getId(), "read", u2);

    final Database loadedDatabase = (Database) tropixObjectService.getAssociation(u2.getCagridId(), analysis.getId(), "database");
    assert loadedDatabase.getId().equals(database.getId());
  }

  @Test
  public void typedLoad() {
    final User user = createTempUser();
    final TropixFile file = new TropixFile();
    file.setName("A file");
    file.setFileId("123456");
    saveNewTropixObject(file, user);

    final TropixFile loadedFile = (TropixFile) tropixObjectService.load(user.getCagridId(), file.getId(), TropixObjectTypeEnum.FILE);
    assert loadedFile.getName().equals("A file");
    assert loadedFile.getFileId().equals("123456");
  }

  @Test(expectedExceptions = {RuntimeException.class})
  public void loadIncorrectType() {
    final User user = createTempUser();
    final TropixFile file = new TropixFile();
    file.setName("A file");
    file.setFileId("123456");
    saveNewTropixObject(file, user);
    final Folder folder = (Folder) tropixObjectService.load(user.getCagridId(), file.getId(), TropixObjectTypeEnum.FOLDER);
    folder.getName(); // Hibernate use to throw exception in last line...
  }

  @Test
  public void save() {
    final User user = createTempUser();
    final TropixObject object = new TropixObject();
    final String name = getClass().getCanonicalName() + "save";
    final String description = name + " description";
    object.setName(name);
    saveNewTropixObject(object, user);

    object.setDescription(description);
    tropixObjectService.update(user.getCagridId(), object);

    final TropixObject updatedObject = getTropixObjectDao().loadTropixObject(object.getId());
    assert updatedObject.getDescription().equals(description);
  }

  @Test
  public void load() {
    final User user = createTempUser();
    final TropixObject object = new TropixObject();
    final String name = getClass().getCanonicalName() + "load";
    final String description = name + " description";
    object.setName(name);
    object.setDescription(description);
    object.setCommitted(true);
    saveNewTropixObject(object, user);

    final TropixObject loadedObject = tropixObjectService.load(user.getCagridId(), object.getId());
    assert loadedObject.getName().equals(name);
    assert loadedObject.getDescription().equals(description);
  }

  @Test(expectedExceptions = RuntimeException.class)
  public void wrongAccessLoad() {
    final User user = createTempUser(), wrongUser = createTempUser();
    final TropixObject object = new TropixObject();
    final String name = getClass().getCanonicalName() + "load";
    object.setName(name);
    saveNewTropixObject(object, user);

    tropixObjectService.load(wrongUser.getCagridId(), object.getId());
  }

  @Test
  public void ownedObjectsVirtualHierarchyCount() {
    final User u1 = createTempUser(), u2 = createTempUser(), u3 = createTempUser(), u4 = createTempUser();
    createTempGroup(u1);
    createTempGroup(u2);

    final VirtualFolder root1 = createTempRootVirtualFolder();

    super.getTropixObjectDao().addVirtualPermissionUser(root1.getId(), "write", u1.getCagridId());
    super.getTropixObjectDao().addVirtualPermissionUser(root1.getId(), "write", u2.getCagridId());
    super.getTropixObjectDao().addVirtualPermissionUser(root1.getId(), "write", u3.getCagridId());
    super.getTropixObjectDao().addVirtualPermissionUser(root1.getId(), "read", u4.getCagridId());

    assert tropixObjectService.ownedObjectsVirtualHierarchyCount(u1.getCagridId(), root1.getId()) == 0L;
    assert tropixObjectService.ownedObjectsVirtualHierarchyCount(u2.getCagridId(), root1.getId()) == 0L;
    assert tropixObjectService.ownedObjectsVirtualHierarchyCount(u3.getCagridId(), root1.getId()) == 0L;
    assert tropixObjectService.ownedObjectsVirtualHierarchyCount(u4.getCagridId(), root1.getId()) == 0L;

    final VirtualFolder child1 = createTempChildVirtualFolder(root1);

    assert tropixObjectService.ownedObjectsVirtualHierarchyCount(u1.getCagridId(), root1.getId()) == 0L;
    assert tropixObjectService.ownedObjectsVirtualHierarchyCount(u2.getCagridId(), root1.getId()) == 0L;
    assert tropixObjectService.ownedObjectsVirtualHierarchyCount(u3.getCagridId(), root1.getId()) == 0L;
    assert tropixObjectService.ownedObjectsVirtualHierarchyCount(u4.getCagridId(), root1.getId()) == 0L;

    final TropixObject object1 = super.saveNewCommitted(new TropixObject(), u1);
    final TropixObject object2 = super.saveNewCommitted(new TropixObject(), u3);

    assert tropixObjectService.ownedObjectsVirtualHierarchyCount(u1.getCagridId(), root1.getId()) == 0L;
    assert tropixObjectService.ownedObjectsVirtualHierarchyCount(u2.getCagridId(), root1.getId()) == 0L;
    assert tropixObjectService.ownedObjectsVirtualHierarchyCount(u3.getCagridId(), root1.getId()) == 0L;
    assert tropixObjectService.ownedObjectsVirtualHierarchyCount(u4.getCagridId(), root1.getId()) == 0L;

    getTropixObjectDao().addToVirtualFolder(root1.getId(), object1.getId());
    getTropixObjectDao().copyVirtualPermissions(root1.getId(), object1.getId());

    assert tropixObjectService.ownedObjectsVirtualHierarchyCount(u1.getCagridId(), root1.getId()) == 1L;
    assert tropixObjectService.ownedObjectsVirtualHierarchyCount(u2.getCagridId(), root1.getId()) == 0L;
    assert tropixObjectService.ownedObjectsVirtualHierarchyCount(u3.getCagridId(), root1.getId()) == 0L;
    assert tropixObjectService.ownedObjectsVirtualHierarchyCount(u4.getCagridId(), root1.getId()) == 0L;

    getTropixObjectDao().addToVirtualFolder(child1.getId(), object1.getId());

    assert tropixObjectService.ownedObjectsVirtualHierarchyCount(u1.getCagridId(), root1.getId()) == 1L;
    assert tropixObjectService.ownedObjectsVirtualHierarchyCount(u2.getCagridId(), root1.getId()) == 0L;
    assert tropixObjectService.ownedObjectsVirtualHierarchyCount(u3.getCagridId(), root1.getId()) == 0L;
    assert tropixObjectService.ownedObjectsVirtualHierarchyCount(u4.getCagridId(), root1.getId()) == 0L;

    getTropixObjectDao().addToVirtualFolder(child1.getId(), object2.getId());
    getTropixObjectDao().copyVirtualPermissions(child1.getId(), object2.getId());

    assert tropixObjectService.ownedObjectsVirtualHierarchyCount(u1.getCagridId(), root1.getId()) == 1L;
    assert tropixObjectService.ownedObjectsVirtualHierarchyCount(u2.getCagridId(), root1.getId()) == 0L;
    assert tropixObjectService.ownedObjectsVirtualHierarchyCount(u3.getCagridId(), root1.getId()) == 1L;
    assert tropixObjectService.ownedObjectsVirtualHierarchyCount(u4.getCagridId(), root1.getId()) == 0L;

    final VirtualFolder root2 = createTempRootVirtualFolder();
    final TropixObject object3 = super.saveNewCommitted(new TropixObject(), u4);
    getTropixObjectDao().addToVirtualFolder(root2.getId(), object3.getId());
    getTropixObjectDao().copyVirtualPermissions(root2.getId(), object3.getId());

    assert tropixObjectService.ownedObjectsVirtualHierarchyCount(u1.getCagridId(), root1.getId()) == 1L;
    assert tropixObjectService.ownedObjectsVirtualHierarchyCount(u2.getCagridId(), root1.getId()) == 0L;
    assert tropixObjectService.ownedObjectsVirtualHierarchyCount(u3.getCagridId(), root1.getId()) == 1L;
    assert tropixObjectService.ownedObjectsVirtualHierarchyCount(u4.getCagridId(), root1.getId()) == 0L;

  }

}
