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

import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.models.utils.TropixObjectType;
import edu.umn.msi.tropix.persistence.service.FolderService;
import edu.umn.msi.tropix.webgui.server.aop.ServiceMethod;
import edu.umn.msi.tropix.webgui.server.models.BeanSanitizer;
import edu.umn.msi.tropix.webgui.server.models.BeanSanitizerUtils;
import edu.umn.msi.tropix.webgui.server.security.UserSession;

@ManagedBean
public class FolderServiceImpl implements edu.umn.msi.tropix.webgui.services.object.FolderService {
  private final FolderService folderService;
  private final UserSession userSession;
  private final BeanSanitizer beanSanitizer;

  @Inject
  public FolderServiceImpl(final FolderService folderService, final UserSession userSession, final BeanSanitizer beanSanitizer) {
    this.folderService = folderService;
    this.userSession = userSession;
    this.beanSanitizer = beanSanitizer;
  }

  @ServiceMethod
  public void createFolder(final String parentFolderId, final Folder folder) {
    this.folderService.createFolder(this.userSession.getGridId(), parentFolderId, folder);
  }

  @ServiceMethod
  public void createVirtualFolder(final String parentFolderId, final VirtualFolder folder) {
    this.folderService.createVirtualFolder(this.userSession.getGridId(), parentFolderId, folder);
  }

  @ServiceMethod(readOnly = true)
  public VirtualFolder[] getSavedVirtualFolders() {
    final VirtualFolder[] savedVirtualFolders = this.folderService.getSavedVirtualFolders(this.userSession.getGridId());
    this.sanitizeArray(savedVirtualFolders);
    return savedVirtualFolders;
  }

  private <T> void sanitizeArray(final T[] objects) {
    for(int i = 0; i < objects.length; i++) {
      objects[i] = this.beanSanitizer.sanitize(objects[i]);
    }
  }

  /*
   * @ServiceMethod(readOnly = true) public TropixObject[][] getFoldersContents(final String[] folderIds, final TropixObjectType[] filterTypes) { final TropixObject[][] objects = new TropixObject[folderIds.length][]; for(int i = 0; i < objects.length; i++) { objects[i] =
   * this.getFolderContents(folderIds[i], filterTypes); } return objects; }
   */

  @ServiceMethod(readOnly = true)
  public List<TropixObject> getFolderContents(final String folderId, final TropixObjectType[] filterTypes) {
    edu.umn.msi.tropix.models.TropixObject[] objects;
    if(filterTypes != null) {
      objects = this.folderService.getFolderContents(this.userSession.getGridId(), folderId, filterTypes);
    } else {
      objects = this.folderService.getFolderContents(this.userSession.getGridId(), folderId);
    }
    return BeanSanitizerUtils.sanitizeArray(beanSanitizer, objects);
  }

  public List<Folder> getGroupFolders() {
    final Folder[] groupFolders = this.folderService.getGroupFolders(this.userSession.getGridId());
    this.sanitizeArray(groupFolders);
    return Lists.newArrayList(groupFolders);
  }
}
