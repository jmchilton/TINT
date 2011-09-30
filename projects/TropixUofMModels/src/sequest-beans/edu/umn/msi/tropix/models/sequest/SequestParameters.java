package edu.umn.msi.tropix.models.sequest;

import java.io.Serializable;

/**
	* 	**/
public class SequestParameters implements Serializable {
  /**
   * An attribute to allow serialization of the domain objects
   */
  private static final long serialVersionUID = 1234567890L;

  /**
	* 	**/
  public String enzymeName;

  /**
   * Retreives the value of enzymeName attribute
   * 
   * @return enzymeName
   **/

  public String getEnzymeName() {
    return enzymeName;
  }

  /**
   * Sets the value of enzymeName attribue
   **/

  public void setEnzymeName(final String enzymeName) {
    this.enzymeName = enzymeName;
  }

  /**
	* 	**/
  public String enzymeLimit;

  /**
   * Retreives the value of enzymeLimit attribute
   * 
   * @return enzymeLimit
   **/

  public String getEnzymeLimit() {
    return enzymeLimit;
  }

  /**
   * Sets the value of enzymeLimit attribue
   **/

  public void setEnzymeLimit(final String enzymeLimit) {
    this.enzymeLimit = enzymeLimit;
  }

  /**
	* 	**/
  public Boolean enzymeCTerm;

  /**
   * Retreives the value of enzymeCTerm attribute
   * 
   * @return enzymeCTerm
   **/

  public Boolean getEnzymeCTerm() {
    return enzymeCTerm;
  }

  /**
   * Sets the value of enzymeCTerm attribue
   **/

  public void setEnzymeCTerm(final Boolean enzymeCTerm) {
    this.enzymeCTerm = enzymeCTerm;
  }

  /**
	* 	**/
  public String enzymeCleave;

  /**
   * Retreives the value of enzymeCleave attribute
   * 
   * @return enzymeCleave
   **/

  public String getEnzymeCleave() {
    return enzymeCleave;
  }

  /**
   * Sets the value of enzymeCleave attribue
   **/

  public void setEnzymeCleave(final String enzymeCleave) {
    this.enzymeCleave = enzymeCleave;
  }

  /**
	* 	**/
  public String enzymeNoCleave;

  /**
   * Retreives the value of enzymeNoCleave attribute
   * 
   * @return enzymeNoCleave
   **/

  public String getEnzymeNoCleave() {
    return enzymeNoCleave;
  }

  /**
   * Sets the value of enzymeNoCleave attribue
   **/

  public void setEnzymeNoCleave(final String enzymeNoCleave) {
    this.enzymeNoCleave = enzymeNoCleave;
  }

  /**
	* 	**/
  public Double peptideMassTolerance;

  /**
   * Retreives the value of peptideMassTolerance attribute
   * 
   * @return peptideMassTolerance
   **/

  public Double getPeptideMassTolerance() {
    return peptideMassTolerance;
  }

  /**
   * Sets the value of peptideMassTolerance attribue
   **/

  public void setPeptideMassTolerance(final Double peptideMassTolerance) {
    this.peptideMassTolerance = peptideMassTolerance;
  }

  /**
	* 	**/
  public String peptideMassUnits;

  /**
   * Retreives the value of peptideMassUnits attribute
   * 
   * @return peptideMassUnits
   **/

  public String getPeptideMassUnits() {
    return peptideMassUnits;
  }

  /**
   * Sets the value of peptideMassUnits attribue
   **/

  public void setPeptideMassUnits(final String peptideMassUnits) {
    this.peptideMassUnits = peptideMassUnits;
  }

  /**
	* 	**/
  public Double fragmentMassTolerance;

  /**
   * Retreives the value of fragmentMassTolerance attribute
   * 
   * @return fragmentMassTolerance
   **/

  public Double getFragmentMassTolerance() {
    return fragmentMassTolerance;
  }

  /**
   * Sets the value of fragmentMassTolerance attribue
   **/

  public void setFragmentMassTolerance(final Double fragmentMassTolerance) {
    this.fragmentMassTolerance = fragmentMassTolerance;
  }

  /**
	* 	**/
  public String massTypeFragment;

  /**
   * Retreives the value of massTypeFragment attribute
   * 
   * @return massTypeFragment
   **/

  public String getMassTypeFragment() {
    return massTypeFragment;
  }

  /**
   * Sets the value of massTypeFragment attribue
   **/

  public void setMassTypeFragment(final String massTypeFragment) {
    this.massTypeFragment = massTypeFragment;
  }

  /**
	* 	**/
  public Integer internalCleavageSites;

  /**
   * Retreives the value of internalCleavageSites attribute
   * 
   * @return internalCleavageSites
   **/

