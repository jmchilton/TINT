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
import java.util.List;

import com.google.common.base.Supplier;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.client.directory.GridUser;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.models.locations.TropixObjectLocation;
import edu.umn.msi.tropix.models.utils.TropixObjectContext;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.CanvasComponent;
import edu.umn.msi.tropix.webgui.client.components.ComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.PageConfiguration;
import edu.umn.msi.tropix.webgui.client.components.SelectionWindowComponent;
import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItem;
import edu.umn.msi.tropix.webgui.client.constants.PageConstants;
import edu.umn.msi.tropix.webgui.client.mediators.NavigationSelectionMediator;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.ClientListGrid;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.client.widgets.WidgetSupplierImpl;
import edu.umn.msi.tropix.webgui.services.object.ObjectServiceAsync;
import edu.umn.msi.tropix.webgui.services.object.Permission;

public class SharingComponentFactoryImpl implements ComponentFactory<PageConfiguration, CanvasComponent<Layout>> {
  private NavigationSelectionMediator navigationSelectionMediator;
  private LocationFactory locationFactory;
  private Supplier<? extends SelectionWindowComponent<GridUser, ? extends Window>> userSelectionWindowComponentSupplier;
  private Supplier<? extends SelectionWindowComponent<Group, ? extends Window>> groupSelectionWindowComponentSupplier;
  private Supplier<? extends SelectionWindowComponent<TreeItem, ? extends Window>> virtualFolderSelectionWindowComponentSupplier;

  private ObjectServiceAsync objectService;

  @Inject
  public void setLocationFactory(final LocationFactory locationFactory) {
    this.locationFactory = locationFactory;
  }

  @Inject
  public void setObjectService(final ObjectServiceAsync objectService) {
    this.objectService = objectService;
  }

  @Inject
  public void setUserSelectionWindowComponentSupplier(
      final Supplier<? extends SelectionWindowComponent<GridUser, ? extends Window>> userSelectionWindowComponentSupplier) {
    this.userSelectionWindowComponentSupplier = userSelectionWindowComponentSupplier;
  }

  @Inject
  public void setGroupSelectionWindowComponentSupplier(
      final Supplier<? extends SelectionWindowComponent<Group, ? extends Window>> groupSelectionWindowComponentSupplier) {
    this.groupSelectionWindowComponentSupplier = groupSelectionWindowComponentSupplier;
  }

  @Inject
  public void setNavigationSelectionMediator(final NavigationSelectionMediator navigationSelectionMediator) {
    this.navigationSelectionMediator = navigationSelectionMediator;
  }

  @Inject
  public void setVirtualFolderSelectionWindowComponentSupplier(
      @Named("sharedFolder") final Supplier<? extends SelectionWindowComponent<TreeItem, ? extends Window>> virtualFolderSelectionWindowComponentSupplier) {
    this.virtualFolderSelectionWindowComponentSupplier = virtualFolderSelectionWindowComponentSupplier;
  }

  class SharingLayoutHandler extends WidgetSupplierImpl<Layout> implements CanvasComponent<Layout> {
    // private final Label loadingLabel;
    private TropixObjectLocation tropixObjectLocation;
    private final TropixObject tropixObject;
    private final String objectId;
    private ClientListGrid userAndGroupGrid, sharedFoldersGrid;
    private Button removeButton, toogleEditButton, removeFolderButton;
    private boolean canModify;

    private boolean isNonRootSharedFolder() {
      boolean nonRootSharedFolder = false;
      if(this.tropixObject instanceof VirtualFolder) {
        final VirtualFolder virtualFolder = (VirtualFolder) this.tropixObject;
        if(virtualFolder.getRoot() == null || !virtualFolder.getRoot()) {
          nonRootSharedFolder = true;
        }
      }
      return nonRootSharedFolder;
    }

