package edu.umn.msi.tropix.proteomics.conversion.impl;

import edu.umn.msi.tropix.proteomics.conversion.impl.MzXmlStreamWriterUtils.MzXmlParentFileType;

public class MzxmlParentFile {
  private final String sha1;
  private final String sourceFileName;
  private final MzXmlParentFileType fileType;

  public MzxmlParentFile(final String sha1, final String sourceFileName, final MzXmlParentFileType fileType) {
    this.sha1 = sha1;
    this.sourceFileName = sourceFileName;
    this.fileType = fileType;
  }

  public String getSha1() {
    return sha1;
  }

  public String getSourceFileName() {
    return sourceFileName;
  }

  public MzXmlParentFileType getFileType() {
    return fileType;
  }

}
