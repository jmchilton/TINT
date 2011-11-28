package edu.umn.msi.tropix.galaxy;

import java.io.File;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.IOTest;

public class GalaxyServiceFunctionImplTest  extends IOTest {

  @Test(groups = "unit")
  public void testDefaultService() {
    final GalaxyServiceFunctionImpl function = new GalaxyServiceFunctionImpl("https://galaxy/Galaxy", null);
    final String serviceUrl = function.apply("pep2pro");
    assert serviceUrl.equals("https://galaxy/Galaxy");
  }
  
  @Test(groups = "unit")
  public void testFileMapping() {
    final GalaxyServiceFunctionImpl function = buildSimpleFunction();
    assert function.apply("msconvert").equals("local://cow");
  }

  @Test(groups = "unit")
  public void testFileMappingFallsBackToDefault() {
    final GalaxyServiceFunctionImpl function = buildSimpleFunction();
    assert function.apply("spectrast").equals("https://galaxy/Galaxy");
  }
  
  @Test(groups = "unit")
  public void testHandlesMissingFile() {
    final GalaxyServiceFunctionImpl function = new GalaxyServiceFunctionImpl("https://galaxy/Galaxy", new File("C://notpresent"));
    assert function.apply("sepectrast").equals("https://galaxy/Galaxy");
  }

  private GalaxyServiceFunctionImpl buildSimpleFunction() {
    final File configFile = getTempFile();
    FILE_UTILS.writeStringToFile(configFile, "pep2pro=local://moo\nmsconvert=local://cow");
    final GalaxyServiceFunctionImpl function = new GalaxyServiceFunctionImpl("https://galaxy/Galaxy", configFile);
    return function;
  }
    
}
