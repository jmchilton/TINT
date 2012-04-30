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

package edu.umn.msi.tropix.storage.core.authorization.impl;

import edu.umn.msi.tropix.persistence.service.FileService;
import edu.umn.msi.tropix.storage.core.authorization.AuthorizationProvider;

public class FileServiceAuthorizationProviderImpl implements AuthorizationProvider {
  private FileService fileService = null;

  public void setFileService(final FileService fileService) {
    this.fileService = fileService;
  }

  public Boolean canDelete(final String id, final String callerIdentity) {
    Boolean canDelete = null;
    if(fileService.fileExists(id)) {
      canDelete = fileService.canDeleteFile(callerIdentity, id);
    }
    return canDelete;
  }

  public Boolean canDownload(final String id, final String callerIdentity) {
    Boolean canRead = null;
    if(fileService.fileExists(id)) {
      canRead = fileService.canReadFile(callerIdentity, id);
    }
    return canRead;
  }

  public Boolean canUpload(final String id, final String callerIdentity) {
    Boolean canWrite = null;
    if(fileService.fileExists(id)) {
      canWrite = fileService.canWriteFile(callerIdentity, id);
    }
    return canWrite;
  }

  public Boolean canDownloadAll(String[] ids, String callerIdentity) {
    if(fileService.filesExistAndCanReadAll(ids, callerIdentity)) {
      return true;
    } else {
      for(final String id : ids) {
        if(fileService.fileExists(id) && !fileService.canReadFile(callerIdentity, id)) {
          return false;
        }
      }
      return null;
    }
  }

}
