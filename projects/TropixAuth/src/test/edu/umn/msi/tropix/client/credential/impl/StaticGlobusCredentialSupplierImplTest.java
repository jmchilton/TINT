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

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.client.credential.CredentialCreationOptions;
import edu.umn.msi.tropix.client.credential.CredentialProvider;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;

public class StaticGlobusCredentialSupplierImplTest {
  
  @Test(groups = "unit")
  public void getCredentials() {
    CredentialProvider credentialProvider = EasyMock.createMock(CredentialProvider.class);
    Credential credential = Credentials.getMock();
    
    CredentialCreationOptions options = new CredentialCreationOptions();
    EasyMock.expect(credentialProvider.getGlobusCredential(EasyMock.eq("moo"), EasyMock.eq("cow"), EasyMock.same(options))).andReturn(credential);
    
    EasyMock.replay(credentialProvider);
    
    
    final StaticGlobusCredentialSupplierImpl supplier = new StaticGlobusCredentialSupplierImpl();
    supplier.setGlobusCredentialProvider(credentialProvider);
    supplier.setUserName("moo");
    supplier.setPassword("cow");
    supplier.setOptions(options);
    assert credential == supplier.get();
    EasyMock.verify(credentialProvider);
  }
}
