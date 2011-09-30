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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.common.base.Supplier;

@Deprecated
public class ZipFileCollection extends FileCollection {
  private ArrayList<String> zipEntryNames;
  private static IOUtils ioUtils = IOUtilsFactory.getInstance();

  public ZipFileCollection(final File zipFile) {
    this(zipFile, TempFileSuppliers.getDefaultTempFileSupplier());
  }

  public ZipFileCollection(final File zipFile, final Supplier<File> fileSupplier) {
    try {
      final ZipFile zip = new ZipFile(zipFile);
      final int numZippedFiles = zip.size();
      final ArrayList<File> files = new ArrayList<File>(numZippedFiles);
      this.zipEntryNames = new ArrayList<String>(numZippedFiles);

      final Enumeration<? extends ZipEntry> enteries = zip.entries();
      for(int i = 0; i < numZippedFiles; i++) {

        // Get InputStream for zipped entry
        final ZipEntry entry = enteries.nextElement();
        if(entry.isDirectory()) {
          continue;
        }

        final InputStream inputStream = zip.getInputStream(entry);
        // Get OutputStream for destination file
        final File unzippedFile = fileSupplier.get();
        final OutputStream unzippedFileOutputStream = new FileOutputStream(unzippedFile);

        // Copy
        ZipFileCollection.ioUtils.copy(inputStream, unzippedFileOutputStream);

        ZipFileCollection.ioUtils.closeQuietly(unzippedFileOutputStream);
        ZipFileCollection.ioUtils.closeQuietly(inputStream);
        files.add(unzippedFile);
        this.zipEntryNames.add(entry.getName());
      }
      zip.close();
      this.setFiles(files.toArray(new File[0]));
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    }
  }

  public String[] getZipEntryNames() {
    return this.zipEntryNames.toArray(new String[0]);
  }

}
