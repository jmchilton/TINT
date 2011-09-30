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

package edu.umn.msi.tropix.proteomics.xtandem;

import edu.umn.msi.tropix.models.xtandem.XTandemParameters;
import edu.umn.msi.tropix.proteomics.BiomlWriter;

public class XTandemBeanParameterTranslator implements XTandemParameterTranslator {
  private static final boolean USE_ANNOTATIONS = false;
  private String defaultsPath = null;
  private String message = null;

  public static String convertBoolean(final boolean boolValue) {
    return boolValue ? "yes" : "no";
  }

  public void setMessage(final String message) {
    this.message = message;
  }

  public String getXTandemParameters(final XTandemParameters inputParameters, final String outputPath, final String inputPath, final String taxon,
      final String taxonomyPath, final String xslPath) {
    final XTandemParameters xTandemParameters = inputParameters;
    final BiomlWriter writer = new BiomlWriter();
    writer.addHeader("list path");
    if(defaultsPath != null) {
      writer.addVariable("list path", "default parameters", defaultsPath);
    }
    writer.addVariable("list path", "taxonomy information", taxonomyPath);

    writer.addHeader("output");
    writer.addVariable("output", "histogram column width", xTandemParameters.getOutputHistogramColumnWidth());
    writer.addVariable("output", "histograms", convertBoolean(xTandemParameters.getOutputHistograms()));
    // Skipping log path
    writer.addVariable("output", "maximum valid expectation value", xTandemParameters.getOutputMaximumValidExpectationValue());
    // Skipping message
    writer.addVariable("output", "one sequence copy", convertBoolean(xTandemParameters.getOutputOneSequencePerCopy()));
    writer.addVariable("output", "parameters", "yes");
    writer.addVariable("output", "path", outputPath);
    writer.addVariable("output", "path hashing", "no");
    writer.addVariable("output", "performance", convertBoolean(xTandemParameters.getOutputPerformance()));
    writer.addVariable("output", "proteins", convertBoolean(xTandemParameters.getOutputProteins()));
    writer.addVariable("output", "results", xTandemParameters.getOutputResults());
    // Skipping sequence path
    writer.addVariable("output", "sort results by", xTandemParameters.getOutputSortResultsBy());
    writer.addVariable("output", "sequences", convertBoolean(xTandemParameters.getOutputSequences()));
    writer.addVariable("output", "spectra", convertBoolean(xTandemParameters.getOutputSpectra()));
    if(message != null) {
      writer.addVariable("output", "message", message);
    }
    if(xslPath != null) {
      writer.addVariable("output", "xsl path", xslPath);
    }

    writer.addHeader("protein");
    writer.addVariable("protein", "cleavage C-terminal mass change", xTandemParameters.getProteinCleavageCTerminalMassChange());
    writer.addVariable("protein", "cleavage N-terminal mass change", xTandemParameters.getProteinCleavageNTerminalMassChange());
    writer.addVariable("protein", "cleavage semi", convertBoolean(xTandemParameters.getProteinCleavageSemi()));
    writer.addVariable("protein", "cleavage site", xTandemParameters.getProteinCleavageSite());
    writer.addVariable("protein", "C-terminal residue modification mass", xTandemParameters.getProteinCTerminalResidueModificationMass());
    writer.addVariable("protein", "N-terminal residue modification mass", xTandemParameters.getProteinNTerminalResidueModificationMass());
    writer.addVariable("protein", "taxon", taxon);
    writer.addVariable("protein", "use annotations", "no");

    writer.addHeader("refine");
    if(xTandemParameters.getRefine()) {
      writer.addVariable("refine", "", "yes");
      writer.addVariable("refine", "cleavage semi", convertBoolean(xTandemParameters.getRefineCleavageSemi()));
      writer.addVariable("refine", "maximum valid expectation value", xTandemParameters.getRefineMaximumValidExpectationValue());
      writer.addVariable("refine", "modification mass", xTandemParameters.getRefineModificationMass());
      writer.addVariable("refine", "potential modification mass", xTandemParameters.getRefinePotentialModificationMass());
      writer.addVariable("refine", "potential modification motif", xTandemParameters.getRefinePotentialModificationMotif());
      writer.addVariable("refine", "potential N-terminus modifications", xTandemParameters.getRefinePotentialNTerminusModifications());
      writer.addVariable("refine", "potential C-terminus modifications", xTandemParameters.getRefinePotentialCTerminusModifications());
      writer.addVariable("refine", "saps", convertBoolean(xTandemParameters.getRefineSingleAminoAcidPolymorphisms()));
      writer.addVariable("refine", "spectrum synthesis", convertBoolean(xTandemParameters.getRefineSpectrumSynthesis()));
      writer.addVariable("refine", "unanticipated cleavage", convertBoolean(xTandemParameters.getRefineUnanticipatedCleavage()));
      writer.addVariable("refine", "use annotations", convertBoolean(USE_ANNOTATIONS));
      writer.addVariable("refine", "use potential modifications for full refinement",
          convertBoolean(xTandemParameters.getRefineUsePotentialModifications()));
      if(xTandemParameters.getRefinePointMutations() != null) {
        writer.addVariable("refine", "point mutations", convertBoolean(xTandemParameters.getRefinePointMutations()));
      }
    } else {
      writer.addVariable("refine", "", "no");
    }

    writer.addHeader("residue");
    writer.addVariable("residue", "modification mass", xTandemParameters.getResidueModificationMass());
    writer.addVariable("residue", "potential modification mass", xTandemParameters.getResiduePotentialModificationMass());
    writer.addVariable("residue", "potential modification motif", xTandemParameters.getResiduePotentialModificationMotif());

    writer.addHeader("scoring");
    writer.addVariable("scoring", "a ions", convertBoolean(xTandemParameters.getScoringAIons()));
    writer.addVariable("scoring", "b ions", convertBoolean(xTandemParameters.getScoringBIons()));
    writer.addVariable("scoring", "c ions", convertBoolean(xTandemParameters.getScoringCIons()));
    writer.addVariable("scoring", "x ions", convertBoolean(xTandemParameters.getScoringXIons()));
    writer.addVariable("scoring", "y ions", convertBoolean(xTandemParameters.getScoringYIons()));
    writer.addVariable("scoring", "z ions", convertBoolean(xTandemParameters.getScoringZIons()));
    writer.addVariable("scoring", "cyclic permutation", convertBoolean(xTandemParameters.getScoringCyclicPermutation()));
    writer.addVariable("scoring", "include reverse", convertBoolean(xTandemParameters.getScoringIncludeReverse()));
    writer.addVariable("scoring", "maximum missed cleavage sites", xTandemParameters.getScoringMaximumMissedCleavageSites());
    writer.addVariable("scoring", "minimum ion count", xTandemParameters.getScoringMinimumIonCount());

    writer.addHeader("spectrum");
    final Integer maximumParentCharge = xTandemParameters.getSpectrumMaximumParentCharge();
    if(maximumParentCharge != null) {
      writer.addVariable("spectrum", "maximum parent charge", maximumParentCharge);
    }
    writer.addVariable("spectrum", "contrast angle", xTandemParameters.getSpectrumContrastAngle());
    writer.addVariable("spectrum", "dynamic range", xTandemParameters.getSpectrumDynamicRange());
    writer.addVariable("spectrum", "fragment mass error", xTandemParameters.getSpectrumFragmentMassError());
    writer.addVariable("spectrum", "fragment mass error units", xTandemParameters.getSpectrumFragmentMassErrorUnits());
    writer.addVariable("spectrum", "fragment mass type", xTandemParameters.getSpectrumFragmentMassType());
    writer.addVariable("spectrum", "fragment monoisotopic mass error", xTandemParameters.getSpectrumFragmentMassError());
    writer.addVariable("spectrum", "fragment monoisotopic mass error units", xTandemParameters.getSpectrumFragmentMassErrorUnits());
    writer.addVariable("spectrum", "minimum fragment mz", xTandemParameters.getSpectrumMinimumFragmentMZ());
    writer.addVariable("spectrum", "minimum peaks", xTandemParameters.getSpectrumMinimumPeaks());
    writer.addVariable("spectrum", "minimum parent m+h", xTandemParameters.getSpectrumMinimumParentMH());
    writer.addVariable("spectrum", "neutral loss mass", xTandemParameters.getSpectrumNeutralLossMass());
    writer.addVariable("spectrum", "neutral loss window", xTandemParameters.getSpectrumNeutralLossWindow());
    writer.addVariable("spectrum", "parent monoisotopic mass error minus", xTandemParameters.getSpectrumParentMonoisotopicMassErrorMinus());
    writer.addVariable("spectrum", "parent monoisotopic mass error plus", xTandemParameters.getSpectrumParentMonoisotopicMassErrorPlus());
    writer.addVariable("spectrum", "parent monoisotopic mass error units", xTandemParameters.getSpectrumParentMonoisotopicMassErrorUnits());
    writer.addVariable("spectrum", "parent monoisotopic mass isotope error",
        convertBoolean(xTandemParameters.getSpectrumParentMonoisotopicMassIsotopeError()));
    writer.addVariable("spectrum", "path", inputPath);
    writer.addVariable("spectrum", "path type", "mzxml");
    // Skipping sequence batch size, threads
    writer.addVariable("spectrum", "total peaks", xTandemParameters.getSpectrumTotalPeaks());
    writer.addVariable("spectrum", "use neutral loss window", convertBoolean(xTandemParameters.getSpectrumUseNeutralLossWindow()));
    writer.addVariable("spectrum", "use noise suppression", convertBoolean(xTandemParameters.getSpectrumUseNoiseSuppression()));
    writer.addVariable("spectrum", "use contrast angle", convertBoolean(xTandemParameters.getSpectrumUseContrastAngle()));
    return writer.toString();
  }

  public void setDefaultsPath(final String defaultsPath) {
    this.defaultsPath = defaultsPath;
  }

}
