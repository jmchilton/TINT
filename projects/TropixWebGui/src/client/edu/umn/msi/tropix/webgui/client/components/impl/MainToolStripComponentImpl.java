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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.MenuItemIfFunction;
import com.smartgwt.client.widgets.menu.MenuItemSeparator;
import com.smartgwt.client.widgets.menu.events.ClickHandler;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.toolbar.ToolStripMenuButton;

import edu.mayo.mprc.dbcurator.client.CurationEditor;
import edu.mayo.mprc.dbcurator.client.EditorCloseCallback;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.Session;
import edu.umn.msi.tropix.webgui.client.components.LocationCommandComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.MainToolStripComponent;
import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponent;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.constants.DomConstants;
import edu.umn.msi.tropix.webgui.client.mediators.ActionMediator;
import edu.umn.msi.tropix.webgui.client.mediators.LocationActionEventImpl;
import edu.umn.msi.tropix.webgui.client.mediators.LoginMediator;
import edu.umn.msi.tropix.webgui.client.mediators.NavigationSelectionMediator;
import edu.umn.msi.tropix.webgui.client.mediators.NavigationSelectionMediator.NavigationSelection;
import edu.umn.msi.tropix.webgui.client.mediators.SimpleActionEventImpl;
import edu.umn.msi.tropix.webgui.client.models.NewItemFolder;
import edu.umn.msi.tropix.webgui.client.models.NewItemFolderActionEventImpl;
import edu.umn.msi.tropix.webgui.client.models.NewItemFolderImpl;
import edu.umn.msi.tropix.webgui.client.models.NewItemModel;
import edu.umn.msi.tropix.webgui.client.models.RootNewItemFolder;
import edu.umn.msi.tropix.webgui.client.modules.GalaxyModuleInstallerImpl;
import edu.umn.msi.tropix.webgui.client.modules.ModuleInstaller;
import edu.umn.msi.tropix.webgui.client.modules.ModuleManager;
import edu.umn.msi.tropix.webgui.client.utils.Iterables;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.session.Module;

public class MainToolStripComponentImpl implements MainToolStripComponent, Listener<NavigationSelection> {
  private static final MenuItemSeparator SEPARATOR = new MenuItemSeparator();
  private ModuleManager moduleManager;
  private Collection<TreeItem> treeItem = new ArrayList<TreeItem>(0);
  private NewItemFolder rootNewItemFolder;
  private TreeComponent mainTreeComponent;
  private NavigationSelectionMediator navigationSelectionMediator;
  private LocationCommandComponentFactory<? extends Command> moveCommandComponentFactory, deleteCommandComponentFactory,
      renameCommandComponentFactory;
  private ActionMediator actionMediator;
  private LocationFactory locationFactory;
  private LoginMediator loginMediator;
  private GalaxyModuleInstallerImpl galaxyInstaller;
  private Session session;

  @Inject
  public void setSession(final Session session) {
    this.session = session;
  }

  @Inject
  public void setLoginMediator(final LoginMediator loginMediator) {
    this.loginMediator = loginMediator;
  }

  @Inject
  public void setModuleManager(final ModuleManager moduleManager) {
    this.moduleManager = moduleManager;
  }

  @Inject
  public void setLocationFactory(final LocationFactory locationFactory) {
    this.locationFactory = locationFactory;
  }

  @Inject
  public void setActionMediator(final ActionMediator actionMediator) {
    this.actionMediator = actionMediator;
  }

  @Inject
  public void setRenameCommandComponentFactory(@Named("rename") final LocationCommandComponentFactory<? extends Command> renameCommandComponentFactory) {
    this.renameCommandComponentFactory = renameCommandComponentFactory;
  }

  public void setMainTreeComponent(final TreeComponent mainTreeComponent) {
    this.mainTreeComponent = mainTreeComponent;
  }

  @Inject
  public void setRootNewItemFolder(final RootNewItemFolder rootNewItemFolder) {
    this.rootNewItemFolder = rootNewItemFolder;
  }

  @Inject
  public void setNavigationSelectionMediator(final NavigationSelectionMediator mediator) {
    this.navigationSelectionMediator = mediator;
    mediator.addNavigationSelectionChangedListener(this);
  }

  @Inject
  public void setGalaxyInstaller(@Named("galaxyInstaller") final ModuleInstaller galaxyModuleInstaller) {
    this.galaxyInstaller = (GalaxyModuleInstallerImpl) galaxyModuleInstaller;
  }

