package edu.umn.msi.tropix.webgui.client.constants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

public interface ProtipConstants extends Constants {
  ProtipConstants INSTANCE = GWT.create(ProtipConstants.class);

  String newIdentificationSearch();

  String newIdentificationSearchBatch();

  String newPrerunIdentificationSearch();

  String newScaffoldAnalysis();

  String newScaffoldQPlusAnalysis();

  String newScaffoldAnalysisBatch();

  String newLtqIQuantAnalysis();

  String newLtqIQuantTraining();

  String newIdentificationWorkflow();

  String newProteomicsRun();

  String newIdPickerAnalysis();

  String typePeakList();

}
