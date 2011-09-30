package edu.umn.msi.tropix.jobs.activities.factories;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.jobs.activities.descriptions.ScaffoldSample;
import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.persistence.service.AnalysisService;

class ScaffoldSampleUtils {

  static Map<String, IdentificationAnalysis> loadIdentificationAnalyses(final String userId, final List<ScaffoldSample> scaffoldSamples, final FactorySupport factorySupport) {
    final Map<String, IdentificationAnalysis> analyses = new LinkedHashMap<String, IdentificationAnalysis>();
    final LinkedList<String> analysisIds = new LinkedList<String>();
    for(final ScaffoldSample sample : scaffoldSamples) {
      analysisIds.addAll(sample.getIdentificationAnalysisIds().toList());
    }
    final TropixObject[] analysisObjects = factorySupport.getTropixObjectService().load(userId, analysisIds.toArray(new String[] {}), TropixObjectTypeEnum.PROTEIN_IDENTIFICATION_ANALYSIS);
    for(final TropixObject tropixObject : analysisObjects) {
      analyses.put(tropixObject.getId(), (IdentificationAnalysis) tropixObject);
    }
    return analyses;
  }
  
  static Map<String, Database> getDatabaseMap(final AnalysisService analysisService, final String userId, final Map<String, IdentificationAnalysis> analysisMap) {
    final Map<String, Database> databaseMap = new LinkedHashMap<String, Database>();
    final String[] analysisIds = analysisMap.keySet().toArray(new String[] {});
    final Database[] databases = analysisService.getIdentificationDatabases(userId, analysisIds);
    for(int i = 0; i < analysisIds.length; i++) {
      final String analysisId = analysisIds[i];
      final Database database = databases[i];
      Preconditions.checkNotNull("analysisService returned a null database for analysis with id " + analysisId);
      databaseMap.put(analysisId, database);
    }
    return databaseMap;
  }


  
}
