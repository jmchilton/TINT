package edu.umn.msi.tropix.client.services;

public class ScaffoldGridService extends QueueGridService {
  private String scaffoldVersion;

  public String getScaffoldVersion() {
    return scaffoldVersion;
  }

  public void setScaffoldVersion(final String scaffoldVersion) {
    this.scaffoldVersion = scaffoldVersion;
  }

}
