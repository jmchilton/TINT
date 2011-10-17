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

import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.VirtualFolder;

public class TreeItems {
  // Used in items
  public static final String ATTRIBUTE_TYPE = "type";
  public static final String ATTRIBUTE_CREATION_DATE = "creationDate";
  public static final String ATTRIBUTE_FOLDER = "isFolder";
  public static final String ATTRIBUTE_NAME = "name";
  public static final String ATTRIBUTE_ID = "id";
  public static final String ATTRIBUTE_ICON = "icon";

  // Managed internally by the tree
  public static final String ATTRIBUTE_PARENT_ID = "parent";
  public static final String ATTRIBUTE_OBJECT = "object";

  public static final String MY_SHARED_FOLDERS_ID = "-1";
  public static final String TROPIX_HOME_ID = "-2";
  public static final String MY_RECENT_ACTIVITY_ID = "-3";
  public static final String MY_RECENT_SEARCHES_ID = "-4";
  public static final String MY_INCOMING_REQUESTS_ID = "-5";
  public static final String MY_OUTGOING_REQUESTS_ID = "-6";
  public static final String MY_GROUP_FOLDERS_ID = "-7";

  public static boolean isMyGroupFoldersItem(final TreeItem treeItem) {
    return TreeItems.MY_GROUP_FOLDERS_ID.equals(treeItem.getId());
  }
  
  public static boolean isMySharedFoldersItem(final TreeItem treeItem) {
    return TreeItems.MY_SHARED_FOLDERS_ID.equals(treeItem.getId());
  }

  public static boolean isIncomingRequestsItem(final TreeItem treeItem) {
    return TreeItems.MY_INCOMING_REQUESTS_ID.equals(treeItem.getId());
  }

  public static boolean isOutgoingRequestsItem(final TreeItem treeItem) {
    return TreeItems.MY_OUTGOING_REQUESTS_ID.equals(treeItem.getId());
  }

  public static boolean isMyRecentActivityItem(final TreeItem treeItem) {
    return TreeItems.MY_RECENT_ACTIVITY_ID.equals(treeItem.getId());
  }

  public static HashMap<String, TreeItem> getIdMap(final Iterable<TreeItem> items) {
    final HashMap<String, TreeItem> idMap = new HashMap<String, TreeItem>();
    for(final TreeItem item : items) {
      idMap.put(item.getId(), item);
    }
    return idMap;
  }

  public static List<String> getAncestorIds(final TreeItem initialItem) {
    final LinkedList<String> ancestorIds = new LinkedList<String>();
    TreeItem currentItem = initialItem;
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

  public static boolean allParentsAreFolder(final Collection<TreeItem> treeItems) {
    boolean allParentsAreFolder = true;
    for(final TreeItem item : treeItems) {
      if(!(item.getParent() instanceof TropixObjectTreeItem)) {
        allParentsAreFolder = false;
        break;
      } 
      final TropixObjectTreeItem parentItem = (TropixObjectTreeItem) item.getParent();
      final TropixObject tropixObject = parentItem.getObject();
      if(!(tropixObject instanceof Folder || tropixObject instanceof VirtualFolder)) {
        allParentsAreFolder = false;
        break;
      }
    }
    System.out.println("Returing allParentsAreFolder " + allParentsAreFolder);
    return allParentsAreFolder;
  }
  
  /**
   * Test used by deleteItem and moveItem, could be useful elsewhere as well.
   * 
   * @param treeItems
   * @return
   */
  public static boolean allTropixObjectTreeItemsWithSameRoot(final Collection<TreeItem> treeItems) {
    TreeItem firstItem = null;
    boolean result = true;
    for(final TreeItem item : treeItems) {
      if(firstItem == null) {
        firstItem = item;
      }
      if(item instanceof TropixObjectTreeItem && item.getRoot() == firstItem.getRoot()) {
        continue;
      } else {
        result = false;
        break;
      }
    }
    return result;
  }

  /**
   * Two tree items are "alike" if they meet the following two conditions
   * <ol>
   * <li>Have the same ids.</li>
   * <li>Have the same parent ids, or both have no parents.</li>
   * </ol>
   * This condition may need to be amended as new types of objects are added, but it works for now to capture the concept of two distinct objects along with a unique path to them.
   * 
   * @param treeItem1
   * @param treeItem2
   * @return
   */
  public static boolean alike(final TreeItem treeItem1, final TreeItem treeItem2) {
    boolean result;
    if(!treeItem1.getId().equals(treeItem2.getId())) {
      result = false;
    } else if(treeItem1.getParent() == null) {
      result = treeItem2.getParent() == null;
    } else if(treeItem2.getParent() == null) {
      result = false;
    } else {
      result = treeItem1.getParent().getId().equals(treeItem2.getParent().getId());
    }
    return result;
  }

}
