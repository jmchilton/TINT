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

package edu.umn.msi.tropix.proteomics.itraqquantitation.impl;

import java.io.File;

import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.proteomics.itraqquantitation.QuantitationOptions;
import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.ReportExtractor.ReportType;
import edu.umn.msi.tropix.proteomics.itraqquantitation.options.QuantificationType;

public class QuantitationOptionsTest {

  @Test(groups = "unit")
  public void quantitationOptions() {
    final QuantitationOptions options = QuantitationOptions
        .forInput(Lists.newArrayList(new File("moo")), new InputReport(new File("report"), ReportType.SCAFFOLD))
        .withOutput(new File("output")).is4Plex().get();
    assert options.getOutputFile().equals(new File("output"));
    assert options.getInputReport().getReport().equals(new File("report"));
    assert options.getInputMzxmlFiles().size() == 1;
    assert options.getInputMzxmlFiles().get(0).equals(new File("moo"));
    assert options.getQuantificationType() == QuantificationType.FOUR_PLEX;
  }
}
