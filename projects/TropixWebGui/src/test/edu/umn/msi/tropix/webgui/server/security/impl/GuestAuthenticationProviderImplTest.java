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

package edu.umn.msi.tropix.webgui.server.security.impl;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.springframework.security.authentication.DisabledException;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.client.authentication.CredentialAuthentication;
import edu.umn.msi.tropix.client.authentication.GuestAuthentication;
import edu.umn.msi.tropix.client.authentication.GuestCredentialAuthenticationProducer;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.webgui.client.utils.Lists;
import edu.umn.msi.tropix.webgui.server.security.SecurityConstants;
import edu.umn.msi.tropix.webgui.services.session.Module;

public class GuestAuthenticationProviderImplTest {

  @Test(groups = "unit", expectedExceptions = DisabledException.class)
  public void testGuestAuthenticationWhenNotEnabled() {
    GuestAuthenticationProviderImpl provider = new GuestAuthenticationProviderImpl(null, 1000L);
    provider.authenticate(new GuestAuthentication());
  }
  
  @Test(groups = "unit")
  public void testGuestAuthenticationWhenEnabled() {
    final GuestCredentialAuthenticationProducer guestCredentialProducer = EasyMock.createMock(GuestCredentialAuthenticationProducer.class);
    final GuestAuthenticationProviderImpl provider = new GuestAuthenticationProviderImpl(guestCredentialProducer, 1000);
    provider.setModules(Lists.newArrayList(Module.GUEST.toString()));
    final GuestAuthentication unauthenticated = new GuestAuthentication();
    final CredentialAuthentication authenticated = EasyMock.createMock(CredentialAuthentication.class);
    final Capture<Credential> guestCredential = EasyMockUtils.newCapture();
    EasyMock.expect(guestCredentialProducer.getGuestCredential(EasyMock.capture(guestCredential), EasyMock.same(unauthenticated))).andReturn(authenticated);
    EasyMock.replay(guestCredentialProducer);
    assert authenticated == provider.authenticate(unauthenticated);
    assert guestCredential.getValue().getIdentity().equals(SecurityConstants.GUEST_IDENTITY);
    final long timeLeft = guestCredential.getValue().getTimeLeft();
    // original time left was 1000, so new should be between 900 and 1001
    assert 900 < timeLeft && timeLeft < 1001;    
  }
  
}
