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

import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.models.utils.TropixObjectType;
import edu.umn.msi.tropix.persistence.service.FolderService;
import edu.umn.msi.tropix.persistence.service.impl.utils.PersistenceModelUtils;

@ManagedBean @Named("folderService")
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

  public VirtualFolder[] getSavedVirtualFolders(final String gridId) {
    final User user = getUserDao().loadUser(gridId);
    return filter(user.getSharedFolders(), VirtualFolder.class, gridId);
  }

  public VirtualFolder createVirtualFolder(final String userGridId, final String parentFolderId, final VirtualFolder inputFolder) {
    if(parentFolderId != null) {
      return createNewChildVirtualFolder(parentFolderId, inputFolder);
    } else {
      if(getTropixObjectDao().ownsSharedFolderWithName(userGridId, inputFolder.getName())) {
        throw new IllegalArgumentException("User alread owns a shared folder with that name.");
      }
      final VirtualFolder folder = newVirtualFolder(inputFolder);      
      folder.setRoot(true);
      saveNewObject(folder, userGridId);
      getTropixObjectDao().createVirtualPermission(folder.getId(), "read");
      getTropixObjectDao().createVirtualPermission(folder.getId(), "write");
      getTropixObjectDao().addVirtualPermissionUser(folder.getId(), "write", userGridId);
      getUserDao().addVirtualFolder(userGridId, folder.getId());
      return folder;
    }
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
}
