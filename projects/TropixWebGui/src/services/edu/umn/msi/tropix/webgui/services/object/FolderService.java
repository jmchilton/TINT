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

package edu.umn.msi.tropix.webgui.services.object;

import java.util.List;

import org.gwtwidgets.server.spring.GWTRequestMapping;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.models.utils.TropixObjectType;

@RemoteServiceRelativePath("FolderService.rpc")
@GWTRequestMapping("/webgui/FolderService.rpc")
public interface FolderService extends RemoteService {

  public static class Util {

    public static FolderServiceAsync getInstance() {
      return (FolderServiceAsync) GWT.create(FolderService.class);
    }
  }

  List<TropixObject> getFolderContents(String folderId, TropixObjectType[] filterTypes);

  // public TropixObject[][] getFoldersContents(String[] folderIds, TropixObjectType[] filterTypes);

  void createFolder(String parentFolderId, Folder folder);

  void createVirtualFolder(String parentFolderId, VirtualFolder folder);

  VirtualFolder[] getSavedVirtualFolders();
}
