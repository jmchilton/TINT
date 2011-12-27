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

package edu.umn.msi.tropix.client.authentication.impl;

import java.util.Collection;
import java.util.List;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IExpectationSetters;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.client.authentication.AuthenticationSourceManager;
import edu.umn.msi.tropix.client.authentication.AuthenticationToken;
import edu.umn.msi.tropix.client.authentication.CredentialAuthentication;
import edu.umn.msi.tropix.client.authentication.CredentialAuthenticationProducer;
import edu.umn.msi.tropix.client.credential.CredentialCreationOptions;
import edu.umn.msi.tropix.client.credential.CredentialProvider;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.MockObjectCollection;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;


public class SpringAuthenticationProviderImplTest {
  private CredentialProvider globusCredentialProvider;
  private AuthenticationSourceManager authenticationSourceManager;
  private CredentialAuthenticationProducer credentialAuthenticationProducer;
  private List<CredentialCreationOptions> optionsList;
  
  private MockObjectCollection mocks;
  
  private SpringAuthenticationProviderImpl authProvider;
  
  private String username = "testu";
  private String password = "testp";

  private Credential credential;

  private Capture<Authentication> authenticationCapture = EasyMockUtils.newCapture(); 
  private CredentialAuthentication resultAuth = EasyMock.createMock(CredentialAuthentication.class);
  
  @BeforeMethod(groups = "unit")
  public void init() {
    authenticationSourceManager = new MapAuthenticationSourceManagerImpl();
    globusCredentialProvider = EasyMock.createMock(CredentialProvider.class);
    credentialAuthenticationProducer = EasyMock.createMock(CredentialAuthenticationProducer.class);

    optionsList = Lists.newArrayList(new CredentialCreationOptions(), new CredentialCreationOptions(), new CredentialCreationOptions());
    
    authProvider = new SpringAuthenticationProviderImpl(authenticationSourceManager, globusCredentialProvider, credentialAuthenticationProducer);
    
    credential = Credentials.getMock();
    
    
    mocks = MockObjectCollection.fromObjects(globusCredentialProvider, credentialAuthenticationProducer);
  }

  private IExpectationSetters<Credential> expectCallGetCredential(final int i) {
    return EasyMock.expect(globusCredentialProvider.getGlobusCredential(EasyMock.eq(username),
                                                                        EasyMock.eq(password),
                                                                        EasyMock.same(optionsList.get(i))));    
  }

  private void expectCredentialAuthentication() {
    EasyMock.expect(credentialAuthenticationProducer.get(EasyMock.eq(credential), EasyMock.capture(authenticationCapture))).andReturn(resultAuth);
  }

  private void verifyAuthenticationCapture(final String key) {
    final AuthenticationToken captured = (AuthenticationToken) authenticationCapture.getValue();
    assert captured.getUsername().equals(username);
    assert captured.getPrincipal().equals(username);
    assert captured.getPassword().equals(password);
    assert captured.getCredentials().equals(password);
    assert captured.getAuthenticationSource().equals(key);
    
  }

  @Test(groups = "unit")
  public void testAuthenticationToken() {
    final AuthenticationToken token = new AuthenticationToken(username, password, "2");
    expectCallGetCredential(2).andReturn(credential);
    expectCredentialAuthentication();
    mocks.replay();
    assert resultAuth == authProvider.authenticate(token);
    verifyAuthenticationCapture("2");        
    mocks.verifyAndReset();
  }

  @Test(groups = "unit", expectedExceptions = AuthenticationException.class)
  public void testUsernamePasswordTokenNoneValid() {
    final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);

    expectCallGetCredential(0).andThrow(new RuntimeException());
    expectCallGetCredential(1).andReturn(null); // Should be an exception
    expectCallGetCredential(2).andThrow(new RuntimeException());

    mocks.replay();
    try {
      authProvider.authenticate(token);
    } finally {
      mocks.verifyAndReset();
    }
  }
  
  @Test(groups = "unit")
  public void testUsernamePasswordTokenValidSecond() {
    final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);

    expectCallGetCredential(0).andThrow(new RuntimeException());
    expectCallGetCredential(1).andReturn(credential);
    expectCredentialAuthentication();
    mocks.replay();
    
    assert resultAuth == authProvider.authenticate(token);
    verifyAuthenticationCapture("1");    
    
    mocks.verifyAndReset();
  }

  class MapAuthenticationSourceManagerImpl implements AuthenticationSourceManager {

    public CredentialCreationOptions getAuthenticationOptions(final String key) {
      return optionsList.get(Integer.parseInt(key));
    }

    public Collection<String> getAuthenticationSourceKeys() {
      return Lists.newArrayList("0", "1", "2");
    }
    
  }

  @Test(groups = "unit")
  public void testSupports() {
    assert authProvider.supports(UsernamePasswordAuthenticationToken.class);
    assert authProvider.supports(AuthenticationToken.class);
    assert !authProvider.supports(AnonymousAuthenticationToken.class);
  }
}
