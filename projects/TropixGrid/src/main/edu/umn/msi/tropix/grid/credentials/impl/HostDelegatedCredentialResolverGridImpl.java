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

import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.grid.credentials.DelegatedCredentialResolver;

/**
 * This is mostly for testing purposes. This delegated credential resolver, ignored the delegated credentials and always returns the default globus credential.
 * 
 * @author John Chilton (chilton at msi dot umn dot edu)
 * 
 */
class HostDelegatedCredentialResolverGridImpl implements DelegatedCredentialResolver {

  public Credential getDelgatedCredential(final DelegatedCredentialReference dcRef) {
    return Credentials.getDefaultCredential();
  }

}
