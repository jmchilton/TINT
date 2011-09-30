package edu.umn.msi.tropix.jobs.activities.factories;

import java.util.UUID;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateMergedIdentificationParametersDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.IdList;
import edu.umn.msi.tropix.jobs.activities.descriptions.ScaffoldQuantativeSample;
import edu.umn.msi.tropix.jobs.activities.descriptions.ScaffoldSample;
import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.persistence.service.AnalysisService;

public class CreateMergeIdentificationAnalyisFactoryImplTest {
  private AnalysisService analysisService;

  protected AnalysisService getAnalysisService() {
    return analysisService;
  }

  public CreateMergeIdentificationAnalyisFactoryImplTest() {
    super();
  }

  @BeforeMethod(groups = "unit")
  public void initAnalysisService() {
    analysisService = EasyMock.createMock(AnalysisService.class);
  }

  protected void expectAnalysisIdsAndReturnDatabases(final ActivityContext context, final Database[] databases) {
    analysisService
        .getIdentificationDatabases(EasyMock.eq(context.getCredential().getIdentity()), EasyMock.aryEq(new String[] {"123", "223", "323"}));
    EasyMock.expectLastCall().andStubReturn(databases);
    EasyMockUtils.replayAll(analysisService);
  }

  protected void verifyIdentificationAnalysisIds(final CreateMergedIdentificationParametersDescription description) {
    assert description.getIdentificationAnalysisIds().toList().get(0).equals("123");
    assert description.getIdentificationAnalysisIds().toList().get(1).equals("223");
    assert description.getIdentificationAnalysisIds().toList().get(2).equals("323");
  }

  protected IdentificationAnalysis[] createIdentificationAnalyses(final String storageServiceUrl) {
    final IdentificationAnalysis[] analyses = new IdentificationAnalysis[3];
    for(int i = 1; i <= 3; i++) {
      final IdentificationAnalysis analysis = new IdentificationAnalysis();
      analysis.setName("name" + i + "23");
      analysis.setIdentificationProgram("SequestBean");
      analysis.setId(i + "23");
      final TropixFile tropixFile = new TropixFile();
      tropixFile.setId(UUID.randomUUID().toString());
      tropixFile.setFileId(i + "");
      tropixFile.setStorageServiceUrl(storageServiceUrl);
      analysis.setOutput(tropixFile);
      analyses[i - 1] = analysis;
    }
    return analyses;
  }

  protected void populateScaffoldSamples(final CreateMergedIdentificationParametersDescription description) {
    final ScaffoldSample sample1 = new ScaffoldSample();
    sample1.setIdentificationAnalysisIds(IdList.forIterable(Lists.newArrayList("123", "223")));
    sample1.setSampleName("Sample1");
    sample1.setCategory("cat1");
    sample1.setAnalyzeAsMudpit(true);

    final ScaffoldQuantativeSample quantSample = new ScaffoldQuantativeSample();
    quantSample.setName("qname");
    quantSample.setCategory("qcat");
    quantSample.setPrimary(true);
    quantSample.setReporter("114");
    quantSample.setDescription("qdescription");
    sample1.setQuantitativeSamples(Lists.newArrayList(quantSample));
    sample1.setQuantitativeModelType("iTRAQ 4-Plex");
    sample1.setQuantitativeModelPurityCorrection("0.0,1.0,0.0");

    final ScaffoldSample sample2 = new ScaffoldSample();
    sample2.setIdentificationAnalysisIds(IdList.forIterable(Lists.newArrayList("323")));
    sample2.setSampleName("Sample2");
    sample2.setCategory("cat2");
    sample2.setAnalyzeAsMudpit(false);

    description.addScaffoldSample(sample1);
    description.addScaffoldSample(sample2);
  }

}