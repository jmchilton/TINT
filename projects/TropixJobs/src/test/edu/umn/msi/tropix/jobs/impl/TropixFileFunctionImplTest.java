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

package edu.umn.msi.tropix.jobs.impl;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.TropixFile;

public class TropixFileFunctionImplTest {

  @Test(groups = "unit", timeOut = 1000)
  public void apply() {
    final TropixFileFunctionImpl function = new TropixFileFunctionImpl("http://moo");
    final TropixFile file = function.apply("12345");
    assert file.getFileId().equals("12345");
    assert file.getStorageServiceUrl().equals("http://moo");
  }
}
