package edu.umn.msi.tropix.webgui.functional;

import org.testng.annotations.Test;

public class LoginFunctionalTest extends FunctionalTestBase {

  @Test(groups = "functional")
  public void testMoo() throws Exception {
    getSelenium().open("/");
    getSelenium().typeKeys("scLocator=//DynamicForm[ID=\"isc_Form_0\"]/item[name=username]/element", "admin");
    getSelenium().typeKeys("scLocator=//DynamicForm[ID=\"isc_Form_0\"]/item[name=password]/element", "admin");
    getSelenium().click("scLocator=//Button[ID=\"isc_Button_0\"]/");
    waitForElementPresent("scLocator=//ToolStripMenuButton[ID=\"isc_ToolStripMenuButton_0\"]/");
    click("scLocator=//ToolStripMen uButton[ID=\"isc_ToolStripMenuButton_0\"]/");
    click("scLocator=//Menu[ID=\"isc_MainToolStripComponentImpl_TitledMenu_1\"]/body/row[title=Export||6]/col[fieldName=title||1]");
    click("scLocator=//Menu[ID=\"isc_MainToolStripComponentImpl_TitledMenu_0\"]/body/row[title=Download%20as%20Zip||0]/col[fieldName=title||1]");
    waitForElementPresent("scLocator=//Dialog[ID=\"isc_WizardFactoryImpl_Wizard_0\"]/header/member[Class=Canvas||index=0||length=4||classIndex=0||classLength=1]/");
  }

}
