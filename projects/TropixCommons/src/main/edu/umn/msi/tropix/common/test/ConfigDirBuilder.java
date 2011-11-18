package edu.umn.msi.tropix.common.test;

import java.io.File;

import edu.umn.msi.tropix.common.io.OutputContext;

/**
 * Interface for building test tropix config directories using the Builder
 * pattern.
 * 
 * @author John
 * 
 */
public interface ConfigDirBuilder {
  /**
   * Creates a configuration subdirectory beneath the current one.
   * 
   * @param name
   *          Name of subdirectory to build configuration for.
   * @return A new ConfigDirBuilder for the specified subdirectory.
   */
  ConfigDirBuilder createSubConfigDir(final String name);

  /**
   * 
   * Add the specified property to the current config directory.
   * 
   * @param propertyName
   * @param propertyValue
   * @return This configuration directory.
   */
  ConfigDirBuilder addDeployProperty(final String propertyName, final String propertyValue);

  OutputContext getOutputContext(final String filename);

  File getDirectory();
}