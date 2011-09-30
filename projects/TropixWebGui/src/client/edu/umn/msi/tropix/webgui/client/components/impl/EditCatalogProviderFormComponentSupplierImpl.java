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
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;

import edu.umn.msi.tropix.webgui.client.catalog.beans.Provider;
import edu.umn.msi.tropix.webgui.client.components.EditCatalogProviderFormComponent;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.WidgetSupplierImpl;

public class EditCatalogProviderFormComponentSupplierImpl implements Supplier<EditCatalogProviderFormComponent> {

  public EditCatalogProviderFormComponent get() {
    return new EditCatalogProviderFormComponentImpl();
  }

  private static class EditCatalogProviderFormComponentImpl extends WidgetSupplierImpl<DynamicForm> implements EditCatalogProviderFormComponent {
    EditCatalogProviderFormComponentImpl() {
      final TextItem addressItem = new TextItem("address", "Address");
      final TextItem contactItem = new TextItem("contact", "Contact Person");
      final TextItem emailItem = new TextItem("email", "E-Mail");
      final TextItem phoneItem = new TextItem("phone", "Phone");
      final TextItem nameItem = new TextItem("name", "Lab Name");
      final TextItem websiteItem = new TextItem("website", "Website (Optional)");
      this.setWidget(new DynamicForm());
      this.get().setItems(nameItem, addressItem, contactItem, emailItem, phoneItem, websiteItem);
    }

    public Provider getObject() {
      final Provider provider = new Provider();
      provider.setAddress(StringUtils.toString(this.get().getValueAsString("address")));
      provider.setContact(StringUtils.toString(this.get().getValueAsString("contact")));
      provider.setEmail(StringUtils.toString(this.get().getValueAsString("email")));
      provider.setPhone(StringUtils.toString(this.get().getValueAsString("phone")));
      provider.setName(StringUtils.toString(this.get().getValueAsString("name")));
      provider.setWebsite(StringUtils.toString(this.get().getValueAsString("website")));
      return provider;
    }

    public boolean isValid() {
      return StringUtils.hasText(this.get().getValueAsString("name")) && StringUtils.hasText(this.get().getValueAsString("contact")) && StringUtils.hasText(this.get().getValueAsString("email")) && StringUtils.hasText(this.get().getValueAsString("phone"))
          && StringUtils.hasText(this.get().getValueAsString("address"));
    }
  }

}
