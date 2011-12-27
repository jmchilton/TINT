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

import javax.annotation.Nullable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.gsi.GlobusCredential;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;

import edu.umn.msi.tropix.client.authentication.config.CaGrid;
import edu.umn.msi.tropix.client.credential.CredentialCreationOptions;
import edu.umn.msi.tropix.client.credential.CredentialProvider;
import edu.umn.msi.tropix.client.credential.InvalidUsernameOrPasswordException;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import gov.nih.nci.cagrid.authentication.bean.BasicAuthenticationCredential;
import gov.nih.nci.cagrid.authentication.client.AuthenticationClient;
import gov.nih.nci.cagrid.dorian.client.IFSUserClient;
import gov.nih.nci.cagrid.dorian.ifs.bean.ProxyLifetime;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

public class GlobusCredentialProviderImpl implements CredentialProvider {
  private static final Log LOG = LogFactory.getLog(GlobusCredentialProviderImpl.class);
  private static final int DEFAULT_DELEGATION_PATH_LENGTH = 10;
  private static final ProxyLifetime DEFAULT_PROXY_LIFETIME = new ProxyLifetime(12, 0, 0); // new ProxyLifetime(24 * 31 * 60,0,0);
  private static final Function<Long, ProxyLifetime> PROXY_LIFETIME_FUNCTION = new ProxyLifetimeFunctionImpl();

  private AuthenticationClientFactory authenticationClientFactory = AuthenticationClientFactories.getInstance();
  private IFSUserClientFactory ifsUserClientFactory = IFSUserClientFactories.getInstance();

  private int defaultDelegationPathLength = DEFAULT_DELEGATION_PATH_LENGTH;
  private ProxyLifetime defaultProxyLifetime = DEFAULT_PROXY_LIFETIME;
  private IFSUserClient defaultIFSUserClient;
  private String defaultIdpUrl;

  public Credential getGlobusCredential(final String username, final String password, @Nullable final CredentialCreationOptions inputOptions) throws InvalidUsernameOrPasswordException {
    final CredentialCreationOptions options = inputOptions == null ? new CredentialCreationOptions() : inputOptions;
    final CaGrid caGridAuthenticationOptions = options.getAuthenicationSource(CaGrid.class);
    final gov.nih.nci.cagrid.authentication.bean.Credential credential = new gov.nih.nci.cagrid.authentication.bean.Credential();
    final BasicAuthenticationCredential bac = new BasicAuthenticationCredential();
    bac.setUserId(username);
    bac.setPassword(password);
    credential.setBasicAuthenticationCredential(bac);
    final String idpUrl = caGridAuthenticationOptions.getAuthenticationServiceUrl();
    LOG.debug("Creating authentication client ");
    final AuthenticationClient client = authenticationClientFactory.getClient(idpUrl == null ? defaultIdpUrl : idpUrl, credential);
    LOG.debug("Attempting to authenticate user " + username);
    SAMLAssertion saml;
    try {
      saml = client.authenticate();
    } catch(final Exception e) {
      throw new RuntimeException("Exception authenticating user proxy.", e);
    }
    final String ifsUrl = caGridAuthenticationOptions.getDorianServiceUrl();
    LOG.debug("Creating or settings IFSUserClient");
    final IFSUserClient ifsUserClient = (ifsUrl == null) ? defaultIFSUserClient : ifsUserClientFactory.getClient(ifsUrl);
    final Long lifetime = caGridAuthenticationOptions.getLifetime();
    final ProxyLifetime proxyLifetime = lifetime == null ? defaultProxyLifetime : PROXY_LIFETIME_FUNCTION.apply(lifetime);
    Integer pathLength = caGridAuthenticationOptions.getDelegationPathLength();
    pathLength = pathLength == null ? defaultDelegationPathLength : pathLength;
    LOG.debug("Attempting to create proxy");
    GlobusCredential proxy;
    try {
      proxy = ifsUserClient.createProxy(saml, proxyLifetime, pathLength);
    } catch(final Exception e) {
      throw new RuntimeException("Failed to create proxy", e);
    }
    return Credentials.get(proxy);
  }

  public void setDefaultDorianUrl(final String url) {
    defaultIFSUserClient = ifsUserClientFactory.getClient(url);
  }

  public void setDefaultIdpUrl(final String url) {
    defaultIdpUrl = url;
  }

  public void setDefaultDelegationPathLength(final int defaultDelegationPathLength) {
    this.defaultDelegationPathLength = defaultDelegationPathLength;
  }

  public void setDefaultProxyLifetime(final long lifetime) {
    defaultProxyLifetime = PROXY_LIFETIME_FUNCTION.apply(lifetime);
  }

  @VisibleForTesting
  void setAuthenticationClientFactory(final AuthenticationClientFactory authenticationClientFactory) {
    this.authenticationClientFactory = authenticationClientFactory;
  }

  @VisibleForTesting
  void setIfsUserClientFactory(final IFSUserClientFactory ifsUserClientFactory) {
    this.ifsUserClientFactory = ifsUserClientFactory;
  }


}
