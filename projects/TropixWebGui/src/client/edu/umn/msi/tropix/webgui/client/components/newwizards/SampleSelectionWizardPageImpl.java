package edu.umn.msi.tropix.webgui.client.components.newwizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.gwt.user.client.Command;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.HiddenItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.tree.TreeGrid;

import edu.umn.msi.tropix.jobs.activities.descriptions.IdList;
import edu.umn.msi.tropix.jobs.activities.descriptions.ScaffoldSample;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.locations.TropixObjectLocation;
import edu.umn.msi.tropix.models.proteomics.IdentificationType;
import edu.umn.msi.tropix.models.utils.TropixObjectType;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.impl.WindowComponentImpl;
import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponent;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItemPredicates;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItems;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions.SelectionType;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItemExpanders;
import edu.umn.msi.tropix.webgui.client.constants.ComponentConstants;
import edu.umn.msi.tropix.webgui.client.constants.ConstantsInstances;
import edu.umn.msi.tropix.webgui.client.utils.Iterables;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.Lists;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.ClientListGrid;
import edu.umn.msi.tropix.webgui.client.widgets.Form;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils.DataSourceItemBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPageImpl;

// TODO: Externalize remaining strings.
class SampleSelectionWizardPageImpl extends WizardPageImpl<VLayout> implements HasScaffoldSamples {
  private static final ComponentConstants CONSTANTS = ConstantsInstances.COMPONENT_INSTANCE;
  private final ArrayList<ListGridRecord> records = new ArrayList<ListGridRecord>();
  private final List<IdentificationType> validAnalysisTypes;
  private final LocationFactory locationFactory;
  private final TreeComponentFactory treeComponentFactory;
  private final ListGrid listGrid;
  private final DataSource dataSource;
  private int sampleCount = 0;
  private Button editButton, removeButton;
  private ListGridRecord contextRecord;
  private final boolean showMudpit;

  SampleSelectionWizardPageImpl(final LocationFactory locationFactory, final TreeComponentFactory treeComponentFactory) {
    this(locationFactory, treeComponentFactory, true, ScaffoldConstants.VALID_SCAFFOLD_IDENTIFICATION_TYPES);
  }

  SampleSelectionWizardPageImpl(final LocationFactory locationFactory, final TreeComponentFactory treeComponentFactory, final boolean showMudpit,
      final List<IdentificationType> validAnalysisTypes) {
    this.locationFactory = locationFactory;
    this.treeComponentFactory = treeComponentFactory;
    this.showMudpit = showMudpit;
    this.validAnalysisTypes = validAnalysisTypes;

    setCanFinishEarly(true);
    setTitle(CONSTANTS.scaffoldWizardSampleTitle());
    setDescription(CONSTANTS.scaffoldWizardSampleDescription());

    // Setup Data Source

    final DataSourceField nameField = SmartUtils.getFieldBuilder("Name").editable().get();
    final DataSourceField categoryField = SmartUtils.getFieldBuilder("Category").editable().get();
    DataSourceItemBuilder mudpitFieldBuilder = SmartUtils.getFieldBuilder("Mudpit").editable().withWidth("100");
    if(showMudpit) {
      mudpitFieldBuilder.checkbox();
    } else {
      mudpitFieldBuilder.hidden();
    }
    final DataSourceField mudpitField = mudpitFieldBuilder.get();
    final DataSourceField idField = SmartUtils.getHiddenIdField();
    dataSource = SmartUtils.newDataSourceWithFields(nameField, mudpitField, categoryField, idField);
    listGrid = new ClientListGrid(dataSource);

    // Setup list grid
    listGrid.setCanEdit(true);
    listGrid.setEditByCell(true);
    listGrid.setEditEvent(ListGridEditEvent.DOUBLECLICK);
    listGrid.setEmptyMessage("No samples to show.");

    final Menu contextMenu = new Menu();
    final MenuItem editItem = SmartUtils.getMenuItem("Edit", Resources.EDIT, new Command() {
      public void execute() {
        editRecord(contextRecord);
      }
    });
    final MenuItem removeItem = SmartUtils.getMenuItem("Remove", Resources.CROSS, new Command() {
      public void execute() {
        removeRecord(contextRecord);
      }
    });
    contextMenu.setItems(editItem, removeItem);
    listGrid.setContextMenu(contextMenu);
    listGrid.addCellContextClickHandler(new CellContextClickHandler() {
      public void onCellContextClick(final CellContextClickEvent event) {
        contextRecord = event.getRecord();
      }
    });
    listGrid.addSelectionChangedHandler(new SelectionChangedHandler() {
      public void onSelectionChanged(final SelectionEvent event) {
        onSelectionUpdated();
      }
    });

    final Button addButton = SmartUtils.getButton("Add...", Resources.ADD, new Command() {
      public void execute() {
        new SampleComponentImpl();
      }
    });

    editButton = SmartUtils.getButton("Edit...", Resources.EDIT, new Command() {
      public void execute() {
        try {
          editRecord(listGrid.getSelectedRecord());
        } catch(Exception e) {
          e.printStackTrace();
        }
      }
    });

    removeButton = SmartUtils.getButton("Remove", Resources.CROSS, new Command() {
      public void execute() {
        removeRecord(listGrid.getSelectedRecord());
      }
    });

    final ToolStrip toolStrip = new ToolStrip();
    toolStrip.setMembers(addButton, editButton, removeButton);
    toolStrip.setMargin(3);
    toolStrip.setWidth100();

    onSelectionUpdated();

    setCanvas(new VLayout());
    getCanvas().setMembers(toolStrip, listGrid);
  }

