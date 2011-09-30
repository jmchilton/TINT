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
import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.catalog.CatalogUtils;
import edu.umn.msi.tropix.webgui.client.catalog.beans.Provider;
import edu.umn.msi.tropix.webgui.client.catalog.beans.ServiceBean;
import edu.umn.msi.tropix.webgui.client.components.CanvasComponent;
import edu.umn.msi.tropix.webgui.client.components.ComponentFactory;
import edu.umn.msi.tropix.webgui.client.mediators.LocationUpdateMediator;
import edu.umn.msi.tropix.webgui.client.mediators.LocationUpdateMediator.UpdateEvent;
import edu.umn.msi.tropix.webgui.client.utils.Iterables;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.ClientListGrid;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.client.widgets.WidgetSupplierImpl;
import edu.umn.msi.tropix.webgui.services.tropix.CatalogServiceDefinition;

public class ManageServicesComponentFactoryImpl implements ComponentFactory<Provider, CanvasComponent<Layout>> {
  private ComponentFactory<Provider, ? extends Command> addCatalogServiceComponentFactory;
  private ComponentFactory<ServiceBean, ? extends Command> showCatalogServiceComponentFactory, editCatalogServiceComponentFactory;
  private final LocationUpdateMediator locationUpdateMediator = LocationUpdateMediator.getInstance();

  @Inject
  public void setAddCatalogServiceComponentFactory(@Named("addCatalogService") final ComponentFactory<Provider, ? extends Command> addCatalogServiceComponentFactory) {
    this.addCatalogServiceComponentFactory = addCatalogServiceComponentFactory;
  }

  @Inject
  public void setShowCatalogServiceComponentFactory(@Named("showCatalogService") final ComponentFactory<ServiceBean, ? extends Command> showCatalogServiceComponentFactory) {
    this.showCatalogServiceComponentFactory = showCatalogServiceComponentFactory;
  }

  @Inject
  public void setEditCatalogServiceComponentFactory(@Named("editCatalogService") final ComponentFactory<ServiceBean, ? extends Command> editCatalogServiceComponentFactory) {
    this.editCatalogServiceComponentFactory = editCatalogServiceComponentFactory;
  }

  public CanvasComponent<Layout> get(final Provider provider) {
    return new ManageServicesComponentImpl(provider);
  }

  private static final Function<ServiceBean, ListGridRecord> TO_RECORD_FUNCTION = new Function<ServiceBean, ListGridRecord>() {
    public ListGridRecord apply(final ServiceBean serviceBean) {
      final ListGridRecord record = new ListGridRecord();
      record.setAttribute("id", serviceBean.getId());
      record.setAttribute("object", serviceBean);
      record.setAttribute("name", StringUtils.sanitize(serviceBean.getName()));
      return record;
    }
  };

  private class ManageServicesComponentImpl extends WidgetSupplierImpl<Layout> implements CanvasComponent<Layout> {
    private final Provider provider;
    private final ListGrid servicesGrid;
    private ServiceBean selectedService;

    public ManageServicesComponentImpl(final Provider provider) {
      this.provider = provider;
      this.setWidget(new VLayout());

      final ListGridField nameField = new ListGridField("name", "Service Name");
      nameField.setType(ListGridFieldType.TEXT);
      nameField.setWidth("*");

      final Button editButton = SmartUtils.getButton("Edit", Resources.EDIT);
      editButton.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          CatalogServiceDefinition.Util.getInstance().getServiceDetails(selectedService.getId(), selectedService.getCatalogId(), new AsyncCallbackImpl<ServiceBean>() {
            @Override
            public void onSuccess(final ServiceBean populatedServiceBean) {
              editCatalogServiceComponentFactory.get(populatedServiceBean).execute();
            }
          });

        }
      });

      final Button showButton = SmartUtils.getButton("View", Resources.CONTROL_NEXT);
      showButton.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          CatalogServiceDefinition.Util.getInstance().getServiceDetails(ManageServicesComponentImpl.this.selectedService.getId(), ManageServicesComponentImpl.this.selectedService.getCatalogId(), new AsyncCallbackImpl<ServiceBean>() {
            @Override
            public void onSuccess(final ServiceBean populatedServiceBean) {
              ManageServicesComponentFactoryImpl.this.showCatalogServiceComponentFactory.get(populatedServiceBean).execute();
            }
          });
        }
      });

      final Button addButton = SmartUtils.getButton("Create", Resources.ADD);
      addButton.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          try {
            addCatalogServiceComponentFactory.get(provider).execute();
          } catch(Exception e) {
            e.printStackTrace();
          }
        }
      });

      this.servicesGrid = new ClientListGrid("id");
      this.servicesGrid.setMinHeight(10);
      this.servicesGrid.setAutoFitMaxRecords(3);
      this.servicesGrid.setAutoFitData(Autofit.VERTICAL);
      this.servicesGrid.setFields(nameField);
      this.servicesGrid.setEmptyMessage("No services to show.");
      this.servicesGrid.addSelectionChangedHandler(new SelectionChangedHandler() {
        public void onSelectionChanged(final SelectionEvent event) {
          if(event.getState()) {
            ManageServicesComponentImpl.this.selectedService = (ServiceBean) event.getRecord().getAttributeAsObject("object");
          } else {
            ManageServicesComponentImpl.this.selectedService = null;
          }
          editButton.setDisabled(ManageServicesComponentImpl.this.selectedService == null);
          showButton.setDisabled(ManageServicesComponentImpl.this.selectedService == null);
        }
      });
      editButton.setDisabled(true);
      showButton.setDisabled(true);

      this.reload();

      locationUpdateMediator.addListener(new Listener<UpdateEvent>() {
        public void onEvent(final UpdateEvent event) {
          if(event.getLocationId() != null && event.getLocationId().startsWith("catalogService")) {
            reload();
          }
        }
      });

      this.setWidget(new CanvasWithOpsLayout<ListGrid>(this.servicesGrid, addButton, editButton, showButton));
    }

    private void reload() {
      SmartUtils.removeAllRecords(ManageServicesComponentImpl.this.servicesGrid);
      CatalogUtils.getEntriesFromProvider(this.provider.getId(), this.provider.getCatalogId(), new AsyncCallbackImpl<List<ServiceBean>>() {
        @Override
        public void onSuccess(final List<ServiceBean> services) {
          SmartUtils.addRecords(ManageServicesComponentImpl.this.servicesGrid, Iterables.transform(services, ManageServicesComponentFactoryImpl.TO_RECORD_FUNCTION));
        }
      });
    }
  }

}
