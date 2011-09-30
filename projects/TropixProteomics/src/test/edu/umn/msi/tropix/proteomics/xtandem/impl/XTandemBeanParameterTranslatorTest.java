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

package edu.umn.msi.tropix.proteomics.xtandem.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.annotations.Test;

import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.models.xtandem.XTandemParameters;
import edu.umn.msi.tropix.proteomics.xtandem.XTandemBeanParameterTranslator;
import edu.umn.msi.tropix.proteomics.xtandem.XTandemTestUtils;

public class XTandemBeanParameterTranslatorTest {

  protected String getParameterValue(final String contents, final String iParameterName) {
    final String parameterName = iParameterName.replace("+", "\\+");
    final String regex = "<note type=\"input\" label=\"" + parameterName + "\"[^>]*>([^<]*)<";
    final Matcher matcher = Pattern.compile(regex).matcher(contents);
    Preconditions.checkState(matcher.find(), String.format("Failed to find parameter %s", iParameterName));
    return matcher.group(1);
  }

  @Test(groups = {"unit"})
  public void testTranslate() throws Exception {
    testTranslate(false);
    testTranslate(true);
  }

  public void testTranslate(final boolean setDefault) throws Exception {
    final XTandemBeanParameterTranslator translator = new XTandemBeanParameterTranslator();
    if(setDefault) {
      translator.setDefaultsPath("moo");
    }
    final XTandemParameters parameters = XTandemTestUtils.getXTandemParameters();

    String contents;

    // output, input, taxon, taxonomy info
    contents = translator.getXTandemParameters(parameters, "output1", "input1", "taxon1", "taxoninfo1", null);
    if(setDefault) {
      assert "moo".equals(getParameterValue(contents, "list path, default parameters"));
    }
    assert getParameterValue(contents, "output, path").equals("output1");
    contents = translator.getXTandemParameters(parameters, "output2", "input1", "taxon1", "taxoninfo1", null);
    assert getParameterValue(contents, "output, path").equals("output2");

    contents = translator.getXTandemParameters(parameters, "output1", "input1", "taxon1", "taxoninfo1", null);
    assert getParameterValue(contents, "spectrum, path").equals("input1");
    contents = translator.getXTandemParameters(parameters, "output1", "input2", "taxon1", "taxoninfo1", null);
    assert getParameterValue(contents, "spectrum, path").equals("input2");

    contents = translator.getXTandemParameters(parameters, "output1", "input1", "taxon1", "taxoninfo1", null);
    assert getParameterValue(contents, "protein, taxon").equals("taxon1");
    contents = translator.getXTandemParameters(parameters, "output1", "input1", "taxon2", "taxoninfo1", null);
    assert getParameterValue(contents, "protein, taxon").equals("taxon2");

    contents = translator.getXTandemParameters(parameters, "output1", "input1", "taxon1", "taxoninfo1", null);
    assert getParameterValue(contents, "list path, taxonomy information").equals("taxoninfo1");
    contents = translator.getXTandemParameters(parameters, "output1", "input1", "taxon1", "taxoninfo2", null);
    assert getParameterValue(contents, "list path, taxonomy information").equals("taxoninfo2");

    translator.setMessage("*moo*");

    contents = translator.getXTandemParameters(parameters, "output1", "input", "taxon1", "taxoninfo1", null);
    assert getParameterValue(contents, "output, message").equals("*moo*");

    parameters.setRefine(false);
    contents = translator.getXTandemParameters(parameters, "output1", "input", "taxon1", "taxoninfo1", null);
    assert getParameterValue(contents, "refine").equals("no") : getParameterValue(contents, "refine");

    parameters.setRefine(true);
    contents = translator.getXTandemParameters(parameters, "output1", "input", "taxon1", "taxoninfo1", null);
    assert getParameterValue(contents, "refine").equals("yes") : getParameterValue(contents, "refine");

    // Alter integer typed parameters and make sure the translated contents change
    HashMap<String, String> properties = new HashMap<String, String>();
    properties.put("outputHistogramColumnWidth", "output, histogram column width");
    properties.put("scoringMinimumIonCount", "scoring, minimum ion count");
    properties.put("spectrumMinimumPeaks", "spectrum, minimum peaks");
    properties.put("spectrumTotalPeaks", "spectrum, total peaks");

    final HashMap<Integer, String> integerInputsAndOutputs = new HashMap<Integer, String>();
    integerInputsAndOutputs.put(1, "1");
    integerInputsAndOutputs.put(3, "3");
    integerInputsAndOutputs.put(0, "0");
    reflectivelyCheckValues(translator, parameters, Integer.class, properties, integerInputsAndOutputs);

    // Alter boolean typed parameters and make sure the translated contents change
    properties = new HashMap<String, String>();
    properties.put("outputHistograms", "output, histograms");
    properties.put("outputOneSequencePerCopy", "output, one sequence copy");
    properties.put("outputPerformance", "output, performance");
    properties.put("outputProteins", "output, proteins");
    properties.put("outputSequences", "output, sequences");
    properties.put("outputSpectra", "output, spectra");
    properties.put("proteinCleavageSemi", "protein, cleavage semi");
    properties.put("scoringAIons", "scoring, a ions");
    properties.put("scoringBIons", "scoring, b ions");
    properties.put("scoringCIons", "scoring, c ions");
    properties.put("scoringXIons", "scoring, x ions");
    properties.put("scoringYIons", "scoring, y ions");
    properties.put("scoringZIons", "scoring, z ions");
    properties.put("scoringCyclicPermutation", "scoring, cyclic permutation");
    properties.put("scoringIncludeReverse", "scoring, include reverse");
    properties.put("spectrumParentMonoisotopicMassIsotopeError", "spectrum, parent monoisotopic mass isotope error");
    properties.put("spectrumUseNeutralLossWindow", "spectrum, use neutral loss window");
    properties.put("spectrumUseNoiseSuppression", "spectrum, use noise suppression");
    properties.put("spectrumUseContrastAngle", "spectrum, use contrast angle");
    properties.put("refineCleavageSemi", "refine, cleavage semi");
    properties.put("refineSingleAminoAcidPolymorphisms", "refine, saps");
    properties.put("refineSpectrumSynthesis", "refine, spectrum synthesis");
    properties.put("refineUnanticipatedCleavage", "refine, unanticipated cleavage");
    properties.put("refineUsePotentialModifications", "refine, use potential modifications for full refinement");
    properties.put("refinePointMutations", "refine, point mutations");

    final HashMap<Boolean, String> booleanInputsAndOutputs = new HashMap<Boolean, String>();
    booleanInputsAndOutputs.put(true, "yes");
    booleanInputsAndOutputs.put(false, "no");
    reflectivelyCheckValues(translator, parameters, Boolean.class, properties, booleanInputsAndOutputs);

    // Alter double typed parameters and make sure the translated contents change
    properties = new HashMap<String, String>();
    properties.put("outputMaximumValidExpectationValue", "output, maximum valid expectation value");
    properties.put("proteinCleavageCTerminalMassChange", "protein, cleavage C-terminal mass change");
    properties.put("proteinCleavageNTerminalMassChange", "protein, cleavage N-terminal mass change");
    properties.put("proteinCTerminalResidueModificationMass", "protein, C-terminal residue modification mass");
    properties.put("proteinNTerminalResidueModificationMass", "protein, N-terminal residue modification mass");
    properties.put("spectrumContrastAngle", "spectrum, contrast angle");
    properties.put("spectrumDynamicRange", "spectrum, dynamic range");
    properties.put("spectrumFragmentMassError", "spectrum, fragment mass error");
    properties.put("spectrumMinimumFragmentMZ", "spectrum, minimum fragment mz");
    properties.put("spectrumMinimumParentMH", "spectrum, minimum parent m+h");
    properties.put("spectrumNeutralLossMass", "spectrum, neutral loss mass");
    properties.put("spectrumParentMonoisotopicMassErrorMinus", "spectrum, parent monoisotopic mass error minus");
    properties.put("spectrumParentMonoisotopicMassErrorPlus", "spectrum, parent monoisotopic mass error plus");
    properties.put("spectrumNeutralLossWindow", "spectrum, neutral loss window");
    final HashMap<Double, String> doubleInputsAndOutputs = new HashMap<Double, String>();
    doubleInputsAndOutputs.put(1.3, "1.3");
    doubleInputsAndOutputs.put(3.13, "3.13");
    doubleInputsAndOutputs.put(0.0, "0.0");
    reflectivelyCheckValues(translator, parameters, Double.class, properties, doubleInputsAndOutputs);

    // Check open ended string parameters

    reflectivelyCheckStrings(translator, parameters, "outputResults", "output, results", new String[] {"all", "valid", "stochastic"});
    reflectivelyCheckStrings(translator, parameters, "outputSortResultsBy", "output, sort results by", new String[] {"protein", "spectrum"});
    reflectivelyCheckStrings(translator, parameters, "proteinCleavageSite", "protein, cleavage site", new String[] {"[KR]|{P}", "[X]|[D]"});
    reflectivelyCheckStrings(translator, parameters, "residueModificationMass", "residue, modification mass", new String[] {"1.3@M", "1.3@X,1.2@C"});
    reflectivelyCheckStrings(translator, parameters, "residuePotentialModificationMass", "residue, potential modification mass", new String[] {
        "1.3@M", "1.3@X,1.2@C"});
    reflectivelyCheckStrings(translator, parameters, "residuePotentialModificationMotif", "residue, potential modification motif", new String[] {
        "16@[M!]", "80@[ST!]PX[KR]"});
    reflectivelyCheckStrings(translator, parameters, "refineModificationMass", "refine, modification mass", new String[] {"1.3@M", "1.3@X,1.2@C"});
    reflectivelyCheckStrings(translator, parameters, "refinePotentialModificationMass", "refine, potential modification mass", new String[] {"1.3@M",
        "1.3@X,1.2@C"});
    reflectivelyCheckStrings(translator, parameters, "refinePotentialModificationMotif", "refine, potential modification motif", new String[] {
        "16@[M!]", "80@[ST!]PX[KR]"});
    reflectivelyCheckStrings(translator, parameters, "refinePotentialNTerminusModifications", "refine, potential N-terminus modifications",
        new String[] {"1.3@[", "1.3@[,1.2@["});
    reflectivelyCheckStrings(translator, parameters, "refinePotentialCTerminusModifications", "refine, potential C-terminus modifications",
        new String[] {"1.3@]", "1.3@],1.2@]"});
    reflectivelyCheckStrings(translator, parameters, "spectrumFragmentMassErrorUnits", "spectrum, fragment mass error units", new String[] {
        "Daltons", "ppm"});
    reflectivelyCheckStrings(translator, parameters, "spectrumFragmentMassType", "spectrum, fragment mass type", new String[] {"average",
        "monoisotopic"});
    reflectivelyCheckStrings(translator, parameters, "spectrumParentMonoisotopicMassErrorUnits", "spectrum, parent monoisotopic mass error units",
        new String[] {"Daltons", "ppm"});

  }

