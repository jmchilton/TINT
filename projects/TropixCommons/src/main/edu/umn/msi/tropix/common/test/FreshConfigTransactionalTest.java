package edu.umn.msi.tropix.common.test;

import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class FreshConfigTransactionalTest extends AbstractTransactionalTestNGSpringContextTests {
  /**
   * Allow subclasses to initialize a configuration directory before Spring is
   * initialized.
   */
  protected void initializeConfigDir(final ConfigDirBuilder configDirBuilder) {

  }

  private FreshConfigurer configurer;

  @BeforeClass(alwaysRun = true)
  @Override
  protected void springTestContextBeforeTestClass() throws Exception {
    configurer = new FreshConfigurer(this);
    super.springTestContextBeforeTestClass();
  }

  @BeforeClass(alwaysRun = true, dependsOnMethods = "springTestContextBeforeTestClass")
  @Override
  protected void springTestContextPrepareTestInstance() throws Exception {
    super.springTestContextPrepareTestInstance();
  }

  @AfterClass(alwaysRun = true)
  @Override
  protected void springTestContextAfterTestClass() throws Exception {
    try {
      super.springTestContextAfterTestClass();
    } finally {
      configurer.destroy();
    }
  }
}
