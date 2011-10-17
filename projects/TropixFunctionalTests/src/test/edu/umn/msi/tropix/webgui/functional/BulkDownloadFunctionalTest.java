package edu.umn.msi.tropix.webgui.functional;

import org.testng.annotations.Test;

public class BulkDownloadFunctionalTest extends FunctionalTestBase {

  @Test(groups = "functional")
  public void testBulkDownloadWindowOpens() throws Exception {
    login();
    expandFileMenu();
    clickExportSubMenu();
    clickExportOption("Download as Zip");
    waitForElementPresent("scLocator=//Window[ID=\"isc_Window_1\"]/header/member[Class=Canvas||index=0||length=4||classIndex=0||classLength=1]/");
  }
}
