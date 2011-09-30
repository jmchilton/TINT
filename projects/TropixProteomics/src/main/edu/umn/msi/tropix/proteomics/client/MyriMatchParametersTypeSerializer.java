package edu.umn.msi.tropix.proteomics.client;

import java.util.Map;

import javax.annotation.ManagedBean;

import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.models.proteomics.IdentificationType;
import edu.umn.msi.tropix.proteomics.bumbershoot.parameters.MyriMatchParameters;
import edu.umn.msi.tropix.proteomics.parameters.ParameterUtils;

@ManagedBean
class MyriMatchParametersTypeSerializer extends IdentificationParametersTypeSerializer {

  public MyriMatchParametersTypeSerializer() {
    super(IdentificationType.MYRIMATCH);
  }

  public MyriMatchParameters loadRawParameters(final InputContext parameterContext) {
    return MyriMatchUtils.deserialize(parameterContext);
  }

  public Map<String, String> loadParametersAndExpandMap(final InputContext parameterContext) {
    final Map<String, String> map = Maps.newHashMap();
    ParameterUtils.setMapFromParameters(loadRawParameters(parameterContext), map);
    return map;
  }

  public String serializeParameterMap(final Map<String, String> parameterMap) {
    final MyriMatchParameters myriMatchParameters = new MyriMatchParameters();
    ParameterUtils.setParametersFromMap(parameterMap, myriMatchParameters);
    return MyriMatchUtils.serialize(myriMatchParameters);
  }

}