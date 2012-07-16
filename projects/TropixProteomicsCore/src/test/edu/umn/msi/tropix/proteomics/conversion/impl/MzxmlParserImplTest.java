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

import java.io.IOException;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import org.testng.Assert;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.proteomics.conversion.MzxmlParser.MzxmlInfo;
import edu.umn.msi.tropix.proteomics.conversion.Scan;
import edu.umn.msi.tropix.proteomics.test.ProteomicsTests;

public class MzxmlParserImplTest {

  @Test(groups = "unit")
  public void bigScanNotTruncated() {
    assert getFirstScan("bigscan.mzxml").getPeaks().length == 5000;
  }

  @Test(groups = "unit")
  public void testParseRetentionTime() {
    Assert.assertEquals(getFirstScan("bigscan.mzxml").getRt(), 300058L);
  }

  @Test(groups = "unit")
  public void testOkWhenNoRetentionTime() {
    final Scan scan = getFirstScan("parentPerScan.mzxml");
    System.out.println(scan.getRt());
    Assert.assertFalse(scan.isRtSet());
  }

  @Test(groups = "unit")
  public void parentFilesPerScan() {
    final Iterator<Scan> scanIterator = getScans("parentPerScan.mzxml");
    final Scan firstScan = scanIterator.next();
    assert firstScan.getParentFileName().equals("mrr.103.106.1.dta");
    assert firstScan.getParentName().equals("mrr");
    final Scan secondScan = scanIterator.next();
    assert secondScan.getParentFileName().equals("mrr.2025.2026.3.dta");
    assert secondScan.getParentName().equals("mrr");
  }

  @Test(groups = "unit")
  public void chargeFromParent() {
    final Iterator<Scan> scanIterator = getScans("parentPerScan.mzxml");
    final Scan firstScan = scanIterator.next();
    assert firstScan.getPrecursorCharge() == 1; // mrr.103.106.1.dta
    final Scan secondScan = scanIterator.next();
    assert secondScan.getPrecursorCharge() == 3; // mrr.2025.2026.3.dta
  }

  @Test(groups = "unit")
  public void chargeFromFile() {
    final Iterator<Scan> scanIterator = getScans("parentPerRun.mzxml");
    final Scan firstScan = scanIterator.next();
    assert firstScan.getPrecursorCharge() == 1;
    final Scan secondScan = scanIterator.next();
    assert secondScan.getPrecursorCharge() == 3;
  }

  @Test(groups = "unit")
  public void numberAndAltFromDta() {
    final Iterator<Scan> scanIterator = getScans("parentPerScan.mzxml");
    final Scan firstScan = scanIterator.next();
    assert firstScan.getNumber() == 106; // "mrr.103.106.1.dta";
    assert firstScan.getAlt() == 103;
    final Scan secondScan = scanIterator.next();
    assert secondScan.getNumber() == 2026; // "mrr.2025.2026.3.dta";
    assert secondScan.getAlt() == 2025;
  }

  @Test(groups = "unit")
  public void numberAndAlt() {
    final Iterator<Scan> scanIterator = getScans("parentPerRun.mzxml");
    final Scan firstScan = scanIterator.next();
    assert firstScan.getNumber() == 106;
    assert firstScan.getAlt() == 106;
    final Scan secondScan = scanIterator.next();
    assert secondScan.getNumber() == 2026;
    assert secondScan.getAlt() == 2026;
  }

  @Test(groups = "unit")
  public void iteratorHasNext() {
    final Iterator<Scan> scanIterator = getScans("parentPerRun.mzxml");
    assert scanIterator.hasNext();
    scanIterator.next();
    assert scanIterator.hasNext();
    scanIterator.next();
    assert !scanIterator.hasNext();
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void invalidParent() {
    final Iterator<Scan> scanIterator = getScans("invalidParent.mzxml");
    scanIterator.next();
  }

  @Test(groups = "unit")
  public void parentFilesPerRun() {
    final Iterator<Scan> scanIterator = getScans("parentPerRun.mzxml");
    final Scan firstScan = scanIterator.next();
    assert firstScan.getParentFileName().equals("mrr.RAW");
    assert firstScan.getParentName().equals("mrr");
    final Scan secondScan = scanIterator.next();
    assert secondScan.getParentFileName().equals("mrr.RAW");
    assert secondScan.getParentName().equals("mrr");
  }

  @Test(groups = "unit")
  public void parseTest() throws IOException, XMLStreamException {
    final Iterator<Scan> scanIterator = getScans("readw.mzXML");
    final Scan firstScan = scanIterator.next();
    assert firstScan.getMsLevel() == 1;
    final Scan secondScan = scanIterator.next();
    assert secondScan.getMsLevel() == 2;
  }

  private Iterator<Scan> getScans(final String resourceName) {
    final MzxmlParserImpl parser = new MzxmlParserImpl();
    final MzxmlInfo info = parser.parse(ProteomicsTests.getResourceAsStream(resourceName));
    return info.iterator();
  }

  private Scan getFirstScan(final String resourceName) {
    final Iterator<Scan> scanIterator = getScans(resourceName);
    final Scan firstScan = scanIterator.next();
    return firstScan;
  }

}
