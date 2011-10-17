package edu.umn.msi.tropix.jobs.newfile;

import org.testng.annotations.Test;

import com.google.common.base.Suppliers;

import edu.umn.msi.tropix.jobs.activities.descriptions.CreateProteomicsRunDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateTropixFileDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitThermofinniganRunJobDescription;

public class RawNewFileProcessorImplTest extends BaseNewFileProcessorTest {

  @Test(groups = "unit")
  public void test() {
    getFile().setName("THE_RUN.RAW");
    final RawNewFileProcessorImpl processor = new RawNewFileProcessorImpl(getMockActivityClient(), Suppliers.ofInstance("local://RawExtract"));
    sendMessage(processor);
    
    final CreateProteomicsRunDescription runDescription = getActivityDescriptionOfType(CreateProteomicsRunDescription.class);
    assert runDescription.getName().equals("THE_RUN");
    assertEqualsFileObjectId(runDescription.getSourceId());
    assertEqualsParentId(runDescription.getDestinationId());
    
    final SubmitThermofinniganRunJobDescription submitDescription = getActivityDescriptionOfType(SubmitThermofinniganRunJobDescription.class);
    assert submitDescription.getServiceUrl().equals("local://RawExtract");
    
    final CreateTropixFileDescription createMzxmlDescription = getActivityDescriptionOfType(CreateTropixFileDescription.class);
    assert runDescription.getDependencies().iterator().next().getActivityDescription() == createMzxmlDescription;
  }
  
}
