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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringWriter;
import java.util.List;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.proteomics.conversion.MzxmlParser;
import edu.umn.msi.tropix.proteomics.conversion.Scan;
import edu.umn.msi.tropix.proteomics.conversion.MzxmlParser.MzxmlInfo;

public class ITraqMatchBuilderImplTest {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();

  @Test(groups = "unit")
  public void builder() {
    final ITraqMatchBuilderImpl builder = new ITraqMatchBuilderImpl();
    final ITraqMatcher iTraqMatcher = EasyMock.createMock(ITraqMatcher.class);
    final ScaffoldReportParser scaffoldReportParser = EasyMock.createMock(ScaffoldReportParser.class);
    final MzxmlParser mzxmlParser = EasyMock.createMock(MzxmlParser.class);

    builder.setItraqMatcher(iTraqMatcher);
    builder.setMzxmlParser(mzxmlParser);
    builder.setScaffoldReportParser(scaffoldReportParser);

    File mzxml1 = null, mzxml2 = null, scaffold = null;
    try {
      mzxml1 = FILE_UTILS.createTempFile("tpxtst", "");
      mzxml2 = FILE_UTILS.createTempFile("tpxtst", "");
      scaffold = FILE_UTILS.createTempFile("tpxtst", "");
      FILE_UTILS.writeStringToFile(mzxml1, "Moo Cow");
      FILE_UTILS.writeStringToFile(mzxml2, "Moo Cow2");
      FILE_UTILS.writeStringToFile(scaffold, "Scaffold Report");

      final StringWriter writer = new StringWriter();
      final List<ScaffoldEntry> scaffoldEntries = Lists.newArrayList();
      EasyMock.expect(scaffoldReportParser.parse(EasyMockUtils.copy(writer))).andReturn(scaffoldEntries);

      final Scan scan1 = new Scan(2, 100, new double[] {1.0, 1.0});
      scan1.setPrecursorCharge((short) 2);
      scan1.setParentFileName("moo.RAW");
      final Scan scan2 = new Scan(2, 1004, new double[] {1.0, 1.0});
      scan2.setPrecursorCharge((short) 2);
      scan2.setParentFileName("cow.RAW");

      final ByteArrayOutputStream mzxmlStream1 = new ByteArrayOutputStream(), mzxmlStream2 = new ByteArrayOutputStream();
      EasyMock.expect(mzxmlParser.parse(EasyMockUtils.copy(mzxmlStream1))).andReturn(new MzxmlInfo(Lists.<Scan>newArrayList(scan1).iterator()));
      EasyMock.expect(mzxmlParser.parse(EasyMockUtils.copy(mzxmlStream2))).andReturn(new MzxmlInfo(Lists.<Scan>newArrayList(scan2).iterator()));

      final Capture<Function<ScanIndex, ITraqScanSummary>> scanSummariesCapture = EasyMockUtils.newCapture();

      final List<ITraqMatch> matches = Lists.newArrayList();
      EasyMock.expect(iTraqMatcher.match(EasyMock.same(scaffoldEntries), EasyMock.capture(scanSummariesCapture))).andReturn(matches);

      EasyMock.replay(iTraqMatcher, mzxmlParser, scaffoldReportParser);
      assert matches == builder.buildDataEntries(Lists.newArrayList(mzxml1, mzxml2), scaffold, new ITraqMatchBuilder.ITraqMatchBuilderOptions(ITraqLabels.get4PlexLabels()));
      EasyMockUtils.verifyAndReset(iTraqMatcher, mzxmlParser, scaffoldReportParser);

      final Function<ScanIndex, ITraqScanSummary> scanSummaries = scanSummariesCapture.getValue();
      assert null != scanSummaries.apply(new ScanIndex("moo", 100, (short) 2));
      assert null != scanSummaries.apply(new ScanIndex("cow", 1004, (short) 2));
      RuntimeException e = null;
      try {
        scanSummaries.apply(new ScanIndex("cow", 100, (short) 3));
      } catch(RuntimeException ie) {
        e = ie;
      }
      assert e != null;
      assert writer.toString().equals("Scaffold Report");

    } finally {
      FILE_UTILS.deleteQuietly(mzxml1);
      FILE_UTILS.deleteQuietly(mzxml2);
      FILE_UTILS.deleteQuietly(scaffold);
    }

  }

}
