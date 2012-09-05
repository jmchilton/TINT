package edu.umn.msi.tropix.client.galaxy.impl;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.management.annotation.ManagedAttribute;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.google.common.base.Supplier;

@Named("galaxyInstanceSupplier")
@ManagedResource
public class GalaxyInstanceSupplierImpl implements Supplier<GalaxyInstance> {
  private static final Log LOG = LogFactory.getLog(GalaxyInstanceSupplierImpl.class);
  private String instanceUrl;
  private String apiKey;

  public GalaxyInstance get() {
    return GalaxyInstanceFactory.get(instanceUrl, apiKey);
  }

  @ManagedAttribute
  public String getInstanceUrl() {
    return instanceUrl;
  }

  @ManagedAttribute
  public String getApiKey() {
    return apiKey;
  }

  @ManagedAttribute
  @Inject
  public void setInstanceUrl(@Value("${galaxy.client.instance}") final String instanceUrl) {
    LOG.debug("InstanceURL is " + instanceUrl);
    this.instanceUrl = instanceUrl;
  }

  @ManagedAttribute
  @Inject
  public void setApiKey(@Value("${galaxy.client.key}") final String apiKey) {
    LOG.debug("Setting galaxy api key to " + apiKey);
    this.apiKey = apiKey;
  }

}
