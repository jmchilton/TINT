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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.Ostermiller.util.MD5InputStream;
import com.Ostermiller.util.MD5OutputStream;

/*
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
*/

import edu.umn.msi.tropix.common.io.IORuntimeException;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.common.io.SizedStreamOutputContextImpl;
import edu.umn.msi.tropix.common.io.StreamInputContextImpl;

public class HttpTransferClients {
  private static final Log LOG = LogFactory.getLog(HttpTransferClients.class);
  private static final HttpTransferClient INSTANCE = new HttpTransferClientImpl();
  private static final InstrumentableHttpTransferClient INSTRUMENTABLE_INSTANCE = new InstrumentableHttpTransferClientImpl();
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  //private static final MultiThreadedHttpConnectionManager HTTP_CONNECTION_MANAGER = new MultiThreadedHttpConnectionManager();
  //private static final HttpClient HTTP_CLIENT = new HttpClient(HTTP_CONNECTION_MANAGER);
  //private static HttpClient client = new DefaultHttpClient(new ThreadSafeClientConnManager(), new DefaultedHttpParams());
  
  public static HttpTransferClient getInstance() {
    return INSTANCE;
  }
  
  public static InstrumentableHttpTransferClient getInstrumentableInstance() {
    return INSTRUMENTABLE_INSTANCE;
  }

  private static final class TransferSummaryImpl implements TransferSummary {
    private final long bytesTranfserred;
    private final byte[] md5sum;
    
    private TransferSummaryImpl(final long bytesTranfserred, final byte[] md5sum) {
      this.bytesTranfserred = bytesTranfserred;
      this.md5sum = md5sum;
    }

    public long getBytesTransferred() {
      return bytesTranfserred;
    }

    public byte[] getMd5Sum() {
      return Arrays.copyOf(md5sum, md5sum.length);
    }
    
  }

  private static class InstrumentableHttpTransferClientImpl implements InstrumentableHttpTransferClient {
        
    public InputContext getInputContext(final String url, final TransferSummaryCallback callback) {
      return new InstrumentableHttpTransferInputContextImpl(url, callback);
    }

    public OutputContext getOutputContext(final String url, final TransferSummaryCallback callback) {
      return new InstrumentableHttpTransferOutputContextImpl(url, callback);
    }

    public InputContext getInputContext(final String url) {
      return getInputContext(url, new NullTransferSummaryCallbackImpl());
    }

    public OutputContext getOutputContext(final String url) {
      return getOutputContext(url, new NullTransferSummaryCallbackImpl());
    }
    
  }
  
  private static class HttpTransferClientImpl implements HttpTransferClient {
    
    public InputContext getInputContext(final String url) {
      return new HttpTransferInputContextImpl(url);
    }
        
    public OutputContext getOutputContext(final String url) {
      return new HttpTransferOutputContextImpl(url);
    }    
    
  }
  
  static class HttpTransferInputContextImpl extends BaseHttpTransferInputContextImpl {
    
    HttpTransferInputContextImpl(final String url) {
      super(url);
    }

    public void get(final OutputStream outputStream) {
      httpGet(outputStream);
    }
    
  }
  
  static class InstrumentableHttpTransferInputContextImpl extends BaseHttpTransferInputContextImpl {
    private final TransferSummaryCallback callback;
    
    public InstrumentableHttpTransferInputContextImpl(final String url, final TransferSummaryCallback callback) {
      super(url);
      this.callback = callback;
    }
    
    public void get(final OutputStream outputStream) {
      final MD5OutputStream wrapperStream = new MD5OutputStream(outputStream);
      long bytesTransferred = httpGet(wrapperStream);
      IO_UTILS.flushQuietly(wrapperStream);
      final byte[] md5Sum = wrapperStream.getHash();
      final String messageTemplate = "Finished getting url %s, bytes transferred %d, md5sum %s";
      final String logMessage = String.format(messageTemplate, getUrl(), bytesTransferred, wrapperStream.getHashString());
      LOG.info(logMessage);
      callback.onTranfserComplete(new TransferSummaryImpl(bytesTransferred, md5Sum));
    }
    
  }

  abstract static class BaseHttpTransferInputContextImpl extends StreamInputContextImpl {
    private final String url;

    protected String getUrl() {
      return url;
    }
    
    BaseHttpTransferInputContextImpl(final String url) {
      this.url = url;
    }

    protected long httpGet(final OutputStream outputStream) {
      long bytesCopied = 0;
      final HttpClient client = new DefaultHttpClient();
      final HttpGet httpget = new HttpGet(url);
      final HttpResponse response = HttpTransferClients.execute(client, httpget);
      try { 
        final HttpEntity entity = response.getEntity();
        if(entity != null) {
          bytesCopied = IO_UTILS.copy(entity.getContent(), outputStream);
        }
      } catch(IOException e) {
        throw new IORuntimeException(e);
      }
      return bytesCopied;
    }
    
  }
  
  static class HttpTransferOutputContextImpl extends BaseHttpTransferOutputContextImpl {
    
    HttpTransferOutputContextImpl(final String url) {
      super(url);
    }
    
    public void put(final InputStream inputStream, final long length) {
      httpPost(inputStream, length);
    }

  }

  static class InstrumentableHttpTransferOutputContextImpl extends BaseHttpTransferOutputContextImpl {
    private final TransferSummaryCallback callback;
    
    InstrumentableHttpTransferOutputContextImpl(final String url, final TransferSummaryCallback callback) {
      super(url);
      this.callback = callback;
    }
    
    public void put(final InputStream inputStream, final long length) {
      final MD5InputStream wrapperStream = new MD5InputStream(inputStream);
      httpPost(wrapperStream, length);
      final byte[] md5Sum = wrapperStream.getHash();
      final String messageTemplate = "Finished posting to url %s, bytes transferred %d, md5sum %s";
      final String logMessage = String.format(messageTemplate, getUrl(), length, wrapperStream.getHashString());
      LOG.info(logMessage);
      callback.onTranfserComplete(new TransferSummaryImpl(length, md5Sum));
    }

  }

  
  abstract static class BaseHttpTransferOutputContextImpl extends SizedStreamOutputContextImpl {
    private final String url;
    
    protected String getUrl() {
      return url;
    }
    
    BaseHttpTransferOutputContextImpl(final String url) {
      this.url = url;
    }

    protected void httpPost(final InputStream inputStream, final long length) {
      final HttpPost httppost = new HttpPost(url);
      final InputStreamEntity entity = new InputStreamEntity(inputStream, length);
      httppost.setEntity(entity);
      final HttpClient client = new DefaultHttpClient();
      execute(client, httppost);
    }
  }

  static HttpResponse execute(final HttpClient client, final HttpUriRequest request) {
    try {
      final HttpResponse response = client.execute(request);
      int statusCode = response.getStatusLine().getStatusCode();
      if(statusCode != 200) {
        throw new RuntimeException("Transfer failed - server returned response code " + statusCode);
      }
      return response;
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
  }
  
}
