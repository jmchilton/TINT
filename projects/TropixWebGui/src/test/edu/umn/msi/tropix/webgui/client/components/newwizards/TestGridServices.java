package edu.umn.msi.tropix.webgui.client.components.newwizards;

import java.util.UUID;

import edu.umn.msi.tropix.client.services.GridService;
import edu.umn.msi.tropix.common.reflect.ReflectionHelpers;

public class TestGridServices {

  public static <T extends GridService> T getTestService(final Class<T> clazz) {
    final T instance = ReflectionHelpers.getInstance().newInstance(clazz);
    instance.setServiceAddress(UUID.randomUUID().toString());
    return instance;
  }

}
