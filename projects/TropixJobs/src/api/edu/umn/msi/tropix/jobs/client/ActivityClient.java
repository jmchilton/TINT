package edu.umn.msi.tropix.jobs.client;

import java.util.Set;

import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;

public interface ActivityClient {

  void submit(final Set<ActivityDescription> activityDescriptions, final Credential credential);
  
}
