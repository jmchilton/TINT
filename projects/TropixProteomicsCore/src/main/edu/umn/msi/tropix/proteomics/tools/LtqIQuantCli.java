package edu.umn.msi.tropix.proteomics.tools;

import java.io.File;
import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.proteomics.itraqquantitation.QuantitationOptions;
import edu.umn.msi.tropix.proteomics.itraqquantitation.QuantitationOptions.GroupType;
import edu.umn.msi.tropix.proteomics.itraqquantitation.QuantitationOptions.QuantitationOptionsBuilder;
import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.InputReport;
import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.ReportExtractor.ReportType;
import edu.umn.msi.tropix.proteomics.itraqquantitation.options.QuantificationType;
import edu.umn.msi.tropix.proteomics.report.PepXmlUtils;

public class LtqIQuantCli {

  public static class LtqIQuantOptions {
    @Parameter(names = "--report", required = false)
    private String report;

    @Parameter(names = "--normalize", required = false)
    private boolean normalize = false;

    @Parameter(names = "--report_type", required = false)
    private String reportType = "SCAFFOLD";

    @Parameter(names = "--group_type", required = false)
    private String groupType = "PROTEIN";

    @Parameter(names = "--type", required = false)
    final String type = "FOUR_PLEX";

    @Parameter(description = "<input>")
    private List<String> files = Lists.newArrayList();

    @Parameter(names = "--help", help = true)
    private boolean help;

    @Parameter(names = "--output", required = false)
    private String output = "itraq_quant.xls";

    @Parameter(names = "--threads", required = false)
    private int threads = 1;

  }

  public static void main(final String[] args) throws Exception {
    final LtqIQuantOptions cliOptions = new LtqIQuantOptions();
    final JCommander commander = new JCommander(cliOptions, args);

    if(cliOptions.help) {
      commander.usage();
      System.exit(0);
    }

    final ReportType reportType = ReportType.valueOf(cliOptions.reportType);
    final GroupType groupType = GroupType.valueOf(cliOptions.groupType);
    final List<File> mzxmlFiles = Lists.newArrayListWithCapacity(cliOptions.files.size());
    for(final String filePath : cliOptions.files) {
      mzxmlFiles.add(new File(filePath));
    }

    if(mzxmlFiles.size() == 0 && reportType == ReportType.PEPXML) {
      mzxmlFiles.addAll(PepXmlUtils.loadInputFiles(new File(cliOptions.report)));
    }

    System.out.println("Running with report type " + reportType + " and loading " + mzxmlFiles.size() + " peak lists.");
    final File reportFile = new File(cliOptions.report);
    final File outFile = new File(cliOptions.output);
    System.out.println("Setting group type to " + groupType);
    final QuantificationType quantificationType = QuantificationType.fromString(cliOptions.type);
    final QuantitationOptionsBuilder optionsBuilder = QuantitationOptions
        .forInput(mzxmlFiles, new InputReport(reportFile, reportType))
        .withGroupType(groupType)
        .withThreds(cliOptions.threads)
        .withOutput(outFile)
        .withQuantificationType(quantificationType);
    if(!cliOptions.normalize) {
      optionsBuilder.excludeNormalized();
    }
    final QuantitationOptions options = optionsBuilder.get();
    final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
        "edu/umn/msi/tropix/proteomics/itraqquantitation/applicationContext.xml");
    @SuppressWarnings("unchecked")
    final Closure<QuantitationOptions> closure = (Closure<QuantitationOptions>) context.getBean("quantitationClosure");
    closure.apply(options);
  }

}