  public Integer getInternalCleavageSites() {
    return internalCleavageSites;
  }

  /**
   * Sets the value of internalCleavageSites attribue
   **/

  public void setInternalCleavageSites(final Integer internalCleavageSites) {
    this.internalCleavageSites = internalCleavageSites;
  }

  /**
	* 	**/
  public Double addG;

  /**
   * Retreives the value of addG attribute
   * 
   * @return addG
   **/

  public Double getAddG() {
    return addG;
  }

  /**
   * Sets the value of addG attribue
   **/

  public void setAddG(final Double addG) {
    this.addG = addG;
  }

  /**
	* 	**/
  public Double addS;

  /**
   * Retreives the value of addS attribute
   * 
   * @return addS
   **/

  public Double getAddS() {
    return addS;
  }

  /**
   * Sets the value of addS attribue
   **/

  public void setAddS(final Double addS) {
    this.addS = addS;
  }

  /**
	* 	**/
  public Double addP;

  /**
   * Retreives the value of addP attribute
   * 
   * @return addP
   **/

  public Double getAddP() {
    return addP;
  }

  /**
   * Sets the value of addP attribue
   **/

  public void setAddP(final Double addP) {
    this.addP = addP;
  }

  /**
	* 	**/
  public Double addV;

  /**
   * Retreives the value of addV attribute
   * 
   * @return addV
   **/

  public Double getAddV() {
    return addV;
  }

  /**
   * Sets the value of addV attribue
   **/

  public void setAddV(final Double addV) {
    this.addV = addV;
  }

  /**
	* 	**/
  public Double addT;

  /**
   * Retreives the value of addT attribute
   * 
   * @return addT
   **/

  public Double getAddT() {
    return addT;
  }

  /**
   * Sets the value of addT attribue
   **/

  public void setAddT(final Double addT) {
    this.addT = addT;
  }

  /**
	* 	**/
  public Double addC;

  /**
   * Retreives the value of addC attribute
   * 
   * @return addC
   **/

  public Double getAddC() {
    return addC;
  }

  /**
   * Sets the value of addC attribue
   **/

  public void setAddC(final Double addC) {
    this.addC = addC;
  }

  /**
	* 	**/
  public Double addL;

  /**
   * Retreives the value of addL attribute
   * 
   * @return addL
   **/

  public Double getAddL() {
    return addL;
  }

  /**
   * Sets the value of addL attribue
   **/

  public void setAddL(final Double addL) {
    this.addL = addL;
  }

  /**
	* 	**/
  public Double addI;

  /**
   * Retreives the value of addI attribute
   * 
   * @return addI
   **/

  public Double getAddI() {
    return addI;
  }

  /**
   * Sets the value of addI attribue
   **/

  public void setAddI(final Double addI) {
    this.addI = addI;
  }

  /**
	* 	**/
  public Double addX;

  /**
   * Retreives the value of addX attribute
   * 
   * @return addX
   **/

  public Double getAddX() {
    return addX;
  }

  /**
   * Sets the value of addX attribue
   **/

  public void setAddX(final Double addX) {
    this.addX = addX;
  }

  /**
	* 	**/
  public Double addN;

  /**
   * Retreives the value of addN attribute
   * 
   * @return addN
   **/

  public Double getAddN() {
    return addN;
  }

  /**
   * Sets the value of addN attribue
   **/

  public void setAddN(final Double addN) {
    this.addN = addN;
  }

  /**
	* 	**/
  public Double addO;

  /**
   * Retreives the value of addO attribute
   * 
   * @return addO
   **/

  public Double getAddO() {
    return addO;
  }

  /**
   * Sets the value of addO attribue
   **/

  public void setAddO(final Double addO) {
    this.addO = addO;
  }

  /**
	* 	**/
  public Double addB;

  /**
   * Retreives the value of addB attribute
   * 
   * @return addB
   **/

  public Double getAddB() {
    return addB;
  }

  /**
   * Sets the value of addB attribue
   **/

  public void setAddB(final Double addB) {
    this.addB = addB;
  }

  /**
	* 	**/
  public Double addD;

  /**
   * Retreives the value of addD attribute
   * 
   * @return addD
   **/

  public Double getAddD() {
    return addD;
  }

  /**
   * Sets the value of addD attribue
   **/

  public void setAddD(final Double addD) {
    this.addD = addD;
  }

  /**
	* 	**/
  public Double addQ;

  /**
   * Retreives the value of addQ attribute
   * 
   * @return addQ
   **/

  public Double getAddQ() {
    return addQ;
  }

  /**
   * Sets the value of addQ attribue
   **/

