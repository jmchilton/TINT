package edu.umn.msi.tropix.webgui.server.session;

import javax.inject.Inject;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@ContextConfiguration
public class SessionInfoSupplierImplTest extends AbstractTestNGSpringContextTests {

  @Inject
  private SessionInfoSupplierImpl sessionInfoSupplier;

  @Test(groups = "unit")
  public void testBeansFoundAndProcessingOccurs() {
    assert sessionInfoSupplier.get().getSessionId().equals(TestClosure1.TEST_ID);
  }

}
