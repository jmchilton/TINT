package edu.umn.msi.tropix.webgui.server.session;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Function;

import edu.umn.msi.tropix.client.services.Constants;
import edu.umn.msi.tropix.client.services.GridServicesSupplier;
import edu.umn.msi.tropix.webgui.client.ConfigurationOptions;
import edu.umn.msi.tropix.webgui.services.session.SessionInfo;

@ManagedBean
public class ServiceEnabledSessionInfoClosureImpl implements SessionInfoClosure {
  private final Function<String, GridServicesSupplier<?>> manager;

  @Inject
  public ServiceEnabledSessionInfoClosureImpl(@Named("gridServiceSupplierManager") final Function<String, GridServicesSupplier<?>> manager) {
    this.manager = manager;
  }

  public void apply(final SessionInfo sessionInfo) {
    setServiceEnabledOption(sessionInfo, Constants.ID_PICKER);
    setServiceEnabledOption(sessionInfo, Constants.SCAFFOLD);
    setServiceEnabledOption(sessionInfo, Constants.ITRAQ_QUANTIFICATION);
  }

  private void setServiceEnabledOption(final SessionInfo sessionInfo, 
                                       final String serviceType) {
    sessionInfo.getConfigurationOptions().put(ConfigurationOptions.serviceEnabled(serviceType),
                                              Boolean.toString(manager.apply(serviceType).isEmpty()));
  }

}
