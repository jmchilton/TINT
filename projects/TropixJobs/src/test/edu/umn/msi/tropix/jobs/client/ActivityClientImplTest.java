package edu.umn.msi.tropix.jobs.client;

import java.util.Set;
import java.util.UUID;

import org.easymock.Capture;
import org.easymock.classextension.EasyMock;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;
import com.google.common.collect.Sets;

import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.ActivityDirector;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.UploadFileDescription;

public class ActivityClientImplTest {
  private static final String STORAGE_SERVICE_URL = UUID.randomUUID().toString();
  private ActivityContext submittedContext;
  private Credential credential;
  private Set<ActivityDescription> activityDescription;
  
  @BeforeMethod(groups = "unit")
  public void init() {
    submittedContext = null;
    activityDescription = Sets.newHashSet();
    credential = Credentials.getMock();
  }
  
  private void submit() {
    final Capture<ActivityContext> contextCapture = EasyMockUtils.newCapture();
    final ActivityDirector mockDirector = EasyMock.createMock(ActivityDirector.class);
    mockDirector.execute(EasyMock.capture(contextCapture));
    EasyMock.replay(mockDirector);

    final ActivityClientImpl client = new ActivityClientImpl(mockDirector, Suppliers.<String>ofInstance(STORAGE_SERVICE_URL));
    client.submit(activityDescription, credential);
    submittedContext = contextCapture.getValue();
  }
    
  @Test(groups = "unit")
  public void testCredentialDelegated() {
    submit();
    Assert.assertEquals(submittedContext.getCredential().getIdentity(), credential.getIdentity());
  }

  @Test(groups = "unit")
  public void testDescriptionsDelegated() {
    activityDescription.add(new UploadFileDescription());
    submit();
    Assert.assertSame(submittedContext.getActivityDescriptions(), activityDescription);
  }
  
  @Test(groups = "unit")
  public void testStorageServiceUrlSetup() {
    final UploadFileDescription uploadDescription = new UploadFileDescription();
    activityDescription.add(uploadDescription);
    submit();
    Assert.assertEquals(uploadDescription.getStorageServiceUrl(), STORAGE_SERVICE_URL);
  }
  
  
}
