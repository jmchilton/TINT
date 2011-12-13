package edu.umn.msi.tropix.proteomics.sequest.impl;

import org.testng.annotations.Test;

public class SequestUtilsTest {

  @Test(groups = "unit")
  public void emptySanitize() {
    assert SequestUtils.sanitizeDatabaseName(null).equals(SequestUtils.DEFAULT_DATABASE_NAME);
    assert SequestUtils.sanitizeDatabaseName("").equals(SequestUtils.DEFAULT_DATABASE_NAME);
  }
  
  @Test(groups = "unit")
  public void emptybaseSanitize() {
    assert SequestUtils.sanitizeDatabaseName(".fasta").equals(SequestUtils.DEFAULT_DATABASE_NAME);
  }
  
  @Test(groups ="unit")
  public void testSanitize() {
    assert SequestUtils.sanitizeDatabaseName("../../@moo.fasta").equals("_moo.fasta");
  }
  
}
