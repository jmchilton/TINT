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

package edu.umn.msi.tropix.webgui.services.object;

import java.io.Serializable;

import org.gwtwidgets.server.spring.GWTRequestMapping;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ExportService.rpc")
@GWTRequestMapping("/webgui/ExportService.rpc")
public interface ExportService extends RemoteService {
  
  public static class Util {
    public static ExportServiceAsync getInstance() {
      return (ExportServiceAsync) GWT.create(ExportService.class);
    }
  }
  
  public static class GridFtpServerOptions implements Serializable {
    private String hostname;
    private int port;
    private String path;
    
    public String getHostname() {
      return hostname;
    }
    
    public void setHostname(final String hostname) {
      this.hostname = hostname;
    }
    
    public int getPort() {
      return port;
    }
    
    public void setPort(final int port) {
      this.port = port;
    }
    
    public String getPath() {
      return path;
    }
    
    public void setPath(final String path) {
      this.path = path;
    }
        
  }
  
  void export(final String[] ids, final GridFtpServerOptions gridFtpOptions);  
}
