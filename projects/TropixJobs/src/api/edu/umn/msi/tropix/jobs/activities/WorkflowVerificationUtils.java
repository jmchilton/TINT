package edu.umn.msi.tropix.jobs.activities;

import java.lang.reflect.Method;

import edu.umn.msi.tropix.common.reflect.ReflectionHelper;
import edu.umn.msi.tropix.common.reflect.ReflectionHelpers;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDependency;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.Consumes;

public class WorkflowVerificationUtils {
  private static final ReflectionHelper REFLECTION_HELPER = ReflectionHelpers.getInstance();

  public static void checkDependency(final ActivityDescription activityDescription, final ActivityDependency dependency) {
    final ActivityDescription dependentActivity = dependency.getActivityDescription();
    String producerProperty = dependency.getProducerProperty();
    String consumerProperty = dependency.getConsumerProperty();
    if(producerProperty == null || consumerProperty == null) {
      assert producerProperty == null;
      assert consumerProperty == null;
    } else {
      if(!ActivityDependency.specifiesProducesIndex(dependency)) {
        final Method getMethod = REFLECTION_HELPER.getMethod(dependentActivity.getClass(),
            buildMethodName("get", producerProperty));

        String setMethodName = buildMethodName("set", consumerProperty);
        boolean hasSetter = REFLECTION_HELPER.hasMethod(activityDescription.getClass(), setMethodName);
        if(hasSetter) {
          final Method setMethod;
          if(ActivityDependency.specifiesConsumesIndex(dependency)) {
            setMethod= REFLECTION_HELPER.getMethod(activityDescription.getClass(),
                setMethodName,
                getMethod.getReturnType(),
                int.class);            
          } else {
            setMethod= REFLECTION_HELPER.getMethod(activityDescription.getClass(),
                setMethodName,
                getMethod.getReturnType());
          }
          // The depending property must consume specified property.
          assert setMethod.getAnnotation(Consumes.class) != null;
        } else {
          final Method addMethod = REFLECTION_HELPER.getMethod(activityDescription.getClass(),
              buildMethodName("add", consumerProperty),
              getMethod.getReturnType());
          assert addMethod.getAnnotation(Consumes.class) != null;
        }
      } else {
        // Assert a get method exists and it returns an Iterable
        final Method getMethod = REFLECTION_HELPER.getMethod(dependentActivity.getClass(),
            buildMethodName("get", producerProperty));
        assert Iterable.class.isAssignableFrom(getMethod.getReturnType());
        Method setMethod = null;
        // Because of type erasure we don't know the type of object stored in Iterable so we cannot simply use getMethod,
        // instead iterate through all methods and find a setter with the proper name.
        for(final Method method : activityDescription.getClass().getMethods()) {
          if(method.getName().equals(buildMethodName("set", consumerProperty)) && method.getParameterTypes().length == 1) {
            setMethod = method;
          }
        }
        assert setMethod.getAnnotation(Consumes.class) != null;
      }
    }
  }

  private static String buildMethodName(final String prefix, final String property) {
    return prefix + property.substring(0, 1).toUpperCase() + property.substring(1);
  }

  public static void checkDependencies(final ActivityDescription activityDescription) {
    for(final ActivityDependency dependency : activityDescription.getDependencies()) {
      checkDependency(activityDescription, dependency);
    }
  }

  public static void checkDependencies(final Iterable<ActivityDescription> activityDescriptions) {
    for(ActivityDescription activityDescription : activityDescriptions) {
      checkDependencies(activityDescription);
    }
  }

  public static void assertJobDescriptionCopied(final ActivityDescription from, final ActivityDescription to) {
    assert from.getJobDescription() == to.getJobDescription();
  }

}
