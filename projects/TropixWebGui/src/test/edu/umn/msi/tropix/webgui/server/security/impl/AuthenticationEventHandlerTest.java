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

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.client.authentication.Roles;
import edu.umn.msi.tropix.webgui.client.utils.Lists;

public class AuthenticationEventHandlerTest {

  @Test(groups = "unit")
  public void testSecurityContextSet() {
    final AuthenticationEventHandler handler = new AuthenticationEventHandler();
    final Authentication auth = new UsernamePasswordAuthenticationToken("moo", "cow", Lists.newArrayList(Roles.ROLE_USER.getAuthority()));
    handler.publishAuthenticationSuccess(auth);
    assert SecurityContextHolder.getContext().getAuthentication() == auth;
  }

  @Test(groups = "unit")
  public void testHandlesFailures() {
    final AuthenticationEventHandler handler = new AuthenticationEventHandler();
    final Authentication auth = new UsernamePasswordAuthenticationToken("moo", "cow", Lists.newArrayList(Roles.ROLE_USER.getAuthority()));
    handler.publishAuthenticationFailure(new DisabledException("test"), auth);    
  }
  
}
