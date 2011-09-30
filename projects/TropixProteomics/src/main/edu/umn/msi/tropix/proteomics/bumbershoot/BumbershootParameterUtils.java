package edu.umn.msi.tropix.proteomics.bumbershoot;

import java.util.Map;

import org.apache.commons.collections.MapUtils;

import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.io.PropertiesUtils;
import edu.umn.msi.tropix.common.io.PropertiesUtilsFactory;
import edu.umn.msi.tropix.proteomics.parameters.ParameterUtils;

public class BumbershootParameterUtils {
  private static final PropertiesUtils PROPERTIES_UTILS = PropertiesUtilsFactory.getInstance();

  public static String serializeBumbershootParameters(final Object parameters) {
    Map<String, String> parametersMap = Maps.newHashMap();
    ParameterUtils.setMapFromParameters(parameters, parametersMap);
    parametersMap = fixParameterNamesCase(parametersMap);
    final String propertiesString = PROPERTIES_UTILS.toString(MapUtils.toProperties(parametersMap));
    return propertiesString;
  }

  private static Map<String, String> fixParameterNamesCase(final Map<String, String> parametersMap) {
    final Map<String, String> fixedParameters = Maps.newHashMap();
    for(Map.Entry<String, String> parametersEntry : parametersMap.entrySet()) {
      fixedParameters.put(fixParameterName(parametersEntry.getKey()), parametersEntry.getValue());
    }
    return fixedParameters;
  }

  private static String fixParameterName(final String key) {
    return key.substring(0, 1).toUpperCase() + key.substring(1);
  }

}
