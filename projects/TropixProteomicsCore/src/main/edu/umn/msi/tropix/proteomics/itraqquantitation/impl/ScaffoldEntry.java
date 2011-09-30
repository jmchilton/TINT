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

class ScaffoldEntry {
  private final String spectraId;
  private final int scanNumber;
  private final int scanAlt;
  private final short scanCharge;

  private final String proteinAccession;
  private final double proteinProbability;
  private final String peptideSequence;
  private final double peptideProbability;

  public ScaffoldEntry(final String spectraId, final int scanNumber, final int scanAlt, final short scanCharge, final String proteinAccession, final double proteinProbability, final String peptideSequence, final double peptideProbability) {
    this.spectraId = spectraId;
    this.scanNumber = scanNumber;
    this.scanAlt = scanAlt;
    this.scanCharge = scanCharge;
    this.proteinAccession = proteinAccession;
    this.proteinProbability = proteinProbability;
    this.peptideSequence = peptideSequence;
    this.peptideProbability = peptideProbability;
  }

  public String getSpectraId() {
    return spectraId;
  }

  public int getScanNumber() {
    return scanNumber;
  }

  public int getScanAlt() {
    return scanAlt;
  }

  public short getScanCharge() {
    return scanCharge;
  }

  public String getProteinAccession() {
    return proteinAccession;
  }

  public double getProteinProbability() {
    return proteinProbability;
  }

  public String getPeptideSequence() {
    return peptideSequence;
  }

  public double getPeptideProbability() {
    return peptideProbability;
  }

}
