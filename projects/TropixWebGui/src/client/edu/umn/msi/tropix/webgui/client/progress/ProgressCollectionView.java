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

package edu.umn.msi.tropix.webgui.client.progress;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Supplier;
import com.google.gwt.core.client.GWT;
import com.google.gwt.widgetideas.client.ProgressBar;
import com.google.gwt.widgetideas.client.ProgressBar.TextFormatter;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
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
import edu.umn.msi.tropix.webgui.services.message.ProgressMessage;

public class ProgressCollectionView extends View implements Supplier<Tab> {
  private static int lastRecordId = 0;
  private final ListGrid grid = new ListGrid();
  private final ProgressCollectionModel progressCollectionModel;
  private final Map<String, ListGridRecord> gridRecords = new HashMap<String, ListGridRecord>();
  private ListGridRecord selectedRecord = null;
  private final DataSource dataSource = new DataSource();
  private Tab tab;

  /**
   * Overloaded from View, this is called once before handleEvent is called.
   */
  protected void initialize() {
    final DataSourceTextField field = new DataSourceTextField();
    field.setName("id");
    field.setPrimaryKey(true);
    this.dataSource.addField(field);
    this.dataSource.setClientOnly(true);
    this.grid.setID("ProgressListGrid");
    this.grid.setDataSource(this.dataSource);
    this.grid.setAutoFetchData(true);
    this.grid.setEmptyMessage("No jobs to show.");

    final ListGridField statusField = new ListGridField();
    statusField.setName("status");
    statusField.setTitle(" ");
    statusField.setWidth("24");
    statusField.setType(ListGridFieldType.IMAGE);
    statusField.setImageURLPrefix(GWT.getHostPageBaseURL());
    statusField.setImageURLSuffix("");
    final ListGridField nameField = new ListGridField();
    nameField.setName("name");
    nameField.setTitle("Job");
    nameField.setWidth("20%");
    final ListGridField stateField = new ListGridField();
    stateField.setName("state");
    stateField.setTitle("Step");
    stateField.setWidth("40%");
    final ListGridField percentField = new ListGridField();
    percentField.setName("percent");
    percentField.setTitle("Status");
    percentField.setWidth("40%");
    percentField.setCellAlign(Alignment.LEFT);
    percentField.setCellFormatter(new CellFormatter() {
      public String format(final Object value, final ListGridRecord record, final int rowNum, final int colNum) {
        final ProgressBar bar = (ProgressBar) record.getAttributeAsObject("percentObject");
        System.out.println(bar.toString());
        return bar.toString();
      }
    });
    this.grid.setFields(statusField, nameField, stateField, percentField);

    this.grid.setWidth100();
    this.grid.setHeight100();

    this.tab = new Tab("Jobs");
    this.tab.setPane(this.grid);
    this.tab.setIcon(Resources.JOB);

    final Menu menu = new Menu();
    final MenuItem cancelItem = new MenuItem();
    cancelItem.setTitle("Cancel Job");
    cancelItem.setIcon(Resources.CROSS);
    cancelItem.addClickHandler(new ClickHandler() {
      public void onClick(final MenuItemClickEvent event) {
        final String jobId = selectedRecord.getAttribute("id");
        final String workflowId = selectedRecord.getAttribute("workflowId");
        getController().handleEvent(new ProgressAppEvent.CancelAppEvent(workflowId, jobId));
      }
    });
    final MenuItem clearItem = new MenuItem();
    clearItem.setTitle("Clear Job");
    clearItem.setIcon(Resources.CROSS);
    clearItem.addClickHandler(new ClickHandler() {
      public void onClick(final MenuItemClickEvent event) {
        final String id = selectedRecord.getAttribute("id");
        final ProgressModel model = progressCollectionModel.getProgressModel(id);
        progressCollectionModel.remove(model);
      }
    });
    final MenuItem clearAllItem = new MenuItem();
    clearAllItem.setTitle("Clear All Jobs");
    clearAllItem.setIcon(Resources.CROSS);
    clearAllItem.addClickHandler(new ClickHandler() {
      public void onClick(final MenuItemClickEvent event) {
        progressCollectionModel.removeAll();
      }
    });

    final MenuItem clearAllCompleteItem = new MenuItem();
    clearAllCompleteItem.setTitle("Clear All Completed Jobs");
    clearAllCompleteItem.setIcon(Resources.CROSS);
    clearAllCompleteItem.addClickHandler(new ClickHandler() {
      public void onClick(final MenuItemClickEvent event) {
        progressCollectionModel.removeAllComplete();
      }
    });

    final MenuItem sep = new MenuItem();
    sep.setIsSeparator(true);

    menu.setItems(cancelItem, clearItem, sep, clearAllCompleteItem, clearAllItem);
    this.grid.addCellContextClickHandler(new CellContextClickHandler() {
      public void onCellContextClick(final CellContextClickEvent event) {
        selectedRecord = event.getRecord();
      }
    });
    this.grid.setContextMenu(menu);
  }

