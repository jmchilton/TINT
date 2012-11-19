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

package edu.umn.msi.tropix.storage.core;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import edu.umn.msi.tropix.common.io.HasStreamInputContext;

public interface StorageManager {

  static interface UploadCallback {

    void onUpload(InputStream inputStream);

  }

  static class FileMetadata {
    private final long dateModified;

    private final long length;

    public FileMetadata(final long dateModified, final long length) {
      this.dateModified = dateModified;
      this.length = length;
    }

    public long getDateModified() {
      return dateModified;
    }

    public long getLength() {
      return length;
    }

  }

  @Deprecated
  long getDateModified(final String id, final String gridId);

  boolean setDateModified(final String id, final String gridId, final long dataModified);

  @Deprecated
  long getLength(final String id, final String gridId);

  FileMetadata getFileMetadata(final String id, final String gridId);

  List<FileMetadata> getFileMetadata(final List<String> ids, final String gridId);

  HasStreamInputContext download(String id, String gridId);

  // Allow batch pre-checking to avoid extra database hits
  HasStreamInputContext download(final String id, final String gridId, final boolean checkAccess);

  UploadCallback upload(String id, String gridId);

  OutputStream prepareUploadStream(final String id, final String gridId);

  boolean delete(String id, String gridId);

  boolean exists(String id);

  boolean canDelete(String id, String callerIdentity);

  boolean canDownload(String id, String callerIdentity);

  boolean canUpload(String id, String callerIdentity);
}