  public void setAddQ(final Double addQ) {
    this.addQ = addQ;
  }

  /**
	* 	**/
  public Double addK;

  /**
   * Retreives the value of addK attribute
   * 
   * @return addK
   **/

  public Double getAddK() {
    return addK;
  }

  /**
   * Sets the value of addK attribue
   **/

  public void setAddK(final Double addK) {
    this.addK = addK;
  }

  /**
	* 	**/
  public Double addZ;

  /**
   * Retreives the value of addZ attribute
   * 
   * @return addZ
   **/

  public Double getAddZ() {
    return addZ;
  }

  /**
   * Sets the value of addZ attribue
   **/

  public void setAddZ(final Double addZ) {
    this.addZ = addZ;
  }

  /**
	* 	**/
  public Double addE;

  /**
   * Retreives the value of addE attribute
   * 
   * @return addE
   **/

  public Double getAddE() {
    return addE;
  }

  /**
   * Sets the value of addE attribue
   **/

  public void setAddE(final Double addE) {
    this.addE = addE;
  }

  /**
	* 	**/
  public Double addM;

  /**
   * Retreives the value of addM attribute
   * 
   * @return addM
   **/

  public Double getAddM() {
    return addM;
  }

  /**
   * Sets the value of addM attribue
   **/

  public void setAddM(final Double addM) {
    this.addM = addM;
  }

  /**
	* 	**/
  public Double addH;

  /**
   * Retreives the value of addH attribute
   * 
   * @return addH
   **/

  public Double getAddH() {
    return addH;
  }

  /**
   * Sets the value of addH attribue
   **/

  public void setAddH(final Double addH) {
    this.addH = addH;
  }

  /**
	* 	**/
  public Double addF;

  /**
   * Retreives the value of addF attribute
   * 
   * @return addF
   **/

  public Double getAddF() {
    return addF;
  }

  /**
   * Sets the value of addF attribue
   **/

  public void setAddF(final Double addF) {
    this.addF = addF;
  }

  /**
	* 	**/
  public Double addR;

  /**
   * Retreives the value of addR attribute
   * 
   * @return addR
   **/

  public Double getAddR() {
    return addR;
  }

  /**
   * Sets the value of addR attribue
   **/

  public void setAddR(final Double addR) {
    this.addR = addR;
  }

  /**
	* 	**/
  public Double addY;

  /**
   * Retreives the value of addY attribute
   * 
   * @return addY
   **/

  public Double getAddY() {
    return addY;
  }

  /**
   * Sets the value of addY attribue
   **/

  public void setAddY(final Double addY) {
    this.addY = addY;
  }

  /**
	* 	**/
  public Double addW;

  /**
   * Retreives the value of addW attribute
   * 
   * @return addW
   **/

  public Double getAddW() {
    return addW;
  }

  /**
   * Sets the value of addW attribue
   **/

  public void setAddW(final Double addW) {
    this.addW = addW;
  }

  /**
	* 	**/
  public Double addCTermPeptide;

  /**
   * Retreives the value of addCTermPeptide attribute
   * 
   * @return addCTermPeptide
   **/

  public Double getAddCTermPeptide() {
    return addCTermPeptide;
  }

  /**
   * Sets the value of addCTermPeptide attribue
   **/

  public void setAddCTermPeptide(final Double addCTermPeptide) {
    this.addCTermPeptide = addCTermPeptide;
  }

  /**
	* 	**/
  public Double addNTermPeptide;

  /**
   * Retreives the value of addNTermPeptide attribute
   * 
   * @return addNTermPeptide
   **/

  public Double getAddNTermPeptide() {
    return addNTermPeptide;
  }

  /**
   * Sets the value of addNTermPeptide attribue
   **/

  public void setAddNTermPeptide(final Double addNTermPeptide) {
    this.addNTermPeptide = addNTermPeptide;
  }

  /**
	* 	**/
  public Double addCTermProtein;

  /**
   * Retreives the value of addCTermProtein attribute
   * 
   * @return addCTermProtein
   **/

  public Double getAddCTermProtein() {
    return addCTermProtein;
  }

  /**
   * Sets the value of addCTermProtein attribue
   **/

  public void setAddCTermProtein(final Double addCTermProtein) {
    this.addCTermProtein = addCTermProtein;
  }

  /**
	* 	**/
  public Double addNTermProtein;

  /**
   * Retreives the value of addNTermProtein attribute
   * 
   * @return addNTermProtein
   **/

  public Double getAddNTermProtein() {
    return addNTermProtein;
  }

  /**
   * Sets the value of addNTermProtein attribue
   **/

  public void setAddNTermProtein(final Double addNTermProtein) {
    this.addNTermProtein = addNTermProtein;
  }

