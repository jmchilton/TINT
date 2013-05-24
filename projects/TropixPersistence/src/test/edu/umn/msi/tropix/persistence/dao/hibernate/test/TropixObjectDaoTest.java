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

package edu.umn.msi.tropix.persistence.dao.hibernate.test;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.NotTransactional;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.InternalRequest;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.Request;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.models.VirtualPermission;
import edu.umn.msi.tropix.persistence.dao.Dao;
import edu.umn.msi.tropix.persistence.dao.DaoFactory;
import edu.umn.msi.tropix.persistence.dao.TropixObjectDao;

public class TropixObjectDaoTest extends DaoTest {
  @Autowired
  private TropixObjectDao objectDao;
  
  @Test
  public void isInstnace() {
    final String objectId = newObject();
    makeFile(objectId);

    assert objectDao.isInstance(objectId, TropixFile.class);
  }

  @Test
  public void isInstanceCacheTest() {
    final String requestorId = newId(), externalId = newId();
    final String objectId = newObject();
    makeInternalRequest(objectId, requestorId, externalId);

    // Load it as a request..
    objectDao.loadTropixObject(objectId, Request.class);

    // Check if it is cached as an InternalRequest...
    assert objectDao.isInstance(objectId, InternalRequest.class);
  }

  @Test
  public void loadRequest() {
    final String objectId = newObject(), requestorId = newId(), externalId = newId();
    makeRequest(objectId, requestorId, externalId);

    Request request = objectDao.loadRequest(requestorId, externalId);
    assert request.getId().equals(objectId);
    request = objectDao.loadRequest(requestorId, newId());
    assert request == null;
    request = objectDao.loadRequest(newId(), externalId);
    assert request == null;

  }

  @Test
  public void loadRequestInternal() {
    final String objectId = newObject(), requestorId = newId(), externalId = newId();
    makeInternalRequest(objectId, requestorId, externalId);

    InternalRequest request = (InternalRequest) objectDao.loadRequest(requestorId, externalId);
    assert request.getStorageServiceUrl().equals("http://moo.cow");
    assert request.getId().equals(objectId);
    request = (InternalRequest) objectDao.loadRequest(requestorId, newId());
    assert request == null;
    request = (InternalRequest) objectDao.loadRequest(newId(), externalId);
    assert request == null;

  }

  private void makeInternalRequest(final String objectId, final String requestorId, final String externalId) {
    makeRequest(objectId, requestorId, externalId);
    execute("INSERT INTO INTERNAL_REQUEST(REQUEST_ID,STORAGE_SERVICE_URL) VALUES ('" + objectId + "','http://moo.cow')");
  }

  private void makeRequest(final String objectId, final String requestorId, final String externalId) {
    execute("INSERT INTO REQUEST(OBJECT_ID,REQUESTOR_ID,EXTERNAL_ID) VALUES ('" + objectId + "','" + requestorId + "','" + externalId + "')");
  }

  @Test
  public void ownsSharedFolderWithName() {
    final String objectId = newObjectWithName("The name1"), userId = newUser(), user2Id = newUser();
    makeVirtualFolder(objectId, true);

    final String ownerPermissionId = newPermission("owner");
    execute("INSERT INTO JOIN_PERMISSION_USER(PERMISSION_ID,USER_ID) VALUES ('" + ownerPermissionId + "','" + userId + "')");
    addObjectPermission(ownerPermissionId, objectId);

    flush();

    assert objectDao.ownsSharedFolderWithName(userId, "The name1");
    assert !objectDao.ownsSharedFolderWithName(user2Id, "The name1");
  }

  @Test
  public void loadFileWithFileId() {
    final String id1 = newObjectWithName("The name");
    final String fileId = makeFile(id1);
    assert objectDao.loadTropixFileWithFileId(fileId).getName().equals("The name");
  }

  @Test
  public void loadFileWithFileIdNotPresent() {
    final String id1 = newObject();
    makeFile(id1);
    assert objectDao.loadTropixFileWithFileId(id1) == null;
  }

  @Test
  public void testFileExists() {
    final String id1 = newObject();
    final String fileId = makeFile(id1);
    assert objectDao.fileExists(fileId);
  }

  @Test
  public void testFileExistsFalse() {
    final String id1 = newObject();
    final String fileId = makeFile(id1);
    // Using object id instead of file id returns false
    assert !objectDao.fileExists(id1);
  }

