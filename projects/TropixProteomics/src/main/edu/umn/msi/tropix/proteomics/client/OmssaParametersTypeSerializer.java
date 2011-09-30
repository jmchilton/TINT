package edu.umn.msi.tropix.proteomics.client;

import java.util.Map;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;

import edu.umn.msi.tropix.common.collect.InvertiableFunction;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.models.proteomics.IdentificationType;
import gov.nih.nlm.ncbi.omssa.MSSearchSettings;

@ManagedBean
class OmssaParametersTypeSerializer extends IdentificationParametersTypeSerializer {
  private final InvertiableFunction<Map<String, String>, MSSearchSettings> omssaParametersFunction;

  @Inject
  OmssaParametersTypeSerializer(
      @Named("omssaParametersFunction") final InvertiableFunction<Map<String, String>, MSSearchSettings> omssaParametersFunction) {
    super(IdentificationType.OMSSA);
    this.omssaParametersFunction = omssaParametersFunction;
  }

  MSSearchSettings loadRawParameters(final InputContext parameterContext) {
    return OmssaUtils.extractMSSearchSettings(parameterContext);
  }

  Map<String, String> loadParametersAndExpandMap(final InputContext parameterContext) {
    return omssaParametersFunction.inverse(loadRawParameters(parameterContext));
  }

  String serializeParameterMap(final Map<String, String> parameterMap) {
    final MSSearchSettings settings = omssaParametersFunction.apply(parameterMap);
    return OmssaUtils.serializeMSSearchSettigns(settings);
  }

}
