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

import org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference;

import edu.umn.msi.tropix.credential.types.CaGridDelegatedCredentialResource;
import edu.umn.msi.tropix.credential.types.CredentialResource;
import edu.umn.msi.tropix.credential.types.GlobusCredentialResource;
import edu.umn.msi.tropix.credential.types.SimpleCredentialResource;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.CredentialResourceResolver;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.grid.credentials.DelegatedCredentialResolver;

class CredentialResourceResolverImpl implements CredentialResourceResolver {
  private final DelegatedCredentialResolver dcResolver;
  
  CredentialResourceResolverImpl(final DelegatedCredentialResolver dcResolver) {
    this.dcResolver = dcResolver;
  }
  
  public Credential getCredential(final CredentialResource credentialResource) {
    Credential credential = null;
    if(credentialResource instanceof CaGridDelegatedCredentialResource) {
      final DelegatedCredentialReference dcRef = ((CaGridDelegatedCredentialResource) credentialResource).getDelegatedCredentialReference();
      credential = dcResolver.getDelgatedCredential(dcRef);
    } else if(credentialResource instanceof GlobusCredentialResource) {
      credential = Credentials.fromString(new String(((GlobusCredentialResource) credentialResource).getEncodedCredential()));
    } else if(credentialResource instanceof SimpleCredentialResource) {
      credential = Credentials.getMock(((SimpleCredentialResource) credentialResource).getIdentity());
    }
    return credential;
  }

}
