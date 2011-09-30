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

package edu.umn.msi.tropix.common.test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.TempDirectoryCreator;
import edu.umn.msi.tropix.common.io.TempDirectoryCreatorImpl;

class Foo {
}

public class TempDirectoryCreatorTest {
  @Test(groups = "unit")
  public void toStringTest() throws IOException {
    final String path = System.getProperty("java.io.tmpdir") + File.separator + "MooCow" + UUID.randomUUID().toString();
    try {
      final TempDirectoryCreatorImpl creator = new TempDirectoryCreatorImpl(path);
      assert creator.toString().contains("MooCow");
    } finally {
      FileUtils.deleteDirectory(new File(path));
    }
  }

  @Test(groups = "linux", expectedExceptions = RuntimeException.class)
  public void initialiFailure() {
    new TempDirectoryCreatorImpl("/moo");
  }

  @Test(groups = "linux")
  public void createFailure() {
    final TempDirectoryCreatorImpl creator = new TempDirectoryCreatorImpl("/");
    RuntimeException re = null;
    try {
      creator.getNewTempDirectory();
    } catch(final RuntimeException e) {
      re = e;
    }
    assert re != null;
  }

  @Test(groups = "unit")
  public void classConstructor() throws IOException {
    final TempDirectoryCreatorImpl creator = new TempDirectoryCreatorImpl(new Foo());
    final File newTempDir = creator.getNewTempDirectory();
    try {
      assert newTempDir.getAbsolutePath().contains("Foo");
    } finally {
      FileUtils.deleteDirectory(newTempDir.getParentFile());
    }
  }

  @Test(groups = {"unit"})
  public void testTempDirectoryCreatorImpl() throws Exception {
    final File systemTempDirectory = File.createTempFile("djfa", "").getParentFile();
    final File baseTempDirectory = new File(systemTempDirectory, "unittest");
    try {
      final TempDirectoryCreatorImpl creator = new TempDirectoryCreatorImpl(baseTempDirectory);
      this.testTempDirectoryCreator(creator);
    } finally {
      FileUtils.deleteDirectory(baseTempDirectory);
    }
  }

  public void testTempDirectoryCreator(final TempDirectoryCreator creator) throws Exception {
    final Collection<File> tempDirectories = new LinkedList<File>();
    for(int i = 0; i < 10; i++) {
      final File tempDirectory = creator.getNewTempDirectory();
      assert !tempDirectories.contains(tempDirectory) : "Non unique temp directory created";
      tempDirectories.add(tempDirectory);
      assert tempDirectory.isDirectory() : "Temp directory is not a directory.";
      assert tempDirectory.canWrite() : "Cant write to temp directory.";
      assert tempDirectory.list().length == 0 : "New directory not empty";
    }
  }

}
