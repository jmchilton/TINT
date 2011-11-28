package edu.umn.msi.tropix.common.test;

import java.io.Closeable;
import java.io.File;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;

public class IOTest {
  protected static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  protected static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();

  private List<File> tempFiles;
  private List<File> tempDirectories;
  private List<Closeable> closeables;

  protected File getTempFile() {
    final File tempFile = FILE_UTILS.createTempFile();
    tempFiles.add(tempFile);
    return tempFile;
  }

  protected File getTempDirectory() {
    final File tempDirectory = FILE_UTILS.createTempDirectory();
    tempDirectories.add(tempDirectory);
    return tempDirectory;
  }

  protected void registerCloseable(final Closeable closeable) {
    closeables.add(closeable);
  }

  @BeforeClass(groups = "unit")
  public void initTempIO() {
    tempFiles = Lists.newArrayList();
    tempDirectories = Lists.newArrayList();
    closeables = Lists.newArrayList();
  }

  @AfterClass(groups = "unit")
  public void cleanUpTempIO() {
    for(final File tempDirectory : tempDirectories) {
      FILE_UTILS.deleteDirectoryQuietly(tempDirectory);
    }
    for(final File tempFile : tempFiles) {
      FILE_UTILS.deleteQuietly(tempFile);
    }
    for(final Closeable closeable : closeables) {
      IO_UTILS.closeQuietly(closeable);
    }
  }

}
