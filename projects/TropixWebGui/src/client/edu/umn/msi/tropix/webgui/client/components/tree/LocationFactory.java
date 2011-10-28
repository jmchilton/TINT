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

package edu.umn.msi.tropix.webgui.client.components.tree;

import javax.annotation.Nullable;

import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.webgui.client.components.tree.impl.TropixObjectTreeItemImpl;

public interface LocationFactory {
  TropixObjectTreeItem getTropixObjectTreeItem(@Nullable TreeItem parent, TropixObject object, TropixObjectTreeItemExpander tropixObjectTreeItemExpander);

  TropixObjectTreeItem getTropixObjectTreeItem(TropixObjectTreeItemImpl parent, TropixObject object);

  TreeItem getMySharedFoldersItem(final TropixObjectTreeItemExpander tropixObjectTreeItemExpander);

  TreeItem getTropixHomeItem();

  TreeItem getMyGroupFoldersItem(@Nullable TropixObjectTreeItemExpander expander);

  TreeItem getHomeRootItem(@Nullable TropixObjectTreeItemExpander expander);

  Iterable<TreeItem> getAllRootItems(@Nullable TropixObjectTreeItemExpander expander);

  Iterable<TreeItem> getTropixObjectSourceRootItems(@Nullable TropixObjectTreeItemExpander expander);

  Iterable<TreeItem> getTropixObjectDestinationRootItems(@Nullable TropixObjectTreeItemExpander expander);
  
  Iterable<TreeItem> getConcreteTropixObjectRootItems(@Nullable TropixObjectTreeItemExpander expander);

}
