package edu.umn.msi.tropix.proteomics.client;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.models.proteomics.IdentificationType;

public interface IdentificationParametersDeserializer {
  <T> T loadParameters(final IdentificationType paramType, final InputContext parameterContext, final Class<T> parameterClass);
}
