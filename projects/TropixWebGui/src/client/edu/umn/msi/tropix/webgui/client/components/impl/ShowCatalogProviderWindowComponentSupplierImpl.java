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

import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;

import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.catalog.beans.Provider;
import edu.umn.msi.tropix.webgui.client.components.ComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.WindowComponent;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.PropertyListGrid;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.tropix.CatalogService;

public class ShowCatalogProviderWindowComponentSupplierImpl implements ComponentFactory<Provider, WindowComponent<Window>> {
  private ComponentFactory<Provider, ? extends Command> editCatalogProviderComponentFactory;

  @Inject
  public void setEditCatalogProviderComponentFactory(@Named("editCatalogProvider") final ComponentFactory<Provider, ? extends Command> editCatalogProviderComponentFactory) {
    this.editCatalogProviderComponentFactory = editCatalogProviderComponentFactory;
  }

  public WindowComponent<Window> get(final Provider provider) {
    return new ShowCatalogProviderWindowComponentImpl(provider);
  }

  private class ShowCatalogProviderWindowComponentImpl extends WindowComponentImpl<Window> {
    private final Button editButton;
    private final PropertyListGrid propertyListGrid;

    ShowCatalogProviderWindowComponentImpl(final Provider provider) {
      this.propertyListGrid = new PropertyListGrid();
      this.propertyListGrid.setWidth100();
      this.propertyListGrid.setHeight100();
      this.propertyListGrid.setCanSelectText(true); // Should allow users to select website and email address
      this.propertyListGrid.set("Contact", "");
      this.propertyListGrid.set("Address", "");
      this.propertyListGrid.set("E-Mail", "");
      this.propertyListGrid.set("Phone", "");
      this.propertyListGrid.set("Website", "");
      this.editButton = SmartUtils.getButton("Edit", Resources.EDIT);
      this.editButton.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent be) {
          editProvider(provider);
        }
      });

      this.propertyListGrid.set("Name", StringUtils.sanitize(provider.getName()));
      this.propertyListGrid.set("Contact", StringUtils.sanitize(provider.getContact()));
      this.propertyListGrid.set("Address", StringUtils.sanitize(provider.getAddress()));
      this.propertyListGrid.set("Phone", StringUtils.sanitize(provider.getPhone()));
      final String website = StringUtils.sanitize(provider.getWebsite());
      final String websiteLink = "<a href='" + website + "'>" + website + "</a>";
      this.propertyListGrid.set("Website", websiteLink);
      final String email = StringUtils.sanitize(provider.getEmail());
      final String emailLink = "<a href='mailto:" + email + "'>" + email + "</a>";
      this.propertyListGrid.set("E-Mail", emailLink);
      CatalogService.Util.getInstance().canModify(provider.getId(), new AsyncCallbackImpl<Boolean>() {
        @Override
        public void onSuccess(final Boolean canModify) {
          editButton.setDisabled(!canModify);
        }
      });

      final CanvasWithOpsLayout<ListGrid> layout = new CanvasWithOpsLayout<ListGrid>(this.propertyListGrid, this.editButton);
      this.setWidget(PopOutWindowBuilder.titled("Provider Details").sized(500, 300).withContents(layout).get());
    }

    private void editProvider(final Provider provider) {
      editCatalogProviderComponentFactory.get(provider).execute();
      get().destroy();
    }
  }
}
