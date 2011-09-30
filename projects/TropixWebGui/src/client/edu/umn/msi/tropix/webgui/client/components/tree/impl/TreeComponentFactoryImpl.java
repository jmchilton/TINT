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

package edu.umn.msi.tropix.webgui.client.components.tree.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.widgets.events.DragStartEvent;
import com.smartgwt.client.widgets.events.DragStartHandler;
import com.smartgwt.client.widgets.events.DragStopEvent;
import com.smartgwt.client.widgets.events.DragStopHandler;
import com.smartgwt.client.widgets.events.DropEvent;
import com.smartgwt.client.widgets.events.DropHandler;
import com.smartgwt.client.widgets.events.DropOutEvent;
import com.smartgwt.client.widgets.events.DropOutHandler;
import com.smartgwt.client.widgets.events.DropOverEvent;
import com.smartgwt.client.widgets.events.DropOverHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.ClickHandler;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.smartgwt.client.widgets.tree.events.FolderOpenedEvent;
import com.smartgwt.client.widgets.tree.events.FolderOpenedHandler;

import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.components.impl.SelectionComponentBaseImpl;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponent;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItems;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions.SelectionType;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeState;
import edu.umn.msi.tropix.webgui.client.mediators.LocationUpdateMediator;
import edu.umn.msi.tropix.webgui.client.mediators.LocationUpdateMediator.AddUpdateEvent;
import edu.umn.msi.tropix.webgui.client.mediators.LocationUpdateMediator.RemoveUpdateEvent;
import edu.umn.msi.tropix.webgui.client.mediators.LocationUpdateMediator.UpdateEvent;
import edu.umn.msi.tropix.webgui.client.smart.handlers.CommandSelectionChangedHandlerImpl;
import edu.umn.msi.tropix.webgui.client.utils.Iterables;
import edu.umn.msi.tropix.webgui.client.utils.Listener;

public class TreeComponentFactoryImpl implements TreeComponentFactory {
  private final LocationUpdateMediator locationUpdateMediator = LocationUpdateMediator.getInstance();
  private Supplier<TreeOptions> defaultTreeOptionsSupplier;
  private static TreeComponentImpl dragSource;

  public void setDefaultTreeOptionsSupplier(final Supplier<TreeOptions> defaultTreeOptionsSupplier) {
    this.defaultTreeOptionsSupplier = defaultTreeOptionsSupplier;
  }

  @SuppressWarnings("unused")
  // Its used from GWT "native" code.
  private static Collection<TreeNode> getTreeNodes(final JavaScriptObject jsObj) {
    final JavaScriptObject[] jsArr = jsObj == null ? new JavaScriptObject[0] : JSOHelper.toArray(jsObj);
    final ArrayList<TreeNode> nodes = new ArrayList<TreeNode>(jsArr.length);
    for(final JavaScriptObject nativeObject : jsArr) {
      nodes.add(TreeNode.getOrCreateRef(nativeObject));
    }
    return nodes;
  }

  private class TreeComponentImpl extends SelectionComponentBaseImpl<TreeItem, TreeGrid> implements TreeComponent {
    private List<TreeState> initialState;
    private Iterable<String> expandIds;
    private Iterable<TreeItem> selectedItems;
    private Tree tree;
    private TreeNode contextNode = null;
    private final Predicate<TreeItem> selectionPredicate, showPredicate;
    private final boolean acceptsDrops;
    private final SelectionType selectionType;

    private final Listener<UpdateEvent> updateListener = new Listener<UpdateEvent>() {
      public void onEvent(final UpdateEvent event) {
        if(event instanceof RemoveUpdateEvent) {
          delete(event.getLocationId());
        } else if(event instanceof AddUpdateEvent) {
          reload(((AddUpdateEvent) event).getParentLocationId());
        } else {
          final String locationId = event.getLocationId();
          if(locationId != null) {
            reload(locationId);
          }
        }
      }
    };

    @Override
    public TreeItem getSelection() {
      final ListGridRecord record = this.get().getSelectedRecord();
      TreeItem selection = null;
      if(record != null) {
        selection = (TreeItem) record.getAttributeAsObject(TreeItems.ATTRIBUTE_OBJECT);
      }
      return selection;
    }

    @Override
    public List<TreeItem> getMultiSelection() {
      final ListGridRecord[] records = this.get().getSelection();
      final ArrayList<TreeItem> items = new ArrayList<TreeItem>(records == null ? 0 : records.length);
      if(records != null) {
        for(final ListGridRecord record : records) {
          items.add((TreeItem) record.getAttributeAsObject(TreeItems.ATTRIBUTE_OBJECT));
        }
      }
      return items;
    }

