package edu.umn.msi.tropix.webgui.functional;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import javax.annotation.WillClose;

import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.thoughtworks.selenium.Selenium;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContexts;

public class FunctionalTestBase {
  private static enum TintInstance {
    PRODUCTION("https://tropix.msi.umn.edu/"),
    STAGING("http://128.101.191.217:8080/tint/"),
    LOCAL("http://127.0.0.1:8181/war/");

    private final String baseUrl;

    private TintInstance(final String baseUrl) {
      this.baseUrl = baseUrl;
    }

    String getBaseUrl() {
      return baseUrl;
    }
  }

  private TintInstance instance = TintInstance.STAGING;
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private static final long DEFAULT_WAIT_TIME = 5000; // Five seconds
  private Selenium selenium;
  private File downloadDirectory;
  private List<File> tempFiles = Lists.newArrayList();

  protected File getTempFile(final String suffix) {
    final File tempFile = FILE_UTILS.createTempFile("tpxtest", suffix);
    return tempFile;
  }

  protected File getTempFieWithContents(final String suffix, @WillClose final InputStream stream) {
    try {
      final File tempFile = getTempFile(suffix);
      InputContexts.forFile(tempFile).put(stream);
      return tempFile;
    } finally {
      IO_UTILS.closeQuietly(stream);
    }
  }

  protected File getDownloadDirectory() {
    return downloadDirectory;
  }

  protected Selenium getSelenium() {
    return selenium;
  }

  protected void clickExportSubMenu() {
    clickFileSubMenu("Export");
  }

  protected void clickExportOption(final String exportOption) {
    click(String.format("scLocator=//Menu[ID=\"isc_MainToolStripComponentImpl_TitledMenu_0\"]/body/row[title=%s]/col[1]", exportOption));
  }

  public void login() {
    getSelenium().open("/");
    final String usernameLocator = "scLocator=//DynamicForm[ID=\"isc_Form_0\"]/item[name=username]/element";
    waitForElementPresent(usernameLocator);
    getSelenium().typeKeys(usernameLocator, "admin");
    getSelenium().typeKeys("scLocator=//DynamicForm[ID=\"isc_Form_0\"]/item[name=password]/element", "admin");
    getSelenium().click("scLocator=//Button[ID=\"isc_Button_0\"]/");
    waitForElementPresent("scLocator=//ToolStripMenuButton[ID=\"isc_ToolStripMenuButton_0\"]/");
  }

  protected void clickNewMenuOption(final String title) {
    expandFileMenu();
    clickFileSubMenu("New...");
    click(String.format("scLocator=//Menu[ID=\"isc_Menu_3\"]/body/row[title=%s]/col[fieldName=title||1]", title));
  }

  @BeforeClass(groups = "functional")
  public void setUp() throws Exception {
    downloadDirectory = FILE_UTILS.createTempDirectory();

    final FirefoxProfile profile = new FirefoxProfile();
    // profile.setPreference("browser.download.useDownloadDir", false);
    // profile.setPreference("browser.download.useDownloadDir", "true");
    profile.setPreference("browser.download.lastDir", downloadDirectory.getAbsolutePath());
    profile.setPreference("browser.download.defaultFolder", downloadDirectory.getAbsolutePath());
    profile.setPreference("browser.download.folderList", 2);
    profile.setPreference("browser.download.dir", downloadDirectory.getAbsolutePath());
    profile.setPreference("browser.download.downloadDir", downloadDirectory.getAbsolutePath());
    profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/x-zip,application/zip");
    final FirefoxDriver driver = new FirefoxDriver(profile);

    String baseUrl = instance.getBaseUrl();
    selenium = new WebDriverBackedSelenium(driver, baseUrl);
    // selenium = new DefaultSelenium("localhost", 4444, "*firefox", baseUrl);
    selenium.addLocationStrategy("scLocator", "return inWindow.isc.AutoTest.getElement(locator);");
    // selenium.setExtensionJs("user-extensions.js");
    // selenium.start();
  }

  private File getDownloadFile() {
    return downloadDirectory.listFiles()[0];
  }

  protected void sleep(final long time) {
    try {
      Thread.sleep(time);
    } catch(InterruptedException ie) {
      throw new RuntimeException(ie);
    }
  }

