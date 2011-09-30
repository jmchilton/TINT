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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.client.authentication.GuestAuthentication;
import edu.umn.msi.tropix.client.authentication.GuestCredentialAuthenticationProducer;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.webgui.server.security.SecurityConstants;
import edu.umn.msi.tropix.webgui.services.session.Module;

@ManagedBean @Named("guestAuthenticationProvider")
class GuestAuthenticationProviderImpl implements AuthenticationProvider  {
  private final GuestCredentialAuthenticationProducer credentialAuthenticationProcuder;
  private long proxyLifetime;
  private boolean allowGuestLogin = false;
  
  @Inject
  GuestAuthenticationProviderImpl(final GuestCredentialAuthenticationProducer credentialAuthenticationProducer,
                                  @Value("${proxy.lifetime}") final long proxyLifetime) {
    this.credentialAuthenticationProcuder = credentialAuthenticationProducer;
    this.proxyLifetime = proxyLifetime;
  }
  
  
  public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
    Preconditions.checkArgument(authentication instanceof GuestAuthentication);
    if(allowGuestLogin) {
      return credentialAuthenticationProcuder.getGuestCredential(Credentials.getMock(SecurityConstants.GUEST_IDENTITY, proxyLifetime), authentication);
    } else {
      throw new DisabledException("Guest account disabled.");
    }
  }

  public boolean supports(final Class<? extends Object> clazz) {
    return GuestAuthentication.class.isAssignableFrom(clazz);
  }

  @Inject
  public void setModules(@Named("modulesIterable") final Iterable<String> modules) {
    this.allowGuestLogin = Iterables.contains(modules, Module.GUEST.toString());
  }

}
