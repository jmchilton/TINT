package edu.umn.msi.tropix.jobs.newfile;

import java.util.UUID;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;

import edu.umn.msi.tropix.files.NewFileMessageQueue.NewFileMessage;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.models.TropixFile;

// TODO: Move into new package
public class BaseNewFileProcessorTest {
  private Credential credential;
  private TropixFile newFile;
  private String parentFileId;
  private NewFileMessage message;
  
  protected void sendMessage(final NewFileProcessor newFileProcessor) {
    newFileProcessor.processFile(message, newFile);
  }

  @BeforeMethod(groups = "unit")
  public void init() {
    credential = Credentials.getMock();
    newFile = new TropixFile();
    newFile.setId(UUID.randomUUID().toString());
    newFile.setFileId(UUID.randomUUID().toString());
    parentFileId = UUID.randomUUID().toString();
    message = new NewFileMessage();
    message.setCredential(credential);
    message.setFileId(newFile.getFileId());
    message.setObjectId(newFile.getId());
    message.setParentId(parentFileId);
  }
  
  protected String expectParentId() {
    return EasyMock.eq(parentFileId);
  }
  
  protected String expectFileObjectId() {
    return EasyMock.eq(newFile.getId());
  }

  protected String expectFileId() {
    return EasyMock.eq(newFile.getFileId());
  }
  
  protected TropixFile getFile() {
    return newFile;
  }
  
}
