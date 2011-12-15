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

package edu.umn.msi.tropix.persistence.service.test;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.service.PersistenceConstants;
import edu.umn.msi.tropix.persistence.service.UserService;

public class UserServiceTest extends ServiceTest {
  @Autowired
  private UserService userService;

  @Test
  public void createFromTemplate() {
    final String gridId = newId();
    final User user = new User();
    user.setCagridId(gridId);
    user.setFirstName("first");
    user.setLastName("last");
    user.setEmail("moo@cow.org");
    user.setPhone("555-5678");

    userService.createUser(user);

    final User loadedUser = getUserDao().loadUser(gridId);
    assert loadedUser.getFirstName().equals("first");
    assert loadedUser.getLastName().equals("last");
    assert loadedUser.getEmail().equals("moo@cow.org");
    assert loadedUser.getPhone().equals("555-5678");
  }

  @Test
  public void create() {
    final String gridId = newId();
    User user;
    user = userService.createOrGetUser(gridId);
    assert user.getHomeFolder().getCommitted();
    assert user.getHomeFolder().getDeletedTime() == null;

    assert user.getCagridId().equals(gridId);

    user = userService.createOrGetUser(gridId);
    assert user.getCagridId().equals(gridId);

    user.setFirstName("homer");
    user = userService.createOrGetUser(gridId);
    assert user.getCagridId().equals(gridId);
    assert user.getFirstName().equals("homer");

  }

  @Test
  public void admin() {
    userService.createOrGetUser("notadmin");
    assert !userService.isAdmin("notadmin");
    userService.createOrGetUser(PersistenceConstants.DEFAULT_ADMIN_USER);
    assert userService.isAdmin(PersistenceConstants.DEFAULT_ADMIN_USER);
  }

  @Test
  public void listUsers() {
    final User user = createTempUser();
    assert Arrays.asList(userService.getUsers()).contains(user);
  }

  @Test
  public void testGetPrimaryGroup() {
    final User user = createTempUser();
    final Group group = createTempGroup(user);

    assert userService.getPrimaryGroup(user.getCagridId()) == null;

    user.setPrimaryGroup(group);
    getUserDao().saveOrUpdateUser(user);

    assert userService.getPrimaryGroup(user.getCagridId()).getId().equals(group.getId());

  }

  @Test
  public void testGetGroups() {
    final User user = createTempUser();

    int numGroups = userService.getGroups(user.getCagridId()).length;

    createTempGroup(user);
    assert userService.getGroups(user.getCagridId()).length == numGroups + 1;
  }

}
