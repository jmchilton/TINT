package edu.umn.msi.tropix.galaxy;

import javax.inject.Inject;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


import edu.umn.msi.tropix.common.jobqueue.client.JobClientFactory;
import edu.umn.msi.tropix.common.jobqueue.client.JobClientFactoryManager;
import edu.umn.msi.tropix.common.jobqueue.test.IntegrationTestBase;
import edu.umn.msi.tropix.galaxy.inputs.cagrid.Input;
import edu.umn.msi.tropix.galaxy.inputs.cagrid.RootInput;
import edu.umn.msi.tropix.galaxy.service.GalaxyJobQueueContext;
import edu.umn.msi.tropix.galaxy.tool.cagrid.Tool;
import edu.umn.msi.tropix.galaxy.tool.repository.GalaxyToolRepository;
import edu.umn.msi.tropix.galaxy.xml.GalaxyXmlUtils;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.transfer.types.TransferResource;

@ContextConfiguration
public class GalaxyIntegrationTest extends IntegrationTestBase {
  private Credential credential = Credentials.getMock();
  private String serviceUrl = "local://Galaxy";
  private Class<GalaxyJobQueueContext> jobQueueContextClass = GalaxyJobQueueContext.class;
  
  @Inject
  private GalaxyToolRepository testToolSource;
  
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
  public void testLoad() {
    final Tool tool = GalaxyXmlUtils.convert(testToolSource.loadForToolId("touch"));
    
    final Input input1 = new Input();
    input1.setName("input");
    input1.setValue("Hello");
    
    final RootInput input = new RootInput();
    input.setInput(new Input[] {input1});
    context.submitJob(tool, input, new String[] {}, new TransferResource[0], null);
    assertFinishsProperly();
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
