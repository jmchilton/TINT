package edu.umn.msi.tropix.webgui.server.session;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

import edu.umn.msi.tropix.client.services.Constants;
import edu.umn.msi.tropix.client.services.GridService;
import edu.umn.msi.tropix.client.services.GridServicesSupplier;
import edu.umn.msi.tropix.common.manager.GenericManager;
import edu.umn.msi.tropix.common.test.TestNGDataProviders;
import edu.umn.msi.tropix.webgui.client.ConfigurationOptions;
import edu.umn.msi.tropix.webgui.services.session.SessionInfo;

public class ServiceEnabledSessionInfoClosureImplTest extends BaseSessionInfoClosureImplTest {

  @Test(groups = "unit", dataProvider = "bool1", dataProviderClass = TestNGDataProviders.class)
  public void testIdPickerPopulated(final boolean isEnabled) {
    final String[] serviceTypes = new String[] {Constants.ID_PICKER, Constants.SCAFFOLD, Constants.ITRAQ_QUANTIFICATION};
    final ImmutableMap.Builder<String, GridServicesSupplier<?>> builder = ImmutableMap.<String, GridServicesSupplier<?>>builder();
    for(final String serviceType : serviceTypes) {
      @SuppressWarnings("unchecked")
      final GridServicesSupplier<GridService> serviceSupplier = EasyMock.createMock(GridServicesSupplier.class);
      EasyMock.expect(serviceSupplier.isEmpty()).andReturn(isEnabled);
      EasyMock.replay(serviceSupplier);
      builder.put(serviceType, serviceSupplier);
    }
    final GenericManager<String, GridServicesSupplier<?>> manager = new GenericManager<String, GridServicesSupplier<?>>();
    manager.setMap(builder.build());

    final ServiceEnabledSessionInfoClosureImpl closure = new ServiceEnabledSessionInfoClosureImpl(manager);
    final SessionInfo sessionInfo = get(closure);
    for(final String serviceType : serviceTypes) {
      assert sessionInfo.getConfigurationOptions().get(ConfigurationOptions.serviceEnabled(serviceType)) == Boolean.toString(isEnabled);
    }
  }
}