  @Test
  public void testFilesExistAndCanReadAll() {
    final UserInfo user1 = newUserIdentity();
    final String fileId1 = makeFileForOwner(user1.id);
    final String fileId2 = makeFileForOwner(user1.id);
    assert objectDao.filesExistAndCanReadAll(new String[] {fileId1, fileId2}, user1.identity);
  }

  @Test
  public void testFilesExistAndCanReadAllNoPerms() {
    final UserInfo user1 = newUserIdentity(), user2 = newUserIdentity();
    final String fileId1 = makeFileForOwner(user1.id);
    final String fileId2 = makeFileForOwner(user1.id);
    assert !objectDao.filesExistAndCanReadAll(new String[] {fileId1, fileId2}, user2.identity);
  }

  @Test
  public void testFilesExistAndCanReadAllSomePerms() {
    final UserInfo user1 = newUserIdentity(), user2 = newUserIdentity();
    final String fileId1 = makeFileForOwner(user1.id);
    final String fileId2 = makeFileForOwner(user1.id);
    final String fileId3 = makeFileForOwner(user2.id);
    assert !objectDao.filesExistAndCanReadAll(new String[] {fileId1, fileId2, fileId3}, user1.identity);
  }

  @Test
  public void testGetFilesObjectId() {
    final String id1 = newObject();
    final String fileId = makeFile(id1);
    assert objectDao.getFilesObjectId(fileId).equals(id1);
  }

  @Test
  public void getAssociation() {
    final String fileId = newObject(), runId = newObject();
    makeFile(fileId);

    execute("INSERT INTO RUN(OBJECT_ID) VALUES ('%s')", runId);
    execute("INSERT INTO PROTEOMICS_RUN(RUN_ID, SOURCE_ID) VALUES ('%s','%s')", runId, fileId);

    final TropixFile file = (TropixFile) objectDao.getAssociation(runId, "source");
    assert file.getId().equals(fileId);
  }

  @Test
  public void getAssociations() {
    final String folderId = newFolder(), runId = newObjectInFolder(folderId), fileId = newObjectInFolder(folderId);
    makeFile(fileId);
    makeFile(runId);

    final Collection<TropixObject> objects = objectDao.getAssociations(folderId, "contents");
    assert objects.size() == 2 : objects.size();
  }

  @Test
  public void polymorphicLoad() {
    final String id1 = newFolder();
    flush();

    final TropixObject object = objectDao.loadTropixObject(id1);
    assert object instanceof Folder;
  }

  @Test
  public void addPermissionParent() {
    final String id1 = newObject(), id2 = newObject();
    flush();

    objectDao.addPermissionParent(id1, id2);

    flush();
    assert objectDao.loadTropixObject(id1).getPermissionParents().size() == 1;
    assert objectDao.loadTropixObject(id2).getPermissionChildren().size() == 1;
  }

  @Test
  public void requestContents() {
    final String reqId = newObject(), fileId = newId();
    makeRequest(reqId, newId(), newId());

    execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION,REQUEST_ID) VALUES ('" + fileId + "','The name3','The description3','" + reqId + "')");
    makeFile(fileId);

