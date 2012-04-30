package edu.umn.msi.tropix.proteomics.test;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;

import com.google.common.base.Optional;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.proteomics.conversion.Scan;
import edu.umn.msi.tropix.proteomics.conversion.Scan.Peak;
import edu.umn.msi.tropix.proteomics.conversion.impl.MgfParser;

public class ITraqScanSummarizer {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private static MgfParser mgfParser = new MgfParser();

  public static void main(final String[] args) {
    final File directory = new File("/home/msi/chilton/to_summarize");
    final File[] mzxmlFiles = directory.listFiles(new FileFilter() {
      public boolean accept(final File pathname) {
        return pathname.getName().toLowerCase().endsWith("mgf");
      }
    });
    for(final File mzxmlFile : mzxmlFiles) {
      summarize(mzxmlFile);
    }
  }

  private static void summarize(final Iterator<Scan> scans, final PrintWriter writer) {
    while(scans.hasNext()) {
      final Scan scan = scans.next();
      summarize(scan, writer);
      writer.append("\n");
    }
  }

  private static void summarize(final Scan scan, final PrintWriter writer) {
    final Optional<Peak> peak114 = scan.mostIntensePeak(113.9, 114.3);
    final Optional<Peak> peak117 = scan.mostIntensePeak(116.9, 117.3);

    appendValue(scan.getNumber(), writer);
    summarizePeak(peak114, writer);
    summarizePeak(peak117, writer);
    appendValue(scan.getPrecursorMz(), writer);
    appendValue(scan.getRt(), writer);
  }

  private static void summarizePeak(final Optional<Peak> peak, final PrintWriter writer) {
    if(peak.isPresent()) {
      appendValue(peak.get().getIntensity(), writer);
      appendValue(peak.get().getMz(), writer);
    } else {
      appendValue("NA", writer);
      appendValue("NA", writer);
    }
  }

  private static void appendValue(final Object value, final PrintWriter writer) {
    writer.append(value.toString());
    writer.append("\t");
  }

  private static void summarize(final File mzxmlFile) {
    final File summaryFile = new File(mzxmlFile.getPath() + "_summary.csv");
    final InputStream mzxmlStream = FILE_UTILS.getFileInputStream(mzxmlFile);
    final PrintWriter summaryWriter = new PrintWriter(FILE_UTILS.getFileWriter(summaryFile));
    try {
      final Iterator<Scan> scanIterator = mgfParser.parserMgf(mzxmlStream);
      summarize(scanIterator, summaryWriter);
    } finally {
      IO_UTILS.closeQuietly(mzxmlStream);
      IO_UTILS.closeQuietly(summaryWriter);
    }
  }

}
