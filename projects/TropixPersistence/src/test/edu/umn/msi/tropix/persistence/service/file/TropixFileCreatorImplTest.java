package edu.umn.msi.tropix.persistence.service.file;

import java.util.UUID;

import org.easymock.Capture;
import org.easymock.classextension.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;
import edu.umn.msi.tropix.persistence.service.file.NewFileMessageQueue.NewFileMessage;

public class TropixFileCreatorImplTest {

  @Test
  public void testCreate() {
    final TropixObjectService objectService = EasyMock.createMock(TropixObjectService.class);
    final TropixFile file = new TropixFile();
    final TropixFile resultFile = new TropixFile();
    resultFile.setId(UUID.randomUUID().toString());
    resultFile.setFileId(UUID.randomUUID().toString());
    EasyMock.expect(
        objectService.createFile(EasyMock.eq("userId"), EasyMock.eq("foldId"), EasyMock.same(file), EasyMock.eq("fileTypeId"))).andReturn(resultFile);
    EasyMock.expect(objectService.getOwnerId(resultFile.getId())).andReturn("ownerUserId");
    final NewFileMessageQueue queue = EasyMock.createMock(NewFileMessageQueue.class);
    final Capture<NewFileMessage> messageCapture = EasyMockUtils.newCapture();
    queue.newFile(EasyMock.capture(messageCapture));
    
    final TropixFileCreatorImpl creator = new TropixFileCreatorImpl(objectService);
    creator.setNewFileMessageQueue(queue);
    EasyMock.replay(objectService, queue);
    assert resultFile == creator.createFile("userId", "foldId", file, "fileTypeId");
    EasyMock.verify(objectService, queue);
    
    NewFileMessage message = messageCapture.getValue();
    assert message.getFileId().equals(resultFile.getFileId());
    assert message.getObjectId().equals(resultFile.getId());
    assert message.getOwnerId().equals("ownerUserId");
    assert message.getParentId().equals("foldId");    
  }
  
  
}
