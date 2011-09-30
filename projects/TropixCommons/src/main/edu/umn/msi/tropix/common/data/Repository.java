package edu.umn.msi.tropix.common.data;

import java.io.InputStream;

import edu.umn.msi.tropix.common.io.InputContext;

public interface Repository {
  
  InputStream getResource(final Class<?> clazz, final String resourceName);
  
  InputContext getResourceContext(final Class<?> clazz, final String resourceName);
  
  InputStream getResource(final String packageName, final String resourceName);
  
  InputContext getResourceContext(final String packageName, final String resourceName);

}
