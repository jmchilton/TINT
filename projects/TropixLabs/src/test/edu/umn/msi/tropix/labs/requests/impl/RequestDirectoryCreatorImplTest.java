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

package edu.umn.msi.tropix.labs.requests.impl;

import java.io.File;
import java.util.UUID;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;

public class RequestDirectoryCreatorImplTest {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();

  /**
   * Tests that the one method on the interface works properly.
   */
  @Test(groups = "unit")
  public void create() {
    final RequestDirectoryCreatorImpl creator = new RequestDirectoryCreatorImpl();
    final File tempDir = FILE_UTILS.createTempDirectory();
    try {
      creator.setBaseDirectory(tempDir);
      final String id = UUID.randomUUID().toString();
      creator.createDirectory("catalog:2", id);
      final File createdFile = new File(new File(tempDir, "catalog:2"), id);
      assert createdFile.exists();
      assert createdFile.isDirectory();
    } finally {
      FILE_UTILS.deleteDirectoryQuietly(tempDir);
    }
  }

  /**
   * Tests that the one method on the interface doeesn't throw exception.
   */
  @Test(groups = "unit")
  public void createProblems() {
    final RequestDirectoryCreatorImpl creator = new RequestDirectoryCreatorImpl();
    final File basedir = new File(File.separator + "some junk");
    creator.setBaseDirectory(basedir);
    final String id = UUID.randomUUID().toString();
    creator.createDirectory("catalog:2", id);
    assert !basedir.exists();
  }
}
