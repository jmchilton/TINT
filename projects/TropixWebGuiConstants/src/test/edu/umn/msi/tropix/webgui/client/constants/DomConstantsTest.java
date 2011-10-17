package edu.umn.msi.tropix.webgui.client.constants;

import org.testng.annotations.Test;

public class DomConstantsTest {

  @Test(groups = "unit")
  public void testBuildConstant() {
    assert DomConstants.buildConstant("moo", "cow").equals("moo_cow");
  }
  
}
