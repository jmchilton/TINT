package edu.umn.msi.tropix.persistence.service.file;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.apache.camel.Produce;

import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;
import edu.umn.msi.tropix.persistence.service.file.NewFileMessageQueue.NewFileMessage;

@ManagedBean
public class TropixFileCreatorImpl implements TropixFileCreator {
  private final TropixObjectService tropixObjectService;
  private NewFileMessageQueue queue;

  @Produce(uri = NewFileMessageQueue.ROUTE)
  public void setNewFileMessageQueue(final NewFileMessageQueue queue) {
    this.queue = queue;
  }
  
  @Inject
  public TropixFileCreatorImpl(final TropixObjectService tropixObjectService) {
    this.tropixObjectService = tropixObjectService;
  }

  public TropixFile createFile(final String userGridId, 
                               final String destinationId, 
                               final TropixFile file, 
                               final String fileTypeId) {
    final TropixFile savedFile = tropixObjectService.createFile(userGridId, destinationId, file, fileTypeId);
    final NewFileMessage message = new NewFileMessage();
    final String objectId = savedFile.getId();
    message.setObjectId(objectId);
    message.setFileId(savedFile.getFileId());
    message.setParentId(destinationId);
    message.setOwnerId(tropixObjectService.getOwnerId(objectId));
    
    queue.newFile(message);
    return savedFile;
    
  }
  
}
