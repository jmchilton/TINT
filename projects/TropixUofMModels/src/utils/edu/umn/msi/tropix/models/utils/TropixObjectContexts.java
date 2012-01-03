package edu.umn.msi.tropix.models.utils;


public class TropixObjectContexts {
  private static final TropixObjectUserAuthorities TROPIX_OBJECT_OWNER_CONTEXT = new TropixObjectUserAuthorities(true, true);

  public static TropixObjectUserAuthorities getOwnerContext() {
    return TROPIX_OBJECT_OWNER_CONTEXT;
  }
  
}
