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

package edu.umn.msi.tropix.webgui.services.galaxy;

import java.util.List;

import org.gwtwidgets.server.spring.GWTRequestMapping;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.umn.msi.tropix.galaxy.tool.Tool;
import edu.umn.msi.tropix.models.GalaxyTool;

@RemoteServiceRelativePath("GalaxyService.rpc")
@GWTRequestMapping("/webgui/GalaxyService.rpc")
public interface GalaxyService extends RemoteService {
  
  public static class Util {
    public static GalaxyServiceAsync getInstance() {
      return (GalaxyServiceAsync) GWT.create(GalaxyService.class);
    }
  } 
    
  Tool loadTool(final String toolId);
  
  String loadToolXml(final String toolId);
  
  void saveTool(final String toolId, final Tool tool);
  
  void saveToolXml(final String toolId, final String xml);
  
  GalaxyTool createTool(final Tool tool, final GalaxyTool toolDescription);

  List<GalaxyTool> listTools();
  
}
