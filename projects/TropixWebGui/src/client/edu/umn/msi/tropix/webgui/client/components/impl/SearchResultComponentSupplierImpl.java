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

import java.util.List;

import com.google.common.base.Supplier;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import edu.umn.msi.tropix.models.utils.TropixObjectContext;
import edu.umn.msi.tropix.webgui.client.components.ResultComponent;
import edu.umn.msi.tropix.webgui.client.mediators.NavigationSelectionMediator;
import edu.umn.msi.tropix.webgui.client.models.SearchResultUtils;
import edu.umn.msi.tropix.webgui.client.smart.handlers.CommandClickHandlerImpl;
import edu.umn.msi.tropix.webgui.client.smart.handlers.CommandDoubleClickHandlerImpl;
import edu.umn.msi.tropix.webgui.client.widgets.Buttons;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.ClientListGrid;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.object.SearchResult;

public class SearchResultComponentSupplierImpl implements Supplier<ResultComponent<List<SearchResult>, Window>> {
  private NavigationSelectionMediator pageDisplayListener;

  @Inject
  public void setPageDisplayListener(final NavigationSelectionMediator pageDisplayListener) {
    this.pageDisplayListener = pageDisplayListener;
  }

  class SearchResultComponentImpl extends WindowComponentImpl<Window> implements ResultComponent<List<SearchResult>, Window> {
    private ClientListGrid resultGrid;
    private final Command selectResultCommand = new Command() {
      public void execute() {
        final ListGridRecord selectedRecord = SearchResultComponentImpl.this.resultGrid.getSelectedRecord();
        if(selectedRecord != null) {
          SearchResultComponentSupplierImpl.this.pageDisplayListener.go((TropixObjectContext) selectedRecord.getAttributeAsObject("object"));
        }
      }
    };

    private void initResultGrid() {
      this.resultGrid = new ClientListGrid();

      final ListGridField nameField = new ListGridField("name", "Name");
      nameField.setType(ListGridFieldType.TEXT);

      final ListGridField typeField = new ListGridField("icon", " ");
      typeField.setType(ListGridFieldType.IMAGE);
      typeField.setImageURLPrefix(GWT.getHostPageBaseURL());
      typeField.setImageURLSuffix("");
      typeField.setWidth("16px");

      final ListGridField descriptionField = new ListGridField("description", "Description");
      descriptionField.setType(ListGridFieldType.TEXT);

      final ListGridField ownerField = new ListGridField("owner", "Owner");
      ownerField.setType(ListGridFieldType.TEXT);
      this.resultGrid.setSelectionType(SelectionStyle.SINGLE);
      this.resultGrid.setFields(typeField, nameField, descriptionField, ownerField);
      this.resultGrid.addDoubleClickHandler(new CommandDoubleClickHandlerImpl(this.selectResultCommand));
    }

    SearchResultComponentImpl() {
      this.initResultGrid();

      final Button goButton = Buttons.getGoButton();
      goButton.setDisabled(true);
      SmartUtils.enabledWhenHasSelection(goButton, this.resultGrid);
      goButton.addClickHandler(new CommandClickHandlerImpl(this.selectResultCommand));
      final CanvasWithOpsLayout<ListGrid> layout = new CanvasWithOpsLayout<ListGrid>(this.resultGrid, goButton);
      layout.setHeight100();
      this.setWidget(PopOutWindowBuilder.titled("Search Results").sized(500, 200).hideOnClose().withContents(layout).get());
    }

    public void setResults(final List<SearchResult> results) {
      for(final SearchResult result : results) {
        this.resultGrid.getClientDataSource().addData(SearchResultUtils.toRecord(result));
      }
    }
  }

  public ResultComponent<List<SearchResult>, Window> get() {
    return new SearchResultComponentImpl();
  }

}
