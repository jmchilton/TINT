package edu.umn.msi.tropix.models.xtandem;

import java.io.Serializable;

/*
 * // Should add at some point
 * public Boolean proteinQuickAcetyl;
 * public Boolean proteinQuickPyrolidone;
 * public Boolean proteinSingleAminoAcidPolymorphisms;
 * public Boolean proteinStpBias;
 */

/**
	* 	**/
public class XTandemParameters implements Serializable {
  /**
   * An attribute to allow serialization of the domain objects
   */
  private static final long serialVersionUID = 1234567890L;

  /**
	* 	**/
  public Integer outputHistogramColumnWidth;

  /**
   * Retreives the value of outputHistogramColumnWidth attribute
   * 
   * @return outputHistogramColumnWidth
   **/

  public Integer getOutputHistogramColumnWidth() {
    return outputHistogramColumnWidth;
  }

  /**
   * Sets the value of outputHistogramColumnWidth attribue
   **/

  public void setOutputHistogramColumnWidth(final Integer outputHistogramColumnWidth) {
    this.outputHistogramColumnWidth = outputHistogramColumnWidth;
  }

  /**
	* 	**/
  public Boolean outputHistograms;

  /**
   * Retreives the value of outputHistograms attribute
   * 
   * @return outputHistograms
   **/

  public Boolean getOutputHistograms() {
    return outputHistograms;
  }

  /**
   * Sets the value of outputHistograms attribue
   **/

  public void setOutputHistograms(final Boolean outputHistograms) {
    this.outputHistograms = outputHistograms;
  }

  /**
	* 	**/
  public Double outputMaximumValidExpectationValue;

  /**
   * Retreives the value of outputMaximumValidExpectationValue attribute
   * 
   * @return outputMaximumValidExpectationValue
   **/

  public Double getOutputMaximumValidExpectationValue() {
    return outputMaximumValidExpectationValue;
  }

  /**
   * Sets the value of outputMaximumValidExpectationValue attribue
   **/

  public void setOutputMaximumValidExpectationValue(final Double outputMaximumValidExpectationValue) {
    this.outputMaximumValidExpectationValue = outputMaximumValidExpectationValue;
  }

  /**
	* 	**/
  public Boolean outputOneSequencePerCopy;

  /**
   * Retreives the value of outputOneSequencePerCopy attribute
   * 
   * @return outputOneSequencePerCopy
   **/

  public Boolean getOutputOneSequencePerCopy() {
    return outputOneSequencePerCopy;
  }

  /**
   * Sets the value of outputOneSequencePerCopy attribue
   **/

  public void setOutputOneSequencePerCopy(final Boolean outputOneSequencePerCopy) {
    this.outputOneSequencePerCopy = outputOneSequencePerCopy;
  }

  /**
	* 	**/
  public Boolean outputPerformance;

  /**
   * Retreives the value of outputPerformance attribute
   * 
   * @return outputPerformance
   **/

  public Boolean getOutputPerformance() {
    return outputPerformance;
  }

  /**
   * Sets the value of outputPerformance attribue
   **/

  public void setOutputPerformance(final Boolean outputPerformance) {
    this.outputPerformance = outputPerformance;
  }

  /**
	* 	**/
  public Boolean outputProteins;

  /**
   * Retreives the value of outputProteins attribute
   * 
   * @return outputProteins
   **/

  public Boolean getOutputProteins() {
    return outputProteins;
  }

  /**
   * Sets the value of outputProteins attribue
   **/

  public void setOutputProteins(final Boolean outputProteins) {
    this.outputProteins = outputProteins;
  }

  /**
	* 	**/
  public String outputResults;

  /**
   * Retreives the value of outputResults attribute
   * 
   * @return outputResults
   **/

  public String getOutputResults() {
    return outputResults;
  }

  /**
   * Sets the value of outputResults attribue
   **/

  public void setOutputResults(final String outputResults) {
    this.outputResults = outputResults;
  }

  /**
	* 	**/
  public String outputSortResultsBy;

  /**
   * Retreives the value of outputSortResultsBy attribute
   * 
   * @return outputSortResultsBy
   **/

  public String getOutputSortResultsBy() {
    return outputSortResultsBy;
  }

  /**
   * Sets the value of outputSortResultsBy attribue
   **/

  public void setOutputSortResultsBy(final String outputSortResultsBy) {
    this.outputSortResultsBy = outputSortResultsBy;
  }

