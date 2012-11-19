package edu.umn.msi.tropix.proteomics.report;

import java.io.InputStream;
import java.util.Iterator;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.proteomics.report.SearchReportParser.SpectrumReport;
import edu.umn.msi.tropix.proteomics.test.ProteomicsTests;

public class PepXmlParserImplTest {

  @Test(groups = "unie")
  public void testSimpleParsing() {
    PepXmlParserImpl parser = new PepXmlParserImpl();
    final InputStream pepXmlStream = ProteomicsTests.getResourceAsStream("minimalPepXml.xml");
    final Iterator<SpectrumReport> spectraMatches = parser.parse(pepXmlStream);
    final SpectrumReport firstSpectraReport = spectraMatches.next();
    System.out.println(firstSpectraReport);
    assert firstSpectraReport.getSpectrumName().equals("dataset_200.dat.01635.01635.2");
  }
}
