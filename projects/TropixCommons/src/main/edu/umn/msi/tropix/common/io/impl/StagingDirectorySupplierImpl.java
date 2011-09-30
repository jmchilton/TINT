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
import java.io.IOException;
import java.util.Collection;

import javax.annotation.Nullable;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.common.io.FileFunctions;
import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.io.InputContexts.FileStreamInputContextImpl;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.common.io.OutputContexts;
import edu.umn.msi.tropix.common.io.StagingDirectory;
import edu.umn.msi.tropix.common.io.StagingDirectoryFactory;
import edu.umn.msi.tropix.common.io.TempDirectoryCreator;
import edu.umn.msi.tropix.common.io.TempDirectoryCreatorImpl;

@ManagedResource
public class StagingDirectorySupplierImpl implements StagingDirectoryFactory {
  private static final TempDirectoryCreatorImpl DEFAULT_TEMP_DIRECTORY_CREATOR = new TempDirectoryCreatorImpl(System.getProperty("java.io.tmpdir"));
  private final FileUtils fileUtils = FileUtilsFactory.getInstance();
  private boolean deleteStagedFiles = true;
  private TempDirectoryCreator tempDirectoryCreator = StagingDirectorySupplierImpl.DEFAULT_TEMP_DIRECTORY_CREATOR;

  public StagingDirectory get() {
    final StagingDirectoryImpl stagingDirectory = new StagingDirectoryImpl();
    return stagingDirectory;
  }

  public StagingDirectory get(final String path) {
    final StagingDirectoryImpl stagingDirectory = new StagingDirectoryImpl();
    stagingDirectory.directory = new File(path);
    stagingDirectory.setup = true;
    return stagingDirectory;
  }

  private class StagingDirectoryImpl implements StagingDirectory {
    private File directory = null;
    private boolean setup = false;

    public void cleanUp() {
      if(StagingDirectorySupplierImpl.this.deleteStagedFiles && this.directory != null) {
        StagingDirectorySupplierImpl.this.fileUtils.deleteDirectoryQuietly(this.directory);
      }
    }

    public void setup() {
      if(!this.setup) {
        this.directory = StagingDirectorySupplierImpl.this.tempDirectoryCreator.getNewTempDirectory();
        this.setup = true;
      }
    }

    public String getAbsolutePath() {
      Preconditions.checkNotNull(this.directory, "setup must be called before getFile()");
      return this.directory.getAbsolutePath();
    }

    private File getFile(@Nullable final String resourcePath) {
      return resourcePath == null ? this.directory : new File(this.directory, resourcePath);
    }

    public FileStreamInputContextImpl getInputContext(final String resourcePath) {
      return InputContexts.forFile(this.getFile(resourcePath));
    }

    public OutputContext getOutputContext(final String resourcePath) {
      return OutputContexts.forFile(this.getFile(resourcePath));
    }

    public void makeDirectory(final String resourcePath) {
      StagingDirectorySupplierImpl.this.fileUtils.mkdirs(this.getFile(resourcePath));
    }

    public Iterable<String> getResourceNames(@Nullable final String resourcePath) {
      final File directoryToList = this.getFile(resourcePath);
      final Collection<File> files = StagingDirectorySupplierImpl.this.fileUtils.listFiles(directoryToList);
      return Iterables.transform(files, FileFunctions.getNameFunction());
    }

    public String getSep() {
      return File.separator;
    }
  }

  @ManagedAttribute
  public boolean getDeleteStagedFiles() {
    return this.deleteStagedFiles;
  }

  @ManagedAttribute
  public void setDeleteStagedFiles(final boolean deleteStagedFiles) {
    this.deleteStagedFiles = deleteStagedFiles;
  }

  public TempDirectoryCreator getTempDirectoryCreator() {
    return this.tempDirectoryCreator;
  }

  public void setTempDirectoryCreator(final TempDirectoryCreator tempDirectoryCreator) {
    this.tempDirectoryCreator = tempDirectoryCreator;
  }

  /**
   * Convenience method which creates a TempDirectoryImpl from the specified directory.
   * 
   * @param tempDirectory
   * @throws IOException
   */
  @ManagedOperation
  public void setTempDirectoryPath(final String tempDirectory) throws IOException {
    final TempDirectoryCreatorImpl tempDirectoryCreator = new TempDirectoryCreatorImpl(tempDirectory);
    this.tempDirectoryCreator = tempDirectoryCreator;
  }

}
