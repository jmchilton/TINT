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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;

import net.sourceforge.sashimi.mzxml.v3_0.MsRun;
import net.sourceforge.sashimi.mzxml.v3_0.MzXML;
import net.sourceforge.sashimi.mzxml.v3_0.Scan;
import edu.umn.msi.tropix.common.data.Repositories;
import edu.umn.msi.tropix.common.data.Repository;
import edu.umn.msi.tropix.proteomics.xml.MzXMLUtility;

public class ProteomicsTests {
  private static Repository repository = Repositories.getInstance();
  private static final MzXMLUtility UTILITY = new MzXMLUtility();
  public static String SMALL_MZXML_RESOURCE_ID = "parentPerScan.mzxml";

  public static synchronized InputStream getMzxmlStream(final int sizeSeed) {
    final MzXML mzxml = UTILITY.deserialize(getResourceAsStream("parentPerScan.mzxml"));
    final MsRun run = mzxml.getMsRun();
    final List<Scan> scans = run.getScan();
    final Scan firstScan = scans.get(0);
    for(int i = 0; i < sizeSeed; i++) {
      final Scan newScan = new Scan();
      newScan.setPeaksCount(firstScan.getPeaksCount());
      newScan.setMsLevel(firstScan.getPeaksCount());
      newScan.setNum(BigInteger.valueOf(1000L + sizeSeed));
      newScan.getPrecursorMz().addAll(firstScan.getPrecursorMz());
      newScan.getScan().addAll(firstScan.getScan());
      newScan.getScanOrigin().addAll(firstScan.getScanOrigin());
      scans.add(newScan);
    }
    final BigInteger scanCount = run.getScanCount();
    if(scanCount != null) {
      run.setScanCount(scanCount.add(BigInteger.valueOf(sizeSeed)));
    }
    return new ByteArrayInputStream(UTILITY.serialize(mzxml).getBytes());
  }

  public static InputStream getResourceAsStream(final String resourceName) {
    final Class<ProteomicsTests> clazz = ProteomicsTests.class;
    InputStream stream;
    stream = clazz.getResourceAsStream(resourceName);
    if(stream == null) {
      stream = repository.getResource(clazz, resourceName);
    }
    return stream;
  }

}
