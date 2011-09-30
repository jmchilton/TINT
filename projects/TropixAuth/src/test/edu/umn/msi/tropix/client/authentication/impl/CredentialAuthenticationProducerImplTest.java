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

import org.easymock.EasyMock;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.client.authentication.CredentialAuthentication;
import edu.umn.msi.tropix.client.authentication.Roles;
import edu.umn.msi.tropix.common.test.TestNGDataProviders;
import edu.umn.msi.tropix.common.time.TimeProvider;
import edu.umn.msi.tropix.common.time.TimeProviders;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.persistence.service.UserService;

public class CredentialAuthenticationProducerImplTest {

  private UserService getUserServiceMock(final String expectedId, final boolean isAdmin) {
    final UserService userService = EasyMock.createMock(UserService.class);
    EasyMock.expect(userService.isAdmin(expectedId)).andStubReturn(isAdmin);
    EasyMock.replay(userService);
    return userService;
  }
  
  @Test(groups = "unit")
  public void testTimeOut() {
    final UserService userService = getUserServiceMock("gridId", false);
    // Time provider will return 0 as initial time, 900 on first check (under logintimeout), and 
    // then 1200L on second check, which is over login timeout
    final TimeProvider fixedTimeProvider = TimeProviders.getFixedTimeProvider(Lists.newArrayList(0L, 900L, 1200L));
    final CredentialAuthenticationProducerImpl producer = new CredentialAuthenticationProducerImpl(userService);
    producer.setLoginTimeout(1); // 1 second == 1000 milliseconds
    producer.setTimeProvider(fixedTimeProvider);

    final CredentialAuthentication authentication = producer.get(Credentials.getMock("gridId"), new UsernamePasswordAuthenticationToken("moo", "cow"));
    assert authentication.isAuthenticated();
    assert !authentication.isAuthenticated() : "Authenticated despite timeout.";
  }
  
  @Test(groups = "unit")
  public void testGuestProduction() {
    final CredentialAuthenticationProducerImpl producer = new CredentialAuthenticationProducerImpl(null);
    producer.setLoginTimeout(1000);

    final CredentialAuthentication authentication = producer.getGuestCredential(Credentials.getMock("guesttest"), new UsernamePasswordAuthenticationToken("moo", "cow"));
    assert authentication.getAuthorities().contains(Roles.ROLE_GUEST.getAuthority());
    assert !authentication.getAuthorities().contains(Roles.ROLE_USER.getAuthority());
    assert !authentication.getAuthorities().contains(Roles.ROLE_ADMIN.getAuthority());    
  }
  
  @Test(groups = "unit", dataProvider = "bool1", dataProviderClass = TestNGDataProviders.class)
  public void testValidProduction(final boolean isAdmin) {
    final UserService userService = getUserServiceMock("gridId", isAdmin);
    final CredentialAuthenticationProducerImpl producer = new CredentialAuthenticationProducerImpl(userService);
    producer.setLoginTimeout(1000);
    
    final CredentialAuthentication authentication = producer.get(Credentials.getMock("gridId"), new UsernamePasswordAuthenticationToken("moo", "cow"));
    assert authentication.getAuthorities().contains(Roles.ROLE_GUEST.getAuthority());
    assert authentication.getAuthorities().contains(Roles.ROLE_USER.getAuthority());
    assert isAdmin == authentication.getAuthorities().contains(Roles.ROLE_ADMIN.getAuthority());
    
    assert authentication.isAuthenticated();
  }
  
}
