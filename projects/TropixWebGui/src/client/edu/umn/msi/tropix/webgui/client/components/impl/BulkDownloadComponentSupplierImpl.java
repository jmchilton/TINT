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

import com.google.common.base.Supplier;
import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.tree.TreeGrid;

import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.WindowComponent;
import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponent;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItemPredicates;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItems;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions.SelectionType;
import edu.umn.msi.tropix.webgui.client.constants.ConstantsInstances;
import edu.umn.msi.tropix.webgui.client.smart.handlers.CommandClickHandlerImpl;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.GWTDownloadFormPanel;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;

public class BulkDownloadComponentSupplierImpl implements Supplier<WindowComponent<Window>> {
  private final TreeComponentFactory treeComponentFactory;
  private final LocationFactory locationFactory;

  @Inject
  public BulkDownloadComponentSupplierImpl(final TreeComponentFactory treeComponentFactory,
      final LocationFactory locationFactory) {
    this.treeComponentFactory = treeComponentFactory;
    this.locationFactory = locationFactory;
  }

  private class BulkDownloadComponentImpl extends WindowComponentImpl<Window> {
    private final GWTDownloadFormPanel smartDownloadFormPanel;

    BulkDownloadComponentImpl() {
      this.smartDownloadFormPanel = new GWTDownloadFormPanel();
      final Button downloadButton = SmartUtils.getButton(ConstantsInstances.COMPONENT_INSTANCE.bulkDownload(), Resources.DOWNLOAD);
      // this.smartDownloadFormPanel.addToPanel(downloadButton);
      this.smartDownloadFormPanel.setType("bulk");
      final TreeOptions treeOptions = new TreeOptions();
      treeOptions.setSelectionType(SelectionType.MULTIPlE);
      treeOptions.setInitialItems(locationFactory.getTropixObjectSourceRootItems(null));
      treeOptions.setSelectionPredicate(TreeItemPredicates.getTropixObjectTreeItemTypePredicate(TropixObjectTypeEnum.FILE, false));
      final TreeComponent treeComponent = treeComponentFactory.get(treeOptions);
      final Command downloadCommand = new Command() {
        public void execute() {
          smartDownloadFormPanel.setFilename(ConstantsInstances.COMPONENT_INSTANCE.bulkDownloadName());
          final Collection<TreeItem> selectedObjects = treeComponent.getMultiSelection();
          final String ids = StringUtils.join(TreeItems.getIds(selectedObjects));
          smartDownloadFormPanel.setId(ids);
          smartDownloadFormPanel.execute();
        }
      };
      treeComponent.addMultiSelectionListener(new Listener<Collection<TreeItem>>() {
        public void onEvent(final Collection<TreeItem> event) {
          downloadButton.setDisabled(event.isEmpty());
        }
      });
      downloadButton.setDisabled(treeComponent.getMultiSelection().isEmpty());
      downloadButton.addClickHandler(new CommandClickHandlerImpl(downloadCommand));
      final CanvasWithOpsLayout<TreeGrid> layout = new CanvasWithOpsLayout<TreeGrid>(treeComponent.get(), downloadButton);
      // layout.setWidth("400px");
      // layout.setHeight("500px");
      layout.addChild(smartDownloadFormPanel);
      this.setWidget(PopOutWindowBuilder.titled("Bulk Download").sized(600, 600).withContents(layout).get());
    }
  }

  public WindowComponent<Window> get() {
    return new BulkDownloadComponentImpl();
  }

}
