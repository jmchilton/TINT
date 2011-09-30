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
import java.util.Formatter;
import java.util.Iterator;
import java.util.Queue;

import org.apache.commons.io.FilenameUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;

import edu.umn.msi.tropix.proteomics.DTAList;
import edu.umn.msi.tropix.proteomics.DTAList.Entry;
import edu.umn.msi.tropix.proteomics.conversion.DtaNameUtils;
import edu.umn.msi.tropix.proteomics.conversion.MzXMLToDTAConverter;
import edu.umn.msi.tropix.proteomics.conversion.MzXMLToDTAOptions;
import edu.umn.msi.tropix.proteomics.conversion.MzxmlParser;
import edu.umn.msi.tropix.proteomics.conversion.MzxmlParser.MzxmlInfo;
import edu.umn.msi.tropix.proteomics.conversion.Scan;

public class MzXMLToDTAConverterStreamingImpl implements MzXMLToDTAConverter {
  private static final String NEWLINE = "\n";
  private MzxmlParser mzxmlParser = new MzxmlParserImpl();

  private static final class EntryImpl implements Entry {
    private final byte[] contents;
    private final String name;

    private EntryImpl(final String name, final byte[] contents) {
      this.contents = contents;
      this.name = name;
    }

    public byte[] getContents() {
      return contents;
    }

    public String getName() {
      return name;
    }

  }

  private final class DTAListImpl extends UnmodifiableIterator<DTAList.Entry> implements DTAList {
    private final Iterator<Scan> scanIterator;
    private final String floatFormat;
    private Queue<Entry> cachedEntries = Lists.newLinkedList();

    private DTAListImpl(final Iterator<Scan> scanIterator, final String floatFormat) {
      this.scanIterator = scanIterator;
      this.floatFormat = floatFormat;
    }

    private void cacheEntries() {
      while(cachedEntries.isEmpty() && scanIterator.hasNext()) {
        final Scan scan = scanIterator.next();
        if(scan.getMsLevel() != 2) {
          // System.out.println("Skipping mslevel != 2");
          continue;
        }

        String fileName = scan.getParentFileName();
        final boolean fromDTA = DtaNameUtils.isDtaName(fileName);

        final double[] doubles = scan.getPeaks();
        if(ConversionUtils.isEmptyPeaks(doubles)) {
          // System.out.println("Skipping empty peaks");
          continue;
        }
        final float precursorMzValue = scan.getPrecursorMz();
        final short chargeState = scan.getPrecursorCharge();
        final short[] precursorCharges = ConversionUtils.getPrecusorCharges(chargeState, precursorMzValue, doubles);

        for(final int precursorCharge : precursorCharges) {
          final StringBuffer outputBuffer = new StringBuffer();
          final double precursorPeptideMass = precursorMzValue * precursorCharge - (precursorCharge - 1) * ConversionUtils.PROTON_MASS;
          final Formatter formatter = new Formatter(outputBuffer);
          formatter.format(floatFormat + "\t%d" + NEWLINE, precursorPeptideMass, precursorCharge);
          final int numPairs = doubles.length / 2;
          for(int i = 0; i < numPairs; i++) {
            formatter.format(floatFormat + "\t" + floatFormat + NEWLINE, doubles[2 * i], doubles[2 * i + 1]);
          }

          String dtaFileName;
          if(fromDTA) {
            dtaFileName = FilenameUtils.getName(fileName);
          } else {
            dtaFileName = FilenameUtils.getBaseName(fileName) + "." + scan.getNumber() + "." + scan.getNumber() + "." + precursorCharge + ".dta";
          }
          cachedEntries.add(new EntryImpl(dtaFileName, outputBuffer.toString().getBytes()));
        }
      }

    }

    public Iterator<Entry> iterator() {
      return this;
    }

    public boolean hasNext() {
      cacheEntries();
      return !cachedEntries.isEmpty();
    }

    public Entry next() {
      cacheEntries();
      final Entry entry = cachedEntries.remove();
      return entry;
    }

  }

  public DTAList mzxmlToDTA(final InputStream mzxmlStream, final MzXMLToDTAOptions inputOptions) {
    final MzXMLToDTAOptions options = inputOptions == null ? new MzXMLToDTAOptions() : inputOptions;
    final String floatFormat = "%" + ((options.getOutputPrecision() == null) ? "" : "." + options.getOutputPrecision()) + "f";
    final MzxmlInfo mzxmlInfo = mzxmlParser.parse(mzxmlStream);
    return new DTAListImpl(mzxmlInfo.iterator(), floatFormat);
  }

}
