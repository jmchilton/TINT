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

package edu.umn.msi.tropix.persistence.dao;

import java.util.Collection;
import java.util.List;

import edu.umn.msi.tropix.models.DirectPermission;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.InternalRequest;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.Request;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.models.VirtualFolder;

public interface TropixObjectDao {

  Collection<Folder> getGroupFolders(final String userId);

  Collection<Folder> getAllGroupFolders();

  Collection<TropixObject> loadRecent(String userId, int num, boolean includeFolders, boolean requireParent);

  void saveOrUpdateTropixObject(TropixObject object);

  TropixObject loadTropixObject(String objectId);

  <T extends TropixObject> T loadTropixObject(String objectId, Class<? extends T> type);

  List<TropixObject> loadTropixObjects(String[] objectIds);

  <T extends TropixObject> List<T> loadTropixObjects(String[] objectIds, Class<? extends T> type);

  Collection<TropixObject> getFolderContents(String folderId);

  void addToFolder(String folderId, String objectId);

  void addToVirtualFolder(String folderId, String objectId);

  User getOwner(String objectId);

  void setOwner(String objectId, User user);

  DirectPermission getGroupDirectRole(String groupId, String objectId);

  DirectPermission getUserDirectRole(String userId, String objectId);

  void addRole(String objectId, String roleName, User user);

  void addGroupRole(String objectId, String roleName, Group group);

  void delete(String objectId);

  void move(String objectId, String folderId);

  void addPermissionParent(String childId, String parentId);

  void removePermissionParent(String childId, String parentId);

  List<TropixObject> getTopLevelObjects(String userId, String ownerId);

  List<TropixObject> searchObjects(String userId, Class<? extends TropixObject> type, String name, String description, String ownerId);

  List<TropixObject> quickSearchObjects(String userId, String query);

  Collection<TropixObject> getAssociations(String objectId, String associationName);

  TropixObject getAssociation(String objectId, String associationName);

  void createVirtualPermission(String objectId, String role);

  void copyVirtualPermissions(String fromObjectId, String toObjectId);

  void addVirtualPermissionUser(String objectId, String role, String userId);

  void addVirtualPermissionGroup(String objectId, String role, String groupId);

  void removeVirtualPermissionUser(String objectId, String role, String userId);

  void removeVirtualPermissionGroup(String objectId, String role, String groupId);

  Collection<DirectPermission> getRoles(String objectId);

  // public Collection<Permission> getGroupRoles(String objectId);

  String getOwnerId(String objectId);

  Collection<VirtualFolder> getSharedFolders(String userId);

  void removeFromVirtualFolder(String virtualFolderId, String objectId);

  void dropVirtualPermission(String virtualFolderId, String objectId);

  String getRootVirtualFolderId(String virtualFolderId);

  long virtualHierarchyCount(String objectId, String rootId);

  Collection<User> getVirtualPermissionUsers(String objectId, String role);

  Collection<Group> getVirtualPermissionGroups(String objectId, String role);

  TropixFile loadTropixFileWithFileId(String fileId);

  boolean ownsSharedFolderWithName(String userGridId, String name);

  Request loadRequest(String newId, String externalId);

  Collection<Request> getActiveRequests(String gridId);

  boolean isInstance(String objectId, Class<? extends TropixObject> class1);

  Collection<InternalRequest> getInternalRequests(String gridId);

  Collection<ProteomicsRun> getRunsFromScaffoldAnalysis(String scaffoldId);

  boolean isAHomeFolder(String id);

  long ownedObjectsVirtualHierarchyCount(String userGridId, String rootVirtualFolderId);

  TropixObject getHomeDirectoryPath(String userId, List<String> asList);

  TropixObject getGroupDirectoryPath(String userId, List<String> asList);

  TropixObject getSharedDirectoryPath(String userId, List<String> asList);

  boolean isAnOwner(final String userId, final String objectId);

}
