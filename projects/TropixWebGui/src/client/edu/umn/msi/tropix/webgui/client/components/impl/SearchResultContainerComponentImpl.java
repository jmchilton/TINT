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
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.ClickHandler;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.tab.Tab;

import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.SearchResultContainerComponent;
import edu.umn.msi.tropix.webgui.client.widgets.ContextAwareTab;

public class SearchResultContainerComponentImpl implements SearchResultContainerComponent {
  private final ContextAwareTab resultTab = new ContextAwareTab();
  private final ListGrid grid = new ListGrid();
  private final DataSource dataSource = new DataSource();
  private ListGridRecord selectedRecord;
  private final Map<String, ListGridRecord> records = new HashMap<String, ListGridRecord>();
  private int curId = 0;

  public SearchResultContainerComponentImpl() {
    final DataSourceTextField field = new DataSourceTextField();
    field.setName("id");
    field.setPrimaryKey(true);
    this.dataSource.addField(field);
    this.dataSource.setClientOnly(true);

    this.grid.setDataSource(this.dataSource);
    this.grid.setAutoFetchData(true);
    this.grid.setAlternateRecordStyles(true);

    final ListGridField nameField = new ListGridField("name", "Name");
    nameField.setType(ListGridFieldType.TEXT);
    nameField.setWidth("*");
    final ListGridField statusField = new ListGridField("status", "Status");
    statusField.setType(ListGridFieldType.TEXT);
    statusField.setWidth("25%");

    this.grid.setFields(nameField, statusField);
    this.grid.setEmptyMessage("No search results to show.");

    this.grid.addRecordClickHandler(new RecordClickHandler() {
      public void onRecordClick(final RecordClickEvent event) {
        final Layout widget = (Layout) event.getRecord().getAttributeAsObject("widget");
        if(widget != null) {
          widget.show();
        }
      }
    });

    final ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>(3);

    final MenuItem removeMenuItem = new MenuItem("Remove Results");
    removeMenuItem.addClickHandler(new ClickHandler() {
      public void onClick(final MenuItemClickEvent event) {
        final ListGridRecord record = SearchResultContainerComponentImpl.this.records.get(SearchResultContainerComponentImpl.this.selectedRecord.getAttribute("id"));
        SearchResultContainerComponentImpl.this.grid.removeData(record);
        final Layout widget = (Layout) SearchResultContainerComponentImpl.this.selectedRecord.getAttributeAsObject("widget");
        if(widget != null) {
          widget.destroy();
        }
      }
    });

    this.grid.addCellContextClickHandler(new CellContextClickHandler() {
      public void onCellContextClick(final CellContextClickEvent event) {
        SearchResultContainerComponentImpl.this.selectedRecord = event.getRecord();
        final Layout widget = SearchResultContainerComponentImpl.this.getWidget();
        for(final MenuItem menuItem : menuItems) {
          menuItem.setEnabled(widget != null);
        }
        removeMenuItem.setEnabled(!selectedRecord.getAttribute("status").equals("Searching..."));
      }
    });

    final Menu contextMenu = new Menu();

    final MenuItem separatorItem = new MenuItem();
    separatorItem.setIsSeparator(true);

    final MenuItem showMenuItem = new MenuItem("Show Window");
    showMenuItem.addClickHandler(new ClickHandler() {
      public void onClick(final MenuItemClickEvent event) {
        final Layout widget = SearchResultContainerComponentImpl.this.getWidget();
        if(widget != null) {
          widget.show();
        }
      }
    });

    final MenuItem hideMenuItem = new MenuItem("Hide Window");
    hideMenuItem.addClickHandler(new ClickHandler() {
      public void onClick(final MenuItemClickEvent event) {
        final Layout widget = SearchResultContainerComponentImpl.this.getWidget();
        if(widget != null) {
          widget.hide();
        }
      }
    });

    contextMenu.setItems(removeMenuItem, separatorItem, showMenuItem, hideMenuItem);
    menuItems.add(hideMenuItem);
    menuItems.add(showMenuItem);
    this.grid.setContextMenu(contextMenu);

    this.resultTab.setTitle("Search Results");
    this.resultTab.setIcon(Resources.FIND);

    final VLayout tabLayout = new VLayout();
    tabLayout.addMember(this.grid);
    this.resultTab.setPane(tabLayout);
  }

  private Layout getWidget() {
    final Layout widget = (Layout) this.selectedRecord.getAttributeAsObject("widget");
    return widget;
  }

  public Tab get() {
    return this.resultTab;
  }

  public AsyncCallback<Layout> startSearch(final String searchName) {
    this.resultTab.selectTab();
    final ListGridRecord record = new ListGridRecord();
    record.setAttribute("name", searchName);
    record.setAttribute("status", "Searching...");
    final String id = "" + this.curId++;
    record.setAttribute("id", id);
    this.records.put(id, record);
    this.grid.addData(record);
    return new AsyncCallback<Layout>() {
      public void onFailure(final Throwable caught) {
        record.setAttribute("status", "Failed");
        SearchResultContainerComponentImpl.this.grid.updateData(record);
      }

      public void onSuccess(final Layout result) {
        result.setTitle(record.getAttribute("name"));
        record.setAttribute("widget", result);
        record.setAttribute("status", "Complete");
        result.show();
        SearchResultContainerComponentImpl.this.grid.updateData(record);
      }
    };
  }

}
