package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

import com.google.common.collect.Iterators;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.proteomics.conversion.Scan;
import edu.umn.msi.tropix.proteomics.conversion.impl.MgfToMzXMLConverterStreamingImpl.MgfParentFileExtracter;
import edu.umn.msi.tropix.proteomics.conversion.impl.MzXmlStreamWriterUtils.MzXmlParentFileType;

public class DefaultMgfParentFileExtracterImpl extends MgfParentFileExtracter {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  
  public Iterator<MzxmlParentFile> getAllParentFiles(File sourceFile) {
    final FileInputStream inputStream = FILE_UTILS.getFileInputStream(sourceFile);
    final String sha1 = ConversionUtils.getSHA1(inputStream); // This will close the stream;
    final String sourceFileName = sourceFile.getName();
    final MzXmlParentFileType fileType = MzXmlParentFileType.PROCESSED;
    final MzxmlParentFile parentFile = new MzxmlParentFile(sha1, sourceFileName, fileType);

    return Iterators.forArray(parentFile);
  }

  public String getParentFileSha1ForScan(Scan scan) {
    return null;
  }

}
