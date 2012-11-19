package edu.umn.msi.tropix.proteomics.report;

import java.io.File;
import java.io.InputStream;
import java.util.Set;

import com.google.common.collect.Sets;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.proteomics.xml.AbstractXmlIterator;

public class PepXmlUtils {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();

  public static Set<File> loadInputFiles(final File pepXmlFile) {
    final InputStream pepXmlInputStream = FILE_UTILS.getFileInputStream(pepXmlFile);
    try {
      return Sets.<File>newHashSet(new PepXmlInputIteratorImpl(pepXmlInputStream));
    } finally {
      IO_UTILS.closeQuietly(pepXmlInputStream);
    }
  }

  private static class PepXmlInputIteratorImpl extends AbstractXmlIterator<File> {

    protected PepXmlInputIteratorImpl(final InputStream inputStream) {
      super(inputStream, "msms_run_summary");
    }

    protected File parseNext() {
      final String baseName = getAttributeValue("base_name");
      final String rawData = getAttributeValue("raw_data");
      final String rawDataType = getAttributeValue("raw_data_type");
      final File[] possibilities = new File[] {new File(baseName), new File(baseName + "." + rawData), new File(baseName + "." + rawDataType)};
      File match = null;
      for(File file : possibilities) {
        if(file.exists()) {
          match = file;
          break;
        }
      }
      if(match == null) {
        throw new IllegalStateException("Failed to find peak list file " + baseName);
      }
      return match;
    }

  }

}
