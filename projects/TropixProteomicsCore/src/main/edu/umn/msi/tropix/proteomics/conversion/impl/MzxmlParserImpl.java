/********************************************************************************
 * Copyright (c) 2009 Regents of the University of Minnesota
 *
 * This Software was written at the Minnesota Supercomputing Institute
 * http://msi.umn.edu
 *
 * All rights reserved. The following statement of license applies
 * only to this file, and and not to the other files distributed with it
 * or derived therefrom.  This file is made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 *******************************************************************************/

package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import javax.annotation.WillNotClose;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.codec.binary.Base64;
import org.springframework.util.StringUtils;

import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;

import edu.umn.msi.tropix.proteomics.conversion.MzxmlParser;
import edu.umn.msi.tropix.proteomics.conversion.Scan;

public class MzxmlParserImpl implements MzxmlParser {

  private static DatatypeFactory xmlDatatypeFactory = null;
  static {
    try {
      xmlDatatypeFactory = DatatypeFactory.newInstance();
    } catch(DatatypeConfigurationException e) {
      e.printStackTrace();
    }
  }

  private static class ScanIterator extends UnmodifiableIterator<Scan> {
    private final XMLStreamReader reader;
    private boolean foundStart = false;
    private final Map<String, String> parentFiles;

    ScanIterator(final XMLStreamReader reader, final Map<String, String> parentFiles) {
      this.reader = reader;
      this.parentFiles = parentFiles;
    }

    public boolean hasNext() {
      return findStart();
    }

    public Scan next() {
      findStart();
      foundStart = false;
      final int number = Integer.parseInt(XMLStreamReaderUtils.getAttributeValue(reader, "num"));
      final int level = Integer.parseInt(XMLStreamReaderUtils.getAttributeValue(reader, "msLevel"));
      final String retentionTimeStr = XMLStreamReaderUtils.getAttributeValue(reader, "retentionTime");
      long retentionTime = -1;
      if(StringUtils.hasText(retentionTimeStr) && xmlDatatypeFactory != null) {
        retentionTime = xmlDatatypeFactory.newDuration(retentionTimeStr).getTimeInMillis(new Date());
      }
      float precursorMz = 0.0f;
      float precursorIntensity = 0.0f;
      short precursorCharge = 0;
      String parentFileName = parentFiles.values().iterator().next();
      boolean explicitParentFileName = false;
      do {
        XMLStreamReaderUtils.next(reader);
        if(XMLStreamReaderUtils.isStartOfElement(reader, "scanOrigin")) { // 0..1
          final String sha1 = XMLStreamReaderUtils.getAttributeValue(reader, "parentFileID");
          if(!parentFiles.containsKey(sha1)) {
            throw new IllegalStateException("Found parentFileID in scanOrigin without match " + sha1);
          }
          parentFileName = parentFiles.get(sha1);
          explicitParentFileName = true;
        } else if(XMLStreamReaderUtils.isStartOfElement(reader, "precursorMz")) {
          final String intensity = XMLStreamReaderUtils.getAttributeValue(reader, "precursorIntensity");
          precursorIntensity = Float.parseFloat(intensity);
          final String precursorChargeStr = XMLStreamReaderUtils.getAttributeValue(reader, "precursorCharge");
          if(StringUtils.hasText(precursorChargeStr)) {
            precursorCharge = Short.parseShort(precursorChargeStr);
          }
          XMLStreamReaderUtils.next(reader);
          final String precursorMzStr = reader.getText();
          precursorMz = Float.parseFloat(precursorMzStr);
          reader.getText();
        }
      } while(!XMLStreamReaderUtils.isStartOfElement(reader, "peaks"));
      final boolean is64Bit = "64".equals(XMLStreamReaderUtils.getAttributeValue(reader, "precision"));

      // XMLStreamReaderUtils.next(reader);
      // XMLStreamReaderUtils.next(reader);

      double[] peaks = new double[0];
      // if(reader.hasText()) {
      // if(reader.getEleme)
      byte[] peaksBytes;
      peaksBytes = XMLStreamReaderUtils.getElementText(reader).getBytes();
      final byte[] decodedBytes = Base64.decodeBase64(peaksBytes);
      peaks = ConversionUtils.extractDoubles(decodedBytes, is64Bit);
      // }
      XMLStreamReaderUtils.next(reader);
      final Scan scan = new Scan(level, number, peaks);
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

    private boolean findStart() {
      while(!foundStart && XMLStreamReaderUtils.hasNext(reader)) {
        XMLStreamReaderUtils.next(reader);
        if(XMLStreamReaderUtils.isStartOfElement(reader, "scan")) {
          foundStart = true;
          break;
        }
      }
      return foundStart;
    }

  }

  public MzxmlInfo parse(@WillNotClose final InputStream mzxmlStream) {
    final XMLStreamReader reader = XMLStreamReaderUtils.get(mzxmlStream);
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
    final ScanIterator scanIterator = new ScanIterator(reader, hashToNameMap);
    return new MzxmlInfo(scanIterator);
  }

}
