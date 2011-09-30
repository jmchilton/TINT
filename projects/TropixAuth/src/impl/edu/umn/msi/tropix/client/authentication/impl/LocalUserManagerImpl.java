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

package edu.umn.msi.tropix.client.authentication.impl;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import edu.umn.msi.tropix.client.authentication.LocalUserManager;

public class LocalUserManagerImpl implements LocalUserManager {
  private LocalAuthenticationService localAuthenticationService;
  
  @PostConstruct
  public void init() {
    if(!userExists("admin")) {
      createUser("admin", "admin");
    }
  }
  
  public void createUser(final String username, final String password) {
    final String passwordHash = hash(password);
    localAuthenticationService.createUser(username, passwordHash);
  }

  public boolean isUsersPassword(final String username, final String password) {
    final String passwordHash = hash(password);
    final String expectedPasswordHash = localAuthenticationService.getPasswordHash(username);
    return expectedPasswordHash.equals(passwordHash);
  }
  
  private String hash(final String password) {
    return new String(Base64.encodeBase64(DigestUtils.sha(password)));
  }

  public boolean userExists(final String username) {
    return localAuthenticationService.userExists(username);
  }

  public void setLocalAuthenticationService(final LocalAuthenticationService localAuthenticationService) {
    this.localAuthenticationService = localAuthenticationService;
  }

  public void changePassword(final String username, final String password) {
    final String passwordHash = hash(password);
    localAuthenticationService.changePassword(username, passwordHash);    
  }
  
}
