package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.io.InputStream;

import javax.xml.stream.XMLStreamReader;

import com.google.common.collect.UnmodifiableIterator;

import edu.umn.msi.tropix.proteomics.conversion.Scan;

public class XmlPeakListParserImpl implements XmlPeakListParser {

  public UnmodifiableIterator<Scan> parse(final InputStream inputStream) {
    final XMLStreamReader reader = XMLStreamReaderUtils.get(inputStream);
    while(!XMLStreamReaderUtils.isStartElement(reader)) {
      XMLStreamReaderUtils.next(reader);
    }
    UnmodifiableIterator<Scan> iterator;
    if(XMLStreamReaderUtils.isStartOfElement(reader, "mzXML")) {
      iterator = new MzxmlScanIterator(reader);
    } else {
      iterator = new MzmlScanIterator(reader);
    }
    return iterator;
  }
}
