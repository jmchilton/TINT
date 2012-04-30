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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.annotation.ManagedBean;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.models.DirectPermission;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.models.locations.Locations;
import edu.umn.msi.tropix.models.utils.ModelUtils;
import edu.umn.msi.tropix.models.utils.StockFileExtensionEnum;
import edu.umn.msi.tropix.models.utils.TropixObjectType;
import edu.umn.msi.tropix.persistence.dao.Dao;
import edu.umn.msi.tropix.persistence.dao.FileTypeDao;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;
import edu.umn.msi.tropix.persistence.service.impl.utils.PersistenceModelUtils;
import edu.umn.msi.tropix.persistence.service.permission.PermissionReport;
import edu.umn.msi.tropix.persistence.service.permission.PermissionSourceType;
import edu.umn.msi.tropix.persistence.service.permission.PermissionType;

@ManagedBean
@Named("tropixObjectService")
class TropixObjectServiceImpl extends ServiceBase implements TropixObjectService {
  private static final Log LOG = LogFactory.getLog(TropixObjectServiceImpl.class);
  private final FileTypeDao fileTypeDao;

  @Inject
  TropixObjectServiceImpl(final FileTypeDao fileTypeDao) {
    this.fileTypeDao = fileTypeDao;
  }

  public TropixObject[] getChildren(final String userGridId, final String objectId, final TropixObjectType[] types) {
    final TropixObject object = getTropixObjectDao().loadTropixObject(objectId);
    return filter(PersistenceModelUtils.typeFilter(ModelUtils.getChildren(object), Arrays.asList(types)), userGridId);
  }

  public TropixObject[] getChildren(final String userGridId, final String objectId) {
    final TropixObject object = getTropixObjectDao().loadTropixObject(objectId);
    return filter(ModelUtils.getChildren(object), userGridId);
  }

  public TropixObject[] load(final String userId, final String[] objectIds, final TropixObjectType type) {
    final Class<? extends TropixObject> clazz = PersistenceModelUtils.getClass(type);
    return filter(getTropixObjectDao().loadTropixObjects(objectIds, clazz), userId);
  }

  public TropixObject load(final String userId, final String objectId) {
    return filterObject(getTropixObjectDao().loadTropixObject(objectId), userId);
  }

  public void update(final String userId, final TropixObject object) {
    getTropixObjectDao().saveOrUpdateTropixObject(object);
  }

  public TropixObject load(final String userId, final String objectId, final TropixObjectType type) {
    final Class<? extends TropixObject> clazz = PersistenceModelUtils.getClass(type);
    final TropixObject object = getTropixObjectDao().loadTropixObject(objectId, clazz);
    clazz.cast(object); // I cannot believe this needs to be here, try removing it.
    return object;
  }

  private boolean isAncestor(final VirtualFolder inputChild, final VirtualFolder potentialAncestor) {
    boolean isAncestor = false;
    VirtualFolder potentialChild = inputChild;
    while(true) {
      if(potentialChild.getId().equals(potentialAncestor.getId())) {
        isAncestor = true;
      }
      if(potentialChild.getRoot()) {
        break;
      } else {
        potentialChild = potentialChild.getParentVirtualFolders().iterator().next();
      }
    }
    return isAncestor;
  }

  public void moveVirtually(final String cagridId, final String parentFolderId, final String objectId, final String newParentFolderId) {
    if(!getTropixObjectDao().getRootVirtualFolderId(parentFolderId).equals(getTropixObjectDao().getRootVirtualFolderId(newParentFolderId))) {
      throw new RuntimeException("Virtual Folders belong to different hierarchies");
    }

    final VirtualFolder newParent = loadSharedFolder(newParentFolderId);
    final TropixObject object = getTropixObjectDao().loadTropixObject(objectId);

    if(object instanceof VirtualFolder) {
      if(isAncestor(newParent, (VirtualFolder) object)) {
        throw new RuntimeException("Ancestor exception");
      }
    }
    if(newParent.getContents().contains(object)) {
      throw new RuntimeException("Object alread exists in destination folder");
    }

    getTropixObjectDao().removeFromVirtualFolder(parentFolderId, object.getId());
    getTropixObjectDao().addToVirtualFolder(newParentFolderId, object.getId());
  }

