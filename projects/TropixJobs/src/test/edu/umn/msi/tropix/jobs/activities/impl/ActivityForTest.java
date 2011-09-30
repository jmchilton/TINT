package edu.umn.msi.tropix.jobs.activities.impl;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.google.common.base.Joiner;

import edu.umn.msi.tropix.jobs.activities.impl.fortest.ActivityForTestBean;

@ContextConfiguration
public class ActivityForTest extends AbstractTestNGSpringContextTests {

  @Test(groups = "unit", timeOut = 1000)
  public void test() {
    System.out.println(Joiner.on(",").join(applicationContext.getBeanNamesForType(Activity.class)));
    final ActivityFor forAnnotation = applicationContext.findAnnotationOnBean("activityForTestBean", ActivityFor.class);
    System.out.println(forAnnotation.descriptionClass());
    super.applicationContext.getBean(ActivityForTestBean.class);
    super.applicationContext.getBean(ActivityForTestBean.class);
    super.applicationContext.getBeansWithAnnotation(ActivityFor.class);

    assert ActivityForTestBean.getCount() == 3;
  }

}
