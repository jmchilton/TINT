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
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.InternalRequest;
import edu.umn.msi.tropix.models.Request;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.Session;
import edu.umn.msi.tropix.webgui.client.WrappedAsyncCallback;
import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItems;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItemExpander;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItemExpanders;
import edu.umn.msi.tropix.webgui.client.modules.ModuleManager;
import edu.umn.msi.tropix.webgui.client.modules.RequiresModule;
import edu.umn.msi.tropix.webgui.client.modules.RequiresModules;
import edu.umn.msi.tropix.webgui.client.search.SearchController;
import edu.umn.msi.tropix.webgui.client.search.SearchModel;
import edu.umn.msi.tropix.webgui.client.utils.Iterables;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.services.object.FolderService;
import edu.umn.msi.tropix.webgui.services.object.ObjectService;
import edu.umn.msi.tropix.webgui.services.object.SearchResult;
import edu.umn.msi.tropix.webgui.services.session.Module;
import edu.umn.msi.tropix.webgui.services.tropix.RequestService;

public class LocationFactoryImpl implements LocationFactory {
  private final ModuleManager moduleManager;
  private Session session;

  @Inject
  public void setSession(final Session session) {
    this.session = session;
  }

  @Inject
  public LocationFactoryImpl(final ModuleManager moduleManager) {
    this.moduleManager = moduleManager;
  }

  public TreeItem getMySharedFoldersItem(final TropixObjectTreeItemExpander tropixObjectTreeItemExpander) {
    return new MySharedFoldersItemImpl(null, tropixObjectTreeItemExpander);
  }

  public TreeItem getMyGroupFoldersItem(final TropixObjectTreeItemExpander tropixObjectTreeItemExpander) {
    return new MyGroupFoldersItemImpl(null, tropixObjectTreeItemExpander);
  }

  public TreeItem getTropixHomeItem() {
    return new TreeItemImpl(null) {
      {
        this.setId(TreeItems.TROPIX_HOME_ID);
        this.setIcon(Resources.OBJECT_16);
        this.setType("");
        this.setName("Tropix Home");
        this.setFolder(false);
      }

      public void getChildren(final AsyncCallback<List<TreeItem>> children) {
      }

    };
  }

  public TropixObjectTreeItem getTropixObjectTreeItem(@Nullable final TreeItem parent, final TropixObject object,
      final TropixObjectTreeItemExpander tropixObjectTreeItemExpander) {
    return new TropixObjectTreeItemImpl(parent, object, tropixObjectTreeItemExpander);
  }

  public TropixObjectTreeItem getTropixObjectTreeItem(final TropixObjectTreeItemImpl parent, final TropixObject object) {
    return new TropixObjectTreeItemImpl(parent, object);
  }

  static class MySharedFoldersItemImpl extends TreeItemImpl implements RequiresModules {
    private final TropixObjectTreeItemExpander tropixObjectTreeItemExpander;
    private final TreeItem treeItem = this;

    MySharedFoldersItemImpl(final TreeItem parent, final TropixObjectTreeItemExpander tropixObjectTreeItemExpander) {
      super(parent);
      this.tropixObjectTreeItemExpander = tropixObjectTreeItemExpander;
      this.setId(TreeItems.MY_SHARED_FOLDERS_ID);
      this.setIcon(Resources.ROOT_SHARED_FOLDER_16);
      this.setType("");
      this.setName("My Shared Folders");
      this.setFolder(true);
    }

    public void getChildren(final AsyncCallback<List<TreeItem>> callback) {
      FolderService.Util.getInstance().getSavedVirtualFolders(new WrappedAsyncCallback<List<TreeItem>, VirtualFolder[]>(callback) {
        public void onSuccess(final VirtualFolder[] folders) {
          final ArrayList<TreeItem> treeItems = new ArrayList<TreeItem>(folders.length);
          for(final VirtualFolder folder : folders) {
            treeItems.add(new TropixObjectTreeItemImpl(treeItem, folder, tropixObjectTreeItemExpander));
          }
          callback.onSuccess(treeItems);
        }
      });
    }

    public boolean isValid() {
      return true;
    }

    public Iterable<Module> requiresModules() {
      return Arrays.asList(Module.SHARING, Module.USER);
    }
  }

  static class MyIncomingRequestsItemImpl extends TreeItemImpl implements RequiresModules {
    private final TreeItem treeItem = this;

