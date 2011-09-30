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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;

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
  private static final HttpTransferClient INSTANCE = new HttpTransferClientImpl();
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  //private static final MultiThreadedHttpConnectionManager HTTP_CONNECTION_MANAGER = new MultiThreadedHttpConnectionManager();
  //private static final HttpClient HTTP_CLIENT = new HttpClient(HTTP_CONNECTION_MANAGER);
  //private static HttpClient client = new DefaultHttpClient(new ThreadSafeClientConnManager(), new DefaultedHttpParams());
  
  public static HttpTransferClient getInstance() {
    return INSTANCE;
  }

  private static class HttpTransferClientImpl implements HttpTransferClient {
    
    public InputContext getInputContext(final String url) {
      return new StreamInputContextImpl() {
        public void get(final OutputStream outputStream) {
          final HttpClient client = new DefaultHttpClient();
          final HttpGet httpget = new HttpGet(url);
          final HttpResponse response = execute(client, httpget);
          try { 
            final HttpEntity entity = response.getEntity();
            if(entity != null) {
              IO_UTILS.copy(entity.getContent(), outputStream);
            }
          } catch(IOException e) {
            throw new IORuntimeException(e);
          }
        }
      };
    }
    
    private static HttpResponse execute(final HttpClient client, final HttpUriRequest request) {
      try {
        final HttpResponse response = client.execute(request);
        if(response.getStatusLine().getStatusCode() != 200) {
          throw new RuntimeException("Transfer failed - server returned response code " + 200);
        }
        return response;
      } catch(Exception e) {
        throw new RuntimeException(e);
      }
    }
    

    public OutputContext getOutputContext(final String url) {
      return new SizedStreamOutputContextImpl() {
        public void put(final InputStream inputStream, final long length) {
          final HttpPost httppost = new HttpPost(url);
          final InputStreamEntity entity = new InputStreamEntity(inputStream, length);
          httppost.setEntity(entity);
          final HttpClient client = new DefaultHttpClient();
          execute(client, httppost);
        }
      };
    }
    
    /*
    public InputContext getInputContext(final String url) {
      return new StreamInputContextImpl() {
        public void get(final OutputStream outputStream) {
          final GetMethod method = new GetMethod(url);
          try {
            execute(method);
            IO_UTILS.copy(method.getResponseBodyAsStream(), outputStream);
          } catch(final IOException ioException) {
            throw new IORuntimeException(ioException);
          } finally {
            method.releaseConnection();
          }
        }
      };
    }

    public OutputContext getOutputContext(final String url) {
      return new SizedStreamOutputContextImpl() {
        public void put(final InputStream inputStream, final long length) {
          final PostMethod method = new PostMethod(url);
          method.setRequestEntity(new InputStreamRequestEntity(inputStream, length));
          try {
            execute(method);
          } finally {
            method.releaseConnection();
          }
        }
      };
    }

  }
  */
  }
}
