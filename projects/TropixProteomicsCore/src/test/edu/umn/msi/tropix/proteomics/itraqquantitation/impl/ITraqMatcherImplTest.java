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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.collect.Collections;
import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.ITraqMatchBuilder.GroupType;
import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.ITraqMatchBuilder.ITraqMatchBuilderOptions;

public class ITraqMatcherImplTest {

  @Test(groups = "unit")
  public void match() {
    final ITraqMatcherImpl iTraqMatcherImpl = new ITraqMatcherImpl();

    final ScaffoldEntry entry1 = new ScaffoldEntry("runId", 100, 97, (short) 3, "prot1", 0.5, "pep1", 0.5);
    final ScaffoldEntry entry2 = new ScaffoldEntry("runId", 102, 102, (short) 1, "prot1", 0.5, "pep2", 0.75);
    final ScaffoldEntry entry3 = new ScaffoldEntry("runId", 105, 105, (short) 10, "prot3", 0.25, "pep2", 0.75);
    final ScaffoldEntry entry4 = new ScaffoldEntry("secondRun", 102, 102, (short) 1, "prot1", 0.5, "pep2", 0.75);
    final ScaffoldEntry entry5 = new ScaffoldEntry("secondRun", 1001, 1001, (short) 2, "prot4", 0.5, "pep4", 0.5);

    final List<ReportEntry> scaffoldEntries = Lists.<ReportEntry>newArrayList(entry1, entry2, entry3, entry4, entry5);

    final Map<ScanIndex, ITraqScanSummary> scanSummaries = Maps.newHashMap();
    final List<ITraqLabel> labels = ITraqLabels.get4PlexL abels();

    final ITraqScanSummary scan1 = ITraqScanSummary.fromIntensities(100, 97, (short) 3, labels, Lists.newArrayList(2d, 4d, 6d, 8d));
    scanSummaries.put(new ScanIndex("runId", 1, 100, (short) 3), scan1);

    final ITraqScanSummary scanNotMatched = ITraqScanSummary.fromIntensities(101, 101, (short) 3, labels, Lists.newArrayList(2d, 4d, 6d, 8d));
    scanSummaries.put(new ScanIndex("runId", 1, 101, (short) 3), scanNotMatched);

    final ITraqScanSummary scan2 = ITraqScanSummary.fromIntensities(102, 102, (short) 1, labels, Lists.newArrayList(2d, 2d, 2d, 2d));
    scanSummaries.put(new ScanIndex("runId", 1, 102, (short) 1), scan2);

    final ITraqScanSummary scan3 = ITraqScanSummary.fromIntensities(105, 105, (short) 10, labels, Lists.newArrayList(2d, 2d, 2d, 2d));
    scanSummaries.put(new ScanIndex("runId", 1, 105, (short) 10), scan3);

    final ITraqScanSummary scan4 = ITraqScanSummary.fromIntensities(102, 102, (short) 1, labels, Lists.newArrayList(2d, 2d, 2d, 2d));
    scanSummaries.put(new ScanIndex("secondRun", 1, 102, (short) 1), scan4);

    final ITraqScanSummary scan5 = ITraqScanSummary.fromIntensities(1001, 1001, (short) 2, labels, Lists.newArrayList(0.04d, 2d, 2d, 2d)); // Not
                                                                                                                                           // intense
                                                                                                                                           // enough
    scanSummaries.put(new ScanIndex("secondRun", 1, 1001, (short) 2), scan5);

    final List<ITraqMatch> matches = iTraqMatcherImpl.match(scaffoldEntries, Functions.forMap(scanSummaries), new ITraqMatchBuilderOptions(
        ITraqLabels.get4PlexLabels(), GroupType.PROTEIN));
    assert matches.size() == 4;

    final Collection<ITraqScanSummary> matchedScans = Collections.transform(matches, new Function<ITraqMatch, ITraqScanSummary>() {
      public ITraqScanSummary apply(final ITraqMatch match) {
        return match.getScan();
      }
    });
    assert matchedScans.contains(scan1);
    assert !matchedScans.contains(scanNotMatched);
    assert matchedScans.contains(scan2);
    assert matchedScans.contains(scan3);
    assert matchedScans.contains(scan4);
    assert !matchedScans.contains(scan5);

  }
}
