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

import java.util.HashSet;

import javax.annotation.ManagedBean;
import javax.inject.Named;

import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.dao.Dao;
import edu.umn.msi.tropix.persistence.service.GroupService;

@ManagedBean
@Named("groupService")
class GroupServiceImpl extends ServiceBase implements GroupService {

  public Group createGroup(final String adminId, final String groupName) {
    final Dao<Group> groupDao = getGroupDao();
    final Group group = new Group();
    group.setName(groupName);
    group.setUsers(new HashSet<User>());
    groupDao.saveObject(group);
    return group;
  }

  public Group[] getAllGroups(final String adminId) {
    final Dao<Group> groupDao = getGroupDao();
    return groupDao.findAll().toArray(new Group[0]);
  }

  public Group[] getGroups(final String userId) {
    final User user = getUserDao().loadUser(userId);
    return user.getGroups().toArray(new Group[0]);
  }

  public User[] getUsers(final String adminId, final String groupId) {
    final Dao<Group> groupDao = getGroupDao();
    final Group group = groupDao.load(groupId);
    return group.getUsers().toArray(new User[0]);
  }

  public void removeUserFromGroup(final String adminId, final String userId, final String groupId) {
    final Dao<Group> groupDao = getGroupDao();
    final Group group = groupDao.load(groupId);
    final User user = getUserDao().loadUser(userId);
    group.getUsers().remove(user);
    user.getGroups().remove(group);
    getUserDao().saveOrUpdateUser(user);
    groupDao.saveObject(group);
  }

  public void addUserToGroup(final String adminId, final String userId, final String groupId) {
    final User user = getUserDao().loadUser(userId);
    final Dao<Group> groupDao = getGroupDao();
    final Group group = groupDao.load(groupId);
    group.getUsers().add(getUserDao().loadUser(userId));
    user.getGroups().add(group);
    groupDao.saveObject(group);
    getUserDao().saveOrUpdateUser(user);
  }

  private Dao<Group> getGroupDao() {
    final Dao<Group> groupDao = getDaoFactory().getDao(Group.class);
    return groupDao;
  }

  public void setPrimaryGroup(final String gridId, final String userId, final String groupId) {
    final Dao<Group> groupDao = getGroupDao();
    final User user = getUserDao().loadUser(userId);
    final Group group = groupDao.load(groupId);
    user.setPrimaryGroup(group);
    getUserDao().saveOrUpdateUser(user);
  }
}