  /**
	* 	**/
  public Boolean outputSequences;

  /**
   * Retreives the value of outputSequences attribute
   * 
   * @return outputSequences
   **/

  public Boolean getOutputSequences() {
    return outputSequences;
  }

  /**
   * Sets the value of outputSequences attribue
   **/

  public void setOutputSequences(final Boolean outputSequences) {
    this.outputSequences = outputSequences;
  }

  /**
	* 	**/
  public Boolean outputSpectra;

  /**
   * Retreives the value of outputSpectra attribute
   * 
   * @return outputSpectra
   **/

  public Boolean getOutputSpectra() {
    return outputSpectra;
  }

  /**
   * Sets the value of outputSpectra attribue
   **/

  public void setOutputSpectra(final Boolean outputSpectra) {
    this.outputSpectra = outputSpectra;
  }

  /**
	* 	**/
  public Double proteinCleavageCTerminalMassChange;

  /**
   * Retreives the value of proteinCleavageCTerminalMassChange attribute
   * 
   * @return proteinCleavageCTerminalMassChange
   **/

  public Double getProteinCleavageCTerminalMassChange() {
    return proteinCleavageCTerminalMassChange;
  }

  /**
   * Sets the value of proteinCleavageCTerminalMassChange attribue
   **/

  public void setProteinCleavageCTerminalMassChange(final Double proteinCleavageCTerminalMassChange) {
    this.proteinCleavageCTerminalMassChange = proteinCleavageCTerminalMassChange;
  }

  /**
	* 	**/
  public Double proteinCleavageNTerminalMassChange;

  /**
   * Retreives the value of proteinCleavageNTerminalMassChange attribute
   * 
   * @return proteinCleavageNTerminalMassChange
   **/

  public Double getProteinCleavageNTerminalMassChange() {
    return proteinCleavageNTerminalMassChange;
  }

  /**
   * Sets the value of proteinCleavageNTerminalMassChange attribue
   **/

  public void setProteinCleavageNTerminalMassChange(final Double proteinCleavageNTerminalMassChange) {
    this.proteinCleavageNTerminalMassChange = proteinCleavageNTerminalMassChange;
  }

  /**
	* 	**/
  public Double proteinCTerminalResidueModificationMass;

  /**
   * Retreives the value of proteinCTerminalResidueModificationMass attribute
   * 
   * @return proteinCTerminalResidueModificationMass
   **/

  public Double getProteinCTerminalResidueModificationMass() {
    return proteinCTerminalResidueModificationMass;
  }

  /**
   * Sets the value of proteinCTerminalResidueModificationMass attribue
   **/

  public void setProteinCTerminalResidueModificationMass(final Double proteinCTerminalResidueModificationMass) {
    this.proteinCTerminalResidueModificationMass = proteinCTerminalResidueModificationMass;
  }

  /**
	* 	**/
  public Double proteinNTerminalResidueModificationMass;

  /**
   * Retreives the value of proteinNTerminalResidueModificationMass attribute
   * 
   * @return proteinNTerminalResidueModificationMass
   **/

  public Double getProteinNTerminalResidueModificationMass() {
    return proteinNTerminalResidueModificationMass;
  }

  /**
   * Sets the value of proteinNTerminalResidueModificationMass attribue
   **/

  public void setProteinNTerminalResidueModificationMass(final Double proteinNTerminalResidueModificationMass) {
    this.proteinNTerminalResidueModificationMass = proteinNTerminalResidueModificationMass;
  }

  /**
	* 	**/
  public Boolean proteinCleavageSemi;

  /**
   * Retreives the value of proteinCleavageSemi attribute
   * 
   * @return proteinCleavageSemi
   **/

  public Boolean getProteinCleavageSemi() {
    return proteinCleavageSemi;
  }

  /**
   * Sets the value of proteinCleavageSemi attribue
   **/

  public void setProteinCleavageSemi(final Boolean proteinCleavageSemi) {
    this.proteinCleavageSemi = proteinCleavageSemi;
  }

  /**
	* 	**/
  public String proteinCleavageSite;

  /**
   * Retreives the value of proteinCleavageSite attribute
   * 
   * @return proteinCleavageSite
   **/

  public String getProteinCleavageSite() {
    return proteinCleavageSite;
  }

  /**
   * Sets the value of proteinCleavageSite attribue
   **/

