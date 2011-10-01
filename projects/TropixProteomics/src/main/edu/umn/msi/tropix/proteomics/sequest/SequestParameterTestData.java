package edu.umn.msi.tropix.proteomics.sequest;

import java.io.InputStream;
import java.util.Map;

import edu.umn.msi.tropix.models.sequest.SequestParameters;
import edu.umn.msi.tropix.proteomics.parameters.ParameterUtils;
import edu.umn.msi.tropix.proteomics.test.ProteomicsTests;

public class SequestParameterTestData {

  public static SequestParameters getSimpleSequestParameters() throws Exception {
    final InputStream inputStream = ProteomicsTests.getResourceAsStream("sequestSimple.properties");
    final SequestParameters sequestParameters = new SequestParameters();
    ParameterUtils.setParametersFromProperties(inputStream, sequestParameters);
    return sequestParameters;
  }

  public static Map<String, String> getSimpleSequestParametersMap() throws Exception {
    final SequestParameters parameters = getSimpleSequestParameters();
    return ParameterUtils.mapForParameters(parameters);
  }

}
