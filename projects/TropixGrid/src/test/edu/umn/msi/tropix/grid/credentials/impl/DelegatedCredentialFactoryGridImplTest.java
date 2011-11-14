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
import java.util.Arrays;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gaards.cds.client.DelegationUserClient;
import org.cagrid.gaards.cds.common.DelegationPolicy;
import org.cagrid.gaards.cds.common.IdentityDelegationPolicy;
import org.cagrid.gaards.cds.common.ProxyLifetime;
import org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference;
import org.cagrid.gaards.cds.stubs.types.CDSInternalFault;
import org.cagrid.gaards.cds.stubs.types.DelegationFault;
import org.cagrid.gaards.cds.stubs.types.PermissionDeniedFault;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.test.TestNGDataProviders;
import edu.umn.msi.tropix.grid.GridServiceFactory;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;

public class DelegatedCredentialFactoryGridImplTest {

  @Test(groups = "unit")
  public void testCreateNull() {
    final DelegatedCredentialFactoryGridImpl factory = new DelegatedCredentialFactoryGridImpl();
    factory.setCdsUrl("");
    assert factory.createDelegatedCredential(Credentials.getMock()) == null;
    
    factory.setCdsUrl("http://aUrl/");
    assert factory.createDelegatedCredential(null) == null;
  }
  
  @Test(groups = "unit", dataProvider="bool1", dataProviderClass=TestNGDataProviders.class)
  public void testCreate(final boolean exception) throws CDSInternalFault, DelegationFault, PermissionDeniedFault, RemoteException, MalformedURIException {
    final DelegatedCredentialFactoryGridImpl factory = new DelegatedCredentialFactoryGridImpl();
    @SuppressWarnings("unchecked")
    final GridServiceFactory<DelegationUserClient> clientFactory = EasyMock.createMock(GridServiceFactory.class);
    DelegationUserClient client = EasyMock.createMock(DelegationUserClient.class);
    final String address = "http://cds";
    final Credential credential = Credentials.getMock();
    EasyMock.expect(clientFactory.getService(address, credential)).andStubReturn(client);
    factory.setClientFactory(clientFactory);
    
    final DelegatedCredentialReference dcRef = new DelegatedCredentialReference();
    final ProxyLifetime dLifetime = new ProxyLifetime(3, 0, 0);
    final ProxyLifetime iLifetime = new ProxyLifetime(3, 0, 1);
   
    factory.setAllowedPartiesIterable(Lists.newArrayList("moo"));
    factory.setCdsUrl(address);
    factory.setDelegationLifetime(60 * 60 * 3);
    factory.setIssuedCredentialLifetime(60 * 60 * 3 + 1);
    factory.setKeySize(6);
    factory.setDelegationPathLength(4);
    factory.setIssuedCredentialPathLength(5);
    
    final Capture<DelegationPolicy> dPolicyCapture = new Capture<DelegationPolicy>();
    client.delegateCredential(EasyMock.eq(dLifetime), EasyMock.eq(4), EasyMock.capture(dPolicyCapture), EasyMock.eq(iLifetime), EasyMock.eq(5), EasyMock.eq(6));
    if(exception) {
      EasyMock.expectLastCall().andThrow(new RemoteException());
    } else {
      EasyMock.expectLastCall().andReturn(dcRef);
    }
    EasyMock.replay(clientFactory, client);

    RuntimeException rException = null;
    try {
      final DelegatedCredentialReference returnedRef = factory.createDelegatedCredential(credential);
      assert dcRef == returnedRef;
    } catch(RuntimeException e) {
      rException = e;
    }
    
    if(exception) {
      assert rException != null;
    } else {
      assert rException == null;
    }    
    EasyMock.verify(clientFactory, client);
    final IdentityDelegationPolicy dPolicy = (IdentityDelegationPolicy) dPolicyCapture.getValue();
    assert Arrays.equals(dPolicy.getAllowedParties().getGridIdentity(), new String[] {"moo"});
    
  }
}
