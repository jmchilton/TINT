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

import javax.annotation.Nullable;

import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.models.utils.TropixObjectType;
import edu.umn.msi.tropix.persistence.aop.AutoUser;
import edu.umn.msi.tropix.persistence.aop.MemberOf;
import edu.umn.msi.tropix.persistence.aop.Modifies;
import edu.umn.msi.tropix.persistence.aop.Owns;
import edu.umn.msi.tropix.persistence.aop.PersistenceMethod;
import edu.umn.msi.tropix.persistence.aop.Reads;
import edu.umn.msi.tropix.persistence.aop.UserId;
import edu.umn.msi.tropix.persistence.service.permission.PermissionReport;
import edu.umn.msi.tropix.persistence.service.permission.PermissionType;

public interface TropixObjectService extends TropixObjectLoaderService, TropixObjectDeleter {

  @PersistenceMethod
  void commit(@UserId String userId, @Modifies String objectId);

  @PersistenceMethod
  TropixObject[] loadRecent(@UserId String userId, int maxNum, boolean includeFolders, @Nullable TropixObjectType[] types, boolean requireParentFolder);

  @PersistenceMethod
  TropixFile createFile(@UserId String userGridId, @Nullable @Modifies String folderId, TropixFile file, @Nullable String fileTypeId);

  @PersistenceMethod
  void update(@UserId String userId, @Modifies(method = "getId") TropixObject object);

  @PersistenceMethod
  void move(@UserId String cagridId, @Owns String objectId, @Owns String folderId);

  @PersistenceMethod
  void moveVirtually(@UserId String cagridId, @Modifies String parentFolderId, @Modifies String objectId, @Modifies String newParentfolderId);

  @PersistenceMethod
  TropixObject[] getAssociations(@UserId String cagridId, @Reads String objectId, String associationName);

  @PersistenceMethod
  TropixObject getAssociation(@UserId String cagridId, @Reads String objectId, String associationName);

  @PersistenceMethod
  PermissionReport[] getPermissionReports(@UserId String cagridId, @Reads String objectId);

  String getOwnerId(String objectId);

  @PersistenceMethod
  VirtualFolder[] getSharedFolders(@UserId String userId);

  @PersistenceMethod
  void addSharedFolder(@UserId String userId, @Reads String virtualFolderId);

  @PersistenceMethod
  void addGroupSharedFolder(@UserId String gridId, @MemberOf String groupId, @Reads String folderId);

  @PersistenceMethod
  void addPermissionForGroup(@UserId String userId, @Owns String objectId, String groupId, PermissionType permissionType);

  @PersistenceMethod
  void addPermissionForUser(@UserId String ownerId, @Owns String objectId, @AutoUser String userId, PermissionType permissionType);

  @PersistenceMethod
  void removePermissionForGroup(@UserId String userId, @Owns String objectId, String groupId, PermissionType permissionType);

  @PersistenceMethod
  void removePermissionForUser(@UserId String ownerId, @Owns String objectId, String userId, PermissionType permissionType);

  @PersistenceMethod
  void addVirtualPermissionForUser(@UserId String ownerId, @Modifies String objectId, @AutoUser String userId, PermissionType type);

  @PersistenceMethod
  void addVirtualPermissionForGroup(@UserId String userId, @Modifies String objectId, String groupId, PermissionType type);

  @PersistenceMethod
  void removeVirtualPermissionForUser(@UserId String ownerId, @Modifies String objectId, String userId, PermissionType type);

  @PersistenceMethod
  void removeVirtualPermissionForGroup(@UserId String userId, @Modifies String objectId, String groupId, PermissionType type);

  @PersistenceMethod
  void addToSharedFolder(@UserId String gridId, @Owns String objectId, @Modifies String virtualFolderId, boolean recursive);

  @PersistenceMethod
  void removeFromSharedFolder(@UserId String gridId, @Modifies String virtualFolderId, String objectId);

  @PersistenceMethod
  void deleteVirtualFolder(@UserId String cagridId, @Modifies String id);

  @PersistenceMethod
  VirtualFolder getRoot(@UserId String gridId, @Reads String virtualFolderId);

  @PersistenceMethod
  boolean canModifySharing(@UserId String gridId, @Reads String objectId);

  @PersistenceMethod
  TropixFile loadFileWithFileId(@UserId String gridId, String fileId);

  @PersistenceMethod
  void removeSharedFolder(@UserId String cagridId, String rootSharedFolderId, boolean removeOwnedObjects);

  @PersistenceMethod
  long ownedObjectsVirtualHierarchyCount(@UserId String cagridId, @Reads String rootSharedFolderId);

  @PersistenceMethod
  boolean canModify(@UserId String userId, @Reads String objectId);

  @PersistenceMethod
  TropixObject[] getChildren(@UserId final String userGridId, @Reads final String objectId, final TropixObjectType[] types);

  @PersistenceMethod
  TropixObject[] getChildren(@UserId final String userGridId, @Reads final String objectId);

  @PersistenceMethod
  TropixObject getPath(@AutoUser @UserId final String userGridId, final String[] names);

  @PersistenceMethod
  void hideSharedFolder(@UserId final String cagridId, @Reads final String rootSharedFolderId);

  @PersistenceMethod
  void hideGroupSharedFolder(@UserId final String cagridId, @MemberOf String groupId, @Reads final String rootSharedFolderId);

}