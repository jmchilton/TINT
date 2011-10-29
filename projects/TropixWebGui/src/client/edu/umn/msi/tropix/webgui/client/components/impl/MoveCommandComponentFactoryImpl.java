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

import java.util.Arrays;
import java.util.Collection;

import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.TreeGrid;

import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.models.locations.Location;
import edu.umn.msi.tropix.models.locations.Locations;
import edu.umn.msi.tropix.models.utils.TropixObjectType;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.components.LocationCommandComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponent;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItems;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItemExpander;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItemExpanders;
import edu.umn.msi.tropix.webgui.client.mediators.LocationUpdateMediator;
import edu.umn.msi.tropix.webgui.client.mediators.LocationUpdateMediator.UpdateEvent;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.widgets.Buttons;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.services.object.ObjectService;

public class MoveCommandComponentFactoryImpl implements LocationCommandComponentFactory<Command> {
  private TreeComponentFactory treeComponentFactory;
  private LocationFactory locationFactory;

  @Inject
  public void setLocationFactory(final LocationFactory locationFactory) {
    this.locationFactory = locationFactory;
  }

  @Inject
  public void setTreeComponentFactory(final TreeComponentFactory treeComponentFactory) {
    this.treeComponentFactory = treeComponentFactory;
  }

  public boolean acceptsLocations(final Collection<TreeItem> treeItems) {
    if(!(treeItems != null 
         && treeItems.size() > 0 
         && TreeItems.allTropixObjectTreeItemsWithSameRoot(treeItems)
         && TreeItems.allParentsAreFolder(treeItems))) {
     return false;
    }
    final Location rootItem = treeItems.iterator().next().getRoot();
    // Don't let you delete from searches, recent activity, etc...
    return rootItem instanceof TropixObjectTreeItem || Locations.isMySharedFoldersItem(rootItem) || Locations.isMyGroupFoldersItem(rootItem);
  }

  public Command get(final Collection<TreeItem> treeItems) {
    return new MoveWindowComponent(treeItems, locationFactory, treeComponentFactory);
  }

  static class MoveWindowComponent extends WindowComponentImpl<Window> {
    private TropixObjectTreeItem selectedTreeItem = null;

    MoveWindowComponent(final Collection<TreeItem> treeItems, final LocationFactory locationFactory, final TreeComponentFactory treeComponentFactory) {
      final Button okButton = Buttons.getOkButton();
      final TropixObjectTreeItem itemToMove = (TropixObjectTreeItem) treeItems.iterator().next();
      final TreeOptions treeOptions = new TreeOptions();
      final TropixObject tropixObjectRoot = itemToMove.getTropixObjectTreeItemRoot().getObject();
      final TropixObjectTreeItemExpander expander = TropixObjectTreeItemExpanders.get(new TropixObjectType[] {TropixObjectTypeEnum.VIRTUAL_FOLDER, TropixObjectTypeEnum.FOLDER});
      final TreeItem moveRoot = locationFactory.getTropixObjectTreeItem(null, tropixObjectRoot, expander);
      treeOptions.setInitialItems(Arrays.asList(moveRoot));
      final TreeComponent treeComponent = treeComponentFactory.get(treeOptions);
      okButton.setDisabled(this.selectedTreeItem == null);
      treeComponent.addSelectionListener(new Listener<TreeItem>() {
        public void onEvent(final TreeItem treeItem) {
          if(treeItem instanceof TropixObjectTreeItem) {
            MoveWindowComponent.this.selectedTreeItem = (TropixObjectTreeItem) treeItem;
            okButton.setDisabled(MoveWindowComponent.this.selectedTreeItem == null);
          }
        }
      });

      final TreeGrid treeGrid = treeComponent.get();
      treeGrid.setWidth("100%");
      treeGrid.setHeight("100%");

      final Label label = new Label("Select new parent folder for selected object(s)");
      label.setWidth100();
      label.setHeight(12);

      final VLayout layout = new VLayout();
      layout.addMember(label);
      layout.addMember(treeGrid);

      okButton.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          for(final TreeItem treeItem : treeItems) {
            final AsyncCallbackImpl<Void> callback = new AsyncCallbackImpl<Void>() {
              // This gets reset before the callback is executed, so grab
              // selectedTreeItem at
              // construction time.
              private final TreeItem selectedTreeContext = selectedTreeItem;

              @Override
              public void onSuccess(final Void result) {
                if(treeItem.getParent() != null) {
                  LocationUpdateMediator.getInstance().onEvent(new UpdateEvent(treeItem.getParent().getId(), null));
                }
                if(this.selectedTreeContext != null) {
                  LocationUpdateMediator.getInstance().onEvent(new UpdateEvent(this.selectedTreeContext.getId(), null));
                }
              }
            };
            if(tropixObjectRoot instanceof VirtualFolder) {
              ObjectService.Util.getInstance().moveVirtually(treeItem.getId(), treeItem.getParent().getId(), selectedTreeItem.getObject().getId(), callback);
            } else {
              ObjectService.Util.getInstance().move(treeItem.getId(), selectedTreeItem.getObject().getId(), callback);
            }
          }
          get().markForDestroy();
        }
      });
      this.setWidget(PopOutWindowBuilder.titled("Move").withContents(new CanvasWithOpsLayout<VLayout>(layout, okButton)).get());
    }
  }

}
