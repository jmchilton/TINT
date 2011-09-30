package edu.umn.msi.tropix.proteomics.idpicker.impl;

import java.io.File;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;

public class IdPickerScriptWriterTest {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private File testDirectory;
  private File batFile;

  @BeforeMethod(groups = "unit")
  public void init() {
    testDirectory = FILE_UTILS.createTempDirectory();
    batFile = new File(testDirectory, "idpicker.bat");
  }

  @AfterMethod(groups = "unit")
  public void tearDown() {
    FILE_UTILS.deleteDirectoryQuietly(testDirectory);
  }

  /*
   * These tests only worked on Windows
   * 
   * @Test(groups = "unit")
   * public void testWrite() {
   * final String pickerHome = "C:\\idpicker";
   * IdPickerScriptWriter.writeScript(testDirectory, pickerHome);
   * assert getWrittenContents().contains("call C:\\idpicker\\idpQonvert ") : getWrittenContents();
   * }
   * 
   * @Test(groups = "unit")
   * public void testWriteWithoutSeparator() {
   * final String pickerHome = "C:\\idpicker\\";
   * IdPickerScriptWriter.writeScript(testDirectory, pickerHome);
   * assert getWrittenContents().contains("call C:\\idpicker\\idpQonvert ");
   * }
   */

  @Test(groups = "unit")
  public void testWriteNullHome() {
    IdPickerScriptWriter.writeScript(testDirectory, null);
    assert getWrittenContents().contains("call \"idpQonvert\" ");
  }

  private String getWrittenContents() {
    assert batFile.exists();
    final String contents = FILE_UTILS.readFileToString(batFile);
    return contents;
  }

}
