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

package edu.umn.msi.tropix.webgui.client.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Supplier;
import com.google.gwt.core.client.GWT;
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
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.ClickHandler;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.tab.Tab;

import edu.umn.msi.gwt.mvc.AppEvent;
import edu.umn.msi.gwt.mvc.ChangeEvent;
import edu.umn.msi.gwt.mvc.ChangeListener;
import edu.umn.msi.gwt.mvc.Model;
import edu.umn.msi.gwt.mvc.View;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.widgets.ContextAwareTab;

// TODO: Implement MenuIf functions
public class SearchView extends View implements Supplier<Tab> {
  private final ContextAwareTab resultTab = new ContextAwareTab();
  private final ListGrid grid = new ListGrid();
  private final DataSource dataSource = new DataSource();
  private ListGridRecord selectedRecord;
  private final Map<String, ListGridRecord> records = new HashMap<String, ListGridRecord>();

  private String getId() {
    return this.selectedRecord.getAttributeAsString("id");
  }

  private static String getStatusIcon(final SearchModel searchModel) {
    String icon = "images/default/icons/wait.gif";
    if(searchModel.isComplete()) {
      icon =  "images/kurumizawa/action_check.gif";      
    } else if(searchModel.isFailed()) {
      icon =  "images/kurumizawa/action_delete.gif";
    }
    return icon;
  }
  
  private final ChangeListener modelChangeListener = new ChangeListener() {
    public void modelChanged(final ChangeEvent event) {
      final SearchModel searchModel = (SearchModel) event.item;
      final String id = searchModel.getAsString("id");
      final ListGridRecord record = records.get(id);
      System.out.println("Updating");
      record.setAttribute("status", searchModel.getAsString("status"));
      record.setAttribute("statusIcon", getStatusIcon(searchModel));
      grid.updateData(record);
    }
  };

  public SearchView(final SearchController controller, final SearchCollectionModel searchCollectionModel) {
    super(controller);
    searchCollectionModel.addChangeListener(new ChangeListener() {
      public void modelChanged(final ChangeEvent event) {
        final SearchModel searchModel = (SearchModel) event.item;
        final String id = searchModel.getAsString("id");
        ListGridRecord record;
        switch(event.type) {
        case Model.Add:
          searchModel.addChangeListener(modelChangeListener);
          record = new ListGridRecord();
          record.setAttribute("name", searchModel.getAsString("name"));
          record.setAttribute("status", searchModel.getAsString("status"));
          record.setAttribute("statusIcon", getStatusIcon(searchModel));
          record.setAttribute("id", id);
          records.put(id, record);
          grid.addData(record);
          resultTab.selectTab();
          break;
        case Model.Remove:
          record = records.get(id);
          records.remove(id);
          grid.removeData(record);
          searchModel.removeChangeListener(modelChangeListener);
          break;
        }
      }
    });
    final DataSourceTextField field = new DataSourceTextField();
    field.setName("id");
    field.setPrimaryKey(true);
    this.dataSource.addField(field);
    this.dataSource.setClientOnly(true);

    this.grid.setDataSource(this.dataSource);
    this.grid.setAutoFetchData(true);
    this.grid.setAlternateRecordStyles(true);

    
    final ListGridField statusIconField = new ListGridField();
    statusIconField.setName("statusIcon");
    statusIconField.setTitle(" ");
    statusIconField.setWidth("24");
    statusIconField.setType(ListGridFieldType.IMAGE);
    statusIconField.setImageURLPrefix(GWT.getHostPageBaseURL());
    statusIconField.setImageURLSuffix("");
    
    final ListGridField nameField = new ListGridField("name", "Search Query");
    nameField.setType(ListGridFieldType.TEXT);
    nameField.setWidth("*");
    final ListGridField statusField = new ListGridField("status", "Status");
    statusField.setType(ListGridFieldType.TEXT);
    statusField.setWidth("25%");

    this.grid.setFields(statusIconField, nameField, statusField);
    this.grid.setEmptyMessage("No search results to show.");

    this.grid.addRecordClickHandler(new RecordClickHandler() {
      public void onRecordClick(final RecordClickEvent event) {
        selectedRecord = (ListGridRecord) event.getRecord();
        controller.handleEvent(new SearchAppEvents.ShowSearchEvent(getId()));
      }
    });

    final ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>(3);

    final MenuItem removeMenuItem = new MenuItem("Remove Results");
    removeMenuItem.addClickHandler(new ClickHandler() {
      public void onClick(final MenuItemClickEvent event) {
        controller.handleEvent(new SearchAppEvents.RemoveSearchEvent(getId()));
      }
    });

    this.grid.addCellContextClickHandler(new CellContextClickHandler() {
      public void onCellContextClick(final CellContextClickEvent event) {
        selectedRecord = event.getRecord();
      }
    });

    final Menu contextMenu = new Menu();

    final MenuItem separatorItem = new MenuItem();
    separatorItem.setIsSeparator(true);

    final MenuItem showMenuItem = new MenuItem("Show Window");
    showMenuItem.addClickHandler(new ClickHandler() {
      public void onClick(final MenuItemClickEvent event) {
        controller.handleEvent(new SearchAppEvents.ShowSearchEvent(getId()));
      }
    });

    final MenuItem hideMenuItem = new MenuItem("Hide Window");
    hideMenuItem.addClickHandler(new ClickHandler() {
      public void onClick(final MenuItemClickEvent event) {
        controller.handleEvent(new SearchAppEvents.HideSearchEvent(getId()));
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

  protected void handleEvent(final AppEvent event) {
  }

  public Tab get() {
    return this.resultTab;
  }

}
