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

package edu.umn.msi.tropix.webgui.services.object;

import java.util.Collection;
import java.util.List;

import org.gwtwidgets.server.spring.GWTRequestMapping;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.utils.SharedFolderContext;
import edu.umn.msi.tropix.models.utils.TropixObjectContext;
import edu.umn.msi.tropix.models.utils.TropixObjectType;
import edu.umn.msi.tropix.models.utils.TropixObjectWithContext;

@RemoteServiceRelativePath("ObjectService.rpc")
@GWTRequestMapping("/webgui/ObjectService.rpc")
public interface ObjectService extends RemoteService {

  public static class Util {
    public static ObjectServiceAsync getInstance() {
      return GWT.create(ObjectService.class);
    }
  }

  List<TropixObject> getChildren(final String objectId, TropixObjectType[] filterTypes);

  TropixObjectContext getObjectContext(final String objectId);

  List<TropixObject> loadRecent(int num, boolean includeFolders, TropixObjectType[] filterTypes);

  TropixObject load(String objectId);

  void move(String objectId, String folderId);

  void rename(String objectId, String newName);

  void changeDescription(String objectId, String newDescription);

  boolean canModifySharing(String objectId);

  SharedFolderContext getRoot(String virtualFolderId);

  void delete(String objectId, String rootId);

  TropixObjectWithContext getAssociation(String objectId, String assoicationName);

  List<TropixObjectWithContext> getAssociations(String objectId, String assoicationName);

  void addSharedFolder(String folderId);

  List<Permission> getPermissions(String objectId);

  void addPermissionForUser(String objectId, String userId, String roleName);

  void removePermissionForUser(String objectId, String userId, String roleName);

  void addPermissionForGroup(String objectId, String groupId, String roleName);

  void removePermissionForGroup(String objectId, String groupId, String roleName);

  void addVirtualPermissionForUser(String objectId, String userId, String roleName);

  void removeVirtualPermissionForUser(String objectId, String userId, String roleName);

  void addVirtualPermissionForGroup(String objectId, String groupId, String roleName);

  void removeVirtualPermissionForGroup(String objectId, String groupId, String roleName);

  void addToSharedFolder(Collection<String> objectId, String folderId, boolean recursive);

  void removeFromSharedFolder(String objectId, String folderId);

  void moveVirtually(String objectId, String parentId, String newParentId);

  long ownedItemsInVirtualHierarchiesCount(final Iterable<String> rootSharedFolderIds);

  void removeSharedFolder(final String rootSharedFolderId, final boolean removeOwnedItems);

  void hideSharedFolder(String id);

  void hideGroupSharedFolder(String groupId, String id);

  void addGroupSharedFolder(String groupId, String folderId);
}