  public void setProteinCleavageSite(final String proteinCleavageSite) {
    this.proteinCleavageSite = proteinCleavageSite;
  }

  /**
	* 	**/
  public String residueModificationMass;

  /**
   * Retreives the value of residueModificationMass attribute
   * 
   * @return residueModificationMass
   **/

  public String getResidueModificationMass() {
    return residueModificationMass;
  }

  /**
   * Sets the value of residueModificationMass attribue
   **/

  public void setResidueModificationMass(final String residueModificationMass) {
    this.residueModificationMass = residueModificationMass;
  }

  /**
	* 	**/
  public String residuePotentialModificationMass;

  /**
   * Retreives the value of residuePotentialModificationMass attribute
   * 
   * @return residuePotentialModificationMass
   **/

  public String getResiduePotentialModificationMass() {
    return residuePotentialModificationMass;
  }

  /**
   * Sets the value of residuePotentialModificationMass attribue
   **/

  public void setResiduePotentialModificationMass(final String residuePotentialModificationMass) {
    this.residuePotentialModificationMass = residuePotentialModificationMass;
  }

  /**
	* 	**/
  public String residuePotentialModificationMotif;

  /**
   * Retreives the value of residuePotentialModificationMotif attribute
   * 
   * @return residuePotentialModificationMotif
   **/

  public String getResiduePotentialModificationMotif() {
    return residuePotentialModificationMotif;
  }

  /**
   * Sets the value of residuePotentialModificationMotif attribue
   **/

  public void setResiduePotentialModificationMotif(final String residuePotentialModificationMotif) {
    this.residuePotentialModificationMotif = residuePotentialModificationMotif;
  }

  /**
	* 	**/
  public Boolean scoringAIons;

  /**
   * Retreives the value of scoringAIons attribute
   * 
   * @return scoringAIons
   **/

  public Boolean getScoringAIons() {
    return scoringAIons;
  }

  /**
   * Sets the value of scoringAIons attribue
   **/

  public void setScoringAIons(final Boolean scoringAIons) {
    this.scoringAIons = scoringAIons;
  }

  /**
	* 	**/
  public Boolean scoringBIons;

  /**
   * Retreives the value of scoringBIons attribute
   * 
   * @return scoringBIons
   **/

  public Boolean getScoringBIons() {
    return scoringBIons;
  }

  /**
   * Sets the value of scoringBIons attribue
   **/

  public void setScoringBIons(final Boolean scoringBIons) {
    this.scoringBIons = scoringBIons;
  }

  /**
	* 	**/
  public Boolean scoringCIons;

  /**
   * Retreives the value of scoringCIons attribute
   * 
   * @return scoringCIons
   **/

  public Boolean getScoringCIons() {
    return scoringCIons;
  }

  /**
   * Sets the value of scoringCIons attribue
   **/

  public void setScoringCIons(final Boolean scoringCIons) {
    this.scoringCIons = scoringCIons;
  }

  /**
	* 	**/
  public Boolean scoringXIons;

  /**
   * Retreives the value of scoringXIons attribute
   * 
   * @return scoringXIons
   **/

  public Boolean getScoringXIons() {
    return scoringXIons;
  }

  /**
   * Sets the value of scoringXIons attribue
   **/

  public void setScoringXIons(final Boolean scoringXIons) {
    this.scoringXIons = scoringXIons;
  }

  /**
	* 	**/
  public Boolean scoringYIons;

  /**
   * Retreives the value of scoringYIons attribute
   * 
   * @return scoringYIons
   **/

  public Boolean getScoringYIons() {
    return scoringYIons;
  }

  /**
   * Sets the value of scoringYIons attribue
   **/

  public void setScoringYIons(final Boolean scoringYIons) {
    this.scoringYIons = scoringYIons;
  }

  /**
	* 	**/
  public Boolean scoringZIons;

  /**
   * Retreives the value of scoringZIons attribute
   * 
   * @return scoringZIons
   **/

  public Boolean getScoringZIons() {
    return scoringZIons;
  }

  /**
   * Sets the value of scoringZIons attribue
   **/

  public void setScoringZIons(final Boolean scoringZIons) {
    this.scoringZIons = scoringZIons;
  }

  /**
	* 	**/
  public Boolean scoringCyclicPermutation;

  /**
   * Retreives the value of scoringCyclicPermutation attribute
   * 
   * @return scoringCyclicPermutation
   **/

