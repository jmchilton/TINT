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
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.google.common.base.Supplier;

public class DeployPropertiesPlaceholderConfigurer extends ComposablePropertyPlaceholderConfigurer implements ApplicationContextAware {
  private final String name;

  public DeployPropertiesPlaceholderConfigurer(final String name) {
    this.name = name;
  }

  @Override
  protected String resolvePlaceholder(final String placeholder, final Properties props) {
    return super.resolvePlaceholder(placeholder, props);
  }

  @SuppressWarnings("unchecked")
  public void setApplicationContext(final ApplicationContext context) throws BeansException {
    final String beanName = name + "ConfigDir";
    // In case desired bean  does not exist ensure reasonable default for locations
    this.setLocations(new Resource[] {});
    
    // If configDir bean exists, grab the specified directory and load properties from the
    // deploy.properties file found therein.
    if(context.containsBean(beanName)) {
      final String resource = ((Supplier<String>) context.getBean(beanName)).get() + File.separator + "deploy.properties";
      this.setLocation(new FileSystemResource(resource));
    }
  }

}