  /**
	* 	**/
  public String diffSearch1Residue;

  /**
   * Retreives the value of diffSearch1Residue attribute
   * 
   * @return diffSearch1Residue
   **/

  public String getDiffSearch1Residue() {
    return diffSearch1Residue;
  }

  /**
   * Sets the value of diffSearch1Residue attribue
   **/

  public void setDiffSearch1Residue(final String diffSearch1Residue) {
    this.diffSearch1Residue = diffSearch1Residue;
  }

  /**
	* 	**/
  public String diffSearch2Residue;

  /**
   * Retreives the value of diffSearch2Residue attribute
   * 
   * @return diffSearch2Residue
   **/

  public String getDiffSearch2Residue() {
    return diffSearch2Residue;
  }

  /**
   * Sets the value of diffSearch2Residue attribue
   **/

  public void setDiffSearch2Residue(final String diffSearch2Residue) {
    this.diffSearch2Residue = diffSearch2Residue;
  }

  /**
	* 	**/
  public String diffSearch3Residue;

  /**
   * Retreives the value of diffSearch3Residue attribute
   * 
   * @return diffSearch3Residue
   **/

  public String getDiffSearch3Residue() {
    return diffSearch3Residue;
  }

  /**
   * Sets the value of diffSearch3Residue attribue
   **/

  public void setDiffSearch3Residue(final String diffSearch3Residue) {
    this.diffSearch3Residue = diffSearch3Residue;
  }

  /**
	* 	**/
  public String diffSearch4Residue;

  /**
   * Retreives the value of diffSearch4Residue attribute
   * 
   * @return diffSearch4Residue
   **/

  public String getDiffSearch4Residue() {
    return diffSearch4Residue;
  }

  /**
   * Sets the value of diffSearch4Residue attribue
   **/

  public void setDiffSearch4Residue(final String diffSearch4Residue) {
    this.diffSearch4Residue = diffSearch4Residue;
  }

  /**
	* 	**/
  public String diffSearch5Residue;

  /**
   * Retreives the value of diffSearch5Residue attribute
   * 
   * @return diffSearch5Residue
   **/

  public String getDiffSearch5Residue() {
    return diffSearch5Residue;
  }

  /**
   * Sets the value of diffSearch5Residue attribue
   **/

  public void setDiffSearch5Residue(final String diffSearch5Residue) {
    this.diffSearch5Residue = diffSearch5Residue;
  }

  /**
	* 	**/
  public String diffSearch6Residue;

  /**
   * Retreives the value of diffSearch6Residue attribute
   * 
   * @return diffSearch6Residue
   **/

  public String getDiffSearch6Residue() {
    return diffSearch6Residue;
  }

  /**
   * Sets the value of diffSearch6Residue attribue
   **/

  public void setDiffSearch6Residue(final String diffSearch6Residue) {
    this.diffSearch6Residue = diffSearch6Residue;
  }

  /**
	* 	**/
  public Double diffSearch1Value;

  /**
   * Retreives the value of diffSearch1Value attribute
   * 
   * @return diffSearch1Value
   **/

  public Double getDiffSearch1Value() {
    return diffSearch1Value;
  }

  /**
   * Sets the value of diffSearch1Value attribue
   **/

  public void setDiffSearch1Value(final Double diffSearch1Value) {
    this.diffSearch1Value = diffSearch1Value;
  }

  /**
	* 	**/
  public Double diffSearch2Value;

  /**
   * Retreives the value of diffSearch2Value attribute
   * 
   * @return diffSearch2Value
   **/

  public Double getDiffSearch2Value() {
    return diffSearch2Value;
  }

  /**
   * Sets the value of diffSearch2Value attribue
   **/

  public void setDiffSearch2Value(final Double diffSearch2Value) {
    this.diffSearch2Value = diffSearch2Value;
  }

  /**
	* 	**/
  public Double diffSearch3Value;

  /**
   * Retreives the value of diffSearch3Value attribute
   * 
   * @return diffSearch3Value
   **/

  public Double getDiffSearch3Value() {
    return diffSearch3Value;
  }

  /**
   * Sets the value of diffSearch3Value attribue
   **/

  public void setDiffSearch3Value(final Double diffSearch3Value) {
    this.diffSearch3Value = diffSearch3Value;
  }

  /**
	* 	**/
  public Double diffSearch4Value;

  /**
   * Retreives the value of diffSearch4Value attribute
   * 
   * @return diffSearch4Value
   **/

  public Double getDiffSearch4Value() {
    return diffSearch4Value;
  }

  /**
   * Sets the value of diffSearch4Value attribue
   **/

