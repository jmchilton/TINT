package edu.umn.msi.tropix.common.jobqueue.deployer;

import java.io.File;
import java.io.InputStream;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;

public class DecompresserImplTest {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private SimpleExecutor mockExecutor;
  private DecompresserImpl decompresser;
  private File file;
  
  @BeforeMethod(groups = "unit")
  public void init() {
    decompresser = new DecompresserImpl();
    mockExecutor = EasyMock.createMock(SimpleExecutor.class);
    decompresser.setSimpleExecutor(mockExecutor);    
  }
  
  @Test(groups = "unit")
  public void unzip() {
    final File tempDirectory = FILE_UTILS.createTempDirectory();
    InputStream zipResourceStream = null;
    try {
      zipResourceStream = getClass().getResourceAsStream("hello.zip");
      final File zipFile = new File(tempDirectory, "hello.zip");
      FILE_UTILS.writeStreamToFile(zipFile, zipResourceStream);
      decompresser.decompress(zipFile);
      assert new File(tempDirectory, "hello1").exists();
      assert new File(tempDirectory, "hello2").exists();
      
    } finally {
      IO_UTILS.closeQuietly(zipResourceStream);
      FILE_UTILS.deleteDirectoryQuietly(tempDirectory);
    }
  }

  @Test(groups = "unit")
  public void testUnTarGz() {
    file = new File(new File("moo"), "foo.tar.gz");
    mockExecutor.executeInDirectory(new File("moo"), "/bin/tar", "xfz", file.getAbsolutePath());
    returnZeroReplayExtractAndVerify();
  }

  @Test(groups = "unit")
  public void testUnTar() {
    file = new File(new File("moo"), "foo.tar");
    mockExecutor.executeInDirectory(new File("moo"), "/bin/tar", "xf", file.getAbsolutePath());
    returnZeroReplayExtractAndVerify();
  }

  private void returnZeroReplayExtractAndVerify() {
    EasyMock.expectLastCall().andReturn(0);
    replayExtractAndVerify();
  }

  @Test(groups = "unit")
  public void testUnTarBz() {
    file = new File(new File("moo"), "foo.tar.bz2");
    mockExecutor.executeInDirectory(new File("moo"), "/bin/tar", "xfj", file.getAbsolutePath());
    returnZeroReplayExtractAndVerify();
  }

  private void replayExtractAndVerify() {
    EasyMock.replay(mockExecutor);
    decompresser.decompress(file);
    EasyMock.verify(mockExecutor);
  }
  
  

}
