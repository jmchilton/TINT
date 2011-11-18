package edu.umn.msi.tropix.common.test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;

import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.common.io.OutputContexts;

final class ConfigDirBuilderImpl implements ConfigDirBuilder {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();

  private final File directory;
  private Properties properties = new Properties();
  private Map<String, ConfigDirBuilderImpl> subDirs = Maps.newHashMap();

  ConfigDirBuilderImpl(final File directory) {
    this.directory = directory;
  }

  public ConfigDirBuilder addDeployProperty(final String propertyName, final String propertyValue) {
    properties.put(propertyName, propertyValue);
    return this;
  }

  public File getDirectory() {
    return directory;
  }

  public ConfigDirBuilder createSubConfigDir(final String name) {
    final ConfigDirBuilderImpl subDirBuilder = new ConfigDirBuilderImpl(new File(directory, name));
    subDirs.put(name, subDirBuilder);
    return subDirBuilder;
  }

  void build() throws IOException {
    if(!properties.isEmpty()) {
      final File deployPropertiesFile = new File(directory, "deploy.properties");
      final OutputStream outputStream = FILE_UTILS.getFileOutputStream(deployPropertiesFile);
      try {
        properties.store(outputStream, "");
      } finally {
        IO_UTILS.closeQuietly(outputStream);
      }
    }
    for(Map.Entry<String, ConfigDirBuilderImpl> subConfigDirEntry : subDirs.entrySet()) {
      final ConfigDirBuilderImpl subConfigDirBuilder = subConfigDirEntry.getValue();
      final File subConfigDirFile = new File(directory, subConfigDirEntry.getKey());
      FILE_UTILS.mkdirs(subConfigDirFile);
      subConfigDirBuilder.build();
    }
  }

  public OutputContext getOutputContext(final String filename) {
    final File file = new File(getDirectory(), filename);
    file.getParentFile().mkdirs();
    return OutputContexts.forFile(file);
  }
}