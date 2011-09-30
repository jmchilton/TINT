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

package edu.umn.msi.tropix.grid.credentials.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.cds.client.ClientConstants;
import org.cagrid.gaards.cds.client.DelegationUserClient;
import org.cagrid.gaards.cds.common.AllowedParties;
import org.cagrid.gaards.cds.common.IdentityDelegationPolicy;
import org.cagrid.gaards.cds.common.ProxyLifetime;
import org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference;
import org.springframework.util.StringUtils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.grid.GridServiceFactories;
import edu.umn.msi.tropix.grid.GridServiceFactory;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.DelegatedCredentialFactory;

public class DelegatedCredentialFactoryGridImpl implements DelegatedCredentialFactory {
  private static final Log LOG = LogFactory.getLog(DelegatedCredentialFactoryGridImpl.class);
  private GridServiceFactory<DelegationUserClient> clientFactory = GridServiceFactories.getFactoryForClientClass(DelegationUserClient.class);
  private static final ProxyLifetime DEFAULT_DELEGATION_LIFETIME;
  private static final ProxyLifetime DEFAULT_CREDENTIAL_LIFETIME;
  static {
    DEFAULT_DELEGATION_LIFETIME = new ProxyLifetime(1, 0, 0);
    DEFAULT_CREDENTIAL_LIFETIME = new ProxyLifetime(1, 0, 0);
  }
  private String cdsUrl = null;
  private ProxyLifetime delegationLifetime = DEFAULT_DELEGATION_LIFETIME;
  private ProxyLifetime issuedCredentialLifetime = DEFAULT_CREDENTIAL_LIFETIME;
  private int delegationPathLength = 2;
  private int issuedCredentialPathLength = 1;
  private int keySize = ClientConstants.DEFAULT_KEY_SIZE;
  private Iterable<String> allowedPartiesIterable;

  private static String toString(final ProxyLifetime proxyLifetime) {
    return "ProxyLifetime[hours=" + proxyLifetime.getHours() + ",minutes=" 
           + proxyLifetime.getMinutes() + ",seconds=" + proxyLifetime.getSeconds() + "]";
  }
  
  @VisibleForTesting
  void setClientFactory(final GridServiceFactory<DelegationUserClient> clientFactory) {
    this.clientFactory = clientFactory;
  }
  
  public DelegatedCredentialReference createDelegatedCredential(final Credential proxy) {
    if(proxy == null || !StringUtils.hasText(cdsUrl)) {
      return null;
    }
    final IdentityDelegationPolicy policy = new IdentityDelegationPolicy();
    final AllowedParties allowedParties = new AllowedParties();
    policy.setAllowedParties(allowedParties);
    allowedParties.setGridIdentity(Iterables.toArray(allowedPartiesIterable, String.class));
    final DelegationUserClient delClient = clientFactory.getService(cdsUrl, proxy);
    LOG.trace("Creating a delegated credential for identity " + proxy.getIdentity() 
              + " delegationLifetime is " + toString(delegationLifetime) + " issuedCredentialLifetime is " + toString(issuedCredentialLifetime));
    try {
      final DelegatedCredentialReference reference = delClient.delegateCredential(delegationLifetime, delegationPathLength, policy, issuedCredentialLifetime, issuedCredentialPathLength, keySize);
      return reference;
    } catch(final Exception e) {
      throw new IllegalStateException("Failed to create delegated credential reference.", e);
    }
  }

  public void setCdsUrl(final String cdsUrl) {
    this.cdsUrl = cdsUrl;
  }

  public void setDelegationLifetime(final long delegationLifetime) {
    this.delegationLifetime = secondsToProxyLifetime(delegationLifetime);
  }

  public void setIssuedCredentialLifetime(final long issuedCredentialLifetime) {
    this.issuedCredentialLifetime = secondsToProxyLifetime(issuedCredentialLifetime);
  }

  public void setDelegationPathLength(final int delegationPathLength) {
    this.delegationPathLength = delegationPathLength;
  }

  public void setIssuedCredentialPathLength(final int issuedCredentialPathLength) {
    this.issuedCredentialPathLength = issuedCredentialPathLength;
  }

  public void setKeySize(final int keySize) {
    this.keySize = keySize;
  }

  public void setAllowedPartiesIterable(final Iterable<String> allowedPartiesIterable) {
    this.allowedPartiesIterable = allowedPartiesIterable;
  }

  private ProxyLifetime secondsToProxyLifetime(final Long lifetime) {
    final int hours = (int) (lifetime / 3600);
    final int seconds = (int) (lifetime % 60);
    final int minutes = (int) ((lifetime % 3600) / 60);
    return new ProxyLifetime(hours, minutes, seconds);
  }
}
