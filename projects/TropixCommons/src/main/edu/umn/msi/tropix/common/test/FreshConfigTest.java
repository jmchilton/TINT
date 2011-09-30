/*******************************************************************************
 * Copyright 2009 Regents of the University of Minnesota. All rights
 * reserved.
 * Copyright 2009 Mayo Foundation for Medical Education and Research.
 * All rights reserved.
 *
 * This program is made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED INCLUDING, WITHOUT LIMITATION, ANY WARRANTIES OR CONDITIONS
 * OF TITLE, NON-INFRINGEMENT, MERCHANTABILITY OR FITNESS FOR A
 * PARTICULAR PURPOSE.  See the License for the specific language
 * governing permissions and limitations under the License.
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 ******************************************************************************/

package edu.umn.msi.tropix.common.test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;

import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.common.io.OutputContexts;
import edu.umn.msi.tropix.common.spring.TropixConfigDirPropertyPlaceholderConfigurer;

/**
 * Sets up a fresh tropix configuration environment for one test class.
 * 
 * @author John Chilton
 *
 */
public class FreshConfigTest  extends AbstractTestNGSpringContextTests  {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private File tropixConfigDir;
  
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
     * @param name Name of subdirectory to build configuration for.
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
  
  /**
   * Allow subclasses to initialize a configuration directory before Spring is
   * initialized.
   */
  protected void initializeConfigDir(final ConfigDirBuilder configDirBuilder) {

  }
  
  private final class ConfigDirBuilderImpl implements ConfigDirBuilder {
    private final File directory;
    private Properties properties = new Properties();
    private Map<String, ConfigDirBuilderImpl> subDirs = Maps.newHashMap();

    private ConfigDirBuilderImpl(final File directory) {
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
    
    private void build() throws IOException {
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
  
  @BeforeClass(alwaysRun = true)
  @Override
  protected void springTestContextBeforeTestClass() throws Exception {
    tropixConfigDir = FILE_UTILS.createTempDirectory();
    TropixConfigDirPropertyPlaceholderConfigurer.overrideConfigDir(tropixConfigDir);
    final ConfigDirBuilderImpl configDirBuilder = new ConfigDirBuilderImpl(tropixConfigDir);
    initializeConfigDir(configDirBuilder);
    configDirBuilder.build();
    super.springTestContextBeforeTestClass();
  }
  

  @BeforeClass(alwaysRun = true, dependsOnMethods = "springTestContextBeforeTestClass")
  @Override
  protected void springTestContextPrepareTestInstance() throws Exception {
    super.springTestContextPrepareTestInstance();
  }

  @AfterClass(alwaysRun = true)
  @Override
  protected void springTestContextAfterTestClass() throws Exception {
    try {
      super.springTestContextAfterTestClass();
    } finally {
      FILE_UTILS.deleteDirectoryQuietly(tropixConfigDir);
    }
  }
    
}
