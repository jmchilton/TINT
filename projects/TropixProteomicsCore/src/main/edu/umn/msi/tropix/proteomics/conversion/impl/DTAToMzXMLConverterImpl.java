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

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;

import net.sourceforge.sashimi.mzxml.v3_0.MsRun;
import net.sourceforge.sashimi.mzxml.v3_0.MzXML;
import net.sourceforge.sashimi.mzxml.v3_0.ObjectFactory;
import net.sourceforge.sashimi.mzxml.v3_0.Scan;
import net.sourceforge.sashimi.mzxml.v3_0.Software;
import edu.umn.msi.tropix.proteomics.DTAList;
import edu.umn.msi.tropix.proteomics.conversion.DTAToMzXMLConverter;
import edu.umn.msi.tropix.proteomics.conversion.DTAToMzXMLOptions;
import edu.umn.msi.tropix.proteomics.utils.DTAUtils;

public class DTAToMzXMLConverterImpl implements DTAToMzXMLConverter {

  public MzXML dtaToMzXML(final DTAList dtaList, final DTAToMzXMLOptions inputOptions) {
    final DTAToMzXMLOptions options = inputOptions == null ? new DTAToMzXMLOptions() : inputOptions;
    final MsRun run = new MsRun();

    final MsRun.DataProcessing conversionProcessing = new MsRun.DataProcessing();
    final Software conversionSoftware = new Software();
    conversionSoftware.setType("conversion");
    conversionSoftware.setName("Tropix");
    conversionSoftware.setVersion("1.0+");
    conversionProcessing.setSoftware(conversionSoftware);

    run.getDataProcessing().add(conversionProcessing);
    final ObjectFactory factory = new ObjectFactory();
    final List<Scan> scanList = run.getScan();
    // Iterator<DTAList.Entry> entryIterator = dtaList.iterator();
    // Iterator<byte[]> contentsIterator = dtaList.getContentsIterator();
    // Iterator<String> namesIterator = dtaList.getNamesIterator();
    int i = 0;
    for(final DTAList.Entry entry : dtaList) {
      i++;
      final byte[] contents = entry.getContents();
      final String name = entry.getName();

      final Matcher dtaFileNameMatcher = DTAUtils.DTA_NAME_PATTERN.matcher(name);

      final MsRun.ParentFile parent = factory.createMsRunParentFile();
      final String sha1 = ConversionUtils.getSHA1(contents);
      parent.setFileName(name);
      parent.setFileSha1(sha1);
      parent.setFileType("processedData");
      run.getParentFile().add(parent);

      final Scan scan = new Scan();
      if(dtaFileNameMatcher.matches()) {
        scan.setNum(new BigInteger(dtaFileNameMatcher.group(3)));
      } else {
        throw new IllegalArgumentException("dta file name is invalid " + name);
      }

      scan.setMsLevel(BigInteger.valueOf(2));
      final Scan.ScanOrigin origin = factory.createScanScanOrigin();
      origin.setParentFileID(sha1);
      origin.setNum(BigInteger.valueOf(i + 1));
      scan.getScanOrigin().add(origin);

      final Scan.Peaks peaks = factory.createScanPeaks();
      peaks.setByteOrder("network");
      peaks.setContentType("m/z-int");
      peaks.setCompressionType("none");

      byte[] encodedPeaks;
      if(options.getDoublePrecision()) {
        final double[] doubles = DTAUtils.readDtaDoublePairs(contents);
        encodedPeaks = ConversionUtils.doubles2bytes(doubles);
        scan.setPeaksCount(BigInteger.valueOf(doubles.length / 2));
        peaks.setPrecision(new java.math.BigInteger("64"));
      } else {
        final float[] floats = DTAUtils.readDtaFloatPairs(contents);
        encodedPeaks = ConversionUtils.floats2bytes(floats);
        scan.setPeaksCount(BigInteger.valueOf(floats.length / 2));
        peaks.setPrecision(new java.math.BigInteger("32"));
      }
      peaks.setValue(encodedPeaks);
      // TODO: The following length is just a guess I think.
      // It might be off by 1-3 (investigate this).
      peaks.setCompressedLen(encodedPeaks.length * 4 / 3);
      final Scan.PrecursorMz precursorMz = factory.createScanPrecursorMz();

      final Scanner scanner = new Scanner(new ByteArrayInputStream(contents));
      final float precursorPeptideMass = scanner.nextFloat();
      final int precursorCharge = scanner.nextInt();
      final float precursorMzValue = (precursorPeptideMass + (precursorCharge - 1) * ConversionUtils.PROTON_MASS) / precursorCharge;
      precursorMz.setPrecursorIntensity(0.0f); // Don't know how to calculate this from a DTA file, may not be possible
      precursorMz.setValue(precursorMzValue);
      precursorMz.setPrecursorCharge(BigInteger.valueOf(precursorCharge));
      scan.getPrecursorMz().add(precursorMz);
      scan.getPeaks().add(peaks);
      scanList.add(scan);
    }

    final MzXML mzxml = new MzXML();
    mzxml.setIndexOffset(0L); // Set to 0 to indicate not using it.
    mzxml.setMsRun(run);
    return mzxml;
  }

}