  public void setDiffSearch4Value(final Double diffSearch4Value) {
    this.diffSearch4Value = diffSearch4Value;
  }

  /**
	* 	**/
  public Double diffSearch5Value;

  /**
   * Retreives the value of diffSearch5Value attribute
   * 
   * @return diffSearch5Value
   **/

  public Double getDiffSearch5Value() {
    return diffSearch5Value;
  }

  /**
   * Sets the value of diffSearch5Value attribue
   **/

  public void setDiffSearch5Value(final Double diffSearch5Value) {
    this.diffSearch5Value = diffSearch5Value;
  }

  /**
	* 	**/
  public Double diffSearch6Value;

  /**
   * Retreives the value of diffSearch6Value attribute
   * 
   * @return diffSearch6Value
   **/

  public Double getDiffSearch6Value() {
    return diffSearch6Value;
  }

  /**
   * Sets the value of diffSearch6Value attribue
   **/

  public void setDiffSearch6Value(final Double diffSearch6Value) {
    this.diffSearch6Value = diffSearch6Value;
  }

  /**
	* 	**/
  public Integer numOutputLines;

  /**
   * Retreives the value of numOutputLines attribute
   * 
   * @return numOutputLines
   **/

  public Integer getNumOutputLines() {
    return numOutputLines;
  }

  /**
   * Sets the value of numOutputLines attribue
   **/

  public void setNumOutputLines(final Integer numOutputLines) {
    this.numOutputLines = numOutputLines;
  }

  /**
	* 	**/
  public Integer numDescriptionLines;

  /**
   * Retreives the value of numDescriptionLines attribute
   * 
   * @return numDescriptionLines
   **/

  public Integer getNumDescriptionLines() {
    return numDescriptionLines;
  }

  /**
   * Sets the value of numDescriptionLines attribue
   **/

  public void setNumDescriptionLines(final Integer numDescriptionLines) {
    this.numDescriptionLines = numDescriptionLines;
  }

  /**
	* 	**/
  public Integer numResults;

  /**
   * Retreives the value of numResults attribute
   * 
   * @return numResults
   **/

  public Integer getNumResults() {
    return numResults;
  }

  /**
   * Sets the value of numResults attribue
   **/

  public void setNumResults(final Integer numResults) {
    this.numResults = numResults;
  }

  /**
	* 	**/
  public Boolean showFragmentIons;

  /**
   * Retreives the value of showFragmentIons attribute
   * 
   * @return showFragmentIons
   **/

  public Boolean getShowFragmentIons() {
    return showFragmentIons;
  }

  /**
   * Sets the value of showFragmentIons attribue
   **/

  public void setShowFragmentIons(final Boolean showFragmentIons) {
    this.showFragmentIons = showFragmentIons;
  }

  /**
	* 	**/
  public Integer maxNumDifferentialsPerPeptide;

  /**
   * Retreives the value of maxNumDifferentialsPerPeptide attribute
   * 
   * @return maxNumDifferentialsPerPeptide
   **/

  public Integer getMaxNumDifferentialsPerPeptide() {
    return maxNumDifferentialsPerPeptide;
  }

  /**
   * Sets the value of maxNumDifferentialsPerPeptide attribue
   **/

  public void setMaxNumDifferentialsPerPeptide(final Integer maxNumDifferentialsPerPeptide) {
    this.maxNumDifferentialsPerPeptide = maxNumDifferentialsPerPeptide;
  }

  /**
	* 	**/
  public Boolean normalizeXCorr;

  /**
   * Retreives the value of normalizeXCorr attribute
   * 
   * @return normalizeXCorr
   **/

  public Boolean getNormalizeXCorr() {
    return normalizeXCorr;
  }

  /**
   * Sets the value of normalizeXCorr attribue
   **/

  public void setNormalizeXCorr(final Boolean normalizeXCorr) {
    this.normalizeXCorr = normalizeXCorr;
  }

  /**
	* 	**/
  public Boolean removePrecursorPeak;

  /**
   * Retreives the value of removePrecursorPeak attribute
   * 
   * @return removePrecursorPeak
   **/

  public Boolean getRemovePrecursorPeak() {
    return removePrecursorPeak;
  }

  /**
   * Sets the value of removePrecursorPeak attribue
   **/

  public void setRemovePrecursorPeak(final Boolean removePrecursorPeak) {
    this.removePrecursorPeak = removePrecursorPeak;
  }

  /**
	* 	**/
  public Double ionCutoffPercentage;

  /**
   * Retreives the value of ionCutoffPercentage attribute
   * 
   * @return ionCutoffPercentage
   **/

  public Double getIonCutoffPercentage() {
    return ionCutoffPercentage;
  }