  private static class TitledMenu extends Menu {
    private final String menuTitle;

    TitledMenu(final String title) {
      this.menuTitle = title;
      this.setShowShadow(true);
      this.setShadowDepth(3);
    }

    String getMenuTitle() {
      return menuTitle;
    }
  }

  private static class MenuBuilder {
    private final ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
    private boolean lastItemSeparator = true;

    void addSeparator() {
      if(!lastItemSeparator) {
        menuItems.add(SEPARATOR);
        lastItemSeparator = true;
      }
    }

    void addMenuItem(final MenuItem menuItem) {
      menuItems.add(menuItem);
      lastItemSeparator = false;
    }

    TitledMenu buildMenu(final String id, @Nullable final String title) {
      if(!hasItems()) {
        return null;
      }

      final TitledMenu menu = new TitledMenu(title);

      if(title != null) {
        setTitle(title, menu);
      }
      if(lastItemSeparator) {
        menuItems.remove(menuItems.size() - 1);
      }

      menu.setItems(menuItems.toArray(new MenuItem[menuItems.size()]));
      return menu;

    }

    private boolean hasItems() {
      return Iterables.any(menuItems, new Predicate<MenuItem>() {
        public boolean apply(final MenuItem menuItem) {
          return menuItem != SEPARATOR;
        }
      });
    }

  }

  private MenuItem getMenuItem(final String text, final String icon, final LocationCommandComponentFactory<? extends Command> commandComponentFactory) {
    return SmartUtils.getMenuItem(text, icon, new Command() {
      public void execute() {
        commandComponentFactory.get(treeItem).execute();
      }
    }, new MenuItemIfFunction() {
      public boolean execute(final Canvas target, final Menu menu, final MenuItem item) {
        return commandComponentFactory.acceptsLocations(treeItem);
      }
    });
  }

  private static void addMenu(final Collection<TitledMenu> menus, @Nullable final TitledMenu menu) {
    if(menu != null) {
      menus.add(menu);
    }
  }

