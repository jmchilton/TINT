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

import com.google.common.base.Function;
import com.google.common.collect.Lists;

class ITraqMatcherImpl implements ITraqMatcher {

  public List<ITraqMatch> match(final Iterable<ReportEntry> scaffoldEntries, final Function<ScanIndex, ITraqScanSummary> scanSummaries) {
    final List<ITraqMatch> iTraqMatchs = Lists.newLinkedList();

    // For each ScaffoldEntry find the matching Scan and if each of the iTraq intensities
    // is high enough add it to the result (dataEntries).
    for(final ReportEntry scaffoldEntry : scaffoldEntries) {
      final String runId;
      if(scaffoldEntry instanceof NamedReportEntry) {
        runId = ((NamedReportEntry) scaffoldEntry).getSpectraId();
      } else {
        throw new RuntimeException("Unknown report entry type " + scaffoldEntry);
      }
      final int scanNumber = scaffoldEntry.getScanNumber();
      final short scanCharge = scaffoldEntry.getScanCharge();
      final ITraqScanSummary scan = scanSummaries.apply(new ScanIndex(runId, scanNumber, scanCharge));
      if(scan.intensitiesGreaterThan(1.0)) {
        final String proteinAccession = scaffoldEntry.getProteinAccession();
        final double proteinProbability = scaffoldEntry.getProteinProbability();
        final String peptideSequence = scaffoldEntry.getPeptideSequence();
        final double peptideProbability = scaffoldEntry.getPeptideProbability();
        iTraqMatchs.add(new ITraqMatch(scan, proteinAccession, proteinProbability, peptideSequence, peptideProbability));
      }
    }

    return iTraqMatchs;
  }

}
