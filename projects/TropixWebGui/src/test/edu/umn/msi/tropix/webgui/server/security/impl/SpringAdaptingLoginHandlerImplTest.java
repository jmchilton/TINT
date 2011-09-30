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

package edu.umn.msi.tropix.webgui.server.security.impl;

import javax.servlet.http.HttpSession;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;

import edu.umn.msi.tropix.client.authentication.AuthenticationToken;
import edu.umn.msi.tropix.client.authentication.GuestAuthentication;
import edu.umn.msi.tropix.common.test.EasyMockUtils;

public class SpringAdaptingLoginHandlerImplTest {
  private SpringAdaptingLoginHandlerImpl handler;
  private AuthenticationManager authenticationManager;
  private HttpSession session;
  
  @BeforeMethod(groups = "unit")
  public void init() {
    authenticationManager = EasyMock.createMock(AuthenticationManager.class);
    session = EasyMock.createMock(HttpSession.class);
    handler = new SpringAdaptingLoginHandlerImpl(authenticationManager, Suppliers.ofInstance(session));
  }
  
  @Test(groups = "unit")
  public void testValidLogin() {
    final Capture<AuthenticationToken> authenticationCapture = EasyMockUtils.newCapture();
    // Return value ignored.
    EasyMock.expect(authenticationManager.authenticate(EasyMock.capture(authenticationCapture))).andReturn(new GuestAuthentication());
    EasyMock.replay(authenticationManager);
    handler.loginAttempt("user", "pass", "key");
    EasyMock.verify(authenticationManager);
    final AuthenticationToken token = authenticationCapture.getValue();
    assert token.getAuthenticationSource().equals("key");
    assert token.getPrincipal().equals("user");
    assert token.getPassword().equals("pass");
  }
  
  @Test(groups = "unit")
  public void testGuestLogin() {
    // Return value ignored.
    EasyMock.expect(authenticationManager.authenticate(EasyMock.isA(GuestAuthentication.class))).andReturn(new GuestAuthentication());
    EasyMock.replay(authenticationManager);
    handler.guestLogin();
    EasyMock.verify(authenticationManager);
  }
  
  @Test(groups = "unit")
  public void testLogout() {
    SecurityContextHolder.getContext().setAuthentication(new GuestAuthentication());
    assert SecurityContextHolder.getContext().getAuthentication() != null;
    session.invalidate();
    EasyMock.replay(session);
    handler.logout();
    EasyMock.verify(session);
    assert SecurityContextHolder.getContext().getAuthentication() == null;    
  }
  
}
