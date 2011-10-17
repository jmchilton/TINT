package edu.umn.msi.tropix.webgui.client.components.impl;

import java.util.List;

import com.google.common.base.Supplier;
import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.WindowComponent;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.ClientListGrid;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.object.FolderService;

public class ManageGroupFoldersWindowComponentSupplierImpl implements Supplier<WindowComponent<Window>> {
  private static final String NAME_FIELD_INDEX = "name";

  public ManageGroupFoldersWindowComponentSupplierImpl() {
  }

  private Supplier<? extends Command> addGroupFolderComponentSupplier;

  @Inject
  public void setAddCatalogProviderComponentSupplier(@Named("addGroupFolder") final Supplier<? extends Command> addGroupFolderComponentSupplier) {
    this.addGroupFolderComponentSupplier = addGroupFolderComponentSupplier;
  }

  public WindowComponent<Window> get() {
    return new ManageGroupFoldersWindowComponentImpl();
  }

  class ManageGroupFoldersWindowComponentImpl extends WindowComponentImpl<Window> {
    private DataSource dataSource;
    private ClientListGrid listGrid = new ClientListGrid();

    private void reload() {
      this.listGrid.deselectAllRecords();
      SmartUtils.removeAllRecords(listGrid);
      FolderService.Util.getInstance().getAllGroupFolders(new AsyncCallbackImpl<List<Folder>>() {
        @Override
        public void onSuccess(final List<Folder> folders) {
          for(final Folder folder : folders) {
            addRecord(folder);
          }
        }
      });
    }

    ManageGroupFoldersWindowComponentImpl() {
      this.setWidget(PopOutWindowBuilder.titled("Manage Group Folders").sized(600, 500).withContents(getContents()).get());
      reload();
    }

    private void addRecord(final Folder folder) {
      final ListGridRecord record = new ListGridRecord();
      record.setAttribute("name", folder.getName());
      record.setAttribute("id", folder.getId());
      record.setAttribute("object", folder);
      dataSource.addData(record);
    }

    private void initListGrid() {
      final DataSourceField nameField = SmartUtils.getFieldBuilder(NAME_FIELD_INDEX, "Name").withWidth("*").get();
      final DataSourceField idField = SmartUtils.getHiddenIdField();
      dataSource = SmartUtils.newDataSourceWithFields(nameField, idField);
      listGrid = new ClientListGrid(dataSource);
    }

    private Canvas getContents() {
      initListGrid();
      final Button newFolderButton = SmartUtils.getButton("New Folder", Resources.ADD, new Command() {
        public void execute() {
          addGroupFolderComponentSupplier.get().execute();
        }
      });
      final Button addUserButton = SmartUtils.getButton("Add User", Resources.ADD, new Command() {
        public void execute() {
        }
      });
      SmartUtils.enabledWhenHasSelection(addUserButton, listGrid, false);
      final Button addGroupButton = SmartUtils.getButton("Add Group", Resources.ADD, new Command() {
        public void execute() {
        }
      });
      SmartUtils.enabledWhenHasSelection(addGroupButton, listGrid, false);
      return SmartUtils.getFullVLayout(new CanvasWithOpsLayout<ListGrid>(listGrid, newFolderButton, addUserButton, addGroupButton));
    }

  }
}
