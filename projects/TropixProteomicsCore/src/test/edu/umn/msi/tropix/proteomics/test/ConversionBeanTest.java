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

import org.testng.annotations.Test;

import edu.umn.msi.tropix.proteomics.conversion.DTAToMzXMLOptions;
import edu.umn.msi.tropix.proteomics.conversion.MzXMLToDTAOptions;

public class ConversionBeanTest {

  @Test(groups = "unit")
  public void dtaToMzXMLOptionsTest() {
    final DTAToMzXMLOptions opts = new DTAToMzXMLOptions();
    opts.setDoublePrecision(true);
    assert opts.getDoublePrecision();
    opts.setDoublePrecision(false);
    assert !opts.getDoublePrecision();
  }

  @Test(groups = "unit")
  public void mzXMLToDTAOptions() {
    final MzXMLToDTAOptions opts = new MzXMLToDTAOptions();
    opts.setOutputPrecision(13);
    assert opts.getOutputPrecision() == 13;
  }
}
