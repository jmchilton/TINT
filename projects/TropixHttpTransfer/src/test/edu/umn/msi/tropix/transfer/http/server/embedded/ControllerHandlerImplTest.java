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

package edu.umn.msi.tropix.transfer.http.server.embedded;

import java.io.IOException;

import javax.servlet.ServletException;

import org.easymock.classextension.EasyMock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.mvc.Controller;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.EasyMockUtils;

public class ControllerHandlerImplTest {

  @Test(groups = "unit")
  public void delegation() throws Exception {
    final Controller controller = EasyMock.createMock(Controller.class);
    final ControllerHandlerImpl handler = new ControllerHandlerImpl(controller);
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();

    EasyMock.expect(controller.handleRequest(request, response)).andReturn(null);
    EasyMock.replay(controller);
    handler.handle("123", request, response, 5);
    EasyMockUtils.verifyAndReset(controller);
  }

  @Test(groups = "unit")
  public void errorTranslation() throws Exception {
    final Controller controller = EasyMock.createMock(Controller.class);
    final ControllerHandlerImpl handler = new ControllerHandlerImpl(controller);
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();

    final Exception e = new Exception();
    EasyMock.expect(controller.handleRequest(request, response)).andThrow(e);
    EasyMock.replay(controller);
    ServletException se = null;
    try {
      handler.handle("123", request, response, 5);
    } catch(final ServletException ise) {
      se = ise;
    }
    assert se != null && se.getRootCause() == e;
    EasyMockUtils.verifyAndReset(controller);

    final IOException ioe = new IOException();
    EasyMock.expect(controller.handleRequest(request, response)).andThrow(ioe);
    EasyMock.replay(controller);
    IOException caughtIoe = null;
    try {
      handler.handle("123", request, response, 5);
    } catch(final IOException iioe) {
      caughtIoe = iioe;
    }
    assert ioe == caughtIoe;
    EasyMockUtils.verifyAndReset(controller);

  }

}
