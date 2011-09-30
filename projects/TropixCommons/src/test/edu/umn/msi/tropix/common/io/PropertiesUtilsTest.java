package edu.umn.msi.tropix.common.io;


import java.util.Properties;

import org.testng.annotations.Test;

public class PropertiesUtilsTest {
  private static final PropertiesUtils PROPERTIES_UTILS = PropertiesUtilsFactory.getInstance();

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void loadProblem() {
    PROPERTIES_UTILS.load(new IOUtilsTest.ClosedReader());
  }
  
  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void storeProblem() {
    PROPERTIES_UTILS.store(new Properties(), new IOUtilsTest.ClosedWriter());
  }

  @Test(groups = "unit")
  public void testLoadFromString() {
    final Properties properties = PROPERTIES_UTILS.load("moo=cow\nfoo=bar");
    assert properties.getProperty("moo").equals("cow");
  }

  @Test(groups = "unit")
  public void testToString() {
    final Properties properties = new Properties();
    properties.put("moo", "cow");
    assert PROPERTIES_UTILS.toString(properties).contains("moo=cow");
  }
  
}
