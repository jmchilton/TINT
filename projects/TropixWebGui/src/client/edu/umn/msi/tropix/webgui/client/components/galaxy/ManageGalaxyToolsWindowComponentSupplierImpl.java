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

import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;

import edu.umn.msi.tropix.models.GalaxyTool;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.ComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.WindowComponent;
import edu.umn.msi.tropix.webgui.client.components.impl.WindowComponentImpl;
import edu.umn.msi.tropix.webgui.client.utils.Iterables;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.ClientListGrid;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils.ButtonType;
import edu.umn.msi.tropix.webgui.services.galaxy.GalaxyService;

public class ManageGalaxyToolsWindowComponentSupplierImpl implements Supplier<WindowComponent<Window>> {
  private ComponentFactory<GalaxyTool, ? extends Command> editGalaxyToolComponentFactory, editGalaxyToolXmlComponentFactory;
  private Supplier<? extends Command> addGalaxyToolComponentSupplier;  

  @Inject
  public void setEditGalaxyToolComponentFactory(@Named("editGalaxyTool") final ComponentFactory<GalaxyTool, ? extends Command> editGalaxyToolComponentFactory) {
    this.editGalaxyToolComponentFactory = editGalaxyToolComponentFactory;
  }

  @Inject
  public void setEditGalaxyToolXmlComponentFactory(@Named("editGalaxyToolXml") final ComponentFactory<GalaxyTool, ? extends Command> editGalaxyToolXmlComponentFactory) {
    this.editGalaxyToolXmlComponentFactory = editGalaxyToolXmlComponentFactory;
  }

  @Inject
  public void setAddGalaxyToolComponentSupplier(@Named("addGalaxyTool") final Supplier<? extends Command> addGalaxyToolComponentSupplier) {
    this.addGalaxyToolComponentSupplier = addGalaxyToolComponentSupplier;
  }

  public WindowComponent<Window> get() {
    return new ManageGalaxyToolsWindowComponenImpl();
  }
  
  private static final Function<GalaxyTool, ListGridRecord> TO_RECORD_FUNCTION = new Function<GalaxyTool, ListGridRecord>() {
    public ListGridRecord apply(final GalaxyTool galaxyTool) {
      final ListGridRecord record = new ListGridRecord();
      record.setAttribute("id", galaxyTool.getId());
      record.setAttribute("object", galaxyTool);
      record.setAttribute("name", StringUtils.sanitize(galaxyTool.getName()));
      return record;
    }
  };

  
  private class ManageGalaxyToolsWindowComponenImpl extends WindowComponentImpl<Window> {
    private final ListGrid toolsGrid;
    private GalaxyTool galaxyTool;    

    private void reload() {
      toolsGrid.deselectAllRecords();
      SmartUtils.removeAllRecords(toolsGrid);
      GalaxyService.Util.getInstance().listTools(new AsyncCallbackImpl<List<GalaxyTool>>() {
        @Override
        public void onSuccess(final List<GalaxyTool> tools) {
          SmartUtils.addRecords(toolsGrid, Iterables.transform(tools, TO_RECORD_FUNCTION));
        }
      });
    }
    
    ManageGalaxyToolsWindowComponenImpl() {
      final ListGridField nameField = new ListGridField("name", "Tool Name");
      nameField.setType(ListGridFieldType.TEXT);
      nameField.setWidth("*");

      final Button addButton = SmartUtils.getButton(ButtonType.ADD, new Command() {
        public void execute() {
          addGalaxyToolComponentSupplier.get().execute();
        }
      });

      final Button editButton = SmartUtils.getButton(ButtonType.EDIT, new Command() {
        public void execute() {
          editGalaxyToolComponentFactory.get(galaxyTool).execute();
        }
      });

      final Button editXmlButton = SmartUtils.getButton("Edit XML", Resources.EDIT, new Command() {
        public void execute() {
          editGalaxyToolXmlComponentFactory.get(galaxyTool).execute();
        }
      });

      toolsGrid = new ClientListGrid("id");
      toolsGrid.setMinHeight(10);
      toolsGrid.setAutoFitMaxRecords(5);
      toolsGrid.setAutoFitData(Autofit.VERTICAL);
      toolsGrid.setFields(nameField);
      toolsGrid.setEmptyMessage("No tools to show");
      toolsGrid.setHeight100();
      toolsGrid.addSelectionChangedHandler(new SelectionChangedHandler() {
        public void onSelectionChanged(final SelectionEvent event) {
          if(event.getState()) {
            galaxyTool = (GalaxyTool) event.getRecord().getAttributeAsObject("object");
          } else {
            galaxyTool = null;
          }
          editButton.setDisabled(galaxyTool == null);
          editXmlButton.setDisabled(galaxyTool == null);
        }
      });
      editButton.setDisabled(true);
      editXmlButton.setDisabled(true);
      
      final CanvasWithOpsLayout<ListGrid> toolsLayout = new CanvasWithOpsLayout<ListGrid>(toolsGrid, addButton, editButton, editXmlButton);
      this.setWidget(PopOutWindowBuilder.titled("Manage Galaxy Style Tool Description").sized(500, 400).withContents(toolsLayout).get());
      reload();
    }
  }
  
}
