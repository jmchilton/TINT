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

package edu.umn.msi.tropix.webgui.server.messages;

import java.lang.reflect.Method;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import edu.umn.msi.tropix.common.reflect.ReflectionHelper;
import edu.umn.msi.tropix.common.reflect.ReflectionHelpers;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.webgui.server.security.UserSessionBean;
import edu.umn.msi.tropix.webgui.services.message.InfoMessage;
import edu.umn.msi.tropix.webgui.services.message.Message;

public class MessageManagerTest {
  private static final ReflectionHelper REFLECTION_HELPER = ReflectionHelpers.getInstance();
  
  @Test(groups = "unit")
  public void testMessageManager() throws Exception {
    final UserSessionBean userSession = new UserSessionBean();
    final HttpSession session = EasyMock.createMock(HttpSession.class);
    final String sessionId = UUID.randomUUID().toString();
    final Capture<Object> finalizerCapture = EasyMockUtils.newCapture();
    EasyMock.expect(session.getId()).andStubReturn(sessionId);
    session.setAttribute(EasyMock.isA(String.class), EasyMock.capture(finalizerCapture));
    // Second time toward end of testing...
    session.setAttribute(EasyMock.isA(String.class), EasyMock.isA(Object.class));
    
    
    final Supplier<HttpSession> httpSessionSupplier = Suppliers.ofInstance(session);
    final MessageManager<Message> messageManager = new MessageManager<Message>(userSession, httpSessionSupplier);
    
    userSession.setGridId("moo");
    final Message message = new InfoMessage("cow", "body");    
    EasyMock.replay(session);

    messageManager.push(message);
    // This should be initially empty because push happened when there was
    // no session bound to the id.
    assert messageManager.drainQueue().isEmpty();

    // Now session is bound to the id, repush the message.
    
    messageManager.push(message);
    // Make sure the user has that message
    assert messageManager.drainQueue().contains(message);
    
    // Make sure the message was removed.
    assert messageManager.drainQueue().isEmpty();

    final Method method = REFLECTION_HELPER.getMethod(finalizerCapture.getValue().getClass(), "performFinalization");
    method.invoke(finalizerCapture.getValue());
    
    // This should undo the session to user binding. So
    // a pushed message should not be returned from drainQueue.
    messageManager.push(message);
    assert messageManager.drainQueue().isEmpty();

    userSession.setGridId(null);
    // Ensure there is no problem if user id is not set yet.
    assert messageManager.drainQueue().isEmpty();

  }
  
  
}
