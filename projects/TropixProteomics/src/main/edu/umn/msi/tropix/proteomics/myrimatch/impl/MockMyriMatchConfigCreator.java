package edu.umn.msi.tropix.proteomics.myrimatch.impl;

import java.io.File;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.test.ConfigDirBuilder;

public class MockMyriMatchConfigCreator {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private final ConfigDirBuilder myriMatchConfigDirBuilder;

  public MockMyriMatchConfigCreator(final ConfigDirBuilder configDirBuilder) {
    this.myriMatchConfigDirBuilder = configDirBuilder.createSubConfigDir("myrimatch");
  }

  public ConfigDirBuilder build() {
    final File mockMyriMatchFile = new File(myriMatchConfigDirBuilder.getDirectory(), "mock_myrimatch");
    final String mockMyrimatchPath = mockMyriMatchFile.getAbsolutePath();
    myriMatchConfigDirBuilder.addDeployProperty("myrimatch.path", mockMyrimatchPath);
    mockMyriMatchFile.getParentFile().mkdirs();
    FILE_UTILS.writeStringToFile(mockMyriMatchFile, "#!/bin/sh\ntouch input.pepXML");
    return myriMatchConfigDirBuilder;
  }

}
