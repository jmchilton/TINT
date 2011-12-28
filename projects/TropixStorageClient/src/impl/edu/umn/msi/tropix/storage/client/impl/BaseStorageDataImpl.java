package edu.umn.msi.tropix.storage.client.impl;

import java.util.UUID;

import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.storage.client.ModelStorageData;

public abstract class BaseStorageDataImpl implements ModelStorageData {
  private final TropixFile tropixFile;
  private final Credential credential;

  public BaseStorageDataImpl(final TropixFile tropixFile, final Credential credential) {
    this.tropixFile = tropixFile;
    this.credential = credential;
    if(tropixFile.getFileId() == null) {
      tropixFile.setFileId(UUID.randomUUID().toString());
    }
  }
  
  protected Credential getCredential() {
    return credential;
  }

  protected String getIdentity() {
    return credential == null ? null : credential.getIdentity();
  }
  
  protected String getFileId() {
    return tropixFile.getFileId();
  }

  public TropixFile getTropixFile() {
    return tropixFile;
  }

  public String getDataIdentifier() {
    return tropixFile.getFileId();
  }
  
}
