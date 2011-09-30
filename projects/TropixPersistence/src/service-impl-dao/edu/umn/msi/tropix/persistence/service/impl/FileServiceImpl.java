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

package edu.umn.msi.tropix.persistence.service.impl;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.ManagedBean;
import javax.annotation.Nullable;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.PhysicalFile;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.Request;
import edu.umn.msi.tropix.models.ScaffoldAnalysis;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.models.utils.ModelFunctions;
import edu.umn.msi.tropix.models.utils.TropixObjectVisitorImpl;
import edu.umn.msi.tropix.models.utils.TropixObjectVistorUtils;
import edu.umn.msi.tropix.persistence.service.FileService;

@ManagedBean
@Named("fileService")
class FileServiceImpl extends ServiceBase implements FileService {
  private static final Log LOG = LogFactory.getLog(FileServiceImpl.class);

  public boolean canDeleteFile(final String userId, final String fileId) {
    return false;
  }

  public boolean canReadFile(final String userId, final String fileId) {
    final TropixFile tropixFile = getTropixObjectDao().loadTropixFileWithFileId(fileId);
    return getSecurityProvider().canRead(tropixFile.getId(), userId);
  }

  public boolean canWriteFile(final String userId, final String fileId) {
    final TropixFile file = getTropixObjectDao().loadTropixFileWithFileId(fileId);
    return file == null || getSecurityProvider().canModify(file.getId(), userId);
  }

  public TropixFile[] getFiles(final String userGridId, final String[] idsArray) {
    return super.filter(getFiles(Arrays.asList(idsArray)), TropixFile.class, userGridId);
  }

  private Collection<TropixFile> getFiles(final Iterable<String> ids) {
    final Collection<TropixFile> files = Lists.newLinkedList();
    for(final String id : ids) {
      final TropixObject tropixObject = getTropixObjectDao().loadTropixObject(id);
      final FileTropixObjectVisitorImpl visitor = new FileTropixObjectVisitorImpl();
      TropixObjectVistorUtils.visit(tropixObject, visitor);
      files.addAll(visitor.files);
    }
    return files;
  }

  class FileTropixObjectVisitorImpl extends TropixObjectVisitorImpl {
    private final Collection<TropixFile> files = Sets.newHashSet();

    private void recursiveAdd(@Nullable final Collection<TropixObject> objects) {
      if(objects != null) {
        files.addAll(getFiles(Iterables.transform(objects, ModelFunctions.getIdFunction())));
      }
    }

    @Override
    public void visitFolder(final Folder folder) {
      recursiveAdd(folder.getContents());
    }

    @Override
    public void visitVirtualFolder(final VirtualFolder virtualFolder) {
      recursiveAdd(virtualFolder.getContents());
    }

    @Override
    public void visitRequest(final Request request) {
      recursiveAdd(request.getContents());
    }

    private void add(@Nullable final TropixFile file) {
      if(file != null) {
        files.add(file);
      }
    }

    @Override
    public void visitDatabase(final Database database) {
      add(database.getDatabaseFile());
    }

    @Override
    public void visitProteomicsRun(final ProteomicsRun proteomicsRun) {
      add(proteomicsRun.getMzxml());
      add(proteomicsRun.getSource());
    }

    @Override
    public void visitScaffoldAnalysis(final ScaffoldAnalysis scaffoldAnalysis) {
      add(scaffoldAnalysis.getOutputs());
    }

    @Override
    public void visitIdentificationAnalysis(final IdentificationAnalysis idAnalysis) {
      add(idAnalysis.getOutput());
    }

    @Override
    public void visitTropixFile(final TropixFile file) {
      add(file);
    }

  }

  public TropixFile ensureFileWithIdExists(final String userId, final String fileId) {
    LOG.trace("Ensuring file with id " + fileId + " exists for user " + userId);
    if(!fileExists(fileId)) {
      final TropixFile file = new TropixFile();
      file.setFileId(fileId);
      file.setCommitted(false);
      super.saveNewObject(file, userId); // When the file is actually saved with a destination the owner
                                         // will be replaced and providers will be setup, etc...
    }
    final TropixFile tropixFile = getTropixObjectDao().loadTropixFileWithFileId(fileId);
    Preconditions.checkState(getSecurityProvider().canRead(tropixFile.getId(), userId));
    return tropixFile;
  }

  public PhysicalFile loadPhysicalFile(final String fileId) {
    return getDaoFactory().getDao(PhysicalFile.class).load(fileId);
  }

  // TODO: Update
  public void recordLength(final String fileId, final long length) {
    LOG.trace("Recording file length of " + length + " for file with fileId " + fileId);
    final PhysicalFile pFile = new PhysicalFile();
    pFile.setId(fileId);
    pFile.setSize(length);
    getDaoFactory().getDao(PhysicalFile.class).saveObject(pFile);
  }

  public void commit(final String fileId) {
    final TropixFile file = getTropixObjectDao().loadTropixFileWithFileId(fileId);
    Preconditions.checkNotNull(file, "Attempting to commit file with fileId " + fileId + " but no such file appears to exist.");
    file.setCommitted(true);
    getTropixObjectDao().saveOrUpdateTropixObject(file);
  }

  public boolean fileExists(final String fileId) {
    return null != getTropixObjectDao().loadTropixFileWithFileId(fileId);
  }

}
