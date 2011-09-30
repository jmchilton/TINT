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

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.proteomics.bumbershoot.parameters.MyriMatchParameters;

public class MyriMatchUtilsTest {

  @Test(groups = "unit")
  public void testSerialization() {
    final MyriMatchParameters parameters = new MyriMatchParameters();
    parameters.setClassSizeMultiplier(103);
    final String serializedParameters = MyriMatchUtils.serialize(parameters);
    final InputContext serializedParametersContext = InputContexts.forString(serializedParameters);
    final MyriMatchParameters convertedParameters = MyriMatchUtils.deserialize(serializedParametersContext);
    assert convertedParameters != null;
    assert convertedParameters.getClassSizeMultiplier() == 103;
    assert convertedParameters.equals(parameters);
  }
}
