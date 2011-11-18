package edu.umn.msi.tropix.common.test;

import java.io.File;
import java.io.IOException;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.spring.StaticPropertyPlaceholderConfigurer;
import edu.umn.msi.tropix.common.spring.TropixConfigDirPropertyPlaceholderConfigurer;

class FreshConfigurer {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private File tropixConfigDir;

  FreshConfigurer(final Object testCase) throws IOException {
    tropixConfigDir = FILE_UTILS.createTempDirectory();
    StaticPropertyPlaceholderConfigurer.addProperty("tropix.repository.config.dir", TropixConfigDirPropertyPlaceholderConfigurer.getConfigDir()
        + File.separator + "repository");
    TropixConfigDirPropertyPlaceholderConfigurer.overrideConfigDir(tropixConfigDir);
    final ConfigDirBuilderImpl configDirBuilder = new ConfigDirBuilderImpl(tropixConfigDir);
    if(testCase instanceof FreshConfigTest) {
      ((FreshConfigTest) testCase).initializeConfigDir(configDirBuilder);
    } else if(testCase instanceof FreshConfigTransactionalTest) {
      ((FreshConfigTransactionalTest) testCase).initializeConfigDir(configDirBuilder);
    }
    configDirBuilder.build();
  }

  void destroy() {
    FILE_UTILS.deleteDirectoryQuietly(tropixConfigDir);
  }

}