package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.io.InputStream;

import com.google.common.collect.UnmodifiableIterator;

import edu.umn.msi.tropix.proteomics.conversion.Scan;

public class MzmlParserImpl {

  public UnmodifiableIterator<Scan> parse(final InputStream inputStream) {
    return new MzmlScanIterator(inputStream);
  }

}
