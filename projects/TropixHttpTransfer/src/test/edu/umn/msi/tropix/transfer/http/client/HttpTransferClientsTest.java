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

package edu.umn.msi.tropix.transfer.http.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContexts;

public class HttpTransferClientsTest {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private static final int PORT = 10345;

  @Test(groups = "unit")
  public void constructor() {
    new HttpTransferClients();
  }

  private static class TestHandlerImpl extends AbstractHandler {
    private final List<byte[]> inputs = Lists.newArrayList();

    public void handle(final String target, final HttpServletRequest request, final HttpServletResponse response, final int action) throws IOException, ServletException {
      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      IO_UTILS.copy(request.getInputStream(), outputStream);
      inputs.add(outputStream.toByteArray());
      InputContexts.forString("Output").get(response.getOutputStream());
      response.getOutputStream().close();
    }

  }

  private static class ServletExceptionHandlerImpl extends AbstractHandler {

    public void handle(final String target, final HttpServletRequest request, final HttpServletResponse response, final int action) throws ServletException {
      throw new ServletException();
    }

  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void getServerError() throws Exception {
    final ServletExceptionHandlerImpl handler = new ServletExceptionHandlerImpl();
    final Server wrappedServer = new Server(PORT);
    wrappedServer.setHandler(handler);
    wrappedServer.start();
    try {
      final HttpTransferClient client = HttpTransferClients.getInstance();
      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      client.getInputContext("http://localhost:" + PORT).get(outputStream);
    } finally {
      wrappedServer.stop();
      wrappedServer.join();
    }
  }

  @Test(groups = "unit")
  public void get() throws Exception {
    final TestHandlerImpl handler = new TestHandlerImpl();
    final Server wrappedServer = new Server(PORT);
    wrappedServer.setHandler(handler);
    wrappedServer.start();

    final HttpTransferClient client = HttpTransferClients.getInstance();
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    client.getInputContext("http://localhost:" + PORT).get(outputStream);
    assert new String(outputStream.toByteArray()).equals("Output");

    wrappedServer.stop();
    wrappedServer.join();
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void putServerError() throws Exception {
    final ServletExceptionHandlerImpl handler = new ServletExceptionHandlerImpl();
    final Server wrappedServer = new Server(PORT);
    wrappedServer.setHandler(handler);
    wrappedServer.start();
    try {
      final HttpTransferClient client = HttpTransferClients.getInstance();
      client.getOutputContext("http://localhost:" + PORT).put("Input".getBytes());
    } finally {
      wrappedServer.stop();
      wrappedServer.join();
    }

  }

  @Test(groups = "unit")
  public void put() throws Exception {
    final TestHandlerImpl handler = new TestHandlerImpl();
    final Server wrappedServer = new Server(PORT);
    wrappedServer.setHandler(handler);
    wrappedServer.start();

    final HttpTransferClient client = HttpTransferClients.getInstance();
    client.getOutputContext("http://localhost:" + PORT).put("Input".getBytes());
    assert new String(handler.inputs.get(0)).equals("Input");

    wrappedServer.stop();
    wrappedServer.join();
  }

}
