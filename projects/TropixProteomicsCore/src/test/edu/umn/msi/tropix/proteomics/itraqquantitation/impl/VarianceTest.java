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

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;


public class VarianceTest {

  @Test(groups = "unit")
  public void compareToRResults() {
    final InputStream inputStream = getClass().getResourceAsStream("training_data.txt");
    final Scanner scanner = new Scanner(inputStream);
    
    final int numValues = 523;
    final double[] i114 = new double[numValues], i115 = new double[numValues], i116 = new double[numValues], i117 = new double[numValues]; 
    
    // Drop the header line
    scanner.nextLine();
    for(int i = 0; i < numValues; i++) {
      scanner.next(); // drop id
      scanner.next(); // drop short name
      scanner.next(); // ignore protein id
      scanner.next(); // ignore peptide seq
      scanner.next(); // ignore peptide prob
      
      i114[i] = scanner.nextDouble();
      i115[i] = scanner.nextDouble();
      i116[i] = scanner.nextDouble();
      i117[i] = scanner.nextDouble();
    }
    
    final double[][] result = new double[31][2];
    final InputStream matrixInputStream = getClass().getResourceAsStream("weight_matrix.txt");
    final Scanner matrixScanner = new Scanner(matrixInputStream);
    // Drop the header line
    matrixScanner.nextLine(); 
    for(int i = 0; i < 31; i++) {
      matrixScanner.next(); // ignore row name
      
      result[i][0] = matrixScanner.nextDouble();
      result[i][1] = matrixScanner.nextDouble();
    }
    final List<ITraqLabel> labels = ITraqLabels.get4PlexLabels();
    final Map<ITraqLabel, double[]> intensities = ImmutableMap.<ITraqLabel, double[]>builder().put(labels.get(0), i114).put(labels.get(1), i115).put(labels.get(2), i116).put(labels.get(3), i117).build(); 
    final double[][] actualResults = Variance.createVarianceMatrix(labels, intensities, new double[]{10, 5, 2, 1}, 100);
    assert actualResults.length == 31;
    for(int i = 0; i < 31; i++) {
      MathAsserts.assertWithin(result[i], actualResults[i], .0001);
    }
    
  }
}
