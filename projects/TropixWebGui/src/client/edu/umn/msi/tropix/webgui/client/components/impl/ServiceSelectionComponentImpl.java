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

import com.google.gwt.user.client.Command;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.ClickHandler;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

import edu.umn.msi.tropix.client.services.GridService;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.components.ServiceSelectionComponent;
import edu.umn.msi.tropix.webgui.client.smart.handlers.CommandSelectionChangedHandlerImpl;
import edu.umn.msi.tropix.webgui.client.widgets.ClientListGrid;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.gridservices.GetGridServices;

public class ServiceSelectionComponentImpl<T extends GridService> extends SelectionComponentBaseImpl<T, ListGrid> implements ServiceSelectionComponent<T> {
  private String servicesType;
  // Ugly hack to account for the fact that calling grid.selectRecord(record)
  // and then
  // calling grid.getSelectedRecord() seems to return null.
  private ListGridRecord initialRecord = null;

  public void setServicesType(final String servicesType) {
    this.servicesType = servicesType;
  }

  public void fetchServices() {
    GetGridServices.Util.getInstance().getServices(this.servicesType, new FillGridCallback());
  }

  public ServiceSelectionComponentImpl() {
    this.setWidget(new ClientListGrid("Host"));
    this.get().setEmptyMessage("<i>Loading services...</i>");
    this.get().setFields(this.getFields());
    this.get().addSelectionChangedHandler(new CommandSelectionChangedHandlerImpl(new Command() {
      public void execute() {
        ServiceSelectionComponentImpl.this.getOnSelectionChangedCommand().execute();
        ServiceSelectionComponentImpl.this.initialRecord = null;
      }
    }));
    final Menu contextMenu = new Menu();
    final MenuItem reloadItem = new MenuItem("Reload service list");
    reloadItem.addClickHandler(new ClickHandler() {
      public void onClick(final MenuItemClickEvent event) {
        SmartUtils.removeAllRecords(ServiceSelectionComponentImpl.this.get());
        ServiceSelectionComponentImpl.this.fetchServices();
      }
    });
    contextMenu.addItem(reloadItem);
    // TODO: Fix this menu and add it back
    get().setContextMenu(contextMenu);

  }

  protected ListGridRecord getRecord(final T gridService) {
    final ListGridRecord record = new ListGridRecord();
    record.setAttribute("Name", gridService.getServiceName());
    record.setAttribute("Host", gridService.getServiceAddress());
    record.setAttribute("object", gridService);
    return record;
  }

  @SuppressWarnings("unchecked")
  public T getSelection() {
    final ListGridRecord selectedRecord = this.initialRecord != null ? this.initialRecord : this.get().getSelectedRecord();
    System.out.println("Selected record is null " + (selectedRecord == null));
    T selection = null;
    if(selectedRecord != null) {
      selection = (T) selectedRecord.getAttributeAsObject("object");
    }
    return selection;
  }

  protected static ListGridField getNameField() {
    final ListGridField nameField = new ListGridField("Name");
    nameField.setType(ListGridFieldType.TEXT);
    return nameField;
  }

  protected static ListGridField getHostField() {
    final ListGridField hostField = new ListGridField("Host");
    hostField.setType(ListGridFieldType.TEXT);
    return hostField;
  }

  protected ListGridField[] getFields() {
    return new ListGridField[] {ServiceSelectionComponentImpl.getNameField(), ServiceSelectionComponentImpl.getHostField()};
  }

  private class FillGridCallback extends AsyncCallbackImpl<Iterable<GridService>> {
    @Override
    public void onFailure(final Throwable t) {
      ServiceSelectionComponentImpl.this.get().setEmptyMessage("<i>Failed to load services.</i>");
      super.onFailure(t);
    }

    @Override
    public void onSuccess(final Iterable<GridService> result) {
      ServiceSelectionComponentImpl.this.get().setEmptyMessage("<i>No services to show.</i>");
      int count = 0;
      ListGridRecord lastRecord = null;
      for(final GridService gridServiceRaw : result) {
        @SuppressWarnings("unchecked")
        final T gridService = (T) gridServiceRaw;
        final ListGridRecord record = ServiceSelectionComponentImpl.this.getRecord(gridService);
        if(record != null) {
          ServiceSelectionComponentImpl.this.get().addData(record);
        }
        ServiceSelectionComponentImpl.this.get().redraw();
        count++;
        lastRecord = record;
      }
      if(count == 1) {
        ServiceSelectionComponentImpl.this.initialRecord = lastRecord;
        ServiceSelectionComponentImpl.this.get().selectRecord(lastRecord);
      }
    }
  }
}