  public void move(final String cagridId, final String objectId, final String folderId) {
    final TropixObject object = getTropixObjectDao().loadTropixObject(objectId, TropixObject.class);
    final Folder objectParentFolder = getTropixObjectDao().loadTropixObject(objectId, TropixObject.class).getParentFolder();
    if(objectParentFolder == null) {
      throw new IllegalArgumentException("Attempted to move a users home folder or an object not currently in the folder hierarchy.");
    }
    Folder objectAncestor = objectParentFolder;
    while(objectAncestor.getParentFolder() != null) {
      objectAncestor = objectAncestor.getParentFolder();
    }
    Folder folderDestinationAncestor = getTropixObjectDao().loadTropixObject(folderId, Folder.class);
    while(folderDestinationAncestor.getParentFolder() != null) {
      if(folderDestinationAncestor.getId().equals(objectId)) {
        if(object instanceof Folder) {
          throw new IllegalArgumentException("Attempted to move a folder into a child folder");
        }
      }
      folderDestinationAncestor = folderDestinationAncestor.getParentFolder();
    }
    if(objectAncestor.getId().equals(folderDestinationAncestor.getId())) {
      changeParentFolder(objectId, folderId);
    } else {
      if(!objectAncestor.getId().equals(getUserDao().loadUser(cagridId).getHomeFolder().getId())) {
        throw new IllegalArgumentException("Attempt to move an object outside a user's home directory into a group folder, this is not implemented.");
      }
      moveToGroupFolder(cagridId, objectId, folderId);
    }
  }

  private void moveToGroupFolder(String cagridId, String objectId, String folderId) {
    TreeUtils.applyPermissionChange(getTropixObjectDao().loadTropixObject(objectId), new DropOwnerPermission(cagridId));
    changeParentFolder(objectId, folderId);
    TreeUtils.applyPermissionChange(getTropixObjectDao().loadTropixObject(objectId), new CopyOwnerPermission(folderId));
  }

  private void changeParentFolder(final String objectId, final String folderId) {
    removeParentFolderPermissionParent(objectId);
    getTropixObjectDao().move(objectId, folderId);
    addParentFolderPermissionParent(objectId, folderId);
  }

  private void addParentFolderPermissionParent(final String objectId, final String folderId) {
    getTropixObjectDao().addPermissionParent(objectId, folderId);
  }

  private void removeParentFolderPermissionParent(final String objectId) {
    getTropixObjectDao().removePermissionParent(objectId, getTropixObjectDao().loadTropixObject(objectId).getParentFolder().getId());
  }

  public TropixObject[] getAssociations(final String cagridId, final String objectId, final String associationName) {
    Collection<TropixObject> objects = getTropixObjectDao().getAssociations(objectId, associationName);
    if(objects == null) {
      objects = Lists.newLinkedList();
    }
    return filter(objects, cagridId);
  }

  public TropixObject getAssociation(final String cagridId, final String objectId, final String associationName) {
    final TropixObject associatedObject = getTropixObjectDao().getAssociation(objectId, associationName);
    if(associatedObject != null) {
      return filterObject(associatedObject, cagridId);
    } else {
      return null;
    }
  }

  private static PermissionType getStrongerPermission(final PermissionType p1, final PermissionType p2) {
    if(p1.equals(PermissionType.Read) || p2.equals(PermissionType.Owner)) {
      return p2;
    } else if(p2.equals(PermissionType.Read) || p1.equals(PermissionType.Owner)) {
      return p1;
    }
    // They are both write (return either)
    return p2;
  }

  private static PermissionType getPermissionType(final String roleName) {
    if(roleName.equals("read")) {
      return PermissionType.Read;
    } else if(roleName.equals("write")) {
      return PermissionType.Write;
    } else {
      return PermissionType.Owner;
    }
  }

  private String getVirtualFolderPath(final VirtualFolder inputVirtualFolder) {
    final StringBuilder pathBuilder = new StringBuilder();
    VirtualFolder virtualFolder = inputVirtualFolder;
    while(!virtualFolder.getRoot()) {
      pathBuilder.insert(0, " > " + virtualFolder.getName());
      final Iterator<VirtualFolder> iterator = virtualFolder.getParentVirtualFolders().iterator();
      if(!iterator.hasNext()) {
        // This shouldn't happen, but the logs make it look like it has.
        LOG.warn(String.format("Found non-root virtual folder without parent with id %s and name %s", virtualFolder.getId(), virtualFolder.getName()));
        pathBuilder.insert(0, "<insconsistent tree> >");
        break;
      } else {
        virtualFolder = iterator.next();
      }
    }
    pathBuilder.insert(0, virtualFolder.getName());
    return pathBuilder.toString();
  }

  private PermissionReport getPermissionReport(final User user) {
    final String userId = user.getCagridId();
    final PermissionReport report = new PermissionReport();
    report.setId(userId);
    report.setName(userId.substring(userId.lastIndexOf('=') + 1));
    report.setPermissionSource(PermissionSourceType.User);
    return report;
  }

