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

package edu.umn.msi.tropix.persistence.service;

import javax.annotation.Nullable;

import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.models.utils.TropixObjectType;
import edu.umn.msi.tropix.persistence.aop.Admin;
import edu.umn.msi.tropix.persistence.aop.Modifies;
import edu.umn.msi.tropix.persistence.aop.PersistenceMethod;
import edu.umn.msi.tropix.persistence.aop.Reads;
import edu.umn.msi.tropix.persistence.aop.UserId;

public interface FolderService {

  @PersistenceMethod Folder[] getGroupFolders(@UserId String gridId);

  @PersistenceMethod Folder[] getAllGroupFolders(@Admin @UserId String gridId);

  @PersistenceMethod TropixObject[] getFolderContents(@UserId String userGridId, @Reads String folderId);

  @PersistenceMethod TropixObject[] getFolderContents(@UserId String userGridId, @Reads String folderId, TropixObjectType[] types);

  @PersistenceMethod Folder createFolder(@UserId String userGridId, @Modifies String parentFolderId, Folder folder);

  @PersistenceMethod VirtualFolder createVirtualFolder(@UserId String userGridId, @Modifies @Nullable String parentFolderId, VirtualFolder folder);
  
  @PersistenceMethod VirtualFolder[] getSavedVirtualFolders(@UserId String gridId);

  @PersistenceMethod VirtualFolder getOrCreateVirtualPath(@UserId String userGridId, @Modifies String rootVirtualFolderId, String[] subfolderNames);
  
  @PersistenceMethod VirtualFolder getOrCreateRootVirtualFolderWithName(@UserId String userGridId, final String name);
   
}
