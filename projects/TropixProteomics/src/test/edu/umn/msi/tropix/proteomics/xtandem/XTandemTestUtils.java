package edu.umn.msi.tropix.proteomics.xtandem;

import java.io.InputStream;

import edu.umn.msi.tropix.models.xtandem.XTandemParameters;
import edu.umn.msi.tropix.proteomics.parameters.ParameterUtils;
import edu.umn.msi.tropix.proteomics.test.ProteomicsTests;

public class XTandemTestUtils {

  public static XTandemParameters getXTandemParameters() {
    final InputStream inputStream = ProteomicsTests.getResourceAsStream("xTandemSimple.properties");
    assert inputStream != null;
    final XTandemParameters xTandemParameters = new XTandemParameters();
    ParameterUtils.setParametersFromProperties(inputStream, xTandemParameters);
    return xTandemParameters;
  }

}
