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
import java.util.Scanner;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.proteomics.itraqquantitation.QuantitationOptions.GroupType;
import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.ReportEntry.SequenceWithModifications;

@Immutable
class ITraqMatch {
  private final ProteinInformation proteinInformation;
  private final String peptideSequence;
  private final double peptideProbability;
  private final SequenceWithModifications modifiedPeptideSequence;
  private final ITraqScanSummary iTraqScanSummary;
  private final GroupType groupType;

  public ITraqMatch(final ITraqScanSummary iTraqScanSummary,
      final String proteinAccession, final double proteinProbability,
      final String peptideSequence, final double peptideProbability, final SequenceWithModifications modifiedPeptideSequence,
      final GroupType groupType) {
    this.iTraqScanSummary = iTraqScanSummary;
    this.proteinInformation = new ProteinInformation(proteinAccession, proteinProbability);
    this.peptideSequence = peptideSequence;
    this.peptideProbability = peptideProbability;
    this.modifiedPeptideSequence = modifiedPeptideSequence;
    this.groupType = groupType;
  }

  @Deprecated
  public String getProteinAccession() {
    return proteinInformation.getProteinAccession();
  }

  @Deprecated
  public double getProteinProbability() {
    return proteinInformation.getProteinScore();
  }

  public ProteinInformation getProteinInformation() {
    return proteinInformation;
  }

  public String getPeptideSequence() {
    return peptideSequence;
  }

  public double getPeptideProbability() {
    return peptideProbability;
  }

  public ITraqScanSummary getScan() {
    return iTraqScanSummary;
  }

  protected SequenceWithModifications getModifiedPeptideSequence() {
    return modifiedPeptideSequence;
  }

  public static ITraqMatch fromLine(final String line) {
    final Scanner scanner = new Scanner(line).useDelimiter("\t");
    final String proteinAccession = scanner.next();
    final double proteinProbability = scanner.nextDouble();
    final String peptideSequence = scanner.next();
    final double peptideProbability = scanner.nextDouble();

    final double i114 = scanner.nextDouble();
    final double i115 = scanner.nextDouble();
    final double i116 = scanner.nextDouble();
    final double i117 = scanner.nextDouble();

    final int number = scanner.nextInt();
    final int alt = scanner.nextInt();
    final short charge = scanner.nextShort();

    final List<ITraqLabel> labels = ITraqLabels.get4PlexLabels();
    final ITraqScanSummary iTraqScanSummary = ITraqScanSummary.fromIntensities(number, alt, charge, labels,
        Lists.newArrayList(i114, i115, i116, i117));
    final ITraqMatch iTraqMatch = new ITraqMatch(iTraqScanSummary, proteinAccession, proteinProbability, peptideSequence, peptideProbability, null,
        GroupType.PROTEIN);
    return iTraqMatch;
  }

  public String toLine() {
    final StringBuilder line = new StringBuilder();
    line.append(getProteinAccession());
    line.append("\t");
    line.append("" + getProteinProbability());
    line.append("\t");
    line.append("" + getPeptideSequence());
    line.append("\t");
    line.append("" + getPeptideProbability());
    line.append("\t");
    for(ITraqLabel label : iTraqScanSummary.getLabels()) {
      line.append("" + iTraqScanSummary.getIntensity(label));
      line.append("\t");
    }
    line.append("" + iTraqScanSummary.getNumber());
    line.append("\t");
    line.append("" + iTraqScanSummary.getAlt());
    line.append("\t");
    line.append("" + iTraqScanSummary.getCharge());
    line.append("\t\n");
    return line.toString();
  }

}
