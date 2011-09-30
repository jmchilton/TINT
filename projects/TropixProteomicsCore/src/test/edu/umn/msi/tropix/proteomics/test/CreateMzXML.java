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

import java.io.File;
import java.io.InputStream;

import net.sourceforge.sashimi.mzxml.v3_0.MzXML;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.proteomics.InMemoryDTAListImpl;
import edu.umn.msi.tropix.proteomics.conversion.impl.DTAToMzXMLConverterImpl;
import edu.umn.msi.tropix.proteomics.xml.MzXMLUtility;

public class CreateMzXML {

  @Test(groups = {"create"})
  public void createMzXML() throws Exception {
    final DTAToMzXMLConverterImpl converter = new DTAToMzXMLConverterImpl();
    final InMemoryDTAListImpl dtaList = new InMemoryDTAListImpl();
    final String[] dtaNames = new String[] {"mrr.103.106.1.dta", "mrr.1105.1106.2.dta", "mrr.1105.1106.3.dta", "mrr.2025.2026.2.dta", "mrr.2025.2026.3.dta", "mrr.3009.3011.1.dta"};
    for(final String dtaName : dtaNames) {
      final InputStream dtaStream = ProteomicsTests.getResourceAsStream(dtaName);
      dtaList.add(IOUtils.toByteArray(dtaStream), dtaName);
    }
    final MzXML mzxml = converter.dtaToMzXML(dtaList, null);
    final MzXMLUtility mzxmlUtility = new MzXMLUtility();
    mzxmlUtility.serialize(mzxml, File.createTempFile("valid", "mzxml"));
    System.out.println(mzxmlUtility.toString(mzxml));
  }
}
