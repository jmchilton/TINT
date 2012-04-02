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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class HttpTransferControllerImpl implements Controller {
  private static final Log LOG = LogFactory.getLog(HttpTransferControllerImpl.class);
  private static final long serialVersionUID = 1L;
  private FileKeyResolver fileKeyResolver;

  public void setFileKeyResolver(final FileKeyResolver fileKeyResolver) {
    this.fileKeyResolver = fileKeyResolver;
  }

  public ModelAndView handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
    final String method = request.getMethod();
    if(method.equals("POST")) {
      doPost(request, response);
    } else {
      doGet(request, response);
    }
    return null;
  }

  private void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
    LOG.info("doGet called");
    final String downloadKey = request.getParameter(ServerConstants.KEY_PARAMETER_NAME);
    LOG.info("Key is " + downloadKey);
    try {
      response.setStatus(HttpServletResponse.SC_OK);
      response.flushBuffer();
      fileKeyResolver.handleDownload(downloadKey, response.getOutputStream());
    } catch(final Exception e) {
      LOG.info("handleDownload threw exception", e);
      throw new ServletException(e);
    }
  }

  private void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
    LOG.info("doPost called");
    final String uploadKey = request.getParameter(ServerConstants.KEY_PARAMETER_NAME);
    LOG.info("Key is " + uploadKey);
    try {
      fileKeyResolver.handleUpload(uploadKey, request.getInputStream());
      response.flushBuffer();
      response.setStatus(HttpServletResponse.SC_OK);
    } catch(final Exception e) {
      throw new ServletException(e);
    }
  }

}
