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
import com.smartgwt.client.widgets.form.fields.TextItem;

import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.WindowComponent;
import edu.umn.msi.tropix.webgui.client.search.SearchController;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.Form;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.object.LocalSearchService;

public class QuickSearchWindowComponentSupplierImpl implements Supplier<WindowComponent<Window>> {
  private SearchController searchController;

  @Inject
  public void setSearchController(final SearchController searchController) {
    this.searchController = searchController;
  }

  class QuickSearchWindowComponentImpl extends WindowComponentImpl<Window> {
    QuickSearchWindowComponentImpl() {
      final Form form = new Form(new TextItem("query", "Query"));
      final Button button = SmartUtils.getButton("Search", Resources.FIND, new Command() {
        public void execute() {
          final String query = form.getValueAsString("query");
          LocalSearchService.Util.getInstance().quickSearch(query, searchController.startLocalSearch(query));
          get().destroy();
        }
      });
      final CanvasWithOpsLayout<DynamicForm> layout = new CanvasWithOpsLayout<DynamicForm>(form, button, SmartUtils.getCancelButton(this));
      setWidget(PopOutWindowBuilder.titled("Quick Search").autoSized().withIcon(Resources.FIND).withContents(layout).get());
    }
  }

  public WindowComponent<Window> get() {
    return new QuickSearchWindowComponentImpl();
  }

}
