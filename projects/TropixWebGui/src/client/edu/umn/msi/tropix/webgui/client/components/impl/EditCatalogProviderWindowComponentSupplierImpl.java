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

import java.util.List;

import com.google.common.base.Supplier;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.events.ItemChangedEvent;
import com.smartgwt.client.widgets.form.events.ItemChangedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.client.directory.GridUser;
import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.catalog.beans.Provider;
import edu.umn.msi.tropix.webgui.client.components.ComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.EditCatalogProviderFormComponent;
import edu.umn.msi.tropix.webgui.client.components.SelectionWindowComponent;
import edu.umn.msi.tropix.webgui.client.components.WindowComponent;
import edu.umn.msi.tropix.webgui.client.mediators.LocationUpdateMediator;
import edu.umn.msi.tropix.webgui.client.mediators.LocationUpdateMediator.UpdateEvent;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.ClientListGrid;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.object.Permission;
import edu.umn.msi.tropix.webgui.services.tropix.CatalogService;
import edu.umn.msi.tropix.webgui.services.tropix.CatalogServiceDefinition;

public class EditCatalogProviderWindowComponentSupplierImpl implements ComponentFactory<Provider, WindowComponent<Window>> {
  private Supplier<? extends SelectionWindowComponent<GridUser, ? extends Window>> userSelectionWindowComponentSupplier;
  private Supplier<? extends SelectionWindowComponent<Group, ? extends Window>> groupSelectionWindowComponentSupplier;
  private Supplier<EditCatalogProviderFormComponent> editCatalogProviderFormComponentSupplier;
  private final LocationUpdateMediator locationUpdateMediator = LocationUpdateMediator.getInstance();

  @Inject
  public void setEditCatalogProviderFormComponentSupplier(final Supplier<EditCatalogProviderFormComponent> editCatalogProviderFormComponentSupplier) {
    this.editCatalogProviderFormComponentSupplier = editCatalogProviderFormComponentSupplier;
  }

  @Inject
  public void setUserSelectionWindowComponentSupplier(final Supplier<? extends SelectionWindowComponent<GridUser, ? extends Window>> userSelectionWindowComponentSupplier) {
    this.userSelectionWindowComponentSupplier = userSelectionWindowComponentSupplier;
  }

  @Inject
  public void setGroupSelectionWindowComponentSupplier(final Supplier<? extends SelectionWindowComponent<Group, ? extends Window>> groupSelectionWindowComponentSupplier) {
    this.groupSelectionWindowComponentSupplier = groupSelectionWindowComponentSupplier;
  }

  public WindowComponent<Window> get(final Provider provider) {
    return new EditCatalogProviderWindowComponentImpl(provider);
  }

  private class EditCatalogProviderWindowComponentImpl extends WindowComponentImpl<Window> {
    private final Provider provider;
    private final DynamicForm form;
    private final ListGrid permissionGrid;

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

    private void reload() {
      this.permissionGrid.deselectAllRecords();
      SmartUtils.removeAllRecords(this.permissionGrid);
      CatalogService.Util.getInstance().getPermissions(this.provider.getId(), new AsyncCallbackImpl<List<Permission>>() {
        @Override
        public void onSuccess(final List<Permission> permissions) {
          for(final Permission permission : permissions) {
            final ListGridRecord record = new ListGridRecord();
            record.setAttribute("name", permission.getName());
            record.setAttribute("id", permission.getId());
            record.setAttribute("object", permission);
            record.setAttribute("type", EditCatalogProviderWindowComponentImpl.this.getPermissionSourceIcon(permission.getSource()));
            EditCatalogProviderWindowComponentImpl.this.permissionGrid.addData(record);
          }
        }
      });
    }

    private <T> AsyncCallback<T> getReloadCallback() {
      return new AsyncCallbackImpl<T>() {
        @Override
        public void onSuccess(final T result) {
          reload();
        }
      };
    }

