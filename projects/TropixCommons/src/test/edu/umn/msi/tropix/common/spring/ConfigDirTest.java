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

import org.easymock.EasyMock;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;

public class ConfigDirTest {

  @Test(groups = "unit")
  public void testConfigDirFromSystemProperty() {
    System.setProperty("tropix.configdirtest.config.dir", "/tmp/moo");
    final ConfigDir dir = new ConfigDir("configdirtest");
    dir.setApplicationContext(null);
    assert dir.get().equals("/tmp/moo");
    assert dir.resolvePlaceholder("tropix.configdirtest.config.dir", null).equals("/tmp/moo");
  }

  @Test(groups = "unit")
  public void testConfigDirFromParent() {
    final ConfigDir dir = new ConfigDir("configdirtest2");    
    final ApplicationContext context = EasyMock.createMock(ApplicationContext.class);
    EasyMock.expect(context.getBean("tropixConfigDir")).andReturn(Suppliers.ofInstance("/tmp/moo"));
    EasyMock.replay(context);
    dir.setApplicationContext(context);
    assert dir.get().equals("/tmp/moo/configdirtest2") : dir.get();
    assert dir.resolvePlaceholder("tropix.configdirtest2.config.dir", null).equals("/tmp/moo/configdirtest2");
  }
  

}
