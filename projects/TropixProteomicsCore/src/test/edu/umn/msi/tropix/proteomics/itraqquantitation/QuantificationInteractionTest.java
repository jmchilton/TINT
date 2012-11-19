package edu.umn.msi.tropix.proteomics.itraqquantitation;

import java.io.File;
import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.InputReport;
import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.ReportExtractor.ReportType;

public class QuantificationInteractionTest {

  @Test(groups = "interaction")
  public void runTest() {
    final List<File> inputFiles = Lists.newArrayList();
    for(int i = 0; i < 20; i++) {
      final String fileName = String.format("/project/msistaff/appdev/proteomics_data/quant-test/input%d.mzxml", i);
      inputFiles.add(new File(fileName));
    }
    final File scaffInputFile = new File("/project/msistaff/appdev/proteomics_data/quant-test/dataReport.xls");
    final QuantitationOptions options = QuantitationOptions.forInput(inputFiles, new InputReport(scaffInputFile, ReportType.SCAFFOLD))
        .withOutput(new File("/tmp/quant-test"))
        .get();
    final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
        "edu/umn/msi/tropix/proteomics/itraqquantitation/applicationContext.xml");
    final Closure<QuantitationOptions> closure = (Closure<QuantitationOptions>) context.getBean("quantitationClosure");
    closure.apply(options);
  }
}
