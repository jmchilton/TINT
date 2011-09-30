package edu.umn.msi.tropix.webgui.client.components.newwizards;

import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Timer;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.jobs.activities.descriptions.ScaffoldQuantativeSample;
import edu.umn.msi.tropix.jobs.activities.descriptions.ScaffoldSample;
import edu.umn.msi.tropix.webgui.client.utils.Iterables;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.Lists;
import edu.umn.msi.tropix.webgui.client.utils.Maps;
import edu.umn.msi.tropix.webgui.client.utils.Properties;
import edu.umn.msi.tropix.webgui.client.utils.Property;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.ClientListGrid;
import edu.umn.msi.tropix.webgui.client.widgets.Form;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPageImpl;

// BACK AND FORWARD WITHOUT CHANGE SHOULD NOT RESET
public class ScaffoldQuantSamplesWizardPageImpl extends WizardPageImpl<Canvas> {
  private static final String CATEGORY_FIELD_INDEX = "category";
  private static final String SCAFFOLD_SAMPLE_FIELD_INDEX = "scaffoldSample";
  private static final String NAME_FIELD_INDEX = "name";
  private static final String REPORTER_FIELD_INDEX = "reporter";
  private static final String ASSIGNED_NAME_INDEX = "assignedName";

  private static final String TMT_6_PLEX = "TMT 6-Plex";
  private static final String TMT_2_PLEX = "TMT 2-Plex";
  private static final String I_TRAQ_8_PLEX = "iTRAQ 8-Plex";
  private static final String I_TRAQ_4_PLEX = "iTRAQ 4-Plex";
  private final HasScaffoldSamples hasScaffoldSamples;
  private List<String> displayedCategories = Lists.newArrayList();
  private final Property<List<String>> categoryListProperty = Properties.newProperty((List<String>) Lists.<String>newArrayList());
  private ClientListGrid listGrid;
  private DataSource dataSource;
  private DataSourceField categoryField;

  public Listener<List<String>> getCategoryUpdateListener() {
    return Properties.asListener(categoryListProperty);
  }

  public static enum Reporter {
    ITRAQ_113, ITRAQ_114, ITRAQ_115, ITRAQ_116,
    ITRAQ_117, ITRAQ_118, ITRAQ_119, ITRAQ_121,
    TMT_126, TMT_127, TMT_128, TMT_129, TMT_130, TMT_131;

    public String getReporterIon() {
      final int underScoreIndex = name().indexOf('_');
      return name().substring(underScoreIndex + 1);
    }
  }

  private static Map<String, List<Reporter>> reporters = Maps.newLinkedHashMap();
  private SelectItem quantTypeItem;
  private ListGridRecord[] currentRecords;
  private Form quantTypeForm;
  private VLayout layout;
  private List<Reporter> displayedReporters = Lists.newArrayList();
  private List<String> displayedSampleNames = Lists.newArrayList();

  static {
    reporters.put(I_TRAQ_4_PLEX, Lists.newArrayList(Reporter.ITRAQ_114, Reporter.ITRAQ_115, Reporter.ITRAQ_116, Reporter.ITRAQ_117));
    reporters.put(I_TRAQ_8_PLEX, Lists.newArrayList(Reporter.ITRAQ_113, Reporter.ITRAQ_114, Reporter.ITRAQ_115, Reporter.ITRAQ_116,
                                                    Reporter.ITRAQ_117, Reporter.ITRAQ_118, Reporter.ITRAQ_119, Reporter.ITRAQ_121));
    reporters.put(TMT_2_PLEX, Lists.newArrayList(Reporter.TMT_126, Reporter.TMT_127));
    reporters.put(TMT_6_PLEX, Lists.newArrayList(Reporter.TMT_126, Reporter.TMT_127, Reporter.TMT_128,
                                                 Reporter.TMT_129, Reporter.TMT_130, Reporter.TMT_131));
  }

