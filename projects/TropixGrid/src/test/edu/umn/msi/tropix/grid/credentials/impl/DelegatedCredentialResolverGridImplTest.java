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

import java.rmi.RemoteException;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.cagrid.gaards.cds.client.DelegatedCredentialUserClient;
import org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference;
import org.easymock.classextension.EasyMock;
import org.globus.gsi.GlobusCredential;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;

import edu.umn.msi.tropix.common.test.TestNGDataProviders;
import edu.umn.msi.tropix.grid.EprUtils;
import edu.umn.msi.tropix.grid.GridServiceFactory;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;

public class DelegatedCredentialResolverGridImplTest {

  @Test(groups = "unit", dataProvider="bool1", dataProviderClass=TestNGDataProviders.class)
  public void testResolve(final boolean exception) throws Exception {
    final Credential cred = Credentials.getMock();
    final DelegatedCredentialResolverGridImpl resolver = new DelegatedCredentialResolverGridImpl(Suppliers.ofInstance(cred));
    final EndpointReferenceType epr = EprUtils.getEprForAddress("http://moo");
    final GridServiceFactory<DelegatedCredentialUserClient> clientFactory = EasyMock.createMock(GridServiceFactory.class);
    resolver.setDelegatedCredentialUserClientFactory(clientFactory);
    final DelegatedCredentialUserClient client = EasyMock.createMock(DelegatedCredentialUserClient.class);
    EasyMock.expect(clientFactory.getService(epr, cred)).andReturn(client);
    final GlobusCredential globusCredential = EasyMock.createMock(GlobusCredential.class);
    client.getDelegatedCredential();
    if(exception) {
      EasyMock.expectLastCall().andThrow(new RemoteException());
    } else {
      EasyMock.expectLastCall().andReturn(globusCredential);      
    }
    EasyMock.replay(clientFactory, client);
    if(!exception) {
      assert resolver.getDelgatedCredential(new DelegatedCredentialReference(epr)).getGlobusCredential() == globusCredential;        
    } else {
      try {
        resolver.getDelgatedCredential(new DelegatedCredentialReference(epr));
        // Make sure exception is thrown.
        assert false;
      } catch(RuntimeException e) {
        assert true; // Check style hates empty blocks.
      }
    }
  }
  
}
