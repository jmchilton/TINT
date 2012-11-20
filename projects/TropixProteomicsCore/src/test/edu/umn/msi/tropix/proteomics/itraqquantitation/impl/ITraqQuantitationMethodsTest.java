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

package edu.umn.msi.tropix.proteomics.itraqquantitation.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;
import java.util.Scanner;

import org.springframework.util.StringUtils;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.WeightedRatiosCalculator.Ratios;
import edu.umn.msi.tropix.proteomics.test.ProteomicsTests;

public class ITraqQuantitationMethodsTest {
  
  @Test(groups = "unit")
  public void testAverages() throws IOException {
    final InputStream entriesStream = ProteomicsTests.getResourceAsStream("quantitation_entries");
    LineNumberReader lineReader = new LineNumberReader(new InputStreamReader(entriesStream));
    final List<ITraqMatch> iTraqMatchs = Lists.newLinkedList();
    while(true) {
      final String line = lineReader.readLine();
      if(!StringUtils.hasText(line)) {
        break;
      }
      iTraqMatchs.add(ITraqMatch.fromLine(line));
    }
    final List<ITraqLabel> labels = ITraqLabels.get4PlexLabels();
    final ReportSummary summary = new ReportSummary(iTraqMatchs, labels);
    //final List<Column> weightedColumns = Lists.newArrayList(QuantifierImpl.getRatiosAndPValues("", "", labels.get(1), labels.get(2), summary, null));
    final Ratios ratios = new WeightedRatiosCalculatorImpl().computeRatios(labels.get(1), labels.get(2), summary, null);
    final List<List<Double>> rValues = Lists.newLinkedList();
    final InputStream averagesStream = ProteomicsTests.getResourceAsStream("quantitation_averages");
    lineReader = new LineNumberReader(new InputStreamReader(averagesStream));
    while(true) {
      final String line = lineReader.readLine();
      if(!StringUtils.hasText(line)) {
        break;
      }
      final Scanner scanner = new Scanner(line).useDelimiter("\\s+");
      final double d1 = scanner.nextDouble(), d2 = scanner.nextDouble(), d3 = scanner.nextDouble(), d4 = scanner.nextDouble();
      rValues.add(Lists.newArrayList(d1, d2, d3, d4));
    }
    final int numProteins = summary.getNumGroups();
    assert numProteins == rValues.size();
    
    // final Column normalizedRatio = regularColumns.get(1);
    // final Column pvalue = regularColumns.get(2);
   // final Column wnormalizedRatio = weightedColumns.get(1);
    //final Column wpvalue = weightedColumns.get(1);

    for(int i = 0; i < numProteins; i++) {
      // almostEqual(Double.parseDouble(normalizedRatio.getValue(i)), rValues.get(i).get(0));
      // almostEqual(Double.parseDouble(pvalue.getValue(i)), rValues.get(i).get(1));
      //almostEqual(Double.parseDouble(wnormalizedRatio.getValue(i)), rValues.get(i).get(2));
      //final String pValueStr = wpvalue.getValue(i);
      double pValue = ratios.getPValues()[i];
      MathAsserts.assertWithin(pValue, rValues.get(i).get(3), .000001);
    }

  }

}