  /**
   * Sets the value of ionCutoffPercentage attribue
   **/

  public void setIonCutoffPercentage(final Double ionCutoffPercentage) {
    this.ionCutoffPercentage = ionCutoffPercentage;
  }

  /**
	* 	**/
  public Integer matchPeakCount;

  /**
   * Retreives the value of matchPeakCount attribute
   * 
   * @return matchPeakCount
   **/

  public Integer getMatchPeakCount() {
    return matchPeakCount;
  }

  /**
   * Sets the value of matchPeakCount attribue
   **/

  public void setMatchPeakCount(final Integer matchPeakCount) {
    this.matchPeakCount = matchPeakCount;
  }

  /**
	* 	**/
  public Integer matchPeakAllowedError;

  /**
   * Retreives the value of matchPeakAllowedError attribute
   * 
   * @return matchPeakAllowedError
   **/

  public Integer getMatchPeakAllowedError() {
    return matchPeakAllowedError;
  }

  /**
   * Sets the value of matchPeakAllowedError attribue
   **/

  public void setMatchPeakAllowedError(final Integer matchPeakAllowedError) {
    this.matchPeakAllowedError = matchPeakAllowedError;
  }

  /**
	* 	**/
  public Double matchPeakTolerance;

  /**
   * Retreives the value of matchPeakTolerance attribute
   * 
   * @return matchPeakTolerance
   **/

  public Double getMatchPeakTolerance() {
    return matchPeakTolerance;
  }

  /**
   * Sets the value of matchPeakTolerance attribue
   **/

  public void setMatchPeakTolerance(final Double matchPeakTolerance) {
    this.matchPeakTolerance = matchPeakTolerance;
  }

  /**
	* 	**/
  public String massTypeParent;

  /**
   * Retreives the value of massTypeParent attribute
   * 
   * @return massTypeParent
   **/

  public String getMassTypeParent() {
    return massTypeParent;
  }

  /**
   * Sets the value of massTypeParent attribue
   **/

  public void setMassTypeParent(final String massTypeParent) {
    this.massTypeParent = massTypeParent;
  }

  /**
	* 	**/
  public Double digestMassRangeLower;

  /**
   * Retreives the value of digestMassRangeLower attribute
   * 
   * @return digestMassRangeLower
   **/

  public Double getDigestMassRangeLower() {
    return digestMassRangeLower;
  }

  /**
   * Sets the value of digestMassRangeLower attribue
   **/

  public void setDigestMassRangeLower(final Double digestMassRangeLower) {
    this.digestMassRangeLower = digestMassRangeLower;
  }

  /**
	* 	**/
  public Double digestMassRangeUpper;

  /**
   * Retreives the value of digestMassRangeUpper attribute
   * 
   * @return digestMassRangeUpper
   **/

  public Double getDigestMassRangeUpper() {
    return digestMassRangeUpper;
  }

  /**
   * Sets the value of digestMassRangeUpper attribue
   **/

  public void setDigestMassRangeUpper(final Double digestMassRangeUpper) {
    this.digestMassRangeUpper = digestMassRangeUpper;
  }

  /**
	* 	**/
  public Integer proteinMassFilterLower;

  /**
   * Retreives the value of proteinMassFilterLower attribute
   * 
   * @return proteinMassFilterLower
   **/

  public Integer getProteinMassFilterLower() {
    return proteinMassFilterLower;
  }

  /**
   * Sets the value of proteinMassFilterLower attribue
   **/

  public void setProteinMassFilterLower(final Integer proteinMassFilterLower) {
    this.proteinMassFilterLower = proteinMassFilterLower;
  }

  /**
	* 	**/
  public Integer proteinMassFilterUpper;

  /**
   * Retreives the value of proteinMassFilterUpper attribute
   * 
   * @return proteinMassFilterUpper
   **/

  public Integer getProteinMassFilterUpper() {
    return proteinMassFilterUpper;
  }

  /**
   * Sets the value of proteinMassFilterUpper attribue
   **/

  public void setProteinMassFilterUpper(final Integer proteinMassFilterUpper) {
    this.proteinMassFilterUpper = proteinMassFilterUpper;
  }

  /**
	* 	**/
  public Integer printDuplicateReferences;

  /**
   * Retreives the value of printDuplicateReferences attribute
   * 
   * @return printDuplicateReferences
   **/

  public Integer getPrintDuplicateReferences() {
    return printDuplicateReferences;
  }

  /**
   * Sets the value of printDuplicateReferences attribue
   **/

  public void setPrintDuplicateReferences(final Integer printDuplicateReferences) {
    this.printDuplicateReferences = printDuplicateReferences;
  }

