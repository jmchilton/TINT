package edu.umn.msi.tropix.proteomics.client;

import java.util.Map;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.models.proteomics.IdentificationType;

public interface IdentificationParameterExpander {
  Map<String, String> loadParameterMap(final IdentificationType paramType, final InputContext parameterDownloadContext);
}