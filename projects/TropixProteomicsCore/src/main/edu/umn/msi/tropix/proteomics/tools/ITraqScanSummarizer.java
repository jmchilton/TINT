package edu.umn.msi.tropix.proteomics.tools;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.proteomics.conversion.Scan;
import edu.umn.msi.tropix.proteomics.conversion.Scan.Peak;
import edu.umn.msi.tropix.proteomics.conversion.impl.MgfParser;
import edu.umn.msi.tropix.proteomics.conversion.impl.XmlPeakListParser;
import edu.umn.msi.tropix.proteomics.conversion.impl.XmlPeakListParserImpl;

public class ITraqScanSummarizer {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private static MgfParser mgfParser = new MgfParser();
  private static XmlPeakListParser mzxmlParser = new XmlPeakListParserImpl();

  public static class ITraqOptions {
    @Parameter(names = "--output", description = "output summary", required = false)
    private String output = null;

    @Parameter(names = "--input_type", description = "input type (mzxml, mzml, or mgf)")
    private String inputType = "mzxml";

    @Parameter(description = "<input>")
    private List<String> files = Lists.newArrayList();

    @Parameter(names = "--window_width_lower", required = false)
    private double windowWidthLower = 0.5;

    @Parameter(names = "--window_width_upper", required = false)
    private double windowWidthUpper = 0.5;

    @Parameter(names = "--help", help = true)
    private boolean help;

  }

  public static class SummarizeOptions {
    private final List<PeakOptions> peakOptions;
    private final boolean mgfInput;

    public SummarizeOptions(final Iterable<PeakOptions> peakOptions, final boolean mgfInputs) {
      this.peakOptions = Lists.newArrayList(peakOptions);
      this.mgfInput = mgfInputs;
    }

    public Iterable<PeakOptions> getPeakOptions() {
      return peakOptions;
    }

  }

  public static class PeakOptions {
    private final double lower;
    private final double upper;
    private final String label;

    public PeakOptions(final String label, final double lower, final double upper) {
      this.lower = lower;
      this.upper = upper;
      this.label = label;
    }

    public double getLower() {
      return lower;
    }

    public double getUpper() {
      return upper;
    }

    public String getLabel() {
      return label;
    }

  }

  public static void main(final String[] args) {
    final ITraqOptions itraqOptions = new ITraqOptions();
    final JCommander commander = new JCommander(itraqOptions, args);
    final List<PeakOptions> peakOptions = Lists.newArrayList();
    for(int i = 114; i <= 117; i++) {
      final PeakOptions peakOption = new PeakOptions("" + i, i - itraqOptions.windowWidthLower, i + itraqOptions.windowWidthUpper);
      peakOptions.add(peakOption);
    }
    if(itraqOptions.help) {
      commander.usage();
      System.exit(0);
    } else {
      SummarizeOptions summarizeOptions = new SummarizeOptions(peakOptions, itraqOptions.inputType.toUpperCase().equals("MGF"));
      final File mzxmlFile = new File(itraqOptions.files.get(0));
      summarize(mzxmlFile, itraqOptions.output, summarizeOptions);
    }
    /*
     * final File directory = new File("/home/msi/chilton/to_summarize");
     * final File[] mzxmlFiles = directory.listFiles(new FileFilter() {
     * public boolean accept(final File pathname) {
     * return pathname.getName().toLowerCase().endsWith("mgf");
     * }
     * });
     * for(final File mzxmlFile : mzxmlFiles) {
     * summarize(mzxmlFile);
     * }
     */

  }

  private static void summarize(final File mzxmlFile, String summaryPath, final SummarizeOptions summarizeOptions) {
    final File summaryFile;
    if(summaryPath == null) {
      summaryFile = new File(mzxmlFile.getPath() + "_summary.csv");
    } else {
      summaryFile = new File(summaryPath);
    }
    final InputStream mzxmlStream = FILE_UTILS.getFileInputStream(mzxmlFile);
    final PrintWriter summaryWriter = new PrintWriter(FILE_UTILS.getFileWriter(summaryFile));
    try {
      final Iterator<Scan> scanIterator;
      if(summarizeOptions.mgfInput) {
        // System.out.println("Parsing MGF");
        scanIterator = mgfParser.parserMgf(mzxmlStream);
      } else {
        // System.out.println("Parsing mzxml");
        scanIterator = mzxmlParser.parse(mzxmlStream);
      }
      summarize(scanIterator, summaryWriter, summarizeOptions);
    } finally {
      IO_UTILS.closeQuietly(mzxmlStream);
      IO_UTILS.closeQuietly(summaryWriter);
    }
  }

  private static void summarize(final Iterator<Scan> scans, final PrintWriter writer, final SummarizeOptions summarizeOptions) {
    while(scans.hasNext()) {
      final Scan scan = scans.next();
      if(scan.getMsLevel() != 2) {
        continue;
      }
      summarize(scan, writer, summarizeOptions);
      writer.append("\n");
    }
  }

  private static void summarize(final Scan scan, final PrintWriter writer, final SummarizeOptions summarizeOptions) {
    appendValue(scan.getNumber(), writer);
    for(final PeakOptions peakOptions : summarizeOptions.getPeakOptions()) {
      final Optional<Peak> peak = scan.mostIntensePeak(peakOptions.getLower(), peakOptions.getUpper());
      summarizePeak(peak, writer);
    }
    appendValue(scan.getPrecursorIntensity(), writer);
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

  private static void appendValue(final String value, final PrintWriter writer) {
    writer.append(value.toString());
    writer.append("\t");
  }

  private static void appendValue(final long value, final PrintWriter writer) {
    appendValue(Long.toString(value), writer);
  }

  private static void appendValue(final double value, final PrintWriter writer) {
    appendValue(String.format("%f", value), writer);
  }

}
