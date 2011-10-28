package edu.umn.msi.tropix.webgui.client.components.tree;

public interface Location {

  /**
   * This should not return null, root can be itself.
   */
  Location getRoot();

  /**
   * May return null if this item is a root item.
   */
  Location getParent();

  String getId();

  boolean isFolder();

  String getType();

  String getName();

  String getSort();

}