  private PermissionReport getPermissionReport(final Group group) {
    final String groupId = group.getId();
    final PermissionReport report = new PermissionReport();
    report.setId(groupId);
    report.setName(group.getName());
    report.setPermissionSource(PermissionSourceType.Group);
    return report;
  }

  public PermissionReport[] getPermissionReports(final String cagridId, final String objectId) {
    final LinkedList<PermissionReport> reports = new LinkedList<PermissionReport>();
    final TropixObject object = getTropixObjectDao().loadTropixObject(objectId);
    if(object instanceof VirtualFolder && ((VirtualFolder) object).getRoot()) {
      final Map<String, PermissionReport> userReports = new HashMap<String, PermissionReport>();
      final PermissionReport ownerReport = getPermissionReport(getTropixObjectDao().getOwner(object.getId()));
      ownerReport.setPermission(PermissionType.Owner);
      reports.add(ownerReport);
      userReports.put(ownerReport.getId(), ownerReport);

      for(final User writeUser : getTropixObjectDao().getVirtualPermissionUsers(objectId, "write")) {
        if(!userReports.containsKey(writeUser.getCagridId())) {
          final PermissionReport report = getPermissionReport(writeUser);
          report.setPermission(PermissionType.Write);
          reports.add(report);
          userReports.put(report.getId(), report);
        }
      }
      for(final User readUser : getTropixObjectDao().getVirtualPermissionUsers(objectId, "read")) {
        if(!userReports.containsKey(readUser.getCagridId())) {
          final PermissionReport report = getPermissionReport(readUser);
          report.setPermission(PermissionType.Read);
          reports.add(report);
        }
      }

      final Map<String, PermissionReport> groupReports = new HashMap<String, PermissionReport>();
      for(final Group writeGroup : getTropixObjectDao().getVirtualPermissionGroups(objectId, "write")) {
        final PermissionReport report = getPermissionReport(writeGroup);
        report.setPermission(PermissionType.Write);
        reports.add(report);
        groupReports.put(report.getId(), report);
      }
      for(final Group readGroup : getTropixObjectDao().getVirtualPermissionGroups(objectId, "read")) {
        final String groupId = readGroup.getId();
        if(!groupReports.containsKey(groupId)) {
          final PermissionReport report = getPermissionReport(readGroup);
          report.setPermission(PermissionType.Read);
          reports.add(report);
        }
      }
    } else if(!(object instanceof VirtualFolder)) {
      final Map<String, PermissionReport> userReports = new HashMap<String, PermissionReport>();
      final Map<String, PermissionReport> groupReports = new HashMap<String, PermissionReport>();
      final Collection<DirectPermission> permissions = getTropixObjectDao().getRoles(objectId);

      for(final DirectPermission permission : permissions) {
        for(final User user : permission.getUsers()) {
          final String userId = user.getCagridId();
          if(userReports.containsKey(userId)) {
            final PermissionReport report = userReports.get(userId);
            report.setPermission(getStrongerPermission(getPermissionType(permission.getRole()), report.getPermission()));
          } else {
            final PermissionReport report = getPermissionReport(user);
            report.setPermission(getPermissionType(permission.getRole()));
            reports.add(report);
            userReports.put(userId, report);
          }
        }

        for(final Group group : permission.getGroups()) {
          final String groupId = group.getId();
          if(groupReports.containsKey(groupId)) {
            final PermissionReport report = groupReports.get(groupId);
            report.setPermission(getStrongerPermission(getPermissionType(permission.getRole()), report.getPermission()));
          } else {
            final PermissionReport report = getPermissionReport(group);
            report.setPermission(getPermissionType(permission.getRole()));
            reports.add(report);
            groupReports.put(groupId, report);
          }
        }
      }

      for(final VirtualFolder virtualFolder : object.getParentVirtualFolders()) {
        final PermissionReport report = new PermissionReport();
        report.setId(virtualFolder.getId());
        report.setName(getVirtualFolderPath(virtualFolder));
        report.setPermissionSource(PermissionSourceType.SharedFolder);
        reports.add(report);
      }
    }
    return reports.toArray(new PermissionReport[reports.size()]);
  }

  public String getOwnerId(final String objectId) {
    return getTropixObjectDao().getOwnerId(objectId);
  }

  public VirtualFolder[] getSharedFolders(final String userId) {
    return getTropixObjectDao().getSharedFolders(userId).toArray(new VirtualFolder[0]);
  }

