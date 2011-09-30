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

import java.util.List;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.client.authentication.AuthenticationSourceManager;
import edu.umn.msi.tropix.common.test.MockObjectCollection;
import edu.umn.msi.tropix.common.test.TestNGDataProviders;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.webgui.server.security.LoginHandler;
import edu.umn.msi.tropix.webgui.server.security.UserSessionBean;
import edu.umn.msi.tropix.webgui.services.session.LoginInfo;
import edu.umn.msi.tropix.webgui.services.session.Module;
import edu.umn.msi.tropix.webgui.services.session.SessionInfo;

public class LoginServiceImplTest extends BaseGwtServiceTest {
  private MockObjectCollection mocks;
  private AuthenticationSourceManager authenticationSourceManager;
  private LoginHandler securityProvider;
  private Supplier<SessionInfo> sessionInfoSupplier = Suppliers.ofInstance(new SessionInfo());
  private UserSessionBean userSession;
  private LoginServiceImpl loginService;
  
  @BeforeMethod(groups = "unit")
  public void init() {
    authenticationSourceManager = EasyMock.createMock(AuthenticationSourceManager.class);
    securityProvider = EasyMock.createMock(LoginHandler.class);
    mocks = MockObjectCollection.fromObjects(authenticationSourceManager, securityProvider);
    userSession = new UserSessionBean();
    userSession.setProxy(Credentials.getMock());
    loginService = new LoginServiceImpl(securityProvider, authenticationSourceManager, sessionInfoSupplier, userSession);
  }
  
  /*
  // This now happens at the level of the AuthenticationProvider
  @Test(groups = "unit")
  public void testPreventGuestLogin()  {
    loginService.setModules(Lists.<String>newArrayList());
    assert null == loginService.guestLogin();    
  }
  */
  
  @Test(groups = "unit")
  public void testAllowGuestLogin()  {
    loginService.setModules(Lists.<String>newArrayList(Module.GUEST.toString()));
    securityProvider.guestLogin();
    mocks.replay();
    assert sessionInfoSupplier.get() == loginService.guestLogin();
    mocks.verifyAndReset();
  }
  
  @Test(groups = "unit", dataProvider="bool1", dataProviderClass=TestNGDataProviders.class)
  public void testUserLoggedIn(final boolean isLoggedIn) {
    userSession.setProxy(isLoggedIn ? Credentials.getMock() : null);
    mocks.replay();
    assert loginService.isLoggedIn() == isLoggedIn;
    mocks.verifyAndReset();
  }
  
  @Test(groups = "unit", dataProvider="bool1", dataProviderClass=TestNGDataProviders.class)
  public void testLoggedOut(final boolean isLoggedIn) {
    securityProvider.logout();
    mocks.replay();
    loginService.logout();
    mocks.verifyAndReset();
  }
  
  @Test(groups = "unit")
  public void testGetUserWhenLoggedIn() {
    mocks.replay();
    final LoginInfo loginInfo = loginService.getUserIfLoggedIn();
    assert loginInfo.getSessionInfo() == sessionInfoSupplier.get();
    mocks.verifyAndReset();
  }
  
  @Test(groups = "unit")
  public void testGetUserWhenNotLoggedIn() {
    userSession.setProxy(null);
    final List<String> sources = Lists.newArrayList("Moo", "Cow");
    EasyMock.expect(authenticationSourceManager.getAuthenticationSourceKeys()).andReturn(sources);
    mocks.replay();
    final LoginInfo loginInfo = loginService.getUserIfLoggedIn();
    mocks.verifyAndReset();
    assert loginInfo.getSessionInfo() == null;
    assert loginInfo.getAuthenticationServices().equals(sources);
  }

  @Test(groups = "unit")
  public void testIsValidLogin() {
    securityProvider.loginAttempt("moo", "cow", "key");    
    mocks.replay();
    assert sessionInfoSupplier.get() == loginService.isValidLogin("moo", "cow", "key");
    mocks.verifyAndReset();
  }

  @Test(groups = "unit")
  public void testIsNotValidLogin() {
    securityProvider.loginAttempt("moo", "cow", "key");
    EasyMock.expectLastCall().andThrow(new RuntimeException());
    mocks.replay();
    assert null == loginService.isValidLogin("moo", "cow", "key");
    mocks.verifyAndReset();
  }

}
