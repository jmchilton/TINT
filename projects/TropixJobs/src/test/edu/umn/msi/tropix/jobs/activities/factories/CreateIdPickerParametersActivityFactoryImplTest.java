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

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateIdPickerParametersDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.StringParameterSet;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.proteomics.client.IdPickerUtils;
import edu.umn.msi.tropix.proteomics.idpicker.parameters.IdPickerParameters;
import edu.umn.msi.tropix.proteomics.idpicker.parameters.Sample;
import edu.umn.msi.tropix.storage.client.ModelStorageData;

public class CreateIdPickerParametersActivityFactoryImplTest extends CreateMergeIdentificationAnalyisFactoryImplTest {
  private final String storageServiceUrl = "http://storage";
  
  @Test(groups = "unit")
  public void testRun() {
    final MockFactorySupportImpl factorySupport = new MockFactorySupportImpl();
    final CreateIdPickerParametersDescription description = TestUtils.init(new CreateIdPickerParametersDescription());
    description.setStorageServiceUrl(storageServiceUrl);
    final ActivityContext context = TestUtils.getContext();

    final CreateIdPickerParametersActivityFactoryImpl factory = new CreateIdPickerParametersActivityFactoryImpl(getAnalysisService(), factorySupport);

    populateScaffoldSamples(description);
    final IdentificationAnalysis[] analyses = createIdentificationAnalyses(storageServiceUrl);

    final Map<String, String> parameters = new HashMap<String, String>();
    parameters.put("MinDistinctPeptides", "3");
    description.setParameterSet(StringParameterSet.fromMap(parameters));


    final Database database = new Database();
    database.setId(UUID.randomUUID().toString());
    final TropixFile tropixFile = new TropixFile();
    tropixFile.setId(UUID.randomUUID().toString());
    tropixFile.setStorageServiceUrl(storageServiceUrl);
    tropixFile.setFileId(6 + "");
    database.setDatabaseFile(tropixFile);

    final Database[] databases = new Database[] {database, database, database};

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

    assert description.getDatabaseIds().getIds().size() == 1 : description.getDatabaseIds().getIds().size();

    final IdPickerParameters idPickerParameters = IdPickerUtils.deserialize(storageData.getDownloadContext());
    assert idPickerParameters.getMinDistinctPeptides() == 3;
    final List<Sample> samples = Lists.newArrayList(idPickerParameters.getSample());
    assert samples.size() == 2;
    assert samples.get(0).getName().equals("Sample1");
    assert samples.get(0).getInput()[0].equals("name123");
    assert samples.get(0).getInput()[1].equals("name223");
    assert samples.get(1).getName().equals("Sample2");
    assert samples.get(1).getInput()[0].equals("name323");
  }

}