  public List<ScaffoldSample> getScaffoldSamples() {
    final List<ScaffoldSample> samples = Lists.newArrayListWithCapacity(records.size());
    for(final ListGridRecord record : records) {
      final ScaffoldSample sample = new ScaffoldSample();
      @SuppressWarnings("unchecked")
      final Collection<TreeItem> selectionItems = (Collection<TreeItem>) record.getAttributeAsObject("Selection");
      final List<String> analysisIds = Lists.newArrayListWithCapacity(selectionItems.size());
      for(final TreeItem treeItem : selectionItems) {
        analysisIds.add(treeItem.getId());
      }
      final String sampleName = record.getAttributeAsString("Name");
      final String categoryName = record.getAttributeAsString("Category");
      final boolean analyzeAsMudpit = (Boolean) record.getAttributeAsBoolean("Mudpit");
      sample.setIdentificationAnalysisIds(IdList.forIterable(analysisIds));
      sample.setSampleName(sampleName);
      sample.setAnalyzeAsMudpit(analyzeAsMudpit);
      if(StringUtils.hasText(categoryName)) {
        sample.setCategory(categoryName);
      }
      samples.add(sample);
    }
    return samples;
  }

  private void checkValid() {
    setValid(records.size() > 0);
  }

  private void editRecord(final ListGridRecord record) {
    new SampleComponentImpl(record, false);
  }

  private void removeRecord(final ListGridRecord record) {
    records.remove(record);
    dataSource.removeData(record);
    checkValid();
  }

  private void onSelectionUpdated() {
    final boolean isEnabled = listGrid.getSelectedRecord() != null;
    editButton.setDisabled(!isEnabled);
    removeButton.setDisabled(!isEnabled);
  }

  class SampleComponentImpl extends WindowComponentImpl<Window> implements Listener<Collection<TreeItem>> {
    private final TropixObjectType[] types = new TropixObjectType[] {TropixObjectTypeEnum.FOLDER, TropixObjectTypeEnum.VIRTUAL_FOLDER,
        TropixObjectTypeEnum.PROTEIN_IDENTIFICATION_ANALYSIS};
    private Collection<TreeItem> selection;
    private Button okButton;
    private TextItem nameItem = new TextItem("sampleName", "Sample Name");
    private FormItem mudpitItem = showMudpit ? new CheckboxItem("asMudpit", "Analyze as mudpit") : new HiddenItem("asMudpit");
    private TextItem categoryItem = new TextItem("category", "Category (Optional)");

    private void setDefaultNameIfNeeded() {
      if(!StringUtils.hasText(nameItem.getValue()) && selection.size() == 1) {
        // Cast is valid because selection predicate supplied to TreeOptions
        // precludes non TropixObjectTreeItems
        final TropixObjectLocation selectionItem = (TropixObjectLocation) Iterables.getOnlyElement(selection);
        nameItem.setValue(selectionItem.getObject().getName());
      }
    }

    public void onEvent(final Collection<TreeItem> treeItems) {
      selection = treeItems;
      setDefaultNameIfNeeded();
      checkOk();
    }

    private void checkOk() {
      okButton.setDisabled(selection == null || selection.isEmpty() || !StringUtils.hasText(nameItem.getValue()));
    }

