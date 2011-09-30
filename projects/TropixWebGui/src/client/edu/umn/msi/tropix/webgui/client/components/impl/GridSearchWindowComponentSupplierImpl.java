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

import com.google.common.base.Supplier;
import com.google.inject.Inject;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CanvasItem;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.client.services.GridService;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.GridUserItemComponent;
import edu.umn.msi.tropix.webgui.client.components.ServiceSelectionComponent;
import edu.umn.msi.tropix.webgui.client.components.WindowComponent;
import edu.umn.msi.tropix.webgui.client.search.SearchController;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.tropix.GridSearchService;

public class GridSearchWindowComponentSupplierImpl implements Supplier<WindowComponent<Window>> {
  private Supplier<GridUserItemComponent> gridUserItemComponentSupplier;
  private Supplier<ServiceSelectionComponent<GridService>> serviceSelectionComponentSupplier;
  private SearchController searchController;

  @Inject
  public void setSearchController(final SearchController searchController) {
    this.searchController = searchController;
  }

  private static class GridSearchWindowComponentImpl extends WindowComponentImpl<Window> {
    private GridService selectedService;

    GridSearchWindowComponentImpl(final GridUserItemComponent gridUserItemComponent, final ServiceSelectionComponent<GridService> serviceSelectionComponent, final SearchController searchController) {
      final DynamicForm form = new DynamicForm();
      final CanvasItem ownerItem = gridUserItemComponent.get();
      final Button findButton = SmartUtils.getButton("Find", Resources.FIND);
      findButton.setDisabled(true);
      findButton.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          final String selectedUserId = gridUserItemComponent.getSelectedUserId();
          final String searchName = "Grid Search of " + selectedService.getServiceName() + " (" + selectedService.getServiceAddress() + ") for Objects Owned by " + gridUserItemComponent.getSelectedUserLabel();
          GridSearchService.Util.getInstance().getTopLevelItems(selectedService.getServiceAddress(), selectedUserId, searchController.startGridSearch(searchName));
          get().destroy();
        }
      });
      serviceSelectionComponent.addSelectionListener(new Listener<GridService>() {
        public void onEvent(final GridService service) {
          findButton.setDisabled(service == null);
          selectedService = service;
        }
      });
      final Button cancelButton = SmartUtils.getCancelButton(this);
      serviceSelectionComponent.setServicesType("search");
      serviceSelectionComponent.fetchServices();
      form.setItems(ownerItem);
      form.setWidth100();
      final VLayout formLayout = new VLayout();
      formLayout.addMember(form);
      formLayout.addMember(serviceSelectionComponent.get());
      this.setWidget(PopOutWindowBuilder.titled("Grid Search").withContents(new CanvasWithOpsLayout<Layout>(formLayout, findButton, cancelButton)).get());
    }
  }

  public WindowComponent<Window> get() {
    return new GridSearchWindowComponentImpl(gridUserItemComponentSupplier.get(), serviceSelectionComponentSupplier.get(), searchController);
  }

  @Inject
  public void setGridUserItemComponentSupplier(final Supplier<GridUserItemComponent> gridUserItemComponentSupplier) {
    this.gridUserItemComponentSupplier = gridUserItemComponentSupplier;
  }

  @Inject
  public void setServiceSelectionComponentSupplier(final Supplier<ServiceSelectionComponent<GridService>> serviceSelectionComponentSupplier) {
    this.serviceSelectionComponentSupplier = serviceSelectionComponentSupplier;
  }

}
