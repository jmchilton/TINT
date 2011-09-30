package edu.umn.msi.tropix.webgui.client.components.newwizards;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.smartgwt.client.core.DataClass;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.util.ValueCallback;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.Lists;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.ClientListGrid;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPageImpl;

// TODO: Handle duplicate names
public class ScaffoldQuantCategoriesWizardPageImpl extends WizardPageImpl<Canvas> {
  private final ClientListGrid listGrid;
  private final DataSource dataSource;
  private final Button removeButton;
  private final Listener<List<String>> categoriesListener;

  ScaffoldQuantCategoriesWizardPageImpl(final Listener<List<String>> categoriesListener) {
    super.setTitle("Q+ Categories");
    super.setDescription("Specify Q+ categories.");
    super.setValid(true);
    this.categoriesListener = categoriesListener;
    final DataSourceField nameField = SmartUtils.getFieldBuilder("name", "Name").editable().primary().get();

    dataSource = SmartUtils.newDataSourceWithFields(nameField);
    listGrid = new ClientListGrid(dataSource);

    // Setup list grid
    listGrid.setCanEdit(true);
    listGrid.setEditByCell(true);
    listGrid.setEditEvent(ListGridEditEvent.DOUBLECLICK);

    final Button addButton = SmartUtils.getButton("Add...", Resources.ADD, new Command() {
      public void execute() {
        ValueCallback callback = new ValueCallback() {

          public void execute(String value) {
            addRecord(value);
          }

        };
        SC.askforValue("What is the name of the quant category you would like to add?", callback);
      }
    });

    removeButton = SmartUtils.getButton("Remove", Resources.CROSS, new Command() {
      public void execute() {
        removeRecord(listGrid.getSelectedRecord());
      }
    });

    // SmartGWT bug hack: Have to delay the add or add two records or this just doesn't work.
    new Timer() {
      public void run() {
        addRecord("Reference");
      }
    }.schedule(1);

    final CanvasWithOpsLayout<Canvas> layout = new CanvasWithOpsLayout<Canvas>(listGrid, addButton, removeButton);
    this.setCanvas(layout);
  }

  private void addRecord(final String name) {
    final ListGridRecord record = new ListGridRecord();
    record.setAttribute("name", name);
    dataSource.addData(record);
    updateListener();
  }

  // Stupid hack to handle bug in SmartGWT.
  private void updateListener() {
    new Timer() {
      public void run() {
        updateListener_();
      }
    }.schedule(1);
  }

  private void updateListener_() {
    final List<String> categories = Lists.newArrayList();
    for(DataClass data : dataSource.getTestData()) {
      categories.add(data.getAttribute("name"));
    }
    Log.debug("Updating categories to " + StringUtils.join(categories));
    categoriesListener.onEvent(categories);
  }

  private void removeRecord(final ListGridRecord record) {
    if(!"Reference".equals(record.getAttribute("name"))) {
      dataSource.removeData(record);
      updateListener();
    }
  }

}
