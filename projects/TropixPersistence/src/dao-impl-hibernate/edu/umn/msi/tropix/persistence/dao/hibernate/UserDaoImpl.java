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

package edu.umn.msi.tropix.persistence.dao.hibernate;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.Permission;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.persistence.dao.UserDao;

@ManagedBean @Named("userDao")
class UserDaoImpl extends TropixPersistenceTemplate implements UserDao {

  /**
   * Override setSessionFactory so the @Inject annotation can be added to it.
   */
  @Inject
  public void setSessionFactory(@Named("sessionFactory") final SessionFactory sessionFactory) {
    super.setSessionFactory(sessionFactory);
  }
  
  public Folder getHomeFolder(final String gridIdentity) {
    final User user = loadUser(gridIdentity);
    return user.getHomeFolder();
  }

  public User loadUser(final String gridIdentity) {
    return (User) find("from User as user where user.cagridId=?", gridIdentity).get(0);
  }

  public void saveOrUpdateUser(final User user) {
    saveOrUpdate(user);
  }

  public boolean userExists(final String gridIdentity) {
    return find("from User as user where user.cagridId=?", gridIdentity).size() > 0;
  }

  public void deleteUser(final User user) {
    for(Group group : user.getGroups()) {
      group.getUsers().remove(user);
      saveOrUpdate(group);
    }
    @SuppressWarnings("unchecked")
    final Collection<Permission> permissions = find("select distinct p from Permission p left join p.users u where u.id = ?", user.getId());
    for(final Permission permission : permissions) {
      permission.getUsers().remove(user);
      saveOrUpdate(permission);
    }
    super.delete(load(User.class, user.getId()));
  }

  public void addVirtualFolder(final String gridIdentity, final String virtualFolderId) {
    final User user = loadUser(gridIdentity);
    final VirtualFolder virtualFolder = (VirtualFolder) load(VirtualFolder.class, virtualFolderId);
    user.getSharedFolders().add(virtualFolder);
    saveOrUpdate(user);
  }

  public void addToGroup(final String gridIdentity, final String groupId) {
    final Group group = (Group) load(Group.class, groupId);
    final User user = loadUser(gridIdentity);
    group.getUsers().add(user);
    user.getGroups().add(group);
    saveOrUpdate(user);
    saveOrUpdate(group);
  }

  public void addToGroupWithName(final String gridIdentity, final String groupName) {
    @SuppressWarnings("unchecked")
    List<Group> groupList = find("from Group as group where group.name=?", groupName);
    if(groupList.size() > 1) {
      throw new IllegalArgumentException("More than one group with specified name exists");
    } else if(groupList.isEmpty()) {
      final Group group = new Group();
      group.setUsers(new HashSet<User>());
      group.setName(groupName);
      saveOrUpdate(group);
      groupList = Arrays.asList(group);
    }
    final Group group = groupList.get(0);
    addToGroup(gridIdentity, group.getId());
  }

  public Collection<User> getUsersWithVirtualFolder(final String virtualFolderId) {
    @SuppressWarnings("unchecked")
    final List<User> userList = find("select u from User u, VirtualFolder f where f.id= ? and f member of u.sharedFolders", virtualFolderId);
    return userList;
  }

  public boolean belongsToGroupWithName(final String gridIdentity, final String groupName) {
    final Query query = getSession().createQuery("select count(*) from Group g, User u where g.name = :name and u.cagridId = :cagridId and u member of g.users");
    query.setParameter("name", groupName);
    query.setParameter("cagridId", gridIdentity);
    final Long count = (Long) query.uniqueResult();
    return count != 0;
  }

  public Collection<User> getUsers() {
    @SuppressWarnings("unchecked")
    final List<User> userList = find("from User");
    return userList;
  }

}
