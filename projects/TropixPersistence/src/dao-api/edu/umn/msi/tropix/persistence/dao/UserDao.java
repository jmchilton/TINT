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

import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.User;

public interface UserDao {
  User loadUser(String gridIdentity);

  void saveOrUpdateUser(User user);

  Folder getHomeFolder(String gridIdentity);

  boolean userExists(String gridIdentity);

  void addToGroupWithName(String gridIdentity, String groupName);

  boolean belongsToGroupWithName(String gridIdentity, String groupName);

  void addToGroup(String gridIdentity, String groupId);

  // For testing purposes only right now, an actual implementation
  // would require more thought
  void deleteUser(User user);

  void addVirtualFolder(String gridIdentity, String virtualFolderId);

  Collection<User> getUsersWithVirtualFolder(String virtualFolderId);

  Collection<User> getUsers();
  
}
