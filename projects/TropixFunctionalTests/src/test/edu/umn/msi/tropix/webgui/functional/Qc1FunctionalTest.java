package edu.umn.msi.tropix.webgui.functional;

import java.io.File;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.proteomics.test.ProteomicsTests;

public class Qc1FunctionalTest extends FunctionalTestBase {
  private static final String WORKFLOW_MENU_OPTION = "Integrated Identification Workflow";

  @Test(groups = "Q")
  public void qaTest() {
    login();
    clickNewMenuOption(WORKFLOW_MENU_OPTION);
    waitForWizardNext("0");
    wizardNext("0");

    changeToTraditionalUpload("0");
    final File tempFile = getTempFieWithContents(".mzxml", ProteomicsTests.getResourceAsStream(ProteomicsTests.SMALL_MZXML_RESOURCE_ID));
    typeKeys("document.forms[4].elements[0]", tempFile.getAbsolutePath());

    wizardNext("0");

    specifyObjectNameAs("Test");
    selectHomeFolderAsDestination("1");

    wizardNext("0");

  }

}
