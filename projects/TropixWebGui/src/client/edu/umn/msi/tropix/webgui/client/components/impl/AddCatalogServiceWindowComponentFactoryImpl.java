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
import com.smartgwt.client.widgets.layout.Layout;

import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.catalog.beans.Provider;
import edu.umn.msi.tropix.webgui.client.catalog.beans.ServiceBean;
import edu.umn.msi.tropix.webgui.client.components.ComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.EditCatalogServiceFormComponent;
import edu.umn.msi.tropix.webgui.client.components.WindowComponent;
import edu.umn.msi.tropix.webgui.client.mediators.LocationUpdateMediator;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.tropix.CatalogServiceDefinition;

public class AddCatalogServiceWindowComponentFactoryImpl implements ComponentFactory<Provider, WindowComponent<Window>> {
  private Supplier<EditCatalogServiceFormComponent> editCatalogServiceFormComponentSupplier;
  private final LocationUpdateMediator locationUpdateMediator = LocationUpdateMediator.getInstance();

  @Inject
  public void setEditCatalogServiceFormComponentSupplier(final Supplier<EditCatalogServiceFormComponent> editCatalogServiceFormComponentSupplier) {
    this.editCatalogServiceFormComponentSupplier = editCatalogServiceFormComponentSupplier;
  }

  public WindowComponent<Window> get(final Provider provider) {
    return new AddCatalogServiceWindowComponentImpl(provider.getId());
  }

  private final class AddCatalogServiceWindowComponentImpl extends WindowComponentImpl<Window> {
    private EditCatalogServiceFormComponent component;

    private AddCatalogServiceWindowComponentImpl(final String providerId) {
      component = editCatalogServiceFormComponentSupplier.get();
      final Button createButton = SmartUtils.getButton("Create", Resources.ADD);
      createButton.setDisabled(true);
      final Layout form = component.get();
      component.addChangedListener(new Listener<EditCatalogServiceFormComponent>() {
        public void onEvent(final EditCatalogServiceFormComponent event) {
          createButton.setDisabled(!component.validate());
        }
      });
      createButton.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          final ServiceBean serviceBean = component.getServiceBean();
          serviceBean.setProviderID(providerId);
          CatalogServiceDefinition.Util.getInstance().createService(serviceBean, new AsyncCallbackImpl<ServiceBean>() {
            public void onSuccess(final ServiceBean serviceBean) {
              final String serviceId = serviceBean != null ? serviceBean.getId() : null;
              final String providerId = serviceBean != null ? serviceBean.getProviderID() : null;
              locationUpdateMediator.onEvent(new LocationUpdateMediator.AddUpdateEvent("catalogService#" + serviceId, null, "catalogProvider#" + providerId));
              get().destroy();
            }
          });
        }
      });
      final CanvasWithOpsLayout<Layout> layout = new CanvasWithOpsLayout<Layout>(form, createButton);
      this.setWidget(PopOutWindowBuilder.titled("Add New Catalog Service").sized(450, 450).withContents(layout).get());
    }
  }

}
