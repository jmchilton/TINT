package edu.umn.msi.tropix.galaxy;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Iterables;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.jobqueue.client.JobClientFactory;
import edu.umn.msi.tropix.common.jobqueue.client.JobClientFactoryManager;
import edu.umn.msi.tropix.common.jobqueue.test.IntegrationTestBase;
import edu.umn.msi.tropix.credential.types.CredentialResource;
import edu.umn.msi.tropix.galaxy.inputs.RootInput;
import edu.umn.msi.tropix.galaxy.service.GalaxyJobQueueContext;
import edu.umn.msi.tropix.galaxy.test.TestDataExtracter;
import edu.umn.msi.tropix.galaxy.test.TestDataExtracter.TestData;
import edu.umn.msi.tropix.galaxy.test.TestDataExtracter.TestData.TestInputFile;
import edu.umn.msi.tropix.galaxy.tool.Tool;
import edu.umn.msi.tropix.galaxy.tool.repository.GalaxyToolRepository;
import edu.umn.msi.tropix.galaxy.xml.GalaxyXmlUtils;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.storage.client.StorageData;
import edu.umn.msi.tropix.transfer.types.TransferResource;

@ContextConfiguration(locations = "GalaxyIntegrationTest-context.xml")
public class CatGalaxyIntegrationTest extends IntegrationTestBase {
  private Credential credential = Credentials.getMock();
  private String serviceUrl = "local://Galaxy";
  private Class<GalaxyJobQueueContext> jobQueueContextClass = GalaxyJobQueueContext.class;
  
  @Inject
  private GalaxyToolRepository testToolSource;
  
  @Inject 
  private TestDataExtracter testDataExtractor;
  
  @Inject
  private JobClientFactoryManager jobClientFactoryManager;

  private GalaxyJobQueueContext context;
  
  protected GalaxyJobQueueContext createContext() {
    final JobClientFactory jobClientFactory = jobClientFactoryManager.getFactory(serviceUrl);
    return jobClientFactory.createJobContext(credential, serviceUrl, jobQueueContextClass);
  }
  
  @BeforeMethod(groups = "spring")
  public void init() {
    context = createContext();
  }
  
  @Test(groups = "spring")
  public void testLoad() throws InterruptedException {
    final Tool tool = testToolSource.loadForToolId("cat1");
    final List<TestData> testDataList = testDataExtractor.getTestCases("cat1");
    for(final TestData testData : testDataList) {
      super.initJobIntegration();
      final List<TestInputFile> inputFiles = testData.getInputFiles();
      final List<String> inputFileNames = Lists.newArrayList();
      final List<TransferResource> resources = Lists.newArrayList();
      for(final TestInputFile inputFile : inputFiles) {
        final String testFileName = inputFile.getInputFileName();
        inputFileNames.add(testFileName);
        resources.add(getReference(new ByteArrayInputStream(inputFile.getContents())));
      }
      final RootInput rootInput = testData.getRootInput();
      context.submitJob(GalaxyXmlUtils.convert(tool),  
                        GalaxyXmlUtils.convert(rootInput), 
                        Iterables.toArray(inputFileNames, String.class),   
                        Iterables.toArray(resources, TransferResource.class), 
                        (CredentialResource) null);
      assertFinishsProperly();
      int i = 0;
      for(final Closure<byte[]> outputFileChecker : testData.getOutputFileChecker()) {
        final StorageData resultStorageData = getResults().get(i++);
        final InputContext resultInputContext = resultStorageData.getDownloadContext();
        byte[] byteArray = InputContexts.getAsByteArray(resultInputContext);
        outputFileChecker.apply(byteArray);
      }
    }
    
    
  }
  
  public void submit(final String xml) {
    
  }
  
  private void assertFinishsProperly() {
    pollJob("Galaxy", context);
    assertJobFinishedProperly();
  }

  protected void submitJob() {
    
  }

}