  /**
	* 	**/
  public Boolean ionSeriesNA;

  /**
   * Retreives the value of ionSeriesNA attribute
   * 
   * @return ionSeriesNA
   **/

  public Boolean getIonSeriesNA() {
    return ionSeriesNA;
  }

  /**
   * Sets the value of ionSeriesNA attribue
   **/

  public void setIonSeriesNA(final Boolean ionSeriesNA) {
    this.ionSeriesNA = ionSeriesNA;
  }

  /**
	* 	**/
  public Boolean ionSeriesNB;

  /**
   * Retreives the value of ionSeriesNB attribute
   * 
   * @return ionSeriesNB
   **/

  public Boolean getIonSeriesNB() {
    return ionSeriesNB;
  }

  /**
   * Sets the value of ionSeriesNB attribue
   **/

  public void setIonSeriesNB(final Boolean ionSeriesNB) {
    this.ionSeriesNB = ionSeriesNB;
  }

  /**
	* 	**/
  public Double ionSeriesA;

  /**
   * Retreives the value of ionSeriesA attribute
   * 
   * @return ionSeriesA
   **/

  public Double getIonSeriesA() {
    return ionSeriesA;
  }

  /**
   * Sets the value of ionSeriesA attribue
   **/

  public void setIonSeriesA(final Double ionSeriesA) {
    this.ionSeriesA = ionSeriesA;
  }

  /**
	* 	**/
  public Double ionSeriesB;

  /**
   * Retreives the value of ionSeriesB attribute
   * 
   * @return ionSeriesB
   **/

  public Double getIonSeriesB() {
    return ionSeriesB;
  }

  /**
   * Sets the value of ionSeriesB attribue
   **/

  public void setIonSeriesB(final Double ionSeriesB) {
    this.ionSeriesB = ionSeriesB;
  }

  /**
	* 	**/
  public Double ionSeriesC;

  /**
   * Retreives the value of ionSeriesC attribute
   * 
   * @return ionSeriesC
   **/

  public Double getIonSeriesC() {
    return ionSeriesC;
  }

  /**
   * Sets the value of ionSeriesC attribue
   **/

  public void setIonSeriesC(final Double ionSeriesC) {
    this.ionSeriesC = ionSeriesC;
  }

  /**
	* 	**/
  public Double ionSeriesD;

  /**
   * Retreives the value of ionSeriesD attribute
   * 
   * @return ionSeriesD
   **/

  public Double getIonSeriesD() {
    return ionSeriesD;
  }

  /**
   * Sets the value of ionSeriesD attribue
   **/

  public void setIonSeriesD(final Double ionSeriesD) {
    this.ionSeriesD = ionSeriesD;
  }

  /**
	* 	**/
  public Double ionSeriesV;

  /**
   * Retreives the value of ionSeriesV attribute
   * 
   * @return ionSeriesV
   **/

  public Double getIonSeriesV() {
    return ionSeriesV;
  }

  /**
   * Sets the value of ionSeriesV attribue
   **/

  public void setIonSeriesV(final Double ionSeriesV) {
    this.ionSeriesV = ionSeriesV;
  }

  /**
	* 	**/
  public Double ionSeriesW;

  /**
   * Retreives the value of ionSeriesW attribute
   * 
   * @return ionSeriesW
   **/

  public Double getIonSeriesW() {
    return ionSeriesW;
  }

  /**
   * Sets the value of ionSeriesW attribue
   **/

  public void setIonSeriesW(final Double ionSeriesW) {
    this.ionSeriesW = ionSeriesW;
  }

  /**
	* 	**/
  public Double ionSeriesX;

  /**
   * Retreives the value of ionSeriesX attribute
   * 
   * @return ionSeriesX
   **/

  public Double getIonSeriesX() {
    return ionSeriesX;
  }

  /**
   * Sets the value of ionSeriesX attribue
   **/

  public void setIonSeriesX(final Double ionSeriesX) {
    this.ionSeriesX = ionSeriesX;
  }

  /**
	* 	**/
  public Double ionSeriesY;

  /**
   * Retreives the value of ionSeriesY attribute
   * 
   * @return ionSeriesY
   **/

  public Double getIonSeriesY() {
    return ionSeriesY;
  }

  /**
   * Sets the value of ionSeriesY attribue
   **/

  public void setIonSeriesY(final Double ionSeriesY) {
    this.ionSeriesY = ionSeriesY;
  }

  /**
	* 	**/
  public Double ionSeriesZ;

  /**
   * Retreives the value of ionSeriesZ attribute
   * 
   * @return ionSeriesZ
   **/

  public Double getIonSeriesZ() {
    return ionSeriesZ;
  }

