package edu.umn.msi.tropix.common.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FileContextImplTest {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private File file;
  private FileContext context;
  
  @BeforeMethod(groups = "unit")
  public void init() throws IOException {
    file = FILE_UTILS.createTempFile();
    context = new FileContext(file);
  }
  
  @AfterMethod(groups = "unit")
  public void destroy() {
    file.delete();
  }

  @Test(groups = "unit")
  public void testAsFile() { 
    assert context.asFile() == file;
  }
  
  @Test(groups = "unit")
  public void putUrl() throws MalformedURLException {
    final File inputFile = FILE_UTILS.createTempFile();
    try {
      FILE_UTILS.writeStringToFile(inputFile, "test");
      context.put(inputFile.toURI().toURL());
      assertContentsAre("test");
    } finally {
      FILE_UTILS.deleteQuietly(inputFile);
    }
  }
  
  @Test(groups = "unit")
  public void testGetOutputContext() {
    FILE_UTILS.writeStringToFile(file, "test 2");
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    context.get(OutputContexts.forOutputStream(outputStream));
    assert new String(outputStream.toByteArray()).equals("test 2");
  }
  
  private void assertContentsAre(final String expectedContents) {
    assert expectedContents.equals(FILE_UTILS.readFileToString(file));
  }
}
