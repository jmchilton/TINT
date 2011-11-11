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

package edu.umn.msi.tropix.storage.core.access.impl;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.common.io.FileContext;
import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.HasStreamInputContext;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.storage.core.access.AccessProvider;

public class FileFunctionAccessProviderImpl implements AccessProvider {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private Function<String, File> fileFunction;

  public boolean deleteFile(final String id) {
    final File file = getRawFile(id);
    return file.delete();
  }

  public boolean fileExists(final String id) {
    final File file = getRawFile(id);
    return file.exists();
  }

  public HasStreamInputContext getFile(final String id) {
    File file = getRawFile(id);
    // <HACK for if file has yet to be copied over by transfer service context reference>
    // int i = 0;
    // while(i++ < 30 && !file.exists()) {
    // try {
    // Thread.sleep(1000);
    // } catch(InterruptedException e) {
    // throw new InterruptedRuntimeException(e);
    // }
    // }
    return new FileContext(file);
  }

  private File getRawFile(final String id) {
    return fileFunction.apply(id);
  }

  public long getLength(String id) {
    return getRawFile(id).length();
  }

  public long getDateModified(String id) {
    return getRawFile(id).lastModified();
  }

  public long putFile(final String id, final InputStream tempFile) {
    final OutputStream outputStream = prepareFileForUpload(id);
    try {
      return IO_UTILS.copy(tempFile, outputStream);
    } finally {
      IO_UTILS.closeQuietly(outputStream);
    }
  }

  private OutputStream prepareFileForUpload(final String id) {
    final File file = getRawFile(id);
    FILE_UTILS.mkdirs(file.getParentFile());
    Preconditions.checkState(file.getParentFile().exists(), "Failed to create the required directory to store the given file");
    final OutputStream outputStream = FILE_UTILS.getFileOutputStream(file);
    return outputStream;
  }

  public void setFileFunction(final Function<String, File> fileFunction) {
    this.fileFunction = fileFunction;
  }

  public OutputStream getPutFileOutputStream(final String id) {
    final OutputStream outputStream = prepareFileForUpload(id);
    return outputStream;
  }

}
