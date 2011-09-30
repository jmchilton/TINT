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

package edu.umn.msi.tropix.grid.io.impl;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.StagingDirectory;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.grid.io.CredentialedStagingDirectoryFactory;

@ContextConfiguration("testSystemStagingDirectory.xml")
public class SystemStagingDirectoryTest extends AbstractTestNGSpringContextTests {
  @Autowired
  private CredentialedStagingDirectoryFactory directoryFactory;

  @Test(groups = "unit")
  public void javaStagingDirectoryFactory() {
    final StagingDirectory directory = directoryFactory.get(Credentials.getMock());
    try {
      directory.setup();
      final StagingDirectory directory2 = directoryFactory.get(Credentials.getMock(), directory.getAbsolutePath());
      assert directory.getAbsolutePath().equals(directory2.getAbsolutePath()) : directory2.getAbsolutePath();
      assert new File(directory.getAbsolutePath()).exists();
    } finally {
      FileUtilsFactory.getInstance().deleteDirectory(directory.getAbsolutePath());
    }
  }

}
