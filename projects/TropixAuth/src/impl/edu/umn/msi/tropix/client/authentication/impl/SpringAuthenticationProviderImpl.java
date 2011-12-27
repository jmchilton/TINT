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

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.client.authentication.AuthenticationSourceManager;
import edu.umn.msi.tropix.client.authentication.AuthenticationToken;
import edu.umn.msi.tropix.client.authentication.CredentialAuthenticationProducer;
import edu.umn.msi.tropix.client.credential.CredentialCreationOptions;
import edu.umn.msi.tropix.client.credential.CredentialProvider;
import edu.umn.msi.tropix.grid.credentials.Credential;

/**
 * Need to have a clearer interface with GlobusCredentialProvider before I will able
 * to use existing AuthenticationException options.
 * 
 * @author John Chilton
 * 
 */
class UnknownProblemAuthenticationException extends AuthenticationException {
  UnknownProblemAuthenticationException(final RuntimeException e) {
    super("Unknown problem with authentication.", e);
  }
}

@ManagedBean
@Named("authenticationProvider")
class SpringAuthenticationProviderImpl implements AuthenticationProvider {
  private final AuthenticationSourceManager authenticationSourceManager;
  private final CredentialProvider globusCredentialProvider;
  private final CredentialAuthenticationProducer credentialAuthenticationProvider;

  @Inject
  SpringAuthenticationProviderImpl(final AuthenticationSourceManager authenticationSourceManager,
                                   final CredentialProvider globusCredentialProvider,
                                   final CredentialAuthenticationProducer credentialAuthenticationProvider) {
    this.authenticationSourceManager = authenticationSourceManager;
    this.globusCredentialProvider = globusCredentialProvider;
    this.credentialAuthenticationProvider = credentialAuthenticationProvider;
  }

  public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
    // Argument must be a UsernamePasswordAuthenticationToken or a subclass.
    Preconditions.checkArgument(authentication instanceof UsernamePasswordAuthenticationToken);
    final UsernamePasswordAuthenticationToken userPassToken = (UsernamePasswordAuthenticationToken) authentication;

    // If this token is an AuthenticationToken it specifies a source, grab the appropriate options and
    // pass them through to the GlobusCredentialProvider, else try different sources.
    if(userPassToken instanceof AuthenticationToken) {
      final AuthenticationToken authenticationToken = (AuthenticationToken) userPassToken;
      final String key = authenticationToken.getAuthenticationSource();
      final CredentialCreationOptions options = authenticationSourceManager.getAuthenticationOptions(key);
      Credential credential = null;
      try {
        credential = globusCredentialProvider.getGlobusCredential(authenticationToken.getUsername(),
                                                                  authenticationToken.getPassword(),
                                                                  options);
        Preconditions.checkNotNull(credential);
      } catch(final RuntimeException e) {
        throw new UnknownProblemAuthenticationException(e);
      }
      // User the credentialAuthenticationProvider to build a CredentialAuthentication from this
      // credential and the input token.
      return credentialAuthenticationProvider.get(credential, authenticationToken);
    } else {
      // If this method was passed a UsernamePasswordAuthenticationToken that was not a AuthenticationToken, then
      // no authentication source has been specified. Loop through the authentication sources and create
      // authentication tokens and return
      AuthenticationException lastAe = null;
      Authentication resultAuth = null;
      for(final String key : authenticationSourceManager.getAuthenticationSourceKeys()) {
        try {
          resultAuth = authenticate(new AuthenticationToken(userPassToken, key));
          // This method cannot result null, only a valid token or an Exception
          Preconditions.checkNotNull(resultAuth);
          break;
        } catch(AuthenticationException ae) {
          lastAe = ae;
        }
      }
      if(resultAuth != null) {
        return resultAuth;
      } else {
        throw lastAe;
      }
    }
  }

  public boolean supports(final Class<? extends Object> clazz) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(clazz);
  }

}
