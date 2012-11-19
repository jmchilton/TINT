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

package edu.umn.msi.tropix.proteomics.itraqquantitation;

import java.io.File;

import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.proteomics.itraqquantitation.QuantitationOptions.QuantitationOptionsBuilder;
import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.InputReport;
import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.ReportExtractor.ReportType;
import edu.umn.msi.tropix.proteomics.itraqquantitation.options.QuantificationType;
import edu.umn.msi.tropix.proteomics.itraqquantitation.training.QuantificationTrainingOptions;
import edu.umn.msi.tropix.proteomics.itraqquantitation.weight.QuantificationWeights;

public class QuantitationOptionsTest {
  private final File mzxml = new File("input.mzxml");
  private final File spectra = new File("spectra.cvs");
  private File outputFile = new File("output.csv");

  @Test(groups = "unit")
  public void testBuilder() {
    final QuantificationWeights weights = new QuantificationWeights();

    final QuantitationOptionsBuilder builder =
        QuantitationOptions.forInput(Lists.newArrayList(mzxml), new InputReport(spectra, ReportType.SCAFFOLD));

    final QuantitationOptions options =
        builder.is4Plex().withWeights(weights).withOutput(outputFile).get();

    assert options.getQuantificationType().equals(QuantificationType.FOUR_PLEX);
    assert Iterables.elementsEqual(options.getInputMzxmlFiles(), Lists.newArrayList(mzxml));
    assert options.getOutputFile().equals(outputFile);
    assert options.getWeights() == weights;
    assert options.getInputReport().getReport().equals(spectra);
    assert options.toString().contains(outputFile.getName());
  }

  @Test(groups = "unit")
  public void testTrainingBuilder() {
    final QuantificationTrainingOptions trainingOptions = new QuantificationTrainingOptions();

    final QuantitationTrainingOptions.QuantitationOptionsBuilder builder =
        QuantitationTrainingOptions.forInput(Lists.newArrayList(mzxml), new InputReport(spectra, ReportType.SCAFFOLD));

    final QuantitationTrainingOptions options =
        builder.is8Plex().withOutput(outputFile).withTrainingOptions(trainingOptions).get();

    assert options.getQuantificationType().equals(QuantificationType.EIGHT_PLEX);
    assert Iterables.elementsEqual(options.getInputMzxmlFiles(), Lists.newArrayList(mzxml));
    assert options.getOutputFile().equals(outputFile);
    assert options.getTrainingOptions() == trainingOptions;
    assert options.getInputScaffoldReport().getReport().equals(spectra);
    assert options.toString().contains(outputFile.getName());
  }

}
