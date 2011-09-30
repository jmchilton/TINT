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

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.catalog.beans.Provider;
import edu.umn.msi.tropix.webgui.client.components.CanvasComponent;
import edu.umn.msi.tropix.webgui.client.components.ComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.WindowComponent;
import edu.umn.msi.tropix.webgui.client.mediators.LocationUpdateMediator;
import edu.umn.msi.tropix.webgui.client.mediators.LocationUpdateMediator.UpdateEvent;
import edu.umn.msi.tropix.webgui.client.utils.Iterables;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.ClientListGrid;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.tropix.CatalogService;

public class ManageProvidersWindowComponentSupplierImpl implements Supplier<WindowComponent<Window>> {
  private ComponentFactory<Provider, ? extends Command> showCatalogProviderComponentFactory, editCatalogProviderComponentFactory;
  private ComponentFactory<Provider, ? extends CanvasComponent<? extends Canvas>> manageServicesComponentFactory;
  private Supplier<? extends Command> addCatalogProviderComponentSupplier;
  private final LocationUpdateMediator locationUpdateMediator = LocationUpdateMediator.getInstance();

  @Inject
  public void setManageServicesComponentFactory(@Named("manageCatalogServices") final ComponentFactory<Provider, ? extends CanvasComponent<? extends Canvas>> manageServicesComponentFactory) {
    this.manageServicesComponentFactory = manageServicesComponentFactory;
  }

  @Inject
  public void setShowCatalogProviderComponentFactory(@Named("showCatalogProvider") final ComponentFactory<Provider, ? extends Command> showCatalogProviderComponentFactory) {
    this.showCatalogProviderComponentFactory = showCatalogProviderComponentFactory;
  }

  @Inject
  public void setEditCatalogProviderComponentFactory(@Named("editCatalogProvider") final ComponentFactory<Provider, ? extends Command> editCatalogProviderComponentFactory) {
    this.editCatalogProviderComponentFactory = editCatalogProviderComponentFactory;
  }

  @Inject
  public void setAddCatalogProviderComponentSupplier(@Named("addCatalogProvider") final Supplier<? extends Command> addCatalogProviderComponentSupplier) {
    this.addCatalogProviderComponentSupplier = addCatalogProviderComponentSupplier;
  }

  public WindowComponent<Window> get() {
    return new ManageProvidersWindowComponent();
  }

  private static final Function<Provider, ListGridRecord> TO_RECORD_FUNCTION = new Function<Provider, ListGridRecord>() {
    public ListGridRecord apply(final Provider provider) {
      final ListGridRecord record = new ListGridRecord();
      record.setAttribute("id", provider.getId());
      record.setAttribute("object", provider);
      record.setAttribute("name", StringUtils.sanitize(provider.getName()));
      return record;
    }
  };

  private class ManageProvidersWindowComponent extends WindowComponentImpl<Window> {
    private final ListGrid providersGrid;
    private Provider selectedProvider = null;

    private void reload() {
      this.providersGrid.deselectAllRecords();
      SmartUtils.removeAllRecords(this.providersGrid);
      CatalogService.Util.getInstance().getMyProviders(new AsyncCallbackImpl<List<Provider>>() {
        @Override
        public void onSuccess(final List<Provider> providers) {
          SmartUtils.addRecords(ManageProvidersWindowComponent.this.providersGrid, Iterables.transform(providers, ManageProvidersWindowComponentSupplierImpl.TO_RECORD_FUNCTION));
        }
      });
    }

    ManageProvidersWindowComponent() {
      final ListGridField nameField = new ListGridField("name", "Provider Name");
      nameField.setType(ListGridFieldType.TEXT);
      nameField.setWidth("*");

      final Button editButton = SmartUtils.getButton("Edit", Resources.EDIT);
      editButton.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          // TODO: Register destruction listener with window.
          editCatalogProviderComponentFactory.get(selectedProvider).execute();
        }
      });

      final Button showButton = SmartUtils.getButton("View", Resources.CONTROL_NEXT);
      showButton.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          showCatalogProviderComponentFactory.get(selectedProvider).execute();
        }
      });

      final Button addButton = SmartUtils.getButton("Create", Resources.ADD);
      addButton.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          addCatalogProviderComponentSupplier.get().execute();
        }
      });

      final VLayout mainLayout = new VLayout();
      final VLayout servicesLayout = new VLayout();

      this.providersGrid = new ClientListGrid("id");
      this.providersGrid.setMinHeight(10);
      this.providersGrid.setAutoFitMaxRecords(3);
      this.providersGrid.setAutoFitData(Autofit.VERTICAL);
      this.providersGrid.setFields(nameField);
      this.providersGrid.setEmptyMessage("No providers to show");
      this.providersGrid.addSelectionChangedHandler(new SelectionChangedHandler() {
        public void onSelectionChanged(final SelectionEvent event) {
          if(event.getState()) {
            selectedProvider = (Provider) event.getRecord().getAttributeAsObject("object");
          } else {
            selectedProvider = null;
          }
          editButton.setDisabled(selectedProvider == null);
          showButton.setDisabled(selectedProvider == null);
          SmartUtils.removeAndDestroyAllMembers(servicesLayout);
          if(ManageProvidersWindowComponent.this.selectedProvider != null) {
            servicesLayout.setMembers(manageServicesComponentFactory.get(selectedProvider).get());
          }
        }
      });
      editButton.setDisabled(true);
      showButton.setDisabled(true);
      final Listener<UpdateEvent> updateListener = new Listener<UpdateEvent>() {
        public void onEvent(final UpdateEvent event) {
          if(event.getLocationId().startsWith("catalogProvider")) {
            reload();
          }
        }
      };
      final CanvasWithOpsLayout<ListGrid> providersLayout = new CanvasWithOpsLayout<ListGrid>("Select a provider to view its advertised services.", this.providersGrid, addButton, editButton, showButton) {
        public void destroy() {
          locationUpdateMediator.removeListener(updateListener);
          super.destroy();
        }
      };
      mainLayout.addMember(providersLayout);
      mainLayout.addMember(servicesLayout);
      this.setWidget(PopOutWindowBuilder.titled("Manage Service Providers").sized(500, 400).withContents(mainLayout).get());
      locationUpdateMediator.addListener(updateListener);
      this.reload();
    }
  }

}