    SharingLayoutHandler(final PageConfiguration pageConfiguration) {
      this.tropixObjectLocation = pageConfiguration.getLocation();
      this.tropixObject = tropixObjectLocation.getObject();
      this.objectId = tropixObject.getId();

      // this.loadingLabel = SmartUtils.smartParagraph(PageConstants.INSTANCE.sharingLoading());
      final VLayout layout = new VLayout();
      // layout.setHeight100();
      layout.setWidth100();
      layout.setAutoHeight();
      layout.setMargin(10);
      layout.setMembersMargin(10);
      this.setWidget(layout);
      // this.get().addMember(this.loadingLabel);
      if(this.isNonRootSharedFolder()) {
        layout.addMember(SmartUtils.smartParagraph(PageConstants.INSTANCE.sharingFoldersInherittedDescription()));
        final Button button = SmartUtils.getButton(PageConstants.INSTANCE.sharingOpenRoot(), Resources.GO, null, null);
        objectService.getRoot(tropixObject.getId(), new AsyncCallbackImpl<TropixObjectContext<VirtualFolder>>() {
          @Override
          public void onSuccess(final TropixObjectContext<VirtualFolder> sharedFolderContext) {
            button.addClickHandler(new ClickHandler() {
              public void onClick(final ClickEvent event) {
                final TropixObjectTreeItem item;
                item = locationFactory.getTropixObjectTreeItem(tropixObjectLocation.getRoot(), sharedFolderContext.getTropixObjectContext(),
                    sharedFolderContext.getTropixObject(), null);
                navigationSelectionMediator.go(item);
              }
            });
            button.setDisabled(false);
          }
        });
        layout.addMember(button);
      } else {
        this.canModify = pageConfiguration.getTropixObjectContext().canModifySharing();
        // TODO: Use canModify to always init, just in a read-only mode if false.
        // TODO: Eliminate sharingNoPermission constant.
        if(pageConfiguration.getTropixObjectContext().canModifySharing()) {
          init();
        } else {
          layout.addMember(SmartUtils.smartParagraph(PageConstants.INSTANCE.sharingNoPermission()));
        }
      }
    }

    private final AsyncCallback<Void> callback = new AsyncCallbackImpl<Void>() {
      @Override
      public void onSuccess(final Void ignored) {
        reload();
      }
    };

