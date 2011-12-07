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

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.models.utils.TropixObjectType;

public interface ObjectServiceAsync {

  void loadRecent(int num, boolean includeFolders, TropixObjectType[] filterTypes, AsyncCallback<List<TropixObject>> callback);

  void load(String objectId, AsyncCallback<TropixObject> callback);

  void canModifySharing(String objectId, AsyncCallback<Boolean> callback);

  void getRoot(String virtualFolderId, AsyncCallback<VirtualFolder> callback);

  void delete(String objectId, String rootId, AsyncCallback<Void> callback);

  void getAssociation(String objectId, String assoicationName, AsyncCallback<TropixObject> callback);

  void getAssociations(String objectId, String assoicationName, AsyncCallback<List<TropixObject>> callback);

  void addSharedFolder(String folderId, AsyncCallback<Void> callback);

  void getPermissions(String objectId, AsyncCallback<List<Permission>> callback);

  void addPermissionForUser(String objectId, String userId, String roleName, AsyncCallback<Void> callback);

  void removePermissionForUser(String objectId, String userId, String roleName, AsyncCallback<Void> callback);

  void addPermissionForGroup(String objectId, String groupId, String roleName, AsyncCallback<Void> callback);

  void removePermissionForGroup(String objectId, String groupId, String roleName, AsyncCallback<Void> callback);

  void addVirtualPermissionForUser(String objectId, String userId, String roleName, AsyncCallback<Void> callback);

  void removeVirtualPermissionForUser(String objectId, String userId, String roleName, AsyncCallback<Void> callback);

  void addVirtualPermissionForGroup(String objectId, String groupId, String roleName, AsyncCallback<Void> callback);

  void removeVirtualPermissionForGroup(String objectId, String groupId, String roleName, AsyncCallback<Void> callback);

  void addToSharedFolder(Collection<String> objectId, String folderId, boolean recursive, AsyncCallback<Void> callback);

  void removeFromSharedFolder(String objectId, String folderId, AsyncCallback<Void> callback);

  void move(String objectId, String folderId, AsyncCallback<Void> callback);

  void rename(String objectId, String newName, AsyncCallback<Void> callback);

  void changeDescription(String objectId, String newDescription, AsyncCallback<Void> callback);

  void moveVirtually(String objectId, String parentId, String newParentId, AsyncCallback<Void> callback);

  void ownedItemsInVirtualHierarchiesCount(Iterable<String> rootSharedFolderIds, AsyncCallback<Long> callback);

  void removeSharedFolder(String rootSharedFolderId, boolean removeOwnedItems, AsyncCallback<Void> callback);

  void getObjectContext(String objectId, AsyncCallback<TropixObjectContext> callback);

  void getChildren(String objectId, TropixObjectType[] filterTypes, AsyncCallback<List<TropixObject>> callback);

  void hideSharedFolder(String id, AsyncCallback<Void> callback);

}
