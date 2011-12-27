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

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.client.credential.CredentialCreationOptions;
import edu.umn.msi.tropix.client.credential.CredentialProvider;
import edu.umn.msi.tropix.grid.credentials.Credential;

public class StaticGlobusCredentialSupplierImpl implements Supplier<Credential> {
  private String username, password;
  private CredentialCreationOptions options;
  private CredentialProvider globusCredentialProvider;

  public Credential get() {
    return globusCredentialProvider.getGlobusCredential(username, password, options);
  }

  public void setUserName(final String username) {
    this.username = username;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public void setOptions(final CredentialCreationOptions options) {
    this.options = options;
  }

  public void setGlobusCredentialProvider(final CredentialProvider globusCredentialProvider) {
    this.globusCredentialProvider = globusCredentialProvider;
  }

}
