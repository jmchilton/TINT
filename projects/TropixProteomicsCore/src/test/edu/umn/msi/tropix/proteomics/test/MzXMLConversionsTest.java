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

package edu.umn.msi.tropix.proteomics.test;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;

import net.sourceforge.sashimi.mzxml.v3_0.MsRun;
import net.sourceforge.sashimi.mzxml.v3_0.MzXML;
import net.sourceforge.sashimi.mzxml.v3_0.Scan;
import net.sourceforge.sashimi.mzxml.v3_0.MsRun.ParentFile;
import net.sourceforge.sashimi.mzxml.v3_0.Scan.ScanOrigin;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.proteomics.InMemoryDTAListImpl;
import edu.umn.msi.tropix.proteomics.conversion.DTAToMzXMLOptions;
import edu.umn.msi.tropix.proteomics.conversion.impl.DTAToMzXMLConverterImpl;

public class MzXMLConversionsTest {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();

  @Test(groups = "unit")
  public void testDTAToMzXML() throws Exception {
    testDTAToMzXML(true, "trunk", false);
    testDTAToMzXML(false, "trunk", false);
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void testDTAToMzXMLInvalidDTAName() throws Exception {
    testDTAToMzXML(true, "trunk", true);
  }

  public void testDTAToMzXML(final boolean useDoubles, final String suffix, final boolean useInvalidName) throws Exception {
    final InMemoryDTAListImpl dtaList = new InMemoryDTAListImpl();
    final HashSet<String> dtaFiles = new HashSet<String>();
    for(int i = 2; i < 6; i++) {
      final InputStream stream = ProteomicsTests.getResourceAsStream("S" + i + suffix + ".dta");
      final byte[] bytes = IO_UTILS.toByteArray(stream);
      String fileName;
      if(useInvalidName) {
        fileName = "S." + i + "." + i + ".dta";
      } else {
        fileName = "S." + i + "." + i + ".1.dta";
      }
      dtaList.add(bytes, fileName);
      dtaFiles.add(fileName);
    }

    final DTAToMzXMLConverterImpl converter = new DTAToMzXMLConverterImpl();
    final DTAToMzXMLOptions options = new DTAToMzXMLOptions();
    options.setDoublePrecision(useDoubles);

    final MzXML mzxml = converter.dtaToMzXML(dtaList, useDoubles ? options : null);
    VerifyUtils.verifyMzXML(mzxml);

    final MsRun run = mzxml.getMsRun();
    assert run.getParentFile().size() == 4;
    final HashSet<String> parentFiles = new HashSet<String>();
    final HashSet<String> sha1Sums = new HashSet<String>();
    for(final ParentFile parentFile : run.getParentFile()) {
      parentFiles.add(parentFile.getFileName());
      sha1Sums.add(parentFile.getFileSha1());
    }
    assert parentFiles.equals(dtaFiles);
    assert run.getScan().size() == 4;
    final HashSet<String> encounteredSha1Sums = new HashSet<String>();
    for(final Scan scan : run.getScan()) {
      final List<ScanOrigin> originList = scan.getScanOrigin();
      assert originList != null && originList.size() == 1;
      final ScanOrigin origin = originList.get(0);
      encounteredSha1Sums.add(origin.getParentFileID());
    }
    assert encounteredSha1Sums.equals(sha1Sums);
  }

}
