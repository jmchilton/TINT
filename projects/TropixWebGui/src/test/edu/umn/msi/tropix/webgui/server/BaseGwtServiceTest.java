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

package edu.umn.msi.tropix.webgui.server;

import java.util.UUID;

import org.easymock.EasyMock;

import edu.umn.msi.tropix.common.reflect.ReflectionHelper;
import edu.umn.msi.tropix.common.reflect.ReflectionHelpers;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.webgui.server.security.UserSession;
import edu.umn.msi.tropix.webgui.server.security.UserSessionBean;

public class BaseGwtServiceTest {
  private static final ReflectionHelper REFLECTION_HELPER = ReflectionHelpers.getInstance(); 
  private String gridId;
  private UserSessionBean userSession;
  private MockBeanSanitizerImpl beanSanitizer;
  
  public void init() {
    gridId = UUID.randomUUID().toString();
    userSession = new UserSessionBean();
    userSession.setGridId(gridId);
    userSession.setProxy(Credentials.getMock(gridId));
    beanSanitizer = new MockBeanSanitizerImpl();
  }
  
  protected String getUserId() {
    return gridId;
  }
  
  protected UserSession getUserSession() {
    return userSession;
  }
  
  protected MockBeanSanitizerImpl getSanitizer() {
    return beanSanitizer;
  }
  
  protected String expectUserId() {
    return EasyMock.eq(gridId);
  }
  
  protected <T extends TropixObject> T createTropixObject(final Class<T> clazz) {
    final T tropixObject = REFLECTION_HELPER.newInstance(clazz);
    tropixObject.setId(UUID.randomUUID().toString());
    return tropixObject;
  }
  
  
}
