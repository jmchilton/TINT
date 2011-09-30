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

import javax.annotation.Resource;
import javax.inject.Inject;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.client.authentication.AuthenticationSourceManager;
import edu.umn.msi.tropix.client.authentication.CredentialAuthentication;
import edu.umn.msi.tropix.client.authentication.Roles;
import edu.umn.msi.tropix.common.test.FreshConfigTest;
import edu.umn.msi.tropix.webgui.client.utils.Iterables;
import edu.umn.msi.tropix.webgui.server.security.LoginHandler;

@ContextConfiguration("classpath:/WEB-INF/applicationContext.xml")
public class SecuritySpringTest extends FreshConfigTest {
  
  @Inject
  private AuthenticationManager authManager;
  
  @Resource
  private FilterChainProxy springSecurityFilterChain;
  
  @Inject 
  private LoginHandler loginHandler;
  
  @Inject 
  private AuthenticationSourceManager sourceManager; 
  
  @Test(groups = "spring")
  public void testFiltered() {
    assert authManager != null;
    assert springSecurityFilterChain != null;
    
    // Ensure an rpc service is filtered.
    assert !springSecurityFilterChain.getFilters("/webgui/object.rpc").isEmpty();
    
    // Ensure uploads filtered
    assert !springSecurityFilterChain.getFilters("/fileupload").isEmpty();

    // Ensure downloads filtered
    assert !springSecurityFilterChain.getFilters("/download").isEmpty();
       
    // Ensure XML services filtered
    assert !springSecurityFilterChain.getFilters("/xml").isEmpty();   
  }
  
  private void validateAuthentication(final Authentication authentication) {
    assert authentication.getAuthorities().contains(Roles.ROLE_GUEST.getAuthority());
    assert authentication.getAuthorities().contains(Roles.ROLE_USER.getAuthority());
    assert authentication.getAuthorities().contains(Roles.ROLE_ADMIN.getAuthority());
    
    assert authentication.isAuthenticated();

    // Assert mock credential is for user admin
    final CredentialAuthentication credentialAuthentication = (CredentialAuthentication) authentication; 
    assert credentialAuthentication.getCredential().getIdentity().equals("admin");    
  }
  
  @Test(groups = "spring")
  public void testLoginHandler() {
    // In a fresh configuration TINT starts with an admin / admin user available.
    loginHandler.loginAttempt("admin", "admin", Iterables.getOnlyElement(sourceManager.getAuthenticationSourceKeys()));
    validateAuthentication(SecurityContextHolder.getContext().getAuthentication());
  }
  
  @Test(groups = "spring")
  public void testAuthentication() {
    // In a fresh configuration TINT starts with an admin / admin user available.
    final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("admin", "admin");
    validateAuthentication(authManager.authenticate(token));
  }
  
  @Test(groups = "spring", expectedExceptions = AuthenticationException.class)
  public void testGuestDisabledByDefault() {
    loginHandler.guestLogin();
  }
  
}
