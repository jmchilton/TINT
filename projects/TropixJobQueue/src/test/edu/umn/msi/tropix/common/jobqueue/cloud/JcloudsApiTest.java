package edu.umn.msi.tropix.common.jobqueue.cloud;

import javax.annotation.Resource;

import org.jclouds.compute.ComputeService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;

@ContextConfiguration("test-context.xml")
public class JcloudsApiTest extends AbstractTestNGSpringContextTests {

  @Resource
  private Supplier<ComputeService> computeServiceSupplier;
  
  @Test(groups = "deployment")
  public void testKeys() {
    computeServiceSupplier.get();
  }
  
}
