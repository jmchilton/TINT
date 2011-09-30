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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import com.google.common.base.Supplier;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;

import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.catalog.beans.CustomQueryBean;
import edu.umn.msi.tropix.webgui.client.catalog.beans.Provider;
import edu.umn.msi.tropix.webgui.client.components.WindowComponent;
import edu.umn.msi.tropix.webgui.client.constants.CatalogSearchDetailsPanelConstants;
import edu.umn.msi.tropix.webgui.client.constants.CatalogSearchPanelConstants;
import edu.umn.msi.tropix.webgui.client.search.SearchController;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.Form;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.client.widgets.WidgetSupplierImpl;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils.Cleanable;
import edu.umn.msi.tropix.webgui.services.tropix.CatalogSearch;
import edu.umn.msi.tropix.webgui.services.tropix.CatalogServiceDefinition;

public class CatalogSearchWindowComponentSupplierImpl implements Supplier<WindowComponent<Window>> {
  private SearchController searchController;

  public WindowComponent<Window> get() {
    return new CatalogSearchComponentImpl();
  }

  @Inject
  public void setSearchController(final SearchController searchController) {
    this.searchController = searchController;
  }

  class CatalogSearchComponentImpl extends WindowComponentImpl<Window> {
    class ServiceAdvSearchPanel extends WidgetSupplierImpl<CanvasWithOpsLayout<DynamicForm>> {
      private static final String ANY = "Any criteria";
      private static final String ALL = "All criteria";
      private static final String ID_SEP = "@@@";

      private CatalogSearchDetailsPanelConstants detailsMessages = (CatalogSearchDetailsPanelConstants) GWT.create(CatalogSearchDetailsPanelConstants.class);
      private CatalogSearchPanelConstants messages = (CatalogSearchPanelConstants) GWT.create(CatalogSearchPanelConstants.class);

      private final TextItem searchItem;
      private final TextItem authorItem;
      private final SelectItem providerItem;
      private final RadioGroupItem searchType;

      public ServiceAdvSearchPanel() {

        this.searchItem = new TextItem("Search for");

        final Button searchButton = SmartUtils.getButton(this.messages.btnSearch(), Resources.FIND, searchCommand);
        this.authorItem = new TextItem("Publisher");
        this.authorItem.setPrompt("Publisher of the entry");

        this.providerItem = new SelectItem(this.detailsMessages.lblProvider());
        this.providerItem.setPrompt(this.detailsMessages.lblProviderHelp());

        this.searchType = new RadioGroupItem("Matches");
        this.searchType.setValueMap(ServiceAdvSearchPanel.ALL, ServiceAdvSearchPanel.ANY);
        this.searchType.setValue(ServiceAdvSearchPanel.ALL);

        final Form form = new Form(this.searchItem, this.authorItem, this.providerItem, this.searchType);
        form.setWrapItemTitles(false);
        final CanvasWithOpsLayout<DynamicForm> layout = new CanvasWithOpsLayout<DynamicForm>(form, searchButton, SmartUtils.getCancelButton(CatalogSearchComponentImpl.this));
        layout.setHeight(180);
        layout.setWidth(280);
        setWidget(layout);
        final Cleanable loadingManager = SmartUtils.indicateLoading(get());
        populateProviderPanel(loadingManager);

      }

      private final Command searchCommand = new Command() {        
        public void execute() {
          final CustomQueryBean queryBean = new CustomQueryBean();
          final ArrayList<String> criteria = new ArrayList<String>(5);
          queryBean.setFreeText((String) searchItem.getValue());
          queryBean.setAuthor((String) authorItem.getValue());
          if(!providerItem.getValue().equals("*select*")) {
            criteria.add("from Provider " + providerItem.getDisplayValue());
            final String providerId = (String) providerItem.getValue();
            final String[] idParts = providerId.split(ID_SEP);
            queryBean.setCatalogId(idParts[0]);
            queryBean.setProivderID(idParts[1]);
          }
          queryBean.setStatus("ACTIVE");
          String combiner = " or ";
          if(searchType.getValue().equals(ServiceAdvSearchPanel.ALL)) {
            combiner = " and ";
            queryBean.setIntersect(true);
          }
          if(StringUtils.hasText(queryBean.getFreeText())) {
            criteria.add("matching text " + queryBean.getFreeText());
          }
          if(StringUtils.hasText(queryBean.getAuthor())) {
            criteria.add("with author " + authorItem.getDisplayValue());
          }
          StringBuilder searchName = new StringBuilder(" Lab Services ");
          if(criteria.size() > 0) {
            boolean first = true;
            for(final String criterion : criteria) {
              if(first) {
                first = false;
              } else {
                searchName.append(combiner);
              }
              searchName.append(criterion);
            }
          } else {
            searchName = new StringBuilder("All " + searchName.toString());
          }
          CatalogSearch.Util.getInstance().advancedSearch(queryBean, searchController.startCatalogSearch(searchName.toString()));
          CatalogSearchComponentImpl.this.get().destroy();
        }
      };

      private void populateProviderPanel(final Cleanable loadingManager) {
        CatalogServiceDefinition.Util.getInstance().getProviders(new AsyncCallbackImpl<Collection<Provider>>(loadingManager) {
          @Override
          public void onSuccess(final Collection<Provider> providers) {
            final LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
            valueMap.put("*select*", "*select*");
            for(final Provider category : providers) {
              final String id = category.getCatalogId() + ID_SEP + category.getId();
              valueMap.put(id, category.getName());
            }
            providerItem.setValueMap(valueMap);
            providerItem.setValue("*select*");
            loadingManager.close();
          }
        });
      }

    }

    CatalogSearchComponentImpl() {
      final ServiceAdvSearchPanel panel = new ServiceAdvSearchPanel(); 
      this.setWidget(PopOutWindowBuilder.titled("Lab Service Search").withIcon(Resources.FIND).autoSized().withContents(panel.get()).get());
    }
  }
}