    public native Collection<TreeNode> findAllNodesWithId(final String id) /*-{
                                                                           var jsTree = this.@edu.umn.msi.tropix.webgui.client.components.tree.impl.TreeComponentFactoryImpl$TreeComponentImpl::tree.@com.smartgwt.client.core.BaseClass::getOrCreateJsObj()();
                                                                           var nodesJs = jsTree.findAll("id", id);
                                                                           return @edu.umn.msi.tropix.webgui.client.components.tree.impl.TreeComponentFactoryImpl::getTreeNodes(Lcom/google/gwt/core/client/JavaScriptObject;)(nodesJs);
                                                                           }-*/;

    private class CustomTreeGrid extends TreeGrid {
      private final TreeComponentImpl treeComponent;

      CustomTreeGrid(final TreeComponentImpl treeComponent) {
        this.treeComponent = treeComponent;
      }

      @Override
      public void onDestroy() {
        locationUpdateMediator.removeListener(TreeComponentImpl.this.updateListener);
      }
    }

    private void copySelection(final TreeComponentImpl otherTreeComponent) {
      Iterable<TreeItem> selectedItems = otherTreeComponent.getMultiSelection();
      if(this.selectionPredicate != null) {
        selectedItems = Iterables.filter(otherTreeComponent.getMultiSelection(), this.selectionPredicate);
      }
      this.selectedItems = selectedItems;
      this.expandIds = TreeItems.getAncestorIds(selectedItems);
      this.expandAndSelect();
    }

    public TreeComponentImpl(final TreeOptions options) {
      this.acceptsDrops = options.isAcceptsDrops();
      this.expandIds = options.getExpandIds();
      this.selectedItems = options.getSelectedItems();
      this.selectionPredicate = options.getSelectionPredicate();
      this.showPredicate = options.getShowPredicate();
      this.selectionType = options.getSelectionType();

      initTreeWidget();
      initContextMenu();
      initFolderOpenedHandler();
      initSelectionPredicate(options);
      initSelectionChangedHandler();
      initDragAndDrop();
      initFields();
      locationUpdateMediator.addListener(updateListener);
      initTreeData();
      initInitialState(options);
    }

    private void initTreeWidget() {
      setWidget(new CustomTreeGrid(this));
      get().setShowHeaderMenuButton(true);
      get().setShowHeaderContextMenu(true);
      get().setShowRoot(false);
      get().setSelectionType(this.selectionType.getSmartSelectionStyle());
      get().setShowOpenIcons(false);
      get().setIndentSize(10);
    }

    private void initSelectionChangedHandler() {
      final CommandSelectionChangedHandlerImpl handler =
          new CommandSelectionChangedHandlerImpl(this.getOnSelectionChangedCommand());
      this.get().addSelectionChangedHandler(handler);
    }

    private void initInitialState(final TreeOptions options) {
      options.getInitialTreeState(new AsyncCallbackImpl<List<TreeState>>() {
        public void onSuccess(final List<TreeState> initialState) {
          TreeComponentImpl.this.initialState = initialState;
          get().setSortField(TreeItems.ATTRIBUTE_NAME);
          reset();
        }
      });
    }

    private void initTreeData() {
      tree = new Tree();
      tree.setIdField(TreeItems.ATTRIBUTE_ID);
      tree.setParentIdField(TreeItems.ATTRIBUTE_PARENT_ID);
      tree.setIsFolderProperty(TreeItems.ATTRIBUTE_FOLDER);

      final TreeNode rootNode = new TreeNode();
      rootNode.setIsFolder(true);
      rootNode.setAttribute(TreeItems.ATTRIBUTE_ID, "0");
      tree.setRoot(rootNode);

      get().setData(tree);
    }

    private void initFields() {
      final TreeGridField idField = new TreeGridField(TreeItems.ATTRIBUTE_ID, "Tropix Grid Id");
      idField.setRequired(true);
      idField.setHidden(true);
      idField.setCanSort(false);
      final TreeGridField parentField = new TreeGridField(TreeItems.ATTRIBUTE_PARENT_ID);
      parentField.setRequired(true);
      parentField.setHidden(true);
      parentField.setCanSort(false);
      final TreeGridField nameField = new TreeGridField(TreeItems.ATTRIBUTE_NAME, "Name");
      final TreeGridField typeField = new TreeGridField(TreeItems.ATTRIBUTE_TYPE, "Type");
      typeField.setHidden(true);
      final TreeGridField dateField = new TreeGridField(TreeItems.ATTRIBUTE_CREATION_DATE, "Creation Date");
      dateField.setType(ListGridFieldType.DATE);
      dateField.setHidden(true);
      dateField.setRequired(false);
      get().setFields(nameField, dateField, typeField);
    }

