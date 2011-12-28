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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.jmchilton.concurrent.CountDownLatch;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;
import org.springframework.util.DigestUtils;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.transfer.http.client.HttpTransferClients.HttpTransferOutputContextImpl;

public class HttpTransferClientsTest {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private static final int PORT = 10345;
  private Server wrappedServer;
  private Handler handler;
  private TransferSummaryCallbackImpl callback;

  @BeforeMethod(groups = "unit")
  public void init() {
    this.wrappedServer = null;
    this.handler = null;
    callback = new TransferSummaryCallbackImpl();
  }

  @AfterMethod(groups = "unit")
  public void cleanUp() throws Exception {
    if(wrappedServer != null) {
      wrappedServer.stop();
      wrappedServer.join();
    }
  }
  

  @Test(groups = "unit")
  public void constructor() {
    new HttpTransferClients();
  }
  
  @Test(groups = "unit")
  public void testPostTranfserSummmary() throws Exception {
    handler = new TestHandlerImpl();
    startServer(handler);

    final OutputContext outputContext = HttpTransferClients.getInstrumentableInstance().getOutputContext(getAddress(), callback);
    outputContext.put(new ByteArrayInputStream("Input".getBytes()));
    callback.latch.await();
    assert callback.transferSummary.getBytesTransferred() == "Input".getBytes().length;
    Assert.assertEquals(callback.transferSummary.getMd5Sum(), DigestUtils.md5Digest("Input".getBytes()));
  }

  @Test(groups = "unit")
  public void testGetTransferSummary() throws Exception {
    handler = new TestHandlerImpl();
    startServer(handler);
    final InputContext inputContext = HttpTransferClients.getInstrumentableInstance().getInputContext(getAddress(), callback);
    inputContext.get(new ByteArrayOutputStream());
    callback.latch.await();
    assert callback.transferSummary.getBytesTransferred() == "Output".getBytes().length;
    Assert.assertEquals(callback.transferSummary.getMd5Sum(), DigestUtils.md5Digest("Output".getBytes()));
  }
  
  @Test(groups = "unit", dataProvider = "getClients",  expectedExceptions = RuntimeException.class)
  public void getServerError(final HttpTransferClient client) throws Exception {
    startServer(new ServletExceptionHandlerImpl());
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    client.getInputContext(getAddress()).get(outputStream);
  }

  @Test(groups = "unit", dataProvider = "getClients")
  public void get(final HttpTransferClient client) throws Exception {
    handler = new TestHandlerImpl();
    startServer(handler);

    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    client.getInputContext(getAddress()).get(outputStream);
    assert new String(outputStream.toByteArray()).equals("Output");
  }

  @Test(groups = "unit", dataProvider = "getClients", expectedExceptions = RuntimeException.class)
  public void putServerError(final HttpTransferClient client) throws Exception {
    startServer(new ServletExceptionHandlerImpl());
    client.getOutputContext(getAddress()).put("Input".getBytes());
  }

  /**
   * This contrived test verifies the behavior that if the client 
   * fails while uploading the server side output stream throws an 
   * exception. 
   * 
   * @throws Exception
   */
  @Test(groups = "unit")
  public void putFailureDoesntCreateFile() throws Exception {
    final ExceptionHandlerImpl eHandler = new ExceptionHandlerImpl();
    startServer(eHandler);
    final InputStream failStream = new FilterInputStream(new ByteArrayInputStream("Input".getBytes())) {
      public int available() throws IOException {
        int available = super.available();
        if(available == 0) {
          available = 1;
        }
        return available;
      }
      
      public int read()  throws IOException {
        int read = super.read();
        if(read == -1) {
          return end();
        }
        return read;
      }
      
      private int end() throws IOException {
        throw new IOException("Connection distrupted");
      }
      
      public int read(final byte[] b) throws IOException {
        int read = super.read();
        if(read == -1) {
          return end();
        }
        b[0] = (byte) read;
        return 1;
      }
      public int read(final byte[] b, final int off, final int len)  throws IOException {
        int read = super.read();
        if(read == -1) {
          return end();
        }
        b[0] = (byte) read;
        return 1;
      }
    };
    final HttpTransferOutputContextImpl oContext = getOutputContext(getAddress());
    try {
      oContext.put(failStream, 100);
    } catch(Exception e) {
      e.printStackTrace();
    }
    Thread.sleep(100);
    Assert.assertNotNull(eHandler.e);
  }

  @Test(groups = "unit", dataProvider = "getClients")
  public void put(final HttpTransferClient client) throws Exception {
    final TestHandlerImpl testHandler = new TestHandlerImpl();
    startServer(testHandler);

    client.getOutputContext(getAddress()).put("Input".getBytes());
    assert new String(testHandler.inputs.get(0)).equals("Input");
  }

  @DataProvider
  public Object[][] getClients() {
    return new Object[][] {new Object[] {HttpTransferClients.getInstance()}, new Object[] {HttpTransferClients.getInstrumentableInstance() } };
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
  
  private static class ExceptionHandlerImpl extends AbstractHandler {
    private Exception e;
    
    public void handle(final String target, final HttpServletRequest request, final HttpServletResponse response, final int dispatch) throws IOException, ServletException {
      try {
        final InputStream stream = request.getInputStream();
        InputContexts.getAsByteArray(InputContexts.forInputStream(stream));
        response.getOutputStream().close();
      } catch(final Exception e) {
        this.e = e;
      }
    }
    
  }

  private static class ServletExceptionHandlerImpl extends AbstractHandler {

    public void handle(final String target, final HttpServletRequest request, final HttpServletResponse response, final int action) throws ServletException {
      throw new ServletException();
    }

  }

  private static class TransferSummaryCallbackImpl implements TransferSummaryCallback {
    private TransferSummary transferSummary;
    private CountDownLatch latch = new CountDownLatch(1);
    public void onTranfserComplete(final TransferSummary transferSummary) {
      this.transferSummary = transferSummary;
      latch.countDown();
    }
    
  }
  
  private void startServer() throws Exception {
    startServer(handler);
  }

  private void startServer(final Handler handler) throws Exception {
    wrappedServer = new Server(PORT);
    wrappedServer.setHandler(handler);
    wrappedServer.start();
  }

  private String getAddress() {
    return "http://localhost:" + PORT;
  }
  
  public HttpTransferOutputContextImpl getOutputContext(final String url) {
    return new HttpTransferOutputContextImpl(url);
  }    

}
