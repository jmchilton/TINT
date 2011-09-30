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

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.client.authentication.AuthenticationToken;
import edu.umn.msi.tropix.client.authentication.GuestAuthentication;
import edu.umn.msi.tropix.webgui.server.security.LoginHandler;

@ManagedBean
class SpringAdaptingLoginHandlerImpl implements LoginHandler {
  private static final Log LOG = LogFactory.getLog(SpringAdaptingLoginHandlerImpl.class);
  private final AuthenticationManager authenticationManager;
  private final Supplier<HttpSession> httpSessionSupplier;
  
  @Inject
  SpringAdaptingLoginHandlerImpl(final AuthenticationManager authenticationManager,
                                @Named("httpSessionSupplier") final Supplier<HttpSession> httpSessionSupplier) {
    this.authenticationManager = authenticationManager;
    this.httpSessionSupplier = httpSessionSupplier;
  }

  public void guestLogin() {
    this.authenticationManager.authenticate(new GuestAuthentication());
  }

  public void loginAttempt(final String username, final String password, final String key) {
    this.authenticationManager.authenticate(new AuthenticationToken(username, password, key));
  }

  // Clear HttpSession and spring authentication token  
  public void logout() {
    //Code roughly resembles SpringSecurity's SecurityContextLogoutHandler class
    final HttpSession session = httpSessionSupplier.get();
    session.invalidate();
    SecurityContextHolder.clearContext();
    LOG.debug("Cleared context and invalidated HttpSession.");
  }

}
