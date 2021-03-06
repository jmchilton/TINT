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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.springframework.web.servlet.mvc.Controller;

import edu.umn.msi.tropix.common.logging.ExceptionUtils;

/**
 * This is an implementation of Jetty's Handler interface that wraps a Spring Controller instance.
 * 
 * @author John Chilton
 * 
 */
public class ControllerHandlerImpl extends AbstractHandler {
  private static final Log LOG = LogFactory.getLog(ControllerHandlerImpl.class);
  private final Controller controller;

  public ControllerHandlerImpl(final Controller controller) {
    this.controller = controller;
  }

  public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
    try {
      controller.handleRequest(request, response);
    } catch(final IOException e) {
      ExceptionUtils.logQuietly(LOG, e, "IOException in transfer controller.");
      throw e;
    } catch(final Exception e) {
      ExceptionUtils.logQuietly(LOG, e, "Generic exception in transfer controller.");
      throw new ServletException(e);
    }
  }

}
