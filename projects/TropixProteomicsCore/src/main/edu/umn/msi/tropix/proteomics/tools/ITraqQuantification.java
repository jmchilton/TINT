/********************************************************************************
 * Copyright (c) 2009 Regents of the University of Minnesota
 *
 * This Software was written at the Minnesota Supercomputing Institute
 * http://msi.umn.edu
 *
 * All rights reserved. The following statement of license applies
 * only to this file, and and not to the other files distributed with it
 * or derived therefrom.  This file is made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 *******************************************************************************/

package edu.umn.msi.tropix.proteomics.tools;

import static java.lang.System.out;

import java.io.File;
import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.proteomics.itraqquantitation.QuantitationOptions;
import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.InputReport;
import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.ReportExtractor.ReportType;

public class ITraqQuantification {

  public static void main(final String[] args) throws Exception {
    if(args.length < 2) {
      usage();
      System.exit(-1);
    }

    final List<File> mzxmlFiles = Lists.newArrayListWithCapacity(args.length - 1);
    for(int i = 0; i < args.length - 2; i++) {
      mzxmlFiles.add(new File(args[i]));
    }
    final File scaffoldFile = new File(args[args.length - 2]);
    final File outFile = new File(args[args.length - 1]);
    // TODO: Take in type some how
    final QuantitationOptions options = QuantitationOptions.forInput(mzxmlFiles, new InputReport(scaffoldFile, ReportType.SCAFFOLD))
        .withOutput(outFile).is4Plex().get();

    final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
        "edu/umn/msi/tropix/proteomics/itraqquantitation/applicationContext.xml");
    @SuppressWarnings("unchecked")
    final Closure<QuantitationOptions> closure = (Closure<QuantitationOptions>) context.getBean("quantitationClosure");
    closure.apply(options);
  }

  private static void usage() {
    out.println("Usage: mzxmlfile(s) xlsfile outfile");
  }
}
