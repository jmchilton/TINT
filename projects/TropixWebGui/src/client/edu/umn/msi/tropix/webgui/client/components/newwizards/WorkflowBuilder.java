package edu.umn.msi.tropix.webgui.client.components.newwizards;

import java.util.Set;

import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.webgui.client.utils.Sets;

public abstract class WorkflowBuilder {

  private Set<ActivityDescription> descriptions;

  protected abstract void populateDescriptions();

  public WorkflowBuilder() {
    super();
  }

  public Set<ActivityDescription> build() {
    descriptions = Sets.newHashSet();
    populateDescriptions();
    return descriptions;
  }

  protected boolean add(final ActivityDescription description) {
    return descriptions.add(description);
  }

}