  public void addSharedFolder(final String userId, final String virtualFolderId) {
    final User user = getUserDao().loadUser(userId);
    addSharedFolder(virtualFolderId, user.getSharedFolders());
    getUserDao().saveOrUpdateUser(user);
  }

  private void addSharedFolder(final String folderId, final Collection<VirtualFolder> sharedFolders) {
    final VirtualFolder folder = loadSharedFolder(folderId);
    if(!isRoot(folder)) {
      throw new RuntimeException("Attempt to add non root shared folder to user");
    }
    sharedFolders.add(folder);
  }

  public void addGroupSharedFolder(final String gridId, final String groupId, final String folderId) {
    final Dao<Group> groupDao = getDaoFactory().getDao(Group.class);
    final Group group = groupDao.load(groupId);
    if(group.getSharedFolders() == null) {
      group.setSharedFolders(Sets.<VirtualFolder>newHashSet());
    }
    addSharedFolder(folderId, group.getSharedFolders());
    groupDao.saveObject(group);
  }

  public void removeSharedFolder(final String cagridId, final String rootSharedFolderId, final boolean removeOwnedObjects) {
    final User user = getUserDao().loadUser(cagridId);
    final VirtualFolder folder = loadSharedFolder(rootSharedFolderId);
    if(!isRoot(folder)) {
      throw new RuntimeException("Attempting to remove non root shared folder from user");
    }

    if(removeOwnedObjects) {
      TreeUtils.stackRecursion(new VirtualEntry(null, folder), VIRTUAL_ENTRIES, new Closure<VirtualEntry>() {
        public void apply(final VirtualEntry entry) {
          final String objectId = entry.object.getId();
          final String ownerId = getTropixObjectDao().getOwnerId(objectId);
          if(!(entry.object instanceof VirtualFolder)) {
            if(cagridId.equals(ownerId)) {
              removeFromSharedFolder(entry.parent.getId(), objectId);
            }
          }
        }
      });
    }

    hideSharedFolder(user, folder);
  }

  public void hideGroupSharedFolder(final String cagridId, final String groupId, final String rootSharedFolderId) {
    final Dao<Group> groupDao = getDaoFactory().getDao(Group.class);
    final Group group = groupDao.load(groupId);
    final VirtualFolder folder = loadSharedFolder(rootSharedFolderId);
    group.getSharedFolders().remove(folder);
    groupDao.saveObject(group);
  }

  public void hideSharedFolder(final String cagridId, final String rootSharedFolderId) {
    final User user = getUserDao().loadUser(cagridId);
    final VirtualFolder folder = loadSharedFolder(rootSharedFolderId);
    hideSharedFolder(user, folder);
  }

  private VirtualFolder loadSharedFolder(final String rootSharedFolderId) {
    final VirtualFolder folder = getTropixObjectDao().loadTropixObject(rootSharedFolderId, VirtualFolder.class);
    return folder;
  }

  private void hideSharedFolder(final User user, final VirtualFolder folder) {
    user.getSharedFolders().remove(folder);
    getUserDao().saveOrUpdateUser(user);
  }

  private static String getRole(final PermissionType permissionType) {
    String roleName;
    if(permissionType.equals(PermissionType.Read)) {
      roleName = "read";
    } else if(permissionType.equals(PermissionType.Write)) {
      roleName = "write";
    } else {
      roleName = "owner";
    }
    return roleName;
  }

  class AddPermissionForUser implements Closure<TropixObject> {
    private String userId;
    private PermissionType permissionType;

    AddPermissionForUser(final String userId, final PermissionType permissionType) {
      this.userId = userId;
      this.permissionType = permissionType;
    }

    public void apply(final TropixObject object) {
      final DirectPermission role = getTropixObjectDao().getUserDirectRole(userId, object.getId());
      if(role == null) {
        getTropixObjectDao().addRole(object.getId(), getRole(permissionType), getUserDao().loadUser(userId));
      } else {
        if(role.getRole().equals("read") && !permissionType.equals(PermissionType.Read)) {
          role.setRole(getRole(permissionType));
          final Dao<DirectPermission> roleDao = getDaoFactory().getDao(DirectPermission.class);
          roleDao.saveObject(role);
        }
      }
    }
  }

  class CopyOwnerPermission implements Closure<TropixObject> {
    private final String folderId;

    public CopyOwnerPermission(final String folderId) {
      this.folderId = folderId;
    }

    public void apply(TropixObject input) {
      copyParentPermissions(folderId, input);
    }

  }

  class DropOwnerPermission implements Closure<TropixObject> {
    private final String userId;

    public DropOwnerPermission(final String userId) {
      super();
      this.userId = userId;
    }

