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

package edu.umn.msi.tropix.webgui.server;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.umn.msi.tropix.client.galaxy.GalaxyExportOptions;
import edu.umn.msi.tropix.client.galaxy.GalaxyExporter;
import edu.umn.msi.tropix.files.PersistentModelStorageDataFactory;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.gridftp.GridFtpClient;
import edu.umn.msi.tropix.grid.gridftp.GridFtpClientUtils;
import edu.umn.msi.tropix.grid.gridftp.GridFtpFactory;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.persistence.service.FileService;
import edu.umn.msi.tropix.storage.client.StorageData;
import edu.umn.msi.tropix.webgui.server.aop.ServiceMethod;
import edu.umn.msi.tropix.webgui.server.security.UserSession;
import edu.umn.msi.tropix.webgui.services.object.ExportService;

@ManagedBean
public class ExportServiceImpl implements ExportService {
  private static final Log LOG = LogFactory.getLog(ExportServiceImpl.class);
  private final FileService fileService;
  private final UserSession userSession;
  private final PersistentModelStorageDataFactory storageDataFactory;
  private final GridFtpFactory gridFtpFactory;
  private final GalaxyExporter galaxyExporter;

  @Inject
  public ExportServiceImpl(final FileService fileService, final UserSession userSession, final PersistentModelStorageDataFactory storageDataFactory,
      final GridFtpFactory gridFtpFactory,
      final GalaxyExporter galaxyExporter) {
    this.fileService = fileService;
    this.userSession = userSession;
    this.storageDataFactory = storageDataFactory;
    this.gridFtpFactory = gridFtpFactory;
    this.galaxyExporter = galaxyExporter;
  }

  @ServiceMethod()
  public void export(final String[] ids, final GridFtpServerOptions gridFtpOptions) {
    final TropixFile[] tropixFiles = fileService.getFiles(userSession.getGridId(), ids);
    final String host = gridFtpOptions.getHostname();
    final int port = gridFtpOptions.getPort();
    final Credential credential = userSession.getProxy();
    LOG.info("GridFTP request to host and port " + host + " " + port);
    final GridFtpClient gridFtpClient = gridFtpFactory.getClient(host, port, credential);

    final String path = FilenameUtils.normalizeNoEndSeparator(gridFtpOptions.getPath());
    try {
      LOG.debug("Attempting to create path " + path);
      gridFtpClient.makeDir(path);
    } catch(final RuntimeException e) {
      LOG.warn("Failed to make directory with path " + path + " proceeding anyway in case directory already exists.", e);
    }
    for(final TropixFile tropixFile : tropixFiles) {
      final StorageData data = storageDataFactory.getStorageData(tropixFile, credential);
      final String name = FilenameUtils.getBaseName(tropixFile.getName());
      LOG.debug("Attempting to transfer file wit id " + tropixFile.getFileId() + " and name " + name);
      data.getDownloadContext().get(GridFtpClientUtils.getOutputContext(gridFtpClient, path + "/" + name));
    }
  }

  @ServiceMethod()
  public void exportGalaxy(GalaxyExportOptions galaxyExportOptions) {
    galaxyExporter.uploadFiles(userSession.getGridId(), galaxyExportOptions);
  }
}
