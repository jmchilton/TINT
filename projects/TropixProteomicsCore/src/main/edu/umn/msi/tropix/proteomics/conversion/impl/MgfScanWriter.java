package edu.umn.msi.tropix.proteomics.conversion.impl;

import edu.umn.msi.tropix.proteomics.conversion.Scan;

interface MgfScanWriter {
  void writeScan(final Scan scan);
}