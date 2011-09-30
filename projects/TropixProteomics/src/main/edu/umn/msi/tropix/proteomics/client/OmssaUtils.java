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

package edu.umn.msi.tropix.proteomics.client;

import edu.umn.msi.tropix.common.io.InputContext;
import gov.nih.nlm.ncbi.omssa.MSSearchSettings;

import javax.xml.namespace.QName;

public class OmssaUtils {
  private static final QName MS_SEARCH_SETTINGS_QNAME = new QName("http://www.ncbi.nlm.nih.gov", "MSSearchSettings");

  public static MSSearchSettings extractMSSearchSettings(final InputContext inputContext) {
    return AxisXmlParameterUtils.deserialize(inputContext, MSSearchSettings.class);
  }

  public static String serializeMSSearchSettigns(final MSSearchSettings msSearchSettings) {
    return AxisXmlParameterUtils.serialize(msSearchSettings, MS_SEARCH_SETTINGS_QNAME);
  }

}
