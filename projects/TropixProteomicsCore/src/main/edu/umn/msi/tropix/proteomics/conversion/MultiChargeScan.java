package edu.umn.msi.tropix.proteomics.conversion;

import java.util.List;

public class MultiChargeScan extends Scan {
  private List<Short> charges;

  public MultiChargeScan(final Scan scan, final List<Short> charges) {
    super(scan);
    this.charges = charges;
  }

  public List<Short> getCharges() {
    return charges;
  }

}
