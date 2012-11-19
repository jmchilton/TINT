package edu.umn.msi.tropix.proteomics.itraqquantitation.impl;

import java.io.InputStream;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.ReportParser.ReportType;
import edu.umn.msi.tropix.proteomics.test.ProteomicsTests;

public class ReportParserImplTest {

  @Test(groups = "unit")
  public void test() {
    ReportParserImpl parser = new ReportParserImpl();
    final InputStream pepXmlStream = ProteomicsTests.getResourceAsStream("minimalPepXml.xml");
    final List<ReportEntry> reports = parser.parse(pepXmlStream, ReportType.PEPXML);
    final ReportEntry entry1 = reports.get(0);
    Assert.assertEquals("dataset_200.dat", entry1.getSpectraId());
  }

}
