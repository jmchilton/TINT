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

package edu.umn.msi.tropix.webgui.client.models;

import java.util.Collection;

import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.mediators.ActionMediator.ActionEvent;

public final class NewItemFolderActionEventImpl implements ActionEvent {
  private final NewItemFolderImpl newItemFolder;
  private final Collection<TreeItem> items;

  private NewItemFolderActionEventImpl(final NewItemFolderImpl newItemFolder, final Collection<TreeItem> items) {
    this.newItemFolder = newItemFolder;
    this.items = items;

  }

  public static NewItemFolderActionEventImpl forNewItemFolder(final NewItemFolderImpl newItemFolder, final Collection<TreeItem> items) {
    return new NewItemFolderActionEventImpl(newItemFolder, items);
  }

  public String getActionType() {
    return "itemFolder";
  }

  public NewItemFolderImpl getNewItemFolder() {
    return newItemFolder;
  }

  public Collection<TreeItem> getItems() {
    return items;
  }

}
