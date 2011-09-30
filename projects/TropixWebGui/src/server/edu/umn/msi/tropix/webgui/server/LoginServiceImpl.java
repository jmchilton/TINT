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
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.client.authentication.AuthenticationSourceManager;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.webgui.server.aop.ServiceMethod;
import edu.umn.msi.tropix.webgui.server.security.LoginHandler;
import edu.umn.msi.tropix.webgui.server.security.UserSession;
import edu.umn.msi.tropix.webgui.services.session.LoginInfo;
import edu.umn.msi.tropix.webgui.services.session.LoginService;
import edu.umn.msi.tropix.webgui.services.session.Module;
import edu.umn.msi.tropix.webgui.services.session.SessionInfo;

@ManagedBean
public class LoginServiceImpl implements LoginService {
  private static final Log LOG = LogFactory.getLog(LoginServiceImpl.class);
  private final LoginHandler loginHandler;
  private final UserSession userSession;
  private final AuthenticationSourceManager authenticationSourceManager;
  private final Supplier<SessionInfo> sessionInfoSupplier;
  private boolean allowGuestLogin = false;

  @Inject
  public LoginServiceImpl(final LoginHandler loginHandler,
                          final AuthenticationSourceManager authenticationSourceManager,
                          @Named("sessionInfoSupplier") final Supplier<SessionInfo> sessionInfoSupplier,
                          final UserSession userSession) {
    this.loginHandler = loginHandler;
    this.authenticationSourceManager = authenticationSourceManager;
    this.sessionInfoSupplier = sessionInfoSupplier;
    this.userSession = userSession;
  }

  @Inject
  public void setModules(@Named("modulesIterable") final Iterable<String> modules) {
    this.allowGuestLogin = Iterables.contains(modules, Module.GUEST.toString());
  }

  private SessionInfo getSessionInfo() {
    return sessionInfoSupplier.get();
  }

  @ServiceMethod(secure = false)
  public SessionInfo guestLogin() {
    try {
      loginHandler.guestLogin();
      return getSessionInfo();
    } catch(RuntimeException e) {
      return null;
    }
  }

  @ServiceMethod(secure = false, readOnly = false)
  public SessionInfo isValidLogin(final String name, final String password, final String authenticationSourceKey) {
    LOG.debug(String.format("Log attempt for username [%s], password [%s], and authenticationSourceKey [%s]", name, password == null ? "" : password.replaceAll(".", "*"), authenticationSourceKey));
    try {
      loginHandler.loginAttempt(name, password, authenticationSourceKey);
      SessionInfo sessionInfo = getSessionInfo();
      LOG.debug("Obtained sessionInfo object " + sessionInfo);
      return sessionInfo;
    } catch(RuntimeException e) {
      ExceptionUtils.logQuietly(LOG, e);
      return null;
    }
  }

  @ServiceMethod(secure = false, readOnly = true)
  public LoginInfo getUserIfLoggedIn() {
    final LoginInfo loginInfo = new LoginInfo();
    if(userSession.isLoggedIn()) {
      loginInfo.setSessionInfo(getSessionInfo());
    } else {
      loginInfo.setAllowGuestLogin(allowGuestLogin);
      final Iterable<String> authenticationSources = authenticationSourceManager.getAuthenticationSourceKeys();
      LOG.trace("Returning authenticationSources " + Iterables.toString(authenticationSources));
      loginInfo.setAuthenticationServices(Lists.newArrayList(authenticationSources));
    }
    return loginInfo;
  }

  @ServiceMethod(secure = false, readOnly = true)
  public boolean isLoggedIn() {
    return userSession.isLoggedIn();
  }

  @ServiceMethod(readOnly = true)
  public void logout() {
    loginHandler.logout();
  }
}