  public Boolean getScoringCyclicPermutation() {
    return scoringCyclicPermutation;
  }

  /**
   * Sets the value of scoringCyclicPermutation attribue
   **/

  public void setScoringCyclicPermutation(final Boolean scoringCyclicPermutation) {
    this.scoringCyclicPermutation = scoringCyclicPermutation;
  }

  /**
	* 	**/
  public Boolean scoringIncludeReverse;

  /**
   * Retreives the value of scoringIncludeReverse attribute
   * 
   * @return scoringIncludeReverse
   **/

  public Boolean getScoringIncludeReverse() {
    return scoringIncludeReverse;
  }

  /**
   * Sets the value of scoringIncludeReverse attribue
   **/

  public void setScoringIncludeReverse(final Boolean scoringIncludeReverse) {
    this.scoringIncludeReverse = scoringIncludeReverse;
  }

  /**
	* 	**/
  public Integer scoringMaximumMissedCleavageSites;

  /**
   * Retreives the value of scoringMaximumMissedCleavageSites attribute
   * 
   * @return scoringMaximumMissedCleavageSites
   **/

  public Integer getScoringMaximumMissedCleavageSites() {
    return scoringMaximumMissedCleavageSites;
  }

  /**
   * Sets the value of scoringMaximumMissedCleavageSites attribue
   **/

  public void setScoringMaximumMissedCleavageSites(final Integer scoringMaximumMissedCleavageSites) {
    this.scoringMaximumMissedCleavageSites = scoringMaximumMissedCleavageSites;
  }

  /**
	* 	**/
  public Integer scoringMinimumIonCount;

  /**
   * Retreives the value of scoringMinimumIonCount attribute
   * 
   * @return scoringMinimumIonCount
   **/

  public Integer getScoringMinimumIonCount() {
    return scoringMinimumIonCount;
  }

  /**
   * Sets the value of scoringMinimumIonCount attribue
   **/

  public void setScoringMinimumIonCount(final Integer scoringMinimumIonCount) {
    this.scoringMinimumIonCount = scoringMinimumIonCount;
  }

  /**
	* 	**/
  public Double spectrumContrastAngle;

  /**
   * Retreives the value of spectrumContrastAngle attribute
   * 
   * @return spectrumContrastAngle
   **/

  public Double getSpectrumContrastAngle() {
    return spectrumContrastAngle;
  }

  /**
   * Sets the value of spectrumContrastAngle attribue
   **/

  public void setSpectrumContrastAngle(final Double spectrumContrastAngle) {
    this.spectrumContrastAngle = spectrumContrastAngle;
  }

  /**
	* 	**/
  public Double spectrumDynamicRange;

  /**
   * Retreives the value of spectrumDynamicRange attribute
   * 
   * @return spectrumDynamicRange
   **/

  public Double getSpectrumDynamicRange() {
    return spectrumDynamicRange;
  }

  /**
   * Sets the value of spectrumDynamicRange attribue
   **/

  public void setSpectrumDynamicRange(final Double spectrumDynamicRange) {
    this.spectrumDynamicRange = spectrumDynamicRange;
  }

  /**
	* 	**/
  public Double spectrumFragmentMassError;

  /**
   * Retreives the value of spectrumFragmentMassError attribute
   * 
   * @return spectrumFragmentMassError
   **/

  public Double getSpectrumFragmentMassError() {
    return spectrumFragmentMassError;
  }

  /**
   * Sets the value of spectrumFragmentMassError attribue
   **/

  public void setSpectrumFragmentMassError(final Double spectrumFragmentMassError) {
    this.spectrumFragmentMassError = spectrumFragmentMassError;
  }

  /**
	* 	**/
  public String spectrumFragmentMassErrorUnits;

  /**
   * Retreives the value of spectrumFragmentMassErrorUnits attribute
   * 
   * @return spectrumFragmentMassErrorUnits
   **/

  public String getSpectrumFragmentMassErrorUnits() {
    return spectrumFragmentMassErrorUnits;
  }

  /**
   * Sets the value of spectrumFragmentMassErrorUnits attribue
   **/

  public void setSpectrumFragmentMassErrorUnits(final String spectrumFragmentMassErrorUnits) {
    this.spectrumFragmentMassErrorUnits = spectrumFragmentMassErrorUnits;
  }

