package edu.umn.msi.tropix.proteomics.client;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.proteomics.idpicker.parameters.IdPickerParameters;

public class IdPickerUtils {

  public static IdPickerParameters deserialize(final InputContext inputContext) {
    return AxisXmlParameterUtils.deserialize(inputContext, IdPickerParameters.class);
  }

  public static String serialize(final IdPickerParameters parameters) {
    return AxisXmlParameterUtils.serialize(parameters, IdPickerParameters.getTypeDesc().getXmlType());
  }

}
