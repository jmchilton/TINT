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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import edu.umn.msi.tropix.proteomics.itraqquantitation.options.QuantificationType;
import edu.umn.msi.tropix.proteomics.itraqquantitation.weight.QuantificationWeights;

public final class QuantitationOptions {
  @Nonnull
  private final ImmutableList<File> inputMzxmlFiles;
  @Nonnull
  private final File inputScaffoldReport;
  @Nonnull
  private final File outputFile;
  @Nonnull
  private final QuantificationType quantificationType;
  @Nullable
  private final QuantificationWeights weights;

  public static final class QuantitationOptionsBuilder {
    private final ImmutableList<File> inputMzxmlFiles;
    private final File inputScaffoldReport;
    private QuantificationWeights weights = null;
    private File outputFile = new File("quantification_output.csv");
    private QuantificationType quantificationType = QuantificationType.FOUR_PLEX;

    private QuantitationOptionsBuilder(final Iterable<File> inputMzxmlFiles, final File inputScaffoldReport) {
      this.inputMzxmlFiles = ImmutableList.copyOf(inputMzxmlFiles);
      this.inputScaffoldReport = inputScaffoldReport;
    }

    public QuantitationOptionsBuilder withWeights(final QuantificationWeights weights) {
      this.weights = weights;
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

    public QuantitationOptions get() {
      return new QuantitationOptions(inputMzxmlFiles, inputScaffoldReport, outputFile, quantificationType, weights);
    }
  }

  public QuantitationOptions(final ImmutableList<File> inputMzxmlFiles, final File inputScaffoldReport, final File outputFile, final QuantificationType quantificationType, final QuantificationWeights weights) {
    this.inputMzxmlFiles = inputMzxmlFiles;
    this.inputScaffoldReport = inputScaffoldReport;
    this.outputFile = outputFile;
    this.quantificationType = quantificationType;
    this.weights = weights;
  }

  public static QuantitationOptionsBuilder forInput(final Iterable<File> inputMzxmlFiles, final File inputScaffoldReport) {
    return new QuantitationOptionsBuilder(inputMzxmlFiles, inputScaffoldReport);
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

  public QuantificationWeights getWeights() {
    return weights;
  }

  public String toString() {
    return "QuantitationOptions[mzxmlFiles" + Joiner.on(",").join(inputMzxmlFiles) + ",scaffoldFile=" + inputScaffoldReport + ", outputFile=" + outputFile + ", type=" + quantificationType.getValue() + ",weights=" + weights + "]";
  }

}
