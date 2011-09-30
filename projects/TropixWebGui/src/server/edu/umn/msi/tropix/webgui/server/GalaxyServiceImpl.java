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

import java.util.Arrays;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.xml.FormattedXmlUtility;
import edu.umn.msi.tropix.galaxy.tool.Tool;
import edu.umn.msi.tropix.models.GalaxyTool;
import edu.umn.msi.tropix.persistence.service.GalaxyToolService;
import edu.umn.msi.tropix.webgui.server.aop.ServiceMethod;
import edu.umn.msi.tropix.webgui.server.models.BeanSanitizer;
import edu.umn.msi.tropix.webgui.server.models.BeanSanitizerUtils;
import edu.umn.msi.tropix.webgui.server.security.UserSession;
import edu.umn.msi.tropix.webgui.services.galaxy.GalaxyService;

@ManagedBean
public class GalaxyServiceImpl implements GalaxyService {
  private static final FormattedXmlUtility<Tool> TOOL_XML_UTILITY = new FormattedXmlUtility<Tool>(Tool.class);
  private final GalaxyToolService galaxyToolService;
  private final UserSession userSession;
  private final BeanSanitizer beanSanitizer;
  
  @Inject
  public GalaxyServiceImpl(final GalaxyToolService galaxyToolService, final UserSession userSession, final BeanSanitizer beanSanitizer) {
    this.galaxyToolService = galaxyToolService;
    this.userSession = userSession;
    this.beanSanitizer = beanSanitizer;
  }

  @ServiceMethod(readOnly = true)
  public Tool loadTool(final String toolId) {
    final String toolXml = galaxyToolService.getXml(userSession.getGridId(), toolId);
    return TOOL_XML_UTILITY.deserialize(toolXml.getBytes());
  }
  
  @ServiceMethod(adminOnly = true)
  public GalaxyTool createTool(final Tool tool, final GalaxyTool toolDescription) {
    final GalaxyTool newToolDescription = galaxyToolService.create(userSession.getGridId(), toolDescription, TOOL_XML_UTILITY.toString(tool));
    return beanSanitizer.sanitize(newToolDescription);
  }

  @ServiceMethod(readOnly = true)
  public List<GalaxyTool> listTools() {
    final GalaxyTool[] toolArray = galaxyToolService.list(userSession.getGridId());
    return Lists.newArrayList(Lists.transform(Arrays.asList(toolArray), BeanSanitizerUtils.<GalaxyTool>asFunction(beanSanitizer)));
  }

  @ServiceMethod(readOnly = true)
  public String loadToolXml(final String toolId) {
    return galaxyToolService.getXml(userSession.getGridId(), toolId);
  }
  
  private void saveTool(final String toolId, final Tool tool, final String toolXml) {
    final GalaxyTool toolDescription = galaxyToolService.load(userSession.getGridId(), toolId);
    toolDescription.setName(tool.getName());
    toolDescription.setDescription(tool.getDescription());
    galaxyToolService.update(userSession.getGridId(), toolDescription, toolXml);
  }
  
  @ServiceMethod(adminOnly = true)
  public void saveTool(final String toolId, final Tool tool) {    
    saveTool(toolId, tool, TOOL_XML_UTILITY.toString(tool));
  }
  
  @ServiceMethod(adminOnly = true)
  public void saveToolXml(final String toolId, final String xml) {
    saveTool(toolId, TOOL_XML_UTILITY.fromString(xml), xml);
  }
  
}
