package edu.umn.msi.tropix.webgui.client.components.impl;

import com.google.inject.Inject;

import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponentFactory;

public class BulkMgfMascotDownloadComponentSupplierImpl extends BulkMgfDownloadComponentSupplierImpl{

  @Inject
  public BulkMgfMascotDownloadComponentSupplierImpl(final TreeComponentFactory treeComponentFactory, 
                                                     final LocationFactory locationFactory) {
    super(treeComponentFactory, locationFactory, "DEFAULT");
  }


}
