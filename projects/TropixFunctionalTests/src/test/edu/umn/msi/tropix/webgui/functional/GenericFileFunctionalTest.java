package edu.umn.msi.tropix.webgui.functional;

import org.testng.annotations.Test;

public class GenericFileFunctionalTest extends FunctionalTestBase {

  @Test(groups = "functional")
  public void canUploadGenericFile() throws Exception {
    login();
    clickNewMenuOption("Other...");
    selectNewItemFolderOption("Generic%20File");
    selectHomeFolderAsDestination("1");
    specifyObjectNameAs("Test");

    wizardNext("0");
    Thread.sleep(10000);
    changeToTraditionalUpload("0");
    typeKeys("name=XYZuploadcomponentXYZ1", "/home/msi/chilton/parse_lmstat.py");
    wizardFinish("0");
    waitForElementPresent("scLocator=//ListGrid[ID=\"ProgressListGrid\"]/body/row[status=images/kurumizawa/action_check.gif]");
  }

}
