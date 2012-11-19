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

package edu.umn.msi.tropix.storage.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.io.output.CountingOutputStream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.HasStreamInputContext;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.persistence.service.FileService;
import edu.umn.msi.tropix.storage.core.StorageManager;
import edu.umn.msi.tropix.storage.core.access.AccessProvider;
import edu.umn.msi.tropix.storage.core.authorization.AuthorizationProvider;

public class StorageManagerImpl implements StorageManager {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private AuthorizationProvider authorizationProvider;
  private AccessProvider accessProvider;
  private FileService fileService;
  // Ids of callers who need the files uploaded to be committed, such as request services
  private Iterable<String> committingCallerIds = Lists.newArrayList();

  public void setCommittingCallerIds(final Iterable<String> committingCallerIds) {
    this.committingCallerIds = committingCallerIds;
  }

  public boolean delete(final String id, final String gridId) {
    if(!authorizationProvider.canDelete(id, gridId)) {
      throw new RuntimeException("User " + gridId + " cannot delete file " + id);
    }
    return accessProvider.deleteFile(id);
  }

  public HasStreamInputContext download(final String id, final String gridId) {
    return download(id, gridId, true);
  }

  public HasStreamInputContext download(final String id, final String gridId, final boolean checkAccess) {
    if(checkAccess && !authorizationProvider.canDownload(id, gridId)) {
      throw new RuntimeException("User " + gridId + " cannot access file " + id);
    }
    return accessProvider.getFile(id);
  }

  public long getDateModified(String id, String gridId) {
    return getFileMetadata(id, gridId).getDateModified();
  }

  public long getLength(String id, String gridId) {
    return getFileMetadata(id, gridId).getLength();
  }

  public boolean setDateModified(final String id, final String gridId, final long dateModified) {
    if(!authorizationProvider.canDownload(id, gridId)) {
      throw new RuntimeException("User " + gridId + " cannot access file " + id);
    }
    return accessProvider.setDateModified(gridId, dateModified);
  }

  public FileMetadata getFileMetadata(String id, String gridId) {
    if(!authorizationProvider.canDownload(id, gridId)) {
      throw new RuntimeException("User " + gridId + " cannot access file " + id);
    }
    return accessProvider.getFileMetadata(id);
  }

  public List<FileMetadata> getFileMetadata(List<String> ids, String gridId) {
    final ImmutableList.Builder<FileMetadata> fileMetadataList = ImmutableList.builder();
    if(!authorizationProvider.canDownloadAll(Iterables.toArray(ids, String.class), gridId)) {
      throw new RuntimeException("User " + gridId + " cannot access one of files " + Iterables.toString(ids));
    }
    for(final String id : ids) {
      fileMetadataList.add(accessProvider.getFileMetadata(id));
    }
    return fileMetadataList.build();
  }

  public boolean exists(final String id) {
    return accessProvider.fileExists(id);
  }

  public UploadCallback upload(final String id, final String gridId) {
    ensureCanUpload(id, gridId);
    return new UploadCallback() {
      public void onUpload(final InputStream inputStream) {
        try {
          long fileLength = accessProvider.putFile(id, inputStream);
          finalizeUpload(id, gridId, fileLength);
        } finally {
          IO_UTILS.closeQuietly(inputStream);
        }
      }
    };
  }

  private void finalizeUpload(final String id, final String gridId, final long fileLength) {
    if(Iterables.contains(committingCallerIds, gridId)) {
      fileService.commit(id);
    }
    fileService.recordLength(id, fileLength);
  }

  private void ensureCanUpload(final String id, final String gridId) {
    if(!authorizationProvider.canUpload(id, gridId)) {
      throw new RuntimeException("User " + gridId + " cannot upload file " + id);
    }
  }

  public OutputStream prepareUploadStream(final String id, final String gridId) {
    ensureCanUpload(id, gridId);
    return new CountingOutputStream(accessProvider.getPutFileOutputStream(id)) {
      @Override
      public void close() throws IOException {
        try {
          super.close();
        } finally {
          finalizeUpload(id, gridId, getByteCount());
        }
      }
    };
  }

  public boolean canDelete(final String id, final String callerIdentity) {
    return unbox(authorizationProvider.canDelete(id, callerIdentity));
  }

  public boolean canDownload(final String id, final String callerIdentity) {
    return unbox(authorizationProvider.canDownload(id, callerIdentity));
  }

  public boolean canUpload(final String id, final String callerIdentity) {
    return unbox(authorizationProvider.canUpload(id, callerIdentity));
  }

  private boolean unbox(@Nullable final Boolean arg) {
    return arg == null ? false : arg;
  }

  public void setAuthorizationProvider(final AuthorizationProvider authorizationProvider) {
    this.authorizationProvider = authorizationProvider;
  }

  public void setAccessProvider(final AccessProvider accessProvider) {
    this.accessProvider = accessProvider;
  }

  public void setFileService(final FileService fileService) {
    this.fileService = fileService;
  }

}