  /**
	* 	**/
  public String spectrumFragmentMassType;

  /**
   * Retreives the value of spectrumFragmentMassType attribute
   * 
   * @return spectrumFragmentMassType
   **/

  public String getSpectrumFragmentMassType() {
    return spectrumFragmentMassType;
  }

  /**
   * Sets the value of spectrumFragmentMassType attribue
   **/

  public void setSpectrumFragmentMassType(final String spectrumFragmentMassType) {
    this.spectrumFragmentMassType = spectrumFragmentMassType;
  }

  /**
	* 	**/
  public Double spectrumMinimumFragmentMZ;

  /**
   * Retreives the value of spectrumMinimumFragmentMZ attribute
   * 
   * @return spectrumMinimumFragmentMZ
   **/

  public Double getSpectrumMinimumFragmentMZ() {
    return spectrumMinimumFragmentMZ;
  }

  /**
   * Sets the value of spectrumMinimumFragmentMZ attribue
   **/

  public void setSpectrumMinimumFragmentMZ(final Double spectrumMinimumFragmentMZ) {
    this.spectrumMinimumFragmentMZ = spectrumMinimumFragmentMZ;
  }

  /**
	* 	**/
  public Integer spectrumMinimumPeaks;

  /**
   * Retreives the value of spectrumMinimumPeaks attribute
   * 
   * @return spectrumMinimumPeaks
   **/

  public Integer getSpectrumMinimumPeaks() {
    return spectrumMinimumPeaks;
  }

  /**
   * Sets the value of spectrumMinimumPeaks attribue
   **/

  public void setSpectrumMinimumPeaks(final Integer spectrumMinimumPeaks) {
    this.spectrumMinimumPeaks = spectrumMinimumPeaks;
  }

  /**
	* 	**/
  public Double spectrumMinimumParentMH;

  /**
   * Retreives the value of spectrumMinimumParentMH attribute
   * 
   * @return spectrumMinimumParentMH
   **/

  public Double getSpectrumMinimumParentMH() {
    return spectrumMinimumParentMH;
  }

  /**
   * Sets the value of spectrumMinimumParentMH attribue
   **/

  public void setSpectrumMinimumParentMH(final Double spectrumMinimumParentMH) {
    this.spectrumMinimumParentMH = spectrumMinimumParentMH;
  }

  /**
	* 	**/
  public Double spectrumNeutralLossMass;

  /**
   * Retreives the value of spectrumNeutralLossMass attribute
   * 
   * @return spectrumNeutralLossMass
   **/

  public Double getSpectrumNeutralLossMass() {
    return spectrumNeutralLossMass;
  }

  /**
   * Sets the value of spectrumNeutralLossMass attribue
   **/

  public void setSpectrumNeutralLossMass(final Double spectrumNeutralLossMass) {
    this.spectrumNeutralLossMass = spectrumNeutralLossMass;
  }

  /**
	* 	**/
  public Double spectrumParentMonoisotopicMassErrorMinus;

  /**
   * Retreives the value of spectrumParentMonoisotopicMassErrorMinus attribute
   * 
   * @return spectrumParentMonoisotopicMassErrorMinus
   **/

  public Double getSpectrumParentMonoisotopicMassErrorMinus() {
    return spectrumParentMonoisotopicMassErrorMinus;
  }

  /**
   * Sets the value of spectrumParentMonoisotopicMassErrorMinus attribue
   **/

  public void setSpectrumParentMonoisotopicMassErrorMinus(final Double spectrumParentMonoisotopicMassErrorMinus) {
    this.spectrumParentMonoisotopicMassErrorMinus = spectrumParentMonoisotopicMassErrorMinus;
  }

  /**
	* 	**/
  public Double spectrumParentMonoisotopicMassErrorPlus;

  /**
   * Retreives the value of spectrumParentMonoisotopicMassErrorPlus attribute
   * 
   * @return spectrumParentMonoisotopicMassErrorPlus
   **/

  public Double getSpectrumParentMonoisotopicMassErrorPlus() {
    return spectrumParentMonoisotopicMassErrorPlus;
  }

  /**
   * Sets the value of spectrumParentMonoisotopicMassErrorPlus attribue
   **/

  public void setSpectrumParentMonoisotopicMassErrorPlus(final Double spectrumParentMonoisotopicMassErrorPlus) {
    this.spectrumParentMonoisotopicMassErrorPlus = spectrumParentMonoisotopicMassErrorPlus;
  }

