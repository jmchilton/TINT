package edu.umn.msi.tropix.jobs.newfile;

import java.util.Set;
import java.util.UUID;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.files.NewFileMessageQueue.NewFileMessage;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.jobs.activities.WorkflowVerificationUtils;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.client.ActivityClient;
import edu.umn.msi.tropix.models.TropixFile;

// TODO: Move into new package
public class BaseNewFileProcessorTest {
  private Credential credential;
  private TropixFile newFile;
  private String parentFileId;
  private NewFileMessage message;
  private ActivityClient activityClient;
  private Capture<Set<ActivityDescription>> submittedDescriptionsCapture;
  
  protected void sendMessage(final NewFileProcessor newFileProcessor) {
    activityClient.submit(EasyMock.capture(submittedDescriptionsCapture), EasyMock.same(credential));
    EasyMock.replay(activityClient);
    newFileProcessor.processFile(message, newFile);
    WorkflowVerificationUtils.checkDependencies(getSubmittedActivities());
    for(ActivityDescription description : getSubmittedActivities()) {
      assert description.getJobDescription() != null;
    }
  }
  
  protected Set<ActivityDescription> getSubmittedActivities() {
    return submittedDescriptionsCapture.getValue();
  }
  
  protected ActivityClient getMockActivityClient() {
    return activityClient;
  }
  
  @SuppressWarnings("unchecked")
  protected <T extends ActivityDescription> T getActivityDescriptionOfType(final Class<T> clazz) {
    return (T) Iterables.find(getSubmittedActivities(), Predicates.instanceOf(clazz));
  }


  @BeforeMethod(groups = "unit")
  public void init() {
    credential = Credentials.getMock();
    newFile = new TropixFile();
    newFile.setId(UUID.randomUUID().toString());
    newFile.setFileId(UUID.randomUUID().toString());
    parentFileId = UUID.randomUUID().toString();
    
    message = new NewFileMessage();
    message.setCredential(credential);
    message.setFileId(newFile.getFileId());
    message.setObjectId(newFile.getId());
    message.setParentId(parentFileId);
    
    activityClient = EasyMock.createMock(ActivityClient.class);
    submittedDescriptionsCapture = EasyMockUtils.newCapture();
  }
  
  protected void assertEqualsParentId(final String id) {
    Assert.assertEquals(id, parentFileId);
  }
  
  protected void assertEqualsFileObjectId(final String otherId) {
    Assert.assertEquals(otherId, newFile.getId());
  }
  
  protected String expectFileObjectId() {
    return EasyMock.eq(newFile.getId());
  }

  protected String expectFileId() {
    return EasyMock.eq(newFile.getFileId());
  }
  
  protected TropixFile getFile() {
    return newFile;
  }
  
}
