package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import javax.xml.stream.XMLStreamReader;

import org.springframework.util.StringUtils;

import com.google.common.collect.Maps;

import edu.umn.msi.tropix.proteomics.conversion.Scan;
import edu.umn.msi.tropix.proteomics.xml.AbstractXmlIterator;

public class MzxmlScanIterator extends AbstractXmlIterator<Scan> {
  private Map<String, String> parentFiles;
  private int index = 0;

  MzxmlScanIterator(final XMLStreamReader reader) {
    super(reader, "scan");
    setupParentFiles();
  }

  MzxmlScanIterator(final InputStream stream) {
    super(stream, "scan");
    setupParentFiles();
  }

  private void setupParentFiles() {
    final XMLStreamReader reader = reader();
    XMLStreamReaderUtils.skipToElement(reader, "msRun");
    // final int scanCount = Integer.parseInt(XMLStreamReaderUtils.getAttributeValue(reader, "scanCount"));
    XMLStreamReaderUtils.skipToElement(reader, "parentFile");
    final Map<String, String> hashToNameMap = Maps.newHashMap();
    // There must be at least on parentFile !
    while(!XMLStreamReaderUtils.isStartOfElement(reader, "dataProcessing") && XMLStreamReaderUtils.hasNext(reader)) {
      // These attributes are both required...
      if(XMLStreamReaderUtils.isStartOfElement(reader, "parentFile")) {
        final String fileSha1 = XMLStreamReaderUtils.getAttributeValue(reader, "fileSha1");
        final String fileName = XMLStreamReaderUtils.getAttributeValue(reader, "fileName");
        hashToNameMap.put(fileSha1, fileName);
      }
      XMLStreamReaderUtils.next(reader);
    }
    this.parentFiles = hashToNameMap;
  }

  @Override
  protected Scan parseNext() {
    final int number = Integer.parseInt(XMLStreamReaderUtils.getAttributeValue(reader(), "num"));
    final int level = Integer.parseInt(XMLStreamReaderUtils.getAttributeValue(reader(), "msLevel"));
    final String retentionTimeStr = XMLStreamReaderUtils.getAttributeValue(reader(), "retentionTime");
    long retentionTime = -1;
    if(StringUtils.hasText(retentionTimeStr) && MzxmlParserImpl.xmlDatatypeFactory != null) {
      retentionTime = MzxmlParserImpl.xmlDatatypeFactory.newDuration(retentionTimeStr).getTimeInMillis(new Date());
    }
    float precursorMz = 0.0f;
    float precursorIntensity = 0.0f;
    short precursorCharge = 0;
    String parentFileName = parentFiles.values().iterator().next();
    boolean explicitParentFileName = false;
    do {
      XMLStreamReaderUtils.next(reader());
      if(XMLStreamReaderUtils.isStartOfElement(reader(), "scanOrigin")) { // 0..1
        final String sha1 = XMLStreamReaderUtils.getAttributeValue(reader(), "parentFileID");
        if(!parentFiles.containsKey(sha1)) {
          throw new IllegalStateException("Found parentFileID in scanOrigin without match " + sha1);
        }
        parentFileName = parentFiles.get(sha1);
        explicitParentFileName = true;
      } else if(XMLStreamReaderUtils.isStartOfElement(reader(), "precursorMz")) {
        final String intensity = XMLStreamReaderUtils.getAttributeValue(reader(), "precursorIntensity");
        precursorIntensity = Float.parseFloat(intensity);
        final String precursorChargeStr = XMLStreamReaderUtils.getAttributeValue(reader(), "precursorCharge");
        if(StringUtils.hasText(precursorChargeStr)) {
          precursorCharge = Short.parseShort(precursorChargeStr);
        }
        XMLStreamReaderUtils.next(reader());
        final String precursorMzStr = reader().getText();
        precursorMz = Float.parseFloat(precursorMzStr);
        reader().getText();
      }
    } while(!XMLStreamReaderUtils.isStartOfElement(reader(), "peaks"));
    final boolean is64Bit = "64".equals(XMLStreamReaderUtils.getAttributeValue(reader(), "precision"));

    // XMLStreamReaderUtils.next(reader);
    // XMLStreamReaderUtils.next(reader);

    double[] peaks = new double[0];
    // if(reader.hasText()) {
    // if(reader.getEleme)
    peaks = ConversionUtils.extractDoublesFromBase64(XMLStreamReaderUtils.getElementText(reader()), is64Bit);
    XMLStreamReaderUtils.next(reader());
    final Scan scan = new Scan(level, index++, number, peaks);
    scan.setPrecursorMz(precursorMz);
    scan.setPrecursorIntensity(precursorIntensity);
    if(retentionTime != -1) {
      scan.setRt(retentionTime);
    }
    if(precursorCharge != 0) {
      scan.setPrecursorCharge(precursorCharge);
    }
    scan.setParentFileName(parentFileName);
    scan.setParentFileNameExplicit(explicitParentFileName);

    return scan;
  }

}