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
import java.util.List;

import org.testng.collections.Lists;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import edu.umn.msi.tropix.proteomics.itraqquantitation.QuantitationOptions.GroupType;
import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.ReportEntry.CanSplitModifications;
import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.ReportEntry.SequenceWithModifications;

class ReportSummary {
  private final LinkedHashMap<String, GroupSummary> groupSummariesByLabel;
  private final LinkedHashMap<String, ProteinInformation> groupLabelProtein;
  private final int numGroups;
  private final GroupType groupType;

  public int getNumGroups() {
    return numGroups;
  }

  public Iterable<String> getGroups() {
    return groupSummariesByLabel.keySet();
  }

  public GroupType getGroupType() {
    return groupType;
  }

  public GroupSummary getGroupSummary(final String groupLabel) {
    return groupSummariesByLabel.get(groupLabel);
  }

  public ProteinInformation getProteinInformationForPeptideGroup(final String groupLabel) {
    return groupLabelProtein.get(groupLabel);
  }

  public ReportSummary(final Iterable<ITraqMatch> iTraqMatchs, final Iterable<ITraqLabel> labels, GroupType groupType) {
    final Multimap<String, ITraqMatch> groupMap = LinkedHashMultimap.create();
    groupLabelProtein = Maps.newLinkedHashMap();

    for(final ITraqMatch iTraqMatch : iTraqMatchs) {
      final ProteinInformation proteinInformation = iTraqMatch.getProteinInformation();
      List<String> groupLabels = Lists.newArrayList();
      if(groupType == GroupType.PROTEIN) {
        groupLabels.add(proteinInformation.getProteinAccession());
      } else if(groupType == GroupType.PEPTIDE) {
        groupLabels.add(iTraqMatch.getPeptideSequence());
      } else if(groupType == GroupType.PEPTIDE_WITH_MODIFICATIONS) {
        groupLabels.add(iTraqMatch.getModifiedPeptideSequence().toString());
      } else if(groupType == GroupType.PEPTIDE_WITH_UNIQUE_MODIFICATION) {
        SequenceWithModifications seqWithMods = iTraqMatch.getModifiedPeptideSequence();
        if(!(seqWithMods instanceof CanSplitModifications)) {
          throw new IllegalArgumentException("Attempt to split modifications when operation unavailable for data source.");
        }
        final CanSplitModifications splittableSeqWithMods = (CanSplitModifications) seqWithMods;
        for(SequenceWithModifications seqWithOneMod : splittableSeqWithMods.splitupModifications()) {
          groupLabels.add(seqWithOneMod.toString());
        }
      } else {
        throw new IllegalArgumentException("Unknown group type " + groupType);
      }
      for(final String groupLabel : groupLabels) {
        groupMap.put(groupLabel, iTraqMatch);
        if(!groupLabelProtein.containsKey(groupLabel)) {
          groupLabelProtein.put(groupLabel, proteinInformation);
        }
      }
    }

    int numGroups = 0;
    groupSummariesByLabel = Maps.newLinkedHashMap();
    for(final String groupLabel : groupMap.keySet()) {
      final Iterable<ITraqMatch> groupDataEntries = groupMap.get(groupLabel);
      groupSummariesByLabel.put(groupLabel, new GroupSummary(groupDataEntries, labels));
      numGroups++;
    }

    this.numGroups = numGroups;
    this.groupType = groupType;
  }

}
