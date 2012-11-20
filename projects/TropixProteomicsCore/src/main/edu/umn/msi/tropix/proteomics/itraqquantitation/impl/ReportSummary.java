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
  private final LinkedHashMap<String, GroupSummary> groupSummaries;
  private final int numGroups;

  public int getNumGroups() {
    return numGroups;
  }

  public Iterable<String> getGroups() {
    return groupSummaries.keySet();
  }

  public GroupSummary getGroupSummary(final String groupLabel) {
    return groupSummaries.get(groupLabel);
  }

  public ReportSummary(final Iterable<ITraqMatch> iTraqMatchs, final Iterable<ITraqLabel> labels) {
    final Multimap<String, ITraqMatch> groupMap = LinkedHashMultimap.create();

    for(final ITraqMatch iTraqMatch : iTraqMatchs) {
      final String groupLabel = iTraqMatch.getProteinAccession();
      groupMap.put(groupLabel, iTraqMatch);
    }

    int numProteins = 0;
    groupSummaries = Maps.newLinkedHashMap();
    for(final String groupLabel : groupMap.keySet()) {
      final Iterable<ITraqMatch> groupDataEntries = groupMap.get(groupLabel);
      groupSummaries.put(groupLabel, new GroupSummary(groupDataEntries, labels));
      numProteins++;
    }

    this.numGroups = numProteins;
  }

}
