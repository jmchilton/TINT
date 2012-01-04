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

package edu.umn.msi.tropix.webgui.client.identification;

import java.util.Map;

import com.google.common.base.Predicate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.tree.TreeGrid;

import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.locations.LocationPredicates;
import edu.umn.msi.tropix.models.utils.TropixObjectType;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponent;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItemExpander;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItemExpanders;
import edu.umn.msi.tropix.webgui.client.smart.handlers.CommandClickHandlerImpl;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.protip.IdentificationParametersService;

public class LoadCommand implements Command {
  private final ParametersPanel parametersPanel;
  private final String parameterType;
  private final TreeComponentFactory treeComponentFactory;
  private final LocationFactory locationFactory;

  public LoadCommand(final TreeComponentFactory treeComponentFactory, final LocationFactory locationFactory, final ParametersPanel panel, final String parametersType) {
    this.treeComponentFactory = treeComponentFactory;
    this.parametersPanel = panel;
    this.parameterType = parametersType;
    this.locationFactory = locationFactory;
  }

  public void execute() {
    final Button okButton = SmartUtils.getButton("Select");
    final Button cancelButton = SmartUtils.getButton("Cancel");
    final TropixObjectType[] types = new TropixObjectType[] {TropixObjectTypeEnum.FOLDER, TropixObjectTypeEnum.VIRTUAL_FOLDER, TropixObjectTypeEnum.IDENTIFICATION_PARAMETERS};
    final Predicate<TropixObject> predicate = new IdentificationParametersPredicate(this.parameterType);
    final TropixObjectTreeItemExpander expander = TropixObjectTreeItemExpanders.get(types);
    final TreeOptions treeOptions = new TreeOptions();
    treeOptions.setInitialItems(locationFactory.getTropixObjectSourceRootItems(expander));
    treeOptions.setShowPredicate(LocationPredicates.getTropixObjectTreeItemPredicate(predicate, true));
    treeOptions.setSelectionPredicate(LocationPredicates.getIdentificationParametersOfTypePredicate(this.parameterType, false));
    final TreeComponent tree = this.treeComponentFactory.get(treeOptions);
    final TreeGrid treeGrid = tree.get();
    SmartUtils.enabledWhenHasSelection(okButton, treeGrid, false);
    final Layout contents = new CanvasWithOpsLayout<TreeGrid>("Select identification parameters to load.", treeGrid, okButton, cancelButton);     
    final Window window = PopOutWindowBuilder.titled("Load Parameters").sized(400, 400).withContents(contents).withIcon(Resources.PARAMETERS_16).get();
    final AsyncCallback<Map<String, String>> setParametersCallback = new AsyncCallback<Map<String, String>>() {
      public void onFailure(final Throwable caught) {
        SC.say("Failed to load parameters");
        window.destroy();
      }

      public void onSuccess(final Map<String, String> templateMap) {
        parametersPanel.setTemplateMap(templateMap);
        window.destroy();
      }
    };
    final Command okCommand = new Command() {
      public void execute() {
        IdentificationParametersService.Util.getInstance().getParameterMap(tree.getSelection().getId(), setParametersCallback);
      }
    };
    SmartUtils.destoryOnClick(window, cancelButton);    
    okButton.addClickHandler(new CommandClickHandlerImpl(okCommand));    
    window.show();
  }
}
