package edu.umn.msi.tropix.webgui.functional;

import java.io.File;
import java.io.InputStream;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.proteomics.test.ProteomicsTests;

public class BulkMgfDownloadFunctionalTest extends FunctionalTestBase {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();

  @Test(groups = "functional")
  public void testProteinPilotDownlaod() throws Exception {
    login();
    clickNewMenuOption("Peak List(s)");

    String mzxmlOptionLocator = "scLocator=//DynamicForm[ID=\"ProteomicsRunSource\"]/item[name=source]/item[index=1]/element";
    // String mzxmlOptionLocator =
    // "scLocator=//DynamicForm[ID=\"isc_Form_1\"]/item[name=source||title=source||value=THERMO||index=0||Class=RadioGroupItem]/item[name=%24540MZXML||title=Upload%20MzXML%20file%28s%29||index=1||Class=RadioItem]/element";
    // "scLocator=//DynamicForm[ID=\"isc_Form_1\"]/item[name=source]/item[index=1]/element";
    waitForElementPresent(mzxmlOptionLocator);
    click(mzxmlOptionLocator);

    final String batchOptionLocator = "scLocator=//DynamicForm[ID=\"ProteomicsRunSource\"]/item[name=batch]/textbox";
    click(batchOptionLocator);
    wizardNext("0");

    specifyObjectNameAs("Test");
    selectHomeFolderAsDestination("1");

    wizardNext("0");
    changeToTraditionalUpload("0");
    final InputStream inputStream = ProteomicsTests.getResourceAsStream(ProteomicsTests.SMALL_MZXML_RESOURCE_ID);
    final File tempFile = File.createTempFile("tpxtest", ".mzxml");
    try {
      InputContexts.forFile(tempFile).put(inputStream);
      typeKeys("document.forms[4].elements[0]", tempFile.getAbsolutePath());
      wizardFinish("0");

      waitForElementPresent("scLocator=//ListGrid[ID=\"ProgressListGrid\"]/body/row[1]");
      click("scLocator=//Window[ID=\"isc_Frame_1\"]/item[0][Class=\"TreeGrid\"]/body/row[0]/col[fieldName=name||0]/open");
      // click("scLocator=//Window[ID=\"isc_Frame_1\"]/item[0][Class=\"TreeGrid\"]/body/row[1]/col[fieldName=name||0]/open");
      expandFileMenu();
      clickExportSubMenu();
      clickExportOption("Download Runs as MGFs");
      click("scLocator=//DynamicForm[ID=\"BulkMgfDownload\"]/item[name=mgfStyle]/textbox");
      click("scLocator=//DynamicForm[ID=\"BulkMgfDownload\"]/item[name=mgfStyle]/pickList/body/row[mgfStyle=PROTEIN_PILOT]/col[0]");

      // click("scLocator=//Window[ID=\"isc_Frame_1\"]/item[0][Class=\"TreeGrid\"]/body/row[0]/col[fieldName=name||0]/open");
      // final String treeComponentId = "BulkMgfDownload";
      String homeFolderLocator = treeItemSelect("BulkMgfDownload", 0);
      waitForElementPresent(homeFolderLocator);
      click(homeFolderLocator + "/open");
      String runLocator = treeItemSelect("BulkMgfDownload", 1);
      waitForElementPresent(runLocator);
      click(runLocator);
      assert getDownloadDirectory().list().length == 0;
      click("scLocator=//Button[ID=\"BulkMgfDownload_Button_Download\"]/");
      // click("scLocator=//Window[ID=\"isc_Frame_1\"]/item[0][Class=\"TreeGrid\"]/body/row[1]/col[fieldName=name||0]/open")
      // waitForElementPresent(myHomeSelector);
      // click(myHomeSelector);

      Thread.sleep(2000);
      assert getDownloadDirectory().list().length == 1 : getDownloadDirectory().list().length;
    } finally {
      tempFile.delete();
    }

  }

  protected String treeItemSelect(final String treeComponentId, final int row) {
    return String.format("scLocator=//TreeGrid[ID=\"TreeComponent_%s\"]/body/row[%d]/col[fieldName=name]", treeComponentId, row);
  }

}
