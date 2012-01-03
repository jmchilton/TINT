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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import edu.umn.msi.tropix.models.locations.Location;

// TODO: Move more items to Locations
public class TreeItems {
  // Used in items
  public static final String ATTRIBUTE_TYPE = "type";
  public static final String ATTRIBUTE_CREATION_DATE = "creationDate";
  public static final String ATTRIBUTE_FOLDER = "isFolder";
  public static final String ATTRIBUTE_NAME = "name";
  public static final String ATTRIBUTE_SORT = "sort";
  public static final String ATTRIBUTE_ID = "id";
  public static final String ATTRIBUTE_ICON = "icon";

  // Managed internally by the tree
  public static final String ATTRIBUTE_PARENT_ID = "parent";
  public static final String ATTRIBUTE_OBJECT = "object";

  public static HashMap<String, TreeItem> getIdMap(final Iterable<TreeItem> items) {
    final HashMap<String, TreeItem> idMap = new HashMap<String, TreeItem>();
    for(final TreeItem item : items) {
      idMap.put(item.getId(), item);
    }
    return idMap;
  }

  public static List<String> getAncestorIds(final Location initialItem) {
    final LinkedList<String> ancestorIds = new LinkedList<String>();
    Location currentItem = initialItem;
    while(currentItem.getParent() != null) {
      currentItem = currentItem.getParent();
      ancestorIds.addFirst(currentItem.getId());
    }
    return ancestorIds;
  }

  public static LinkedHashSet<String> getAncestorIds(final Iterable<TreeItem> treeItems) {
    final LinkedHashSet<String> ancestorIds = new LinkedHashSet<String>();
    for(final TreeItem treeItem : treeItems) {
      ancestorIds.addAll(TreeItems.getAncestorIds(treeItem));
    }
    return ancestorIds;
  }

  public static Collection<String> getIds(final Iterable<TreeItem> treeItems) {
    final LinkedList<String> ids = new LinkedList<String>();
    for(TreeItem treeItem : treeItems) {
      ids.add(treeItem.getId());
    }
    return ids;
  }

}
