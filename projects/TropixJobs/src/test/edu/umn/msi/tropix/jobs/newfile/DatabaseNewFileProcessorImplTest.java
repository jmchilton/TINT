package edu.umn.msi.tropix.jobs.newfile;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.jobs.activities.WorkflowVerificationUtils;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateDatabaseDescription;

public class DatabaseNewFileProcessorImplTest extends BaseNewFileProcessorTest {
  
  @Test(groups = "unit")
  public void testProcess() {
    getFile().setName("THE_DB.fasta");
    final DatabaseNewFileProcessorImpl processor = new DatabaseNewFileProcessorImpl(getMockActivityClient());
    sendMessage(processor);
    
    WorkflowVerificationUtils.checkDependencies(getSubmittedActivities());
    final ActivityDescription description = getSubmittedActivities().iterator().next();

    assert description instanceof CreateDatabaseDescription;
    final CreateDatabaseDescription createDatabaseDescription = (CreateDatabaseDescription) description;
    assert createDatabaseDescription.getDatabaseType().equals("FASTA");
    assertEqualsFileObjectId(createDatabaseDescription.getDatabaseFileId());
    assertEqualsParentId(createDatabaseDescription.getDestinationId());
    assert createDatabaseDescription.getName().equals("THE_DB");
    assert createDatabaseDescription.getCommitted();
  }

}
