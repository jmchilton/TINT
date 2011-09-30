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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.StagingDirectory;
import edu.umn.msi.tropix.common.io.TempDirectoryCreator;
import edu.umn.msi.tropix.common.io.impl.StagingDirectorySupplierImpl;

public class StagingDirectoryTest {
  private static FileUtils fileUtils = FileUtilsFactory.getInstance();

  @Test(groups = "unit")
  public void contexts() {
    final StagingDirectorySupplierImpl supplier = new StagingDirectorySupplierImpl();
    final TempDirectoryCreator creator = EasyMock.createMock(TempDirectoryCreator.class);
    supplier.setTempDirectoryCreator(creator);
    final File tempDirectory = fileUtils.createTempDirectory();
    EasyMock.expect(creator.getNewTempDirectory()).andReturn(tempDirectory);
    EasyMock.replay(creator);
    final StagingDirectory dir = supplier.get(tempDirectory.getAbsolutePath());
    dir.setup();
    dir.getOutputContext("moo").put("Moo Cow".getBytes());
    assert fileUtils.readFileToString(new File(tempDirectory, "moo")).equals("Moo Cow");

    fileUtils.writeStringToFile(new File(tempDirectory, "cow"), "Cow Moo");
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    dir.getInputContext("cow").get(outputStream);
    assert "Cow Moo".equals(new String(outputStream.toByteArray()));

    assert Iterables.contains(dir.getResourceNames(""), "cow");

    assert dir.getSep().equals(File.separator);

    dir.makeDirectory("subdir");
    assert new File(tempDirectory, "subdir").isDirectory();
  }

  @Test(groups = "unit")
  public void stagingDirectory() throws IOException {
    final StagingDirectorySupplierImpl supplier = new StagingDirectorySupplierImpl();
    final TempDirectoryCreator creator = EasyMock.createMock(TempDirectoryCreator.class);
    supplier.setTempDirectoryCreator(creator);
    EasyMock.replay(creator);
    final File tempDirectory = fileUtils.createTempDirectory();
    final StagingDirectory dir = supplier.get(tempDirectory.getAbsolutePath());
    dir.setup();
    assert dir.getAbsolutePath().equals(tempDirectory.getAbsolutePath());
    EasyMock.verify(creator);
  }

  @Test(groups = "unit")
  public void beanOps() throws IOException {
    final StagingDirectorySupplierImpl supplier = new StagingDirectorySupplierImpl();
    supplier.setDeleteStagedFiles(true);
    assert supplier.getDeleteStagedFiles();
    supplier.setDeleteStagedFiles(false);
    assert !supplier.getDeleteStagedFiles();

    assert supplier.getTempDirectoryCreator() != null;
    supplier.setTempDirectoryCreator(null);
    assert supplier.getTempDirectoryCreator() == null;
  }

  @Test(groups = {"unit"})
  public void deleteStagedFiles() throws IOException {
    final StagingDirectorySupplierImpl supplier = new StagingDirectorySupplierImpl();
    final TempDirectoryCreator creator = EasyMock.createMock(TempDirectoryCreator.class);
    final File dir = new File("testdir");
    EasyMock.expect(creator.getNewTempDirectory()).andReturn(dir);

    final StagingDirectory directory1 = supplier.get(), directory2 = supplier.get();

    directory1.setup();
    directory2.setup();

    assert !directory1.getAbsolutePath().equals(directory2.getAbsolutePath());
    assert new File(directory1.getAbsolutePath()).isDirectory();
    assert new File(directory2.getAbsolutePath()).isDirectory();

    final File file1 = new File(directory1.getAbsolutePath(), "testfile");
    final File file2 = new File(directory2.getAbsolutePath(), "testfile");

    fileUtils.touch(file1);
    fileUtils.touch(file2);

    supplier.setDeleteStagedFiles(true);
    directory1.cleanUp();
    assert !new File(directory1.getAbsolutePath()).exists();
    assert !file1.exists();

    supplier.setDeleteStagedFiles(false);
    directory2.cleanUp();
    assert new File(directory2.getAbsolutePath()).exists();
    assert file2.exists();

    fileUtils.deleteDirectory(new File(directory2.getAbsolutePath()));
  }

  @Test(groups = "unit")
  public void setPath() throws IOException {
    final StagingDirectorySupplierImpl supplier = new StagingDirectorySupplierImpl();
    final File dir = fileUtils.createTempDirectory();
    try {
      supplier.setTempDirectoryPath(dir.getAbsolutePath());
      final StagingDirectory stagingDirectory = supplier.get();
      stagingDirectory.setup();
      assert new File(stagingDirectory.getAbsolutePath()).getParentFile().equals(dir);
    } finally {
      fileUtils.deleteDirectory(dir);
    }
  }

  @Test(groups = {"unit"})
  public void testTempDirectoryCreator() throws IOException {
    final TempDirectoryCreator creator = EasyMock.createMock(TempDirectoryCreator.class);
    final File dir = new File("testdir");
    EasyMock.expect(creator.getNewTempDirectory()).andReturn(dir);
    EasyMock.replay(creator);
    final StagingDirectorySupplierImpl supplier = new StagingDirectorySupplierImpl();
    supplier.setTempDirectoryCreator(creator);
    final StagingDirectory stagingDirectory = supplier.get();
    stagingDirectory.setup();
    assert stagingDirectory.getAbsolutePath().equals(dir.getAbsolutePath()) : "Expected [" + dir.getAbsolutePath() + "] obtained " + stagingDirectory.getAbsolutePath();
    EasyMock.verify(creator);
  }

}
