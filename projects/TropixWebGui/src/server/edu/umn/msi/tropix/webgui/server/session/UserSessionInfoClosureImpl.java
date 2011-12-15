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

package edu.umn.msi.tropix.webgui.server.session;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.service.UserService;
import edu.umn.msi.tropix.webgui.server.models.BeanSanitizer;
import edu.umn.msi.tropix.webgui.server.security.SecurityConstants;
import edu.umn.msi.tropix.webgui.server.security.UserSession;
import edu.umn.msi.tropix.webgui.services.session.Module;
import edu.umn.msi.tropix.webgui.services.session.SessionInfo;

@ManagedBean
class UserSessionInfoClosureImpl implements SessionInfoClosure {
  private static final Log LOG = LogFactory.getLog(UserSessionInfoClosureImpl.class);
  private UserService userService;
  private UserSession userSession;
  private BeanSanitizer beanSanitizer;

  @Inject
  public void setBeanSanitizer(final BeanSanitizer beanSanitizer) {
    this.beanSanitizer = beanSanitizer;
  }

  @Inject
  public void setUserService(final UserService userService) {
    this.userService = userService;
  }

  @Inject
  public void setUserSession(final UserSession userSession) {
    this.userSession = userSession;
  }

  public void apply(final SessionInfo sessionInfo) {
    final String gridIdentity = userSession.getGridId();
    final User user = userService.createOrGetUser(gridIdentity);
    final User gwtUser = beanSanitizer.sanitize(user);
    gwtUser.setHomeFolder(beanSanitizer.sanitize(userService.getHomeFolder(user.getCagridId())));

    final Group primaryGroup = userService.getPrimaryGroup(gridIdentity);
    final Group gwtPrimaryGroup = beanSanitizer.sanitize(primaryGroup);
    final Group[] groups = userService.getGroups(gridIdentity);
    final List<Group> gwtGroups = Lists.newArrayListWithExpectedSize(groups.length);
    for(Group group : groups) {
      gwtGroups.add(beanSanitizer.sanitize(group));
    }
    sessionInfo.setPrimaryGroup(gwtPrimaryGroup);
    sessionInfo.setGroups(gwtGroups);

    if(gridIdentity.equals(SecurityConstants.GUEST_IDENTITY)) {
      sessionInfo.getModules().add(Module.GUEST);
    } else {
      sessionInfo.getModules().add(Module.USER);

    }
    if(userSession.getProxy().getGlobusCredential() != null) {
      sessionInfo.getModules().add(Module.GRID);
    } else {
      sessionInfo.getModules().add(Module.LOCAL);
    }
    LOG.trace("Checking if " + gridIdentity + " is admin");
    if(userService.isAdmin(gridIdentity)) {
      sessionInfo.getModules().add(Module.ADMIN);
      sessionInfo.setAdmin(true);
    } else {
      sessionInfo.setAdmin(false);
    }
    sessionInfo.setUser(gwtUser);
    sessionInfo.setGuest(userSession.isGuest());
  }

}
