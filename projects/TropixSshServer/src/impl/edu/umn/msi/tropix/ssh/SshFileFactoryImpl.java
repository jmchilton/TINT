package edu.umn.msi.tropix.ssh;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.server.SshFile;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.InputStreamCoercible;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.persistence.service.FolderService;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;
import edu.umn.msi.tropix.persistence.service.file.TropixFileCreator;
import edu.umn.msi.tropix.storage.core.StorageManager;
import edu.umn.msi.tropix.storage.core.StorageManager.UploadCallback;

@ManagedBean
public class SshFileFactoryImpl implements SshFileFactory {
  private static final Log LOG = LogFactory.getLog(SshFileFactoryImpl.class);
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private final TropixObjectService tropixObjectService;
  private final TropixFileCreator tropixFileCreator;
  private final StorageManager storageManager;
  private final FolderService folderService;

  @Inject
  public SshFileFactoryImpl(final TropixObjectService tropixObjectService,
                            final TropixFileCreator tropixFileCreator,
                            final StorageManager storageManager,
                            final FolderService folderService) {
    this.tropixObjectService = tropixObjectService;
    this.tropixFileCreator = tropixFileCreator;
    this.storageManager = storageManager;
    this.folderService = folderService;
  }

  public SshFile getFile(final String identity, final String virtualPath) {
    return new SshFileImpl(identity, virtualPath);
  }

  class SshFileImpl implements SshFile {
    private String identity;
    private String virtualPath;
    private TropixObject object;

    private void initObject() {
      if(object == null) {
        log("initialize virutal path");
        object = getTropixObject(virtualPath);
      }
    }

    private TropixObject getTropixObject(final String path) {
      final List<String> pathPieces = Utils.pathPieces(path);
      return tropixObjectService.getPath(identity, Iterables.toArray(pathPieces, String.class));
    }

    SshFileImpl(final String identity, final String virtualPath) {
      this.identity = identity;
      this.virtualPath = virtualPath;
    }

    public String getAbsolutePath() {     
      final String absolutePath = Utils.cleanAndExpandPath(virtualPath);
      log(String.format("getAbsolutePath called, result is %s", absolutePath));
      return absolutePath;
    }

    public String getName() {
      final String name = Utils.name(virtualPath);
      log(String.format("getName called, result is %s", name));
      return name;
    }

    private void verifyObjectExists() {
      Preconditions.checkState(doesExist(), String.format("Operation called on non-existent object with path %s by user %s", virtualPath, identity));
    }

    public boolean isDirectory() {
      log("isDirectory");
      if(doesExist()) {
        initObject();
        return !(object instanceof TropixFile);
      } else {
        return false;        
      }
    }

    public boolean isFile() {
      log("isDirectory");
      if(doesExist()) {
        initObject();
        return (object instanceof TropixFile);
      } else {
        return false;        
      }
    }

    public boolean doesExist() {
      log("doesExist");
      initObject();
      final boolean doesExist = object != null;
      log(String.format("Checking doesExist %b", doesExist));
      return doesExist;
    }

    public boolean isRemovable() {
      log("isRemovable");
      return true;
    }

    public SshFile getParentFile() {
      log("getParentFile");
      return getFile(identity, Utils.parent(virtualPath));
    }

    public long getLastModified() {
      log("getLastModified");
      if(isTropixFile()) {
        return storageManager.getDateModified(getFileId(), identity);
      } else {
        return 0L;
      }
    }

    public boolean setLastModified(final long time) {
      log("setLastModified");
      return false;
    }

    private boolean isTropixFile() {
      initObject();
      return object instanceof TropixFile;
    }
    
    private String getFileId() {
      Preconditions.checkState(isTropixFile(), "getFileId called for non-file object");
      final TropixFile file = (TropixFile) object;
      return file.getFileId();
    }

    public long getSize() {
      log("getSize");
      if(isTropixFile()) {
        return storageManager.getLength(getFileId(), identity);
      } else {
        return 0;
      }
    }

    private InputStreamCoercible readFile() {
      return storageManager.download(getFileId(), identity);
    }

