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

package edu.umn.msi.tropix.webgui.client.modules;

import com.google.gwt.user.client.Command;
import com.google.inject.Inject;

import edu.umn.msi.tropix.models.GalaxyTool;
import edu.umn.msi.tropix.webgui.client.Session;
import edu.umn.msi.tropix.webgui.client.components.ComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.galaxy.GalaxyActionEvent;
import edu.umn.msi.tropix.webgui.client.mediators.ActionMediator;
import edu.umn.msi.tropix.webgui.client.mediators.LocationActionEventImpl;
import edu.umn.msi.tropix.webgui.client.mediators.ActionMediator.ActionEvent;
import edu.umn.msi.tropix.webgui.client.models.NewItemContext;
import edu.umn.msi.tropix.webgui.client.models.NewItemModelImpl;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.services.session.Module;

public class GalaxyModuleInstallerImpl extends BaseModuleInstallerImpl implements ModuleInstaller {
  private ActionMediator actionMediator;
  private ComponentFactory<GalaxyActionEvent, ? extends Command> galaxyActionComponentFactory;
  
  @Inject
  public void setActionMediator(final ActionMediator actionMediator) {
    this.actionMediator = actionMediator;
  }
  
  @Inject
  public void setGalaxyActionComponentFactory(final ComponentFactory<GalaxyActionEvent, ? extends Command> galaxyActionComponentFactory) {
    this.galaxyActionComponentFactory = galaxyActionComponentFactory;
  }

  public void installToolItemModels(final Session session, final NewItemContext newItemContext) {
    for(final GalaxyTool tool : session.getGalaxyTools()) {
      final String toolName = tool.getName();
      final String toolDescription = tool.getDescription();
      newItemContext.addModel(new NewItemModelImpl(toolName, toolDescription));
      actionMediator.registerActionListener("newItem" + toolName, new Listener<ActionEvent>() {
        public void onEvent(final ActionEvent event) {
          if(event instanceof LocationActionEventImpl) {
            final LocationActionEventImpl locationActionEvent = (LocationActionEventImpl) event;          
            galaxyActionComponentFactory.get(new GalaxyActionEvent(tool, locationActionEvent.getItems())).execute();
          }
        }
      });
    }
    
  }

  public Module getModule() {
    return Module.GALAXY;
  }

  public void installNewItemModels(final Session session, final NewItemContext newItemContext) {
    
  }
  
}
