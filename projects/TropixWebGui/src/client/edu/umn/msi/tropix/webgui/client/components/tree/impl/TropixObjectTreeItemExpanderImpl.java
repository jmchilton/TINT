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

package edu.umn.msi.tropix.webgui.client.components.tree.impl;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.Request;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.models.utils.TropixObjectType;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItemExpander;
import edu.umn.msi.tropix.webgui.services.object.FolderService;
import edu.umn.msi.tropix.webgui.services.object.ObjectService;

public class TropixObjectTreeItemExpanderImpl implements TropixObjectTreeItemExpander {
  private TropixObjectType[] types = null;

  public TropixObjectTreeItemExpanderImpl(final TropixObjectType[] types) {
    this.types = types;
  }

  public void expand(final TropixObjectTreeItem tropixObjectTreeItem, final AsyncCallback<List<TropixObject>> callback) {
    final String id = tropixObjectTreeItem.getId();
    final TropixObject object = tropixObjectTreeItem.getObject();
    // Wrong place to check if this was folder IMHO but this pipeline was implemented for folders first
    // so might as well keep the code in place. May be worthwhile to simply everything down the road by 
    // eliminating getFolderContents from FolderService.
    if(object instanceof Folder 
       || object instanceof VirtualFolder
       || object instanceof Request) {
      FolderService.Util.getInstance().getFolderContents(id, this.types, callback);
    } else {
      ObjectService.Util.getInstance().getChildren(id, this.types, callback);
    }
  }

}
