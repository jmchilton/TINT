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
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.Layout;

import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
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

public class EditCatalogServiceWindowComponentFactoryImpl implements ComponentFactory<ServiceBean, WindowComponent<Window>> {
  private Supplier<EditCatalogServiceFormComponent> editCatalogServiceFormComponentSupplier;
  private final LocationUpdateMediator locationUpdateMediator = LocationUpdateMediator.getInstance();

  @Inject
  public void setEditCatalogServiceFormComponentSupplier(final Supplier<EditCatalogServiceFormComponent> editCatalogServiceFormComponentSupplier) {
    this.editCatalogServiceFormComponentSupplier = editCatalogServiceFormComponentSupplier;
  }

  public WindowComponent<Window> get(final ServiceBean service) {
    return new EditCatalogServiceWindowComponentImpl(service);
  }

  private class EditCatalogServiceWindowComponentImpl extends WindowComponentImpl<Window> {
    private final EditCatalogServiceFormComponent editCatalogServiceComponent;

    public EditCatalogServiceWindowComponentImpl(final ServiceBean service) {
      this.editCatalogServiceComponent = EditCatalogServiceWindowComponentFactoryImpl.this.editCatalogServiceFormComponentSupplier.get();
      final Button saveButton = SmartUtils.getButton("Save", Resources.SAVE);
      saveButton.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          final ServiceBean updatedService = EditCatalogServiceWindowComponentImpl.this.editCatalogServiceComponent.getServiceBean();

          // Merge changes...
          service.setDescription(updatedService.getDescription());
          service.setName(updatedService.getName());
          service.setStatus(updatedService.getStatus());
          service.setCategory(updatedService.getCategory());
          service.setCategoryID(updatedService.getCategoryID());
          service.setPublisher(updatedService.getPublisher());
          service.setFieldList(updatedService.getFieldList());

          CatalogServiceDefinition.Util.getInstance().updateService(service, new AsyncCallbackImpl<ServiceBean>() {
            @Override
            public void onSuccess(final ServiceBean savedService) {
              SC.say("Service saved.");
              locationUpdateMediator.onEvent(new LocationUpdateMediator.UpdateEvent("catalogService#", null));
              EditCatalogServiceWindowComponentImpl.this.get().destroy();
            }
          });
        }
      });
      this.editCatalogServiceComponent.setServiceBean(service);
      this.editCatalogServiceComponent.addChangedListener(new Listener<EditCatalogServiceFormComponent>() {
        public void onEvent(final EditCatalogServiceFormComponent event) {
          saveButton.setDisabled(!EditCatalogServiceWindowComponentImpl.this.editCatalogServiceComponent.validate());
        }
      });
      this.setWidget(PopOutWindowBuilder.titled("Edit Catalog Service").sized(450, 450).withContents(new CanvasWithOpsLayout<Layout>(editCatalogServiceComponent.get(), saveButton)).get());
    }
  }
}
