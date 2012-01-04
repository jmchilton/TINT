package edu.umn.msi.tropix.models.locations;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.models.utils.TropixObjectUserAuthorities;

public class Locations {
  public static final String MY_HOME = "My Home";
  public static final String MY_GROUP_FOLDERS = "My Group Folders";
  public static final String MY_SHARED_FOLDERS = "My Shared Folders";

  private static final Set<String> BASE_LOCATIONS = ImmutableSet.<String>builder().add(MY_GROUP_FOLDERS).add(MY_HOME).add(MY_SHARED_FOLDERS).build();

  public static boolean isValidBaseLocation(final String name) {
    return BASE_LOCATIONS.contains(name);
  }

  public static final String MY_SHARED_FOLDERS_ID = "-1";
  public static final String MY_RECENT_ACTIVITY_ID = "-3";
  public static final String MY_RECENT_SEARCHES_ID = "-4";
  public static final String MY_INCOMING_REQUESTS_ID = "-5";
  public static final String MY_OUTGOING_REQUESTS_ID = "-6";
  public static final String MY_GROUP_FOLDERS_ID = "-7";
  public static final String MY_GROUP_SHARED_FOLDERS_ID = "-8";

  public static boolean isMyGroupSharedFoldersItem(final Location treeItem) {
    return MY_GROUP_SHARED_FOLDERS_ID.equals(treeItem.getId());
  }

  public static boolean isMyGroupFoldersItem(final Location treeItem) {
    return MY_GROUP_FOLDERS_ID.equals(treeItem.getId());
  }

  public static boolean isMySharedFoldersItem(final Location treeItem) {
    return MY_SHARED_FOLDERS_ID.equals(treeItem.getId());
  }

  public static boolean isIncomingRequestsItem(final Location treeItem) {
    return MY_INCOMING_REQUESTS_ID.equals(treeItem.getId());
  }

  public static boolean isOutgoingRequestsItem(final Location treeItem) {
    return MY_OUTGOING_REQUESTS_ID.equals(treeItem.getId());
  }

  public static boolean isMyRecentActivityItem(final Location treeItem) {
    return MY_RECENT_ACTIVITY_ID.equals(treeItem.getId());
  }

  public static boolean isRootLocationAFolder(final Location rootLocation) {
    return isMySharedFoldersItem(rootLocation) ||
           isMyGroupFoldersItem(rootLocation) ||
           isMyGroupSharedFoldersItem(rootLocation);
  }

  public static boolean isRootASharedRootMetaLocation(final Location location) {
    final Location root = location.getRoot();
    return isASharedRootMetaLocation(root);
  }

  public static boolean isASharedRootMetaLocation(final Location location) {
    return Locations.isMySharedFoldersItem(location) || Locations.isMyGroupSharedFoldersItem(location);
  }

  public static boolean isSharedFolderRoot(final Location location) {
    return isRootASharedRootMetaLocation(location) && isASharedRootMetaLocation(location.getParent());
  }

  /**
   * Two tree items are "alike" if they meet the following two conditions
   * <ol>
   * <li>Have the same ids.</li>
   * <li>Have the same parent ids, or both have no parents.</li>
   * </ol>
   * This condition may need to be amended as new types of objects are added, but it works for now to capture the concept of two distinct objects
   * along with a unique path to them.
   * 
   * @param treeItem1
   * @param treeItem2
   * @return
   */
  public static boolean alike(final Location treeItem1, final Location treeItem2) {
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

  public static boolean allParentsAreFolder(final Collection<? extends Location> treeItems) {
    boolean allParentsAreFolder = true;
    for(final Location item : treeItems) {
      if(!(item.getParent() instanceof TropixObjectLocation)) {
        allParentsAreFolder = false;
        break;
      }
      final TropixObjectLocation parentItem = (TropixObjectLocation) item.getParent();
      final TropixObject tropixObject = parentItem.getObject();
      if(!(tropixObject instanceof Folder || tropixObject instanceof VirtualFolder)) {
        allParentsAreFolder = false;
        break;
      }
    }
    return allParentsAreFolder;
  }

  /**
   * Test used by deleteItem and moveItem, could be useful elsewhere as well.
   * 
   * @param treeItems
   * @return
   */
  public static boolean allTropixObjectTreeItemsWithSameRoot(final Collection<? extends Location> treeItems) {
    TropixObjectLocation firstItem = null;
    boolean result = true;
    for(final Location item : treeItems) {
      if(!(item instanceof TropixObjectLocation)) {
        result = false;
        break;
      }
      final TropixObjectLocation tropixObjectTreeItem = (TropixObjectLocation) item;
      if(firstItem == null) {
        firstItem = tropixObjectTreeItem;
      }
      if(tropixObjectTreeItem.getTropixObjectLocationRoot() != firstItem.getTropixObjectLocationRoot()) {
        result = false;
        break;
      }
    }
    return result;
  }

  public static boolean allTropixObjectLocationsAreModifiable(final Collection<? extends Location> locations) {
    boolean anyUndeletable = false;
    for(final Location treeItem : locations) {
      final TropixObjectLocation tropixObjectItem = (TropixObjectLocation) treeItem;
      final TropixObjectUserAuthorities authorities = tropixObjectItem.getContext();
      if(authorities != null && !authorities.isModifiable()) {
        anyUndeletable = true;
        break;
      }
    }
    return !anyUndeletable;
  }

  public static <T extends Location> HashMap<String, T> getIdMap(final Iterable<T> items) {
    final HashMap<String, T> idMap = new HashMap<String, T>();
    for(final T item : items) {
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

  public static Collection<String> getIds(final Iterable<? extends Location> treeItems) {
    final LinkedList<String> ids = new LinkedList<String>();
    for(Location treeItem : treeItems) {
      ids.add(treeItem.getId());
    }
    return ids;
  }

  public static LinkedHashSet<String> getAncestorIds(final Iterable<? extends Location> treeItems) {
    final LinkedHashSet<String> ancestorIds = new LinkedHashSet<String>();
    for(final Location treeItem : treeItems) {
      ancestorIds.addAll(getAncestorIds(treeItem));
    }
    return ancestorIds;
  }

}
