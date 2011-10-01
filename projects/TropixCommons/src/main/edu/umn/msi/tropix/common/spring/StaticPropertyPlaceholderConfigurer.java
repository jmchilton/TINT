package edu.umn.msi.tropix.common.spring;

import java.util.Map;
import java.util.Properties;

import com.google.common.collect.Maps;

public class StaticPropertyPlaceholderConfigurer extends ComposablePropertyPlaceholderConfigurer {
  private static Map<String, String> properties = Maps.newHashMap();

  @Override
  protected String resolvePlaceholder(final String placeholder, final Properties props) {
    final String value = properties.get(placeholder);
    return value;
  }

  public static void addProperty(final String property, final String value) {
    properties.put(property, value);
  }

}
