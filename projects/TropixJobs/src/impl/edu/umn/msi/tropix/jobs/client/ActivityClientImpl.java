package edu.umn.msi.tropix.jobs.client;

import java.util.Set;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.ActivityDirector;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.ConsumesStorageServiceUrl;

@ManagedBean
public class ActivityClientImpl implements ActivityClient {
  private final ActivityDirector activityDirector;
  private final Supplier<String> storageServiceUrlSupplier;
 
  @Inject
  public ActivityClientImpl(final ActivityDirector activityDirector,
                            @Named("storageServiceUrlSupplier") 
                            final Supplier<String> storageServiceUrlSupplier) {
    this.activityDirector = activityDirector;
    this.storageServiceUrlSupplier = storageServiceUrlSupplier;
  }

  public void submit(final Set<ActivityDescription> activityDescriptions, 
                     final Credential credential) {
    populateStorageServiceUrls(activityDescriptions);
    final ActivityContext context = new ActivityContext();
    context.setCredentialStr(credential.toString());
    context.setActivityDescription(activityDescriptions);
    activityDirector.execute(context);
  }
  
  protected void populateStorageServiceUrls(final Iterable<ActivityDescription> activityDescriptions) {
    for(final ActivityDescription activityDescription : activityDescriptions) {
      if(activityDescription instanceof ConsumesStorageServiceUrl) {
        final ConsumesStorageServiceUrl consumer = (ConsumesStorageServiceUrl) activityDescription;
        consumer.setStorageServiceUrl(storageServiceUrlSupplier.get());
      }
    }    
  }

}
