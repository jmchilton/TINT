package edu.umn.msi.tropix.proteomics.conversion;

import com.google.common.base.Function;

import edu.umn.msi.tropix.proteomics.conversion.impl.ITraqFilterScanTransformerImpl;

public class ScanTransformers {

  public static Function<Scan, Scan> getITraqFilter() {
    return new ITraqFilterScanTransformerImpl();
  }
}
