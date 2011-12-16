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

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.dao.TropixObjectDao;
import edu.umn.msi.tropix.persistence.dao.UserDao;
import edu.umn.msi.tropix.persistence.service.PersistenceConstants;
import edu.umn.msi.tropix.persistence.service.UserService;

@ManagedBean
@Named("userService")
class UserServiceImpl implements UserService {
  private final UserDao userDao;
  private final TropixObjectDao objectDao;

  @Inject
  UserServiceImpl(final UserDao userDao, final TropixObjectDao objectDao) {
    this.userDao = userDao;
    this.objectDao = objectDao;
  }

  public void ensureUserExists(final String gridIdentity) {
    UserUtils.ensureUserExists(gridIdentity, userDao, objectDao);
  }

  public User createOrGetUser(final String gridIdentity) {
    ensureUserExists(gridIdentity);
    final User user = userDao.loadUser(gridIdentity);
    // Kill laziness of homeFolder
    return user;
  }

  public Folder getHomeFolder(final String userId) {
    final User user = userDao.loadUser(userId);
    return objectDao.loadTropixObject(user.getHomeFolder().getId(), Folder.class);
  }

  public boolean isAdmin(final String userId) {
    return userDao.belongsToGroupWithName(userId, PersistenceConstants.ADMIN_GROUP);
  }

  public User[] getUsers() {
    return Iterables.toArray(userDao.getUsers(), User.class);
  }

  public void createUser(final User user) {
    ensureUserExists(user.getCagridId());
    final User dbUser = userDao.loadUser(user.getCagridId());
    dbUser.setEmail(user.getEmail());
    dbUser.setPhone(user.getPhone());
    dbUser.setFirstName(user.getFirstName());
    dbUser.setLastName(user.getLastName());
  }

  public Group getPrimaryGroup(final String gridIdentity) {
    final User user = userDao.loadUser(gridIdentity);
    return user.getPrimaryGroup();
  }

  public Group[] getGroups(final String gridIdentity) {
    final User user = userDao.loadUser(gridIdentity);
    return Iterables.toArray(user.getGroups(), Group.class);
  }

}
