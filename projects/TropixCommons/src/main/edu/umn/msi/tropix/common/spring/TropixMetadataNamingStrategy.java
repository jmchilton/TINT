package edu.umn.msi.tropix.common.spring;

import org.springframework.jmx.export.naming.MetadataNamingStrategy;
import org.springframework.util.StringUtils;

public class TropixMetadataNamingStrategy extends MetadataNamingStrategy {

  @Override
  public void setDefaultDomain(final String defaultDomain) {
    if(StringUtils.hasText(defaultDomain) && !defaultDomain.startsWith("$")) {
      super.setDefaultDomain(defaultDomain);
    }
  }

}
