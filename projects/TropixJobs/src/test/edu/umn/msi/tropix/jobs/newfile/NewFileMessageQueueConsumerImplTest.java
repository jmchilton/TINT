package edu.umn.msi.tropix.jobs.newfile;

import java.util.List;
import java.util.UUID;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.files.NewFileMessageQueue.NewFileMessage;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.models.FileType;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.utils.StockFileExtensionEnum;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.persistence.service.TropixObjectLoaderService;

public class NewFileMessageQueueConsumerImplTest {

  static class TestProcessor implements NewFileProcessor {
    private List<NewFileMessage> messages = Lists.newArrayList();

    public void processFile(final NewFileMessage message, final TropixFile tropixFile) {
      messages.add(message);
    }

    void assertProcessedMessage(final NewFileMessage message) {
      assert messages.contains(message);
    }

  }

  @ForExtension(StockFileExtensionEnum.THERMO_RAW)
  public static class RawProcessor extends TestProcessor {
  }

  @ForExtension(StockFileExtensionEnum.FASTA)
  public static class FastaProcessor extends TestProcessor {
  }

  @Test(groups = "unit")
  public void testDispatching() {
    final TropixObjectLoaderService loader = EasyMock.createMock(TropixObjectLoaderService.class);
    final NewFileMessageQueueConsumerImpl consumer = new NewFileMessageQueueConsumerImpl(loader);
    final RawProcessor rawProcessor = new RawProcessor();
    final FastaProcessor fastaProcessor = new FastaProcessor();
    consumer.processBeans(Lists.<Object>newArrayList(rawProcessor, fastaProcessor));
    final NewFileMessage message1 = new NewFileMessage();
    // message1.setOwnerId(UUID.randomUUID().toString());
    message1.setObjectId(UUID.randomUUID().toString());
    message1.setCredential(Credentials.getMock());
    final TropixFile rawFile = newFileOfType(StockFileExtensionEnum.THERMO_RAW);

    EasyMock.expect(loader.load(message1.getCredential().getIdentity(), message1.getObjectId(), TropixObjectTypeEnum.FILE)).andReturn(rawFile);
    EasyMock.replay(loader);
    consumer.newFile(message1);
    rawProcessor.assertProcessedMessage(message1);
  }

  private TropixFile newFileOfType(final StockFileExtensionEnum extension) {
    final TropixFile file = new TropixFile();
    final FileType fileType = new FileType();
    fileType.setExtension(extension.getExtension());
    file.setFileType(fileType);
    return file;
  }

}
