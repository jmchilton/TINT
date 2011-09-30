package edu.umn.msi.tropix.proteomics.client;

import java.util.Map;

import edu.umn.msi.tropix.models.proteomics.IdentificationType;

public interface IdentificationParametersSerializer {
  String serializeParameters(final IdentificationType parameterType, final Map<String, String> parametersMap);
}