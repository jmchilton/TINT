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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.FreshConfigTest;

/**
 * Test DeployPropertiesPlacecholderConfigurer by creating creating a fresh configuration 
 * directory, placing a deploy.properties file in that directory and making sure that
 * properties get configured from that file properly.
 * 
 * @author John Chilton
 *
 */
@ContextConfiguration
public class DeployPropertiesPlaceholderConfigurerTest extends FreshConfigTest {

  @Override
  protected void initializeConfigDir(final ConfigDirBuilder rootConfigDirBuilder) {
    final ConfigDirBuilder testConfigDirBuilder = rootConfigDirBuilder.createSubConfigDir("test");
    testConfigDirBuilder.addDeployProperty("foo", "bar");
  }
  
  @Value("${foo}")
  private String foo;
  
  @Test(groups = "spring")
  public void testPropertyIntialized() {
    assert applicationContext.containsBean("testConfigDir");
    //assert tropixConfigDir != null : "TropixConfigDir is null";
    final ConfigDir configDir = (ConfigDir) applicationContext.getBean("testConfigDir");
    final String defaultConfigDir = System.getProperty("user.home") + File.separator + ".tropix";
    //assert !tropixConfigDir.getAbsolutePath().startsWith(defaultConfigDir) : "field start with default";
    assert !configDir.get().startsWith(defaultConfigDir) : "bean starts with default";
    assert foo.equals("bar") : foo;
  }
  
}
