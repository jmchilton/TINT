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

import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.galaxy.tool.Tool;
import edu.umn.msi.tropix.models.GalaxyTool;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.ComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.EditObjectComponent;
import edu.umn.msi.tropix.webgui.client.components.WindowComponent;
import edu.umn.msi.tropix.webgui.client.components.impl.WindowComponentImpl;
import edu.umn.msi.tropix.webgui.client.mediators.LocationUpdateMediator;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.galaxy.GalaxyService;

public class EditGalaxyToolWindowComponentFactoryImpl implements ComponentFactory<GalaxyTool, WindowComponent<Window>> {
  private ComponentFactory<Tool, ? extends EditObjectComponent<? extends Canvas, Tool>> editGalaxyToolComponentFactory;
  private final LocationUpdateMediator locationUpdateMediator = LocationUpdateMediator.getInstance();

  @Inject
  public void setEditGalaxyToolComponentSupplier(final ComponentFactory<Tool, ? extends EditObjectComponent<? extends Canvas, Tool>> editGalaxyToolComponentFactory) {
    this.editGalaxyToolComponentFactory = editGalaxyToolComponentFactory;
  }

  public WindowComponent<Window> get(final GalaxyTool galaxyTool) {
    return new EditGalaxyToolWindowComponentImpl(galaxyTool);
  }

  private class EditGalaxyToolWindowComponentImpl extends WindowComponentImpl<Window> {
    private final VLayout layout = new VLayout();
    EditGalaxyToolWindowComponentImpl(final GalaxyTool galaxyTool) {
      this.setWidget(PopOutWindowBuilder.titled("Edit Galaxy Tool").sized(430, 430).withContents(layout).get());      
      // Do this last so we have reference to window.
      GalaxyService.Util.getInstance().loadTool(galaxyTool.getId(), new AsyncCallbackImpl<Tool>(get()) {
        @Override
        protected void handleSuccess() {
          final EditObjectComponent<? extends Canvas, Tool> editComponent = editGalaxyToolComponentFactory.get(getResult());
          final Button saveButton = SmartUtils.getButton("Save", Resources.SAVE, new Command() {
            public void execute() {
              GalaxyService.Util.getInstance().saveTool(galaxyTool.getId(), editComponent.getObject(), new AsyncCallbackImpl<Void>() {
                @Override
                protected void handleSuccess() {
                  locationUpdateMediator.onEvent(new LocationUpdateMediator.AddUpdateEvent("galaxyTool", null, ""));
                  get().destroy();
                }
              });
            }
          });
          SmartUtils.enabledWhenValid(saveButton, editComponent);
          layout.addMember(new CanvasWithOpsLayout<Canvas>(editComponent.get(), saveButton));
        }
      }.noCleanUpOnSuccess()); // Don't destroy window if handleSuccess is called
    }
  }
  
}