  protected ListGridRecord getRecord(final ProgressModel progressModel) {
    final String name = progressModel.getAsString("name");
    final String status = progressModel.getAsString("step");
    final ProgressBar bar = new ProgressBar(0.0, 1.0, 0.0);
    bar.setHeight("15px");
    bar.setWidth("95%");
    bar.setProgress(0.0);

    final ListGridRecord record = new ListGridRecord();
    record.setAttribute("recordId", lastRecordId++);
    record.setAttribute("workflowId", progressModel.getAsString("workflowId"));
    record.setAttribute("id", progressModel.getAsString("id"));
    record.setAttribute("status", "images/default/icons/wait.gif");
    record.setAttribute("name", name);
    record.setAttribute("state", status);
    record.setAttribute("percent", "placeholder");
    record.setAttribute("percentObject", bar);
    updateRecord(record, progressModel);
    return record;
  }

  private static void updateBar(final ProgressModel progressModel, final ProgressBar bar) {
    final String stepStatus = (String) progressModel.get("stepStatus");
    if(stepStatus == null) {
      bar.setTextFormatter(null);
      bar.setProgress(((Float) progressModel.get("stepPercent")).doubleValue());
    } else {
      bar.setTextFormatter(new TextFormatter() {
        public String getText(final ProgressBar bar, final double curProgress) {
          return stepStatus;
        }
      });
      bar.setProgress(((Float) progressModel.get("stepPercent")).doubleValue());
    }
  }

  private static void updateRecord(final ListGridRecord record, final ProgressModel progressModel) {
    final int status = ((Integer) progressModel.get("jobStatus")).intValue();
    if(status == ProgressMessage.JOB_COMPLETE) {
      record.setAttribute("status", "images/kurumizawa/action_check.gif");
    } else if(status == ProgressMessage.JOB_FAILED) {
      record.setAttribute("status", "images/kurumizawa/action_delete.gif");
    }
    record.setAttribute("state", progressModel.get("step"));
    final ProgressBar bar = (ProgressBar) record.getAttributeAsObject("percentObject");
    updateBar(progressModel, bar);
  }

  private ChangeListener progressCollectionModelChangeListener = new ChangeListener() {

    public void modelChanged(final ChangeEvent event) {
      final ProgressModel progressModel = (ProgressModel) event.item;
      final String id = progressModel.getAsString("id");
      ListGridRecord record;
      switch(event.type) {
      case Model.Add:
        record = getRecord(progressModel);
        gridRecords.put(id, record);
        grid.addData(record);
        progressModel.addChangeListener(progressModelChangeListener);
        break;
      case Model.Remove:
        record = gridRecords.get(id);
        gridRecords.remove(id);
        grid.removeData(record);
        progressModel.removeChangeListener(progressModelChangeListener);
        break;
      }
    }
  };

  private ChangeListener progressModelChangeListener = new ChangeListener() {

    public void modelChanged(final ChangeEvent event) {
      if(event.type == Model.Update) {
        final ProgressModel progressModel = (ProgressModel) event.item;
        final String id = progressModel.getAsString("id");
        final ListGridRecord record = gridRecords.get(id);
        if(record == null) {
          throw new IllegalStateException("No table row corresponding to job with id " + id);
        } else {
          updateRecord(record, progressModel);
          grid.markForRedraw();
        }
      }
    }

  };

  public ProgressCollectionView(final ProgressControllerImpl controller, final ProgressCollectionModel progressCollectionModel) {
    super(controller);
    this.progressCollectionModel = progressCollectionModel;
    this.progressCollectionModel.addChangeListener(this.progressCollectionModelChangeListener);
  }

  protected void handleEvent(final AppEvent event) {
  }

  public Tab get() {
    return this.tab;
  }
}
