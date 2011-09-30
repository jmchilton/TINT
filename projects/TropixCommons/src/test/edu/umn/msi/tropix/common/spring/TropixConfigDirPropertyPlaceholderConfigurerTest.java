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

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;

public class TropixConfigDirPropertyPlaceholderConfigurerTest {

  @Test(groups = "unit")
  public void testGet() {
    // TODO: Implement
  }
  

  @Test(groups = "unit")
  public void testOverrideTropixConfigDir() {
    final FileUtils fileUtils = FileUtilsFactory.getInstance();
    final File tempDir = fileUtils.createTempDirectory();
    try {
      TropixConfigDirPropertyPlaceholderConfigurer.overrideConfigDir(tempDir);
      final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("edu/umn/msi/tropix/common/spring/probeConfigDirContext.xml");
      final ConfigDir testConfigDir = context.getBean(ConfigDir.class);
      final Bean bean = context.getBean(Bean.class);
      assert !bean.getProperty().equals(new File(System.getProperty("user.home"), ".tropix").getAbsolutePath());
      assert new File(testConfigDir.get()).equals(new File(bean.getProperty(), "test")) : "Expecting " + new File(bean.getProperty(), "test") + " got " + testConfigDir.get();
    } finally {
      fileUtils.deleteDirectory(tempDir);
    }
  }  
  
}
