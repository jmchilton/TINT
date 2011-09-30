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

import java.util.UUID;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.TestNGDataProviders;
import edu.umn.msi.tropix.common.xml.FormattedXmlUtility;
import edu.umn.msi.tropix.galaxy.tool.Command;
import edu.umn.msi.tropix.galaxy.tool.Tool;
import edu.umn.msi.tropix.models.GalaxyTool;
import edu.umn.msi.tropix.persistence.service.GalaxyToolService;

public class GalaxyServiceImplTest extends BaseGwtServiceTest {
  private static final FormattedXmlUtility<Tool> TOOL_XML_UTILITY = new FormattedXmlUtility<Tool>(Tool.class);
  private GalaxyServiceImpl galaxyService;
  private GalaxyToolService galaxyToolService;
  
  @BeforeMethod(groups = "unit")
  public void init() {
    super.init();
    galaxyToolService = EasyMock.createMock(GalaxyToolService.class);
    galaxyService = new GalaxyServiceImpl(galaxyToolService, getUserSession(), getSanitizer());    
  }
  
  @Test(groups = "unit")
  public void create() {
    final Tool tool = new Tool();
    final GalaxyTool toolDescription = new GalaxyTool();
    final GalaxyTool returnedTool = new GalaxyTool();
    galaxyToolService.create(expectUserId(), EasyMock.same(toolDescription), EasyMock.eq(TOOL_XML_UTILITY.toString(tool)));
    EasyMock.expectLastCall().andReturn(returnedTool);
    EasyMock.replay(galaxyToolService);
    galaxyService.createTool(tool, toolDescription);
    EasyMock.verify(galaxyToolService);
    assert getSanitizer().wasSanitized(returnedTool);
  }
  
  @Test(groups = "unit", dataProvider = "bool1", dataProviderClass = TestNGDataProviders.class)
  public void save(final boolean asXml) {
    final Tool tool = new Tool();
    tool.setName("New Name");
    tool.setDescription("New Description");
    final GalaxyTool toolDescription = new GalaxyTool();
    toolDescription.setId(UUID.randomUUID().toString());
    EasyMock.expect(galaxyToolService.load(getUserId(), toolDescription.getId())).andReturn(toolDescription);
    final Capture<GalaxyTool> descriptionCapture = EasyMockUtils.newCapture();
    galaxyToolService.update(expectUserId(), EasyMock.capture(descriptionCapture), EasyMock.eq(TOOL_XML_UTILITY.toString(tool)));
    EasyMock.replay(galaxyToolService);
    if(asXml) {
      galaxyService.saveToolXml(toolDescription.getId(), TOOL_XML_UTILITY.toString(tool));
    } else {
      galaxyService.saveTool(toolDescription.getId(), tool);
    }
    EasyMock.verify(galaxyToolService);
    assert descriptionCapture.getValue().getName().equals("New Name");
    assert descriptionCapture.getValue().getDescription().equals("New Description");    
  }
  
  @Test(groups = "unit")
  public void loadToolXml() {
    final Tool tool = new Tool();
    final Command command = new Command();
    command.setValue("moo cow");
    tool.setCommand(command);
    final GalaxyTool toolDescription = new GalaxyTool();
    toolDescription.setId(UUID.randomUUID().toString());
    EasyMock.expect(galaxyToolService.getXml(getUserId(), toolDescription.getId())).andReturn(TOOL_XML_UTILITY.toString(tool));
    EasyMock.replay(galaxyToolService);
    assert galaxyService.loadToolXml(toolDescription.getId()).equals(TOOL_XML_UTILITY.toString(tool));
    EasyMock.verify(galaxyToolService);   
  }
  
  @Test(groups = "unit")
  public void loadTool() {
    final Tool tool = new Tool();
    final Command command = new Command();
    command.setValue("moo cow");
    tool.setCommand(command);
    final GalaxyTool toolDescription = new GalaxyTool();
    toolDescription.setId(UUID.randomUUID().toString());
    EasyMock.expect(galaxyToolService.getXml(getUserId(), toolDescription.getId())).andReturn(TOOL_XML_UTILITY.toString(tool));
    EasyMock.replay(galaxyToolService);
    assert galaxyService.loadTool(toolDescription.getId()).getCommand().getValue().equals("moo cow");
    EasyMock.verify(galaxyToolService);  
  }
  
  @Test(groups = "unit")
  public void listTools() {
    final GalaxyTool tool1 = new GalaxyTool();
    tool1.setName("tool1");
    final GalaxyTool tool2 = new GalaxyTool();
    tool2.setName("tool2");    
    EasyMock.expect(galaxyToolService.list(getUserId())).andReturn(new GalaxyTool[] {tool1, tool2});
    EasyMock.replay(galaxyToolService);
    assert Iterables.elementsEqual(galaxyService.listTools(), Lists.newArrayList(tool1, tool2));
    EasyMock.verify(galaxyToolService);
    getSanitizer().wasSanitized(tool1);
    getSanitizer().wasSanitized(tool2);
  }
  
}
