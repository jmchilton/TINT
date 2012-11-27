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

import java.util.List;

import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.proteomics.itraqquantitation.QuantitationOptions.GroupType;

public class ITraqMatchTest {

  @Test(groups = "unit")
  public void toFromLine() {
    final List<ITraqLabel> labels = ITraqLabels.get4PlexLabels();
    final ITraqScanSummary summary = ITraqScanSummary.fromIntensities(100, 99, (short) 1, labels, Lists.newArrayList(1.0, 2.0, 3.0, 4.0));
    final ITraqMatch match = new ITraqMatch(summary, "prot1", 50.0, "pep1", 45.0, null, GroupType.PROTEIN);

    final ITraqMatch convertedMatch = ITraqMatch.fromLine(match.toLine());
    assert convertedMatch.getProteinAccession().equals("prot1");
    assert convertedMatch.getPeptideSequence().equals("pep1");
    assert convertedMatch.getPeptideProbability() == 45.0;
    assert convertedMatch.getProteinProbability() == 50.0;

    final ITraqScanSummary convertedSummary = convertedMatch.getScan();
    assert convertedSummary.getAlt() == 99;
    assert convertedSummary.getCharge() == 1;
    assert convertedSummary.getNumber() == 100;
    assert convertedSummary.getIntensity(labels.get(0)) == 1.0;
    assert convertedSummary.getIntensity(labels.get(1)) == 2.0;
    assert convertedSummary.getIntensity(labels.get(2)) == 3.0;
    assert convertedSummary.getIntensity(labels.get(3)) == 4.0;
  }

}
