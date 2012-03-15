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
import java.util.HashSet;

import javax.annotation.ManagedBean;
import javax.inject.Named;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.models.utils.ModelPredicates;
import edu.umn.msi.tropix.models.utils.ModelUtils;
import edu.umn.msi.tropix.models.utils.TropixObjectContext;
import edu.umn.msi.tropix.models.utils.TropixObjectType;
import edu.umn.msi.tropix.models.utils.TropixObjectUserAuthorities;
import edu.umn.msi.tropix.persistence.dao.Dao;
import edu.umn.msi.tropix.persistence.service.FolderService;
import edu.umn.msi.tropix.persistence.service.impl.utils.PersistenceModelUtils;

@ManagedBean
@Named("folderService")
class FolderServiceImpl extends ServiceBase implements FolderService {

  public TropixObject[] getFolderContents(final String userGridId, final String folderId, final TropixObjectType[] types) {
    return super.filter(PersistenceModelUtils.typeFilter(getTropixObjectDao().getFolderContents(folderId), Arrays.asList(types)), userGridId);
  }

  public TropixObject[] getFolderContents(final String userGridId, final String folderId) {
    return super.filter(getTropixObjectDao().getFolderContents(folderId), userGridId);
  }

  public Folder createFolder(final String userGridId, final String parentFolderId, final Folder folder) {
    folder.setContents(new HashSet<TropixObject>());
    folder.setCommitted(true);
    super.saveNewObjectToDestination(folder, userGridId, parentFolderId);
    return folder;
  }

  private Collection<TropixObjectContext<VirtualFolder>> buildSharedFolderContexts(final Collection<VirtualFolder> virtualFolders, final String gridId) {
    final Multimap<String, String> objectsRoles = getTropixObjectDao().getRoles(gridId, ModelUtils.getIds(virtualFolders));
    return Collections2.transform(Collections2.filter(virtualFolders, new Predicate<TropixObject>() {

      public boolean apply(final TropixObject input) {
        return objectsRoles.containsKey(input.getId()) && ModelPredicates.isValidObjectPredicate().apply(input);
      }

    }), new Function<VirtualFolder, TropixObjectContext<VirtualFolder>>() {

      public TropixObjectContext<VirtualFolder> apply(final VirtualFolder input) {
        final Collection<String> objectRoles = objectsRoles.get(input.getId());
        final TropixObjectUserAuthorities context = new TropixObjectUserAuthorities(objectRoles.contains("write"), objectRoles.contains("write"));
        return new TropixObjectContext<VirtualFolder>(context, input);
      }

    });

  }

  public Collection<TropixObjectContext<VirtualFolder>> getSavedVirtualFolders(final String gridId) {
    final User user = getUserDao().loadUser(gridId);
    return buildSharedFolderContexts(user.getSharedFolders(), gridId);
  }

  public Collection<TropixObjectContext<VirtualFolder>> getGroupSharedFolders(final String userId, final String groupId) {
    final Group group = getDaoFactory().getDao(Group.class).load(groupId);
    return buildSharedFolderContexts(group.getSharedFolders(), userId);
  }

  public VirtualFolder createGroupVirtualFolder(final String gridId, final String groupId, final VirtualFolder inputFolder) {
    if(getTropixObjectDao().ownsSharedFolderWithName(gridId, inputFolder.getName())) {
      throw new IllegalArgumentException("User alread owns a shared folder with that name.");
    }

    final VirtualFolder folder = saveRootVirtualFolder(gridId, inputFolder);
    getTropixObjectDao().addVirtualPermissionGroup(folder.getId(), "write", groupId);
    final Dao<Group> groupDao = getDaoFactory().getDao(Group.class);
    final Group group = groupDao.load(groupId);
    if(group.getSharedFolders() == null) {
      group.setSharedFolders(Sets.<VirtualFolder>newHashSet());
    }
    group.getSharedFolders().add(folder);
    groupDao.saveObject(group);
    return folder;
  }

  public VirtualFolder createVirtualFolder(final String userGridId, final String parentFolderId, final VirtualFolder inputFolder) {
    if(parentFolderId != null) {
      return createNewChildVirtualFolder(parentFolderId, inputFolder);
    } else {
      if(getTropixObjectDao().ownsSharedFolderWithName(userGridId, inputFolder.getName())) {
        throw new IllegalArgumentException("User alread owns a shared folder with that name.");
      }
      final VirtualFolder folder = saveRootVirtualFolder(userGridId, inputFolder);
      getTropixObjectDao().addVirtualPermissionUser(folder.getId(), "write", userGridId);
      getUserDao().addVirtualFolder(userGridId, folder.getId());
      return folder;
    }
  }

  private VirtualFolder saveRootVirtualFolder(final String userGridId, final VirtualFolder inputFolder) {
    final VirtualFolder folder = newVirtualFolder(inputFolder);
    folder.setRoot(true);
    saveNewObject(folder, userGridId);
    getTropixObjectDao().createVirtualPermission(folder.getId(), "read");
    getTropixObjectDao().createVirtualPermission(folder.getId(), "write");
    return folder;
  }

  public VirtualFolder getOrCreateVirtualPath(final String userGridId, final String rootVirtualFolderId, final String[] subfolderNames) {
    VirtualFolder folder = getTropixObjectDao().loadTropixObject(rootVirtualFolderId, VirtualFolder.class);
    for(final String subfolderName : subfolderNames) {
      VirtualFolder childFolder = null;
      for(final TropixObject child : folder.getContents()) {
        if(child instanceof VirtualFolder && child.getName().equals(subfolderName)) {
          childFolder = (VirtualFolder) child;
          break;
        }
      }
      // If folder didn't exist, create a new one...
      if(childFolder == null) {
        final VirtualFolder newChild = new VirtualFolder();
        newChild.setName(subfolderName);
        childFolder = createNewChildVirtualFolder(folder.getId(), newChild);
      }
      folder = childFolder;
    }
    return folder;
  }

  public VirtualFolder getOrCreateRootVirtualFolderWithName(final String userGridId, final String name) {
    final Collection<VirtualFolder> sharedFolders = super.getUserDao().loadUser(userGridId).getSharedFolders();
    VirtualFolder sharedFolderToReturn = null;
    for(final VirtualFolder sharedFolder : sharedFolders) {
      if(sharedFolder.getName().equals(name)) {
        sharedFolderToReturn = sharedFolder;
        break;
      }
    }
    if(sharedFolderToReturn == null) {
      final VirtualFolder sharedFolder = new VirtualFolder();
      sharedFolder.setName(name);
      sharedFolder.setCommitted(true);
      sharedFolderToReturn = this.createVirtualFolder(userGridId, null, sharedFolder);

    }
    return sharedFolderToReturn;
  }

  public Folder[] getGroupFolders(final String gridId) {
    return filter(getTropixObjectDao().getGroupFolders(gridId), Folder.class, gridId);
  }

  public Folder[] getAllGroupFolders(final String gridId) {
    return Iterables.toArray(getTropixObjectDao().getAllGroupFolders(), Folder.class);
  }

}
