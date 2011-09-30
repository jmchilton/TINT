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

package edu.umn.msi.tropix.common.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.testng.annotations.Test;

import com.google.common.base.Function;

import edu.umn.msi.tropix.common.test.CommonTests;
import edu.umn.msi.tropix.common.test.IOTest;
import edu.umn.msi.tropix.common.test.TestNGDataProviders;

public class ZipUtilsImplTest extends IOTest {
  private static final ZipUtils ZIP_UTILS = ZipUtilsFactory.getInstance();
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();

  private void verifyZipEntry(final ZipFile zipFile, final ZipEntry zipEntry) throws IOException {
    if(zipEntry.getName().equals("a.txt")) {
      assert "a contents".equals(IO_UTILS.toString(zipFile.getInputStream(zipEntry)));
    } else if(zipEntry.getName().equals("b.png")) {
      assert "b contents".equals(IO_UTILS.toString(zipFile.getInputStream(zipEntry)));
    } else {
      assert false : "Unexpected zip entry " + zipEntry.getName();
    }
  }

  @Test(groups = "unit", dataProvider = "bool1", dataProviderClass = TestNGDataProviders.class)
  public void testZipDirectory(final boolean specifySubDir) throws ZipException, IOException {
    final File parentDir = getTempDirectory();
    File tempDir = parentDir;
    if(specifySubDir) {
      tempDir = new File(parentDir, "sub");
    }
    final File tempZipFile = getTempFile();
    FILE_UTILS.writeStringToFile(new File(tempDir, "a.txt"), "a contents");
    FILE_UTILS.writeStringToFile(new File(tempDir, "b.png"), "b contents");

    final Directory directory = Directories.fromFile(parentDir);

    final FileOutputStream outputStream = FILE_UTILS.getFileOutputStream(tempZipFile);
    registerCloseable(outputStream);
    ZIP_UTILS.zipDirectory(directory, specifySubDir ? "sub" : null, outputStream);

    final ZipFile zipFile = new ZipFile(tempZipFile);
    final Enumeration<? extends ZipEntry> entriesEnum = zipFile.entries();
    verifyZipEntry(zipFile, entriesEnum.nextElement());
    verifyZipEntry(zipFile, entriesEnum.nextElement());
    assert !entriesEnum.hasMoreElements();
  }

  @Test(groups = "unit")
  public void unzip() throws IOException {
    final File tempDir = getTempDirectory();
    final Function<String, File> nameFunction = new Function<String, File>() {
      public File apply(final String name) {
        return new File(tempDir, name);
      }
    };

    final File zipFile = new File(tempDir, "temp.zip");
    final InputStream stream = CommonTests.class.getResourceAsStream("hello.zip");
    FILE_UTILS.writeByteArrayToFile(zipFile, IO_UTILS.toByteArray(stream));
    ZIP_UTILS.unzip(zipFile, nameFunction);

    assert new File(tempDir, "hello1").exists();
    assert FILE_UTILS.readFileToString(new File(tempDir, "hello1")).equals("Hello World!");
    assert new File(tempDir, "hello2").exists();
  }

  @Test(groups = "unit")
  public void zipToStream() throws IOException {
    this.zipToStream(false);
  }

  public void zipToStream(final boolean closeException) throws IOException {
    final ZipUtilsImpl utils = new ZipUtilsImpl();
    final List<String> names = Arrays.asList("a.txt", "b.png");
    final InputStream inputStream = new ByteArrayInputStream("a contents".getBytes()) {
      public void close() throws IOException {
        if(closeException) {
          throw new IOException();
        }
      }
    };
    final List<InputStream> streams = Arrays.<InputStream>asList(inputStream, new ByteArrayInputStream("b contents".getBytes()));
    final File file = File.createTempFile("tpx", ".zip");

    final OutputStream outputStream = new FileOutputStream(file);
    utils.zipToStream(streams, names, outputStream);
    final ZipFile zipFile = new ZipFile(file);
    final Enumeration<? extends ZipEntry> entriesEnum = zipFile.entries();
    ZipEntry entry = entriesEnum.nextElement();
    verifyZipEntry(zipFile, entry);
    entry = entriesEnum.nextElement();
    verifyZipEntry(zipFile, entry);
    assert !entriesEnum.hasMoreElements();
  }

  @Test(groups = "unit")
  public void testUnzipToTempDirectory() {
    final InputStream zippedStream = CommonTests.class.getResourceAsStream("hello.zip");
    final File tempDirectory = ZIP_UTILS.unzipToTempDirectory(zippedStream);
    try {
      final File unzippedFile = new File(tempDirectory, "hello1");
      assert unzippedFile.exists();
      assert FILE_UTILS.readFileToString(unzippedFile).equals("Hello World!");
    } finally {
      FILE_UTILS.deleteDirectoryQuietly(tempDirectory);
    }
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void testUnzipToTempDirectoryInvalid() {
    final InputStream invalidZipStream = new ByteArrayInputStream("foo".getBytes());
    ZIP_UTILS.unzipToTempDirectory(invalidZipStream);
  }

}
