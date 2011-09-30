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

package edu.umn.msi.tropix.webgui.server.session;

import java.util.UUID;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.TestNGDataProviders;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.service.UserService;
import edu.umn.msi.tropix.webgui.services.session.SessionInfo;

public class UserSessionInfoClosureImplTest extends BaseSessionInfoClosureImplTest {

  @Test(groups = "unit", dataProvider = "bool1", dataProviderClass = TestNGDataProviders.class)
  public void testGetSessionInfo(final boolean isAdmin) {
    init();

    final UserService userService = EasyMock.createMock(UserService.class);

    final UserSessionInfoClosureImpl sessionInfoClosure = new UserSessionInfoClosureImpl();
    sessionInfoClosure.setBeanSanitizer(getSanitizer());
    sessionInfoClosure.setUserService(userService);
    sessionInfoClosure.setUserSession(getUserSession());

    final User user = new User();
    user.setId(UUID.randomUUID().toString());
    user.setCagridId(getUserId());

    EasyMock.expect(userService.createOrGetUser(getUserId())).andStubReturn(user);
    EasyMock.expect(userService.isAdmin(getUserId())).andStubReturn(isAdmin);

    final Folder homeFolder = createTropixObject(Folder.class);
    EasyMock.expect(userService.getHomeFolder(getUserId())).andStubReturn(homeFolder);

    // Setup a mock request and session
    EasyMock.replay(userService);
    final SessionInfo sessionInfo = get(sessionInfoClosure);

    assert sessionInfo.isAdmin() == isAdmin;

    assert sessionInfo.getUser().getCagridId().equals(getUserId());
    assert sessionInfo.getUser().getHomeFolder().equals(homeFolder);

    assert getSanitizer().wasSanitized(user);
    assert getSanitizer().wasSanitized(homeFolder);

  }

}
