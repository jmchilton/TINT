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

package edu.umn.msi.tropix.proteomics.itraqquantitation;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.annotation.WillNotClose;

import edu.umn.msi.tropix.common.xml.XMLUtility;
import edu.umn.msi.tropix.proteomics.itraqquantitation.results.Protein;
import edu.umn.msi.tropix.proteomics.itraqquantitation.results.QuantificationResults;
import edu.umn.msi.tropix.proteomics.itraqquantitation.results.Ratio;

public class QuantificationResultsExporter {
  public static final String NO_PROTEIN_MESSAGE = "No proteins found.";
  private static final XMLUtility<QuantificationResults> RESULTS_XML_UTILITY = new XMLUtility<QuantificationResults>(QuantificationResults.class);

  public static void writeAsSpreadsheet(final File quantificationResultsFile, @WillNotClose final OutputStream outputStream) {
    final QuantificationResults results = RESULTS_XML_UTILITY.deserialize(quantificationResultsFile);
    writeAsSpreadsheet(results, outputStream);
  }

  public static void writeAsSpreadsheet(final QuantificationResults results, @WillNotClose final OutputStream outputStream) {
    final PrintStream printStream = new PrintStream(outputStream);
    if(results.getProtein() == null || results.getProtein().isEmpty()) {
      printStream.append(NO_PROTEIN_MESSAGE);
    } else {
      final Protein exampleProtein = results.getProtein().get(0);
      printStream.append("Protein\tNumber of Sequences\tNumber of Unique Peptides");
      for(final Ratio ratio : exampleProtein.getRatio()) {
        final String ratioSuffix = ratio.getNumeratorLabel() + ":" + ratio.getDenominatorLabel();
        final String ratioStr = ratio.getMethod() + "_ratio_" + ratioSuffix;
        final String pValueStr = ratio.getMethod() + "_pvalue_" + ratioSuffix;
        printStream.append("\t" + ratioStr + "\t" + pValueStr);
      }
      for(Protein protein : results.getProtein()) {
        printStream.append("\n" + protein.getName() +"\t" + protein.getNumSequences() + "\t" + protein.getNumPeptides());
        for(Ratio ratio : protein.getRatio()) {
          printStream.append("\t" + ratio.getRatio() + "\t");
          final Double pValue = ratio.getPValue();
          if(pValue != null) {
            printStream.append(Double.toString(pValue));
          } else {
            printStream.append("*");
          }

        }
      }
    }
    printStream.flush();
  }

}
