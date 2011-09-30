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

import com.google.common.collect.Sets;

import edu.umn.msi.tropix.models.InternalRequest;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.Request;
import edu.umn.msi.tropix.models.Sample;
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
    final String objectId = newId();
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + objectId + "','The name1','The description2')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO FILE(OBJECT_ID) VALUES ('" + objectId + "')");

    assert objectDao.isInstance(objectId, TropixFile.class);
  }

  @Test
  public void isInstanceCacheTest() {
    final String objectId = newId(), requestorId = newId(), externalId = newId();
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + objectId + "','The name1','The description2')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO REQUEST(OBJECT_ID,REQUESTOR_ID,EXTERNAL_ID) VALUES ('" + objectId + "','" + requestorId + "','" + externalId + "')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO INTERNAL_REQUEST(REQUEST_ID,STORAGE_SERVICE_URL) VALUES ('" + objectId + "','http://moo.cow')");

    // Load it as a request..
    objectDao.loadTropixObject(objectId, Request.class);

    // Check if it is cached as an InternalRequest...
    assert objectDao.isInstance(objectId, InternalRequest.class);
  }

  @Test
  public void loadRequest() {
    final String objectId = newId(), requestorId = newId(), externalId = newId();
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + objectId + "','The name1','The description2')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO REQUEST(OBJECT_ID,REQUESTOR_ID,EXTERNAL_ID) VALUES ('" + objectId + "','" + requestorId + "','" + externalId + "')");

    Request request = objectDao.loadRequest(requestorId, externalId);
    assert request.getId().equals(objectId);
    request = objectDao.loadRequest(requestorId, newId());
    assert request == null;
    request = objectDao.loadRequest(newId(), externalId);
    assert request == null;

  }

  @Test
  public void loadRequestInternal() {
    final String objectId = newId(), requestorId = newId(), externalId = newId();
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + objectId + "','The name1','The description2')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO REQUEST(OBJECT_ID,REQUESTOR_ID,EXTERNAL_ID) VALUES ('" + objectId + "','" + requestorId + "','" + externalId + "')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO INTERNAL_REQUEST(REQUEST_ID,STORAGE_SERVICE_URL) VALUES ('" + objectId + "','http://moo.cow')");

    InternalRequest request = (InternalRequest) objectDao.loadRequest(requestorId, externalId);
    assert request.getStorageServiceUrl().equals("http://moo.cow");
    assert request.getId().equals(objectId);
    request = (InternalRequest) objectDao.loadRequest(requestorId, newId());
    assert request == null;
    request = (InternalRequest) objectDao.loadRequest(newId(), externalId);
    assert request == null;

  }

  private String newPermission(final String role) {
    final String id = newId();
    final String query = "INSERT INTO PERMISSION(ID,ROLE) VALUES ('" + id + "','" + role + "')";
    simpleJdbcTemplate.getJdbcOperations().execute(query);
    return id;
  }

  private void addUserPermission(final String permissionId, final String userId) {
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO JOIN_PERMISSION_USER(PERMISSION_ID,USER_ID) VALUES ('" + permissionId + "','" + userId + "')");
  }

  private void addGroupPermission(final String permissionId, final String groupId) {
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO JOIN_PERMISSION_GROUP(PERMISSION_ID,GROUP_ID) VALUES ('" + permissionId + "','" + groupId + "')");
  }

  private void addObjectPermission(final String permissionId, final String objectId) {
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO JOIN_PERMISSION_OBJECT(PERMISSION_ID,OBJECT_ID) VALUES ('" + permissionId + "','" + objectId + "')");
  }

  @Test
  public void ownsSharedFolderWithName() {
    // TODO: Run more tests

    final String objectId = newId(), userId = newId(), user2Id = newId();
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + objectId + "','The name1','The description2')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO VIRTUAL_FOLDER(OBJECT_ID,ROOT) VALUES ('" + objectId + "','1')");

    final String user1Query = "INSERT INTO USER(ID,CAGRID_ID) VALUES ('" + userId + "','" + userId + "')";
    simpleJdbcTemplate.getJdbcOperations().execute(user1Query);
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO USER(ID,CAGRID_ID) VALUES ('" + user2Id + "','" + user2Id + "')");

    final String ownerPermissionId = newPermission("owner");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO JOIN_PERMISSION_USER(PERMISSION_ID,USER_ID) VALUES ('" + ownerPermissionId + "','" + userId + "')");
    addObjectPermission(ownerPermissionId, objectId);

    flush();

    assert objectDao.ownsSharedFolderWithName(userId, "The name1");
    assert !objectDao.ownsSharedFolderWithName(user2Id, "The name1");

  }

  @Test
  public void loadFileWithFileId() {
    final String id1 = newId(), fileId = newId();

    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + id1 + "','The name','The description')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO FILE(OBJECT_ID, FILE_ID) VALUES ('" + id1 + "','" + fileId + "')");

    assert objectDao.loadTropixFileWithFileId(fileId).getName().equals("The name");
  }

  @Test
  public void loadFileWithFileIdNotPresent() {
    final String id1 = newId(), fileId = newId();

    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + id1 + "','The name','The description')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO FILE(OBJECT_ID, FILE_ID) VALUES ('" + id1 + "','" + fileId + "')");

    assert objectDao.loadTropixFileWithFileId(id1) == null;
  }

  @Test
  public void getOwnerId() {
    final String id1 = newId(), userId = newId();

    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + id1 + "','The name','The description')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO FILE(OBJECT_ID) VALUES ('" + id1 + "')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO USER(ID,CAGRID_ID,FIRST_NAME) VALUES ('" + userId + "','" + userId + "','Homer')");
    final String roleId = this.newPermission("owner");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO DIRECT_PERMISSION(PERMISSION_ID) VALUES ('" + roleId + "')");
    this.addObjectPermission(roleId, id1);
    this.addUserPermission(roleId, userId);
    flush();
    final String ownerId = objectDao.getOwnerId(id1);
    assert ownerId.equals(userId) : ownerId;
  }

  @Test
  public void getAssociation() {
    final String fileId = newId(), runId = newId(), folderId = newId();

    // Setup folder
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + folderId + "','The name','The description')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO FOLDER(OBJECT_ID) VALUES ('" + folderId + "')");

    // setup file
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + fileId + "','The name','The description')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO FILE(OBJECT_ID) VALUES ('" + fileId + "')");

    // setup object
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION,FOLDER_ID) VALUES ('" + runId + "','The name','The description','" + folderId + "')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO RUN(OBJECT_ID) VALUES ('" + runId + "')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO PROTEOMICS_RUN(RUN_ID, SOURCE_ID) VALUES ('" + runId + "','" + fileId + "')");

    final TropixFile file = (TropixFile) objectDao.getAssociation(runId, "source");
    assert file.getId().equals(fileId);
  }

  @Test
  public void getAssociations() {
    final String folderId = newId(), runId = newId(), fileId = newId();

    // Setup folder
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + folderId + "','The name','The description')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO FOLDER(OBJECT_ID) VALUES ('" + folderId + "')");

    // setup file
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION,FOLDER_ID) VALUES ('" + fileId + "','The name','The description','" + folderId + "')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO FILE(OBJECT_ID) VALUES ('" + fileId + "')");

    // setup object
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION,FOLDER_ID) VALUES ('" + runId + "','The name','The description','" + folderId + "')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO RUN(OBJECT_ID) VALUES ('" + runId + "')");

    final Collection<TropixObject> objects = objectDao.getAssociations(folderId, "contents");
    assert objects.size() == 2 : objects.size();
  }

  @Test
  public void polymorphicLoad() {
    final String id1 = newId();
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + id1 + "','The name1','The description2')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO SAMPLE(OBJECT_ID) VALUES ('" + id1 + "')");
    flush();

    final TropixObject object = objectDao.loadTropixObject(id1);
    assert object instanceof Sample;
  }

  @Test
  public void addPermissionParent() {
    final String id1 = newId(), id2 = newId();
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + id1 + "','The name1','The description2')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + id2 + "','The name1','The description2')");
    flush();

    objectDao.addPermissionParent(id1, id2);
    flush();
    assert objectDao.loadTropixObject(id1).getPermissionParents().size() == 1;
    assert objectDao.loadTropixObject(id2).getPermissionChildren().size() == 1;
  }

  @Test
  public void requestContents() {
    final String reqId = newId(), fileId = newId();
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + reqId + "','The name1','The description2')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO REQUEST(OBJECT_ID) VALUES ('" + reqId + "')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION,REQUEST_ID) VALUES ('" + fileId + "','The name3','The description3','" + reqId + "')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO FILE(OBJECT_ID) VALUES ('" + fileId + "')");
    flush();
    final Collection<TropixObject> collect = objectDao.getFolderContents(reqId);
    assert collect.size() == 1;

  }

  @Test
  public void contents() {
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('1234','The name1','The description2')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO FOLDER(OBJECT_ID) VALUES ('1234')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION,FOLDER_ID) VALUES ('1235','The name2','The description2','1234')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO FOLDER(OBJECT_ID) VALUES ('1235')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION,FOLDER_ID) VALUES ('1236','The name3','The description3','1234')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO SAMPLE(OBJECT_ID) VALUES ('1236')");
    flush();
    final Collection<TropixObject> collect = objectDao.getFolderContents("1234");
    assert collect.size() == 2;

    final Collection<TropixObject> collect3 = objectDao.getFolderContents("1235");
    assert collect3.size() == 0;
  }

  @Test
  public void createVirtualPermissions() {
    final String objectId = newId();
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + objectId + "','The name1','The description2')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO VIRTUAL_FOLDER(OBJECT_ID) VALUES ('" + objectId + "')");
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
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO USER(ID,CAGRID_ID,FIRST_NAME) VALUES ('+userId+','" + userId + "','Homer')");
    flush();
  }

  @Test
  public void addVirtualPermissionUser() {
    final String objectId = newId(), userId = newId();
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + objectId + "','The name1','The description2')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO USER(ID,CAGRID_ID,FIRST_NAME) VALUES ('" + userId + "','" + userId + "','Homer')");
    final String permId = this.newPermission("read");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO VIRTUAL_PERMISSION(PERMISSION_ID) VALUES ('" + permId + "')");
    this.addObjectPermission(permId, objectId);
    // simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO JOIN_PERMISSION_OBJECT(VIRTUAL_PERMISSION_ID,OBJECT_ID) VALUES ('"+ permId +"','"+objectId+"')");
    flush();
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from JOIN_PERMISSION_USER WHERE PERMISSION_ID='" + permId + "' AND USER_ID='" + userId + "'") == 0;
    objectDao.addVirtualPermissionUser(objectId, "read", userId);
    flush();
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from JOIN_PERMISSION_USER WHERE PERMISSION_ID='" + permId + "' AND USER_ID='" + userId + "'") == 1;
  }

  @Test
  public void addVirtualPermissionGroup() {
    final String objectId = newId(), groupId = newId();
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + objectId + "','The name1','The description2')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_GROUP(ID) VALUES ('" + groupId + "')");
    final String permId = this.newPermission("read");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO VIRTUAL_PERMISSION(PERMISSION_ID) VALUES ('" + permId + "')");
    this.addObjectPermission(permId, objectId);
    flush();
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from JOIN_PERMISSION_GROUP WHERE PERMISSION_ID='" + permId + "' AND GROUP_ID='" + groupId + "'") == 0;
    objectDao.addVirtualPermissionGroup(objectId, "read", groupId);
    flush();
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from JOIN_PERMISSION_GROUP WHERE PERMISSION_ID='" + permId + "' AND GROUP_ID='" + groupId + "'") == 1;
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
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('356','The name','The description')");
    final TropixObject object = objectDao.loadTropixObject("356");
    assert object.getId().equals("356");
    assert object.getDescription().equals("The description");
    assert object.getName().equals("The name");

    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('357','The name','The description')");
    assert objectDao.loadTropixObjects(new String[] {"356", "357"}).size() == 2;
    assert objectDao.loadTropixObjects(new String[] {"356"}).size() == 1;
  }

  @Test
  public void getOwner() {
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO USER(ID,CAGRID_ID) VALUES (111,112)");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID) VALUES (333)");
    final String permissionId = newPermission("owner");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO DIRECT_PERMISSION(PERMISSION_ID) VALUES ('" + permissionId + "')");
    this.addObjectPermission(permissionId, "333");
    this.addUserPermission(permissionId, "111");
    // simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_USER_ROLE(ID,ROLE_NAME,USER_ID,OBJECT_ID) VALUES (123412323,'owner',111,333)");
    flush();
    assert objectDao.getOwner("333").getCagridId().equals("112");

  }

  @Test
  public void setOwner() {
    final User user = getNewUser();
    final String id = user.getCagridId();

    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO USER(ID,CAGRID_ID) VALUES ('" + id + "','" + id + "')");
    // simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_USER_ROLE(ID,ROLE_NAME,USER_ID,OBJECT_ID) VALUES (123412323,'owner',111,333)");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID) VALUES (33323432)");

    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from PERMISSION, JOIN_PERMISSION_USER, JOIN_PERMISSION_OBJECT WHERE PERMISSION.ROLE='owner' AND JOIN_PERMISSION_USER.USER_ID='" + id
        + "' AND JOIN_PERMISSION_OBJECT.OBJECT_ID='33323432' AND JOIN_PERMISSION_OBJECT.PERMISSION_ID=PERMISSION.ID AND JOIN_PERMISSION_USER.PERMISSION_ID=PERMISSION.ID") == 0;
    objectDao.setOwner("33323432", user);
    flush();
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from PERMISSION, JOIN_PERMISSION_USER, JOIN_PERMISSION_OBJECT WHERE PERMISSION.ROLE='owner' AND JOIN_PERMISSION_USER.USER_ID='" + id
        + "' AND JOIN_PERMISSION_OBJECT.OBJECT_ID='33323432' AND JOIN_PERMISSION_OBJECT.PERMISSION_ID=PERMISSION.ID AND JOIN_PERMISSION_USER.PERMISSION_ID=PERMISSION.ID") == 1;
  }

  @Test
  public void delete() {
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('3523426','The name','The description')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO FOLDER(OBJECT_ID) VALUES ('3523426')");
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
    
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + objectId + "','The name','The description')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO PERMISSION(ID) VALUES ('" + permissionId + "')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO JOIN_PERMISSION_OBJECT(PERMISSION_ID, OBJECT_ID) VALUES ('" + permissionId + "','" + objectId + "')");
    
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from JOIN_PERMISSION_OBJECT WHERE OBJECT_ID='"+ objectId + "'") == 1;
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from TROPIX_OBJECT WHERE ID='"+ objectId + "'") == 1;
    objectDao.delete(objectId);
    flush();
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from JOIN_PERMISSION_OBJECT WHERE OBJECT_ID='"+ objectId + "'") == 0;
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from TROPIX_OBJECT WHERE ID='" + objectId + "'") == 0;
    simpleJdbcTemplate.getJdbcOperations().execute("DELETE FROM PERMISSION WHERE ID='" + permissionId + "'");
  }

  @Test @NotTransactional
  public void deleteWithRoleNotTransactional() {
    final String permissionId = newId(), objectId = newId();
    
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + objectId + "','The name','The description')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO PERMISSION(ID) VALUES ('" + permissionId + "')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO DIRECT_PERMISSION(PERMISSION_ID) VALUES ('" + permissionId + "')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO JOIN_PERMISSION_OBJECT(PERMISSION_ID, OBJECT_ID) VALUES ('" + permissionId + "','" + objectId + "')");
    
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from JOIN_PERMISSION_OBJECT WHERE OBJECT_ID='"+ objectId + "'") == 1;
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from TROPIX_OBJECT WHERE ID='"+ objectId + "'") == 1;
    objectDao.delete(objectId);
    flush();
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from JOIN_PERMISSION_OBJECT WHERE OBJECT_ID='"+ objectId + "'") == 0;
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from TROPIX_OBJECT WHERE ID='" + objectId + "'") == 0;
    simpleJdbcTemplate.getJdbcOperations().execute("DELETE FROM DIRECT_PERMISSION WHERE PERMISSION_ID='" + permissionId + "'");
    simpleJdbcTemplate.getJdbcOperations().execute("DELETE FROM PERMISSION WHERE ID='" + permissionId + "'");
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
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO USER(ID,CAGRID_ID) VALUES ('" + userId + "','" + userId + "')");

    // Setup group
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_GROUP(ID) VALUES ('" + groupId + "')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO JOIN_USER_GROUP(USER_ID,GROUP_ID) VALUES ('" + userId + "','" + groupId + "')");

    // Setup folder
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + folderId + "','The name','The description')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO FOLDER(OBJECT_ID) VALUES ('" + folderId + "')");

    // setup file
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + fileId + "','The name','The description')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO FILE(OBJECT_ID) VALUES ('" + fileId + "')");

    // setup object
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION,FOLDER_ID) VALUES ('" + objId + "','The name','The description','" + folderId + "')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO RUN(OBJECT_ID) VALUES ('" + objId + "')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO PROTEOMICS_RUN(RUN_ID,SOURCE_ID) VALUES ('" + objId + "','" + fileId + "')");

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
  public void getSharedFoldersFromGroup() {
    final String objectId = newId(), groupId = newId(), userId = newId();
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + objectId + "','The name1','The description2')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO VIRTUAL_FOLDER(OBJECT_ID,ROOT) VALUES ('" + objectId + "','1')");

    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO USER(ID,CAGRID_ID) VALUES ('" + userId + "','" + userId + "')");

    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_GROUP(ID,NAME) VALUES ('" + groupId + "','Moo')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO JOIN_USER_GROUP(USER_ID,GROUP_ID) VALUES ('" + userId + "','" + groupId + "')");

    final String permId = this.newPermission("read");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO VIRTUAL_PERMISSION(PERMISSION_ID,ROOT_VIRTUAl_FOLDER_ID) VALUES ('" + permId + "','" + objectId + "')");
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
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,NAME,DESCRIPTION) VALUES ('" + objectId + "','The name1','The description2')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO VIRTUAL_FOLDER(OBJECT_ID,ROOT) VALUES ('" + objectId + "','1')");

    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO USER(ID,CAGRID_ID) VALUES ('" + userId + "','" + userId + "')");
    permId = newPermission("read");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO VIRTUAL_PERMISSION(PERMISSION_ID,ROOT_VIRTUAl_FOLDER_ID) VALUES ('" + permId + "','" + objectId + "')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO JOIN_PERMISSION_OBJECT(PERMISSION_ID,OBJECT_ID) VALUES ('" + permId + "','" + objectId + "')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO JOIN_PERMISSION_USER(PERMISSION_ID,USER_ID) VALUES ('" + permId + "','" + userId + "')");
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

}