  /**
   * Sets the value of ionSeriesZ attribue
   **/

  public void setIonSeriesZ(final Double ionSeriesZ) {
    this.ionSeriesZ = ionSeriesZ;
  }

  /**
	* 	**/
  public Double diffSearchN;

  /**
   * Retreives the value of diffSearchN attribute
   * 
   * @return diffSearchN
   **/

  public Double getDiffSearchN() {
    return diffSearchN;
  }

  /**
   * Sets the value of diffSearchN attribue
   **/

  public void setDiffSearchN(final Double diffSearchN) {
    this.diffSearchN = diffSearchN;
  }

  /**
	* 	**/
  public Double diffSearchC;

  /**
   * Retreives the value of diffSearchC attribute
   * 
   * @return diffSearchC
   **/

  public Double getDiffSearchC() {
    return diffSearchC;
  }

  /**
   * Sets the value of diffSearchC attribue
   **/

  public void setDiffSearchC(final Double diffSearchC) {
    this.diffSearchC = diffSearchC;
  }

  /**
	* 	**/
  public Boolean ionSeriesNY;

  /**
   * Retreives the value of ionSeriesNY attribute
   * 
   * @return ionSeriesNY
   **/

  public Boolean getIonSeriesNY() {
    return ionSeriesNY;
  }

  /**
   * Sets the value of ionSeriesNY attribue
   **/

  public void setIonSeriesNY(final Boolean ionSeriesNY) {
    this.ionSeriesNY = ionSeriesNY;
  }

  /**
	* 	**/
  public String secondaryDatabaseId;

  /**
   * Retreives the value of secondaryDatabaseId attribute
   * 
   * @return secondaryDatabaseId
   **/

  public String getSecondaryDatabaseId() {
    return secondaryDatabaseId;
  }

  /**
   * Sets the value of secondaryDatabaseId attribue
   **/

  public void setSecondaryDatabaseId(final String secondaryDatabaseId) {
    this.secondaryDatabaseId = secondaryDatabaseId;
  }

  /**
	* 	**/
  public Integer nucleotideReadingFrame;

  /**
   * Retreives the value of nucleotideReadingFrame attribute
   * 
   * @return nucleotideReadingFrame
   **/

  public Integer getNucleotideReadingFrame() {
    return nucleotideReadingFrame;
  }

  /**
   * Sets the value of nucleotideReadingFrame attribue
   **/

  public void setNucleotideReadingFrame(final Integer nucleotideReadingFrame) {
    this.nucleotideReadingFrame = nucleotideReadingFrame;
  }

  /**
	* 	**/
  public String partialSequence;

  /**
   * Retreives the value of partialSequence attribute
   * 
   * @return partialSequence
   **/

  public String getPartialSequence() {
    return partialSequence;
  }

  /**
   * Sets the value of partialSequence attribue
   **/

  public void setPartialSequence(final String partialSequence) {
    this.partialSequence = partialSequence;
  }

  /**
	* 	**/
  public String sequenceHeaderFilter;

  /**
   * Retreives the value of sequenceHeaderFilter attribute
   * 
   * @return sequenceHeaderFilter
   **/

  public String getSequenceHeaderFilter() {
    return sequenceHeaderFilter;
  }

  /**
   * Sets the value of sequenceHeaderFilter attribue
   **/

  public void setSequenceHeaderFilter(final String sequenceHeaderFilter) {
    this.sequenceHeaderFilter = sequenceHeaderFilter;
  }

  /**
	* 	**/
  public Double addA;

  /**
   * Retreives the value of addA attribute
   * 
   * @return addA
   **/

  public Double getAddA() {
    return addA;
  }

  /**
   * Sets the value of addA attribue
   **/

  public void setAddA(final Double addA) {
    this.addA = addA;
  }

  /**
	* 	**/
  public Double addJ;

  /**
   * Retreives the value of addJ attribute
   * 
   * @return addJ
   **/

  public Double getAddJ() {
    return addJ;
  }

  /**
   * Sets the value of addJ attribue
   **/

  public void setAddJ(final Double addJ) {
    this.addJ = addJ;
  }

  /**
	* 	**/
  public Double addU;

  /**
   * Retreives the value of addU attribute
   * 
   * @return addU
   **/

  public Double getAddU() {
    return addU;
  }

  /**
   * Sets the value of addU attribue
   **/

  public void setAddU(final Double addU) {
    this.addU = addU;
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
   * Compares <code>obj</code> to it self and returns true if they both are same
   * 
   * @param obj
   **/
  @Override
  public boolean equals(final Object obj) {
    if(obj instanceof SequestParameters) {
      final SequestParameters c = (SequestParameters) obj;
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