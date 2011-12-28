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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import com.google.common.base.Supplier;

/**
 * Abstract base class for creating {@link OutputContext} objects. Concrete 
 * subclasses need only implement {@link #put(InputStream, long)}.
 * 
 * @author John Chilton
 *
 */
public abstract class SizedStreamOutputContextImpl implements OutputContext {
  private static final Supplier<File> TEMP_FILE_SUPPLIER = TempFileSuppliers.getDefaultTempFileSupplier();
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();

  public void put(final File file) {
    final InputStream inputStream = FILE_UTILS.getFileInputStream(file);
    try {
      put(inputStream, file.length());
    } finally {
      IO_UTILS.closeQuietly(inputStream);
    }
  }

  public void put(final URL url) {
    InputStream urlInputStream = null;
    try {
      urlInputStream = url.openStream();
      put(urlInputStream);
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    } finally {
      IO_UTILS.closeQuietly(urlInputStream);
    }
  }

  public void put(final byte[] bytes) {
    put(new ByteArrayInputStream(bytes), bytes.length);
  }

  protected abstract void put(final InputStream inputStream, long length);

  public void put(final InputStream inputStream) {
    final File tempFile = TEMP_FILE_SUPPLIER.get();
    OutputStream outputStream = null;
    try {
      outputStream = FILE_UTILS.getFileOutputStream(tempFile);
      IO_UTILS.copyLarge(inputStream, outputStream);
      put(tempFile);
    } finally {
      FILE_UTILS.deleteQuietly(tempFile);
      IO_UTILS.closeQuietly(outputStream);
    }
  }

}
