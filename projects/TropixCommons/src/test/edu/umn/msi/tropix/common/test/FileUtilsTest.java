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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IORuntimeException;

public class FileUtilsTest {
  private FileUtils fileUtils = FileUtilsFactory.getInstance();
  private File file;

  @BeforeMethod(groups = "unit")
  public void init() throws IOException {
    this.file = File.createTempFile("moo", "cow");
  }

  @AfterMethod(groups = "unit")
  public void clean() throws IOException {
    if(file.exists()) {
      if(file.isFile()) {
        assert file.delete();
      } else if(file.isDirectory()) {
        org.apache.commons.io.FileUtils.deleteDirectory(file);
      }
    }
  }

  @Test(groups = "unit")
  public void deleteQuitelyNull() {
    assert !fileUtils.deleteDirectoryQuietly((File) null);
    assert !fileUtils.deleteDirectoryQuietly((String) null);
  }

  /*
   * @Test(groups = "unit", expectedExceptions = IORuntimeException.class) public void deleteDirectoryException() { fileUtils.deleteDirectory(new File("/1231u4qweoirweuir02342")); }
   */

  @Test(groups = "unit")
  public void deleteQuietlyFalse() {
    assert file.delete();
    assert file.mkdir();
    fileUtils.touch(new File(file, "moo"));
    assert !fileUtils.deleteQuietly(file);
  }

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void createException() {
    fileUtils.createTempFile("\\/moocow", "");
  }

  @Test(groups = "unit")
  public void listFilesPath() {
    assert file.delete();
    assert file.mkdir();
    assert fileUtils.listFiles(file.getAbsolutePath()).isEmpty();
  }

  @Test(groups = "unit")
  public void iterateFiles() {
    assert file.delete();
    assert file.mkdir();
    final File file1 = new File(file, "one");
    fileUtils.touch(file1);
    fileUtils.iterateFiles(file, null, true).next().equals(file1);
  }

  @Test(groups = "unit")
  public void copy() {
    fileUtils.writeStringToFile(file, "Moo Cow");
    final File toFile = fileUtils.createTempFile("tpxtst", "");
    try {
      fileUtils.copyFile(file, toFile);
      assert fileUtils.readFileToString(toFile).equals("Moo Cow");
    } finally {
      assert toFile.delete();
    }
  }

  @Test(groups = "unit")
  public void move() {
    fileUtils.writeStringToFile(file, "Moo Cow");
    final File toFile = fileUtils.createTempFile("tpxtst", "");
    assert toFile.delete();
    try {
      fileUtils.moveFile(file, toFile);
      assert !file.exists();
      assert fileUtils.readFileToString(toFile).equals("Moo Cow");
    } finally {
      assert toFile.delete();
    }
  }

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void moveException() {
    fileUtils.writeStringToFile(file, "Moo Cow");
    final File toFile = fileUtils.createTempFile("tpxtst", "");
    assert toFile.delete();
    assert toFile.mkdir();
    try {
      // Shouldn't be able to copy to result, its a directoory
      fileUtils.moveFile(file, toFile);
    } finally {
      fileUtils.deleteDirectoryQuietly(toFile);
    }
  }

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void copyException() {
    fileUtils.writeStringToFile(file, "Moo Cow");
    final File toFile = fileUtils.createTempFile("tpxtst", "");
    assert toFile.delete();
    assert toFile.mkdir();
    try {
      // Shouldn't be able to copy to result, its a directoory
      fileUtils.copyFile(file, toFile);
    } finally {
      fileUtils.deleteDirectoryQuietly(toFile);
    }
  }

  @Test(groups = "unit")
  public void mkdir() {
    assert file.delete();
    assert fileUtils.mkdir(this.file.getAbsolutePath());
    assert file.isDirectory();
  }

  @Test(groups = "unit")
  public void mkdirs() {
    assert file.delete();
    final File nFile = new File(this.file, "moo");
    assert this.fileUtils.mkdirs(nFile.getAbsolutePath());
    assert this.file.isDirectory();
  }

  @Test(groups = "unit")
  public void delete() throws IOException {
    deleteDir(false);
  }

  @Test(groups = "unit")
  public void deleteDirQuietly() throws IOException {
    deleteDir(true);
  }

  private void deleteDir(final boolean quietly) throws IOException {
    assert file.delete();
    assert file.mkdir();
    if(quietly) {
      this.fileUtils.deleteDirectory(this.file.getAbsolutePath());
    } else {
      this.fileUtils.deleteDirectoryQuietly(this.file.getAbsolutePath());
    }
    assert !this.file.exists();
  }

  @Test(groups = "unit")
  public void deleteQuietly() throws IOException {
    assert this.file.exists();
    this.fileUtils.deleteQuietly(this.file.getAbsolutePath());
    assert !this.file.exists();
  }

  @Test(groups = "unit")
  public void touch() throws IOException {
    assert file.delete();
    assert !file.exists();
    this.fileUtils.touch(this.file.getAbsolutePath());
    assert file.exists();
  }

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void touchFailure() {
    assert file.delete();
    assert file.mkdir();
    this.fileUtils.touch("/moo");
  }

  @Test(groups = "unit")
  public void writeStringToFile() throws IOException {
    this.fileUtils.writeStringToFile(this.file.getAbsolutePath(), "moocow");
    final FileInputStream fis = new FileInputStream(this.file);
    try {
      final byte[] bytes = new byte[6];
      assert 6 == fis.read(bytes);
      assert "moocow".equals(new String(bytes));
    } finally {
      fis.close();
    }
  }

