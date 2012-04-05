package edu.umn.msi.tropix.webgui.client.components.impl;

import com.google.inject.Inject;

import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponentFactory;

public class BulkMgfProteinPilotITraqDownloadComponentSupplierImpl extends BulkMgfDownloadComponentSupplierImpl {

  @Inject
  public BulkMgfProteinPilotITraqDownloadComponentSupplierImpl(final TreeComponentFactory treeComponentFactory,
      final LocationFactory locationFactory) {
    super(treeComponentFactory, locationFactory, "PROTEIN_PILOT", true);
  }

}
