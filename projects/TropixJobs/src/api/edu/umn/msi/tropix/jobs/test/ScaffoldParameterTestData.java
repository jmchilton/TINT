package edu.umn.msi.tropix.jobs.test;

import java.util.HashMap;
import java.util.Map;

public class ScaffoldParameterTestData {
  
  public static Map<String, String> getTestParameters() {
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
    return parameters;
  }
}
