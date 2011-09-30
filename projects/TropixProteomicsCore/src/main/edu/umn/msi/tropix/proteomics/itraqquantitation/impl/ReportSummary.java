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

import java.util.LinkedHashMap;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

class ReportSummary {
  private final LinkedHashMap<String, ProteinSummary> proteinSummaries;
  private final int numProteins;

  public int getNumProteins() {
    return numProteins;
  }

  public Iterable<String> getProteins() {
    return proteinSummaries.keySet();
  }

  public ProteinSummary getProteinSummary(final String protein) {
    return proteinSummaries.get(protein);
  }

  public ReportSummary(final Iterable<ITraqMatch> iTraqMatchs, final Iterable<ITraqLabel> labels) {
    final Multimap<String, ITraqMatch> proteinMap = LinkedHashMultimap.create();

    for(final ITraqMatch iTraqMatch : iTraqMatchs) {
      final String protein = iTraqMatch.getProteinAccession();
      proteinMap.put(protein, iTraqMatch);
    }

    int numProteins = 0;
    proteinSummaries = Maps.newLinkedHashMap();
    for(final String protein : proteinMap.keySet()) {
      final Iterable<ITraqMatch> proteinDataEntries = proteinMap.get(protein);
      proteinSummaries.put(protein, new ProteinSummary(proteinDataEntries, labels));
      numProteins++;
    }

    this.numProteins = numProteins;
  }

}
