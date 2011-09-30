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

package edu.umn.msi.tropix.storage.core.monitor;

import java.io.File;

import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;

public class MonitorConfigImplTest {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  
  @Test(groups = "unit")
  public void testFileSet() {    
    final File tempFile = FILE_UTILS.createTempFile();
    try {
      FILE_UTILS.writeStringToFile(tempFile, "<monitorDirectories xmlns=\"http://msi.umn.edu/tropix/storage/core/monitor/config\"><directory path=\"/path/to/monitor\" sharedFolderName=\"thename\" /></monitorDirectories>");
      final MonitorConfigImpl config = new MonitorConfigImpl();
      config.setConfigFile(tempFile.getAbsolutePath());
      assert Iterables.contains(config.getDirectories(), new File("/path/to/monitor"));
      config.getSharedFolderName(new File("/path/to/monitor")).equals("thename");
    } finally {
      FILE_UTILS.deleteQuietly(tempFile);
    }
  }
  
  @Test(groups = "unit")
  public void testFileNotSet() {
    final MonitorConfigImpl configDirNull = new MonitorConfigImpl();
    configDirNull.setConfigFile(null);
    assert Iterables.isEmpty(configDirNull.getDirectories());
    
    final MonitorConfigImpl configDirEmpty = new MonitorConfigImpl();
    configDirEmpty.setConfigFile("");
    assert Iterables.isEmpty(configDirEmpty.getDirectories());

    final MonitorConfigImpl configUnsetProperty = new MonitorConfigImpl();
    configUnsetProperty.setConfigFile("${config.dir}");
    assert Iterables.isEmpty(configUnsetProperty.getDirectories());

    final MonitorConfigImpl configNoSuchFile = new MonitorConfigImpl();
    configNoSuchFile.setConfigFile("/moo/cow/2.xml");
    assert Iterables.isEmpty(configNoSuchFile.getDirectories());    
  }  
}
