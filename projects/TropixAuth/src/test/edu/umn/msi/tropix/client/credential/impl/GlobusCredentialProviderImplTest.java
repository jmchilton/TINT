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

import java.rmi.RemoteException;

import edu.umn.msi.tropix.client.authentication.config.CaGrid;
import edu.umn.msi.tropix.client.credential.CredentialCreationOptions;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.grid.credentials.Credential;
import gov.nih.nci.cagrid.authentication.bean.BasicAuthenticationCredential;
import gov.nih.nci.cagrid.authentication.client.AuthenticationClient;
import gov.nih.nci.cagrid.dorian.client.IFSUserClient;
import gov.nih.nci.cagrid.dorian.ifs.bean.ProxyLifetime;
import gov.nih.nci.cagrid.dorian.stubs.types.UserPolicyFault;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.globus.gsi.GlobusCredential;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class GlobusCredentialProviderImplTest {
  private GlobusCredential globusCredential = EasyMock.createMock(GlobusCredential.class);
  private GlobusCredentialProviderImpl provider;
  private AuthenticationClientFactory authenticationClientFactory;
  private IFSUserClientFactory ifsUserClientFactory;
  private IFSUserClient ifsUserClient;
  private AuthenticationClient authenticationClient;
  private Capture<gov.nih.nci.cagrid.authentication.bean.Credential> credentialCapture;
  private SAMLAssertion assertion;
  private CredentialCreationOptions options;
  
  @BeforeMethod(groups = "unit")
  public void init() {
    provider = new GlobusCredentialProviderImpl();
    
    authenticationClientFactory = EasyMock.createMock(AuthenticationClientFactory.class);
    ifsUserClientFactory = EasyMock.createMock(IFSUserClientFactory.class);
    
    ifsUserClient = EasyMock.createMock(IFSUserClient.class);
    authenticationClient = EasyMock.createMock(AuthenticationClient.class);

    options = new CredentialCreationOptions();
    final CaGrid caGridOptions = new CaGrid();
    caGridOptions.setDelegationPathLength(10);
    caGridOptions.setLifetime(100L);
    caGridOptions.setAuthenticationServiceUrl("idp");
    caGridOptions.setDorianServiceUrl("ifs");
    options.setAuthenticationSource(caGridOptions);
    
    assertion = new SAMLAssertion();
    credentialCapture = EasyMockUtils.newCapture();
    
    provider.setAuthenticationClientFactory(authenticationClientFactory);
    provider.setIfsUserClientFactory(ifsUserClientFactory);
  }

  private void replay() {
    EasyMock.replay(authenticationClientFactory, ifsUserClientFactory, ifsUserClient, authenticationClient);
  }

  private void reset() {
    EasyMock.verify(authenticationClientFactory, ifsUserClientFactory, ifsUserClient, authenticationClient);
    EasyMock.reset(authenticationClientFactory, ifsUserClientFactory, ifsUserClient, authenticationClient);
  }

  @Test(groups = "unit")
  public void inputOptions() throws Exception {
    EasyMock.expect(ifsUserClientFactory.getClient("ifs")).andReturn(ifsUserClient);
    EasyMock.expect(authenticationClientFactory.getClient(EasyMock.eq("idp"), EasyMock.capture(credentialCapture))).andReturn(authenticationClient);
    EasyMock.expect(authenticationClient.authenticate()).andReturn(assertion);
        
    EasyMock.expect(ifsUserClient.createProxy(EasyMock.same(assertion), EasyMock.eq(new ProxyLifetime(0, 1, 40)), EasyMock.eq(10))).andReturn(globusCredential);
    
    replay();
    Credential credential = provider.getGlobusCredential("john", "pass", options);
    reset();
    assert credential.getGlobusCredential() == globusCredential;
    checkNameAndPassword("john", "pass");
  }
  
  
  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void authenticationProblems() throws Exception {
    
    EasyMock.expect(authenticationClientFactory.getClient(EasyMock.eq("idp"), EasyMock.capture(credentialCapture))).andReturn(authenticationClient);
    EasyMock.expect(authenticationClient.authenticate()).andThrow(new RemoteException());
    
    replay();
    provider.getGlobusCredential("john", "pass", options);
  }


  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void dorianProblems() throws Exception {
    EasyMock.expect(ifsUserClientFactory.getClient("ifs")).andReturn(ifsUserClient);
    EasyMock.expect(authenticationClientFactory.getClient(EasyMock.eq("idp"), EasyMock.capture(credentialCapture))).andReturn(authenticationClient);
    EasyMock.expect(authenticationClient.authenticate()).andReturn(assertion);
        
    EasyMock.expect(ifsUserClient.createProxy(EasyMock.same(assertion), EasyMock.eq(new ProxyLifetime(0, 1, 40)), EasyMock.eq(10))).andThrow(new UserPolicyFault());
    
    replay();
    provider.getGlobusCredential("john", "pass", options);
  }

  
  @Test(groups = "unit")
  public void defaults() throws Exception {
    provider.setDefaultDelegationPathLength(12);
    provider.setDefaultIdpUrl("didp");
    provider.setDefaultProxyLifetime(120);
    
    EasyMock.expect(ifsUserClientFactory.getClient("dorian")).andReturn(ifsUserClient);
    EasyMock.expect(authenticationClientFactory.getClient(EasyMock.eq("didp"), EasyMock.capture(credentialCapture))).andReturn(authenticationClient);
    
    EasyMock.expect(authenticationClient.authenticate()).andReturn(assertion);
        
    EasyMock.expect(ifsUserClient.createProxy(EasyMock.same(assertion), EasyMock.eq(new ProxyLifetime(0, 2, 0)), EasyMock.eq(12))).andReturn(globusCredential);
    
    replay();

    provider.setDefaultDorianUrl("dorian");

    Credential credential = provider.getGlobusCredential("john", "pass", null);
    reset();
    assert credential.getGlobusCredential() == globusCredential;
    checkNameAndPassword("john", "pass");
  }
  
  
  
  private void checkNameAndPassword(final String name, final String password) {
    final BasicAuthenticationCredential bac = credentialCapture.getValue().getBasicAuthenticationCredential();
    assert bac.getUserId().equals(name);
    assert bac.getPassword().equals(password);
  }

}
