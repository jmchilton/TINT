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

import java.util.Map;

import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.client.credential.GlobusCredentialOptions;
import edu.umn.msi.tropix.client.credential.GlobusCredentialProvider;
import edu.umn.msi.tropix.client.credential.InvalidUsernameOrPasswordException;
import edu.umn.msi.tropix.grid.credentials.Credential;

public class DelegatingCredentialProviderImpl implements GlobusCredentialProvider {
  private Map<String, GlobusCredentialProvider> delegateMap;
  
  public void setDelegateMap(final Map<String, GlobusCredentialProvider> delegateMap) {
    this.delegateMap = delegateMap;
  }

  public Credential getGlobusCredential(final String username, final String password, final GlobusCredentialOptions options) throws InvalidUsernameOrPasswordException {
    GlobusCredentialProvider provider = null;
    for(final Map.Entry<String, GlobusCredentialProvider> entry : delegateMap.entrySet()) {
      final String prefix = entry.getKey().toLowerCase();
      final String idpUrl = options.getIdpUrl().toLowerCase();
      if(idpUrl.startsWith(prefix)) {
        provider = entry.getValue();
      }
    }
    Preconditions.checkState(provider != null);
    return provider.getGlobusCredential(username, password, options);
  }

}
