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

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import edu.umn.msi.tropix.client.authentication.LocalUserManager;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.service.UserService;
import edu.umn.msi.tropix.webgui.server.aop.ServiceMethod;
import edu.umn.msi.tropix.webgui.server.security.UserSession;
import edu.umn.msi.tropix.webgui.services.session.LocalUserService;

@ManagedBean
public class LocalUserServiceImpl implements LocalUserService {
  private final UserSession userSession;
  private final LocalUserManager localUserManager;
  private final UserService userService;

  @Inject
  LocalUserServiceImpl(final UserSession userSession, final LocalUserManager localUserManager, final UserService userService) {
    this.userSession = userSession;
    this.localUserManager = localUserManager;
    this.userService = userService;
  }

  @ServiceMethod
  public void changePassword(final String currentPassword, final String newPassword) {
    if(localUserManager.isUsersPassword(userSession.getGridId(), currentPassword)) {
      localUserManager.changePassword(userSession.getGridId(), newPassword);
    }
  }

  @ServiceMethod(adminOnly = true)
  public void createLocalUser(final User user, final String password) {
    localUserManager.createUser(user.getCagridId(), password);
    userService.createUser(user);
  }

  public void modifyUser(final User user) {
    throw new RuntimeException("This feature is not yet implemented.");
  }

}
