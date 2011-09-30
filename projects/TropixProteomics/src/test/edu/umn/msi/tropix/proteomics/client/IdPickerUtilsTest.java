package edu.umn.msi.tropix.proteomics.client;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.proteomics.idpicker.parameters.IdPickerParameters;

public class IdPickerUtilsTest {

  @Test(groups = "unit")
  public void testSerialization() {
    final IdPickerParameters parameters = new IdPickerParameters();
    final String serializedParameters = IdPickerUtils.serialize(parameters);
    final InputContext serializedParametersContext = InputContexts.forString(serializedParameters);
    final IdPickerParameters convertedParameters = IdPickerUtils.deserialize(serializedParametersContext);
    assert convertedParameters != null;
    assert convertedParameters.equals(parameters);
  }  
  
}
