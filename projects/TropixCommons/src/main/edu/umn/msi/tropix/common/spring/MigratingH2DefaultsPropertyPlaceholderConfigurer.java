package edu.umn.msi.tropix.common.spring;

import java.util.Map;

import com.google.common.base.Supplier;

public class MigratingH2DefaultsPropertyPlaceholderConfigurer extends H2DefaultsPropertyPlaceholderConfigurer {

  public MigratingH2DefaultsPropertyPlaceholderConfigurer(final String shortname, final boolean testing) {
    super(shortname, testing);
  }

  public MigratingH2DefaultsPropertyPlaceholderConfigurer(final String shortname, final Supplier<String> configPathSupplier) {
    super(shortname, configPathSupplier);
  }

  public MigratingH2DefaultsPropertyPlaceholderConfigurer(final String shortname) {
    super(shortname);
  }
  
  @Override
  protected Map<String, String> getDefaultProperties() {
    final Map<String, String> defaultProperties = super.getDefaultProperties();
    defaultProperties.put(getHbm2DdlPropertyName(), "migrate");
    return defaultProperties;
    
  }  

}
