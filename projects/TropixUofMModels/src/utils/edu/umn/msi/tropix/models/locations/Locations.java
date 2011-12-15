package edu.umn.msi.tropix.models.locations;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public class Locations {
  public static final String MY_HOME = "My Home";
  public static final String MY_GROUP_FOLDERS = "My Group Folders";
  public static final String MY_SHARED_FOLDERS = "My Shared Folders";

  private static final Set<String> BASE_LOCATIONS = ImmutableSet.<String>builder().add(MY_GROUP_FOLDERS).add(MY_HOME).add(MY_SHARED_FOLDERS).build();

  public static boolean isValidBaseLocation(final String name) {
    return BASE_LOCATIONS.contains(name);
  }

  public static final String MY_SHARED_FOLDERS_ID = "-1";
  public static final String TROPIX_HOME_ID = "-2";
  public static final String MY_RECENT_ACTIVITY_ID = "-3";
  public static final String MY_RECENT_SEARCHES_ID = "-4";
  public static final String MY_INCOMING_REQUESTS_ID = "-5";
  public static final String MY_OUTGOING_REQUESTS_ID = "-6";
  public static final String MY_GROUP_FOLDERS_ID = "-7";
  public static final String MY_GROUP_SHARED_FOLDERS_ID = "-7";

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

}