  protected void reflectivelyCheckStrings(final XTandemBeanParameterTranslator translator, final XTandemParameters parameters, final String property,
      final String parameter, final String[] domain) throws Exception {
    final HashMap<String, String> inputsAndOutputs = new HashMap<String, String>(domain.length);
    for(final String domainElement : domain) {
      inputsAndOutputs.put(domainElement, domainElement);
    }
    final HashMap<String, String> properties = new HashMap<String, String>(1);
    properties.put(property, parameter);
    reflectivelyCheckValues(translator, parameters, String.class, properties, inputsAndOutputs);
  }

  protected <T> void reflectivelyCheckValues(final XTandemBeanParameterTranslator translator, final XTandemParameters parameters,
      final Class<T> propertiesClass, final Map<String, String> propertiesMap, final Map<T, String> expectedInputsAndOutputs) throws Exception {
    for(final String property : propertiesMap.keySet()) {
      final String parameter = propertiesMap.get(property);
      final String setBeanName = "set" + property.substring(0, 1).toUpperCase() + property.substring(1);
      final Method setMethod = parameters.getClass().getMethod(setBeanName, propertiesClass);
      for(final T input : expectedInputsAndOutputs.keySet()) {
        final String expectedOutput = expectedInputsAndOutputs.get(input);
        setMethod.invoke(parameters, input);
        final String contents = translator.getXTandemParameters(parameters, "output1", "input1", "taxon1", "taxoninfo1", null);
        final String value = getParameterValue(contents, parameter);
        if(!propertiesClass.equals(Double.class)) {
          assert value.equals(expectedOutput) : "Failure on property " + property;
        } else {
          assert Math.abs(Double.parseDouble(value) - Double.parseDouble(expectedOutput)) < 0.0001 : "Failure on proeprty " + property;
        }
      }
    }
  }

}
