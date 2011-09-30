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

package edu.umn.msi.tropix.proteomics.omssa;

import java.io.InputStreamReader;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.grid.xml.SerializationUtils;
import edu.umn.msi.tropix.grid.xml.SerializationUtilsFactory;
import edu.umn.msi.tropix.proteomics.test.ProteomicsTests;
import gov.nih.nlm.ncbi.omssa.MSSearchSettings;

public class OmssaSerializationTest {

  @Test(groups = "unit")
  public void serialization() {
    final SerializationUtils utils = SerializationUtilsFactory.getInstance();
    utils.deserialize(new InputStreamReader(ProteomicsTests.getResourceAsStream("default.omssa.params.xml")), MSSearchSettings.class);
  }

}