    SampleComponentImpl() {
      final ListGridRecord record = new ListGridRecord();
      record.setAttribute("Name", "");
      record.setAttribute("Mudpit", true);
      record.setAttribute("Category", "");
      record.setAttribute("Selection", new ArrayList<TreeItem>());
      record.setAttribute("id", "" + sampleCount++);
      init(record, true);
    }

    SampleComponentImpl(final ListGridRecord sampleRecord, final boolean add) {
      init(sampleRecord, add);
    }

    private boolean validIdentification(final TreeItem treeItem) {
      if(!(treeItem instanceof TropixObjectTreeItem)) {
        return true;
      }
      final TropixObjectLocation tropixObjectTreeItem = (TropixObjectLocation) treeItem;
      final TropixObject tropixObject = tropixObjectTreeItem.getObject();
      if(!(tropixObject instanceof IdentificationAnalysis)) {
        return true;
      }
      final IdentificationAnalysis analysis = (IdentificationAnalysis) tropixObject;
      final IdentificationType analysisType = IdentificationType.fromParameterType(analysis.getIdentificationProgram());
      return validAnalysisTypes.contains(analysisType);
    }

    private Predicate<TreeItem> getShowItemPredicate() {
      final Predicate<TreeItem> typePredicate = TreeItemPredicates.getTropixObjectTreeItemTypePredicate(types, true);
      return new Predicate<TreeItem>() {
        public boolean apply(final TreeItem treeItem) {
          return typePredicate.apply(treeItem) && validIdentification(treeItem);
        }
      };
    }

    private void init(final ListGridRecord sampleRecord, final boolean add) {
      final Form form = new Form(nameItem, mudpitItem, categoryItem);
      form.setWrapItemTitles(false);
      nameItem.setValue(sampleRecord.getAttributeAsString("Name"));
      nameItem.addChangedHandler(new ChangedHandler() {
        public void onChanged(final ChangedEvent event) {
          checkOk();
        }
      });
      mudpitItem.setValue(sampleRecord.getAttributeAsBoolean("Mudpit"));
      categoryItem.setValue(sampleRecord.getAttributeAsString("Category"));

      final Label label = new Label(CONSTANTS.scaffoldWizardSampleSearchLabel());
      label.setWidth100();
      label.setHeight(12);

      final TreeOptions treeOptions = new TreeOptions();
      treeOptions.setInitialItems(locationFactory.getTropixObjectSourceRootItems(TropixObjectTreeItemExpanders.get(types)));
      treeOptions.setShowPredicate(getShowItemPredicate());
      treeOptions.setSelectionPredicate(TreeItemPredicates.getTropixObjectNotFolderPredicate());
      treeOptions.setSelectionType(SelectionType.MULTIPlE);
      @SuppressWarnings("unchecked")
      final Collection<TreeItem> selectedItems = (Collection<TreeItem>) sampleRecord.getAttributeAsObject("Selection");
      treeOptions.setExpandIds(TreeItems.getAncestorIds(selectedItems));
      treeOptions.setSelectedItems(selectedItems);
      final TreeComponent tree = treeComponentFactory.get(treeOptions);
      tree.addMultiSelectionListener(this);
      final TreeGrid treeGrid = tree.get();
      treeGrid.setTooltip("Hold the control key to select multiple items.");
      final VLayout layout = new VLayout();
      layout.addMember(form);
      layout.addMember(label);
      layout.addMember(treeGrid);
      layout.setSize("500px", "500px");
      okButton = SmartUtils.getButton(add ? "Add" : "Update", null, new Command() {
        public void execute() {
          sampleRecord.setAttribute("Name", (String) nameItem.getValue());
          sampleRecord.setAttribute("Category", (String) categoryItem.getValue());
          sampleRecord.setAttribute("Mudpit", (Boolean) mudpitItem.getValue());
          sampleRecord.setAttribute("Selection", selection);
          if(add) {
            dataSource.addData(sampleRecord);
            records.add(sampleRecord);
          }
          listGrid.markForRedraw();
          get().markForDestroy();
          checkValid();
        }
      });
      final Button cancelButton = SmartUtils.getCancelButton(this);
      final Layout opsLayout = new CanvasWithOpsLayout<VLayout>(layout, okButton, cancelButton);
      final Window window = PopOutWindowBuilder.titled((add ? "Add" : "Update") + " Sample").withContents(opsLayout).autoSized().get();
      setWidget(window);
      checkOk();
      get().draw();
    }
  }
}