  /**
	* 	**/
  public String spectrumParentMonoisotopicMassErrorUnits;

  /**
   * Retreives the value of spectrumParentMonoisotopicMassErrorUnits attribute
   * 
   * @return spectrumParentMonoisotopicMassErrorUnits
   **/

  public String getSpectrumParentMonoisotopicMassErrorUnits() {
    return spectrumParentMonoisotopicMassErrorUnits;
  }

  /**
   * Sets the value of spectrumParentMonoisotopicMassErrorUnits attribue
   **/

  public void setSpectrumParentMonoisotopicMassErrorUnits(final String spectrumParentMonoisotopicMassErrorUnits) {
    this.spectrumParentMonoisotopicMassErrorUnits = spectrumParentMonoisotopicMassErrorUnits;
  }

  /**
	* 	**/
  public Boolean spectrumParentMonoisotopicMassIsotopeError;

  /**
   * Retreives the value of spectrumParentMonoisotopicMassIsotopeError attribute
   * 
   * @return spectrumParentMonoisotopicMassIsotopeError
   **/

  public Boolean getSpectrumParentMonoisotopicMassIsotopeError() {
    return spectrumParentMonoisotopicMassIsotopeError;
  }

  /**
   * Sets the value of spectrumParentMonoisotopicMassIsotopeError attribue
   **/

  public void setSpectrumParentMonoisotopicMassIsotopeError(final Boolean spectrumParentMonoisotopicMassIsotopeError) {
    this.spectrumParentMonoisotopicMassIsotopeError = spectrumParentMonoisotopicMassIsotopeError;
  }

  /**
	* 	**/
  public Integer spectrumTotalPeaks;

  /**
   * Retreives the value of spectrumTotalPeaks attribute
   * 
   * @return spectrumTotalPeaks
   **/

  public Integer getSpectrumTotalPeaks() {
    return spectrumTotalPeaks;
  }

  /**
   * Sets the value of spectrumTotalPeaks attribue
   **/

  public void setSpectrumTotalPeaks(final Integer spectrumTotalPeaks) {
    this.spectrumTotalPeaks = spectrumTotalPeaks;
  }

  /**
	* 	**/
  public Boolean spectrumUseNeutralLossWindow;

  /**
   * Retreives the value of spectrumUseNeutralLossWindow attribute
   * 
   * @return spectrumUseNeutralLossWindow
   **/

  public Boolean getSpectrumUseNeutralLossWindow() {
    return spectrumUseNeutralLossWindow;
  }

  /**
   * Sets the value of spectrumUseNeutralLossWindow attribue
   **/

  public void setSpectrumUseNeutralLossWindow(final Boolean spectrumUseNeutralLossWindow) {
    this.spectrumUseNeutralLossWindow = spectrumUseNeutralLossWindow;
  }

  /**
	* 	**/
  public Boolean spectrumUseNoiseSuppression;

  /**
   * Retreives the value of spectrumUseNoiseSuppression attribute
   * 
   * @return spectrumUseNoiseSuppression
   **/

  public Boolean getSpectrumUseNoiseSuppression() {
    return spectrumUseNoiseSuppression;
  }

  /**
   * Sets the value of spectrumUseNoiseSuppression attribue
   **/

  public void setSpectrumUseNoiseSuppression(final Boolean spectrumUseNoiseSuppression) {
    this.spectrumUseNoiseSuppression = spectrumUseNoiseSuppression;
  }

  /**
	* 	**/
  public Boolean spectrumUseContrastAngle;

  /**
   * Retreives the value of spectrumUseContrastAngle attribute
   * 
   * @return spectrumUseContrastAngle
   **/

  public Boolean getSpectrumUseContrastAngle() {
    return spectrumUseContrastAngle;
  }

  /**
   * Sets the value of spectrumUseContrastAngle attribue
   **/

  public void setSpectrumUseContrastAngle(final Boolean spectrumUseContrastAngle) {
    this.spectrumUseContrastAngle = spectrumUseContrastAngle;
  }

  /**
	* 	**/
  public Double spectrumNeutralLossWindow;

  /**
   * Retreives the value of spectrumNeutralLossWindow attribute
   * 
   * @return spectrumNeutralLossWindow
   **/

  public Double getSpectrumNeutralLossWindow() {
    return spectrumNeutralLossWindow;
  }

