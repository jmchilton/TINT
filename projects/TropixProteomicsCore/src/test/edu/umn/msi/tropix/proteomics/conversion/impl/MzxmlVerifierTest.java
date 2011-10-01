package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.io.InputStream;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.proteomics.test.ProteomicsTests;

public class MzxmlVerifierTest {

  @Test(groups = "unit")
  public void testValidMzxml() {
    checkResource("readw.mzXML", true);
  }

  @Test(groups = "unit")
  public void testInvalidStartTag() {
    checkResource("invalid4.mzxml", false); // doesn't start with <mzXML>
  }

  @Test(groups = "unit")
  public void testTruncatedFile() {
    checkResource("invalid1.mzxml", false);
    checkResource("invalid2.mzxml", false);
    checkResource("invalid3.mzxml", false);
  }

  private void checkResource(final String resourceName, final boolean expectedToVerify) {
    final InputStream inputStream = ProteomicsTests.getResourceAsStream(resourceName);
    assert MzxmlVerifier.isValid(inputStream) == expectedToVerify;
  }

}
