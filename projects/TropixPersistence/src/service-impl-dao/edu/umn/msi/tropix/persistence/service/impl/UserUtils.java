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

import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.persistence.dao.TropixObjectDao;
import edu.umn.msi.tropix.persistence.dao.UserDao;
import edu.umn.msi.tropix.persistence.service.PersistenceConstants;

public class UserUtils {

  /**
   * This class and this method is a hack to share this piece of code between UserServiceImpl and AutoUserAspect.
   * 
   * @param gridIdentity
   * @param userDao
   * @param objectDao
   */
  public static void ensureUserExists(final String gridIdentity, final UserDao userDao, final TropixObjectDao objectDao) {
    if(!userDao.userExists(gridIdentity)) {
      final User user = new User();
      user.setGroups(new HashSet<Group>());
      user.setSharedFolders(new HashSet<VirtualFolder>());
      user.setCagridId(gridIdentity);
      userDao.saveOrUpdateUser(user);
      final Folder homeFolder = new Folder();
      homeFolder.setName("My Home");
      homeFolder.setCommitted(true);
      homeFolder.setContents(new HashSet<TropixObject>());
      Utils.initObject(homeFolder);
      objectDao.saveOrUpdateTropixObject(homeFolder);
      objectDao.setOwner(homeFolder.getId(), user);
      user.setHomeFolder(homeFolder);
      userDao.saveOrUpdateUser(user);
      userDao.addToGroupWithName(gridIdentity, PersistenceConstants.ALL_USERS_GROUP);
      if(gridIdentity.equals(PersistenceConstants.DEFAULT_ADMIN_USER)) {
        userDao.addToGroupWithName(gridIdentity, PersistenceConstants.ADMIN_GROUP);
      }
    }
  }
}
