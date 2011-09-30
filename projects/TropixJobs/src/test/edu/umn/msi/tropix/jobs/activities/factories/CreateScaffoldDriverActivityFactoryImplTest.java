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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateScaffoldDriverDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.StringParameterSet;
import edu.umn.msi.tropix.jobs.activities.factories.CreateScaffoldDriverActivityFactoryImpl.DatabaseType;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.proteomics.scaffold.input.BiologicalSample;
import edu.umn.msi.tropix.proteomics.scaffold.input.Experiment;
import edu.umn.msi.tropix.proteomics.scaffold.input.FastaDatabase;
import edu.umn.msi.tropix.proteomics.scaffold.input.QuantitativeModel;
import edu.umn.msi.tropix.proteomics.scaffold.input.QuantitativeSample;
import edu.umn.msi.tropix.proteomics.scaffold.input.Scaffold;
import edu.umn.msi.tropix.proteomics.xml.ScaffoldUtility;
import edu.umn.msi.tropix.storage.client.ModelStorageData;

public class CreateScaffoldDriverActivityFactoryImplTest extends CreateMergeIdentificationAnalyisFactoryImplTest {
  private final String storageServiceUrl = "http://storage";

  @Test(groups = "unit")
  public void testRun() {
    final MockFactorySupportImpl factorySupport = new MockFactorySupportImpl();
    final CreateScaffoldDriverDescription description = TestUtils.init(new CreateScaffoldDriverDescription());
    description.setStorageServiceUrl(storageServiceUrl);
    final ActivityContext context = TestUtils.getContext();

    final CreateScaffoldDriverActivityFactoryImpl factory = new CreateScaffoldDriverActivityFactoryImpl(getAnalysisService(), factorySupport);

    super.populateScaffoldSamples(description);

    // TODO: Verify
    final Map<String, String> parameters = new HashMap<String, String>();
    parameters.put("minimumNTT", "3");
    parameters.put("minimumPeptideCount", "4");
    parameters.put("peptideProbability", ".95");
    parameters.put("proteinProbability", ".99");
    parameters.put("useCharge1", "false");
    parameters.put("useCharge2", "true");
    parameters.put("useCharge3", "true");
    parameters.put("connectToNCBI", "true");
    parameters.put("decoyRegex", "REVERSE");
    parameters.put("databaseType", "IPI");

    description.setParameterSet(StringParameterSet.fromMap(parameters));

    final IdentificationAnalysis[] analyses = createIdentificationAnalyses(storageServiceUrl);

    final Database[] databases = new Database[3];
    for(int i = 1; i < 3; i++) {
      final Database database = new Database();
      database.setName("FooBase");
      database.setId(UUID.randomUUID().toString());
      final TropixFile tropixFile = new TropixFile();
      tropixFile.setId(UUID.randomUUID().toString());
      tropixFile.setStorageServiceUrl(storageServiceUrl);
      tropixFile.setFileId((6 + i) + "");
      database.setDatabaseFile(tropixFile);
      databases[i] = database;
    }
    databases[0] = databases[1];

    expectAnalysisIdsAndReturnDatabases(context, databases);

    factorySupport.saveObjects(databases);
    factorySupport.saveObjects(analyses);

    factorySupport.replay();
    final Activity activity = factory.getActivity(description, context);
    activity.run();

    factorySupport.verifyAndReset();

    final ModelStorageData storageData = factorySupport.getStorageDataFactory().getNewlyIssuedStorageDataObject();
    assert description.getDriverFileId().equals(storageData.getDataIdentifier());
    verifyIdentificationAnalysisIds(description);

    assert description.getDatabaseIds().getIds().size() == 2 : description.getDatabaseIds().getIds().size();
    assert description.getDatabaseIds().toList().get(0).equals(databases[0].getId());
    assert description.getDatabaseIds().toList().get(1).equals(databases[2].getId());

    final Scaffold scaffold = new ScaffoldUtility().deserialize(storageData.getDownloadContext());
    final Experiment experiment = scaffold.getExperiment();
    assert experiment.isConnectToNCBI();
    final List<BiologicalSample> bioSamples = experiment.getBiologicalSample();
    final List<FastaDatabase> fastaDatabases = experiment.getFastaDatabase();
    assert bioSamples.size() == 2;
    assert fastaDatabases.size() == 2;
    final BiologicalSample bioSample1 = bioSamples.get(0);
    final BiologicalSample bioSample2 = bioSamples.get(1);
    assert bioSample1.isAnalyzeAsMudpit();
    assert !bioSample2.isAnalyzeAsMudpit();
    assert bioSample1.getName().equals("Sample1");
    assert bioSample1.getInputFile().get(0).equals("name123.zip");
    assert bioSample1.getInputFile().get(1).equals("name223.zip");

    assert bioSample1.getCategory().equals("cat1");
    assert bioSample2.getName().equals("Sample2");
    assert bioSample2.getCategory().equals("cat2");
    assert bioSample2.getInputFile().get(0).equals("name323.zip");

    final QuantitativeModel model = bioSample1.getQuantitativeModel();
    assert model.getPurityCorrection().equals("0.0,1.0,0.0");
    assert model.getType().equals("iTRAQ 4-Plex");

    final QuantitativeSample quantSample = model.getQuantitativeSample().get(0);
    assert quantSample.getName().equals("qname");
    assert quantSample.isPrimary();
    assert quantSample.getCategory().equals("qcat");
    assert quantSample.getReporter().equals("114");
    assert quantSample.getDescription().equals("qdescription");

    assert bioSample2.getQuantitativeModel() == null;

    final FastaDatabase fastaDatabase1 = fastaDatabases.get(0);
    assert fastaDatabase1.getId().equals(bioSample1.getDatabase());
    assert fastaDatabase1.getName().equals("FooBase");
    assert fastaDatabase1.getDecoyProteinRegEx().equals("REVERSE");

    final FastaDatabase fastaDatabase2 = fastaDatabases.get(1);
    assert fastaDatabase2.getId().equals(bioSample2.getDatabase());
    assert fastaDatabase2.getName().equals("FooBase_1");
    assert fastaDatabase2.getDatabaseAccessionRegEx().equals(DatabaseType.IPI.getAccessionRegEx());
    assert fastaDatabase2.getDatabaseDescriptionRegEx().equals(DatabaseType.IPI.getDescriptionRegEx());
    assert fastaDatabase2.getDecoyProteinRegEx().equals("REVERSE");

    assert scaffold.getExperiment().getDisplayThresholds().get(0).getMinimumNTT() == 3;
    assert scaffold.getExperiment().getDisplayThresholds().get(0).getMinimumPeptideCount() == 4;
    assert Math.abs(scaffold.getExperiment().getDisplayThresholds().get(0).getPeptideProbability() - .95) < .001;
    assert Math.abs(scaffold.getExperiment().getDisplayThresholds().get(0).getProteinProbability() - .99) < .001;
    assert scaffold.getExperiment().getDisplayThresholds().get(0).getUseCharge().matches("false,\\s*true,\\s*true");
  }
}