    public void apply(final TropixObject object) {
      final DirectPermission role = getTropixObjectDao().getUserDirectRole(userId, object.getId());
      if(role.getRole().equals("owner")) {
        getRoleDao().delete(role.getId());
      }
    }

  }

  private Dao<DirectPermission> getRoleDao() {
    final Dao<DirectPermission> roleDao = getDaoFactory().getDao(DirectPermission.class);
    return roleDao;
  }

  class RemovePermissionForUser implements Closure<TropixObject> {
    private String userId;
    private PermissionType permissionType;

    RemovePermissionForUser(final String userId, final PermissionType permissionType) {
      this.userId = userId;
      this.permissionType = permissionType;
    }

    public void apply(final TropixObject object) {
      final DirectPermission role = getTropixObjectDao().getUserDirectRole(userId, object.getId());
      if(role == null) {
        return;
      } else if(permissionType.equals(PermissionType.Read)) {
        getRoleDao().delete(role.getId());
      } else {
        if(!role.getRole().equals("read")) {
          role.setRole("read");
          final Dao<DirectPermission> roleDao = getDaoFactory().getDao(DirectPermission.class);
          roleDao.saveObject(role);
        }
      }
    }
  }

  class AddPermissionForGroup implements Closure<TropixObject> {
    private String groupId;
    private PermissionType permissionType;

    AddPermissionForGroup(final String groupId, final PermissionType permissionType) {
      this.groupId = groupId;
      this.permissionType = permissionType;
    }

    public void apply(final TropixObject object) {
      final DirectPermission role = getTropixObjectDao().getGroupDirectRole(groupId, object.getId());
      final Dao<Group> groupDao = getDaoFactory().getDao(Group.class);
      if(role == null) {
        getTropixObjectDao().addGroupRole(object.getId(), getRole(permissionType), groupDao.load(groupId));
      } else {
        if(role.getRole().equals("read") && !permissionType.equals(PermissionType.Read)) {
          role.setRole(getRole(permissionType));
          final Dao<DirectPermission> roleDao = getDaoFactory().getDao(DirectPermission.class);
          roleDao.saveObject(role);
        }
      }
    }
  }

  class RemovePermissionForGroup implements Closure<TropixObject> {
    private String groupId;
    private PermissionType permissionType;

    RemovePermissionForGroup(final String groupId, final PermissionType permissionType) {
      this.groupId = groupId;
      this.permissionType = permissionType;
    }

    public void apply(final TropixObject object) {
      final DirectPermission role = getTropixObjectDao().getGroupDirectRole(groupId, object.getId());
      if(role == null) {
        return;
      } else if(permissionType.equals(PermissionType.Read)) {
        final Dao<DirectPermission> roleDao = getDaoFactory().getDao(DirectPermission.class);
        roleDao.delete(role.getId());
      } else {
        if(!role.getRole().equals("read")) {
          role.setRole("read");
          final Dao<DirectPermission> roleDao = getDaoFactory().getDao(DirectPermission.class);
          roleDao.saveObject(role);
        }
      }
    }
  }

  class CopyVirtualPermissions implements Closure<TropixObject> {
    private String sourceId;

    CopyVirtualPermissions(final String sourceId) {
      this.sourceId = sourceId;
    }

    public void apply(final TropixObject object) {
      getTropixObjectDao().copyVirtualPermissions(sourceId, object.getId());
    }
  }

  class DropVirtualPermissions implements Closure<TropixObject> {
    private String sourceId;

    DropVirtualPermissions(final String sourceId) {
      this.sourceId = sourceId;
    }

    public void apply(final TropixObject object) {
      getTropixObjectDao().dropVirtualPermission(sourceId, object.getId());
    }
  }

  public void addPermissionForUser(final String ownerId, final String objectId, final String userId, final PermissionType permissionType) {
    TreeUtils.applyPermissionChange(getTropixObjectDao().loadTropixObject(objectId), new AddPermissionForUser(userId, permissionType));
  }

  public void removePermissionForUser(final String ownerId, final String objectId, final String userId, final PermissionType permissionType) {
    TreeUtils.applyPermissionChange(getTropixObjectDao().loadTropixObject(objectId), new RemovePermissionForUser(userId, permissionType));
  }

  public void addPermissionForGroup(final String userId, final String objectId, final String groupId, final PermissionType permissionType) {
    TreeUtils.applyPermissionChange(getTropixObjectDao().loadTropixObject(objectId), new AddPermissionForGroup(groupId, permissionType));
  }

