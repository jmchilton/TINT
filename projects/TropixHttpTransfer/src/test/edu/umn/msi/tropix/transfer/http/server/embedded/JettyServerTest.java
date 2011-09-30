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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.mortbay.jetty.handler.AbstractHandler;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContexts;

public class JettyServerTest {


  //  private static final MultiThreadedHttpConnectionManager HTTP_CONNECTION_MANAGER = new MultiThreadedHttpConnectionManager();
  //  private static final HttpClient HTTP_CLIENT = new HttpClient(HTTP_CONNECTION_MANAGER);
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();

  private class TestHandlerImpl extends AbstractHandler {
    private final List<byte[]> inputs = Lists.newArrayList();

    public void handle(final String arg0, final HttpServletRequest request, final HttpServletResponse response, final int arg3) throws IOException, ServletException {
      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      IO_UTILS.copy(request.getInputStream(), outputStream);
      inputs.add(outputStream.toByteArray());
      InputContexts.forString("Output").get(response.getOutputStream());
      response.getOutputStream().close();
    }

  }

  private void init(final JettyServer jettyServer) throws Exception {
    jettyServer.init();
    while(!jettyServer.isRunning()) {
      Thread.sleep(5);
    }
    return;
  }

  @Test(groups = "unit")
  public void testFindsNewPort() throws Exception {
    final int port = 61948;
    final JettyServer jettyServer = new JettyServer(port);
    final TestHandlerImpl handler = new TestHandlerImpl();
    jettyServer.setHandler(handler);
    init(jettyServer);
    try {
      assert jettyServer.getPort() == 61948;

      final JettyServer jettyServer2 = new JettyServer(port);
      jettyServer2.setHandler(handler);
      init(jettyServer2);
      try {
        final int port2 = jettyServer2.getPort();
        assert port2 != port;

        // Test that Jetty is listening on port 2.
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final HttpClient client = new DefaultHttpClient();
        final HttpGet httpget = new HttpGet("http://127.0.0.1:" + port2);
        final HttpResponse response = client.execute(httpget);    
        try {
          final int statusCode = response.getStatusLine().getStatusCode();
          assert statusCode == 200 : statusCode;
          
          IO_UTILS.copy(response.getEntity().getContent(), stream);
        } finally {
          httpget.abort();
        }
        assert new String(stream.toByteArray()).equals("Output");
      } finally {
        jettyServer2.destroy();
      }
    } finally {
      jettyServer.destroy();
    }
  }

  @Test(groups = "unit")
  public void init() throws Exception {
    final int port = 61948;
    final JettyServer jettyServer = new JettyServer(port);
    final TestHandlerImpl handler = new TestHandlerImpl();
    jettyServer.setHandler(handler);
    init(jettyServer);
    assert jettyServer.getPort() == 61948 : jettyServer.getPort(); 

    final ByteArrayOutputStream stream = new ByteArrayOutputStream();
    final HttpClient client = new DefaultHttpClient();
    final HttpGet httpget = new HttpGet("http://127.0.0.1:" + port);
    final HttpResponse response = client.execute(httpget);    
    try {
      assert response.getStatusLine().getStatusCode() == 200;
      IO_UTILS.copy(response.getEntity().getContent(), stream);
    } finally {
      httpget.abort();
    }
    assert new String(stream.toByteArray()).equals("Output");

    final HttpPost httppost = new HttpPost("http://127.0.0.1:" + port);
    final ByteArrayEntity entity = new ByteArrayEntity("Input".getBytes());
    httppost.setEntity(entity);
    final HttpResponse postResponse = client.execute(httppost);
    try {
      assert postResponse.getStatusLine().getStatusCode() == 200;
    } finally {
      httppost.abort();
    }

    assert new String(handler.inputs.get(1)).equals("Input");
    jettyServer.destroy();
  }
}
