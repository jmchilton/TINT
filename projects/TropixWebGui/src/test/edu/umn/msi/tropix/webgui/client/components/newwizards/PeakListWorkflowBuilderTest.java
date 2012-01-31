package edu.umn.msi.tropix.webgui.client.components.newwizards;

import java.util.List;

import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.client.services.GridService;
import edu.umn.msi.tropix.client.services.QueueGridService;
import edu.umn.msi.tropix.jobs.activities.WorkflowVerificationUtils;
import edu.umn.msi.tropix.jobs.activities.descriptions.CommonMetadataProvider;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateFolderDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateProteomicsRunDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateTropixFileDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.models.utils.StockFileExtensionEnum;
import edu.umn.msi.tropix.models.utils.StockFileExtensionI;
import edu.umn.msi.tropix.webgui.client.components.UploadComponentFactory.FileSource;
import edu.umn.msi.tropix.webgui.client.components.newwizards.ProteomicsRunSourceTypeWizardPageImpl.ProteomicsRunSource;
import edu.umn.msi.tropix.webgui.client.constants.ComponentConstants;
import edu.umn.msi.tropix.webgui.client.constants.ConstantProxies;
import edu.umn.msi.tropix.webgui.client.utils.Properties;
import edu.umn.msi.tropix.webgui.client.utils.Property;

public class PeakListWorkflowBuilderTest extends BaseWorkflowBuilderTest<PeakListWorkflowBuilder> {
  private List<FileSource> fileSources = Lists.newArrayList();
  private Property<Boolean> batchProperty;
  private Property<ProteomicsRunSource> sourceProperty;
  private MockServiceWizardPageImpl<QueueGridService> proteomicsConvertGridServicePage;
  private MockServiceWizardPageImpl<QueueGridService> rawExtractGridServicePage;
  private PeakListSourceTypeWizardPage sourcePage;

  @BeforeMethod(groups = "unit")
  public void init() {
    PeakListWorkflowBuilder builder = setWorkflowBuilder(new PeakListWorkflowBuilder(ConstantProxies.getProxy(ComponentConstants.class)));
    fileSources.clear();
    builder.setFileSources(fileSources);
    CommonMetadataProvider metadataProvider = new TestCommonMetadataProvider();
    builder.setCommonMetadataProvider(metadataProvider);
    sourcePage = EasyMock.createMock(PeakListSourceTypeWizardPage.class);
    batchProperty = Properties.newProperty();
    batchProperty.set(false);
    sourceProperty = Properties.newProperty();
    EasyMock.expect(sourcePage.getBatchProperty()).andStubReturn(batchProperty);
    EasyMock.expect(sourcePage.getProteomicsRunSourceProperty()).andStubReturn(sourceProperty);
    builder.setSourcePage(sourcePage);
    proteomicsConvertGridServicePage = MockServiceWizardPageImpl.getNew(QueueGridService.class);
    rawExtractGridServicePage = MockServiceWizardPageImpl.getNew(QueueGridService.class);
    builder.setProteomicsConvertServicesSelectionPage(proteomicsConvertGridServicePage);
    builder.setThermoServicesSelectionPage(rawExtractGridServicePage);
    EasyMock.replay(sourcePage);
  }

  @Test(groups = "unit")
  public void testSingleRawExtractUpload() {
    setThermo();
    buildAndVerify();
    verifyCreatesSinglePeakList();
    assertCreatesFileOfType(StockFileExtensionEnum.THERMO_RAW);
    assertDoesntCreateFolder();
    checkJobDescriptions();
  }

  @Test(groups = "unit")
  public void testSingleRawExtractExisting() {
    setSourceType(ProteomicsRunSource.THERMO);
    fileSources.add(TestFileSources.testExistingWithName("mrr.RAW"));
    buildAndVerify();
    verifyCreatesSinglePeakList();
    assertDoesntCreateFolder();
    Assert.assertFalse(createsFileWithType(StockFileExtensionEnum.THERMO_RAW));
    checkJobDescriptions();
  }

  @Test(groups = "unit")
  public void testMultipleRawExtractUpload() {
    batch();
    setThermo();
    buildAndVerify();
    assertCreatesFolder();
  }

  @Test(groups = "unit")
  public void testSingleMgfConvert() {
    setMgf();
    buildAndVerify();
    verifyCreatesSinglePeakList();
    assertCreatesFileOfType(StockFileExtensionEnum.MASCOT_GENERIC_FORMAT);
    assertDoesntCreateFolder();
    checkJobDescriptions();
  }