    MyIncomingRequestsItemImpl(final TreeItem parent) {
      super(parent);
      this.setId(TreeItems.MY_INCOMING_REQUESTS_ID);
      this.setIcon(Resources.INCOMING_REQUEST_16);
      this.setType("");
      this.setName("My Incoming Requests");
      this.setFolder(true);
    }

    public void getChildren(final AsyncCallback<List<TreeItem>> callback) {
      RequestService.Util.getInstance().getActiveRequests(new WrappedAsyncCallback<List<TreeItem>, Collection<Request>>(callback) {
        public void onSuccess(final Collection<Request> requests) {
          final List<TreeItem> children = new LinkedList<TreeItem>();
          for(final Request request : requests) {
            children.add(new TropixObjectTreeItemImpl(treeItem, request, TropixObjectTreeItemExpanders.get()));
          }
          getWrappedCallback().onSuccess(children);
        }
      });
    }

    public Iterable<Module> requiresModules() {
      return Arrays.asList(Module.REQUEST, Module.USER);
    }
  }

  static class MyOutgoingRequestsItemImpl extends TreeItemImpl implements RequiresModules {
    private final TreeItem treeItem = this;

    MyOutgoingRequestsItemImpl(final TreeItem parent) {
      super(parent);
      this.setId(TreeItems.MY_OUTGOING_REQUESTS_ID);
      this.setIcon(Resources.OUTGOING_REQUEST_16);
      this.setType("");
      this.setName("My Outgoing Requests");
      this.setFolder(true);
    }

    public void getChildren(final AsyncCallback<List<TreeItem>> callback) {
      RequestService.Util.getInstance().getOutgoingRequests(new WrappedAsyncCallback<List<TreeItem>, Collection<InternalRequest>>(callback) {
        public void onSuccess(final Collection<InternalRequest> requests) {
          final List<TreeItem> children = new LinkedList<TreeItem>();
          for(final Request request : requests) {
            children.add(new TropixObjectTreeItemImpl(treeItem, request, null));
          }
          getWrappedCallback().onSuccess(children);
        }
      });
    }

    public Iterable<Module> requiresModules() {
      return Arrays.asList(Module.REQUEST, Module.USER);
    }
  }

  class MyGroupFoldersItemImpl extends TreeItemImpl implements RequiresModule {
    private final TreeItem treeItem = this;
    private final TropixObjectTreeItemExpander tropixObjectTreeItemExpander;

    protected MyGroupFoldersItemImpl(final TreeItem parent, final TropixObjectTreeItemExpander tropixObjectTreeItemExpander) {
      super(parent);
      this.tropixObjectTreeItemExpander = tropixObjectTreeItemExpander;
      this.setId(TreeItems.MY_GROUP_FOLDERS_ID);
      this.setIcon(Resources.SHARED_FOLDER_16);
      this.setType("");
      this.setName("My Group Folders");
      this.setFolder(true);
    }

    public void getChildren(final AsyncCallback<List<TreeItem>> childrenCallback) {
      FolderService.Util.getInstance().getGroupFolders(new WrappedAsyncCallback<List<TreeItem>, List<Folder>>(childrenCallback) {
        public void onSuccess(final List<Folder> folders) {
          final ArrayList<TreeItem> treeItems = new ArrayList<TreeItem>(folders.size());
          for(final Folder folder : folders) {
            treeItems.add(new TropixObjectTreeItemImpl(treeItem, folder, tropixObjectTreeItemExpander));
          }
          childrenCallback.onSuccess(treeItems);
        }
      });
    }

    public Module requiresModule() {
      return Module.USER;
    }

  }

  class MyRecentActivityItemImpl extends TreeItemImpl implements RequiresModule {
    private final TreeItem treeItem = this;

    MyRecentActivityItemImpl(final TreeItem parent) {
      super(parent);
      this.setId(TreeItems.MY_RECENT_ACTIVITY_ID);
      this.setIcon(Resources.RECENT_ACTIVITY_16);
      this.setType("");
      this.setName("My Recent Activity");
      this.setFolder(true);
    }

    public void getChildren(final AsyncCallback<List<TreeItem>> callback) {
      ObjectService.Util.getInstance().loadRecent(20, false, null, new WrappedAsyncCallback<List<TreeItem>, List<TropixObject>>(callback) {
        public void onSuccess(final List<TropixObject> objects) {
          final ArrayList<TreeItem> items = new ArrayList<TreeItem>(objects.size());
          for(final TropixObject object : objects) {
            if(object instanceof Folder || object instanceof VirtualFolder) {
              continue;
            }
            items.add(getTropixObjectTreeItem(treeItem, object, getExpander(null)));
          }
          getWrappedCallback().onSuccess(items);
        }
      });
    }

