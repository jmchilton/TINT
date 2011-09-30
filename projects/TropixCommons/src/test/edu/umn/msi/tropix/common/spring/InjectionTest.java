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

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

/**
 * Tests that @Inject and @Autowired can work together.
 * 
 * @author John Chilton
 *
 */
@ContextConfiguration(locations = "injectAndAutowiredTest.xml")
public class InjectionTest extends AbstractTestNGSpringContextTests {
  private TestClass1 testClass1; 
  private TestClass3 testClass3;
  
  @Inject
  void setTestClass1(final TestClass1 testClass1) {
    this.testClass1 = testClass1;
  }

  @Autowired
  void setTestClass3(final TestClass3 testClass3) {
    this.testClass3 = testClass3;
  }
  
  /**
   * Tests that both fields are injected.
   */
  @Test(groups = "unit")
  public void injectedFields() {
    assert testClass1 != null;
    assert testClass3 != null;
  }
  
}
