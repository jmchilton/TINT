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

package edu.umn.msi.tropix.common.io.impl;

import java.io.File;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.io.DisposableResource;
import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.TempFileSuppliers;

public class FileDisposableResourceFactory {
  private static FileUtils fileUtils = FileUtilsFactory.getInstance();

  private static final Function<File, DisposableResource> FILE_FUNCTION = new Function<File, DisposableResource>() {
    public DisposableResource apply(final File tempFile) {
      final FileDisposableResourceImpl disposableResource = new FileDisposableResourceImpl(tempFile);
      return disposableResource;
    }
  };

  public static Function<File, DisposableResource> getFileFunction() {
    return FileDisposableResourceFactory.FILE_FUNCTION;
  }

  public static Function<byte[], DisposableResource> getByteFunciton() {
    return FileDisposableResourceFactory.getByteFunction(TempFileSuppliers.getDefaultTempFileSupplier());
  }

  public static Function<byte[], DisposableResource> getByteFunction(final Supplier<File> tempFileSupplier) {
    return new Function<byte[], DisposableResource>() {
      public DisposableResource apply(final byte[] contents) {
        // Create temp file
        final File tempFile = tempFileSupplier.get();

        // Write contents to temp file
        FileDisposableResourceFactory.fileUtils.writeByteArrayToFile(tempFile, contents);

        return new FileDisposableResourceImpl(tempFile);
      }
    };
  }
}
