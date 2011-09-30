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

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.FreshConfigTest;

@ContextConfiguration(locations = "probeConfigDirContext.xml")
public class FreshConfigTestTest extends FreshConfigTest {
  
  @Inject
  private ConfigDir testConfigDir;
  
  @Inject
  private Bean bean;
  
  @Value("${tropix.test.config.dir}")
  private String injectedValue;
  
  @Test(groups = "unit")
  public void test() {
    assert !bean.getProperty().equals(new File(System.getProperty("user.home"), ".tropix").getAbsolutePath());
    assert new File(testConfigDir.get()).equals(new File(bean.getProperty(), "test")) : "Expecting " + new File(bean.getProperty(), "test") + " got " + testConfigDir.get();
    assert new File(testConfigDir.get()).equals(new File(injectedValue));
  }
  
}
