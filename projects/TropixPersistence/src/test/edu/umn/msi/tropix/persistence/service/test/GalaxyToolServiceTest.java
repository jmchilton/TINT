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

package edu.umn.msi.tropix.persistence.service.test;

import javax.inject.Inject;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.GalaxyTool;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.galaxy.PersistentGalaxyToolStore;
import edu.umn.msi.tropix.persistence.service.GalaxyToolService;

public class GalaxyToolServiceTest extends ServiceTest {
  @Inject
  private GalaxyToolService galaxyToolService;
  
  @Inject 
  private PersistentGalaxyToolStore galaxyToolStore;
  
  @Test
  public void test() {
    User user = super.createTempUser();
    final GalaxyTool tool = new GalaxyTool();
    final GalaxyTool createdTool = galaxyToolService.create(user.getCagridId(), tool, "xml");
    final GalaxyTool updatedTool = new GalaxyTool();
    updatedTool.setId(createdTool.getId());
    updatedTool.setName("new name");
    updatedTool.setDescription("new description");
    
    
    final String id = createdTool.getId();
    assert galaxyToolService.getXml(user.getCagridId(), id).equals("xml");
    galaxyToolService.update(user.getCagridId(), updatedTool, "new xml");
    assert galaxyToolService.getXml(user.getCagridId(), id).equals("new xml");
    
    final GalaxyTool reloadedTool = galaxyToolService.load(user.getCagridId(), id);
    assert reloadedTool.getName().equals("new name");
    assert reloadedTool.getDescription().equals("new description");
    
    boolean found = false;
    for(GalaxyTool listedTool : galaxyToolService.list(user.getCagridId())) {
      found = found || listedTool.getId().equals(createdTool.getId());
    }
    assert found;
  }
  
  
}
