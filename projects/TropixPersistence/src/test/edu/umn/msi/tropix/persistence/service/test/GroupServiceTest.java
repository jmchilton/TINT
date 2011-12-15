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
import edu.umn.msi.tropix.persistence.service.GroupService;

public class GroupServiceTest extends ServiceTest {
  @Autowired
  private GroupService groupService;

  private static boolean containsGroupWithName(final Iterable<Group> groups, final String name) {
    boolean found = false;
    for(final Group group : groups) {
      if(group.getName().equals(name)) {
        found = true;
        break;
      }
    }
    return found;
  }

  @Test
  public void setPrimaryGroup() {
    final User admin = createTempUser();
    final Group group = createTempGroup();
    final User user = createTempUser(group);
    assert user.getPrimaryGroup() == null;
    groupService.setPrimaryGroup(admin.getCagridId(), user.getCagridId(), group.getId());
    assert user.getPrimaryGroup().equals(group);
  }

  @Test
  public void createAndList() {
    final User user1 = createTempUser();
    final String userId = user1.getCagridId();

    final Group group = groupService.createGroup(userId, "MooGroup");
    assert containsGroupWithName(Arrays.asList(groupService.getAllGroups(userId)), "MooGroup");
    assert !containsGroupWithName(Arrays.asList(groupService.getAllGroups(userId)), "MooGroup2");

    final User tempUser = createTempUser();
    assert !containsGroupWithName(Arrays.asList(groupService.getGroups(tempUser.getCagridId())), "MooGroup");
    assert groupService.getUsers(userId, group.getId()).length == 0;

    groupService.addUserToGroup(userId, tempUser.getCagridId(), group.getId());
    assert containsGroupWithName(Arrays.asList(groupService.getGroups(tempUser.getCagridId())), "MooGroup");
    assert groupService.getUsers(userId, group.getId()).length == 1;

    groupService.removeUserFromGroup(userId, tempUser.getCagridId(), group.getId());
    assert !containsGroupWithName(Arrays.asList(groupService.getGroups(tempUser.getCagridId())), "MooGroup");
    assert groupService.getUsers(userId, group.getId()).length == 0;
  }

}
