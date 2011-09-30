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

package edu.umn.msi.tropix.client.credential.impl;

import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.client.authentication.LocalUserManager;
import edu.umn.msi.tropix.client.credential.GlobusCredentialOptions;
import edu.umn.msi.tropix.client.credential.GlobusCredentialProvider;
import edu.umn.msi.tropix.client.credential.InvalidUsernameOrPasswordException;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;

public class LocalGlobusCredentialProviderImpl implements GlobusCredentialProvider {
  private LocalUserManager localUserManager;
  
  public void setLocalUserManager(final LocalUserManager localUserManager) {
    this.localUserManager = localUserManager;
  }

  private static final long DEFAULT_LIFETIME = 12 * 60 * 60;
  private long defaultLifetime = DEFAULT_LIFETIME;
  
  public void setDefaultLifetime(final long defaultLifetime) {
    this.defaultLifetime = defaultLifetime;
  }

  public Credential getGlobusCredential(final String username, final String password, final GlobusCredentialOptions inputOptions) throws InvalidUsernameOrPasswordException {
    Preconditions.checkNotNull(username); 
    Preconditions.checkNotNull(password);
    final GlobusCredentialOptions options = inputOptions == null ? new GlobusCredentialOptions() : inputOptions;

    final Long inputLifetime = options.getLifetime();
    final Long lifetime = inputLifetime == null ? defaultLifetime : inputLifetime;
    
    
    Credential credential = null;
    if(localUserManager.isUsersPassword(username, password)) {
      credential = Credentials.getMock(username, lifetime);
    } else {
      throw new InvalidUsernameOrPasswordException(null);
    }
    return credential;    
  }

}
