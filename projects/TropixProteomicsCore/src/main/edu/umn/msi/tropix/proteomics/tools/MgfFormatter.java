package edu.umn.msi.tropix.proteomics.tools;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.proteomics.conversion.MzXMLToMGFConverter.MgfConversionOptions;
import edu.umn.msi.tropix.proteomics.conversion.MzXMLToMGFConverter.MgfConversionOptions.MgfStyle;
import edu.umn.msi.tropix.proteomics.conversion.ScanTransformers;
import edu.umn.msi.tropix.proteomics.conversion.impl.MzXMLToMGFConverterStreamingImpl;
import edu.umn.msi.tropix.proteomics.conversion.impl.PeakListParser.PeakListParserOptions;

public class MgfFormatter {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();

  public static class MgfFormatterOptions {
    @Parameter(names = "--mgf_format", required = false)
    private String mgfFormat = "DEFAULT";

    @Parameter(names = "--itraq_filter", required = false)
    private boolean itraqFilter;

    @Parameter(names = "--output", description = "Output")
    private String output;

    @Parameter(names = "--no_split_multiple_charge_states", required = false)
    private boolean noSplitMultipleChargeStates = false;

    @Parameter(description = "<input>")
    private List<String> inputs = Lists.newArrayList();

    @Parameter(names = "--help", help = true)
    private boolean help;

  }

  public static void main(final String[] args) {
    final MgfFormatterOptions cliOptions = new MgfFormatterOptions();
    final JCommander commander = new JCommander(cliOptions, args);

    if(cliOptions.help) {
      commander.usage();
      System.exit(0);
    }

    final MgfStyle mgfStyle = MgfStyle.valueOf(cliOptions.mgfFormat);
    final MgfConversionOptions conversionOptions = new MgfConversionOptions();
    if(cliOptions.itraqFilter) {
      System.out.println("Applying iTRAQ filter.");
      conversionOptions.addScanTransformer(ScanTransformers.getITraqFilter());
    }
    conversionOptions.setMgfStyle(mgfStyle);

    final PeakListParserOptions parserOptions = new PeakListParserOptions();
    parserOptions.setSplitChargeStates(!cliOptions.noSplitMultipleChargeStates);

    MzXMLToMGFConverterStreamingImpl streamingConverter = new MzXMLToMGFConverterStreamingImpl();
    OutputStream mgfOutput = null;
    try {
      mgfOutput = FILE_UTILS.getFileOutputStream(cliOptions.output);
      List<File> files = Lists.newArrayList();
      for(String inputPath : cliOptions.inputs) {
        files.add(new File(inputPath));
      }
      streamingConverter.peakListToMgf(files, mgfOutput, conversionOptions, parserOptions);
    } finally {
      IO_UTILS.closeQuietly(mgfOutput);
    }
  }

}
