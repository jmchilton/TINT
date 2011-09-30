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
import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CanvasItem;
import com.smartgwt.client.widgets.form.fields.PickTreeItem;
import com.smartgwt.client.widgets.form.fields.TextItem;

import edu.umn.msi.tropix.models.utils.TropixObjectType;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.GridUserItemComponent;
import edu.umn.msi.tropix.webgui.client.components.WindowComponent;
import edu.umn.msi.tropix.webgui.client.constants.ConstantsInstances;
import edu.umn.msi.tropix.webgui.client.models.TropixTypeModel;
import edu.umn.msi.tropix.webgui.client.search.SearchController;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.Form;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.object.LocalSearchService;

public class SearchWindowComponentSupplierImpl implements Supplier<WindowComponent<Window>> {
  private Supplier<GridUserItemComponent> gridUserItemComponentSupplier;
  private SearchController searchController;

  @Inject
  public void setSearchController(final SearchController searchController) {
    this.searchController = searchController;
  }

  private class SearchWindowComponentImpl extends WindowComponentImpl<Window> {
    private final TextItem nameItem, descriptionItem;
    private final PickTreeItem typeItem;
    private final GridUserItemComponent gridUserItemComponent;

    private String getSearchName() {
      String searchName;
      if(StringUtils.hasText(this.nameItem.getValue())) {
        searchName = "Items with Name Containing " + this.nameItem.getValue();
      } else if(StringUtils.hasText(this.descriptionItem.getValue())) {
        searchName = "Items with Description Containing " + this.descriptionItem.getValue();
      } else if(!this.gridUserItemComponent.getSelectedUserLabel().equals(ConstantsInstances.COMPONENT_INSTANCE.gridUserAnyone())) {
        searchName = "Items Owned by " + this.gridUserItemComponent.getSelectedUserLabel();
      } else {
        searchName = "Items of Type " + this.typeItem.getValue();
      }
      return searchName;
    }

    SearchWindowComponentImpl() {
      this.setWidget(PopOutWindowBuilder.titled("Search").autoSized().withIcon(Resources.FIND).get());

      this.nameItem = new TextItem("name");
      this.nameItem.setTitle("Name");

      this.descriptionItem = new TextItem("description");
      this.descriptionItem.setTitle("Description");

      this.typeItem = new PickTreeItem("type");
      this.typeItem.setTitle("Item Type");
      this.typeItem.setValueField("type");
      this.typeItem.setCanSelectParentItems(true);
      this.typeItem.setValueTree(TropixTypeModel.getTree());

      this.gridUserItemComponent = gridUserItemComponentSupplier.get();
      final CanvasItem ownerItem = this.gridUserItemComponent.get();
      final Form form = new Form(nameItem, descriptionItem, typeItem, ownerItem);
      form.setWrapItemTitles(false);
      final Button button = SmartUtils.getButton("Search", Resources.FIND, new Command() {
        public void execute() {
          final String name = form.getValueAsString("name");
          final String desc = form.getValueAsString("description");
          // I can't seem to get TROPIX_OBJECT ("Any Object") to be selected by default, so I need
          // to do a null check here.
          final String typeStr = form.getValueAsString("type");
          final TropixObjectType type = typeStr == null ? TropixObjectTypeEnum.TROPIX_OBJECT : TropixObjectTypeEnum.valueOf(form.getValueAsString("type"));
          LocalSearchService.Util.getInstance().fullSearch(name, desc, gridUserItemComponent.getSelectedUserId(), type, searchController.startLocalSearch(getSearchName()));
          get().destroy();
        }
      });       
      final CanvasWithOpsLayout<DynamicForm> layout = new CanvasWithOpsLayout<DynamicForm>(form, button, SmartUtils.getCancelButton(this));
      this.get().addItem(layout);
    }
  }

  public WindowComponent<Window> get() {
    return new SearchWindowComponentImpl();
  }

  @Inject
  public void setGridUserItemComponentSupplier(final Supplier<GridUserItemComponent> gridUserItemComponentSupplier) {
    this.gridUserItemComponentSupplier = gridUserItemComponentSupplier;
  }

}
