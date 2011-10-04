package edu.umn.msi.tropix.webgui.functional;

import org.testng.annotations.Test;

public class LoginFunctionalTest extends FunctionalTestBase {

  @Test(groups = "functional")
  public void testValidLogin() throws Exception {
    login();
  }

}
