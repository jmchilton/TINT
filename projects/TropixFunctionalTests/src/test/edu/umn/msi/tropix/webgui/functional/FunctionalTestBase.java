package edu.umn.msi.tropix.webgui.functional;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.thoughtworks.selenium.Selenium;

public class FunctionalTestBase {
  private static final long DEFAULT_WAIT_TIME = 5000; // Five seconds
  private Selenium selenium;

  protected Selenium getSelenium() {
    return selenium;
  }

  @BeforeClass(groups = "functional")
  public void setUp() throws Exception {
    WebDriver driver = new FirefoxDriver();

    String baseUrl = "http://127.0.0.1:8180/war/";
    selenium = new WebDriverBackedSelenium(driver, baseUrl);
    // selenium = new DefaultSelenium("localhost", 4444, "*firefox", baseUrl);
    selenium.addLocationStrategy("scLocator", "return inWindow.isc.AutoTest.getElement(locator);");
    // selenium.setExtensionJs("user-extensions.js");
    // selenium.start();
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

  protected void click(final String locator) {
    selenium.click(locator);
  }

  @AfterClass(groups = "functional")
  public void tearDown() throws Exception {
    selenium.stop();
  }

}
