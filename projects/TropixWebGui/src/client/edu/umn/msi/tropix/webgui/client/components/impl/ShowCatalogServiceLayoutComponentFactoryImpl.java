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

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.Layout;

import edu.umn.msi.tropix.webgui.client.catalog.CatalogUtils;
import edu.umn.msi.tropix.webgui.client.catalog.beans.Attribute;
import edu.umn.msi.tropix.webgui.client.catalog.beans.ReferenceBean;
import edu.umn.msi.tropix.webgui.client.catalog.beans.ServiceBean;
import edu.umn.msi.tropix.webgui.client.components.CanvasComponent;
import edu.umn.msi.tropix.webgui.client.components.ComponentFactory;
import edu.umn.msi.tropix.webgui.client.constants.CatalogSearchDetailsPanelConstants;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.PropertyListGrid;
import edu.umn.msi.tropix.webgui.client.widgets.WidgetSupplierImpl;

public class ShowCatalogServiceLayoutComponentFactoryImpl implements ComponentFactory<ServiceBean, CanvasComponent<Layout>> {
  private static final CatalogSearchDetailsPanelConstants MESSAGES = (CatalogSearchDetailsPanelConstants) GWT.create(CatalogSearchDetailsPanelConstants.class);

  public CanvasComponent<Layout> get(final ServiceBean service) {
    return new ShowCatalogServiceLayoutComponentImpl(service);
  }

  private static String buildLink(final String value) {
    return "<a target=\"_blank\" href=\"" + StringUtils.sanitize(value) + "\">" + StringUtils.sanitize(value) + "</a>";
  }

  private static class ShowCatalogServiceLayoutComponentImpl extends WidgetSupplierImpl<Layout> implements CanvasComponent<Layout> {
    private final PropertyListGrid propertyListGrid;

    public ShowCatalogServiceLayoutComponentImpl(final ServiceBean service) {
      this.propertyListGrid = new PropertyListGrid();
      this.propertyListGrid.setAlternateRecordStyles(true);
      this.propertyListGrid.set(MESSAGES.lblServiceName(), MESSAGES.lblServiceNameHelp(), "");
      this.propertyListGrid.set(MESSAGES.lblAuthor(), MESSAGES.lblAuthorHelp(), "");
      this.propertyListGrid.set(MESSAGES.lblDateCreated(), MESSAGES.lblDateCreatedHelp(), new Date());
      this.propertyListGrid.set(MESSAGES.lblVersion(), MESSAGES.lblVersionHelp(), "");
      this.propertyListGrid.set(MESSAGES.lblCode(), MESSAGES.lblCodeHelp(), "");
      this.propertyListGrid.set(MESSAGES.lblDateLastModified(), MESSAGES.lblDateLastModifiedHelp(), new Date());
      this.propertyListGrid.set(MESSAGES.lblStatus(), MESSAGES.lblStatusHelp(), "");
      this.propertyListGrid.set(MESSAGES.lblProvider(), MESSAGES.lblProviderHelp(), "");
      this.propertyListGrid.set(MESSAGES.lblCategory(), MESSAGES.lblCategoryHelp(), "");
      this.propertyListGrid.set(MESSAGES.lblDescription(), MESSAGES.lblDescriptionHelp(), "");
      this.propertyListGrid.set(MESSAGES.lblReferences(), MESSAGES.lblReferencesHelp(), "");

      this.propertyListGrid.set(MESSAGES.lblServiceName(), StringUtils.sanitize(service.getName()));
      this.propertyListGrid.set(MESSAGES.lblCode(), StringUtils.sanitize(service.getId()));
      this.propertyListGrid.set(MESSAGES.lblAuthor(), StringUtils.sanitize(service.getPublisher()));
      this.propertyListGrid.set(MESSAGES.lblDateCreated(), service.getDateServiceCreated());
      this.propertyListGrid.set(MESSAGES.lblVersion(), StringUtils.sanitize(service.getRevision()));
      this.propertyListGrid.set(MESSAGES.lblDateLastModified(), service.getDateLastModified());
      this.propertyListGrid.set(MESSAGES.lblStatus(), StringUtils.sanitize(service.getStatus()));
      this.propertyListGrid.set(MESSAGES.lblProvider(), StringUtils.sanitize(service.getProvider()));
      this.propertyListGrid.set(MESSAGES.lblCategory(), StringUtils.sanitize(service.getCategory()));
      this.propertyListGrid.set(MESSAGES.lblDescription(), StringUtils.sanitize(service.getDescription()));
      this.propertyListGrid.set(MESSAGES.lblService(), StringUtils.sanitize(service.getCatalogId()));

      final StringBuilder referenceBuilder = new StringBuilder("");
      boolean firstReference = true;
      for(final ReferenceBean refBean : service.getReferences()) {
        if(firstReference) {
          firstReference = false;
        } else {
          referenceBuilder.append(", ");
        }
        if(refBean.isURL()) {
          referenceBuilder.append(ShowCatalogServiceLayoutComponentFactoryImpl.buildLink(refBean.getValue()));
        } else {
          referenceBuilder.append(StringUtils.sanitize(refBean.getValue()));
        }
      }
      this.propertyListGrid.set(MESSAGES.lblReferences(), referenceBuilder.toString());

      for(final Attribute attribute : service.getAttributes()) {
        if(attribute.getName().startsWith("TROPIX")) {
          continue;
        }
        this.propertyListGrid.set("Input " + attribute.getName(), CatalogUtils.getValuesString(attribute.getValues()));
      }

      this.setWidget(new CanvasWithOpsLayout<ListGrid>(this.propertyListGrid));
    }
  }

}