  @Test(groups = "unit")
  public void testSingleMgfExisting() {
    setSourceType(ProteomicsRunSource.MGF);
    fileSources.add(TestFileSources.testExistingWithName("mrr.mgf"));
    buildAndVerify();
    verifyCreatesSinglePeakList();
    assertDoesntCreateFolder();
    Assert.assertFalse(createsFileWithType(StockFileExtensionEnum.MASCOT_GENERIC_FORMAT));
    checkJobDescriptions();
  }

  @Test(groups = "unit")
  public void testMultipleMgfConvert() {
    batch();
    setMgf();
    buildAndVerify();
    assertCreatesFolder();
  }

  @Test(groups = "unit")
  public void testSingleMzxml() {
    setMzxml();
    buildAndVerify();
    verifyCreatesSinglePeakList();
    assertDoesntCreateFolder();
    checkJobDescriptions();
  }

  @Test(groups = "unit")
  public void testSingleMzxmlExisting() {
    setSourceType(ProteomicsRunSource.MZXML);
    fileSources.add(TestFileSources.testExistingWithName("mrr.mzxml"));
    buildAndVerify();
    verifyCreatesSinglePeakList();
    assertDoesntCreateFolder();
    Assert.assertFalse(createsFileWithType(StockFileExtensionEnum.MZXML));
    checkJobDescriptions();
  }

  @Test(groups = "unit")
  public void testMultipleMzxml() {
    batch();
    setMzxml();
    buildAndVerify();
    assertCreatesFolder();
  }

  protected void buildAndVerify() {
    super.buildAndVerify();
  }

  private void checkJobDescriptions() {
    final JobDescription jobDescription = getDescriptionOfType(CreateProteomicsRunDescription.class).getJobDescription();
    assert jobDescription != null;
    WorkflowVerificationUtils.assertHasJobDescription(jobDescription, getDescriptionsOfType(CreateTropixFileDescription.class));
    WorkflowVerificationUtils.assertHasJobDescription(jobDescription, getDescriptionsOfType(CreateProteomicsRunDescription.class));
  }

  private void batch() {
    batchProperty.set(true);
  }

  private void setMgf() {
    setType(ProteomicsRunSource.MGF, StockFileExtensionEnum.MASCOT_GENERIC_FORMAT);
  }

  private void setThermo() {
    setType(ProteomicsRunSource.THERMO, StockFileExtensionEnum.THERMO_RAW);
  }

  private void setMzxml() {
    setType(ProteomicsRunSource.MZXML, StockFileExtensionEnum.MZXML);
  }

  private void setType(final ProteomicsRunSource type, final StockFileExtensionI extension) {
    setSourceType(type);
    fileSources.add(TestFileSources.testUploadWithName(String.format("mrr%s", extension.getExtension())));
    if(batchProperty.get()) {
      fileSources.add(TestFileSources.testUploadWithName(String.format("mrr2%s", extension.getExtension())));
    }
  }

  private void assertCreatesFolder() {
    assert !getDescriptionsOfType(CreateFolderDescription.class).isEmpty();
  }

  private void assertDoesntCreateFolder() {
    assert getDescriptionsOfType(CreateFolderDescription.class).isEmpty();
  }

  private void verifyCreatesSinglePeakList() {
    assertBuiltNDescriptionsOfType(1, CreateProteomicsRunDescription.class);
    final CreateProteomicsRunDescription runDescription = getDescriptionOfType(CreateProteomicsRunDescription.class);
    Assert.assertEquals(runDescription.getName(), "test name");
  }

  private void assertCreatesFileOfType(final StockFileExtensionI extension) {
    Assert.assertTrue(createsFileWithType(extension));
  }

  private boolean createsFileWithType(final StockFileExtensionI extension) {
    boolean foundType = false;
    for(final CreateTropixFileDescription fileDescription : getDescriptionsOfType(CreateTropixFileDescription.class)) {
      if(extension.getExtension().equals(fileDescription.getExtension())) {
        foundType = true;
      }
    }
    return foundType;
  }

  private void setSourceType(final ProteomicsRunSource sourceType) {
    this.sourceProperty.set(sourceType);
  }

  public static class MockServiceWizardPageImpl<T extends GridService> implements ServiceWizardPage<T> {
    private T gridService;

    MockServiceWizardPageImpl(final T gridService) {
      this.gridService = gridService;
    }

    public T getGridService() {
      return gridService;
    }

    public void setGridService(final T gridService) {
      this.gridService = gridService;
    }

    static <T extends GridService> MockServiceWizardPageImpl<T> getNew(Class<T> clazz) {
      return new MockServiceWizardPageImpl<T>(TestGridServices.getTestService(clazz));
    }

  }

}
