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

package edu.umn.msi.tropix.webgui.client.components.impl;

import java.util.Collection;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.events.ItemChangedEvent;
import com.smartgwt.client.widgets.form.events.ItemChangedHandler;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.models.locations.LocationPredicates;
import edu.umn.msi.tropix.models.locations.Locations;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.WindowComponent;
import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponent;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions.SelectionType;
import edu.umn.msi.tropix.webgui.client.smart.handlers.CommandClickHandlerImpl;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.Form;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.object.ExportService;
import edu.umn.msi.tropix.webgui.services.object.ExportService.GridFtpServerOptions;

public class GridFtpExportComponentSupplierImpl implements Supplier<WindowComponent<Window>> {
  private final TreeComponentFactory treeComponentFactory;
  private final LocationFactory locationFactory;

  @Inject
  public GridFtpExportComponentSupplierImpl(final TreeComponentFactory treeComponentFactory, 
                                            final LocationFactory locationFactory) {
    this.treeComponentFactory = treeComponentFactory;
    this.locationFactory = locationFactory;
  }
  
  private class GridFtpExportComponentImpl extends WindowComponentImpl<Window> {
    private final Form form = new Form();
    private final TreeComponent treeComponent;
    private final Button exportButton = SmartUtils.getButton("Export", Resources.SAVE);
    
    private void validate() {
      exportButton.setDisabled(!form.isValid() || treeComponent.getMultiSelection().isEmpty());
    }
    
    GridFtpExportComponentImpl() {
      final TextItem hostItem = new TextItem("host", "Host");
      final TextItem portItem = new TextItem("port", "Port");
      portItem.setValue("2811");
      final TextItem pathItem = new TextItem("path", "Path");
      form.setItems(hostItem, portItem, pathItem);
      form.setValidationPredicate(new Predicate<Form>() {
        public boolean apply(final Form form) {
          return StringUtils.hasText(form.getValueAsString("host")) 
                 && StringUtils.toString(form.getValueAsString("port")).matches("\\d+") 
                 && StringUtils.hasText(form.getValueAsString("path"));
        }
      });
      form.addItemChangedHandler(new ItemChangedHandler() {
        public void onItemChanged(final ItemChangedEvent event) {
          validate();
        }        
      });
      
      exportButton.setDisabled(true);
      
      final TreeOptions treeOptions = new TreeOptions();
      treeOptions.setSelectionType(SelectionType.MULTIPlE);
      treeOptions.setInitialItems(locationFactory.getTropixObjectSourceRootItems(null));
      treeOptions.setSelectionPredicate(LocationPredicates.getTropixObjectTreeItemTypePredicate(TropixObjectTypeEnum.FILE, false));
      treeComponent = treeComponentFactory.get(treeOptions);
      final Command downloadCommand = new Command() {
        public void execute() {
          final GridFtpServerOptions options = new GridFtpServerOptions();
          options.setHostname(StringUtils.toString(hostItem.getValue()));
          options.setPath(StringUtils.toString(pathItem.getValue()));
          options.setPort(Integer.parseInt(StringUtils.toString(portItem.getValue())));
          ExportService.Util.getInstance().export(Locations.getIds(treeComponent.getMultiSelection()).toArray(new String[0]), options, new AsyncCallbackImpl<Void>() {
            protected void handleSuccess() {
              SC.say("Files exported successfully.");
            }
          });
          get().destroy();
        }
      };
      treeComponent.addMultiSelectionListener(new Listener<Collection<TreeItem>>() {
        public void onEvent(final Collection<TreeItem> event) {
          validate();
        }
      });
      exportButton.addClickHandler(new CommandClickHandlerImpl(downloadCommand));
      final VLayout layout = new VLayout();
      layout.setMembers(form, treeComponent.get());
      final CanvasWithOpsLayout<VLayout> windowLayout = new CanvasWithOpsLayout<VLayout>(layout, exportButton);
      windowLayout.setWidth("400px");
      windowLayout.setHeight("500px");
      setWidget(PopOutWindowBuilder.titled("GridFTP Export").autoSized().withContents(windowLayout).get());
    }
  }
   
  public WindowComponent<Window> get() {
    return new GridFtpExportComponentImpl();
  }

}
