package edu.umn.msi.tropix.common.test;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@ContextConfiguration(locations = "jmxTestContext.xml")
public class JmxInteractionTest extends AbstractTestNGSpringContextTests {

  @Test()
  public void test() throws InterruptedException {
    Thread.sleep(1000 * 60 * 10);
  }

}