    private void initDragAndDrop() {
      this.get().setDragDataAction(DragDataAction.NONE);
      this.get().addDragStartHandler(new DragStartHandler() {
        public void onDragStart(final DragStartEvent event) {
          dragSource = ((CustomTreeGrid) event.getSource()).treeComponent;
        }
      });
      this.get().addDragStopHandler(new DragStopHandler() {
        public void onDragStop(final DragStopEvent event) {
          dragSource = null;
        }
      });

      if(this.acceptsDrops) {
        this.get().addDropOverHandler(new DropOverHandler() {
          public void onDropOver(final DropOverEvent event) {
            if(hasOtherDragSource()) {
              get().setBorder("2px solid navy");
            }
          }
        });
        get().addDropOutHandler(new DropOutHandler() {
          public void onDropOut(final DropOutEvent event) {
            if(hasOtherDragSource()) {
              get().setBorder("");
            }
          }
        });
        get().addDropHandler(new DropHandler() {
          public void onDrop(final DropEvent event) {
            event.cancel();
            if(hasOtherDragSource()) {
              copySelection(dragSource);
            }
          }
        });
      }
      this.get().setCanAcceptDroppedRecords(true);
      this.get().setCanDragRecordsOut(true);
    }

    private boolean hasOtherDragSource() {
      return dragSource != null && this != dragSource;
    }

    private void initFolderOpenedHandler() {
      this.get().addFolderOpenedHandler(new FolderOpenedHandler() {
        public void onFolderOpened(final FolderOpenedEvent event) {
          loadChildren(event.getNode(), null);
        }
      });
    }

    private void initSelectionPredicate(final TreeOptions options) {
      if(this.selectionPredicate != null) {
        this.get().addSelectionChangedHandler(new SelectionChangedHandler() {
          public void onSelectionChanged(final SelectionEvent event) {
            if(event.getState() && event.getRecord() != null) {
              if(!options.getSelectionPredicate().apply((TreeItem) event.getRecord().getAttributeAsObject(TreeItems.ATTRIBUTE_OBJECT))) {
                TreeComponentImpl.this.get().deselectRecord(event.getRecord());
              }
            }
          }
        });
      }
    }

    private void initContextMenu() {
      // Setup menu
      final Menu contextMenu = new Menu();
      final MenuItem reloadItem = new MenuItem("Reload Item");
      reloadItem.addClickHandler(new ClickHandler() {
        public void onClick(final MenuItemClickEvent event) {
          if(TreeComponentImpl.this.contextNode != null) {
            TreeComponentImpl.this.reload(TreeComponentImpl.this.contextNode);
          }
        }
      });
      final MenuItem resetItem = new MenuItem("Reset Tree");
      resetItem.addClickHandler(new ClickHandler() {
        public void onClick(final MenuItemClickEvent event) {
          TreeComponentImpl.this.reset();
        }
      });
      contextMenu.setItems(reloadItem, resetItem);
      this.get().setContextMenu(contextMenu);

      // setup listener
      this.get().addCellContextClickHandler(new CellContextClickHandler() {
        public void onCellContextClick(final CellContextClickEvent event) {
          TreeComponentImpl.this.contextNode = (TreeNode) event.getRecord();
        }
      });
    }

    public void deselect() {
      this.get().deselectAllRecords();
    }

    private void reload(@Nullable final TreeNode node) {
      if(node == null) {
        return;
      }

      // If its a "Folder" in the Smart sense, refresh the contents...
      if(node.getAttributeAsBoolean(TreeItems.ATTRIBUTE_FOLDER)) {
        this.loadChildren(node, null);
      }

      // Check for rename...
      final TreeItem item = (TreeItem) node.getAttributeAsObject(TreeItems.ATTRIBUTE_OBJECT);
      item.refresh(new AsyncCallbackImpl<Void>() {
        public void onSuccess(final Void ignored) {
          if(item.isValid() && !item.getName().equals(node.getAttribute(TreeItems.ATTRIBUTE_NAME))) {
            node.setAttribute(TreeItems.ATTRIBUTE_NAME, item.getName());
            TreeComponentImpl.this.get().redraw();
          }
        }
      });
    }

    public void delete(final String locationId) {
      this.get().getTree().removeList(this.findAllNodesWithId(locationId).toArray(new TreeNode[0]));
    }

