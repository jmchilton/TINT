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

package edu.umn.msi.tropix.proteomics.itraqquantitation.impl;

import java.io.InputStreamReader;
import java.util.List;

import junit.framework.Assert;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.proteomics.conversion.DtaNameUtils.DtaNameSummary;
import edu.umn.msi.tropix.proteomics.test.ProteomicsTests;

public class ScaffoldReportParserImplTest {

  @Test(groups = "unit")
  public void testScaffoldReportParse() {
    final ScaffoldReportParserImpl parser = new ScaffoldReportParserImpl();
    final List<ReportEntry> entries = parser.parse(new InputStreamReader(ProteomicsTests.getResourceAsStream("test_report.xls")));
    final ReportEntry entry1 = entries.get(0);
    assert entry1.getProteinAccession().equals("YPL237W");
    assert entry1.getProteinProbability() == Double.parseDouble("77.90");
    assert entry1.getPeptideSequence().equals("sVSADAEAEkEPTDDIAEALGELSLk");
    assert entry1.getPeptideProbability() == Double.parseDouble("95.00");
    assert ((ScaffoldEntry) entry1).getScanAlt() == 6496;
    assert entry1.getScanNumber() == 6496;
    assert entry1.getScanCharge() == 4;
    assert entry1.getSpectraId().equals("PQD_31");
    assert entries.size() == 1405;
  }

  @Test(groups = "unit")
  public void testXTandemSpectrumNameParse() {
    final DtaNameSummary summary = ScaffoldReportParserImpl.getNameSummary("data.mzxml scan 6033 (charge 3)");
    Assert.assertEquals("data", summary.getBasename());
    assert summary.getEnd() == 6033;
    assert summary.getCharge() == 3;
  }

}