  @Test(groups = "unit")
  public void writeByteArrayToFile() throws IOException {
    this.fileUtils.writeByteArrayToFile(this.file.getAbsolutePath(), "moocow".getBytes());
    final FileInputStream fis = new FileInputStream(this.file);
    try {
      final byte[] bytes = new byte[6];
      assert 6 == fis.read(bytes);
      assert "moocow".equals(new String(bytes));
    } finally {
      fis.close();
    }
  }

  @Test(groups = "unit")
  public void listFiles() {
    assert this.file.delete();
    assert this.file.mkdir();
    this.fileUtils.touch(new File(this.file, "moo.1"));
    this.fileUtils.touch(new File(this.file, "cow.1"));
    this.fileUtils.touch(new File(this.file, "moo.2"));
    final Collection<File> files = this.fileUtils.listFiles(this.file.getAbsolutePath(), new String[] {"1"}, false);
    assert files.size() == 2;
  }

  @Test(groups = "unit")
  public void getInputStream() throws IOException {
    final FileOutputStream fos = new FileOutputStream(this.file);
    try {
      fos.write("Hello".getBytes());      
    } finally {
      fos.close();
    }
    final FileInputStream fis = this.fileUtils.getFileInputStream(this.file.getAbsolutePath());
    final byte[] bytes = new byte[5];
    assert 5 == fis.read(bytes);
    assert "Hello".equals(new String(bytes));
  }

  @Test(groups = "unit")
  public void getFileReader() throws IOException {
    final FileOutputStream fos = new FileOutputStream(this.file);
    try {
      fos.write("Hello".getBytes());
    } finally {
      fos.close();
    }
    final FileReader fr = this.fileUtils.getFileReader(this.file.getAbsolutePath());
    final char[] chars = new char[5];
    assert 5 == fr.read(chars);
    assert "Hello".equals(new String(chars));
  }

  @Test(groups = "unit")
  public void getFileWriter() throws IOException {
    final FileWriter fileWriter = this.fileUtils.getFileWriter(this.file.getAbsolutePath());
    try {
      fileWriter.write("moocow");
    } finally {
      fileWriter.close();
    }
    assert "moocow".equals(this.fileUtils.readFileToString(this.file.getAbsolutePath()));
  }

  @Test(groups = "unit")
  public void getFileOutputStream() throws IOException {
    final FileOutputStream fos = this.fileUtils.getFileOutputStream(this.file.getAbsolutePath());
    try {
      fos.write("moo".getBytes());
    } finally {
      fos.close();
    }
    assert "moo".equals(this.fileUtils.readFileToString(this.file.getAbsolutePath()));
  }

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void getFileReaderException() throws IOException {
    assert this.file.delete();
    this.fileUtils.getFileReader(this.file.getAbsolutePath());
  }

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void getFileInputStreamrException() throws IOException {
    assert this.file.delete();
    this.fileUtils.getFileInputStream(this.file.getAbsolutePath());
  }

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void getFileToStringException() throws IOException {
    assert this.file.delete();
    this.fileUtils.readFileToString(this.file.getAbsolutePath());
  }

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void readStringException() {
    assert this.file.delete();
    this.fileUtils.readFileToString(this.file.getAbsolutePath());
  }

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void readByteArrayException() {
    assert this.file.delete();
    this.fileUtils.readFileToByteArray(this.file.getAbsolutePath());
  }

  @Test(groups = "unit")
  public void readString() throws IOException {
    final FileOutputStream fos = new FileOutputStream(this.file);
    try { 
      fos.write("Moo".getBytes());
    } finally {
      fos.close();
    }
    assert "Moo".equals(this.fileUtils.readFileToString(this.file.getAbsolutePath()));
  }

  @Test(groups = "unit")
  public void readByteArray() throws IOException {
    final FileOutputStream fos = new FileOutputStream(this.file);
    try {
      fos.write("Moo".getBytes());
    } finally {
      fos.close();
    }
    assert "Moo".equals(new String(this.fileUtils.readFileToByteArray(this.file.getAbsolutePath())));
  }

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void getFileInputStreamException() throws IOException {
    assert this.file.delete();
    this.fileUtils.getFileInputStream(this.file.getAbsolutePath());
  }

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void writeBytesException() {
    assert file.delete();
    assert file.mkdir();
    this.fileUtils.writeByteArrayToFile(file.getAbsolutePath(), "moo".getBytes());
  }

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void writeStringException() {
    assert file.delete();
    assert file.mkdir();
    this.fileUtils.writeStringToFile(file.getAbsolutePath(), "moo");
  }

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void getFileWriterException() {
    assert file.delete();
    assert file.mkdir();
    this.fileUtils.getFileWriter(file.getAbsolutePath());
  }

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void getFileOutputStreamException() {
    assert file.delete();
    assert file.mkdir();
    this.fileUtils.getFileOutputStream(file.getAbsolutePath());
  }

  @Test()
  public void forEachFile() throws IOException {
    final File directory = fileUtils.createTempDirectory();
    try {
      this.fileUtils.iterateFiles(directory, null, true);
    } finally {
      org.apache.commons.io.FileUtils.deleteDirectory(directory);
    }
  }

}
