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

import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.InputReport;
import edu.umn.msi.tropix.proteomics.itraqquantitation.options.QuantificationType;
import edu.umn.msi.tropix.proteomics.itraqquantitation.weight.QuantificationWeights;

public final class QuantitationOptions {
  @Nonnull
  private final ImmutableList<File> inputMzxmlFiles;
  @Nonnull
  private final InputReport inputScaffoldReport;
  @Nonnull
  private final File outputFile;
  @Nonnull
  private final QuantificationType quantificationType;
  @Nullable
  private final QuantificationWeights weights;
  @Nullable
  private final GroupType groupType;
  private final boolean includeNormalized;
  private final int threads;

  public static enum GroupType {
    PROTEIN("Protein"),
    PEPTIDE("Peptide"),
    PEPTIDE_WITH_MODIFICATIONS("Peptide with Modifications"),
    PEPTIDE_WITH_UNIQUE_MODIFICATION("Peptide with an Identified Modification");

    private final String headerLabel;

    private GroupType(final String headerLabel) {
      this.headerLabel = headerLabel;
    }

    public String getHeaderLabel() {
      return headerLabel;
    }
  }

  public static final class QuantitationOptionsBuilder {
    private final ImmutableList<File> inputMzxmlFiles;
    private final InputReport inputScaffoldReport;
    private GroupType groupType;
    private QuantificationWeights weights = null;
    private File outputFile = new File("quantification_output.csv");
    private QuantificationType quantificationType = QuantificationType.FOUR_PLEX;
    private int threads = 1;
    private boolean includeNormalized = true;

    private QuantitationOptionsBuilder(final Iterable<File> inputMzxmlFiles, final InputReport inputScaffoldReport) {
      this.inputMzxmlFiles = ImmutableList.copyOf(inputMzxmlFiles);
      this.inputScaffoldReport = inputScaffoldReport;
    }

    public QuantitationOptionsBuilder excludeNormalized() {
      this.includeNormalized = false;
      return this;
    }

    public QuantitationOptionsBuilder withGroupType(final GroupType groupType) {
      this.groupType = groupType;
      return this;
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

    public QuantitationOptionsBuilder withThreds(final int threads) {
      this.threads = threads;
      return this;
    }

    public QuantitationOptions get() {
      return new QuantitationOptions(inputMzxmlFiles, inputScaffoldReport, outputFile, quantificationType, weights, groupType, includeNormalized,
          threads);
    }
  }

  public QuantitationOptions(final ImmutableList<File> inputMzxmlFiles, final InputReport inputScaffoldReport, final File outputFile,
      final QuantificationType quantificationType, final QuantificationWeights weights, final GroupType groupType,
      final boolean includeNormalized, final int threads) {
    this.inputMzxmlFiles = inputMzxmlFiles;
    this.inputScaffoldReport = inputScaffoldReport;
    this.outputFile = outputFile;
    this.quantificationType = quantificationType;
    this.weights = weights;
    this.groupType = groupType;
    this.threads = threads;
    this.includeNormalized = includeNormalized;
  }

  public static QuantitationOptionsBuilder forInput(final Iterable<File> inputMzxmlFiles, final InputReport inputScaffoldReport) {
    return new QuantitationOptionsBuilder(inputMzxmlFiles, inputScaffoldReport);
  }

  public ImmutableList<File> getInputMzxmlFiles() {
    return inputMzxmlFiles;
  }

  public InputReport getInputReport() {
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
    return "QuantitationOptions[mzxmlFiles" + Joiner.on(",").join(inputMzxmlFiles) + ",scaffoldFile=" + inputScaffoldReport + ", outputFile="
        + outputFile + ", type=" + quantificationType.getValue() + ",weights=" + weights + "]";
  }

  public GroupType getGroupType() {
    return groupType;
  }

  public int getThreads() {
    return threads;
  }

  public boolean includeNormalized() {
    return includeNormalized;
  }

}