    public Module requiresModule() {
      return Module.USER;
    }

  }

  class MyRecentSearchesItem extends TreeItemImpl implements RequiresModule {

    MyRecentSearchesItem(final TreeItem parent) {
      super(parent);
      this.setId(TreeItems.MY_RECENT_SEARCHES_ID);
      this.setIcon(Resources.MY_RECENT_SEARCHES_16);
      this.setFolder(true);
      this.setName("My Recent Searches");
      this.setType("");
    }

    public void getChildren(final AsyncCallback<List<TreeItem>> callback) {
      final SearchController searchController = SearchController.getInstance();
      final List<TreeItem> children = new LinkedList<TreeItem>();
      for(final SearchModel searchModel : searchController.getLocalSearchModels()) {
        if(searchModel.isComplete()) {
          children.add(new SearchTreeItemImpl(this, searchModel));
        }
      }
      callback.onSuccess(children);
    }

    public Module requiresModule() {
      return Module.LOCAL_SEARCH;
    }

  }

  class SearchTreeItemImpl extends TreeItemImpl {
    private final SearchModel searchModel;

    SearchTreeItemImpl(final TreeItem parent, final SearchModel searchModel) {
      super(parent);
      this.setId(searchModel.getAsString("id"));
      this.setIcon(Resources.RECENT_SEARCHES_16);
      this.setName(StringUtils.sanitize(searchModel.getAsString("name")));
      this.setType("");
      this.setFolder(true);
      this.searchModel = searchModel;
    }

    public void getChildren(final AsyncCallback<List<TreeItem>> callback) {
      @SuppressWarnings("unchecked")
      final List<SearchResult> results = (List<SearchResult>) this.searchModel.getResults();
      final List<TreeItem> children = new LinkedList<TreeItem>();
      for(final SearchResult result : results) {
        final TropixObject tropixObject = result.getTropixObject();
        if(tropixObject instanceof Folder || tropixObject instanceof VirtualFolder) {
          continue;
        }
        children.add(getTropixObjectTreeItem(this, result.getTropixObject(), getExpander(null)));
      }
      callback.onSuccess(children);
    }

  }

  public TreeItem getMyRecentSearchesItem() {
    return new MyRecentSearchesItem(null);
  }

  public TreeItem getMyRecentActivityItem() {
    return new MyRecentActivityItemImpl(null);
  }

  public TreeItem getOutgoingRequestsItem() {
    return new MyOutgoingRequestsItemImpl(null);
  }

  public TreeItem getIncomingRequestsItem() {
    return new MyIncomingRequestsItemImpl(null);
  }

  public TreeItem getHomeRootItem(@Nullable final TropixObjectTreeItemExpander expander) {
    return getTropixObjectTreeItem(null, session.getUser().getHomeFolder(), getExpander(expander));
  }

  public TreeItem getSharedRootItem(@Nullable final TropixObjectTreeItemExpander expander) {
    return getMySharedFoldersItem(getExpander(expander));
  }

  public TreeItem getGroupFoldersRootItem(@Nullable final TropixObjectTreeItemExpander expander) {
    return getMyGroupFoldersItem(getExpander(expander));
  }

  public Iterable<TreeItem> getAllRootItems(@Nullable final TropixObjectTreeItemExpander expander) {
    return Iterables.filter(Arrays.asList(getHomeRootItem(expander), getSharedRootItem(expander), getMyRecentActivityItem(),
        getMyRecentSearchesItem(), getIncomingRequestsItem(), getOutgoingRequestsItem(), getGroupFoldersRootItem(expander)), moduleManager);
  }

  public Iterable<TreeItem> getTropixObjectSourceRootItems(@Nullable final TropixObjectTreeItemExpander expander) {
    return Iterables.filter(
        Arrays.asList(getHomeRootItem(expander), getSharedRootItem(expander), getGroupFoldersRootItem(expander), getMyRecentActivityItem(),
            getMyRecentSearchesItem()), moduleManager);
  }

  public Iterable<TreeItem> getTropixObjectDestinationRootItems(@Nullable final TropixObjectTreeItemExpander expander) {
    return Iterables.filter(Arrays.asList(getHomeRootItem(expander), getIncomingRequestsItem(), getGroupFoldersRootItem(expander)), moduleManager);
  }

  private TropixObjectTreeItemExpander getExpander(@Nullable final TropixObjectTreeItemExpander expander) {
    return expander == null ? TropixObjectTreeItemExpanders.get() : expander;
  }

}