  public void removePermissionForGroup(final String userId, final String objectId, final String groupId, final PermissionType permissionType) {
    TreeUtils.applyPermissionChange(getTropixObjectDao().loadTropixObject(objectId), new RemovePermissionForGroup(groupId, permissionType));
  }

  public void addVirtualPermissionForUser(final String ownerId, final String objectId, final String userId, final PermissionType type) {
    getTropixObjectDao().addVirtualPermissionUser(objectId, getRole(type), userId);
    if(type.equals(PermissionType.Write)) {
      getTropixObjectDao().addVirtualPermissionUser(objectId, "read", userId);
    }
  }

  public void addVirtualPermissionForGroup(final String userId, final String objectId, final String groupId, final PermissionType type) {
    getTropixObjectDao().addVirtualPermissionGroup(objectId, getRole(type), groupId);
    if(type.equals(PermissionType.Write)) {
      getTropixObjectDao().addVirtualPermissionGroup(objectId, "read", groupId);
    }
  }

  public void removeVirtualPermissionForUser(final String ownerId, final String objectId, final String userId, final PermissionType type) {
    getTropixObjectDao().removeVirtualPermissionUser(objectId, getRole(type), userId);
    if(type.equals(PermissionType.Read)) {
      getTropixObjectDao().removeVirtualPermissionUser(objectId, "write", userId);
    }
    recalculateVirtualPermissions(loadSharedFolder(objectId));
  }

  public void removeVirtualPermissionForGroup(final String userId, final String objectId, final String groupId, final PermissionType type) {
    getTropixObjectDao().removeVirtualPermissionGroup(objectId, getRole(type), groupId);
    if(type.equals(PermissionType.Read)) {
      getTropixObjectDao().removeVirtualPermissionGroup(objectId, "write", groupId);
    }
    recalculateVirtualPermissions(getTropixObjectDao().loadTropixObject(objectId, VirtualFolder.class));
  }

  private static class VirtualEntry {
    private VirtualFolder parent;
    private TropixObject object;

    VirtualEntry(final VirtualFolder parent, final TropixObject object) {
      this.parent = parent;
      this.object = object;
    }
  }

  private void recalculateVirtualPermissions(final VirtualFolder root) {
    // If a user can no long read a folder, make sure it removed from their favorites list
    final Collection<User> virtualFolderUsers = getUserDao().getUsersWithVirtualFolder(root.getId());
    for(final User user : virtualFolderUsers) {
      if(!getSecurityProvider().canRead(root.getId(), user.getCagridId())) {
        hideSharedFolder(user, root);
      }
    }

    // If a user can no longer write to a folder, make sure all of their stuff is removed
    final HashMap<String, Boolean> canEditMap = new HashMap<String, Boolean>();
    TreeUtils.stackRecursion(new VirtualEntry(null, root), VIRTUAL_ENTRIES, new Closure<VirtualEntry>() {
      public void apply(final VirtualEntry entry) {
        if(!(entry.object instanceof VirtualFolder)) {
          final String userId = getTropixObjectDao().getOwnerId(entry.object.getId());
          boolean canEdit;
          if(canEditMap.containsKey(userId)) {
            canEdit = canEditMap.get(userId);
          } else {
            canEdit = getSecurityProvider().canModify(root.getId(), userId);
            canEditMap.put(userId, canEdit);
          }
          if(!canEdit) {
            final VirtualFolder parent = entry.parent;
            removeFromSharedFolder(parent.getId(), entry.object.getId());
          }
        }
      }
    });
  }

  public void removeFromSharedFolder(final String gridId, final String virtualFolderId, final String objectId) {
    removeFromSharedFolder(virtualFolderId, objectId);
  }

  public void addToSharedFolder(final String gridId, final String inputObjectId, final String inputFolderId, final boolean recursive) {
    final Stack<String> objectIds = new Stack<String>();
    final Stack<String> virtualFolderIds = new Stack<String>();

    objectIds.add(inputObjectId);
    virtualFolderIds.add(inputFolderId);

    while(!objectIds.isEmpty()) {
      final String objectId = objectIds.pop();
      final String folderId = virtualFolderIds.pop();
      final TropixObject object = getTropixObjectDao().loadTropixObject(objectId);
      if(!(object instanceof Folder)) {
        getTropixObjectDao().addToVirtualFolder(folderId, objectId);
        TreeUtils.applyPermissionChange(getTropixObjectDao().loadTropixObject(objectId), new CopyVirtualPermissions(folderId));
      } else {
        final Folder sourceFolder = (Folder) object;
        final VirtualFolder destinationFolder = new VirtualFolder();
        destinationFolder.setName(sourceFolder.getName());
        destinationFolder.setDescription(sourceFolder.getDescription());
        // System.out.println(String.format("Destination is %s", folderId));
        final String destinationId = createNewChildVirtualFolder(folderId, destinationFolder).getId();
        for(final TropixObject child : sourceFolder.getContents()) {
          objectIds.add(child.getId());
          virtualFolderIds.add(destinationId);
        }
      }
    }
    /*
     * final TropixObject object = getTropixObjectDao().loadTropixObject(objectId);
     * if(!(object instanceof Folder)) {
     * getTropixObjectDao().addToVirtualFolder(folderId, objectId);
     * TreeUtils.applyPermissionChange(getTropixObjectDao().loadTropixObject(objectId), new CopyVirtualPermissions(folderId));
     * }
     */
  }

