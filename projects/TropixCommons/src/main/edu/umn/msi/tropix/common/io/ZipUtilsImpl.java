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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.annotation.Nullable;
import javax.annotation.WillClose;
import javax.annotation.WillNotClose;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

class ZipUtilsImpl implements ZipUtils {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();

  public void zipContextsToStream(final Iterable<? extends InputContext> inputContexts, final Iterable<String> names, final OutputStream outputStream) {
    final ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
    ZipEntry zipEntry;

    final Iterator<? extends InputContext> inputContextIterator = inputContexts.iterator();
    final Iterator<String> nameIterator = names.iterator();
    try {
      while(inputContextIterator.hasNext()) {
        final InputContext inputContext = inputContextIterator.next();
        final String name = nameIterator.next();
        zipEntry = new ZipEntry(name);
        zipOutputStream.putNextEntry(zipEntry);
        inputContext.get(zipOutputStream);
        zipOutputStream.closeEntry();
      }
      zipOutputStream.finish();
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    }
  }

  // Some day might want to descend into subdirectories
  private Iterable<String> getFileNames(final Directory directory, final String resourcePath) {
    return directory.getResourceNames(resourcePath);
  }

  public void zipDirectory(final Directory directory, @Nullable final String resourcePath, @WillNotClose final OutputStream outputStream) {
    try {
      final Iterable<String> fileNames = getFileNames(directory, resourcePath);
      final String prefix = resourcePath == null ? "" : (resourcePath + directory.getSep());
      final Iterable<InputContext> fileContents = Iterables.transform(fileNames, new Function<String, InputContext>() {
        public InputContext apply(final String fileName) {
          return directory.getInputContext(prefix + fileName);
        }
      });
      zipContextsToStream(fileContents, fileNames, outputStream);
    } finally {
      IO_UTILS.closeQuietly(outputStream);
    }
  }

  public void zipToStream(final Iterable<InputStream> inputStreams, final Iterable<String> names, final OutputStream outputStream) {
    this.zipContextsToStream(Iterables.transform(inputStreams, InputContexts.getForStreamFunction()), names, outputStream);
  }

  public void unzipToContexts(final File zippedFile, final Function<? super String, OutputContext> outputContextFunction) {
    try {
      final ZipFile zip = new ZipFile(zippedFile);
      final Enumeration<? extends ZipEntry> zipEntries = zip.entries();
      ZipEntry entry;
      while(zipEntries.hasMoreElements()) {
        entry = zipEntries.nextElement();
        if(entry.isDirectory()) {
          continue;
        }
        final OutputContext outputContext = outputContextFunction.apply(entry.getName());
        final InputStream inputStream = zip.getInputStream(entry);
        outputContext.put(inputStream);
      }
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    }
  }

  public void unzip(final File zippedFile, final Function<? super String, File> outputFileFunction) {
    this.unzipToContexts(zippedFile, new Function<String, OutputContext>() {
      public OutputContext apply(final String entryName) {
        return OutputContexts.forFile(outputFileFunction.apply(entryName));
      }
    });
  }

  public void unzipToDirectory(final File zipFile, final File directory) {
    unzip(zipFile, new Function<String, File>() {
      public File apply(final String fileName) {
        final File newFile = new File(directory, fileName);
        newFile.getParentFile().mkdirs();
        return newFile;
      }
    });
  }

  public File unzipToTempDirectory(final File zipFile) {
    final File tempDirectory = FILE_UTILS.createTempDirectory();
    try {
      unzipToDirectory(zipFile, tempDirectory);
    } catch(final RuntimeException e) {
      FILE_UTILS.deleteQuietly(tempDirectory);
      throw e;
    }
    return tempDirectory;
  }

  public File unzipToTempDirectory(@WillClose final InputStream inputStream) {
    final File tempFile = FILE_UTILS.createTempFile();
    OutputStream fileOutputStream = null;
    try {
      fileOutputStream = FILE_UTILS.getFileOutputStream(tempFile);
      IO_UTILS.copy(inputStream, fileOutputStream);
      return unzipToTempDirectory(tempFile);
    } finally {
      IO_UTILS.closeQuietly(fileOutputStream);
      IO_UTILS.closeQuietly(inputStream);
      FILE_UTILS.deleteQuietly(tempFile);
    }
  }

}
