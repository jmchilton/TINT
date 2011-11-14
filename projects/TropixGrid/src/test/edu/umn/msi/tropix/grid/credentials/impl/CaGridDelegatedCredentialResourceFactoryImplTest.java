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
import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.credential.types.CaGridDelegatedCredentialResource;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.grid.credentials.DelegatedCredentialFactory;

public class CaGridDelegatedCredentialResourceFactoryImplTest {

  @Test(groups = "unit")
  public void testCreate() {
    final DelegatedCredentialFactory mockFactory = EasyMock.createMock(DelegatedCredentialFactory.class);
    final CaGridDelegatedCredentialResourceFactoryImpl resourceFactory = new CaGridDelegatedCredentialResourceFactoryImpl(mockFactory);
    
    // Test null works
    EasyMock.expect(mockFactory.createDelegatedCredential(null)).andStubReturn(null);
    EasyMock.replay(mockFactory);
    assert resourceFactory.createDelegatedCredential(null) == null;
    EasyMockUtils.verifyAndReset(mockFactory);
    
    final Credential credential = Credentials.getMock();
    final DelegatedCredentialReference dcRef = new DelegatedCredentialReference();
    EasyMock.expect(mockFactory.createDelegatedCredential(credential)).andStubReturn(dcRef);
    EasyMock.replay(mockFactory);
    
    final CaGridDelegatedCredentialResource resource = resourceFactory.createDelegatedCredential(credential);
    assert resource.getDelegatedCredentialReference() == dcRef;        
    EasyMockUtils.verifyAndReset(mockFactory);
  }
  
}
