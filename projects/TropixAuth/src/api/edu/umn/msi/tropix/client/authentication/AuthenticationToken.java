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

package edu.umn.msi.tropix.client.authentication;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * An extension of the Spring Security's UsernamePasswordAuthenticationToken that
 * has an additional field to track the authentication source to be paired with.
 * 
 * @author John Chilton
 *
 */
public class AuthenticationToken extends UsernamePasswordAuthenticationToken {
  private final String authenticationSource;
  
  public AuthenticationToken(final String username, final String password, final String authenticationSource) {
    super(username, password);
    this.authenticationSource = authenticationSource;
  }

  public AuthenticationToken(final UsernamePasswordAuthenticationToken userPassToken, final String authenticationSource) {
    super(userPassToken.getPrincipal(), userPassToken.getCredentials());
    this.authenticationSource = authenticationSource;
  }
  
  public String getUsername() {
    return (String) super.getPrincipal();
  }
  
  public String getPassword() {
    return (String) super.getCredentials();
  }
    
  public String getAuthenticationSource() {
    return authenticationSource;
  }
  
}
