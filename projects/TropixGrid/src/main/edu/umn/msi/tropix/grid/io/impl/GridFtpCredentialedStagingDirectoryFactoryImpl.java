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

package edu.umn.msi.tropix.grid.io.impl;

import java.io.File;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.common.io.StagingDirectory;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.gridftp.GridFtpClient;
import edu.umn.msi.tropix.grid.gridftp.GridFtpClientFactory;
import edu.umn.msi.tropix.grid.gridftp.GridFtpClientUtils;
import edu.umn.msi.tropix.grid.gridftp.GridFtpClient.GridFtpFile;
import edu.umn.msi.tropix.grid.io.CredentialedStagingDirectoryFactory;

public class GridFtpCredentialedStagingDirectoryFactoryImpl implements CredentialedStagingDirectoryFactory {
  private static final Log LOG = LogFactory.getLog(GridFtpCredentialedStagingDirectoryFactoryImpl.class);
  private GridFtpClientFactory gridFtpClientFactory;
  private String tempDirectoryPath = System.getProperty("java.io.tmpdir");
  private boolean deleteStagedFiles = true;
  private String sep = File.separator;

  public void setSep(final String sep) {
    this.sep = sep;
  }
  
  public StagingDirectory get(final Credential proxy) {
    final String newDir = UUID.randomUUID().toString();
    LOG.debug("In get() with proxy " + proxy);
    return new GridFtpStagingDirectoryImpl(gridFtpClientFactory.getGridFtpClient(proxy), tempDirectoryPath + sep + newDir);
  }

  public StagingDirectory get(final Credential proxy, final String stagingDirectoryPath) {
    return new GridFtpStagingDirectoryImpl(gridFtpClientFactory.getGridFtpClient(proxy), stagingDirectoryPath);
  }

  public void setGridFtpClientFactory(final GridFtpClientFactory gridFtpClientFactory) {
    this.gridFtpClientFactory = gridFtpClientFactory;
  }

  public void setDeleteStagedFiles(final boolean deleteTempFiles) {
    this.deleteStagedFiles = deleteTempFiles;
  }

  public void setTempDirectoryPath(final String path) {
    this.tempDirectoryPath = path;
  }

  @NotThreadSafe
  private class GridFtpStagingDirectoryImpl implements StagingDirectory {
    private final GridFtpClient gridFtpClient;
    private final String dir;

    GridFtpStagingDirectoryImpl(final GridFtpClient gridFtpClient, final String dir) {
      this.gridFtpClient = gridFtpClient;
      this.dir = dir;
    }

    public void cleanUp() {
      if(deleteStagedFiles) {
        GridFtpClientUtils.deleteDirectoryRecursively(gridFtpClient, dir);
      }
    }

    public void setup() {
      gridFtpClient.makeDir(dir);
    }

    public String getAbsolutePath() {
      return dir;
    }

    public InputContext getInputContext(final String resourcePath) {
      return GridFtpClientUtils.getInputContext(gridFtpClient, dir + getSep() + resourcePath);
    }

    public OutputContext getOutputContext(final String resourcePath) {
      return GridFtpClientUtils.getOutputContext(gridFtpClient, dir + getSep() + resourcePath);
    }

    public Iterable<String> getResourceNames(@Nullable final String resourcePath) {
      final String fullPath = resourcePath == null ? dir : dir + getSep() + resourcePath;
      final Iterable<GridFtpFile> gridFtpFile = gridFtpClient.list(fullPath);
      return Iterables.transform(gridFtpFile, new Function<GridFtpFile, String>() {
        public String apply(final GridFtpFile gridFtpFile) {
          return FilenameUtils.getName(gridFtpFile.getPath());
        }
      });
    }

    public String getSep() {
      return sep;
    }

    public void makeDirectory(final String resourcePath) {
      gridFtpClient.makeDir(dir + getSep() + resourcePath);
    }

  }

}
