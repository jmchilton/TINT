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

package edu.umn.msi.tropix.client.credential;

import javax.annotation.Resource;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.client.authentication.config.Local;
import edu.umn.msi.tropix.common.test.FreshConfigTest;
import edu.umn.msi.tropix.grid.credentials.Credential;

@ContextConfiguration(locations = "classpath:edu/umn/msi/tropix/client/credential/testContext.xml")
public class LocalCredentialProviderSpringTest extends FreshConfigTest {
  @Resource
  private CredentialProvider globusCredentialProvider;
  
  @Test(groups = "spring")
  public void localCredentialProvider() {
    final CredentialCreationOptions options =  new CredentialCreationOptions();
    options.setAuthenticationSource(new Local());
    final Credential credential = globusCredentialProvider.getGlobusCredential("admin", "admin", options);
    assert credential.getIdentity().equals("admin");
  }
}
