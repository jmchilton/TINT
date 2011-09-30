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

package edu.umn.msi.tropix.storage.core.monitor;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FilenameUtils;

import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.persistence.service.FileService;
import edu.umn.msi.tropix.persistence.service.FolderService;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;
import edu.umn.msi.tropix.storage.core.PersistentFileMapperService;

class StorageMonitorClosureImpl implements Closure<File> {
  private final TropixObjectService tropixObjectService;
  private final FolderService folderService;
  private final FileService fileService;
  private final PersistentFileMapperService persistentFileMapperService;
  private final MonitorConfig monitorConfig;
  private final Supplier<Credential> credentialSupplier;
  private String storageServiceUrl;

  @Inject
  StorageMonitorClosureImpl(final TropixObjectService tropixObjectService, final FolderService folderService,
      final PersistentFileMapperService persistentFileMapperService, final MonitorConfig monitorConfig, final FileService fileService,
      @Named("hostCredentialSupplier") final Supplier<Credential> credentialSupplier) {
    this.folderService = folderService;
    this.tropixObjectService = tropixObjectService;
    this.persistentFileMapperService = persistentFileMapperService;
    this.monitorConfig = monitorConfig;
    this.credentialSupplier = credentialSupplier;
    this.fileService = fileService;
  }

  public void apply(final File input) {
    if(!persistentFileMapperService.pathHasMapping(FilenameUtils.normalize(input.getAbsolutePath()))) {
      registerFile(input);
    }
  }

  private String getIdentity() {
    return credentialSupplier.get().getIdentity();
  }

  private void registerFile(final File newFile) {
    final String newId = UUID.randomUUID().toString(); // It is perhaps inappropriate to pick id here

    final File rootDirectory = findMatchingDirectory(newFile);
    if(newFile.equals(rootDirectory)) {
      return;
    }

    final List<String> extractPathList = Lists.newArrayList();
    File parent = newFile.getParentFile();
    while(!rootDirectory.equals(parent)) {
      extractPathList.add(parent.getName());
      parent = parent.getParentFile();
    }
    Collections.reverse(extractPathList);

    final String rootFolderName = monitorConfig.getSharedFolderName(rootDirectory);
    final VirtualFolder rootSharedFolder = folderService.getOrCreateRootVirtualFolderWithName(getIdentity(), rootFolderName);
    final VirtualFolder child = folderService.getOrCreateVirtualPath(getIdentity(), rootSharedFolder.getId(),
        Iterables.toArray(extractPathList, String.class));

    final TropixFile tropixFile = new TropixFile();
    tropixFile.setFileId(newId);
    tropixFile.setCommitted(true);
    tropixFile.setName(newFile.getName());
    // TODO: FIX THIS
    // tropixFile.setFileSize(newFile.length());

    tropixFile.setStorageServiceUrl(storageServiceUrl);

    persistentFileMapperService.registerMapping(newId, FilenameUtils.normalize(newFile.getAbsolutePath()));
    final TropixFile returnedFile = tropixObjectService.createFile(getIdentity(), child.getId(), tropixFile, null);
    fileService.recordLength(newId, newFile.length());
    tropixObjectService.addToSharedFolder(getIdentity(), returnedFile.getId(), child.getId(), false);
  }

  private File findMatchingDirectory(File file) {
    File rootDirectory = null;
    for(final File directory : monitorConfig.getDirectories()) {
      if(FilenameUtils.normalize(file.getAbsolutePath()).startsWith(FilenameUtils.normalize(directory.getAbsolutePath()))) {
        rootDirectory = directory;
        break;
      }
    }
    return rootDirectory;
  }

  public void setStorageServiceUrl(String storageServiceUrl) {
    this.storageServiceUrl = storageServiceUrl;
  }

}