  private static final Function<VirtualEntry, Iterable<VirtualEntry>> VIRTUAL_ENTRIES = new Function<VirtualEntry, Iterable<VirtualEntry>>() {
    public Iterable<VirtualEntry> apply(final VirtualEntry entry) {
      final LinkedList<VirtualEntry> entries = new LinkedList<VirtualEntry>();
      if(entry.object instanceof VirtualFolder) {
        for(final TropixObject object : ((VirtualFolder) entry.object).getContents()) {
          entries.add(new VirtualEntry((VirtualFolder) entry.object, object));
        }
      }
      return entries;
    }
  };

  private static final Function<TropixObject, Iterable<TropixObject>> VIRTUAL_FOLDER_CONTENTS = new Function<TropixObject, Iterable<TropixObject>>() {
    @edu.umd.cs.findbugs.annotations.SuppressWarnings("BC")
    public Iterable<TropixObject> apply(final TropixObject object) {
      final LinkedList<TropixObject> contents = new LinkedList<TropixObject>();
      if(object instanceof VirtualFolder) {
        contents.addAll(((VirtualFolder) object).getContents());
      }
      return contents;
    }
  };

  public void delete(final String cagridId, final String id) {
    final TropixObject object = getTropixObjectDao().loadTropixObject(id);
    final Closure<TropixObject> deleter = new Closure<TropixObject>() {
      public void apply(final TropixObject object) {
        object.setDeletedTime("" + System.currentTimeMillis());
        getTropixObjectDao().saveOrUpdateTropixObject(object);
      }
    };
    if(object instanceof VirtualFolder) {
      throw new RuntimeException("Cannot delete a virtual folder with this method");
    }
    if(getTropixObjectDao().isAHomeFolder(id)) {
      throw new RuntimeException("Cannot delete a home folder.");
    }
    TreeUtils.applyPermissionChange(object, deleter, true);
  }

  private static boolean isRoot(final VirtualFolder virtualFolder) {
    final Boolean isRoot = virtualFolder.getRoot();
    return isRoot != null && isRoot;
  }

  public void deleteVirtualFolder(final String cagridId, final String id) {
    final VirtualFolder object = loadSharedFolder(id);
    if(isRoot(object)) {
      throw new RuntimeException("Cannot delete a root virtual folder");
    }
    final Closure<TropixObject> deleter = new Closure<TropixObject>() {
      public void apply(final TropixObject object) {
        object.setDeletedTime("" + System.currentTimeMillis());
        getTropixObjectDao().saveOrUpdateTropixObject(object);
      }
    };
    final String rootId = getTropixObjectDao().getRootVirtualFolderId(id);
    TreeUtils.stackRecursion(object, VIRTUAL_FOLDER_CONTENTS, new Closure<TropixObject>() {
      public void apply(final TropixObject object) {
        if(object instanceof VirtualFolder) {
          deleter.apply(object);
        } else {
          removeVirtualPermissionIfNessecary(rootId, object.getId());
        }
      }
    });
  }

  private void removeFromSharedFolder(final String virtualFolderId, final String objectId) {
    getTropixObjectDao().removeFromVirtualFolder(virtualFolderId, objectId);
    removeVirtualPermissionIfNessecary(virtualFolderId, objectId);
  }

  private void removeVirtualPermissionIfNessecary(final String virtualFolderId, final String objectId) {
    final long count = getTropixObjectDao().virtualHierarchyCount(objectId, getTropixObjectDao().getRootVirtualFolderId(virtualFolderId));
    // Only drop the inherited virtual permissions if this is the last occurrence of objectId
    // in this virtual hierarchy.
    if(count == 0) {
      TreeUtils.applyPermissionChange(getTropixObjectDao().loadTropixObject(objectId), new DropVirtualPermissions(virtualFolderId));
    }
  }

