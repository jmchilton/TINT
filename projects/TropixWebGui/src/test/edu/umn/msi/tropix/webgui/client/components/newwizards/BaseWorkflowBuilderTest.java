package edu.umn.msi.tropix.webgui.client.components.newwizards;

import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;
import com.google.gwt.thirdparty.guava.common.collect.Iterables;

import edu.umn.msi.tropix.jobs.activities.WorkflowVerificationUtils;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;

public class BaseWorkflowBuilderTest<T extends WorkflowBuilder> {
  private Set<ActivityDescription> builtDescriptions;
  private T workflowBuilder;

  public BaseWorkflowBuilderTest() {
    super();
  }

  @BeforeMethod(groups = "unit")
  public void resetDescriptions() {
    builtDescriptions = null;
  }

  protected T setWorkflowBuilder(final T workflowBuilder) {
    this.workflowBuilder = workflowBuilder;
    return workflowBuilder;
  }

  protected T getWorkflowBuilder() {
    return workflowBuilder;
  }

  protected void buildAndVerify() {
    final Set<ActivityDescription> descriptions = workflowBuilder.build();
    WorkflowVerificationUtils.checkDependencies(descriptions);
    builtDescriptions = descriptions;
  }

  protected void assertBuiltNDescriptionsOfType(final int n, final Class<? extends ActivityDescription> activityDescription) {
    final int numDescriptions = getDescriptionsOfType(activityDescription).size();
    Assert.assertEquals(n, numDescriptions);
  }

  protected <D extends ActivityDescription> Set<D> getDescriptionsOfType(final Class<D> activityDescriptionClass) {
    @SuppressWarnings({"unchecked", "rawtypes"})
    final Set<D> matchingDescriptions = Sets.filter(builtDescriptions, (Predicate) Predicates.instanceOf(activityDescriptionClass));
    return matchingDescriptions;
  }

  protected <D extends ActivityDescription> D getDescriptionOfType(final Class<D> activityDescriptionClass) {
    return Iterables.getOnlyElement(getDescriptionsOfType(activityDescriptionClass));
  }

}