    // TODO: Actually truncate
    public void truncate() throws IOException {
      log("truncate");
    }

    public boolean delete() {
      log("delete");
      initObject();
      tropixObjectService.delete(identity, object.getId());
      return true;
    }

    public boolean move(final SshFile destination) {
      boolean moved = false;
      log(String.format("In move - moved? %b", moved));
      return moved;
    }

    public boolean isReadable() {
      final boolean readable = true;
      log(String.format("Checking is readable - %b", readable));
      return readable;
    }
    
    private void log(final String message) {
      if(LOG.isDebugEnabled()) {
        LOG.debug(String.format("For virtual path <%s> - %s", virtualPath, message));
      }
    }

    public boolean isWritable() {
      log("Checking is writable");
      initObject();
      // TODO: Refine, objects not writable but are sort of directories
      // return isDirectory();
      return object == null || object instanceof Folder;
    }

    public void handleClose() throws IOException {
    }

    // TODO: Check uniqueness
    public boolean mkdir() {
      log("Creating directory");
      final TropixObject parentObject = getParentFolder();
      if(!(parentObject instanceof Folder)) {
        return false;
      } else {
        final Folder newFolder = new Folder();
        newFolder.setCommitted(true);
        newFolder.setName(Utils.name(virtualPath));
        folderService.createFolder(identity, parentObject.getId(), newFolder);
        return true;
      }
    }

    private TropixObject getParentFolder() {
      final String parentPath = Utils.parent(virtualPath);
      final TropixObject parentObject = getTropixObject(parentPath);
      return parentObject;
    }

    public OutputStream createOutputStream(final long offset) throws IOException {
      final TropixObject parentObject = getParentFolder();
      if(!(parentObject instanceof Folder)) {
        throw new IllegalStateException("Invalid path to create output stream from " + virtualPath);
      } else {
        final String newFileId = UUID.randomUUID().toString();
        System.out.println("Calling with " + newFileId);
        final UploadCallback uploadCallback = storageManager.upload(newFileId, identity);
        Preconditions.checkNotNull(uploadCallback);
        final File tempFile = FILE_UTILS.createTempFile(); 
        return new FileOutputStream(tempFile) {
          @Override
          public void close() throws IOException {
            try {
              super.close();
            } finally {
              try {
                uploadCallback.onUpload(FILE_UTILS.getFileInputStream(tempFile));
                final Folder parentFolder = (Folder) parentObject;
                final TropixFile tropixFile = new TropixFile();
                tropixFile.setName(getName());
                tropixFile.setCommitted(true);
                tropixFile.setFileId(newFileId);
                tropixFileCreator.createFile(identity, parentFolder.getId(), tropixFile, null);
              } finally {
                FILE_UTILS.deleteQuietly(tempFile);
              }
            }
          }
        };
        
      }
    }

    public InputStream createInputStream(final long offset) throws IOException {
      final InputStream inputStream = readFile().asInputStream();
      inputStream.skip(offset);
      return inputStream;
    }

    public List<SshFile> listSshFiles() {
      initObject();
      log("listSshFiles");
      final TropixObject[] objects = tropixObjectService.getChildren(identity, object.getId());
      final Map<String, Boolean> uniqueName = Maps.newHashMap();
      for(TropixObject object : objects) {
        final String objectName = object.getName();
        if(!uniqueName.containsKey(objectName)) {
          uniqueName.put(objectName, true);
        } else {
          uniqueName.put(objectName, false);
        }
      }      
      final ImmutableList.Builder<SshFile> children = ImmutableList.builder();
      for(TropixObject object : objects) {
        final String name = object.getName();
        final String derivedName = uniqueName.get(name) ? name : String.format("%s [id:%s]", name, object.getId());
        final String childName = Utils.join(virtualPath, derivedName);
        LOG.debug(String.format("Creating child with name [%s]", childName));
        children.add(getFile(identity, childName));
      }
      return children.build();
    }

    public boolean create() throws IOException {
      log("Attempting create");
      return !isTropixFile();
    }

    public boolean isExecutable() {
      log("Checking executable");
      return true; // TODO: Revert
      //throw new UnsupportedOperationException();
    }

  }


}
