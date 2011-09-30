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

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import net.sourceforge.sashimi.mzxml.v3_0.MsRun;
import net.sourceforge.sashimi.mzxml.v3_0.MzXML;
import net.sourceforge.sashimi.mzxml.v3_0.Scan;
import net.sourceforge.sashimi.mzxml.v3_0.Software;
import net.sourceforge.sashimi.mzxml.v3_0.MsRun.DataProcessing;
import net.sourceforge.sashimi.mzxml.v3_0.MsRun.ParentFile;

import org.springframework.util.StringUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.proteomics.test.ProteomicsTests;
import edu.umn.msi.tropix.proteomics.xml.MzXMLUtility;

public class MgfToMzXMLConverterStreamingImplTest {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private static final MzXMLUtility MZXML_UTILITY = new MzXMLUtility();

  private File mgfFile = null;
  private File mzxmlFile = null; 
  private OutputStream currentOutputStream = null;
  private InputStream currentInputStream = null;
  
  @BeforeMethod(groups = {"unit", "integration"})
  public void setupFilesAndStreams() {
    mgfFile = FILE_UTILS.createTempFile("tpx", ".mgf");
    mzxmlFile = FILE_UTILS.createTempFile("tpx", "mzXML");
    final OutputStream outputStream = FILE_UTILS.getFileOutputStream(mgfFile);
    try {
      FILE_UTILS.writeStreamToFile(mgfFile, ProteomicsTests.getResourceAsStream("readw.mgf-shortened"));
    } finally {
      IO_UTILS.closeQuietly(outputStream);
    }
  }


  @AfterMethod(groups = {"unit", "integration"})
  public void cleanUpFilesAndStreams() {
    IO_UTILS.closeQuietly(currentOutputStream);
    IO_UTILS.closeQuietly(currentInputStream);
    FILE_UTILS.deleteQuietly(mgfFile);
    FILE_UTILS.deleteQuietly(mzxmlFile);
  }
  
  @Test(groups = "integration")
  public void testRoundTrip() {
    convertMgfToMzxml();
    
    final MzXMLToMGFConverterStreamingImpl converter = new MzXMLToMGFConverterStreamingImpl();
    currentInputStream = FILE_UTILS.getFileInputStream(mzxmlFile);
    final File mgfFile2 = FILE_UTILS.createTempFile();
    InputStream mgfStream1 = null, mgfStream2 = null;
    try {
      currentOutputStream = FILE_UTILS.getFileOutputStream(mgfFile2);
      converter.mzxmlToMGF(currentInputStream, currentOutputStream, null);

      mgfStream1 = FILE_UTILS.getFileInputStream(mgfFile);
      mgfStream2 = FILE_UTILS.getFileInputStream(mgfFile2);

      assertMgsAreSimilar(mgfStream1, mgfStream2);
    } finally {
      FILE_UTILS.deleteQuietly(mgfFile2);
      IO_UTILS.closeQuietly(mgfStream1);
      IO_UTILS.closeQuietly(mgfStream2);
    }
  }


  private void assertMgsAreSimilar(final InputStream mgfStream1, final InputStream mgfStream2) {
    final Iterator<edu.umn.msi.tropix.proteomics.conversion.Scan> scanIterator1 = new MgfParser().parserMgf(mgfStream1);
    final Iterator<edu.umn.msi.tropix.proteomics.conversion.Scan> scanIterator2 = new MgfParser().parserMgf(mgfStream2);

    int scanNumber = 0;
    while(scanIterator1.hasNext()) {
      scanNumber++;
      final edu.umn.msi.tropix.proteomics.conversion.Scan scan1 = scanIterator1.next();
      assert scanIterator2.hasNext() : "MGF File Converted back from MzXML has fewer scans";
      
      final edu.umn.msi.tropix.proteomics.conversion.Scan scan2 = scanIterator2.next();
      
      assert scan1.getNumber() == scan2.getNumber() : String.format("Expected %d, obtained %d", scan1.getNumber(), scan2.getNumber());
      assert scan1.getAlt() == scan2.getAlt() : String.format("Expected %d, obtained %d", scan1.getAlt(), scan2.getAlt());
      assert scan1.getMsLevel() == scan2.getMsLevel();        
      //assert scan1.getParentFileName().equals(scan2.getParentFileName()) : String.format("Expect %s, found %s", scan1.getParentFileName(), scan2.getParentFileName());
      assert scan1.getPrecursorCharge() == scan2.getPrecursorCharge();
      assert scan1.getPrecursorIntensity() == scan2.getPrecursorIntensity()
        : String.format("Expecting %f, found %f for intensity on scan #%d", scan1.getPrecursorIntensity(), scan2.getPrecursorIntensity(), scanNumber);
      assert scan1.getPrecursorMz() == scan2.getPrecursorMz() 
        : String.format("Expecting %f, found %f on scan #%d - scan1 is %s, scan2 is %s", scan1.getPrecursorMz(), scan2.getPrecursorMz(), scanNumber, scan1, scan2);
    }
    assert !scanIterator2.hasNext();
  }
  
  private void convertMgfToMzxml() {
    currentOutputStream = FILE_UTILS.getFileOutputStream(mzxmlFile);
    final MgfToMzXMLConverterStreamingImpl converter = new MgfToMzXMLConverterStreamingImpl();
    converter.mgfToMzXmxl(mgfFile, currentOutputStream);
    IO_UTILS.flush(currentOutputStream);
    IO_UTILS.closeQuietly(currentOutputStream);
  }

  @Test(groups = "unit")
  public void testConversion() {
    convertMgfToMzxml();
    final MzXML mzxml = MZXML_UTILITY.deserialize(mzxmlFile);
    // Getting through the test to here, verifies the XML structure of the resulting file.

    final MsRun msRun = mzxml.getMsRun();
    final DataProcessing processing = Iterables.getOnlyElement(msRun.getDataProcessing());
    final Software software = processing.getSoftware();
    assert software.getName().equals("ProTIP");
    assert StringUtils.hasText(software.getVersion());
    assert StringUtils.hasText(software.getType());

    final ParentFile parentFile = Iterables.getOnlyElement(msRun.getParentFile());
    assert StringUtils.hasText(parentFile.getFileSha1());
    assert parentFile.getFileName().equals(mgfFile.getName());
    //assert parentFile.getFileType().equals("mgf");

    assert mzxml.getIndexOffset().equals(0L);

    for(final Scan scan : mzxml.getMsRun().getScan()) {
      assert scan.getPeaks().size() > 0;
    }
  }

}