    public void reload(final String objectId) {
      this.reload(this.findAllNodesWithId(objectId));
    }

    private void reload(final Collection<TreeNode> nodes) {
      for(final TreeNode node : nodes) {
        this.reload(node);
      }
    }

    private TreeNode toTreeNode(final TreeItem treeItem) {
      final TreeNode treeNode = new TreeNode();
      treeNode.setAttribute(TreeItems.ATTRIBUTE_ID, treeItem.getId());
      treeNode.setAttribute(TreeItems.ATTRIBUTE_FOLDER, treeItem.isFolder());
      treeNode.setAttribute(TreeItems.ATTRIBUTE_ICON, treeItem.getIcon());
      treeNode.setAttribute(TreeItems.ATTRIBUTE_TYPE, treeItem.getType());
      treeNode.setAttribute(TreeItems.ATTRIBUTE_NAME, treeItem.getName());
      final Date creationDate = treeItem.getCreationDate();
      if(creationDate != null) {
        treeNode.setAttribute(TreeItems.ATTRIBUTE_CREATION_DATE, creationDate);
      }
      treeNode.setAttribute(TreeItems.ATTRIBUTE_OBJECT, treeItem);
      if(treeItem.getParent() != null) {
        treeNode.setAttribute(TreeItems.ATTRIBUTE_PARENT_ID, treeItem.getParent().getId());
      } else {
        treeNode.setAttribute(TreeItems.ATTRIBUTE_PARENT_ID, "0");
      }
      return treeNode;
    }

    /*
     * PRECONDITION: node exists in tree
     */
    private int initNodes(final TreeState state, final TreeNode node) {
      final List<TreeState> childStates = state.getChildStates();
      int sum = 0;
      if(childStates == null || childStates.isEmpty()) {
        return sum;
      }
      final TreeNode[] nodes = new TreeNode[childStates.size()];
      for(int i = 0; i < nodes.length; i++) {
        final TreeState childState = childStates.get(i);
        final TreeItem treeItem = childState.getTreeItem();
        nodes[i] = this.toTreeNode(treeItem);
      }
      sum += nodes.length;
      this.get().getTree().addList(nodes, node);
      this.checkSelection(nodes);
      for(int i = 0; i < nodes.length; i++) {
        final TreeState childState = childStates.get(i);
        sum += this.initNodes(childState, nodes[i]);
      }
      return sum;
    }

    public void reset() {
      final TreeNode rootNode = this.get().getTree().getRoot();
      rootNode.setChildren(null);
      final TreeState rootState = new TreeState();
      rootState.setChildStates(this.initialState);
      final int numNodes = this.initNodes(rootState, rootNode);
      if(numNodes == 1 && (this.expandIds == null || Iterables.isEmpty(this.expandIds))) {
        final TreeNode onlyChild = this.get().getTree().getChildren(rootNode)[0];
        this.expandIds = Arrays.asList(onlyChild.getAttribute(TreeItems.ATTRIBUTE_ID));
      }
      this.expandAndSelect();
    }

    private void expandAndSelect() {
      final TreeNode rootNode = this.get().getTree().getRoot();
      if(this.expandIds != null && !Iterables.isEmpty(this.expandIds)) {
        new Expander(rootNode, this.expandIds.iterator());
      }
    }

    private final class Expander implements AsyncCallback<TreeNode> {
      private final Iterator<String> ids;

      private Expander(final TreeNode rootNode, final Iterator<String> ids) {
        this.ids = ids;
        this.onSuccess(rootNode);
      }

      public void onFailure(final Throwable arg0) {
      }

      public void onSuccess(final TreeNode node) {
        try {
          if(this.ids.hasNext()) {
            final String nextId = this.ids.next();
            // Need to find the nextId to expand it, as an optimization
            // lets check the nodes we just created first, this is consistent with
            // expanding straight down to a node.
            TreeNode nextNode = null;
            for(final TreeNode childNode : TreeComponentImpl.this.get().getTree().getChildren(node)) {
              if(childNode.getAttribute(TreeItems.ATTRIBUTE_ID).equals(nextId)) {
                nextNode = childNode;
                break;
              }
            }
            // If we didn't find it there, broaden the search to whole tree
            if(nextNode == null) {
              nextNode = TreeComponentImpl.this.get().getTree().findById(nextId);
              // Give up if we still cannot find the id
              if(nextNode == null) {
                GWT.log("Unable to expand node corresponding id " + nextId, null);
                return;
              }
            }
            loadChildren(nextNode, this);
            get().getTree().openFolder(nextNode);

            // Check selection in case node was alread opened...
            checkSelection(get().getTree().getChildren(nextNode));
          } // else done with ids
        } catch(final RuntimeException e) {
          e.printStackTrace();
        }
      }
    }