    flush();
    final Collection<TropixObject> collect = objectDao.getFolderContents(reqId);
    assert collect.size() == 1;

  }

  @Test
  public void contents() {
    final String folder1Id = newFolder();
    final String subFolderId = newFolderInFolder(folder1Id);
    final String object1 = newObjectInFolder(folder1Id);
    flush();
    final Collection<TropixObject> collect = objectDao.getFolderContents(folder1Id);
    assert collect.size() == 2;

    final Collection<TropixObject> collect3 = objectDao.getFolderContents(subFolderId);
    assert collect3.size() == 0;
  }

  @Test
  public void createVirtualPermissions() {
    final String objectId = newId();
    execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + objectId + "','The name1','The description2')");
    execute("INSERT INTO VIRTUAL_FOLDER(OBJECT_ID) VALUES ('" + objectId + "')");
    flush();
    final int preCount = simpleJdbcTemplate.queryForInt("SELECT count(*) from PERMISSION WHERE ROLE='read'");
    final int preJoinCount = simpleJdbcTemplate.queryForInt("SELECT count(*) from JOIN_PERMISSION_OBJECT WHERE OBJECT_ID='" + objectId + "'");
    objectDao.createVirtualPermission(objectId, "read");
    flush();
    final int postCount = simpleJdbcTemplate.queryForInt("SELECT count(*) from PERMISSION WHERE ROLE='read'");
    final int postJoinCount = simpleJdbcTemplate.queryForInt("SELECT count(*) from JOIN_PERMISSION_OBJECT WHERE OBJECT_ID='" + objectId + "'");
    assert postCount - preCount == 1;
    assert postJoinCount - preJoinCount == 1;

    final String userId = newId();
    execute("INSERT INTO USER(ID,CAGRID_ID,FIRST_NAME) VALUES ('+userId+','" + userId + "','Homer')");
    flush();
  }

  @Test
  public void addVirtualPermissionUser() {
    final String objectId = newId(), userId = newId();
    execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + objectId + "','The name1','The description2')");
    execute("INSERT INTO USER(ID,CAGRID_ID,FIRST_NAME) VALUES ('" + userId + "','" + userId + "','Homer')");
    final String permId = this.newPermission("read");
    execute("INSERT INTO VIRTUAL_PERMISSION(PERMISSION_ID) VALUES ('" + permId + "')");
    addObjectPermission(permId, objectId);
    flush();
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from JOIN_PERMISSION_USER WHERE PERMISSION_ID='" + permId + "' AND USER_ID='" + userId
        + "'") == 0;
    objectDao.addVirtualPermissionUser(objectId, "read", userId);
    flush();
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from JOIN_PERMISSION_USER WHERE PERMISSION_ID='" + permId + "' AND USER_ID='" + userId
        + "'") == 1;
  }

  @Test
  public void addVirtualPermissionGroup() {
    final String objectId = newId(), groupId = newId();
    execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + objectId + "','The name1','The description2')");
    execute("INSERT INTO TROPIX_GROUP(ID) VALUES ('" + groupId + "')");
    final String permId = this.newPermission("read");
    execute("INSERT INTO VIRTUAL_PERMISSION(PERMISSION_ID) VALUES ('" + permId + "')");
    this.addObjectPermission(permId, objectId);
    flush();
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from JOIN_PERMISSION_GROUP WHERE PERMISSION_ID='" + permId + "' AND GROUP_ID='" + groupId
        + "'") == 0;
    objectDao.addVirtualPermissionGroup(objectId, "read", groupId);
    flush();
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from JOIN_PERMISSION_GROUP WHERE PERMISSION_ID='" + permId + "' AND GROUP_ID='" + groupId
        + "'") == 1;
  }

  @Test
  public void save() {
    final TropixObject object = new TropixObject();
    object.setName("AnObject");
    object.setDescription("ObjectDescription");
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from TROPIX_OBJECT WHERE NAME='AnObject'") == 0;
    objectDao.saveOrUpdateTropixObject(object);
    flush();
    final String id = object.getId();
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from TROPIX_OBJECT WHERE NAME='AnObject'") == 1;
    assert simpleJdbcTemplate.queryForObject("SELECT NAME from TROPIX_OBJECT WHERE ID=?", String.class, id).equals("AnObject");
    assert simpleJdbcTemplate.queryForObject("SELECT DESCRIPTION from TROPIX_OBJECT WHERE ID=?", String.class, id).equals("ObjectDescription");
  }

  @Test
  public void load() {
    execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('356','The name','The description')");
    final TropixObject object = objectDao.loadTropixObject("356");
    assert object.getId().equals("356");
    assert object.getDescription().equals("The description");
    assert object.getName().equals("The name");

    execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('357','The name','The description')");
    assert objectDao.loadTropixObjects(new String[] {"356", "357"}).size() == 2;
    assert objectDao.loadTropixObjects(new String[] {"356"}).size() == 1;
  }

  @Test
  public void getOwner() {
    execute("INSERT INTO USER(ID,CAGRID_ID) VALUES (111,112)");
    execute("INSERT INTO TROPIX_OBJECT(ID) VALUES (333)");
    final String permissionId = newPermission("owner");
    makeDirectPermission(permissionId);
    this.addObjectPermission(permissionId, "333");
    this.addUserPermission(permissionId, "111");
    // execute("INSERT INTO TROPIX_USER_ROLE(ID,ROLE_NAME,USER_ID,OBJECT_ID) VALUES (123412323,'owner',111,333)");
    flush();
    assert objectDao.getOwner("333").getCagridId().equals("112");

  }

  @Test
  public void setOwner() {
    final User user = getNewUser();
    final String id = user.getCagridId();

    execute("INSERT INTO USER(ID,CAGRID_ID) VALUES ('" + id + "','" + id + "')");
    // execute("INSERT INTO TROPIX_USER_ROLE(ID,ROLE_NAME,USER_ID,OBJECT_ID) VALUES (123412323,'owner',111,333)");
    execute("INSERT INTO TROPIX_OBJECT(ID) VALUES (33323432)");

    assert simpleJdbcTemplate
        .queryForInt("SELECT count(*) from PERMISSION, JOIN_PERMISSION_USER, JOIN_PERMISSION_OBJECT WHERE PERMISSION.ROLE='owner' AND JOIN_PERMISSION_USER.USER_ID='"
            + id
            + "' AND JOIN_PERMISSION_OBJECT.OBJECT_ID='33323432' AND JOIN_PERMISSION_OBJECT.PERMISSION_ID=PERMISSION.ID AND JOIN_PERMISSION_USER.PERMISSION_ID=PERMISSION.ID") == 0;
    objectDao.setOwner("33323432", user);
    flush();
    assert simpleJdbcTemplate
        .queryForInt("SELECT count(*) from PERMISSION, JOIN_PERMISSION_USER, JOIN_PERMISSION_OBJECT WHERE PERMISSION.ROLE='owner' AND JOIN_PERMISSION_USER.USER_ID='"
            + id
            + "' AND JOIN_PERMISSION_OBJECT.OBJECT_ID='33323432' AND JOIN_PERMISSION_OBJECT.PERMISSION_ID=PERMISSION.ID AND JOIN_PERMISSION_USER.PERMISSION_ID=PERMISSION.ID") == 1;
  }

  @Test
  public void delete() {
    execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('3523426','The name','The description')");
    execute("INSERT INTO FOLDER(OBJECT_ID) VALUES ('3523426')");
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from TROPIX_OBJECT WHERE ID='3523426'") == 1;
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from FOLDER WHERE OBJECT_ID='3523426'") == 1;
    objectDao.delete("3523426");
    flush();
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from TROPIX_OBJECT WHERE ID='3523426'") == 0;
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from FOLDER WHERE OBJECT_ID='3523426'") == 0;
  }

  @Test
  public void deleteWithRole() {
    final String permissionId = newId(), objectId = newId();

    execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + objectId + "','The name','The description')");
    execute("INSERT INTO PERMISSION(ID) VALUES ('" + permissionId + "')");
    execute("INSERT INTO JOIN_PERMISSION_OBJECT(PERMISSION_ID, OBJECT_ID) VALUES ('" + permissionId + "','" + objectId + "')");

    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from JOIN_PERMISSION_OBJECT WHERE OBJECT_ID='" + objectId + "'") == 1;
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from TROPIX_OBJECT WHERE ID='" + objectId + "'") == 1;
    objectDao.delete(objectId);
    flush();
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from JOIN_PERMISSION_OBJECT WHERE OBJECT_ID='" + objectId + "'") == 0;
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from TROPIX_OBJECT WHERE ID='" + objectId + "'") == 0;
    execute("DELETE FROM PERMISSION WHERE ID='" + permissionId + "'");
  }

  @Test
  @NotTransactional
  public void deleteWithRoleNotTransactional() {
    final String permissionId = newId(), objectId = newId();

    execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + objectId + "','The name','The description')");
    execute("INSERT INTO PERMISSION(ID) VALUES ('" + permissionId + "')");
    makeDirectPermission(permissionId);
    execute("INSERT INTO JOIN_PERMISSION_OBJECT(PERMISSION_ID, OBJECT_ID) VALUES ('" + permissionId + "','" + objectId + "')");

    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from JOIN_PERMISSION_OBJECT WHERE OBJECT_ID='" + objectId + "'") == 1;
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from TROPIX_OBJECT WHERE ID='" + objectId + "'") == 1;
    objectDao.delete(objectId);
    flush();
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from JOIN_PERMISSION_OBJECT WHERE OBJECT_ID='" + objectId + "'") == 0;
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from TROPIX_OBJECT WHERE ID='" + objectId + "'") == 0;
    execute("DELETE FROM DIRECT_PERMISSION WHERE PERMISSION_ID='" + permissionId + "'");
    execute("DELETE FROM PERMISSION WHERE ID='" + permissionId + "'");
  }

  @Test
  public void deleteInSession() {
    TropixFile tropixFile = new TropixFile();
    tropixFile.setPermissionParents(Sets.<TropixObject>newHashSet());
    tropixFile.setPermissionChildren(Sets.<TropixObject>newHashSet());
    objectDao.saveOrUpdateTropixObject(tropixFile);
    objectDao.delete(tropixFile.getId());
    flush();

    tropixFile = new TropixFile();
    tropixFile.setPermissionParents(Sets.<TropixObject>newHashSet());
    tropixFile.setPermissionChildren(Sets.<TropixObject>newHashSet());
    final ProteomicsRun run = new ProteomicsRun();
    objectDao.saveOrUpdateTropixObject(tropixFile);
    run.setMzxml(tropixFile);
    objectDao.saveOrUpdateTropixObject(run);
    flush();
    run.setMzxml(null);
    objectDao.saveOrUpdateTropixObject(run);
    objectDao.delete(tropixFile.getId());
    flush();
  }

  private enum RoleType {
    USER, GROUP, BOTH
  };

  @Test
  public void deleteUserRoles() {
    deleteRoles(RoleType.USER);
  }

  @Test
  public void deleteGroupRoles() {
    deleteRoles(RoleType.GROUP);
  }

  @Test
  public void deleteBothRoles() {
    deleteRoles(RoleType.BOTH);
  }

  public void deleteRoles(final RoleType roleType) {
    final String userId = newId();
    final String objId = newId();
    final String fileId = newId();
    String roleId, groupRoleId;
    final String groupId = newId();
    final String folderId = newId();

    // Setup user
    execute("INSERT INTO USER(ID,CAGRID_ID) VALUES ('" + userId + "','" + userId + "')");

    // Setup group
    execute("INSERT INTO TROPIX_GROUP(ID) VALUES ('" + groupId + "')");
    execute("INSERT INTO JOIN_USER_GROUP(USER_ID,GROUP_ID) VALUES ('" + userId + "','" + groupId + "')");

    // Setup folder
    execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + folderId + "','The name','The description')");
    execute("INSERT INTO FOLDER(OBJECT_ID) VALUES ('" + folderId + "')");

    // setup file
    execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + fileId + "','The name','The description')");
    execute("INSERT INTO FILE(OBJECT_ID) VALUES ('" + fileId + "')");

    // setup object
    execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION,FOLDER_ID) VALUES ('" + objId + "','The name','The description','" + folderId + "')");
    execute("INSERT INTO RUN(OBJECT_ID) VALUES ('" + objId + "')");
    execute("INSERT INTO PROTEOMICS_RUN(RUN_ID,SOURCE_ID) VALUES ('" + objId + "','" + fileId + "')");

    // setup role for object and file
    roleId = this.newPermission("read");
    groupRoleId = this.newPermission("read");

    final String newPerm1 = newPermission("read"), newPerm2 = newPermission("read");
    this.addObjectPermission(newPerm1, fileId);
    this.addObjectPermission(newPerm2, folderId);
    if(roleType != RoleType.GROUP) {
      this.addObjectPermission(roleId, objId);
      this.addUserPermission(roleId, userId);
      this.addUserPermission(newPerm1, userId);
      this.addUserPermission(newPerm2, userId);
    }

    if(roleType != RoleType.USER) {
      this.addObjectPermission(groupRoleId, objId);
      this.addGroupPermission(groupRoleId, groupId);
      this.addGroupPermission(newPerm1, groupId);
      this.addGroupPermission(newPerm2, groupId);
    }

    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from FOLDER WHERE OBJECT_ID='" + folderId + "'") == 1;
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from USER WHERE ID='" + userId + "'") == 1;
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from PROTEOMICS_RUN WHERE RUN_ID='" + objId + "'") == 1;
    if(roleType != RoleType.GROUP) {
      assert simpleJdbcTemplate.queryForInt("SELECT count(*) from PERMISSION WHERE ID='" + roleId + "'") == 1;
    }
    if(roleType != RoleType.USER) {
      assert simpleJdbcTemplate.queryForInt("SELECT count(*) from PERMISSION WHERE ID='" + groupRoleId + "'") == 1;
    }

    objectDao.delete(objId);

    flush();
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from FOLDER WHERE OBJECT_ID='" + folderId + "'") == 1;
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from USER WHERE ID='" + userId + "'") == 1;
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from PROTEOMICS_RUN WHERE RUN_ID='" + objId + "'") == 0;
  }

  @Test
  public void getOwnerId() {
    final String userId = newUser();
    final String id1 = newObjectWithOwner(userId);

    flush();
    final String ownerId = objectDao.getOwnerId(id1);
    assert ownerId.equals(userId) : ownerId;
  }

  private String newObjectWithOwner(final String userId) {
    final String roleId = newDirectPermissionForUser(userId);
    final String id1 = newObjectWithPermission(roleId);
    return id1;
  }

  @Test
  public void testUserIsOwner() {
    final String userId = newUser();
    final String roleId = newDirectPermissionForUser(userId);
    final String id1 = newObjectWithPermission(roleId);
    flush();
    assert objectDao.isAnOwner(userId, id1);
  }

  @Test
  public void testProviderIsOwner() {
    final String userId = newUser();
    final String groupId = newGroupWithUser(userId);
    final String providerId = newProviderForGroup(groupId);
    final String objectId = newObjectWithPermission(providerId);
    flush();
    assert objectDao.isAnOwner(userId, objectId);
  }

  @Test
  public void testGetGroupFolder() {
    final String userId = newUser();
    final String groupId = newGroupWithUser(userId);
    final String providerId = newProviderForGroup(groupId);
    final String objectId = newObjectWithPermission(providerId);
    makeFolder(objectId);

    final Collection<Folder> groupFolders = this.objectDao.getGroupFolders(userId);
    assert Iterables.getOnlyElement(groupFolders).getId().equals(objectId);
  }

  @Test
  public void testGetRoleForObjectsUserOwner() {
    final String userId = newUser();
    final String roleId = newDirectPermissionForUser(userId);
    final String id1 = newObjectWithPermission(roleId);
    flush();
    final Multimap<String, String> roleMap = objectDao.getRoles(userId, Lists.newArrayList(id1));
    roleMap.get(id1).contains("owner");
  }

  @Test
  public void testGetRoleForObjectsUserMultiple() {
    final String userId = newUser();
    final String roleId = newDirectPermissionForUser(userId);
    final String realRoleId = newDirectPermissionForUser(userId, "read");
    final String id1 = newObjectWithPermission(roleId);
    addObjectPermission(realRoleId, id1);
    flush();
    final Multimap<String, String> roleMap = objectDao.getRoles(userId, Lists.newArrayList(id1));
    roleMap.get(id1).containsAll(Lists.newArrayList("read", "owner"));
  }

  @Test
  public void testGetRoleForObjectsGroupMultiple() {
    final String userId = newUser();
    final String groupId = newGroupWithUser(userId);
    final String readId = newDirectPermissionForGroup(groupId, "read");
    final String writeId = newDirectPermissionForGroup(groupId, "write");
    final String id1 = newObjectWithPermission(readId);
    addObjectPermission(writeId, id1);
    flush();
    final Multimap<String, String> roleMap = objectDao.getRoles(userId, Lists.newArrayList(id1));
    roleMap.get(id1).containsAll(Lists.newArrayList("read", "write"));

  }

  @Test
  public void testGetAllGroupFolders() {
    final String userId = newUser();
    final String groupId = newGroupWithUser(userId);
    final String providerId = newProviderForGroup(groupId);
    final String objectId = newObjectWithPermission(providerId);
    makeFolder(objectId);

    final Collection<Folder> groupFolders = this.objectDao.getAllGroupFolders();
    assert groupFolders.contains(this.objectDao.loadTropixObject(objectId));
  }

  @Test
  public void getSharedFoldersFromGroup() {
    final String objectId = newId();
    final String userId = newUser();
    final String groupId = newGroupWithUser(userId);
    execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + objectId + "','The name1','The description2')");
    execute("INSERT INTO VIRTUAL_FOLDER(OBJECT_ID,ROOT) VALUES ('" + objectId + "','1')");

    final String permId = this.newPermission("read");
    execute("INSERT INTO VIRTUAL_PERMISSION(PERMISSION_ID,ROOT_VIRTUAl_FOLDER_ID) VALUES ('" + permId + "','" + objectId + "')");
    this.addObjectPermission(permId, objectId);
    this.addGroupPermission(permId, groupId);

    final DaoFactory daoFactory = applicationContext.getBean(DaoFactory.class);
    final Dao<VirtualPermission> virtualPermissionDao = daoFactory.getDao(VirtualPermission.class);
    flush();

    final VirtualPermission vp = virtualPermissionDao.load(permId);
    assert !vp.getGroups().isEmpty();
    assert !vp.getObjects().isEmpty();
    assert !vp.getGroups().iterator().next().getUsers().isEmpty();
    assert vp.getRootVirtualFolder().getRoot();

    final Collection<VirtualFolder> folders = objectDao.getSharedFolders(userId);
    assert folders.size() == 1 : folders.size();
  }

  @Test
  public void getSharedFolders() {
    final String objectId = newId();
    String permId;
    final String userId = newId();
    execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + objectId + "','The name1','The description2')");
    execute("INSERT INTO VIRTUAL_FOLDER(OBJECT_ID,ROOT) VALUES ('" + objectId + "','1')");

    execute("INSERT INTO USER(ID,CAGRID_ID) VALUES ('" + userId + "','" + userId + "')");
    permId = newPermission("read");
    execute("INSERT INTO VIRTUAL_PERMISSION(PERMISSION_ID,ROOT_VIRTUAl_FOLDER_ID) VALUES ('" + permId + "','" + objectId + "')");
    execute("INSERT INTO JOIN_PERMISSION_OBJECT(PERMISSION_ID,OBJECT_ID) VALUES ('" + permId + "','" + objectId + "')");
    execute("INSERT INTO JOIN_PERMISSION_USER(PERMISSION_ID,USER_ID) VALUES ('" + permId + "','" + userId + "')");
    flush();
    final Collection<VirtualFolder> folders = objectDao.getSharedFolders(userId);
    assert folders.size() == 1 : folders.size();
  }

  // The following two tests aren't good unit tests, because they rely on createVirtualPermission and
  // copyVirtualPermissions to test getRootVirtualFolderId and virtualHeirarcyCount respectively.
  // They should be refactored to use SQL directly to setup the required hierarchies and permissions.

  @Test
  public void getRoot() {
    final VirtualFolder folder1 = new VirtualFolder(), folder2 = new VirtualFolder(), folder3 = new VirtualFolder();
    folder1.setContents(new HashSet<TropixObject>());
    folder2.setContents(new HashSet<TropixObject>());
    folder3.setContents(new HashSet<TropixObject>());
    objectDao.saveOrUpdateTropixObject(folder1);
    objectDao.saveOrUpdateTropixObject(folder2);
    objectDao.saveOrUpdateTropixObject(folder3);

    folder1.getContents().add(folder2);
    objectDao.saveOrUpdateTropixObject(folder1);
    folder2.getContents().add(folder3);
    objectDao.saveOrUpdateTropixObject(folder2);

    objectDao.createVirtualPermission(folder1.getId(), "test");
    objectDao.copyVirtualPermissions(folder1.getId(), folder2.getId());
    objectDao.copyVirtualPermissions(folder2.getId(), folder3.getId());
    flush();
    assert folder1.getId().equals(objectDao.getRootVirtualFolderId(folder3.getId()));
  }

  @Test
  public void vhCount() {
    final VirtualFolder folder1 = new VirtualFolder(), folder2 = new VirtualFolder(), folder3 = new VirtualFolder();
    folder1.setContents(new HashSet<TropixObject>());
    folder2.setContents(new HashSet<TropixObject>());
    folder3.setContents(new HashSet<TropixObject>());
    objectDao.saveOrUpdateTropixObject(folder1);
    objectDao.saveOrUpdateTropixObject(folder2);
    objectDao.saveOrUpdateTropixObject(folder3);

    final TropixObject object = new TropixObject();
    objectDao.saveOrUpdateTropixObject(object);

    folder1.getContents().add(folder2);
    objectDao.saveOrUpdateTropixObject(folder1);
    folder2.getContents().add(folder3);
    objectDao.saveOrUpdateTropixObject(folder2);

    objectDao.createVirtualPermission(folder1.getId(), "test1");
    objectDao.createVirtualPermission(folder1.getId(), "test2");

    objectDao.copyVirtualPermissions(folder1.getId(), folder2.getId());
    objectDao.copyVirtualPermissions(folder2.getId(), folder3.getId());
    flush();
    assert 0 == objectDao.virtualHierarchyCount(object.getId(), folder1.getId());

    folder2.getContents().add(object);
    objectDao.saveOrUpdateTropixObject(folder2);
    long count = objectDao.virtualHierarchyCount(object.getId(), folder1.getId());
    assert count == 1 : count;

    folder3.getContents().add(object);
    objectDao.saveOrUpdateTropixObject(folder3);
    count = objectDao.virtualHierarchyCount(object.getId(), folder1.getId());
    assert count == 2 : count;

  }

  private String newObjectInFolder(final String folderId) {
    return newObjectInFolderWithName(folderId, newId());
  }

  private String newObjectInFolderWithName(final String folderId, final String name) {
    final String objectId = newId();
    if(folderId != null) {
      execute("INSERT INTO TROPIX_OBJECT(ID,NAME, DESCRIPTION, FOLDER_ID, COMMITTED) VALUES ('%s','%s', 'Test description', '%s', 1)", objectId,
          name, folderId);
    } else {
      execute("INSERT INTO TROPIX_OBJECT(ID,NAME, DESCRIPTION, COMMITTED) VALUES ('%s','%s', 'Test description', 1)", objectId, name);
    }
    return objectId;
  }

  private String newObjectWithName(final String name) {
    return newObjectInFolderWithName(null, name);
  }

  public String newObject() {
    return newObjectWithName(newId());
  }

  public String makeFile(final String objectId) {
    final String fileId = newId();
    execute("INSERT INTO FILE (OBJECT_ID, FILE_ID) VALUES ('%s', '%s')", objectId, fileId);
    return fileId;
  }

  public String makeFileForOwner(final String userId) {
    final String objectId = newObjectWithOwner(userId);
    return makeFile(objectId);
  }

  public String newFolderInFolder(final String parentId) {
    final String objectId = newObjectInFolder(parentId);
    makeFolder(objectId);
    return objectId;
  }

  public String newFolder() {
    final String objectId = newObject();
    makeFolder(objectId);
    return objectId;
  }

  public void makeFolder(final String objectId) {
    execute("INSERT INTO FOLDER (OBJECT_ID) VALUES ('%s')", objectId);
  }

  private void makeVirtualFolder(final String objectId, final boolean root) {
    execute("INSERT INTO VIRTUAL_FOLDER(OBJECT_ID, ROOT) VALUES ('%s','%d')", objectId, root ? 1 : 0);
  }

  private String newDirectPermissionForGroup(final String groupId, final String role) {
    final String roleId = newDirectPermission(role);
    addGroupPermission(roleId, groupId);
    return roleId;
  }

  private String newDirectPermissionForUser(final String userId, final String role) {
    final String roleId = newDirectPermission(role);
    addUserPermission(roleId, userId);
    return roleId;
  }

  private String newDirectPermissionForUser(final String userId) {
    return newDirectPermissionForUser(userId, "owner");
  }

  private String newDirectPermission(final String role) {
    final String roleId = newPermission(role);
    makeDirectPermission(roleId);
    return roleId;
  }

  public String newObjectWithPermission(final String permissionId) {
    final String objectId = newObject();
    addObjectPermission(permissionId, objectId);
    return objectId;
  }

  public String newProviderForGroup(final String groupId) {
    final String providerId = newPermission("write");
    makeProvider(providerId);
    addGroupPermission(providerId, groupId);
    return providerId;
  }

  public String newGroupWithUser(final String userId) {
    final String groupId = newId(), groupName = newId();
    execute("INSERT INTO TROPIX_GROUP(ID,NAME) VALUES ('" + groupId + "','" + groupName + "')");
    execute("INSERT INTO JOIN_USER_GROUP(USER_ID,GROUP_ID) VALUES ('" + userId + "','" + groupId + "')");
    return groupId;
  }

  public String newUser() {
    final String userId = newId();
    String query = "INSERT INTO USER(ID,CAGRID_ID) VALUES ('" + userId + "','" + userId + "')";
    execute(query);
    return userId;
  }

  private class UserInfo {
    String identity = newId();
    String id = newId();
  }

  private UserInfo newUserIdentity() {
    final UserInfo userInfo = new UserInfo();
    final String query = "INSERT INTO USER(ID,CAGRID_ID) VALUES ('" + userInfo.id + "','" + userInfo.identity + "')";
    execute(query);
    return userInfo;
  }

  private void makeDirectPermission(final String permissionId) {
    execute("INSERT INTO DIRECT_PERMISSION(PERMISSION_ID) VALUES ('%s')", permissionId);
  }

  private String newPermission(final String role) {
    final String id = newId();
    final String query = "INSERT INTO PERMISSION(ID,ROLE) VALUES ('" + id + "','" + role + "')";
    execute(query);
    return id;
  }

  private void makeProvider(final String providerId) {
    execute("INSERT INTO PROVIDER(PERMISSION_ID) VALUES ('%s')", providerId);
  }

  private void addUserPermission(final String permissionId, final String userId) {
    execute("INSERT INTO JOIN_PERMISSION_USER(PERMISSION_ID,USER_ID) VALUES ('" + permissionId + "','" + userId + "')");
  }

  private void addGroupPermission(final String permissionId, final String groupId) {
    execute("INSERT INTO JOIN_PERMISSION_GROUP(PERMISSION_ID,GROUP_ID) VALUES ('" + permissionId + "','" + groupId + "')");
  }

  private void addObjectPermission(final String permissionId, final String objectId) {
    execute("INSERT INTO JOIN_PERMISSION_OBJECT(PERMISSION_ID,OBJECT_ID) VALUES ('" + permissionId + "','" + objectId + "')");
  }

  public String newPermission() {
    return newPermission("owner");
  }

  private void execute(final String query, final Object... args) {
    super.simpleJdbcTemplate.getJdbcOperations().execute(String.format(query, args));
  }

}
