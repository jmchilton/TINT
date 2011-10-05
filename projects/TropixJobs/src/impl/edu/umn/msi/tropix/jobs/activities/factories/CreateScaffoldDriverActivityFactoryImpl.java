/********************************************************************************
 * Copyright (c) 2009 Regents of the University of Minnesota
 *
 * This Software was written at the Minnesota Supercomputing Institute
 * http://msi.umn.edu
 *
 * All rights reserved. The following statement of license applies
 * only to this file, and and not to the other files distributed with it
 * or derived therefrom.  This file is made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 *******************************************************************************/

package edu.umn.msi.tropix.jobs.activities.factories;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.springframework.util.StringUtils;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.common.collect.Collections;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.common.shutdown.ShutdownException;
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateScaffoldDriverDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.IdList;
import edu.umn.msi.tropix.jobs.activities.descriptions.ScaffoldQuantativeSample;
import edu.umn.msi.tropix.jobs.activities.descriptions.ScaffoldSample;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactory;
import edu.umn.msi.tropix.jobs.activities.impl.ActivityFactoryFor;
import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.utils.ModelFunctions;
import edu.umn.msi.tropix.persistence.service.AnalysisService;
import edu.umn.msi.tropix.proteomics.cagrid.metadata.ParameterType;
import edu.umn.msi.tropix.proteomics.scaffold.input.BiologicalSample;
import edu.umn.msi.tropix.proteomics.scaffold.input.DisplayThresholds;
import edu.umn.msi.tropix.proteomics.scaffold.input.Experiment;
import edu.umn.msi.tropix.proteomics.scaffold.input.Export;
import edu.umn.msi.tropix.proteomics.scaffold.input.ExportType;
import edu.umn.msi.tropix.proteomics.scaffold.input.FastaDatabase;
import edu.umn.msi.tropix.proteomics.scaffold.input.QuantitativeModel;
import edu.umn.msi.tropix.proteomics.scaffold.input.QuantitativeSample;
import edu.umn.msi.tropix.proteomics.scaffold.input.Scaffold;
import edu.umn.msi.tropix.proteomics.xml.ScaffoldUtility;
import edu.umn.msi.tropix.storage.client.ModelStorageData;

@ManagedBean
@ActivityFactoryFor(CreateScaffoldDriverDescription.class)
class CreateScaffoldDriverActivityFactoryImpl implements ActivityFactory<CreateScaffoldDriverDescription> {
  private static final ScaffoldUtility SCAFFOLD_XML_UTILITY = new ScaffoldUtility();
  private final AnalysisService analysisService;

  @Inject
  CreateScaffoldDriverActivityFactoryImpl(final AnalysisService analysisService, final FactorySupport factorySupport) {
    this.analysisService = analysisService;
    this.factorySupport = factorySupport;
  }

  public static enum DatabaseType {
    ESTNR("EST/NR (NCBI)", ">(gi\\|[0-9]*)", ">[^ ]* (.*)"),
    IPI("IPI (EBI)", ">IPI:([^\\| .]*)", ">[^ ]* Tax_Id=[0-9]* (.*)"),
    SWISSPROT("Swiss-Prot (SIB/EBI)", ">([^ ]*)", ">[^ ]* \\([^ ]*\\) (.*)"),
    UNIPROT("UniProt/Swiss-Prot (UniProtKB)", ">[^ ]*\\|([^ ]*)", ">[^ ]*\\|[^ ]* (.*)"),
    UNIREF("UniRef/NREF (UniProt)", ">UniRef100_([^ ]*)", ">[^ ]* (.*)"),
    ENSEMBL("Ensembl (EMBL/EBI)", ">(ENS[^ ]*)", ">[^ ]* (.*)"),
    MSDB("MSDB (Proteomics Group)", ">([^ ]*)", ">[^ ]* (.*)"),
    GENERIC("Generic", ">([^ ]*)", ">[^ ]* (.*)");

    private String name;
    private String accessionRegEx;
    private String descriptionRegEx;

    private DatabaseType(final String name, final String accessionRegEx, final String descriptionRegEx) {
      this.name = name;
      this.accessionRegEx = accessionRegEx;
      this.descriptionRegEx = descriptionRegEx;
    }

    String getName() {
      return name;
    }

    String getAccessionRegEx() {
      return accessionRegEx;
    }

    String getDescriptionRegEx() {
      return descriptionRegEx;
    }

    public static DatabaseType get(final String name) {
      DatabaseType type = DatabaseType.GENERIC;
      if(StringUtils.hasText(name)) {
        type = DatabaseType.valueOf(name);
      }
      Preconditions.checkNotNull(type, "No database type for name " + name);
      return type;
    }

  }

