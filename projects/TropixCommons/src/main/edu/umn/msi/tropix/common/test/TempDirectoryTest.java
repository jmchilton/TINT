package edu.umn.msi.tropix.common.test;

import java.io.Closeable;
import java.io.File;
import java.util.List;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;

/**
 * Creates a temp directory before and cleans it up after each
 * test method.
 * 
 * @author John Chilton (jmchilton at gmail dot com)
 *
 */
public class TempDirectoryTest {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  
  /**
   * Temp directory for writing temp files into.
   */
  private File testDirectory;
  
  /**
   * Steams to close between tests, register one with the register function and it
   * will be torn down post test.
   */
  private List<Closeable> streamsToClose;

  protected static FileUtils getFileUtils() {
    return FILE_UTILS;
  }
  
  protected static IOUtils getIOUtils() {
    return IO_UTILS;
  }
  
  @BeforeMethod(groups = "unit")
  public void setup() {
    testDirectory = FILE_UTILS.createTempDirectory();
    streamsToClose = Lists.newArrayList();
  }
  
  @AfterMethod(groups = "unit")
  public void tearDown() {
    FILE_UTILS.deleteDirectoryQuietly(testDirectory);
    for(final Closeable streamToClose : streamsToClose) {
      IO_UTILS.closeQuietly(streamToClose);
    }
  }

  // Return the closeable for a more fluent API.
  protected <T extends Closeable> T register(final T closeable) {
    streamsToClose.add(closeable);
    return closeable;
  }
  
  protected File getTestDirectory() {
    return testDirectory;
  }
  
  protected File getFile(final String relativePath) {
    return new File(testDirectory, relativePath);
  }
  
  protected void writeToFile(final String relativePath, final String contents) {
    getFileUtils().writeStringToFile(getFile(relativePath), contents);
  }
  
}
