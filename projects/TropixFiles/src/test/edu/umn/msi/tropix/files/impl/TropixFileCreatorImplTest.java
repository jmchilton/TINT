package edu.umn.msi.tropix.files.impl;

import java.util.UUID;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.files.NewFileMessageQueue;
import edu.umn.msi.tropix.files.NewFileMessageQueue.NewFileMessage;
import edu.umn.msi.tropix.files.creator.TropixFileCreatorImpl;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.models.FileType;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.persistence.service.FileTypeService;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;

public class TropixFileCreatorImplTest {
  private NewFileMessageQueue queue;
  private Capture<NewFileMessage> messageCapture;
  private TropixObjectService objectService;
  private FileTypeService fileTypeService;
  private TropixFile file;
  private TropixFile resultFile;
  private String fileTypeId;
  private String fileName;
  private Credential credential;
  
  @Test(groups = "unit")
  public void testAssingsFileTypeIfNeeded() {
    fileTypeId = null;
    fileName = "FooCow.EBWT.zip";
    final FileType fileType = new FileType();
    fileType.setId(UUID.randomUUID().toString());
    fileTypeId = fileType.getId();
    EasyMock.expect(fileTypeService.getFileTypeForName(credential.getIdentity(), fileName)).andReturn(fileType);
    create(null);
  }

  @BeforeMethod(groups = "unit")
  public void init() {
    credential = Credentials.getMock();
    fileName = UUID.randomUUID().toString();
    objectService = EasyMock.createMock(TropixObjectService.class);
    fileTypeService = EasyMock.createMock(FileTypeService.class);
    file = new TropixFile();
    resultFile = new TropixFile();
    resultFile.setId(UUID.randomUUID().toString());
    resultFile.setFileId(UUID.randomUUID().toString());
    queue = EasyMock.createMock(NewFileMessageQueue.class);
    messageCapture = EasyMockUtils.newCapture();
    queue.newFile(EasyMock.capture(messageCapture));
  }
  
  private void create(final String inputFileTypeId) {
    file.setName(fileName);
    final TropixFileCreatorImpl creator = new TropixFileCreatorImpl(objectService, fileTypeService, null);
    EasyMock.expect(
        objectService.createFile(EasyMock.eq(credential.getIdentity()), EasyMock.eq("foldId"), EasyMock.same(file), EasyMock.eq(fileTypeId))).andStubReturn(resultFile);
    EasyMock.expect(objectService.getOwnerId(resultFile.getId())).andStubReturn("ownerUserId");
    creator.setNewFileMessageQueue(queue);
    EasyMock.replay(objectService, fileTypeService, queue);
    assert resultFile == creator.createFile(credential, "foldId", file, inputFileTypeId);
    EasyMock.verify(objectService, fileTypeService, queue);    
  }
  
  @Test(groups = "unit")
  public void testCreate() {
    fileTypeId = UUID.randomUUID().toString();
    create(fileTypeId);
    NewFileMessage message = messageCapture.getValue();
    assert message.getFileId().equals(resultFile.getFileId());
    assert message.getObjectId().equals(resultFile.getId());
    assert message.getOwnerId().equals("ownerUserId");
    assert message.getParentId().equals("foldId");    
  }
  
  
}
