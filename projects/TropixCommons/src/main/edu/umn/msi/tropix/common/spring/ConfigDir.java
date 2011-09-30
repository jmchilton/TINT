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

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;

public class ConfigDir extends MapPropertyPlaceholderConfigurer implements Supplier<String>, ApplicationContextAware {
  private final String name;
  private final String parent;
  private String configDir;

  public ConfigDir(final String name) {
    this(name, "tropix");
  }

  public ConfigDir(final String name, final String parent) {
    this.name = name;
    this.parent = parent;
    setIgnoreResourceNotFound(true);
    setIgnoreUnresolvablePlaceholders(true);
  }

  public String get() {
    return configDir;
  }

  @SuppressWarnings("unchecked")
  public void setApplicationContext(final ApplicationContext context) throws BeansException {
    final String propertyName = "tropix." + name + ".config.dir";
    if(StringUtils.hasText(System.getProperty(propertyName))) {
      this.configDir = System.getProperty(propertyName);
    } else {
      this.configDir = ((Supplier<String>) context.getBean(parent + "ConfigDir")).get() + File.separator + name;
    }
    super.setProperties(new ImmutableMap.Builder<String, String>().put(propertyName, configDir).build());
  }

}
