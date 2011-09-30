package edu.umn.msi.tropix.common.jobqueue.cloud;

public class AccessKey {
  private String accessKey;
  private String secretAccessKey;
  
  public AccessKey(final String accessKey, final String secretAccessKey) {
    this.accessKey = accessKey;
    this.secretAccessKey = secretAccessKey;
  }

  public String getAccessKey() {
    return accessKey;
  }
  
  public String getSecretAccessKey() {
    return secretAccessKey;
  }
  
}
