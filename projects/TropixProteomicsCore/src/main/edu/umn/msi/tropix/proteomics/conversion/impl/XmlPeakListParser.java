package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.io.InputStream;

import javax.annotation.WillNotClose;

import com.google.common.collect.UnmodifiableIterator;

import edu.umn.msi.tropix.proteomics.conversion.Scan;

public interface XmlPeakListParser {

  public UnmodifiableIterator<Scan> parse(@WillNotClose final InputStream inputStream);

}
