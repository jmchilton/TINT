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

package edu.umn.msi.tropix.webgui.client.components.galaxy;

import com.google.common.base.Supplier;
import com.google.inject.Inject;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;

import edu.umn.msi.tropix.galaxy.tool.Tool;
import edu.umn.msi.tropix.models.GalaxyTool;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.components.ComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.EditObjectComponent;
import edu.umn.msi.tropix.webgui.client.components.WindowComponent;
import edu.umn.msi.tropix.webgui.client.components.impl.EditObjectWindowComponentImpl;
import edu.umn.msi.tropix.webgui.client.mediators.LocationUpdateMediator;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils.ButtonType;
import edu.umn.msi.tropix.webgui.services.galaxy.GalaxyService;

public class AddGalaxyToolWindowComponentSupplierImpl implements Supplier<WindowComponent<Window>> {
  private ComponentFactory<Tool, ? extends EditObjectComponent<? extends Canvas, Tool>> editGalaxyToolComponentSupplier;
  private static LocationUpdateMediator locationUpdateMediator = LocationUpdateMediator.getInstance();

  @Inject
  public void setEditCatalogProviderFormComponentSupplier(final ComponentFactory<Tool, ? extends EditObjectComponent<? extends Canvas, Tool>> editGalaxyToolComponentSupplier) {
    this.editGalaxyToolComponentSupplier = editGalaxyToolComponentSupplier;
  }

  public WindowComponent<Window> get() {
    return new AddGalaxyToolWindowComponentImpl(editGalaxyToolComponentSupplier.get(new Tool()));
  }

  private static class AddGalaxyToolWindowComponentImpl extends EditObjectWindowComponentImpl<Tool> {
    AddGalaxyToolWindowComponentImpl(final EditObjectComponent<? extends Canvas, Tool> editGalaxyToolComponent) {
      super(editGalaxyToolComponent, PopOutWindowBuilder.titled("Create New Galaxy Tool").sized(430, 430), ButtonType.ADD);
      setCallback(new Listener<Tool>() {
        public void onEvent(final Tool event) {
          final String name = editGalaxyToolComponent.getObject().getName();
          final GalaxyTool galaxyTool = new GalaxyTool();
          galaxyTool.setName(name);
          GalaxyService.Util.getInstance().createTool(editGalaxyToolComponent.getObject(), galaxyTool, new AsyncCallbackImpl<GalaxyTool>() {
            @Override
            protected void handleSuccess() {
              locationUpdateMediator.onEvent(new LocationUpdateMediator.AddUpdateEvent("galaxyTool", null, ""));
              get().destroy();
            }
          });
        }        
      });
    }
  }
}
