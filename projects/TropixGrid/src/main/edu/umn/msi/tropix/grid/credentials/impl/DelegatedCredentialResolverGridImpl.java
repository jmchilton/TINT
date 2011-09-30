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

import org.cagrid.gaards.cds.client.DelegatedCredentialUserClient;
import org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference;
import org.globus.gsi.GlobusCredential;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.grid.GridServiceFactories;
import edu.umn.msi.tropix.grid.GridServiceFactory;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.grid.credentials.DelegatedCredentialResolver;

class DelegatedCredentialResolverGridImpl implements DelegatedCredentialResolver {
  private final Supplier<Credential> proxySupplier;
  private GridServiceFactory<DelegatedCredentialUserClient> delegatedCredentialUserClientFactory = GridServiceFactories
      .getFactoryForClientClass(DelegatedCredentialUserClient.class);

  @VisibleForTesting
  void setDelegatedCredentialUserClientFactory(final GridServiceFactory<DelegatedCredentialUserClient> delegatedCredentialUserClientFactory) {
    this.delegatedCredentialUserClientFactory = delegatedCredentialUserClientFactory;
  }

  public DelegatedCredentialResolverGridImpl(final Supplier<Credential> proxySupplier) {
    this.proxySupplier = proxySupplier;
  }

  public Credential getDelgatedCredential(final DelegatedCredentialReference dcRef) {
    final Credential hostProxy = proxySupplier.get();
    final DelegatedCredentialUserClient dcClient = delegatedCredentialUserClientFactory.getService(dcRef.getEndpointReference(), hostProxy);
    GlobusCredential delegatedProxy = null;
    try {
      delegatedProxy = dcClient.getDelegatedCredential();
    } catch(final Exception e) {
      final String hostIdentity = hostProxy == null ? null : hostProxy.getIdentity();
      throw ExceptionUtils.convertException(e, "Failed to resolve delegated credential for delegate credential user client for reference " + dcRef
          + " using host credential with identity " + hostIdentity);
    }
    return Credentials.get(delegatedProxy);
  }

}
