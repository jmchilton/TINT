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

package edu.umn.msi.tropix.webgui.client.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.inject.Inject;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.HoverCustomizer;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;

import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.mediators.ActionMediator;
import edu.umn.msi.tropix.webgui.client.mediators.ActionMediator.ActionEvent;
import edu.umn.msi.tropix.webgui.client.mediators.LocationActionEventImpl;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.client.widgets.TextBlock;

public class NewItemFolderActionListenerImpl implements Listener<ActionEvent> {
  //private final RootNewItemFolder rootNewItemFolder;
  private final ActionMediator actionMediator;

  @Inject
  public NewItemFolderActionListenerImpl(final ActionMediator actionMediator) {
    //this.rootNewItemFolder = rootNewItemFolder;
    this.actionMediator = actionMediator;
    actionMediator.registerActionListener("itemFolder", this);
  }

  private class NewItemSelectionChangedHandler implements SelectionChangedHandler, ClickHandler {
    private NewItemModel selectedModel;
    private Canvas canvas;
    private Window window;
    private TextBlock block;
    private Collection<TreeItem> selectedItem;

    NewItemSelectionChangedHandler(final Window window, final Canvas canvas, final TextBlock block, final Collection<TreeItem> selectedItem) {
      this.canvas = canvas;
      this.window = window;
      this.block = block;
      canvas.setDisabled(this.selectedModel == null);
      canvas.addClickHandler(this);
    }

    public void onSelectionChanged(final SelectionEvent event) {
      final NewItemModel selection = (NewItemModel) event.getRecord().getAttributeAsObject("model");
      if(event.getState() && selection != null && !(selection instanceof NewItemFolderImpl)) {
        this.selectedModel = selection;
        this.block.setText(selection.getDescription());
      } else {
        this.selectedModel = null;
        this.block.setText("");
      }
      this.canvas.setDisabled(this.selectedModel == null);
    }

    public void onClick(final ClickEvent event) {
      final NewItemModel selectedModel = this.selectedModel;
      this.window.destroy();
      actionMediator.handleEvent(LocationActionEventImpl.forItems("newItem" + selectedModel.getName(), selectedItem));
    }
  }

  public void onEvent(final ActionEvent actionEvent) { // final Collection<TreeItem> selectedItem, final NewItemModel selectedModel) {
    final NewItemFolderActionEventImpl event = (NewItemFolderActionEventImpl) actionEvent;

    final Dialog window = new Dialog();
    window.setShowToolbar(true);
    final Button okButton = SmartUtils.getButton("Ok", Resources.OK);
    okButton.setID("NewItemFolder_Button_Ok");
    okButton.disable();

    final TextBlock block = new TextBlock("Description");
    block.setHeight("25%");
    block.setWidth100();

    window.setToolbarButtons(okButton);

    window.setSize("500", "500");
    window.setTitle("New...");
    window.setCanDragReposition(true);
    window.setCanDragResize(true);
    window.centerInPage();
    window.setIsModal(true);

    final Tree tree = new Tree();
    tree.setOpenProperty("open");
    tree.setNameProperty("name");
    tree.setIdField("name");
    tree.setRoot(this.getTreeNode(event.getRootItemFolder(), new ArrayList<TreeNode>(), event.getNewItemFolder()));
    final TreeGrid treeGrid = new TreeGrid();
    treeGrid.setHeight("75%");
    treeGrid.setWidth100();
    treeGrid.setData(tree);
    treeGrid.setShowHeader(false);
    final TreeGridField field = new TreeGridField();
    field.setHoverCustomizer(new HoverCustomizer() {
      public String hoverHTML(final Object value, final ListGridRecord record, final int rowNum, final int colNum) {
        return record.getAttribute("description");
      }
    });
    field.setShowHover(true);
    field.setName("name");
    treeGrid.addSelectionChangedHandler(new NewItemSelectionChangedHandler(window, okButton, block, event.getItems()));
    treeGrid.setFields(field);

    final VLayout layout = new VLayout();
    layout.addMember(treeGrid);
    layout.addMember(block);
    window.addItem(layout);
    window.show();
  }

  public TreeNode getTreeNode(final NewItemFolder newItemsFolder, final List<TreeNode> parentNodes, final NewItemModel selectedModel) {
    final TreeNode treeNode = new TreeNode();
    if(newItemsFolder == selectedModel) {
      treeNode.setAttribute("open", true);
      for(final TreeNode parentNode : parentNodes) {
        parentNode.setAttribute("open", true);
      }
    } else {
      treeNode.setAttribute("open", false);
    }
    treeNode.setName(newItemsFolder.getName());
    treeNode.setAttribute("description", newItemsFolder.getDescription());
    final TreeNode[] children = new TreeNode[newItemsFolder.getChildren().size()];
    int i = 0;
    for(final NewItemModel newItemModel : newItemsFolder.getChildren()) {
      TreeNode childNode;
      if(newItemModel instanceof NewItemFolderImpl) {
        parentNodes.add(treeNode);
        childNode = this.getTreeNode((NewItemFolderImpl) newItemModel, parentNodes, selectedModel);
        parentNodes.remove(treeNode);
      } else {
        childNode = new TreeNode();
        childNode.setName(newItemModel.getName());
        childNode.setAttribute("model", newItemModel);
        childNode.setAttribute("description", newItemModel.getDescription());
      }

      children[i++] = childNode;
    }
    treeNode.setChildren(children);
    return treeNode;
  }

}