    EditCatalogProviderWindowComponentImpl(final Provider provider) {
      this.provider = provider;
      final EditCatalogProviderFormComponent formComponent = EditCatalogProviderWindowComponentSupplierImpl.this.editCatalogProviderFormComponentSupplier.get();
      this.form = formComponent.get();
      this.form.setValue("name", provider.getName());
      this.form.setValue("address", provider.getAddress());
      this.form.setValue("contact", provider.getContact());
      this.form.setValue("email", provider.getEmail());
      this.form.setValue("phone", provider.getPhone());
      this.form.setValue("website", provider.getWebsite());

      final Button saveButton = SmartUtils.getButton("Save", Resources.SAVE);
      saveButton.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          final Provider newProvider = formComponent.getObject();
          newProvider.setId(provider.getId());
          CatalogServiceDefinition.Util.getInstance().updateProvider(newProvider, new AsyncCallbackImpl<Provider>() {
            @Override
            public void onSuccess(final Provider provider) {
              locationUpdateMediator.onEvent(new UpdateEvent("catalogProvider#", null));
              SC.say("Provider details saved.");
            }
          });
        }
      });
      this.form.addItemChangedHandler(new ItemChangedHandler() {
        public void onItemChanged(final ItemChangedEvent event) {
          saveButton.setDisabled(!formComponent.isValid());
        }
      });

      final ListGridField nameField = new ListGridField("name", "Name");
      nameField.setType(ListGridFieldType.TEXT);
      nameField.setWidth("*");
      final ListGridField typeField = new ListGridField("type", " ");
      typeField.setType(ListGridFieldType.IMAGE);
      typeField.setWidth(16);
      typeField.setImageURLPrefix(GWT.getHostPageBaseURL());
      typeField.setImageURLSuffix("");

      this.permissionGrid = new ClientListGrid("id");
      this.permissionGrid.setMinHeight(10);
      this.permissionGrid.setAutoFitMaxRecords(5);
      this.permissionGrid.setAutoFitData(Autofit.VERTICAL);
      this.permissionGrid.setFields(typeField, nameField);

      final Button addUserButton = SmartUtils.getButton("Add User", Resources.PERSON_ABSOLUTE);
      final Button addGroupButton = SmartUtils.getButton("Add Group", Resources.GROUP_ABSOLUTE);
      final Button removeButton = SmartUtils.getButton("Remove", Resources.CROSS);
      final String providerId = provider.getId();
      addUserButton.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          final SelectionWindowComponent<GridUser, ? extends Window> userSelectionComponent = EditCatalogProviderWindowComponentSupplierImpl.this.userSelectionWindowComponentSupplier.get();
          userSelectionComponent.setSelectionCallback(new Listener<GridUser>() {
            public void onEvent(final GridUser gridUser) {
              CatalogService.Util.getInstance().addProviderPermissionForUser(providerId, gridUser.getGridId(), EditCatalogProviderWindowComponentImpl.this.<Void>getReloadCallback());
            }
          });
          userSelectionComponent.execute();
        }
      });
      addGroupButton.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          final SelectionWindowComponent<Group, ? extends Window> groupSelectionComponent = EditCatalogProviderWindowComponentSupplierImpl.this.groupSelectionWindowComponentSupplier.get();
          groupSelectionComponent.setSelectionCallback(new Listener<Group>() {
            public void onEvent(final Group group) {
              CatalogService.Util.getInstance().addProviderPermissionForGroup(providerId, group.getId(), EditCatalogProviderWindowComponentImpl.this.<Void>getReloadCallback());
            }
          });
          groupSelectionComponent.execute();
        }
      });
      removeButton.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          final ListGridRecord record = EditCatalogProviderWindowComponentImpl.this.permissionGrid.getSelectedRecord();
          final Permission permission = (Permission) record.getAttributeAsObject("object");
          final String sourceId = permission.getId();
          if(permission.getSource().equals(Permission.Source.USER)) {
            CatalogService.Util.getInstance().removeProviderPermissionForUser(providerId, sourceId, EditCatalogProviderWindowComponentImpl.this.<Void>getReloadCallback());
          } else {
            CatalogService.Util.getInstance().removeProviderPermissionForGroup(providerId, sourceId, EditCatalogProviderWindowComponentImpl.this.<Void>getReloadCallback());
          }
        }
      });
      this.permissionGrid.addSelectionChangedHandler(new SelectionChangedHandler() {
        public void onSelectionChanged(final SelectionEvent event) {
          removeButton.setDisabled(!event.getState());
        }
      });
      this.reload();

      final CanvasWithOpsLayout<DynamicForm> formLayout = new CanvasWithOpsLayout<DynamicForm>(this.form, saveButton);
      final CanvasWithOpsLayout<ListGrid> gridLayout = new CanvasWithOpsLayout<ListGrid>(this.permissionGrid, addUserButton, addGroupButton, removeButton);
      final VLayout layout = new VLayout();
      layout.addMember(formLayout);
      layout.addMember(gridLayout);
      setWidget(PopOutWindowBuilder.titled("Edit Lab Service Provider").withContents(layout).get());
    }
  }

}
