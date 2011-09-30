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
import edu.umn.msi.tropix.proteomics.bumbershoot.parameters.MyriMatchParameters;

public class MyriMatchUtils {

  public static MyriMatchParameters deserialize(final InputContext inputContext) {
    return AxisXmlParameterUtils.deserialize(inputContext, MyriMatchParameters.class);
  }

  public static String serialize(final MyriMatchParameters parameters) {
    return AxisXmlParameterUtils.serialize(parameters, MyriMatchParameters.getTypeDesc().getXmlType());
  }
}
