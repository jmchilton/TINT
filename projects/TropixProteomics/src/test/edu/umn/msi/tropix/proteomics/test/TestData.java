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

import java.io.IOException;
import java.io.InputStream;

import net.sourceforge.sashimi.mzxml.v3_0.MzXML;
import edu.umn.msi.tropix.common.xml.XMLException;
import edu.umn.msi.tropix.models.xtandem.XTandemParameters;
import edu.umn.msi.tropix.proteomics.parameters.ParameterUtils;
import edu.umn.msi.tropix.proteomics.xml.MzXMLUtility;

public class TestData {

  public static MzXML getMzXML(final String id) throws XMLException {
    final MzXMLUtility mzxmlUtility = new MzXMLUtility();
    return mzxmlUtility.deserialize(ProteomicsTests.getResourceAsStream(id));
  }

  public static XTandemParameters getXTandemParameters(final String id) throws IOException {
    final InputStream xTandemPropertiesInputStream = ProteomicsTests.getResourceAsStream(id);
    final XTandemParameters xTandemParameters = new XTandemParameters();
    ParameterUtils.setParametersFromProperties(xTandemPropertiesInputStream, xTandemParameters);
    return xTandemParameters;
  }

}
