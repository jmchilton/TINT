package edu.umn.msi.tropix.files;

import java.io.Serializable;

import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.messaging.AsyncMessage;
import edu.umn.msi.tropix.messaging.MessageRouting;

public interface NewFileMessageQueue {
  String ROUTE = MessageRouting.QUEUE_PREFIX + "newfile";
  
  public static class NewFileMessage implements Serializable {
    private String objectId;
    private String fileId;
    private String parentId;
    private String ownerId;
    private Credential credential;

    public Credential getCredential() {
      return credential;
    }

    public void setCredential(final Credential credential) {
      this.credential = credential;
    }

    @Deprecated
    public String getOwnerId() {
      return ownerId;
    }

    @Deprecated
    public void setOwnerId(final String ownerId) {
      this.ownerId = ownerId;
    }

    public String getObjectId() {
      return objectId;
    }

    public void setObjectId(final String objectId) {
      this.objectId = objectId;
    }

    public String getFileId() {
      return fileId;
    }

    public void setFileId(final String fileId) {
      this.fileId = fileId;
    }

    public String getParentId() {
      return parentId;
    }

    public void setParentId(final String parentId) {
      this.parentId = parentId;
    }

  }
  
  @AsyncMessage 
  void newFile(NewFileMessage message);
  
}