    private HashMap<String, TreeNode> getChildIds(final TreeNode node) {
      final TreeNode[] nodes = this.get().getTree().getChildren(node);
      final HashMap<String, TreeNode> idMap = new HashMap<String, TreeNode>(nodes.length);
      for(final TreeNode childNode : nodes) {
        idMap.put(childNode.getAttribute(TreeItems.ATTRIBUTE_ID), childNode);
      }
      return idMap;
    }

    private boolean validSelection(final TreeItem treeItem) {
      return this.selectionPredicate == null || this.selectionPredicate.apply(treeItem);
    }

    private void checkSelection(final TreeNode[] nodes) {
      if(this.selectedItems != null) {
        // System.out.println("In check selection with " + nodes.length + " nodes.");
        outerfor: for(final TreeNode node : nodes) {
          final TreeItem item = (TreeItem) node.getAttributeAsObject(TreeItems.ATTRIBUTE_OBJECT);
          for(final TreeItem selectedItem : this.selectedItems) {
            final boolean itemAlike = TreeItems.alike(item, selectedItem);
            // System.out.println("Checking selection of " + selectedItem + " alike? " + itemAlike);
            if(itemAlike && this.validSelection(item)) {
              // System.out.println("Selecting a record");
              if(this.selectionType.equals(SelectionType.SINGLE)) {
                this.selectedItems = null;
                this.get().deselectAllRecords();
                this.get().selectRecord(node);
                break outerfor;
              } else {
                this.get().selectRecord(node);
                break;
              }
            }
          }
        }
      } else {
        GWT.log("Selected items is null", null);
      }
    }

    private void loadChildren(final TreeNode node, final AsyncCallback<TreeNode> callback) {
      final TreeItem item = (TreeItem) node.getAttributeAsObject(TreeItems.ATTRIBUTE_OBJECT);
      item.getChildren(new AsyncCallback<List<TreeItem>>() {

        public void onSuccess(final List<TreeItem> items) {
          // Goal here is to keep the existing nodes in tact if the id exists
          // in the new items produced, if the id doesn't exist we want to
          // remove
          // the node. Also we should add all new items.

          Iterable<TreeItem> filteredItems;
          if(TreeComponentImpl.this.showPredicate != null) {
            filteredItems = Iterables.filter(items, TreeComponentImpl.this.showPredicate);
          } else {
            filteredItems = items;
          }

          // Put old nodes and new nodes in a form for efficient comparison.
          final HashMap<String, TreeNode> nodeMap = TreeComponentImpl.this.getChildIds(node); // O(n),
          // n
          // constant
          // time
          // insertions
          final HashMap<String, TreeItem> itemMap = TreeItems.getIdMap(filteredItems); // O(n),
          // n
          // constant
          // time
          // insertion

          // Remove nodes that must be removed
          final LinkedList<TreeNode> toRemove = new LinkedList<TreeNode>(); // O(1)
          for(final String oldId : nodeMap.keySet()) { // Amortized O(n) for
            // iterating
            if(!itemMap.containsKey(oldId)) { // O(1)
              toRemove.add(nodeMap.get(oldId)); // O(1)
            }
          }
          // Not sure of the complexity of the following expression,
          // but the input is as small as I can make it.
          TreeComponentImpl.this.get().getTree().removeList(toRemove.toArray(new TreeNode[0]));

          // Add nodes that must be added
          final LinkedList<TreeNode> toAdd = new LinkedList<TreeNode>();
          for(final String newId : itemMap.keySet()) {
            if(!nodeMap.containsKey(newId)) {
              toAdd.add(TreeComponentImpl.this.toTreeNode(itemMap.get(newId)));
            }
          }
          final TreeNode[] toAddArr = toAdd.toArray(new TreeNode[0]);
          TreeComponentImpl.this.get().getTree().addList(toAddArr, node);

          TreeComponentImpl.this.checkSelection(toAddArr);

          if(callback != null) {
            callback.onSuccess(node);
          }
        }

        public void onFailure(final Throwable throwable) {
          if(callback != null) {
            callback.onFailure(throwable);
          }
        }
      });
    }

  }

  public TreeComponent get() {
    return this.get(null);
  }

  public TreeComponent get(@Nullable final TreeOptions options) {
    TreeOptions treeOptions = options;
    if(treeOptions == null) {
      treeOptions = this.defaultTreeOptionsSupplier.get();
    }
    return new TreeComponentImpl(treeOptions);
  }

}
