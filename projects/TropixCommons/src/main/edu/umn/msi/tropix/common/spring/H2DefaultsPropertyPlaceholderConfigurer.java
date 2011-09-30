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

package edu.umn.msi.tropix.common.spring;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.google.common.base.Supplier;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.collect.StringPredicates;
import edu.umn.msi.tropix.common.io.FileFunctions;
import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;

public class H2DefaultsPropertyPlaceholderConfigurer extends MapPropertyPlaceholderConfigurer implements ApplicationContextAware {
  private static final Log LOG = LogFactory.getLog(H2DefaultsPropertyPlaceholderConfigurer.class);
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private ApplicationContext context;
  private final String shortname;
  private Supplier<String> configPathSupplier;
  private final boolean testing;
  private boolean initialized = false;

  public H2DefaultsPropertyPlaceholderConfigurer(final String shortname) {
    this(shortname, false);
  }

  public H2DefaultsPropertyPlaceholderConfigurer(final String shortname, final Supplier<String> configPathSupplier) {
    this(shortname, false);
    this.configPathSupplier = configPathSupplier;
  }

  @Override
  protected String resolvePlaceholder(final String placeholder, final Properties props) {
    if(!initialized) {
      init();
      initialized = true;
    }
    return super.resolvePlaceholder(placeholder, props);
  }

  private void init() {
    String configDir;
    final String beanName = shortname + "ConfigDir";
    if(configPathSupplier != null) {
      configDir = configPathSupplier.get();
    } else if(context.containsBean(beanName)) {
      @SuppressWarnings("unchecked")
      final Supplier<String> configDirSupplier = (Supplier<String>) context.getBean(shortname + "ConfigDir");
      configDir = configDirSupplier.get();
    } else {
      configDir = System.getProperty("user.home") + File.separator + ".tropix" + File.separator + File.separator + shortname;
    }
    LOG.debug("Initializing H2 database properties with shortname " + shortname + " and configDir " + configDir);
    final Map<String, String> defaultProperties = Maps.newHashMap();
    defaultProperties.put(shortname + ".db.username", "sa");
    defaultProperties.put(shortname + ".db.password", "");
    defaultProperties.put(shortname + ".db.driver", "org.h2.Driver");
    defaultProperties.put(shortname + ".db.dialect", "org.hibernate.dialect.H2Dialect");
    defaultProperties.put(shortname + ".db.showsql", "false");
    if(testing) {
      defaultProperties.put(shortname + ".db.hbm2ddl", "create");
      defaultProperties.put(shortname + ".db.url", "jdbc:h2:mem:" + shortname + ";DB_CLOSE_DELAY=-1");
    } else {
      final String dbPath = getDbPath(new File(configDir), shortname);
      defaultProperties.put(shortname + ".db.url", "jdbc:h2:" + dbPath);
      final File dbDir = new File(dbPath).getParentFile();
      boolean filesExist = false;
      if(dbDir.exists() && dbDir.isDirectory()) {
        final Collection<String> fileNames = Collections2.transform(FILE_UTILS.listFiles(dbDir), FileFunctions.getNameFunction());
        filesExist = !Collections2.filter(fileNames, StringPredicates.startsWith("db")).isEmpty();
      }
      defaultProperties.put(shortname + ".db.hbm2ddl", filesExist ? "update" : "create");
    }
    setProperties(defaultProperties);
  }

  public H2DefaultsPropertyPlaceholderConfigurer(final String shortname, final boolean testing) {
    this.shortname = shortname;
    this.testing = testing;
  }

  private String getDbPath(final File configDir, final String shortName) {
    return new File(configDir, "db").getAbsolutePath();
  }

  public void setApplicationContext(final ApplicationContext context) throws BeansException {
    this.context = context;
  }
}
