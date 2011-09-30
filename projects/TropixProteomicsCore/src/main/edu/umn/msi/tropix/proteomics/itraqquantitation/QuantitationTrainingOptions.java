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

import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import edu.umn.msi.tropix.proteomics.itraqquantitation.options.QuantificationType;
import edu.umn.msi.tropix.proteomics.itraqquantitation.training.QuantificationTrainingOptions;

public class QuantitationTrainingOptions {
  private final ImmutableList<File> inputMzxmlFiles;
  private final File inputScaffoldReport;
  private final File outputFile;
  private final QuantificationType quantificationType;
  private final QuantificationTrainingOptions trainingOptions;

  public static final class QuantitationOptionsBuilder {
    private final ImmutableList<File> inputMzxmlFiles;
    private final File inputScaffoldReport;
    private File outputFile = new File("quantification_output.csv");
    private QuantificationTrainingOptions trainingOptions;
    private QuantificationType quantificationType = QuantificationType.FOUR_PLEX;

    private QuantitationOptionsBuilder(final Iterable<File> inputMzxmlFiles, final File inputScaffoldReport) {
      this.inputMzxmlFiles = ImmutableList.copyOf(inputMzxmlFiles);
      this.inputScaffoldReport = inputScaffoldReport;
    }

    public QuantitationOptionsBuilder withTrainingOptions(final QuantificationTrainingOptions trainingOptions) {
      this.trainingOptions = trainingOptions;
      return this;
    }

    public QuantitationOptionsBuilder ofType(final QuantificationType quantificationType) {
      this.quantificationType = quantificationType;
      return this;
    }

    public QuantitationOptionsBuilder is4Plex() {
      this.quantificationType = QuantificationType.FOUR_PLEX;
      return this;
    }

    public QuantitationOptionsBuilder is8Plex() {
      this.quantificationType = QuantificationType.EIGHT_PLEX;
      return this;
    }

    public QuantitationOptionsBuilder withOutput(final File outputFile) {
      this.outputFile = outputFile;
      return this;
    }

    public QuantitationTrainingOptions get() {
      return new QuantitationTrainingOptions(inputMzxmlFiles, inputScaffoldReport, outputFile, quantificationType, trainingOptions);
    }
  }

  public static QuantitationOptionsBuilder forInput(final Iterable<File> inputMzxmlFiles, final File inputScaffoldReport) {
    return new QuantitationOptionsBuilder(inputMzxmlFiles, inputScaffoldReport);
  }

  public QuantitationTrainingOptions(final ImmutableList<File> inputMzxmlFiles, final File inputScaffoldReport, final File outputFile, final QuantificationType quantificationType, final QuantificationTrainingOptions trainingOptions) {
    this.inputMzxmlFiles = inputMzxmlFiles;
    this.inputScaffoldReport = inputScaffoldReport;
    this.outputFile = outputFile;
    this.quantificationType = quantificationType;
    this.trainingOptions = trainingOptions;
  }

  public ImmutableList<File> getInputMzxmlFiles() {
    return inputMzxmlFiles;
  }

  public File getInputScaffoldReport() {
    return inputScaffoldReport;
  }

  public File getOutputFile() {
    return outputFile;
  }

  public QuantificationType getQuantificationType() {
    return quantificationType;
  }

  public QuantificationTrainingOptions getTrainingOptions() {
    return trainingOptions;
  }

  public String toString() {
    return "QuantitationTrainingOptions[mzxmlFiles" + Joiner.on(",").join(inputMzxmlFiles) + ",scaffoldFile=" + inputScaffoldReport + ", outputFile=" + outputFile + ", type=" + quantificationType.getValue() + ",weights=" + ToStringBuilder.reflectionToString(trainingOptions)
        + "]";
  }

}
