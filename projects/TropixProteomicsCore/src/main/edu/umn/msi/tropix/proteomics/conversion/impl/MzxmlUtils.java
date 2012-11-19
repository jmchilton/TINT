package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.io.File;
import java.io.InputStream;

import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StringUtils;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;

public class MzxmlUtils {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();

  public static String guessMzxmlName(final File mzxmlFile, final String defaultName) {
    final InputStream mzxmlStream = FILE_UTILS.getFileInputStream(mzxmlFile);
    try {
      final XMLStreamReader reader = XMLStreamReaderUtils.get(mzxmlStream);
      String bestGuess = null;
      while(!XMLStreamReaderUtils.isStartOfElement(reader, "dataProcessing")) {
        XMLStreamReaderUtils.next(reader);
        if(XMLStreamReaderUtils.isStartOfElement(reader, "parentFile")) {
          final String fileName = XMLStreamReaderUtils.getAttributeValue(reader, "fileName");
          if(!StringUtils.hasText(fileName)) {
            continue;
          }
          String parentName = FilenameUtils.getBaseName(fileName);
          if(StringUtils.hasText(parentName) && bestGuess == null) {
            bestGuess = parentName;
          } else if(StringUtils.hasText(parentName)) {
            // Only guess a name if there is one clear option
            bestGuess = null;
            break;
          }
        }
      }
      if(bestGuess == null) {
        bestGuess = defaultName;
      }
      return bestGuess;
    } finally {
      IO_UTILS.closeQuietly(mzxmlStream);
    }
  }
}
