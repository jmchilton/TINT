package edu.umn.msi.tropix.models.utils;


public class TropixObjectContexts {
  private static final TropixObjectContext TROPIX_OBJECT_OWNER_CONTEXT = new TropixObjectContext(true, true);

  public static TropixObjectContext getOwnerContext() {
    return TROPIX_OBJECT_OWNER_CONTEXT;
  }
  
}
