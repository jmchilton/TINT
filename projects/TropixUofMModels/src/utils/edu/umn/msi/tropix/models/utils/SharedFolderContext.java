package edu.umn.msi.tropix.models.utils;

import java.io.Serializable;

import edu.umn.msi.tropix.models.VirtualFolder;

public class SharedFolderContext extends TropixObjectWithContext<VirtualFolder> implements Serializable {
  
  public SharedFolderContext() {
  }
  
  public SharedFolderContext(final TropixObjectContext tropixObjectContext, 
                             final VirtualFolder sharedFolder) {
    super(tropixObjectContext, sharedFolder);
  }

}
