package edu.umn.msi.tropix.persistence.service.file;

import javax.annotation.Nullable;

import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.persistence.aop.Modifies;
import edu.umn.msi.tropix.persistence.aop.UserId;

public interface TropixFileCreator {

  TropixFile createFile(@UserId String userGridId, @Nullable @Modifies String folderId, TropixFile file, @Nullable String fileTypeId);

}