  protected File waitForDownload(final String fileName) {

    final File downloadFile = new File(downloadDirectory, fileName);
    while(!downloadFile.exists()) {
      sleep(100);
    }

    long lastLength = -1;
    // Wait for file to finish downloading.
    while(true) {
      long length = downloadFile.length();
      if(lastLength == length) {
        break;
      }
      lastLength = length;
      sleep(500);
    }
    return downloadFile;
  }

  protected void waitFor(final Supplier<String> locator) {
    waitForElementPresent(locator.get());
  }

  protected void waitForElementPresent(final String locator) {
    waitForElementPresent(locator, DEFAULT_WAIT_TIME);
  }

  protected void waitForElementPresent(final String locator, final long timeout) {
    final long now = System.currentTimeMillis();
    while(!selenium.isElementPresent(locator)) {
      if(System.currentTimeMillis() - now > timeout) {
        assert false : String.format("Timeout while waiting for presence of element with locator [%s]", locator);
      }
      try {
        Thread.sleep(300L);
      } catch(InterruptedException e) {
        assert false : String.format("Interruption encountered while waiting for presence of element with locator [%s]", locator);
      }
    }
  }

  protected void click(final Supplier<String> locatorSupplier) {
    click(locatorSupplier.get());
  }

  protected void click(final String locator) {
    selenium.click(locator);
  }

  @AfterClass(groups = "functional")
  public void tearDown() throws Exception {
    FILE_UTILS.deleteDirectoryQuietly(downloadDirectory);
    selenium.stop();
    for(File tempFile : tempFiles) {
      FILE_UTILS.deleteQuietly(tempFile);
    }
  }

  protected void expandFileMenu() {
    click("scLocator=//ToolStripMen uButton[ID=\"isc_ToolStripMenuButton_0\"]/");
  }

  protected void clickFileSubMenu(final String subMenuName) {
    click(String.format("scLocator=//Menu[ID=\"isc_MainToolStripComponentImpl_TitledMenu_1\"]/body/row[title=%s]/col[fieldName=title||1]",
        subMenuName));
  }

  protected void typeKeys(final String locator, final String text) {
    getSelenium().typeKeys(locator, text);
  }

  protected void selectNewItemFolderOption(final String name) {
    final String genericFileSelector = String.format("scLocator=//TreeGrid[ID=\"isc_TreeGrid_0\"]/body/row[name=%s]/col[fieldName=name||0]", name);
    waitForElementPresent(genericFileSelector);
    click(genericFileSelector);
    click("scLocator=//Button[ID=\"NewItemFolder_Button_Ok\"]/");
  }

  protected void selectHomeFolderAsDestination(final String treeComponentId) {
    String myHomeSelector = String.format("scLocator=//TreeGrid[ID=\"TreeComponent_%s\"]/body/row[0]/col[fieldName=name]", treeComponentId);
    waitForElementPresent(myHomeSelector);
    click(myHomeSelector);
  }

  protected void specifyObjectNameAs(final String name) {
    final String locator = "document.forms[1].elements[0]";
    waitForElementPresent(locator);
    typeKeys(locator, "Test");
  }

  protected void waitForWizardNext(final String wizardId) {
    waitForElementPresent(wizardNextButtonSelector(wizardId));
  }

  protected void wizardNext(final String wizardId) {
    click(wizardNextButtonSelector(wizardId));
  }

  private String wizardNextButtonSelector(final String wizardId) {
    return String.format("scLocator=//Button[ID=\"Wizard_%s_Button_Next\"]/", wizardId);
  }

  protected void wizardFinish(final String wizardId) {
    click(String.format("scLocator=//Button[ID=\"Wizard_%s_Button_Finish\"]/", wizardId));
  }

  protected void changeToTraditionalUpload(final String uploadComponentId) {
    final String uploadComponentTypeLocator = String.format("scLocator=//DynamicForm[ID=\"UploadComponentType_%s\"]/item[name=uploadType]/textbox",
        uploadComponentId);
    waitForElementPresent(uploadComponentTypeLocator);
    click(uploadComponentTypeLocator);
    final String traditionalUploadSelector = String
        .format(
            "scLocator=//DynamicForm[ID=\"UploadComponentType_%s\"]/item[name=uploadType]/pickList/body/row[uploadType=Traditional Upload]/col[fieldName=uploadType]",
            uploadComponentId);
    if(getSelenium().isElementPresent(traditionalUploadSelector)) {
      click(traditionalUploadSelector);
    } else {

    }
  }
}
