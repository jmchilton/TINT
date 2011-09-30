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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.client.authentication.CredentialAuthentication;
import edu.umn.msi.tropix.client.authentication.Roles;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.webgui.server.security.UserSession;

@ManagedBean @Named("session")
class UserSessionImpl implements UserSession {
  private static final Log LOG = LogFactory.getLog(UserSessionImpl.class);
  private final Supplier<CredentialAuthentication> credentialAuthenticationSupplier;
 
  @Inject
  UserSessionImpl(@Named("credentialAuthenticationSupplier") final Supplier<CredentialAuthentication> credentialAuthenticationSupplier) {    
    this.credentialAuthenticationSupplier = credentialAuthenticationSupplier;
    LOG.debug("Constructing UserSessionImpl");
  }

  public boolean isLoggedIn() {
    final CredentialAuthentication authentication = credentialAuthenticationSupplier.get();
    final boolean loggedIn = authentication != null && authentication.isAuthenticated();
    return loggedIn;
  }
  
  public Credential getProxy() {
    return credentialAuthenticationSupplier.get().getCredential();
  }

  public String getGridId() {
    return getProxy().getIdentity();
  }

  public boolean isGuest() {
    return !credentialAuthenticationSupplier.get().getAuthorities().contains(Roles.ROLE_USER.getAuthority());
  }

  public boolean isAdmin() {
    return credentialAuthenticationSupplier.get().getAuthorities().contains(Roles.ROLE_ADMIN.getAuthority());
  }

}