  private final FactorySupport factorySupport;

  private static Map<Database, FastaDatabase> getFastaDatabaseMap(final Map<String, Database> databaseMap,
                                                                  final String databaseTypeName) {
    final Map<Database, FastaDatabase> fastaMap = new LinkedHashMap<Database, FastaDatabase>();
    final UniqueSanitizedFileNamer namer = new UniqueSanitizedFileNamer();
    int i = 0;
    for(final Database database : databaseMap.values()) {
      if(!fastaMap.containsKey(database)) {
        final FastaDatabase fastaDatabase = new FastaDatabase();
        final String databaseFileName = namer.nameFor(database.getName());
        fastaDatabase.setPath(databaseFileName + ".fasta");
        fastaDatabase.setName(databaseFileName);
        fastaDatabase.setId("db" + i);
        final DatabaseType databaseType = DatabaseType.get(databaseTypeName);
        fastaDatabase.setDatabaseAccessionRegEx(databaseType.getAccessionRegEx());
        fastaDatabase.setDatabaseDescriptionRegEx(databaseType.getDescriptionRegEx());
        i++;
        fastaMap.put(database, fastaDatabase);
      }
    }
    return fastaMap;
  }

  class CreateScaffoldDriverActivityImpl extends BaseActivityImpl<CreateScaffoldDriverDescription> {

    protected CreateScaffoldDriverActivityImpl(final CreateScaffoldDriverDescription activityDescription, final ActivityContext activityContext) {
      super(activityDescription, activityContext);
    }

