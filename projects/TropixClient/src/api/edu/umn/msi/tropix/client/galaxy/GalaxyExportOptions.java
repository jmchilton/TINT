package edu.umn.msi.tropix.client.galaxy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GalaxyExportOptions implements Serializable {
  private List<String> fileObjectIds = new ArrayList<String>();
  private boolean makePrivate;
  private String name;

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public List<String> getFileObjectIds() {
    return fileObjectIds;
  }

  public void setFileObjectIds(final List<String> fileObjectIds) {
    this.fileObjectIds = fileObjectIds;
  }

  public boolean isMakePrivate() {
    return makePrivate;
  }

  public void setMakePrivate(final boolean makePrivate) {
    this.makePrivate = makePrivate;
  }

}