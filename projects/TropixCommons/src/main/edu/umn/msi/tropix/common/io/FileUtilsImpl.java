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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.annotation.WillClose;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;


class FileUtilsImpl implements edu.umn.msi.tropix.common.io.FileUtils {

  public byte[] readFileToByteArray(final String path) {
    return this.readFileToByteArray(new File(path));
  }

  public byte[] readFileToByteArray(final File file) {
    try {
      return FileUtils.readFileToByteArray(file);
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    }
  }

  public void writeStringToFile(final File file, final String data) {
    try {
      FileUtils.writeStringToFile(file, data);
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    }
  }

  public void writeStringToFile(final String path, final String data) {
    this.writeStringToFile(new File(path), data);
  }

  public Collection<File> listFiles(final File directory, final String[] extensions, final boolean recursive) {
    @SuppressWarnings("unchecked")
    final Collection<File> results = FileUtils.listFiles(directory, extensions, recursive);
    return results;
  }

  public Collection<File> listFiles(final String directoryPath, final String[] extensions, final boolean recursive) {
    return this.listFiles(new File(directoryPath), extensions, recursive);
  }

  public Collection<File> listFiles(final String directoryPath) {
    return this.listFiles(new File(directoryPath));
  }

  public Collection<File> listFiles(final File directory) {
    return this.listFiles(directory, null, false);
  }

  public Iterator<File> iterateFiles(final File directory, final String[] extensions, final boolean recursive) {
    @SuppressWarnings("unchecked")
    final Iterator<File> fileIterator = FileUtils.iterateFiles(directory, extensions, recursive);
    return fileIterator;
  }

  public void touch(final File file) {
    try {
      FileUtils.touch(file);
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    }
  }

  public void touch(final String path) {
    this.touch(new File(path));
  }

  public boolean mkdir(final File file) {
    return file.mkdir();
  }

  public boolean mkdir(final String path) {
    return this.mkdir(new File(path));
  }

  public boolean mkdirs(final File file) {
    return file.mkdirs();
  }

  public boolean mkdirs(final String path) {
    return this.mkdirs(new File(path));
  }
  
  public void writeStreamToFile(final String path, @WillClose final InputStream inputStream) {
    writeStreamToFile(new File(path), inputStream);
  }

  public void writeStreamToFile(final File file, @WillClose final InputStream inputStream) {
    final FileOutputStream outputStream = getFileOutputStream(file);
    try {
      IOUtils.copyLarge(inputStream, outputStream);
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    } finally {
      IOUtils.closeQuietly(outputStream);
      IOUtils.closeQuietly(inputStream);
    }
  }
  
  public void writeByteArrayToFile(final File file, final byte[] contents) {
    try {
      FileUtils.writeByteArrayToFile(file, contents);
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    }
  }

  public void writeByteArrayToFile(final String path, final byte[] contents) {
    this.writeByteArrayToFile(new File(path), contents);
  }

  public void deleteDirectory(final File directory) {
    try {
      FileUtils.deleteDirectory(directory);
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    }
  }

  public void deleteDirectory(final String directoryPath) {
    this.deleteDirectory(new File(directoryPath));
  }

  public boolean deleteDirectoryQuietly(@Nullable final File file) {
    try {
      FileUtils.deleteDirectory(file);
      return true;
    } catch(final Exception e) {
      return false;
    }
  }

  public boolean deleteDirectoryQuietly(@Nullable final String path) {
    final File file = getFile(path);
    return this.deleteDirectoryQuietly(file);
  }

  @Nullable
  private File getFile(@Nullable final String path) {
    try {
      return new File(path);
    } catch(final RuntimeException e) {
      return null;
    }
  }

  public boolean deleteQuietly(@Nullable final File file) {
    try {
      if(file != null) {
        return file.delete();
      } else {
        return false;
      }
    } catch(final Exception e) {
      return false;
    }
  }

  public boolean deleteQuietly(@Nullable final String path) {
    final File file = getFile(path);
    return this.deleteQuietly(file);
  }

  public FileInputStream getFileInputStream(final File file) {
    try {
      return new FileInputStream(file);
    } catch(final FileNotFoundException e) {
      throw new IORuntimeException(e);
    }
  }

  public FileInputStream getFileInputStream(final String path) {
    return this.getFileInputStream(new File(path));
  }

  public FileReader getFileReader(final File file) {
    try {
      return new FileReader(file);
    } catch(final FileNotFoundException e) {
      throw new IORuntimeException(e);
    }
  }

  public FileReader getFileReader(final String path) {
    return this.getFileReader(new File(path));
  }

  public FileOutputStream getFileOutputStream(final File file) {
    try {
      return new FileOutputStream(file);
    } catch(final FileNotFoundException e) {
      throw new IORuntimeException(e);
    }
  }

  public FileOutputStream getFileOutputStream(final String path) {
    return this.getFileOutputStream(new File(path));
  }

  public FileWriter getFileWriter(final File file) {
    try {
      return new FileWriter(file);
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    }
  }

  public FileWriter getFileWriter(final String path) {
    return this.getFileWriter(new File(path));
  }

  public File createTempFile(final String prefix, final String suffix) {
    try {
      return File.createTempFile(prefix, suffix);
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    }
  }

  public String readFileToString(final File file) {
    try {
      return FileUtils.readFileToString(file);
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    }
  }

  public String readFileToString(final String path) {
    return this.readFileToString(new File(path));
  }

  public void moveFile(final File sourceFile, final File destFile) {
    try {
      FileUtils.moveFile(sourceFile, destFile);
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    }
  }

  public File createTempDirectory() {
    @SuppressWarnings("serial")
    final File tempDir = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString()) {
      @Override
      public void finalize() throws Throwable {
        FileUtilsImpl.this.deleteDirectoryQuietly(this);
        super.finalize();
      }
    };
    this.mkdirs(tempDir);
    return tempDir;
  }

  public void copyFile(final File fromFile, final File toFile) {
    try {
      FileUtils.copyFile(fromFile, toFile);
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    }
  }

  public File createTempFile() {
    return createTempFile("tpx", "");
  }

}