  /**
   * Sets the value of spectrumNeutralLossWindow attribue
   **/

  public void setSpectrumNeutralLossWindow(final Double spectrumNeutralLossWindow) {
    this.spectrumNeutralLossWindow = spectrumNeutralLossWindow;
  }

  /**
	* 	**/
  public String id;

  /**
   * Retreives the value of id attribute
   * 
   * @return id
   **/

  public String getId() {
    return id;
  }

  /**
   * Sets the value of id attribue
   **/

  public void setId(final String id) {
    this.id = id;
  }

  /**
	* 	**/
  public Boolean refine;

  /**
   * Retreives the value of refine attribute
   * 
   * @return refine
   **/

  public Boolean getRefine() {
    return refine;
  }

  /**
   * Sets the value of refine attribue
   **/

  public void setRefine(final Boolean refine) {
    this.refine = refine;
  }

  /**
	* 	**/
  public Boolean refineCleavageSemi;

  /**
   * Retreives the value of refineCleavageSemi attribute
   * 
   * @return refineCleavageSemi
   **/

  public Boolean getRefineCleavageSemi() {
    return refineCleavageSemi;
  }

  /**
   * Sets the value of refineCleavageSemi attribue
   **/

  public void setRefineCleavageSemi(final Boolean refineCleavageSemi) {
    this.refineCleavageSemi = refineCleavageSemi;
  }

  /**
	* 	**/
  public Double refineMaximumValidExpectationValue;

  /**
   * Retreives the value of refineMaximumValidExpectationValue attribute
   * 
   * @return refineMaximumValidExpectationValue
   **/

  public Double getRefineMaximumValidExpectationValue() {
    return refineMaximumValidExpectationValue;
  }

  /**
   * Sets the value of refineMaximumValidExpectationValue attribue
   **/

  public void setRefineMaximumValidExpectationValue(final Double refineMaximumValidExpectationValue) {
    this.refineMaximumValidExpectationValue = refineMaximumValidExpectationValue;
  }

  /**
	* 	**/
  public String refineModificationMass;

  /**
   * Retreives the value of refineModificationMass attribute
   * 
   * @return refineModificationMass
   **/

  public String getRefineModificationMass() {
    return refineModificationMass;
  }

  /**
   * Sets the value of refineModificationMass attribue
   **/

  public void setRefineModificationMass(final String refineModificationMass) {
    this.refineModificationMass = refineModificationMass;
  }

  /**
	* 	**/
  public String refinePotentialModificationMass;

  /**
   * Retreives the value of refinePotentialModificationMass attribute
   * 
   * @return refinePotentialModificationMass
   **/

  public String getRefinePotentialModificationMass() {
    return refinePotentialModificationMass;
  }

  /**
   * Sets the value of refinePotentialModificationMass attribue
   **/

  public void setRefinePotentialModificationMass(final String refinePotentialModificationMass) {
    this.refinePotentialModificationMass = refinePotentialModificationMass;
  }

  /**
	* 	**/
  public String refinePotentialModificationMotif;

  /**
   * Retreives the value of refinePotentialModificationMotif attribute
   * 
   * @return refinePotentialModificationMotif
   **/

  public String getRefinePotentialModificationMotif() {
    return refinePotentialModificationMotif;
  }

  /**
   * Sets the value of refinePotentialModificationMotif attribue
   **/

  public void setRefinePotentialModificationMotif(final String refinePotentialModificationMotif) {
    this.refinePotentialModificationMotif = refinePotentialModificationMotif;
  }

  /**
	* 	**/
  public Boolean refinePointMutations;

  /**
   * Retreives the value of refinePointMutations attribute
   * 
   * @return refinePointMutations
   **/

  public Boolean getRefinePointMutations() {
    return refinePointMutations;
  }

  /**
   * Sets the value of refinePointMutations attribue
   **/

  public void setRefinePointMutations(final Boolean refinePointMutations) {
    this.refinePointMutations = refinePointMutations;
  }

  /**
	* 	**/
  public String refinePotentialNTerminusModifications;

  /**
   * Retreives the value of refinePotentialNTerminusModifications attribute
   * 
   * @return refinePotentialNTerminusModifications
   **/

  public String getRefinePotentialNTerminusModifications() {
    return refinePotentialNTerminusModifications;
  }

  /**
   * Sets the value of refinePotentialNTerminusModifications attribue
   **/

