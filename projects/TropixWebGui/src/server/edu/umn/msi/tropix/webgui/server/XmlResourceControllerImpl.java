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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import edu.umn.msi.tropix.common.io.IORuntimeException;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.webgui.server.aop.ServiceMethod;
import edu.umn.msi.tropix.webgui.server.xml.XMLResourceManager;

public class XmlResourceControllerImpl implements Controller {
  private static final String DEFAULT_ENCODING = "xml";
  private XMLResourceManager xmlResourceManager;
  private final IOUtils ioUtils = IOUtilsFactory.getInstance();

  @ServiceMethod(readOnly = true)
  public ModelAndView handleRequest(final HttpServletRequest request, final HttpServletResponse response) {
    System.out.println("Start " + System.currentTimeMillis());
    @SuppressWarnings("unchecked")
    final Map<String, Object> parameterMap = request.getParameterMap();
    String encoding = XmlResourceControllerImpl.DEFAULT_ENCODING;
    if(parameterMap.containsKey("encoding")) {
      encoding = request.getParameter("encoding");
    }
    final String identifier = request.getParameter("id");
    InputStream inputStream = null;
    OutputStream responseOutputStream = null;
    try {
      if(encoding.equals("xml")) {
        inputStream = this.xmlResourceManager.getResourceXmlStream(identifier);
      } else if(encoding.equals("json")) {
        inputStream = this.xmlResourceManager.getResourceJSONStream(identifier);
      } else {
        throw new IllegalStateException("Unknown resource encoding -- " + encoding);
      }
      try {
        responseOutputStream = response.getOutputStream();
      } catch(final IOException e) {
        throw new IORuntimeException(e);
      }
      this.ioUtils.copy(inputStream, responseOutputStream);
    } finally {
      this.ioUtils.closeQuietly(inputStream);
      this.ioUtils.closeQuietly(responseOutputStream);
    }
    System.out.println("End " + System.currentTimeMillis());
    return null;
  }

  public void setXmlResourceManager(final XMLResourceManager xmlResourceManager) {
    this.xmlResourceManager = xmlResourceManager;
  }

}