  private void rebuildGrid() {
    removeCurrentListGrid();
    final DataSourceField nameField = SmartUtils.getFieldBuilder(NAME_FIELD_INDEX, "Name").withWidth("*").editable().get();
    final DataSourceField reporterField = SmartUtils.getFieldBuilder(REPORTER_FIELD_INDEX, "Reporter").withWidth("40px").get();
    final DataSourceField idField = SmartUtils.getHiddenIdField();
    final DataSourceField scaffoldSampleField = SmartUtils.getFieldBuilder(SCAFFOLD_SAMPLE_FIELD_INDEX, "Scaffold Sample").withWidth("*").get();
    categoryField = SmartUtils.getFieldBuilder(CATEGORY_FIELD_INDEX, "Category").withWidth("*").list().editable().get();
    final Map<String, String> categoryMap = Maps.newLinkedHashMap();
    categoryMap.put("", "");
    for(final String category : displayedCategories) {
      categoryMap.put(category, category);
    }
    categoryField.setValueMap(categoryMap);

    dataSource = SmartUtils.newDataSourceWithFields(nameField, categoryField, reporterField, idField, scaffoldSampleField);

    listGrid = new ClientListGrid(dataSource);
    listGrid.setCanEdit(true);
    listGrid.setEditByCell(true);
    listGrid.setEditEvent(ListGridEditEvent.CLICK);
    listGrid.setEditOnFocus(true);
    listGrid.setSaveLocally(true);

    layout.addMember(listGrid);

    displayedSampleNames = Lists.newArrayList();
    displayedReporters = Lists.newArrayList();
  }

  private void removeCurrentListGrid() {
    if(listGrid != null) {
      layout.removeMember(listGrid);
      listGrid = null;
    }
  }

  ScaffoldQuantSamplesWizardPageImpl(final HasScaffoldSamples hasScaffoldSamples) {
    this.hasScaffoldSamples = hasScaffoldSamples;
    super.setTitle("Q+ Samples");
    super.setDescription("Specify Q+ samples.");
    super.setValid(true);

    layout = SmartUtils.getFullVLayout();

    // Add form and list grid to layout
    initQuantTypeForm();
    rebuildGrid();

    setCanvas(layout);
  }

  private void initQuantTypeForm() {
    quantTypeItem = new SelectItem("select", "Quantification Type");
    quantTypeItem.setValueMap(I_TRAQ_4_PLEX, I_TRAQ_8_PLEX, TMT_2_PLEX, TMT_6_PLEX);
    quantTypeItem.addChangedHandler(new ChangedHandler() {
      public void onChanged(final ChangedEvent event) {
        attemptUpdateSamples();
      }
    });
    quantTypeItem.setValue(I_TRAQ_4_PLEX);
    quantTypeForm = new Form(quantTypeItem);
    layout.addMember(quantTypeForm);
  }

  @Override
  public void onDisplay() {
    Log.debug("In onDisplay");
    updateCategoryMap();
    new Timer() {
      public void run() {
        attemptUpdateSamples();
      }
    }.schedule(1);
  }

  private boolean shouldUpdateSamples() {
    return !(displayedReporters.equals(getCurrentReporters()) && displayedSampleNames.equals(getSampleNames(hasScaffoldSamples.getScaffoldSamples())));
  }

  private List<String> getSampleNames(final Iterable<ScaffoldSample> scaffoldSamples) {
    final List<String> sampleNames = Lists.newArrayListWithCapacity(Iterables.size(scaffoldSamples));
    for(final ScaffoldSample scaffoldSample : scaffoldSamples) {
      sampleNames.add(scaffoldSample.getSampleName());
    }
    return sampleNames;
  }

  private void attemptUpdateSamples() {
    final boolean shouldUpdateSamples = shouldUpdateSamples();
    Log.debug("shouldUpdateSamples : " + shouldUpdateSamples);
    if(shouldUpdateSamples) {
      updateSamples();
    }
  }

  private void updateSamples() {
    int index = 0;
    displayedReporters = getCurrentReporters();
    displayedSampleNames = getSampleNames(hasScaffoldSamples.getScaffoldSamples());
    final int numRecords = displayedReporters.size() * displayedSampleNames.size();
    Log.debug("In update samples, allocating room for num records: " + numRecords);
    final ListGridRecord[] previousRecords = currentRecords;
    currentRecords = new ListGridRecord[numRecords];
    for(final Reporter reporter : displayedReporters) {
      for(final String sampleName : displayedSampleNames) {
        final ListGridRecord previousRecord = findMatchingRecord(previousRecords, reporter, sampleName);
        final ListGridRecord record = buildRecord(reporter,
                                                  sampleName,
                                                  index,
                                                  previousRecord);
        currentRecords[index] = record;
        index++;
      }
    }
    listGrid.setRecords(currentRecords);
  }

