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

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.fields.TextItem;

import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.WindowComponent;
import edu.umn.msi.tropix.webgui.client.constants.CatalogSearchPanelConstants;
import edu.umn.msi.tropix.webgui.client.search.SearchController;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.Form;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.client.widgets.WidgetSupplierImpl;
import edu.umn.msi.tropix.webgui.services.tropix.CatalogSearch;

public class CatalogQuickSearchWindowComponentSupplierImpl implements Supplier<WindowComponent<Window>> {
  private SearchController searchController;

  @Inject
  public void setSearchController(final SearchController searchController) {
    this.searchController = searchController;
  }

  public WindowComponent<Window> get() {
    return new CatalogSearchComponentImpl();
  }

  class CatalogSearchComponentImpl extends WindowComponentImpl<Window> {
    class ServiceSearchPanel extends WidgetSupplierImpl<CanvasWithOpsLayout<Form>> {
      private final Form form;
      private final CatalogSearchPanelConstants messages = (CatalogSearchPanelConstants) GWT.create(CatalogSearchPanelConstants.class);

      public ServiceSearchPanel() {
        form = new Form(new TextItem("query", "Query"));
        form.setValidationPredicate(new Predicate<Form>() {
          public boolean apply(final Form form) {
            return StringUtils.hasText(form.getValueAsString("query"));
          }          
        });        
        final Button searchButton = SmartUtils.getButton(messages.btnSearch(), Resources.FIND, searchCommand);
        SmartUtils.enabledWhenValid(searchButton, form);
        this.setWidget(new CanvasWithOpsLayout<Form>(form, searchButton, SmartUtils.getCancelButton(CatalogSearchComponentImpl.this)));
      }

      private final Command searchCommand = new Command() {
        public void execute() {
          search(form.getValueAsString("query").trim());
        }
      };

      private void search(final String searchText) {
        CatalogSearch.Util.getInstance().searchServices(searchText, searchController.startCatalogSearch("Services matching text " + searchText));
        CatalogSearchComponentImpl.this.get().destroy();
      }
    }

    CatalogSearchComponentImpl() {
      final ServiceSearchPanel panel = new ServiceSearchPanel();
      this.setWidget(PopOutWindowBuilder.titled("Lab Service Search").withIcon(Resources.FIND).autoSized().withContents(panel.get()).get());
    }
  }
}
