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
import java.util.List;

import javax.annotation.WillNotClose;

import edu.umn.msi.tropix.common.xml.XMLUtility;
import edu.umn.msi.tropix.proteomics.itraqquantitation.QuantitationOptions.GroupType;
import edu.umn.msi.tropix.proteomics.itraqquantitation.results.PeptideGroup;
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
    writeAsSpreadsheet(results, outputStream, GroupType.PROTEIN);
  }

  private static List<Ratio> getFirstRatioList(final QuantificationResults results) {
    final List<Protein> proteinList = results.getProtein();
    if(proteinList == null || proteinList.isEmpty()) {
      return null;
    }
    final Protein protein = proteinList.get(0);
    final List<PeptideGroup> peptideGroupList = protein.getPeptideGroup();
    final List<Ratio> ratioList;
    if(peptideGroupList.isEmpty()) {
      System.out.println("Example is protein");
      ratioList = protein.getRatio();
    } else {
      System.out.println("Example is peptide group");
      ratioList = peptideGroupList.get(0).getRatio();
    }
    return ratioList;
  }

  public static void writeAsSpreadsheet(final QuantificationResults results, @WillNotClose final OutputStream outputStream, final GroupType groupType) {
    final PrintStream printStream = new PrintStream(outputStream);
    final List<Ratio> exampleRatioList = getFirstRatioList(results);
    if(exampleRatioList == null) {
      printStream.append(NO_PROTEIN_MESSAGE);
    } else {
      if(groupType == GroupType.PROTEIN) {
        printStream.append("Protein\tNumber of Spectra\tNumber of Unique Peptides");
      } else if(groupType == GroupType.PEPTIDE) {
        printStream.append("Protein\tPeptide\tNumber of Spectra");
      } else if(groupType == GroupType.PEPTIDE_WITH_MODIFICATIONS) {
        printStream.append("Protein\tPeptide with Modifications\tNumber of Spectra");
      }
      for(final Ratio ratio : exampleRatioList) {
        final String ratioSuffix = ratio.getNumeratorLabel() + ":" + ratio.getDenominatorLabel();
        final String ratioStr = ratio.getMethod() + "_ratio_" + ratioSuffix;
        final String pValueStr = ratio.getMethod() + "_pvalue_" + ratioSuffix;
        printStream.append("\t" + ratioStr + "\t" + pValueStr);
      }
      for(Protein protein : results.getProtein()) {
        if(groupType == GroupType.PROTEIN) {
          printStream.append("\n" + protein.getName() + "\t" + protein.getNumSequences() + "\t" + protein.getNumPeptides());
          writeRatioEntries(printStream, protein.getRatio());
        } else {
          for(PeptideGroup peptideGroup : protein.getPeptideGroup()) {
            printStream.append("\n" + protein.getName() + "\t" + peptideGroup.getGroupLabel() + "\t" + peptideGroup.getNumSpectra());
            writeRatioEntries(printStream, peptideGroup.getRatio());
          }
        }
      }
    }
    printStream.flush();
  }

  private static void writeRatioEntries(final PrintStream printStream, final List<Ratio> ratioList) {
    for(Ratio ratio : ratioList) {
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
