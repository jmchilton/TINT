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

package edu.umn.msi.tropix.webgui.services.session;

import java.util.Set;

import org.gwtwidgets.server.spring.GWTRequestMapping;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.User;

@RemoteServiceRelativePath("GroupService.rpc")
@GWTRequestMapping("/webgui/GroupService.rpc")
public interface GroupService extends RemoteService {

  Group createGroup(String groupName);

  void setPrimaryGroup(final String userId, final String groupId);

  void addUserToGroup(String userId, String groupId);

  void removeUserFromGroup(String userId, String groupId);

  Set<Group> getGroups();

  Set<Group> getGroups(String userId);

  Set<User> getUsers(String groupId);

  public static class Util {
    public static GroupServiceAsync getInstance() {
      return GWT.create(GroupService.class);
    }
  }
}
