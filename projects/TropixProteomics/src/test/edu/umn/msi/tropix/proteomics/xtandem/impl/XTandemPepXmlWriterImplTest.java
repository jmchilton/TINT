package edu.umn.msi.tropix.proteomics.xtandem.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.OutputContexts;
import edu.umn.msi.tropix.common.test.IOTest;
import edu.umn.msi.tropix.common.xml.XMLUtility;
import edu.umn.msi.tropix.proteomics.bioml.Bioml;
import edu.umn.msi.tropix.proteomics.pepxml.EngineType;
import edu.umn.msi.tropix.proteomics.pepxml.MsmsPipelineAnalysis;
import edu.umn.msi.tropix.proteomics.pepxml.MsmsPipelineAnalysis.MsmsRunSummary;
import edu.umn.msi.tropix.proteomics.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SampleEnzyme;
import edu.umn.msi.tropix.proteomics.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary;
import edu.umn.msi.tropix.proteomics.pepxml.NameValueType;
import edu.umn.msi.tropix.proteomics.test.ProteomicsTests;
import edu.umn.msi.tropix.proteomics.xtandem.impl.XTandemPepXmlWriterImpl.XTandemPepXmlWriterOptions;

public class XTandemPepXmlWriterImplTest extends IOTest {
  private static final XMLUtility<MsmsPipelineAnalysis> XML_UTILITY = new XMLUtility<MsmsPipelineAnalysis>(MsmsPipelineAnalysis.class);
  private static final XMLUtility<Bioml> BIOML_XML_UTILITY = new XMLUtility<Bioml>(Bioml.class);
  private File biomlResultsFile;
  private MsmsPipelineAnalysis analysis;
  private MsmsRunSummary runSummary;
  private SearchSummary searchSummary;
  private String xml;
    
  @BeforeMethod(groups = "unit")
  public void init() {
    biomlResultsFile = getTempFile();

    final InputStream biomlResultsInputStream = ProteomicsTests.getResourceAsStream("tandem-output-truncated.xml");
    final InputStream biomlParametersInputStream = ProteomicsTests.getResourceAsStream("tandem-parameters-bioml.xml");
    registerCloseable(biomlResultsInputStream);
    registerCloseable(biomlParametersInputStream);
    final Bioml bioml = BIOML_XML_UTILITY.deserialize(biomlParametersInputStream);
    OutputContexts.forFile(biomlResultsFile).put(biomlResultsInputStream);

    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    XTandemPepXmlWriterOptions options = new XTandemPepXmlWriterOptions(outputStream, biomlResultsInputStream, bioml);
    new XTandemPepXmlWriterImpl(options).write();
    xml = new String(outputStream.toByteArray());
    analysis = XML_UTILITY.fromString(xml);
    runSummary = analysis.getMsmsRunSummary().get(0);
    searchSummary =  runSummary.getSearchSummary().get(0);
  }
  
  @Test(groups = "unit")
  public void testRaw() {
    assert runSummary.getRawDataType().equals("raw");
    assert runSummary.getRawData().equals(".mzxml");
  }
  
  @Test(groups = "unit")
  public void testSearchEngine() {    
    assert searchSummary.getSearchEngine().equals(EngineType.X_TANDEM);    
  }
  
  @Test(groups = "unit")
  public void testEnzymeNotNull() {
    final SampleEnzyme sampleEnzyme = runSummary.getSampleEnzyme();
    assert sampleEnzyme != null;
    assert sampleEnzyme.getName() != null : xml;
    assert sampleEnzyme.getName().equals("trypsin") : xml;
  }

  @Test(groups = "unit")
  public void testParameters() {
    String histogramValue = getParameterValue(searchSummary.getParameter(), "output, histogram column width");
    assert histogramValue.equals("50") : histogramValue;
  }
  
  private String getParameterValue(final Iterable<NameValueType> nameValueTypes, final String name) {
    String value = null;
    for(final NameValueType parameter : searchSummary.getParameter()) {
      if(parameter.getName().equals(name)) {
        value = parameter.getTheValue();
        break;
      }
    }
    return value;    
  }
  
  
  
}
