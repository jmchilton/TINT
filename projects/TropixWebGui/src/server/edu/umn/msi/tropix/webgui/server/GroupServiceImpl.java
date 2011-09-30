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

package edu.umn.msi.tropix.webgui.server;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.service.GroupService;
import edu.umn.msi.tropix.webgui.server.aop.ServiceMethod;
import edu.umn.msi.tropix.webgui.server.models.BeanSanitizer;
import edu.umn.msi.tropix.webgui.server.security.UserSession;

@ManagedBean
public class GroupServiceImpl implements edu.umn.msi.tropix.webgui.services.session.GroupService {
  private final GroupService groupService;
  private final BeanSanitizer beanSanitizer;
  private final UserSession userSession;

  @Inject
  GroupServiceImpl(final GroupService groupService, final BeanSanitizer beanSanitizer, final UserSession userSession) {
    this.groupService = groupService;
    this.beanSanitizer = beanSanitizer;
    this.userSession = userSession;
  }

  @ServiceMethod(adminOnly = true)
  public void addUserToGroup(final String userId, final String groupId) {
    this.groupService.addUserToGroup(this.userSession.getGridId(), userId, groupId);
  }

  @ServiceMethod(adminOnly = true)
  public Group createGroup(final String groupName) {
    return this.beanSanitizer.sanitize(this.groupService.createGroup(this.userSession.getGridId(), groupName));
  }

  public Set<Group> getGroups() {
    return this.sanitizeArray(this.groupService.getAllGroups(this.userSession.getGridId()));
  }

  public Set<Group> getGroups(final String userId) {
    return this.sanitizeArray(this.groupService.getGroups(userId));
  }

  private <T> Set<T> sanitizeArray(final T[] array) {
    final HashSet<T> set = new HashSet<T>();
    for(final T object : array) {
      set.add(this.beanSanitizer.sanitize(object));
    }
    return set;
  }

  @ServiceMethod(adminOnly = true)
  public Set<User> getUsers(final String groupId) {
    return this.sanitizeArray(this.groupService.getUsers(this.userSession.getGridId(), groupId));
  }

  @ServiceMethod(adminOnly = true)
  public void removeUserFromGroup(final String userId, final String groupId) {
    this.groupService.removeUserFromGroup(this.userSession.getGridId(), userId, groupId);
  }

}