  public VirtualFolder getRoot(final String gridId, final String virtualFolderId) {
    return getTropixObjectDao().loadTropixObject(getTropixObjectDao().getRootVirtualFolderId(virtualFolderId), VirtualFolder.class);
  }

  public boolean canModifySharing(final String gridId, final String objectId) {
    final TropixObject object = getTropixObjectDao().loadTropixObject(objectId);
    boolean isRootSharedFolder;
    if(!(object instanceof VirtualFolder)) {
      isRootSharedFolder = false;
    } else {
      final VirtualFolder virtualFolder = (VirtualFolder) object;
      Boolean root = virtualFolder.getRoot();
      if(root != null && root.booleanValue()) {
        isRootSharedFolder = true;
      } else {
        isRootSharedFolder = false;
      }
    }
    boolean canModifySharing;
    if(isRootSharedFolder) {
      canModifySharing = getSecurityProvider().canModify(objectId, gridId);
      LOG.debug("Checking if canModifySharing for root virtual folder with objectId " + objectId + " " + canModifySharing);
    } else {
      canModifySharing = getTropixObjectDao().isAnOwner(gridId, objectId);
    }
    return canModifySharing;
  }

  public TropixFile loadFileWithFileId(final String gridId, final String fileId) {
    final TropixFile loadedFile = getTropixObjectDao().loadTropixFileWithFileId(fileId);
    if(!getSecurityProvider().canRead(loadedFile.getId(), gridId)) {
      throw new RuntimeException("Illegal Access");
    }
    return loadedFile;
  }

  public TropixFile createFile(final String userGridId, final String folderId, final TropixFile file, @Nullable final String fileTypeId) {
    // If storage service already saved a file size, grab it and set it on the new file, along with
    // previously set id.
    final TropixFile loadedFile = getTropixObjectDao().loadTropixFileWithFileId(file.getFileId());
    if(loadedFile != null) {
      file.setId(loadedFile.getId());
      super.getDaoFactory().getDao(TropixFile.class).evictEntity(loadedFile);
    }
    file.setDeletedTime(null);
    if(fileTypeId == null || fileTypeId.equals("")) {
      file.setFileType(getFileType(StockFileExtensionEnum.UNKNOWN));
    } else {
      file.setFileType(fileTypeDao.load(fileTypeId));
    }
    saveNewObjectToDestination(file, userGridId, folderId);
    return file;
  }

  public TropixObject[] loadRecent(final String userId, final int maxNum, final boolean includeFolders, final TropixObjectType[] types,
      final boolean requireParent) {
    Iterable<TropixObject> objects = getTropixObjectDao().loadRecent(userId, maxNum, includeFolders, requireParent);
    if(types != null) {
      // Filter by type
      objects = PersistenceModelUtils.typeFilter(objects, Arrays.asList(types));
    }
    return Iterables.toArray(objects, TropixObject.class);
  }

  public long ownedObjectsVirtualHierarchyCount(final String cagridId, final String rootSharedFolderId) {
    return getTropixObjectDao().ownedObjectsVirtualHierarchyCount(cagridId, rootSharedFolderId);
  }

  public boolean canModify(final String userId, final String objectId) {
    return getSecurityProvider().canModify(objectId, userId);
  }

  public void commit(final String userId, final String objectId) {
    TreeUtils.applyPermissionChange(getTropixObjectDao().loadTropixObject(objectId), new Closure<TropixObject>() {
      public void apply(final TropixObject input) {
        input.setCommitted(true);
        getTropixObjectDao().saveOrUpdateTropixObject(input);
      }
    }, true);
  }

  public TropixObject getPath(final String userId, final String[] inputNames) {
    Preconditions.checkArgument(inputNames != null && inputNames.length > 0);
    final String rootLocationName = inputNames[0];
    final List<String> restOfNames = Arrays.asList(inputNames).subList(1, inputNames.length);
    TropixObject object = null;
    if(Locations.MY_HOME.equals(rootLocationName)) {
      object = getTropixObjectDao().getHomeDirectoryPath(userId, restOfNames);
    } else if(Locations.MY_GROUP_FOLDERS.equals(rootLocationName)) {
      object = getTropixObjectDao().getGroupDirectoryPath(userId, restOfNames);
    } else if(Locations.MY_SHARED_FOLDERS.equals(rootLocationName)) {
      object = getTropixObjectDao().getSharedDirectoryPath(userId, restOfNames);
    } else {
      throw new IllegalArgumentException(String.format("Unknown root location name %s", rootLocationName));
    }
    return object;
  }

}
