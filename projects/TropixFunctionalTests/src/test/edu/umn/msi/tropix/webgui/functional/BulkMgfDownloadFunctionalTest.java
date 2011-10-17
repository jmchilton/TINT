package edu.umn.msi.tropix.webgui.functional;

import java.io.File;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.io.ZipUtilsFactory;
import edu.umn.msi.tropix.proteomics.test.ProteomicsTests;

public class BulkMgfDownloadFunctionalTest extends FunctionalTestBase {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  
  public void testProteinPilotDownlaod() {
    testBulkMgfDownload(true);
  }
  
  @Test(groups = "functional", timeOut = 30000)
  public void testMascotDownload() {
    testBulkMgfDownload(false);
  }
  
  public void testBulkMgfDownload(final boolean forProteinPilot) {
    login();
    clickNewMenuOption("Peak List(s)");

    final ScLocator sourceForm = ScLocator.form("ProteomicsRunSource");
    final ScLocator mzxmlOptionLocator = sourceForm.childMatching("item", "name=source")
                                                   .childMatching("item", "index=1")
                                                   .child("element");
    waitFor(mzxmlOptionLocator);
    click(mzxmlOptionLocator);

    final ScLocator batchOptionLocator = sourceForm.childMatching("item", "name=batch")
                                                   .child("textbox");
    click(batchOptionLocator);
    wizardNext("0");

    specifyObjectNameAs("Test");
    selectHomeFolderAsDestination("1");

    wizardNext("0");
    changeToTraditionalUpload("0");
    final File tempFile = getTempFieWithContents(".mzxml", ProteomicsTests.getResourceAsStream(ProteomicsTests.SMALL_MZXML_RESOURCE_ID));
    typeKeys("document.forms[4].elements[0]", tempFile.getAbsolutePath());
    wizardFinish("0");

    waitFor(ScLocator.progressListGrid(1));
    click("scLocator=//Window[ID=\"isc_Frame_1\"]/item[0][Class=\"TreeGrid\"]/body/row[0]/col[fieldName=name||0]/open");
    expandFileMenu();
    clickExportSubMenu();
    final String exportOption = "Download Peak Lists for " + (forProteinPilot ? "ProteinPilot" : "Mascot");  
    clickExportOption(exportOption);
        
    String homeFolderLocator = treeItemSelect("BulkMgfDownload", 0);
    waitForElementPresent(homeFolderLocator);
    click(homeFolderLocator + "/open");
    sleep(100);
    String runLocator = treeItemSelect("BulkMgfDownload", 1);
    waitForElementPresent(runLocator);
    click(runLocator);
    assert getDownloadDirectory().list().length == 0;
    click("scLocator=//Button[ID=\"BulkMgfDownload_Button_Download\"]/");

    final File downloadedFile = waitForDownload("mgf_export.zip");
    ZipUtilsFactory.getInstance().unzipToDirectory(downloadedFile, getDownloadDirectory());
    final File testMgf = new File(getDownloadDirectory(), "Test.mgf");
    assert testMgf.exists();
    assert forProteinPilot == InputContexts.toString(InputContexts.forFile(testMgf)).contains("Elution");
  }

  protected String treeItemSelect(final String treeComponentId, final int row) {
    return String.format("scLocator=//TreeGrid[ID=\"TreeComponent_%s\"]/body/row[%d]/col[fieldName=name]", treeComponentId, row);
  }

}
