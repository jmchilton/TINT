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
import com.smartgwt.client.widgets.form.events.ItemChangedEvent;
import com.smartgwt.client.widgets.form.events.ItemChangedHandler;

import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.EditCatalogProviderFormComponent;
import edu.umn.msi.tropix.webgui.client.components.WindowComponent;
import edu.umn.msi.tropix.webgui.client.mediators.LocationUpdateMediator;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.tropix.CatalogService;

public class AddCatalogProviderWindowComponentSupplierImpl implements Supplier<WindowComponent<Window>> {
  private Supplier<EditCatalogProviderFormComponent> editCatalogProviderFormComponentSupplier;
  private static LocationUpdateMediator locationUpdateMediator = LocationUpdateMediator.getInstance();

  @Inject
  public void setEditCatalogProviderFormComponentSupplier(final Supplier<EditCatalogProviderFormComponent> editCatalogProviderFormComponentSupplier) {
    this.editCatalogProviderFormComponentSupplier = editCatalogProviderFormComponentSupplier;
  }

  public WindowComponent<Window> get() {
    return new AddCatalogProviderWindowComponent(editCatalogProviderFormComponentSupplier.get());
  }

  private static class AddCatalogProviderWindowComponent extends WindowComponentImpl<Window> {
    AddCatalogProviderWindowComponent(final EditCatalogProviderFormComponent editCatalogProvider) {
      final DynamicForm form = editCatalogProvider.get();
      final Button createButton = SmartUtils.getButton("Create", Resources.ADD);
      createButton.setDisabled(true);
      createButton.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          CatalogService.Util.getInstance().createProvider(editCatalogProvider.getObject(), new AsyncCallbackImpl<Void>() {
            @Override
            public void onSuccess(final Void result) {
              AddCatalogProviderWindowComponent.this.get().destroy();
            }
          });
          locationUpdateMediator.onEvent(new LocationUpdateMediator.AddUpdateEvent("catalogProvider", null, ""));
        }
      });
      form.addItemChangedHandler(new ItemChangedHandler() {
        public void onItemChanged(final ItemChangedEvent event) {
          createButton.setDisabled(!editCatalogProvider.isValid());
        }
      });
      this.setWidget(PopOutWindowBuilder.titled("Create New Service Provider").sized(320, 240).withContents(new CanvasWithOpsLayout<DynamicForm>(form, createButton)).get());
    }

  }

}
