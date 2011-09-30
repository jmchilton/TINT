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

package edu.umn.msi.tropix.transfer.http.server;

import java.io.ByteArrayOutputStream;

import javax.servlet.ServletException;

import org.easymock.classextension.EasyMock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.EasyMockUtils;

public class HttpTransferControllerImplTest {

  @Test(groups = "unit")
  public void get() throws ServletException {
    final HttpTransferControllerImpl controller = new HttpTransferControllerImpl();
    final FileKeyResolver fileKeyResolver = EasyMock.createMock(FileKeyResolver.class);
    controller.setFileKeyResolver(fileKeyResolver);

    final MockHttpServletResponse response = new MockHttpServletResponse();
    final MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("GET");
    request.setParameter("key", "1234567");

    fileKeyResolver.handleDownload("1234567", response.getOutputStream());
    EasyMock.replay(fileKeyResolver);
    controller.handleRequest(request, response);
    EasyMockUtils.verifyAndReset(fileKeyResolver);
  }

  @Test(groups = "unit", expectedExceptions = ServletException.class)
  public void getException() throws ServletException {
    final HttpTransferControllerImpl controller = new HttpTransferControllerImpl();
    final FileKeyResolver fileKeyResolver = EasyMock.createMock(FileKeyResolver.class);
    controller.setFileKeyResolver(fileKeyResolver);

    final MockHttpServletResponse response = new MockHttpServletResponse();
    final MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("GET");
    request.setParameter("key", "1234567");

    fileKeyResolver.handleDownload("1234567", response.getOutputStream());
    EasyMock.expectLastCall().andThrow(new RuntimeException());
    EasyMock.replay(fileKeyResolver);
    controller.handleRequest(request, response);
    EasyMockUtils.verifyAndReset(fileKeyResolver);
  }

  @Test(groups = "unit")
  public void put() throws ServletException {
    final HttpTransferControllerImpl controller = new HttpTransferControllerImpl();
    final FileKeyResolver fileKeyResolver = EasyMock.createMock(FileKeyResolver.class);
    controller.setFileKeyResolver(fileKeyResolver);

    final MockHttpServletResponse response = new MockHttpServletResponse();
    final MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
    request.setMethod("POST");
    request.setParameter("key", "1234567");
    request.setContent("Moo Cow".getBytes());

    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    fileKeyResolver.handleUpload(EasyMock.eq("1234567"), EasyMockUtils.copy(outputStream));
    EasyMock.replay(fileKeyResolver);
    controller.handleRequest(request, response);
    EasyMockUtils.verifyAndReset(fileKeyResolver);
    assert "Moo Cow".equals(new String(outputStream.toByteArray()));
  }

  @Test(groups = "unit", expectedExceptions = ServletException.class)
  public void putException() throws ServletException {
    final HttpTransferControllerImpl controller = new HttpTransferControllerImpl();
    final FileKeyResolver fileKeyResolver = EasyMock.createMock(FileKeyResolver.class);
    controller.setFileKeyResolver(fileKeyResolver);

    final MockHttpServletResponse response = new MockHttpServletResponse();
    final MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("POST");
    request.setParameter("key", "1234567");

    fileKeyResolver.handleUpload("1234567", request.getInputStream());
    EasyMock.expectLastCall().andThrow(new RuntimeException());
    EasyMock.replay(fileKeyResolver);
    controller.handleRequest(request, response);
    EasyMockUtils.verifyAndReset(fileKeyResolver);
  }

}
