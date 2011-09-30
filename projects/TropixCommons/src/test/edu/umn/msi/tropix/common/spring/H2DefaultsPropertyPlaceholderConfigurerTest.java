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

import org.testng.annotations.Test;

import com.google.common.base.Suppliers;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.test.TestNGDataProviders;

public class H2DefaultsPropertyPlaceholderConfigurerTest {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  
  @Test(groups = "unit", dataProvider="bool1", dataProviderClass=TestNGDataProviders.class)
  public void testFromConfigDirectory(final boolean dirExists) {
    final File tempDir = FILE_UTILS.createTempDirectory();
    try {
      if(!dirExists) {
        FILE_UTILS.deleteDirectory(tempDir);
        assert !tempDir.exists();
      } else {
        FILE_UTILS.touch(new File(tempDir, "db.moo.log"));
      }
      final H2DefaultsPropertyPlaceholderConfigurer configurer = new H2DefaultsPropertyPlaceholderConfigurer("moo", Suppliers.ofInstance(tempDir.getAbsolutePath()));
      assert configurer.resolvePlaceholder("moo.db.username", null).equals("sa");
      assert configurer.resolvePlaceholder("moo.db.password", null).equals("");
      assert configurer.resolvePlaceholder("moo2.db.username", null) == null;
      
      assert configurer.resolvePlaceholder("moo.db.driver", null).equals("org.h2.Driver");
      assert configurer.resolvePlaceholder("moo.db.dialect", null).equals("org.hibernate.dialect.H2Dialect");
      assert configurer.resolvePlaceholder("moo.db.showsql", null).equals("false");
      if(!dirExists) {
        assert configurer.resolvePlaceholder("moo.db.hbm2ddl", null).equals("create");
      } else {
        assert configurer.resolvePlaceholder("moo.db.hbm2ddl", null).equals("update");
      }
    } finally {
      FILE_UTILS.deleteDirectory(tempDir);
    }
  }
}
