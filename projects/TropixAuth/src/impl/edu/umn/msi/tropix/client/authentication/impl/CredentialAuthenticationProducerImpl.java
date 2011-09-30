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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.client.authentication.CredentialAuthentication;
import edu.umn.msi.tropix.client.authentication.CredentialAuthenticationProducer;
import edu.umn.msi.tropix.client.authentication.Roles;
import edu.umn.msi.tropix.common.time.TimeProvider;
import edu.umn.msi.tropix.common.time.TimeProviders;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.persistence.service.UserService;

/**
 * 
 * This is not in the tropix-client auth-impl module because it has dependencies on the
 * Tropix metadata database, none of the other code there does. 
 * 
 * @author john
 *
 */ 
@ManagedBean @ManagedResource
class CredentialAuthenticationProducerImpl implements CredentialAuthenticationProducer, edu.umn.msi.tropix.client.authentication.GuestCredentialAuthenticationProducer {
  private TimeProvider timeProvider = TimeProviders.getInstance();
  private final UserService userService;  
  @Value("${login.timeout?:43200}")
  private long loginTimeout = 43200L;
  
  @VisibleForTesting
  void setTimeProvider(final TimeProvider timeProvider) {
    this.timeProvider = timeProvider;
  }
  
  @Inject
  CredentialAuthenticationProducerImpl(final UserService userService) {
    this.userService = userService;
  }
  
  @ManagedAttribute
  public long getLoginTimeout() {
    return loginTimeout;
  }
  
  public void setLoginTimeout(final long loginTimeout) {
    this.loginTimeout = loginTimeout;
  }
    
  private static final class CredentialAuthenticationImpl extends UsernamePasswordAuthenticationToken implements CredentialAuthentication, Serializable {
    private final TimeProvider timeProvider;
    private final long loginTimeout;
    private Credential credential;
    private long initialAuthenticationTime;
       
    private CredentialAuthenticationImpl(final Credential credential, 
                                         final Authentication inputAuthentication, 
                                         final Collection<GrantedAuthority> authorities,
                                         final TimeProvider timeProvider,
                                         final long loginTimeout) {
      super(inputAuthentication.getPrincipal(), inputAuthentication.getCredentials(), authorities);
      this.timeProvider = timeProvider;
      this.initialAuthenticationTime = timeProvider.currentTimeMillis();
      this.credential = credential;
      this.loginTimeout = loginTimeout;
    }
    
    // Credential expiring if it has been more than loginTimeout seconds since initialAuthenticationTime.
    private boolean credentialExpiring() {
      return (timeProvider.currentTimeMillis() - initialAuthenticationTime) > 1000 * loginTimeout;
    }
   
    public boolean isAuthenticated() {
      return super.isAuthenticated() && !credentialExpiring();
    }
    
    public Credential getCredential() {
      return credential;
    }
    
  }
  
  public CredentialAuthentication getGuestCredential(final Credential credential, final Authentication guestAuthentication) {
    final List<GrantedAuthority> authorities = Lists.<GrantedAuthority>newArrayList(Roles.ROLE_GUEST.getAuthority());
    return new CredentialAuthenticationImpl(credential, guestAuthentication, authorities, timeProvider, loginTimeout);
  }

  public CredentialAuthentication get(final Credential credential, final Authentication inputAuthentication) {
    Preconditions.checkArgument(credential.getTimeLeft() > 0);
    final List<GrantedAuthority> authorities = Lists.<GrantedAuthority>newArrayList(Roles.ROLE_GUEST.getAuthority(), Roles.ROLE_USER.getAuthority());
    if(userService.isAdmin(credential.getIdentity())) {
      authorities.add(Roles.ROLE_ADMIN.getAuthority());
    }
    return new CredentialAuthenticationImpl(credential, inputAuthentication, authorities, timeProvider, loginTimeout);
  }

}