  private ListGridRecord buildRecord(final Reporter reporter, final String sampleName, final int index, final ListGridRecord previousRecord) {
    final ListGridRecord record = new ListGridRecord();
    final String assignedName = "Quant" + (index + 1);
    String name = assignedName;
    if(previousRecord != null) {
      final String previousName = previousRecord.getAttributeAsString(NAME_FIELD_INDEX);
      final String previousAssignedName = previousRecord.getAttributeAsString(ASSIGNED_NAME_INDEX);
      if(previousName != null && !previousName.equals(previousAssignedName)) {
        name = previousName;
      }
    }

    final String category;
    final String previousCategory = previousRecord == null ? null : previousRecord.getAttributeAsString(CATEGORY_FIELD_INDEX);
    if(previousCategory != null && displayedCategories.contains(previousCategory)) {
      category = previousCategory;
    } else {
      category = "";
    }
    record.setAttribute(ASSIGNED_NAME_INDEX, assignedName);
    record.setAttribute(NAME_FIELD_INDEX, name);
    record.setAttribute(CATEGORY_FIELD_INDEX, category);
    record.setAttribute(SCAFFOLD_SAMPLE_FIELD_INDEX, sampleName);
    record.setAttribute(REPORTER_FIELD_INDEX, reporter.getReporterIon());
    record.setAttribute("id", index);
    return record;
  }

  private ListGridRecord findMatchingRecord(final ListGridRecord[] records, final Reporter reporter, final String sampleName) {
    if(records == null) {
      return null;
    }
    final String queryReporter = reporter.getReporterIon();
    final String querySampleName = sampleName;
    if(querySampleName == null) {
      return null;
    }
    ListGridRecord matchingRecord = null;
    for(final ListGridRecord record : records) {
      final boolean reporterMatches = queryReporter.equals(record.getAttributeAsString(REPORTER_FIELD_INDEX));
      final boolean nameMatches = querySampleName.equals(record.getAttributeAsString(SCAFFOLD_SAMPLE_FIELD_INDEX));
      if(reporterMatches && nameMatches) {
        matchingRecord = record;
      }
    }
    return matchingRecord;
  }

  public void augmentScaffoldSamples(final Iterable<ScaffoldSample> scaffoldSamples) {
    for(final ScaffoldSample scaffoldSample : scaffoldSamples) {
      augmentScaffoldSample(scaffoldSample);
    }
  }

  private void augmentScaffoldSample(final ScaffoldSample scaffoldSample) {
    final List<ScaffoldQuantativeSample> quantSamples = Lists.newArrayList();
    final StringBuilder purityCorrection = new StringBuilder();
    for(final ListGridRecord record : currentRecords) {
      if(record.getAttributeAsString(SCAFFOLD_SAMPLE_FIELD_INDEX).equals(scaffoldSample.getSampleName())) {
        final ScaffoldQuantativeSample quantSample = getQuantSample(record);
        quantSamples.add(quantSample);
        purityCorrection.append("0.0,0.0,100.0,0.0,0.0\n");
      }
    }
    scaffoldSample.setQuantitativeModelType(getSelectedQuantType());
    scaffoldSample.setQuantitativeModelPurityCorrection(purityCorrection.toString());
    scaffoldSample.setQuantitativeSamples(quantSamples);
  }

  private ScaffoldQuantativeSample getQuantSample(final ListGridRecord record) {
    final ScaffoldQuantativeSample quantSample = new ScaffoldQuantativeSample();
    final String rawCategory = record.getAttributeAsString(CATEGORY_FIELD_INDEX);
    quantSample.setPrimary(false);
    if(StringUtils.hasText(rawCategory)) {
      if(rawCategory.equals("Reference")) {
        quantSample.setPrimary(true);
      } else {
        quantSample.setCategory(rawCategory);
      }
    }
    quantSample.setReporter(record.getAttributeAsString(REPORTER_FIELD_INDEX));
    quantSample.setName(record.getAttributeAsString(NAME_FIELD_INDEX));
    return quantSample;
  }

  private List<Reporter> getCurrentReporters() {
    final String typeValue = getSelectedQuantType();
    return reporters.get(typeValue);
  }

  private String getSelectedQuantType() {
    return quantTypeItem.getValueAsString();
  }

  private void updateCategoryMap() {
    if(!displayedCategories.equals(categoryListProperty.get())) {
      displayedCategories = categoryListProperty.get();
      rebuildGrid();
    }
  }
}
