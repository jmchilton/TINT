package edu.umn.msi.tropix.ssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.apache.commons.io.output.ProxyOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.server.SshFile;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.InputStreamCoercible;
import edu.umn.msi.tropix.files.creator.TropixFileCreator;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.locations.Location;
import edu.umn.msi.tropix.persistence.service.FolderService;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;
import edu.umn.msi.tropix.storage.core.StorageManager;

@ManagedBean
public class SshFileFactoryImpl implements SshFileFactory {
  private static final Log LOG = LogFactory.getLog(SshFileFactoryImpl.class);
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private final TropixObjectService tropixObjectService;
  private final TropixFileCreator tropixFileCreator;
  private final StorageManager storageManager;
  private final FolderService folderService;

  private static final Set<String> META_OBJECT_PATHS = Sets.newHashSet("/", "/My Home", "/My Group Folders");

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

  public SshFile getFile(final Credential credential, final String virtualPath) {
    return new SshFileImpl(credential, virtualPath);
  }

  class SshFileImpl implements SshFile {
    private Credential credential;
    private String identity;
    private String virtualPath;
    private TropixObject object;
    private Location location = null;

    private void initObject() {
      if(object == null) {
        log("initialize virutal path");
        object = getTropixObject(virtualPath);
      }
    }

    private TropixObject getTropixObject(final String path) {
      List<String> pathPieces = Utils.pathPieces(path);
      if(pathPieces.size() > 0 && pathPieces.get(0).equals("My Home")) {
        pathPieces = pathPieces.subList(1, pathPieces.size());
        return tropixObjectService.getPath(identity, Iterables.toArray(pathPieces, String.class));
      } else {
        return null;
      }
    }

    SshFileImpl(final Credential credential, final String virtualPath) {
      this.credential = credential;
      this.identity = credential.getIdentity();
      this.virtualPath = virtualPath;
    }

    // Remove trailing / for directories, is this expected?
    public String getAbsolutePath() {
      final String absolutePath = Utils.cleanAndExpandPath(virtualPath);
      log(String.format("getAbsolutePath called, result is %s", absolutePath));
      return absolutePath;
    }

    // What should this return for root?
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
      boolean doesExist = false;
      if(isMetaLocation()) {
        doesExist = true;
      } else {
        initObject();
        doesExist = object != null;
        log(String.format("Checking doesExist %b", doesExist));
      }
      return doesExist;
    }

    public boolean isRemovable() {
      log("isRemovable");
      return !isMetaLocation() && doesExist();
    }

    public SshFile getParentFile() {
      log("getParentFile");
      return getFile(credential, Utils.parent(virtualPath));
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
      // log("setLastModified");
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

    public void truncate() throws IOException {
      log("truncate");
      if(doesExist()) {
        // TODO: Handle this better
        throw new IllegalStateException("Cannot truncate this file, please delete and add a new file.");
      }
    }

    public boolean delete() {
      log("delete");
      if(isMetaLocation() || !doesExist()) {
        return false;
      } else {
        initObject();
        tropixObjectService.delete(identity, object.getId());
        return true;
      }
    }

    public boolean move(final SshFile destination) {
      log("move");
      if(isMetaLocation()) {
        return false;
      }
      initObject();
      boolean moved = false;
      if(parentIsFolder() && destination instanceof SshFileImpl) {
        final SshFileImpl destinationFile = (SshFileImpl) destination;
        final boolean validDestination = !destinationFile.doesExist() && destinationFile.parentIsFolder();
        System.out.println(destinationFile.doesExist());
        log("Move valid destination - " + validDestination);
        if(validDestination) {
          final String objectId = object.getId();
          tropixObjectService.move(identity, objectId, destinationFile.parentAsFolder().getId());
          final TropixObject object = tropixObjectService.load(identity, objectId);
          object.setName(destination.getName());
          tropixObjectService.update(identity, object);
          moved = true;
        }
      }
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
      return object instanceof Folder || (object == null && parentIsFolder());
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

    private boolean parentIsFolder() {
      return getParentFolder() instanceof Folder;
    }

    private Folder parentAsFolder() {
      return (Folder) getParentFolder();
    }

    public OutputStream createOutputStream(final long offset) throws IOException {
      if(!parentIsFolder()) {
        final String errorMessage = "Invalid path to create output stream from " + virtualPath;
        LOG.warn(errorMessage);
        throw new IllegalStateException(errorMessage);
      } else if(offset > 0) {
        final String errorMessage = "Server only supports offsets of 0 - path " + virtualPath;
        LOG.warn(errorMessage);
        throw new IllegalStateException(errorMessage);
      } else {
        final String newFileId = UUID.randomUUID().toString();
        final OutputStream outputStream = storageManager.prepareUploadStream(newFileId, identity);
        return new ProxyOutputStream(outputStream) {
          @Override
          public void close() throws IOException {
            try {
              super.close();
            } finally {
              LOG.debug("Preparing file for tropixfilecreator");
              final Folder parentFolder = parentAsFolder();
              final TropixFile tropixFile = new TropixFile();
              tropixFile.setName(getName());
              tropixFile.setCommitted(true);
              tropixFile.setFileId(newFileId);
              tropixFileCreator.createFile(credential, parentFolder.getId(), tropixFile, null);
              LOG.debug("File created " + virtualPath);
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
      final ImmutableList.Builder<SshFile> children = ImmutableList.builder();
      if(isRoot()) {
        children.add(getFile(credential, "My Home"));
        children.add(getFile(credential, "My Group Folders"));
      } else {
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
        for(TropixObject object : objects) {
          final String name = object.getName();
          final String derivedName = uniqueName.get(name) ? name : String.format("%s [id:%s]", name, object.getId());
          final String childName = Utils.join(virtualPath, derivedName);
          LOG.debug(String.format("Creating child with name [%s]", childName));
          children.add(getFile(credential, childName));
        }
      }
      return children.build();
    }

    private boolean isRoot() {
      return "/".equals(getAbsolutePath());
    }

    private boolean isMetaLocation() {
      return META_OBJECT_PATHS.contains(getAbsolutePath());
    }

    public boolean create() throws IOException {
      log("Attempting create");
      return !isTropixFile();
    }

    public boolean isExecutable() {
      log("Checking executable");
      return true; // TODO: Revert
      // throw new UnsupportedOperationException();
    }

  }

}
