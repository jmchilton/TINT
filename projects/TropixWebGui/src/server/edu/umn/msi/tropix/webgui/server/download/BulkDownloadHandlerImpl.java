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

package edu.umn.msi.tropix.webgui.server.download;

import java.io.OutputStream;
import java.util.Arrays;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.ZipUtils;
import edu.umn.msi.tropix.common.io.ZipUtilsFactory;
import edu.umn.msi.tropix.files.PersistentModelStorageDataFactory;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.persistence.service.FileService;
import edu.umn.msi.tropix.webgui.server.security.UserSession;

@ManagedBean
@FileDownloadHandlerType("bulk")
class BulkDownloadHandlerImpl implements FileDownloadHandler {
  private static final ZipUtils ZIP_UTILS = ZipUtilsFactory.getInstance();
  private final FileService fileService;
  private final UserSession userSession;
  private final PersistentModelStorageDataFactory storageDataFactory;

  @Inject
  BulkDownloadHandlerImpl(final FileService fileService, final UserSession userSession, final PersistentModelStorageDataFactory storageDataFactory) {
    this.fileService = fileService;
    this.userSession = userSession;
    this.storageDataFactory = storageDataFactory;
  }

  public void processDownloadRequest(final OutputStream stream, final Function<String, String> accessor) {
    final String ids = accessor.apply("id");
    String[] idsArray = new String[0];
    if(ids.length() > 0) {
      idsArray = ids.split("\\s*,\\s*");
    }
    final Iterable<TropixFile> files = Arrays.asList(fileService.getFiles(userSession.getGridId(), idsArray));
    final Iterable<InputContext> inputContexts = Iterables.transform(files, new Function<TropixFile, InputContext>() {
      public InputContext apply(final TropixFile file) {
        return storageDataFactory.getStorageData(file, userSession.getProxy()).getDownloadContext();
      }
    });
    final Iterable<String> names = Iterables.transform(files, new Function<TropixFile, String>() {
      public String apply(final TropixFile file) {
        return file.getName();
      }
    });
    ZIP_UTILS.zipContextsToStream(inputContexts, names, stream);
  }

}
