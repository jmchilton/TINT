package edu.umn.msi.tropix.proteomics.client;

import java.util.Map;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.models.proteomics.IdentificationType;

abstract class IdentificationParametersTypeSerializer {
  private IdentificationType identificationType;

  protected IdentificationParametersTypeSerializer(final IdentificationType identificationType) {
    this.identificationType = identificationType;
  }

  abstract Object loadRawParameters(final InputContext parameterContext);

  abstract Map<String, String> loadParametersAndExpandMap(final InputContext parameterContext);

  abstract String serializeParameterMap(final Map<String, String> parameterMap);

  public IdentificationType getIdentificationType() {
    return identificationType;
  }

}