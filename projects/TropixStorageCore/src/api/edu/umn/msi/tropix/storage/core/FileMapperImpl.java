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
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Supplier;
import com.google.common.collect.MapMaker;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.storage.core.StorageManager.UploadCallback;

public class FileMapperImpl implements FileMapper {
  // TODO: Replace with cache, cannot until Cache interface has put method (in guava 11)
  private final ConcurrentMap<String, Object> fileMap = new MapMaker().expiration(120 * 60, TimeUnit.SECONDS).makeMap();
  private Supplier<String> downloadPrefix, uploadPrefix;

  public void setDownloadPrefix(final Supplier<String> downloadPrefix) {
    this.downloadPrefix = downloadPrefix;
  }

  public void setUploadPrefix(final Supplier<String> uploadPrefix) {
    this.uploadPrefix = uploadPrefix;
  }

  public String prepareDownload(final InputContext file) {
    final String key = UUID.randomUUID().toString();
    fileMap.put(key, file);
    return downloadPrefix.get() + key;
  }

  public String prepareUpload(final UploadCallback uploadCallback) {
    final String key = UUID.randomUUID().toString();
    fileMap.put(key, uploadCallback);
    return uploadPrefix.get() + key;
  }

  public void handleDownload(final String fileKey, final OutputStream outputStream) {
    final InputContext file = (InputContext) fileMap.remove(fileKey);
    file.get(outputStream);
  }

  public void handleUpload(final String fileKey, final InputStream inputStream) {
    final UploadCallback uploadCallback = (UploadCallback) fileMap.remove(fileKey);
    uploadCallback.onUpload(inputStream);
  }

}