  public void setRefinePotentialNTerminusModifications(final String refinePotentialNTerminusModifications) {
    this.refinePotentialNTerminusModifications = refinePotentialNTerminusModifications;
  }

  /**
	* 	**/
  public String refinePotentialCTerminusModifications;

  /**
   * Retreives the value of refinePotentialCTerminusModifications attribute
   * 
   * @return refinePotentialCTerminusModifications
   **/

  public String getRefinePotentialCTerminusModifications() {
    return refinePotentialCTerminusModifications;
  }

  /**
   * Sets the value of refinePotentialCTerminusModifications attribue
   **/

  public void setRefinePotentialCTerminusModifications(final String refinePotentialCTerminusModifications) {
    this.refinePotentialCTerminusModifications = refinePotentialCTerminusModifications;
  }

  /**
	* 	**/
  public Boolean refineSingleAminoAcidPolymorphisms;

  /**
   * Retreives the value of refineSingleAminoAcidPolymorphisms attribute
   * 
   * @return refineSingleAminoAcidPolymorphisms
   **/

  public Boolean getRefineSingleAminoAcidPolymorphisms() {
    return refineSingleAminoAcidPolymorphisms;
  }

  /**
   * Sets the value of refineSingleAminoAcidPolymorphisms attribue
   **/

  public void setRefineSingleAminoAcidPolymorphisms(final Boolean refineSingleAminoAcidPolymorphisms) {
    this.refineSingleAminoAcidPolymorphisms = refineSingleAminoAcidPolymorphisms;
  }

  /**
	* 	**/
  public Boolean refineSpectrumSynthesis;

  /**
   * Retreives the value of refineSpectrumSynthesis attribute
   * 
   * @return refineSpectrumSynthesis
   **/

  public Boolean getRefineSpectrumSynthesis() {
    return refineSpectrumSynthesis;
  }

  /**
   * Sets the value of refineSpectrumSynthesis attribue
   **/

  public void setRefineSpectrumSynthesis(final Boolean refineSpectrumSynthesis) {
    this.refineSpectrumSynthesis = refineSpectrumSynthesis;
  }

  /**
	* 	**/
  public Boolean refineUnanticipatedCleavage;

  /**
   * Retreives the value of refineUnanticipatedCleavage attribute
   * 
   * @return refineUnanticipatedCleavage
   **/

  public Boolean getRefineUnanticipatedCleavage() {
    return refineUnanticipatedCleavage;
  }

  /**
   * Sets the value of refineUnanticipatedCleavage attribue
   **/

  public void setRefineUnanticipatedCleavage(final Boolean refineUnanticipatedCleavage) {
    this.refineUnanticipatedCleavage = refineUnanticipatedCleavage;
  }

  /**
	* 	**/
  public Boolean refineUsePotentialModifications;

  /**
   * Retreives the value of refineUsePotentialModifications attribute
   * 
   * @return refineUsePotentialModifications
   **/

  public Boolean getRefineUsePotentialModifications() {
    return refineUsePotentialModifications;
  }

  /**
   * Sets the value of refineUsePotentialModifications attribue
   **/

  public void setRefineUsePotentialModifications(final Boolean refineUsePotentialModifications) {
    this.refineUsePotentialModifications = refineUsePotentialModifications;
  }

  /**
	* 	**/
  public Integer spectrumMaximumParentCharge;

  /**
   * Retreives the value of spectrumMaximumParentCharge attribute
   * 
   * @return spectrumMaximumParentCharge
   **/

  public Integer getSpectrumMaximumParentCharge() {
    return spectrumMaximumParentCharge;
  }

  /**
   * Sets the value of spectrumMaximumParentCharge attribue
   **/

  public void setSpectrumMaximumParentCharge(final Integer spectrumMaximumParentCharge) {
    this.spectrumMaximumParentCharge = spectrumMaximumParentCharge;
  }

  /**
   * Compares <code>obj</code> to it self and returns true if they both are same
   * 
   * @param obj
   **/
  @Override
  public boolean equals(final Object obj) {
    if(obj instanceof XTandemParameters) {
      final XTandemParameters c = (XTandemParameters) obj;
      if(getId() != null && getId().equals(c.getId())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns hash code for the primary key of the object
   **/
  @Override
  public int hashCode() {
    if(getId() != null) {
      return getId().hashCode();
    }
    return 0;
  }

}