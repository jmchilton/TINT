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

package edu.umn.msi.tropix.proteomics.myrimatch.impl;

import java.util.Properties;

import org.globus.exec.generated.JobDescriptionType;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.PropertiesUtils;
import edu.umn.msi.tropix.common.io.PropertiesUtilsFactory;
import edu.umn.msi.tropix.proteomics.bumbershoot.parameters.MyriMatchParameters;
import edu.umn.msi.tropix.proteomics.parameters.ParameterUtils;
import edu.umn.msi.tropix.proteomics.test.IdentificationJobProcessorFactoryTest;

public class MyriMatchJobProcessorImplTest extends IdentificationJobProcessorFactoryTest<MyriMatchParameters, MyriMatchJobProcessorFactoryImpl> {
  private static final PropertiesUtils PROPERTY_UTILS = PropertiesUtilsFactory.getInstance();

  @Test(groups = "unit")
  public void createJob() {
    setFactoryAndInit(new MyriMatchJobProcessorFactoryImpl());

    // Setup for preprocessing
    expectDownloadDatabaseAndInput();

    expectDownloadDatabaseTo("db.fasta");
    expectDownloadInputTo("input.mzXML");
    expectGetDirMockOutputContextAndRecord("myrimatch.cfg");
    setParameters(initializeParameters());

    getFactory().setApplicationPath("/usr/bin/myrimatch");
    replayBuildJobProcessorPreprocessAndVerify();

    // Verify preprocessing
    String myrimatchCfg = super.getRecordedOutputAsString("myrimatch.cfg");
    assert !myrimatchCfg.contains("DynamicMods");
    verifyParameters(getParameters(), PROPERTY_UTILS.load(myrimatchCfg));
    assert getExecutableDescription().getJobType().equals("MyriMatch");
    final JobDescriptionType jobDescription = getJobDescriptionType();
    assert jobDescription.getExecutable().equals("/usr/bin/myrimatch");
    final String[] arguments = jobDescription.getArgument();
    assert arguments[0].equals("-ProteinDatabase");
    assert arguments[1].equals("db.fasta");
    assert arguments[2].equals("input.mzXML");
    assert jobDescription.getDirectory().equals(getPath());

    // Setup for postprocessing
    expectAddResource("input.pepXML");

    replayPostprocessAndVerify();
  }

  private MyriMatchParameters initializeParameters() {
    final MyriMatchParameters parameters = new MyriMatchParameters();
    parameters.setClassSizeMultiplier(5);
    parameters.setCleavageRules("Lys-C");
    parameters.setDynamicMods(null);
    parameters.setFragmentMzTolerance(4.0);
    parameters.setFragmentMzToleranceUnits("ppm");
    parameters.setMaxResults(100);
    parameters.setNumChargeStates(6);
    parameters.setNumIntensityClasses(9);
    parameters.setNumMaxMissedCleavages(8);
    parameters.setNumMinTerminiCleavages(3);
    parameters.setPrecursorMzTolerance(5.3);
    parameters.setPrecursorMzToleranceUnits("daltons");
    parameters.setStaticMods("C 123");
    parameters.setUseAvgMassOfSequences(true);
    parameters.setUseChargeStateFromMS(true);
    return parameters;
  }

  private void verifyParameters(final MyriMatchParameters parameters, final Properties load) {
    assert load.getProperty("ClassSizeMultiplier").equals("5");
    assert load.getProperty("DynamicMods") == null;
    assert load.getProperty("DeisotopingMode") == null; // Parameter not set, shouldn't appear in file
    final MyriMatchParameters loadedParameters = new MyriMatchParameters();
    ParameterUtils.setParametersFromMap(load, loadedParameters);
    assert parameters.equals(loadedParameters);
  }

}