    private void init() {
      final DataSourceField nameField = SmartUtils.getFieldBuilder("name", "Name").get();
      final DataSourceField typeField = SmartUtils.getFieldBuilder("type", " ").withWidth(20).image().get();
      final DataSourceField canEditField = SmartUtils.getFieldBuilder("writeIcon", " ").withWidth("35%").image().get();
      final DataSourceField idField = SmartUtils.getHiddenIdField();
      final DataSource dataSource = SmartUtils.newDataSourceWithFields(typeField, nameField, canEditField, idField);
      this.userAndGroupGrid = new ClientListGrid(dataSource);
      this.userAndGroupGrid.setMinHeight(10);
      this.userAndGroupGrid.setAutoFitMaxRecords(3);
      this.userAndGroupGrid.setAutoFitData(Autofit.VERTICAL);

      final Button addUserButton = SmartUtils.getButton(PageConstants.INSTANCE.sharingAddUser(), Resources.PERSON_ABSOLUTE, 140);
      final Button addGroupButton = SmartUtils.getButton(PageConstants.INSTANCE.sharingAddGroup(), Resources.GROUP_ABSOLUTE, 140);
      removeButton = SmartUtils.getButton(PageConstants.INSTANCE.sharingRemovePermission(), Resources.CROSS, 140);
      toogleEditButton = SmartUtils.getButton(PageConstants.INSTANCE.sharingToogleEdit(), Resources.EDIT, 140);

      userAndGroupGrid.addSelectionChangedHandler(new SelectionChangedHandler() {
        public void onSelectionChanged(final SelectionEvent event) {
          final ListGridRecord record = userAndGroupGrid.getSelectedRecord();
          final boolean disabled = record == null ? true : record.getAttributeAsBoolean("immutable");
          removeButton.setDisabled(disabled);
          toogleEditButton.setDisabled(disabled);
        }
      });

      final Canvas userAndGroupLayout = new CanvasWithOpsLayout<ClientListGrid>(this.userAndGroupGrid, addUserButton, addGroupButton,
          this.removeButton, this.toogleEditButton);
      userAndGroupLayout.setIsGroup(true);
      userAndGroupLayout.setAutoHeight();
      userAndGroupLayout.setGroupTitle("Users and Groups");

      if(this.tropixObject instanceof VirtualFolder) {
        this.initUserAndGroupGrid();
        canEditField.setTitle(PageConstants.INSTANCE.sharingFoldersCanEdit());
        addUserButton.addClickHandler(new ClickHandler() {
          public void onClick(final ClickEvent event) {
            final SelectionWindowComponent<GridUser, ? extends Window> userSelectionComponent = userSelectionWindowComponentSupplier.get();
            userSelectionComponent.setSelectionCallback(new Listener<GridUser>() {
              public void onEvent(final GridUser gridUser) {
                objectService.addVirtualPermissionForUser(objectId, gridUser.getGridId(), "read", callback);
              }
            });
            userSelectionComponent.execute();
          }
        });
        addGroupButton.addClickHandler(new ClickHandler() {
          public void onClick(final ClickEvent event) {
            final SelectionWindowComponent<Group, ? extends Window> groupSelectionComponent = groupSelectionWindowComponentSupplier.get();
            groupSelectionComponent.setSelectionCallback(new Listener<Group>() {
              public void onEvent(final Group group) {
                objectService.addVirtualPermissionForGroup(objectId, group.getId(), "read", callback);
              }
            });
            groupSelectionComponent.execute();
          }
        });
        this.removeButton.addClickHandler(new ClickHandler() {
          public void onClick(final ClickEvent event) {
            final ListGridRecord record = userAndGroupGrid.getSelectedRecord();
            final Permission permission = (Permission) record.getAttributeAsObject("object");
            final String sourceId = permission.getId();
            if(permission.getSource().equals(Permission.Source.USER)) {
              objectService.removeVirtualPermissionForUser(objectId, sourceId, "read", callback);
            } else {
              objectService.removeVirtualPermissionForGroup(objectId, sourceId, "read", callback);
            }
          }
        });
        this.toogleEditButton.addClickHandler(new ClickHandler() {
          public void onClick(final ClickEvent event) {
            final ListGridRecord record = userAndGroupGrid.getSelectedRecord();
            final boolean canEdit = record.getAttributeAsBoolean("write");
            final Permission permission = (Permission) record.getAttributeAsObject("object");
            final String sourceId = permission.getId();
            if(permission.getSource().equals(Permission.Source.USER)) {
              if(!canEdit) {
                objectService.addVirtualPermissionForUser(objectId, sourceId, "write", callback);
              } else {
                objectService.removeVirtualPermissionForUser(objectId, sourceId, "write", callback);
              }
            } else {
              if(!canEdit) {
                objectService.addVirtualPermissionForGroup(objectId, sourceId, "write", callback);
              } else {
                objectService.removeVirtualPermissionForGroup(objectId, sourceId, "write", callback);
              }
            }
          }
        });
        this.get().addMember(userAndGroupLayout);
      } else {
        addUserButton.addClickHandler(new ClickHandler() {
          public void onClick(final ClickEvent event) {
            final SelectionWindowComponent<GridUser, ? extends Window> userSelectionComponent = userSelectionWindowComponentSupplier.get();
            userSelectionComponent.setSelectionCallback(new Listener<GridUser>() {
              public void onEvent(final GridUser gridUser) {
                objectService.addPermissionForUser(objectId, gridUser.getGridId(), "read", callback);
              }
            });
            userSelectionComponent.execute();
          }
        });
        addGroupButton.addClickHandler(new ClickHandler() {
          public void onClick(final ClickEvent event) {
            final SelectionWindowComponent<Group, ? extends Window> groupSelectionComponent = groupSelectionWindowComponentSupplier.get();
            groupSelectionComponent.setSelectionCallback(new Listener<Group>() {
              public void onEvent(final Group group) {
                objectService.addPermissionForGroup(objectId, group.getId(), "read", callback);
              }
            });
            groupSelectionComponent.execute();
          }
        });
        this.removeButton.addClickHandler(new ClickHandler() {
          public void onClick(final ClickEvent event) {
            final ListGridRecord record = userAndGroupGrid.getSelectedRecord();
            final Permission permission = (Permission) record.getAttributeAsObject("object");
            final String sourceId = permission.getId();
            if(permission.getSource().equals(Permission.Source.USER)) {
              objectService.removePermissionForUser(objectId, sourceId, "read", callback);
            } else {
              objectService.removePermissionForGroup(objectId, sourceId, "read", callback);
            }
          }
        });
        this.toogleEditButton.addClickHandler(new ClickHandler() {
          public void onClick(final ClickEvent event) {
            final ListGridRecord record = userAndGroupGrid.getSelectedRecord();
            final boolean canEdit = record.getAttributeAsBoolean("write");
            final Permission permission = (Permission) record.getAttributeAsObject("object");
            final String sourceId = permission.getId();
            if(permission.getSource().equals(Permission.Source.USER)) {
              if(!canEdit) {
                objectService.addPermissionForUser(objectId, sourceId, "write", callback);
              } else {
                objectService.removePermissionForUser(objectId, sourceId, "write", callback);
              }
            } else {
              if(!canEdit) {
                objectService.addPermissionForGroup(objectId, sourceId, "write", callback);
              } else {
                objectService.removePermissionForGroup(objectId, sourceId, "write", callback);
              }
            }
          }
        });

        canEditField.setTitle(PageConstants.INSTANCE.sharingObjectsCanEdit());
        final DataSourceField sfNameField = SmartUtils.getFieldBuilder("name", "Name").get();
        final DataSource sfDataSource = SmartUtils.newDataSourceWithFields(sfNameField, SmartUtils.getHiddenIdField());
        this.sharedFoldersGrid = new ClientListGrid(sfDataSource);
        this.sharedFoldersGrid.setMinHeight(10);
        this.sharedFoldersGrid.setAutoFitData(Autofit.VERTICAL);
        this.sharedFoldersGrid.setAutoFitMaxRecords(3);
        this.sharedFoldersGrid.setEmptyMessage(PageConstants.INSTANCE.sharingObjectsNoSharedFolders());

        final Button addToFolderButton = SmartUtils.getButton(PageConstants.INSTANCE.sharingAddToASharedFolder(), Resources.SHARED_FOLDER,
            new Command() {
              public void execute() {
                final SelectionWindowComponent<TreeItem, ? extends Window> component = virtualFolderSelectionWindowComponentSupplier.get();
                component.setSelectionCallback(new Listener<TreeItem>() {
                  public void onEvent(final TreeItem selectedItem) {
                    if(selectedItem == null) {
                      return;
                    }
                    objectService.addToSharedFolder(Arrays.asList(objectId), selectedItem.getId(), false, callback);
                  }
                });
                component.execute();
              }
            }, 180);
        this.removeFolderButton = SmartUtils.getButton(PageConstants.INSTANCE.sharingRemoveFromSharedFolder(), Resources.CROSS, new Command() {
          public void execute() {
            final ListGridRecord record = sharedFoldersGrid.getSelectedRecord();
            objectService.removeFromSharedFolder(objectId, record.getAttribute("id"), callback);
          }
        }, 180);

        SmartUtils.enabledWhenHasSelection(removeFolderButton, sharedFoldersGrid);
        final Layout sharedFoldersLayout = new CanvasWithOpsLayout<ClientListGrid>(sharedFoldersGrid, addToFolderButton, removeFolderButton);
        sharedFoldersLayout.setIsGroup(true);
        sharedFoldersLayout.setAutoHeight();
        sharedFoldersLayout.setGroupTitle("Shared Folders");
        final Label description = SmartUtils.smartParagraph(PageConstants.INSTANCE.sharingObjectsDescription());
        this.get().addMember(description);
        this.get().addMember(userAndGroupLayout);
        this.get().addMember(sharedFoldersLayout);
        this.initUserAndGroupGrid();
      }
      this.resetButtons();
    }