  public ToolStrip get() {
    // final MenuBar menuBar = new MenuBar();
    final ToolStrip toolStrip = new ToolStrip();
    // toolStrip.setBackgroundImage(GWT.getHostPageBaseURL() + "images/menu_background.png");
    // toolStrip.setMargin(0);
    // toolStrip.setHeight(20);

    final Collection<TitledMenu> menus = new ArrayList<TitledMenu>();
    addMenu(menus, getFileMenu());
    addMenu(menus, getProjectMenu());
    addMenu(menus, getSearchMenu());
    addMenu(menus, getToolsAdmin());
    addMenu(menus, getAdminMenu());
    addMenu(menus, getHelpMenu());

    final ToolStripButton logoutButton = new ToolStripButton("Logout");
    logoutButton.setIcon(Resources.SHUTDOWN);
    logoutButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
      public void onClick(final ClickEvent event) {
        loginMediator.attemptLogout();
      }
    });

    // logoutButton.setShowShadow(false);
    // logoutButton.setBorder("0px solid black");
    // logoutButton.setHeight(20);
    logoutButton.setIconOrientation("right");
    // logoutButton.setMargin(0);
    // rightLayout.addMember(logoutButton);
    // toolStrip.addMember(menuBar);
    for(TitledMenu menu : menus) {
      final ToolStripMenuButton menuButton = new ToolStripMenuButton(menu.getMenuTitle(), menu);
      menuButton.setWidth("100px");
      toolStrip.addMenuButton(menuButton);
    }
    toolStrip.addFill();
    toolStrip.addButton(logoutButton);
    // toolStrip.addMember(rightLayout);
    toolStrip.setWidth100();
    return toolStrip;
  }

  private TitledMenu getHelpMenu() {
    final MenuBuilder menuBuilder = new MenuBuilder();
    menuBuilder.addMenuItem(getDialogMenuItem("About", "about", Resources.HELP));
    return menuBuilder.buildMenu(DomConstants.HELP_MENU_ID, "Help");
  }
  
  
  private void popupDbCurator() {

    Map<String, String> emailInitialPairs = new TreeMap<String, String>();
    emailInitialPairs.put(session.getUserName(), session.getUserName());
    final Layout panel =  SmartUtils.getFullVLayout();
    
    PopOutWindowBuilder.titled("foo").sized(600, 600).modal().withContents(panel).asCommand().execute();
    
    //final DialogBox dialogBox = new DialogBox(false);
    //dialogBox.setHTML("<p>MooCow</p>");
    CurationEditor ce = new CurationEditor(null, session.getUserName(), emailInitialPairs, new EditorCloseCallback() {
      public void editorClosed(final Integer openCurationID) {
        //dialogBox.hide();
      }
    });
    ce.setSize("550px", "550px");
    panel.addMember(ce);
    //DOM.setElementAttribute(panel.getElement(), "id", "db-curator");
    //dialogBox.setStyleName("dbCuratorEmbed");
    //dialogBox.setWidget(ce);
    //dialogBox.setSize(Window.getClientWidth() * .8 + "px", Window.getClientHeight() * .8 + "px");
    //ce.setPixelSize(Math.max((int) (Window.getClientWidth() * .8), 770), (int) (Window.getClientHeight() * .8));
//    LightBox lb = new LightBox(dialogBox);
//    try {
//      lb.show();
//    } catch (Exception ignore) {
    //dialogBox.show();
//    }
    //dialogBox.center();
  }

  

  private TitledMenu getToolsAdmin() {
    final MenuBuilder menuBuilder = new MenuBuilder();
    if(moduleManager.containsModules(Module.GALAXY)) {
      final NewItemFolderImpl folder = new NewItemFolderImpl("Root", "Root new folder");
      galaxyInstaller.installToolItemModels(session, folder);
      final Menu toolsMenu = getNewItemMenu(folder.getChildren(), folder);
      final MenuItem newItem = new MenuItem();
      newItem.setSubmenu(toolsMenu);
      newItem.setTitle("Galaxy Tools...");
      newItem.setIcon(Resources.ANALYSIS_16);
      menuBuilder.addMenuItem(newItem);
      menuBuilder.addSeparator();
    }
    if(moduleManager.containsModules(Module.USER, Module.PROTIP)) {
      final MenuItem dbCuratortem = new MenuItem();
      dbCuratortem.setTitle("Database Curator");
      dbCuratortem.setIcon(Resources.DATABASE_16);
      dbCuratortem.addClickHandler(new ClickHandler() {

        public void onClick(final MenuItemClickEvent event) {
          popupDbCurator();
        }
        
      });
      menuBuilder.addMenuItem(dbCuratortem);
      menuBuilder.addSeparator();
    }
    if(moduleManager.containsModules(Module.USER, Module.REQUEST)) {
      menuBuilder.addMenuItem(getDialogMenuItem("Manage Service Providers", "manageCatalogProviders", Resources.PARAMETERS_16));
    }
    if(moduleManager.containsModules(Module.USER, Module.LOCAL)) {
      menuBuilder.addMenuItem(getDialogMenuItem("Change Password", "changePassword", Resources.EDIT));
    }
    return menuBuilder.buildMenu(DomConstants.TOOLS_ADMIN_MENU_ID, "Tools");
  }

  private TitledMenu getAdminMenu() {
    final MenuBuilder menuBuilder = new MenuBuilder();
    if(moduleManager.containsModules(Module.ADMIN)) {
      menuBuilder.addMenuItem(getDialogMenuItem("Manage Users", "userAdmin", Resources.PARAMETERS_16));
      menuBuilder.addMenuItem(getDialogMenuItem("Manage Groups", "groupAdmin", Resources.PARAMETERS_16));
      menuBuilder.addMenuItem(getDialogMenuItem("Manage Group Folders", "manageGroupFolders", Resources.PARAMETERS_16));
      menuBuilder.addMenuItem(getDialogMenuItem("Create Local User", "createLocalUser", Resources.ADD));
      menuBuilder.addSeparator();
      menuBuilder.addMenuItem(getDialogMenuItem("Manage File Types", "manageFileTypes", Resources.PARAMETERS_16));
      if(moduleManager.containsModules(Module.CATALOG)) {
        menuBuilder.addSeparator();
        menuBuilder.addMenuItem(getDialogMenuItem("Catalog Admin", "catalogAdmin", Resources.PARAMETERS_16));
      }
      menuBuilder.addSeparator();
      menuBuilder.addMenuItem(getDialogMenuItem("Manage Galaxy Tools", "manageGalaxyTools", Resources.PARAMETERS_16));

      final MenuItem consoleMenuItem = new MenuItem("Show Smart Console");
      consoleMenuItem.addClickHandler(new ClickHandler() {
        public void onClick(final MenuItemClickEvent event) {
          SC.showConsole();
        }
      });
      menuBuilder.addSeparator();
      menuBuilder.addMenuItem(consoleMenuItem);
    }
    return menuBuilder.buildMenu(DomConstants.ADMIN_MENU_ID, "Admin");
  }

  private ClickHandler getDialogLaunchClickHandler(final String actionType) {
    return new ClickHandler() {
      public void onClick(final MenuItemClickEvent event) {
        actionMediator.handleEvent(SimpleActionEventImpl.forType(actionType));
      }
    };
  }

  private MenuItem getDialogMenuItem(final String title, final String actionType, final String icon) {
    final MenuItem menuItem = new MenuItem(title);
    menuItem.addClickHandler(this.getDialogLaunchClickHandler(actionType));
    if(icon != null) {
      menuItem.setIcon(icon);
    }
    return menuItem;
  }

  private TitledMenu getFileMenu() {
    final MenuBuilder menuBuilder = new MenuBuilder();
    if(moduleManager.containsModules(Module.USER)) {
      final Menu newMenu = getNewItemMenu(rootNewItemFolder.getChildren(), rootNewItemFolder);
      final MenuItem newItem = new MenuItem();
      newItem.setSubmenu(newMenu);
      newItem.setTitle("New...");
      newItem.setIcon(Resources.FOLDER_NEW);
      menuBuilder.addMenuItem(newItem);
      menuBuilder.addSeparator();
      menuBuilder.addMenuItem(getMenuItem("Rename...", Resources.EDIT_CLEAR, renameCommandComponentFactory));
      menuBuilder.addMenuItem(getMenuItem("Move...", Resources.FOLDER, moveCommandComponentFactory));
      menuBuilder.addMenuItem(getMenuItem("Delete...", Resources.CROSS, deleteCommandComponentFactory));
      menuBuilder.addSeparator();

      final MenuBuilder exportMenuBuilder = new MenuBuilder();
      final MenuItem bulkDownloadItem = getDialogMenuItem("Download as Zip", "bulkDownload", Resources.SAVE);
      exportMenuBuilder.addMenuItem(bulkDownloadItem);

      if(moduleManager.containsModules(Module.PROTIP)) {
        final MenuItem bulkMgfMascotDownloadItem = getDialogMenuItem("Download Peak Lists for Mascot", "bulkMgfMascotDownload", Resources.SAVE);
        exportMenuBuilder.addMenuItem(bulkMgfMascotDownloadItem);

        final MenuItem bulkMgfProteinPilotDownloadItem = getDialogMenuItem("Download Peak Lists for ProteinPilot", "bulkMgfProteinPilotDownload",
            Resources.SAVE);
        exportMenuBuilder.addMenuItem(bulkMgfProteinPilotDownloadItem);
      }

      final MenuItem gridFtpItem = getDialogMenuItem("Export via GridFTP", "gridFtpExport", Resources.SAVE);
      exportMenuBuilder.addMenuItem(gridFtpItem);

      final MenuItem exportItem = new MenuItem("Export");
      exportItem.setSubmenu(exportMenuBuilder.buildMenu(DomConstants.EXPORT_MENU_ID, null));
      menuBuilder.addMenuItem(exportItem);
    }

    return menuBuilder.buildMenu(DomConstants.ADMIN_MENU_ID, "File");
  }

  private TitledMenu getProjectMenu() {
    final MenuBuilder menuBuilder = new MenuBuilder();
    final MenuItem refreshItem = new MenuItem("Refresh");
    refreshItem.addClickHandler(new ClickHandler() {
      public void onClick(final MenuItemClickEvent event) {
        mainTreeComponent.reset();
        navigationSelectionMediator.onEvent(new NavigationSelection(locationFactory.getTropixHomeItem()));
      }
    });
    refreshItem.setIcon(Resources.REFRESH);
    menuBuilder.addMenuItem(refreshItem);
    return menuBuilder.buildMenu(DomConstants.PROJECT_MENU_ID, "Project");
  }

  private static void setTitle(final String title, final Menu menu) {
    menu.setTitle(title);
    // final int titleLength = title.length();
    // final int width = 50 + 8 * titleLength;
    // menu.setWidth(width);
  }

  private TitledMenu getSearchMenu() {
    final MenuBuilder menuBuilder = new MenuBuilder();
    if(moduleManager.containsModules(Module.SHARING)) {
      menuBuilder.addMenuItem(getDialogMenuItem("Find Shared Folders", "findSharedFolders", Resources.FIND));
    }
    if(moduleManager.containsModules(Module.LOCAL_SEARCH)) {
      menuBuilder.addSeparator();
      menuBuilder.addMenuItem(getDialogMenuItem("Search", "search", Resources.FIND));
      menuBuilder.addMenuItem(getDialogMenuItem("Quick Search", "quickSearch", Resources.FIND));
    }
    if(moduleManager.containsModules(Module.CATALOG)) {
      menuBuilder.addSeparator();
      menuBuilder.addMenuItem(getDialogMenuItem("Quick Lab Service Search", "quickCatalogSearch", Resources.FIND));
      menuBuilder.addMenuItem(getDialogMenuItem("Lab Service Search", "catalogSearch", Resources.FIND));
    }
    if(moduleManager.containsModules(Module.GRID_SEARCH)) {
      menuBuilder.addSeparator();
      menuBuilder.addMenuItem(getDialogMenuItem("Grid Search", "gridSearch", Resources.FIND));
    }
    return menuBuilder.buildMenu(DomConstants.SEARCH_MENU_ID, "Search");
  }

  private static final Comparator<NewItemModel> NEW_ITEM_MODEL_COMPARATOR = new Comparator<NewItemModel>() {

    public int compare(final NewItemModel left, final NewItemModel right) {
      if((left instanceof NewItemFolder && right instanceof NewItemFolder)
          || !(left instanceof NewItemFolder || right instanceof NewItemFolder)) {
        return left.getName().compareTo(right.getName());
      } else if(left instanceof NewItemFolder) {
        return -1;
      } else {
        return 1;
      }
    }

  };

  // TODO: Refactor to MenuItem getNewItemMenuItem(NewItemFolder newItemFolder)
  public Menu getNewItemMenu(final List<NewItemModel> inputList, final NewItemFolder rootNewItemFolder) {
    final ArrayList<NewItemModel> list = new ArrayList<NewItemModel>(inputList);
    Collections.sort(list, NEW_ITEM_MODEL_COMPARATOR);
    final Menu menu = new Menu();
    menu.setCanSelectParentItems(true);
    for(final NewItemModel newItem : list) {
      final MenuItem menuItem = new MenuItem();
      if(!moduleManager.apply(newItem)) {
        continue;
      }
      menuItem.setTitle(newItem.getName());
      if(newItem instanceof NewItemFolderImpl) {
        menuItem.setTitle(menuItem.getTitle() + "...");
        final Menu subMenu = this.getNewItemMenu(((NewItemFolder) newItem).getChildren(), rootNewItemFolder);
        menuItem.setSubmenu(subMenu);
        menuItem.setIcon(Resources.FOLDER_NEW);
      } else {
        menuItem.setIcon(Resources.WINDOW_NEW);
      }
      menuItem.addClickHandler(new ClickHandler() {
        public void onClick(final MenuItemClickEvent event) {
          if(newItem instanceof NewItemFolderImpl) {
            actionMediator.handleEvent(NewItemFolderActionEventImpl.forNewItemFolder((NewItemFolderImpl) newItem, rootNewItemFolder, treeItem));
          } else {
            actionMediator.handleEvent(LocationActionEventImpl.forItems("newItem" + newItem.getName(), treeItem));
          }
        }
      });
      menu.addItem(menuItem);
    }
    return menu;
  }

  public void onEvent(final NavigationSelection navigationSelection) {
    if(navigationSelection != null) {
      this.treeItem = navigationSelection.getLocations();
    } else {
      this.treeItem = null;
    }
  }

  @Inject
  public void setMoveCommandComponentFactory(@Named("move") final LocationCommandComponentFactory<? extends Command> moveCommandComponentFactory) {
    this.moveCommandComponentFactory = moveCommandComponentFactory;
  }

  @Inject
  public void setDeleteCommandComponentFactory(@Named("delete") final LocationCommandComponentFactory<? extends Command> deleteCommandComponentFactory) {
    this.deleteCommandComponentFactory = deleteCommandComponentFactory;
  }

}
