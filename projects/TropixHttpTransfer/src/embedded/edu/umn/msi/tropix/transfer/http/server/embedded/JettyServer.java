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
import java.net.ServerSocket;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;

import edu.umn.msi.tropix.common.shutdown.ShutdownAware;

/**
 * A wrapper around Jetty's embedded server class that can be configured via Spring.
 * 
 */
public class JettyServer implements ShutdownAware, HasPort {
  private static final Random RANDOM = new Random();
  private static final Log LOG = LogFactory.getLog(JettyServer.class);
  private Server wrappedServer;
  private int port;
  private Handler handler;
  private boolean use = true;

  public JettyServer(final int port) {
    this.port = getFreePort(port);
  }
  
  private boolean isPortFree(final int port) {
    boolean isPortFree = true;
    ServerSocket socket = null;
    try {
      socket = new ServerSocket(port);
    } catch (final IOException e) {
      isPortFree = false;
    } finally {
      // Close socket quitely.
      if (socket != null) {
        try { 
          socket.close();
        } catch(final IOException io) {
          LOG.debug("Failed to close socket " + socket);
        }
      }
    }
    return isPortFree;
  }
  
  private int getFreePort(final int attempt) {
    if(!isPortFree(attempt)) {
      LOG.warn("Failed to start embedded Jetty on port " + attempt + " try to find another port.");
      // Get a random port from 12000 to 14000
      return getFreePort((RANDOM.nextInt() % 1000) + 13000);
    } else {
      return attempt;
    }
  }
  
  @PostConstruct
  public void init() throws Exception {
    if(!use) {
      return;
    }
    LOG.info("Starting embedded Jetty server on port " + port);
    wrappedServer = new Server(port);    
    wrappedServer.setHandler(handler);    
    wrappedServer.start();
  }

  public void destroy() {
    if(wrappedServer == null) {
      return;
    }
    try {
      wrappedServer.stop();
    } catch(final Exception e) {
      throw new RuntimeException(e);
    }
    wrappedServer.destroy();
  }

  public void setUse(final boolean use) {
    this.use = use;
  }

  public void setHandler(final Handler handler) {
    this.handler = handler;
  }

  public boolean isRunning() {
    return wrappedServer.isRunning();
  }
  
  public int getPort() {
    return port;
  }

}
