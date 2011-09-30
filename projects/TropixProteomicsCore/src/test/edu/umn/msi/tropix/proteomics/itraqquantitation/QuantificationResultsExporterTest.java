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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

import org.springframework.util.StringUtils;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.xml.XMLUtility;
import edu.umn.msi.tropix.proteomics.itraqquantitation.results.Protein;
import edu.umn.msi.tropix.proteomics.itraqquantitation.results.QuantificationResults;
import edu.umn.msi.tropix.proteomics.itraqquantitation.results.Ratio;
import edu.umn.msi.tropix.proteomics.itraqquantitation.results.RatioLabel;

public class QuantificationResultsExporterTest {
  private static final String NEWLINE = System.getProperty("line.separator");
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();

  @Test(groups = "unit")
  public void testEmpty() throws IOException {
    final File tempResultFile = FILE_UTILS.createTempFile();
    try {
      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      final QuantificationResults results = new QuantificationResults();
      final RatioLabel label1 = new RatioLabel();
      label1.setDenominatorLabel("i113");
      label1.setNumeratorLabel("i114");
      results.getRatioLabel().add(label1);
      new XMLUtility<QuantificationResults>(QuantificationResults.class).serialize(results, tempResultFile);
      QuantificationResultsExporter.writeAsSpreadsheet(tempResultFile, outputStream);
      final String output = new String(outputStream.toByteArray());
      assert output.startsWith(QuantificationResultsExporter.NO_PROTEIN_MESSAGE);
    } finally {
      FILE_UTILS.deleteQuietly(tempResultFile);
    }
  }

  @Test(groups = "unit")
  public void testOneRow() {
    final QuantificationResults results = new QuantificationResults();

    final String denomLabel = "i113";
    final String numLabel = "i114";

    final Protein protein1 = new Protein();
    protein1.setName("prot1");
    protein1.setNumPeptides(3);
    protein1.setNumSequences(4);
    final Ratio ratio1 = new Ratio();
    ratio1.setDenominatorLabel(denomLabel);
    ratio1.setNumeratorLabel(numLabel);
    String methodLabel = "meth1";
    ratio1.setMethod(methodLabel);
    ratio1.setPValue(3.4);
    ratio1.setRatio(1.2);

    protein1.getRatio().add(ratio1);

    results.getProtein().add(protein1);

    final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    QuantificationResultsExporter.writeAsSpreadsheet(results, byteStream);
    final String resultsAsString = new String(byteStream.toByteArray());
    final Scanner scanner = new Scanner(resultsAsString);
    scanner.useDelimiter("[\t\n\r]");

    assert StringUtils.hasText(scanner.next()); // Protein
    assert StringUtils.hasText(scanner.next()); // Number of sequences
    assert StringUtils.hasText(scanner.next()); // Peptides
    final String ratioHeader = scanner.next().toLowerCase(Locale.getDefault());
    assert ratioHeader.contains("ratio");
    assert ratioHeader.contains(denomLabel);
    assert ratioHeader.contains(numLabel);
    assert ratioHeader.contains(methodLabel);

    final String pvalueHeader = scanner.next().toLowerCase(Locale.getDefault());
    assert pvalueHeader.contains("pvalue");
    assert pvalueHeader.contains(denomLabel);
    assert pvalueHeader.contains(numLabel);
    assert pvalueHeader.contains(methodLabel);

    assert scanner.next().equals("prot1");
    assert scanner.next().equals("4");
    assert scanner.next().equals("3");

    assert Double.parseDouble(scanner.next()) == 1.2;
    assert Double.parseDouble(scanner.next()) == 3.4;

    assert !scanner.hasNext();

  }
}
