package edu.umn.msi.tropix.proteomics.conversion;

public class MgfToMzxmlOptions {
  private boolean useRawFilesAsParents = false;

  public boolean isUseRawFilesAsParents() {
    return useRawFilesAsParents;
  }

  public void setUseRawFilesAsParents(final boolean useRawFilesAsParents) {
    this.useRawFilesAsParents = useRawFilesAsParents;
  }
}
