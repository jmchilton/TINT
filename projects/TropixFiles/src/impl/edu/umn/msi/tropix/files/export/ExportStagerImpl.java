package edu.umn.msi.tropix.files.export;

import java.io.File;
import java.util.UUID;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.execution.process.ProcessConfiguration;
import edu.umn.msi.tropix.common.execution.process.Processes;
import edu.umn.msi.tropix.common.io.FileContext;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.HasStreamInputContext;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;
import edu.umn.msi.tropix.storage.core.StorageManager;

@ManagedBean
public class ExportStagerImpl implements ExportStager {
  private static final Log LOG = LogFactory.getLog(ExportStagerImpl.class);
  private final StorageManager storageManager;
  private final TropixObjectService tropixObjectService;
  private String exportDirectory;

  @Inject
  public ExportStagerImpl(final StorageManager storageManager,
      @Value("${file.export.directory}") final String exportDirectory,
      final TropixObjectService tropixObjectService) {
    this.storageManager = storageManager;
    this.tropixObjectService = tropixObjectService;
    this.exportDirectory = exportDirectory;
  }

  public File stageFilesForExport(String userId, String[] ids) {
    final String subdirectory = UUID.randomUUID().toString();
    LOG.info("Creating directory in " + exportDirectory + " with name " + subdirectory + " for export staging for files "
        + Joiner.on(",").join(ids));
    final File stagingDirectory = new File(exportDirectory, subdirectory);
    stageFilesForExport(userId, stagingDirectory, ids);
    return stagingDirectory;
  }

  private void stageFilesForExport(final String userId, final File stagingDirectory, final String[] ids) {
    final TropixObject[] files = (TropixObject[]) tropixObjectService.load(userId, ids, TropixObjectTypeEnum.FILE);

    // final List<TropixFile> files = fileService.loadTropixFilesForFileIds(userId, fileIds);
    if(!stagingDirectory.exists()) {
      if(!stagingDirectory.mkdirs()) {
        throw new RuntimeException("Failed to create staging directory for export.");
      }
    }
    if(files.length == 0) {
      FileUtilsFactory.getInstance().writeStringToFile(new File(stagingDirectory, "moocow"), "Hello World!");
    }

    for(final TropixObject fileObject : files) {
      final TropixFile file = (TropixFile) fileObject;
      final String storedFilePath = getStoredFilePath(userId, file);
      stageFile(file, stagingDirectory, storedFilePath);
    }
  }

  private void stageFile(final TropixFile file, final File directory, final String storedFilePath) {
    final ProcessConfiguration processConfiguration = new ProcessConfiguration();
    processConfiguration.setApplication("ln");
    processConfiguration.setArguments(Lists.newArrayList("-s", storedFilePath, new File(directory, file.getName()).getAbsolutePath()));
    try {
      final int returnCode = Processes.getDefaultFactory().createProcess(processConfiguration).waitFor();
      if(returnCode != 0) {
        throw new RuntimeException("Failed to create symbolic link for " + file.getName());
      }
    } catch(InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private String getStoredFilePath(final String userId, final TropixFile tropixFile) {
    final HasStreamInputContext context = storageManager.download(tropixFile.getFileId(), userId, false);
    if(!(context instanceof FileContext)) {
      throw new UnsupportedOperationException(); // Not yet supported, could be easily...
    }
    final FileContext fileContext = (FileContext) context;
    final File storedFile = fileContext.asFile();
    return storedFile.getAbsolutePath();
  }

}
