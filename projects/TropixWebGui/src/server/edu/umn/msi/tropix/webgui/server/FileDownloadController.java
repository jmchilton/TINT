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

package edu.umn.msi.tropix.webgui.server;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import edu.umn.msi.tropix.webgui.server.aop.ServiceMethod;
import edu.umn.msi.tropix.webgui.server.download.FileDownloadManager;
import edu.umn.msi.tropix.webgui.server.servlet.HttpRequestParameterFunctionImpl;

public class FileDownloadController implements Controller {
  private FileDownloadManager fileDownloadManager;

  @ServiceMethod(readOnly = true)
  public ModelAndView handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
    final String downloadType = request.getParameter("downloadType");
    final String uri = request.getRequestURI();
    final String fileName = uri.substring(uri.lastIndexOf('/') + 1);
    // The following line forces a save dialog even for plain text (e.g. XML)
    // files.
    response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
    this.fileDownloadManager.handleDownloadRequest(downloadType, response.getOutputStream(), new HttpRequestParameterFunctionImpl(request));
    return null; // Return null to indicate the request was handled here.
  }

  @Inject
  public void setFileDownloadManager(final FileDownloadManager fileDownloadManager) {
    this.fileDownloadManager = fileDownloadManager;
  }

}
