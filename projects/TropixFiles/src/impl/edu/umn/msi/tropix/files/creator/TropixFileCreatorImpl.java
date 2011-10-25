package edu.umn.msi.tropix.files.creator;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.apache.camel.Produce;

import edu.umn.msi.tropix.files.NewFileMessageQueue;
import edu.umn.msi.tropix.files.NewFileMessageQueue.NewFileMessage;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.models.FileType;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.persistence.service.FileTypeService;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;

@ManagedBean
public class TropixFileCreatorImpl implements TropixFileCreator {
  private final TropixObjectService tropixObjectService;
  private final FileTypeService fileTypeService;
  private NewFileMessageQueue queue;

  @Produce(uri = NewFileMessageQueue.ROUTE)
  public void setNewFileMessageQueue(final NewFileMessageQueue queue) {
    this.queue = queue;
  }
  
  @Inject
  public TropixFileCreatorImpl(final TropixObjectService tropixObjectService,
                               final FileTypeService fileTypeService) {
    this.tropixObjectService = tropixObjectService;
    this.fileTypeService = fileTypeService;
  }

  public TropixFile createFile(final Credential credential, 
                               final String destinationId, 
                               final TropixFile file, 
                               final String inputFileTypeId) {
    final String fileTypeId;
    if(inputFileTypeId == null) {
      final String fileName = file.getName();
      final FileType fileType = fileTypeService.getFileTypeForName(credential.getIdentity(), fileName);
      fileTypeId = fileType != null ? fileType.getId() : null;              
    } else {
      fileTypeId = inputFileTypeId;
    }
    final TropixFile savedFile = tropixObjectService.createFile(credential.getIdentity(), destinationId, file, fileTypeId);
    final NewFileMessage message = new NewFileMessage();
    final String objectId = savedFile.getId();
    message.setObjectId(objectId);
    message.setFileId(savedFile.getFileId());
    message.setParentId(destinationId);
    message.setOwnerId(tropixObjectService.getOwnerId(objectId));
    message.setCredential(credential);
    queue.newFile(message);
    return savedFile;
    
  }
  
}
