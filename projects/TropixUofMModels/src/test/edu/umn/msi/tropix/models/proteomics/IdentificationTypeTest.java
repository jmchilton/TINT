package edu.umn.msi.tropix.models.proteomics;

import org.testng.annotations.Test;

public class IdentificationTypeTest {

  @Test(groups = "unit")
  public void testFromString() {
    assert IdentificationType.fromParameterType("SequestBean").equals(IdentificationType.SEQUEST);
    assert IdentificationType.fromParameterType("OmssaXml").equals(IdentificationType.OMSSA);
  }

}