    public void run() throws ShutdownException {
      final Map<String, String> parameters = getDescription().getParameterSet().toMap();
      final List<ScaffoldSample> samples = getDescription().getScaffoldSamples();
      Preconditions.checkNotNull(samples);
      final Map<String, IdentificationAnalysis> analysesMap = ScaffoldSampleUtils.loadIdentificationAnalyses(getUserId(), samples, factorySupport);
      Preconditions.checkState(analysesMap.values() != null && !Iterables.isEmpty(analysesMap.values()));
      final Map<String, Database> databaseMap = ScaffoldSampleUtils.getDatabaseMap(analysisService, getUserId(), analysesMap);
      Preconditions.checkState(databaseMap.values() != null && !Iterables.isEmpty(databaseMap.values()));
      final Map<Database, FastaDatabase> fastaDatabaseMap = getFastaDatabaseMap(databaseMap, parameters.get("databaseType"));
      final Function<String, FastaDatabase> analysisToFastaDatabase = Functions.compose(Functions.forMap(fastaDatabaseMap),
          Functions.forMap(databaseMap));

      final Scaffold scaffoldInput = new Scaffold();
      final Experiment scaffoldExperiment = new Experiment();
      scaffoldExperiment.setName("TINT Scaffold Experiment");
      final String connectToNcbiString = parameters.get("connectToNCBI");
      if(StringUtils.hasText(connectToNcbiString)) {
        final boolean connectToNcbi = Boolean.parseBoolean(connectToNcbiString);
        scaffoldExperiment.setConnectToNCBI(connectToNcbi);
      }

      scaffoldInput.setExperiment(scaffoldExperiment);

      // Setup display thresholds, later should pull this from parameters;
      final DisplayThresholds thresholds = new DisplayThresholds();
      thresholds.setId("thresh");
      thresholds.setName("thresh");
      
      thresholds.setMinimumNTT(Integer.valueOf(parameters.get("minimumNTT")));
      thresholds.setMinimumPeptideCount(Integer.valueOf(parameters.get("minimumPeptideCount")));
      thresholds.setPeptideProbability(Float.valueOf(parameters.get("peptideProbability")));
      thresholds.setProteinProbability(Float.valueOf(parameters.get("proteinProbability")));
      final StringBuffer useCharge = new StringBuffer();
      for(int i = 1; i <= 3; i++) {
        useCharge.append(parameters.get("useCharge" + i));
        if(i != 3) {
          useCharge.append(",");
        }
      }
      thresholds.setUseCharge(useCharge.toString());
      scaffoldExperiment.getDisplayThresholds().add(thresholds);

      final String inputDecoyRegex = parameters.get("decoyRegex");
      final String decoyRegex = StringUtils.hasText(inputDecoyRegex) ? inputDecoyRegex : null;

      // Specify required databases
      for(final FastaDatabase fastaDatabase : fastaDatabaseMap.values()) {
        fastaDatabase.setDecoyProteinRegEx(decoyRegex);
        scaffoldExperiment.getFastaDatabase().add(fastaDatabase);
      }

      // Specify export for scaffold, just hard code a single SFD file for now
      final Export scaffoldExport = new Export();
      if("V2".equals(getDescription().getScaffoldVersion())) {
        scaffoldExport.setType(ExportType.SFD);
        scaffoldExport.setPath("output.sfd");
      } else {
        scaffoldExport.setType(ExportType.SF_3);
        scaffoldExport.setPath("output.sf3");
      }
      scaffoldExport.setThresholds("thresh");

      scaffoldExperiment.getExport().add(scaffoldExport);

      // Add all scaffold samples with their inputs and corresponding database
      int i = 0;
      final List<BiologicalSample> scaffoldSamples = scaffoldExperiment.getBiologicalSample();
      final UniqueSanitizedFileNamer namer = new UniqueSanitizedFileNamer();

      for(final ScaffoldSample sample : samples) {
        final BiologicalSample scaffoldSample = new BiologicalSample();
        scaffoldSample.setAnalyzeAsMudpit(sample.getAnalyzeAsMudpit());
        scaffoldSample.setDatabase(null);
        scaffoldSample.setName(sample.getSampleName());
        scaffoldSample.setCategory(sample.getCategory());
        setQuantitativeInformation(scaffoldSample, sample);
        for(final String analysisId : sample.getIdentificationAnalysisIds().toList()) {
          final IdentificationAnalysis idAnalysis = analysesMap.get(analysisId);
          final String inputFileName = namer.nameFor(idAnalysis.getName());
          final String expectedDB = analysisToFastaDatabase.apply(analysisId).getId();
          if(scaffoldSample.getDatabase() == null) {
            scaffoldSample.setDatabase(expectedDB);
          } else if(!scaffoldSample.getDatabase().equals(expectedDB)) {
            throw new IllegalArgumentException("Databases corresponding to specified analyses in sample don't match");
          }
          final String idProgram = idAnalysis.getIdentificationProgram();
          if(idProgram.equals(ParameterType.SequestBean.toString())) {
            scaffoldSample.getInputFile().add(inputFileName + ".zip");
          } else if(idProgram.equals(ParameterType.XTandemBean.toString())) {
            scaffoldSample.getInputFile().add(inputFileName + ".xml");
          } else if(idProgram.equals("Mascot")) {
            scaffoldSample.getInputFile().add(inputFileName + ".dat");
          } else if(idProgram.equals(ParameterType.OmssaXml.toString())) {
            scaffoldSample.getInputFile().add(inputFileName + ".omx");
          } else {
            throw new IllegalStateException("Unknown identification program encountered.");
          }
          i++;
        }
        scaffoldSamples.add(scaffoldSample);
      }

      final ModelStorageData inputData = factorySupport.getStorageDataFactory().getStorageData(getDescription().getStorageServiceUrl(),
          getCredential());
      final OutputContext outputContext = inputData.getUploadContext();
      outputContext.put(SCAFFOLD_XML_UTILITY.serialize(scaffoldInput).getBytes());
      getDescription().setDriverFileId(inputData.getDataIdentifier());
      getDescription().setDatabaseIds(IdList.forIterable(Collections.transform(fastaDatabaseMap.keySet(), ModelFunctions.getIdFunction())));
      getDescription().setIdentificationAnalysisIds(IdList.forIterable(Collections.transform(analysesMap.values(), ModelFunctions.getIdFunction())));
    }

    private void setQuantitativeInformation(final BiologicalSample scaffoldSample, final ScaffoldSample sample) {
      if(sample.getQuantitativeModelType() == null) {
        return;
      }
      final QuantitativeModel model = new QuantitativeModel();
      model.setPurityCorrection(sample.getQuantitativeModelPurityCorrection());
      model.setType(sample.getQuantitativeModelType());
      Preconditions.checkNotNull(sample.getQuantitativeSamples());
      for(ScaffoldQuantativeSample quantSample : sample.getQuantitativeSamples()) {
        model.getQuantitativeSample().add(convertQuantativeSample(quantSample));
      }
      scaffoldSample.setQuantitativeModel(model);
    }

    private QuantitativeSample convertQuantativeSample(final ScaffoldQuantativeSample inputSample) {
      final QuantitativeSample convertedSample = new QuantitativeSample();
      convertedSample.setCategory(inputSample.getCategory());
      convertedSample.setDescription(inputSample.getDescription());
      convertedSample.setName(inputSample.getName());
      convertedSample.setPrimary(inputSample.isPrimary());
      convertedSample.setReporter(inputSample.getReporter());
      return convertedSample;
    }

  }

  public Activity getActivity(final CreateScaffoldDriverDescription activityDescription, final ActivityContext activityContext) {
    return new CreateScaffoldDriverActivityImpl(activityDescription, activityContext);
  }

}
