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

import java.util.Arrays;

import com.google.common.base.Supplier;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.WindowComponent;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.tropix.CatalogService;

public class CatalogAdminWindowComponentSupplierImpl implements Supplier<WindowComponent<Window>> {

  public WindowComponent<Window> get() {
    return new CatalogAdminWindowComponentImpl();
  }

  private static Layout getAddFreeTextFieldLayout() {
    final DynamicForm addFreeTextFieldForm = new DynamicForm();
    final TextItem fieldNameItem = new TextItem("name", "Name");
    addFreeTextFieldForm.setItems(fieldNameItem);

    final Button addFreeTextFieldButton = SmartUtils.getButton("Add Free Text Field", Resources.ADD, 150);
    addFreeTextFieldButton.setShowDisabledIcon(false);
    addFreeTextFieldButton.setAutoFit(true);
    addFreeTextFieldButton.addClickHandler(new ClickHandler() {
      public void onClick(final ClickEvent event) {
        CatalogService.Util.getInstance().addFreeTextField(addFreeTextFieldForm.getValueAsString("name"), new AsyncCallbackImpl<Void>());
      }
    });
    final Layout freeTextFieldLayout = new CanvasWithOpsLayout<DynamicForm>(addFreeTextFieldForm, addFreeTextFieldButton);
    return freeTextFieldLayout;
  }

  private static Layout getAddEnumFieldLayout() {
    final DynamicForm addEnumFieldForm = new DynamicForm();
    final TextItem fieldNameItem = new TextItem("name", "Name");
    final TextAreaItem enumValuesItem = new TextAreaItem("values", "Values (Comman Seperated)");
    addEnumFieldForm.setItems(fieldNameItem, enumValuesItem);

    final Button addEnumFieldButton = SmartUtils.getButton("Add Enumerated Field", Resources.ADD, 150);
    addEnumFieldButton.addClickHandler(new ClickHandler() {
      public void onClick(final ClickEvent event) {
        final String valuesStr = addEnumFieldForm.getValueAsString("values");
        final String[] valuesArray = valuesStr.split("\\s*,\\s*");
        CatalogService.Util.getInstance().addEnumField(addEnumFieldForm.getValueAsString("name"), Arrays.asList(valuesArray), new AsyncCallbackImpl<Void>());
      }
    });

    final Layout addEnumFieldLayout = new CanvasWithOpsLayout<DynamicForm>(addEnumFieldForm, addEnumFieldButton);
    return addEnumFieldLayout;
  }

  private static Layout getAddCategoryLayout() {
    final DynamicForm addCategoryForm = new DynamicForm();
    final TextItem categoryNameItem = new TextItem("name", "Name");
    final TextItem categoryDescriptionItem = new TextItem("description", "Description");
    addCategoryForm.setItems(categoryNameItem, categoryDescriptionItem);

    final Button addCategoryButton = SmartUtils.getButton("Add Category", Resources.ADD, 150);
    addCategoryButton.addClickHandler(new ClickHandler() {
      public void onClick(final ClickEvent event) {
        CatalogService.Util.getInstance().addCategory(addCategoryForm.getValueAsString("name"), addCategoryForm.getValueAsString("description"), new AsyncCallbackImpl<Void>());
      }
    });

    final Layout addCategoryLayout = new CanvasWithOpsLayout<DynamicForm>(addCategoryForm, addCategoryButton);
    return addCategoryLayout;
  }

  private static class CatalogAdminWindowComponentImpl extends WindowComponentImpl<Window> {
    CatalogAdminWindowComponentImpl() {

      final VLayout layout = new VLayout();
      layout.addMember(getAddCategoryLayout());
      layout.addMember(getAddFreeTextFieldLayout());
      layout.addMember(getAddEnumFieldLayout());      
      this.setWidget(PopOutWindowBuilder.titled("Catalog Admin").sized(500, 500).withContents(layout).get());
    }
  }

}