    private void resetButtons() {
      this.toogleEditButton.setDisabled(true);
      this.removeButton.setDisabled(true);
      if(this.removeFolderButton != null) {
        this.removeFolderButton.setDisabled(true);
      }
    }

    private void reload() {
      this.resetButtons();
      SmartUtils.removeAllRecords(this.userAndGroupGrid);
      if(this.sharedFoldersGrid != null) {
        SmartUtils.removeAllRecords(this.sharedFoldersGrid);
      }
      this.initUserAndGroupGrid();
    }

    private String getPermissionSourceIcon(final Permission.Source source) {
      String icon = null;
      switch(source) {
      case SHARED_FOLDER:
        icon = Resources.SHARED_FOLDER;
        break;
      case USER:
        icon = Resources.PERSON;
        break;
      case GROUP:
        icon = Resources.GROUP;
        break;
      }
      return icon;
    }

    private void initUserAndGroupGrid() {
      objectService.getPermissions(this.objectId, new AsyncCallbackImpl<List<Permission>>() {
        @Override
        public void onSuccess(final List<Permission> permissions) {
          for(final Permission permission : permissions) {
            final ListGridRecord record = new ListGridRecord();
            record.setAttribute("name", StringUtils.sanitize(permission.getName()));
            record.setAttribute("id", permission.getId());
            record.setAttribute("object", permission);
            record.setAttribute("write", permission.getType().equals(Permission.Type.WRITE));
            record.setAttribute("writeIcon", permission.getType().equals(Permission.Type.WRITE) ? Resources.CHECK : null); //
            record.setAttribute("immutable", permission.getImmutable());
            if(permission.getSource().equals(Permission.Source.SHARED_FOLDER)) {
              sharedFoldersGrid.getClientDataSource().addData(record);
            } else {
              record.setAttribute("type", getPermissionSourceIcon(permission.getSource()));
              userAndGroupGrid.getClientDataSource().addData(record);
            }
          }
        }
      });
    }

  }

  public CanvasComponent<Layout> get(final PageConfiguration pageConfiguration) {
    if(pageConfiguration.getLocation().getObject() instanceof Folder) {
      return null;
    }
    return new SharingLayoutHandler(pageConfiguration);
  }

}
