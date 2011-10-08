package edu.umn.msi.tropix.files.creator;

import javax.annotation.Nullable;

import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.models.TropixFile;

public interface TropixFileCreator {

  TropixFile createFile(final Credential credential, @Nullable String folderId, TropixFile file, @Nullable String fileTypeId);

}
