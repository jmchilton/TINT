package edu.umn.msi.tropix.webgui.server.download;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Map;

import org.easymock.classextension.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.common.io.ZipUtils;
import edu.umn.msi.tropix.common.io.ZipUtilsFactory;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.webgui.server.BaseGwtServiceTest;

public class BulkMgfDownloadHandlerImplTest extends BaseGwtServiceTest {
  private static final ZipUtils ZIP_UTILS = ZipUtilsFactory.getInstance();
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private MgfDownloadHelper mgfDownloadHelper;

  @BeforeMethod(groups = "unit")
  public void init() {
    super.init();
    mgfDownloadHelper = EasyMock.createMock(MgfDownloadHelper.class);
  }

  @Test(groups = "unit")
  public void testDownload() {
    final Map<String, String> args =
        ImmutableMap.<String, String>builder().put("mgfStyle", "DEFAULT").put("id", "123,456").build();
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    final TropixFile file1 = new TropixFile();
    file1.setName("file1.mzxml");
    final TropixFile file2 = new TropixFile();
    file2.setName("file2.mzxml");
    EasyMock.expect(mgfDownloadHelper.getMzXMLTropixFile("123")).andReturn(file1);
    EasyMock.expect(mgfDownloadHelper.getMzXMLTropixFile("456")).andReturn(file2);
    mgfDownloadHelper.writeMgf(EasyMock.same(file1), EasyMock.eq("DEFAULT"), EasyMockUtils.copy(new ByteArrayInputStream("f1c".getBytes())));
    mgfDownloadHelper.writeMgf(EasyMock.same(file2), EasyMock.eq("DEFAULT"), EasyMockUtils.copy(new ByteArrayInputStream("f2c".getBytes())));
    EasyMock.replay(mgfDownloadHelper);
    new BulkMgfDownloadHanderImpl(mgfDownloadHelper).processDownloadRequest(outputStream, Functions.forMap(args));
    EasyMock.verify(mgfDownloadHelper);
    verifyZipFile(outputStream.toByteArray());
  }

  private void verifyZipFile(final byte[] bytes) {
    final InputStream inputStream = new ByteArrayInputStream(bytes);
    File tempDirectory = null;
    try {
      tempDirectory = ZIP_UTILS.unzipToTempDirectory(inputStream);
      final File file1 = new File(tempDirectory, "file1.mgf");
      final File file2 = new File(tempDirectory, "file2.mgf");
      assert FILE_UTILS.readFileToString(file1).equals("f1c");
      assert FILE_UTILS.readFileToString(file2).equals("f2c");
    } finally {
      IO_UTILS.closeQuietly(inputStream);
      FILE_UTILS.deleteDirectoryQuietly(tempDirectory);
    }
  }

}
