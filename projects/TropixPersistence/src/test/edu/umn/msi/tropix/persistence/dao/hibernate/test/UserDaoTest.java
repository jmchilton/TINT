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

package edu.umn.msi.tropix.persistence.dao.hibernate.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.dao.UserDao;

public class UserDaoTest extends DaoTest {
  @Autowired
  private UserDao userDao;

  @Test
  public void save() {
    final User user = new User();
    user.setFirstName("Homer");
    user.setCagridId("123-456");
    user.setLastName("Simpson");
    userDao.saveOrUpdateUser(user);
    final String id = user.getId();
    assert id != null : "User id wasn't set in save process.";
    flush();
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from USER WHERE ID=?", id) == 1;
    assert simpleJdbcTemplate.queryForObject("SELECT FIRST_NAME from USER WHERE ID=?", String.class, id).equals("Homer");
    assert simpleJdbcTemplate.queryForObject("SELECT LAST_NAME from USER WHERE ID=?", String.class, id).equals("Simpson");
    assert simpleJdbcTemplate.queryForObject("SELECT CAGRID_ID from USER WHERE ID=?", String.class, id).equals("123-456");
  }

  @Test
  public void belongs() {
    final String userId = newId(), groupId = newId();
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO USER(ID,CAGRID_ID,FIRST_NAME) VALUES ('" + userId + "', '" + userId + "','Homer')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_GROUP(ID,NAME) VALUES ('" + groupId + "','MooGroup')");

    flush();
    assert !userDao.belongsToGroupWithName(userId, "MooGroup");
    assert !userDao.belongsToGroupWithName(userId, "MooGroup2");

    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO JOIN_USER_GROUP(USER_ID,GROUP_ID) VALUES ('" + userId + "','" + groupId + "')");
    flush();
    assert userDao.belongsToGroupWithName(userId, "MooGroup");
    assert !userDao.belongsToGroupWithName(userId, "MooGroup2");

  }

  @Test
  public void addToEmptyGroup() {
    final String userId = newId();
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO USER(ID,CAGRID_ID,FIRST_NAME) VALUES ('" + userId + "', '" + userId + "','Homer')");
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from TROPIX_GROUP WHERE NAME=?", "moocow") == 0;
    userDao.addToGroupWithName(userId, "moocow");
    flush();
    assert simpleJdbcTemplate.queryForInt("SELECT count(*) from TROPIX_GROUP WHERE NAME=?", "moocow") == 1;

  }

  @Test
  public void load() {
    final String userId = newId();
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO USER(ID,CAGRID_ID,FIRST_NAME) VALUES ('" + userId + "', '" + userId + "','Homer')");

    final User loadedUser = userDao.loadUser(userId);
    assert loadedUser != null;
    assert loadedUser.getId().equals(userId);
    assert loadedUser.getCagridId().equals(userId);
    assert loadedUser.getFirstName().equals("Homer");
  }

  @Test
  public void homeFolder() {
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID,DESCRIPTION) VALUES (3,'Folder 3')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO FOLDER(OBJECT_ID) VALUES (3)");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO USER(ID,CAGRID_ID,HOME_FOLDER_ID) VALUES (1,2,3)");

    final Folder homeFolder = userDao.getHomeFolder("2");
    assert homeFolder != null && homeFolder.getDescription().equals("Folder 3");
  }

  @Test
  public void getUsersWithVirtualFolder() {
    final String u1Id = newId(), u2Id = newId(), u3Id = newId(), u4Id = newId(), fId = newId(), oId = newId();
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID) VALUES ('" + fId + "')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO VIRTUAL_FOLDER(OBJECT_ID) VALUES ('" + fId + "')");

    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID) VALUES ('" + oId + "')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO VIRTUAL_FOLDER(OBJECT_ID) VALUES ('" + oId + "')");

    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO USER(ID,CAGRID_ID,FIRST_NAME) VALUES ('" + u1Id + "', '" + u1Id + "','Homer')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO USER(ID,CAGRID_ID,FIRST_NAME) VALUES ('" + u2Id + "', '" + u2Id + "','Homer')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO USER(ID,CAGRID_ID,FIRST_NAME) VALUES ('" + u3Id + "', '" + u3Id + "','Homer')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO USER(ID,CAGRID_ID,FIRST_NAME) VALUES ('" + u4Id + "', '" + u4Id + "','Homer')");

    flush();
    assert userDao.getUsersWithVirtualFolder(fId).isEmpty();

    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO JOIN_USER_SHARED_FOLDER(USER_ID,VIRTUAL_FOLDER_ID) VALUES ('" + u1Id + "', '" + fId + "')");
    assert userDao.getUsersWithVirtualFolder(fId).size() == 1;

    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO JOIN_USER_SHARED_FOLDER(USER_ID,VIRTUAL_FOLDER_ID) VALUES ('" + u1Id + "', '" + oId + "')");
    assert userDao.getUsersWithVirtualFolder(fId).size() == 1;

    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO JOIN_USER_SHARED_FOLDER(USER_ID,VIRTUAL_FOLDER_ID) VALUES ('" + u2Id + "', '" + oId + "')");
    assert userDao.getUsersWithVirtualFolder(fId).size() == 1;

    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO JOIN_USER_SHARED_FOLDER(USER_ID,VIRTUAL_FOLDER_ID) VALUES ('" + u3Id + "', '" + fId + "')");
    assert userDao.getUsersWithVirtualFolder(fId).size() == 2;

  }
}
