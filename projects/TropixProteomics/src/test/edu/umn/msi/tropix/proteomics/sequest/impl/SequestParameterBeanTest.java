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

package edu.umn.msi.tropix.proteomics.sequest.impl;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.sequest.SequestParameters;
import edu.umn.msi.tropix.proteomics.sequest.SequestBeanParameterTranslator;
import edu.umn.msi.tropix.proteomics.sequest.SequestParameterTestData;

public class SequestParameterBeanTest {

  @Test(groups = {"unit"})
  public void testTranslateSequestBean() throws Exception {
    final SequestBeanParameterTranslator translator = new SequestBeanParameterTranslator();
    translator.getSequestParameters(SequestParameterTestData.getSimpleSequestParameters(), "/tmp/database.fasta");
  }

  // Fetches the value of the given parameter from a sequest parameters file
  private String getParameterValue(final String parameter, final String contents) {
    final Pattern p = Pattern.compile("^.*" + parameter + ".*=(.*)$", Pattern.MULTILINE);
    final Matcher m = p.matcher(contents);
    m.find();
    return m.group(1).trim();
  }

  @Test(groups = {"unit"})
  public void testUnitTranslateSequestBean() throws Exception {
    String parametersContents;
    String value;
    final SequestBeanParameterTranslator translator = new SequestBeanParameterTranslator();
    final SequestParameters sequestParameters = SequestParameterTestData.getSimpleSequestParameters();
    // java.util.Map<String, Pair<String, Double>> doubleParameters = new java.util.HashMap<String, Pair<String, Double>>();
    final java.util.Map<String, String> doubleParameters = new java.util.HashMap<String, String>();
    doubleParameters.put("AddA", "add_A_Alanine");
    doubleParameters.put("AddB", "add_B_avg_NandD");
    doubleParameters.put("AddC", "add_C_Cysteine");
    doubleParameters.put("AddD", "add_D_Aspartic_Acid");
    doubleParameters.put("AddE", "add_E_Glutamic_Acid");
    doubleParameters.put("AddF", "add_F_Phenylalanine");
    doubleParameters.put("AddG", "add_G_Glycine");
    doubleParameters.put("AddH", "add_H_Histidine");
    doubleParameters.put("AddI", "add_I_Isoleucine");
    doubleParameters.put("AddJ", "add_J_user_amino_acid");
    doubleParameters.put("AddK", "add_K_Lysine");
    doubleParameters.put("AddL", "add_L_Leucine");
    doubleParameters.put("AddM", "add_M_Methionine");
    doubleParameters.put("AddN", "add_N_Asparagine");
    doubleParameters.put("AddO", "add_O_Ornithine");
    doubleParameters.put("AddP", "add_P_Proline");
    doubleParameters.put("AddQ", "add_Q_Glutamine");
    doubleParameters.put("AddR", "add_R_Arginine");
    doubleParameters.put("AddS", "add_S_Serine");
    doubleParameters.put("AddT", "add_T_Threonine");
    doubleParameters.put("AddU", "add_U_user_amino_acid");
    doubleParameters.put("AddV", "add_V_Valine");
    doubleParameters.put("AddW", "add_W_Tryptophan");
    doubleParameters.put("AddX", "add_X_LorI");
    doubleParameters.put("AddY", "add_Y_Tyrosine");
    doubleParameters.put("AddZ", "add_Z_avg_QandE");
    doubleParameters.put("AddCTermPeptide", "add_Cterm_peptide");
    doubleParameters.put("AddNTermPeptide", "add_Nterm_peptide");
    doubleParameters.put("AddCTermProtein", "add_Cterm_protein");
    doubleParameters.put("AddNTermProtein", "add_Nterm_protein");
    doubleParameters.put("MatchPeakTolerance", "match_peak_tolerance");
    doubleParameters.put("IonCutoffPercentage", "ion_cutoff_percentage");
    doubleParameters.put("FragmentMassTolerance", "fragment_ion_tolerance");
    doubleParameters.put("PeptideMassTolerance", "peptide_mass_tolerance");
    for(final String setterName : doubleParameters.keySet()) {
      final Method setter = sequestParameters.getClass().getMethod("set" + setterName, Double.class);
      setter.invoke(sequestParameters, 0.0);
      parametersContents = translator.getSequestParameters(sequestParameters, "/tmp/database.fasta");
      value = getParameterValue(doubleParameters.get(setterName), parametersContents);
      assert Math.abs(Double.parseDouble(value) - 0.0D) < .0001 : "Setter set" + setterName + " failed after translation.";
      setter.invoke(sequestParameters, 5.0);
      parametersContents = translator.getSequestParameters(sequestParameters, "/tmp/database.fasta");
      value = getParameterValue(doubleParameters.get(setterName), parametersContents);
      assert Math.abs(Double.parseDouble(value) - 5.0D) < .0001 : "Setter set" + setterName + " failed after translation.";
    }
    final java.util.Map<String, String> integerParameters = new java.util.HashMap<String, String>();
    integerParameters.put("InternalCleavageSites", "max_num_internal_cleavage_sites");
    integerParameters.put("NumOutputLines", "num_output_lines");
    integerParameters.put("NumResults", "num_results");
    integerParameters.put("MaxNumDifferentialsPerPeptide", "max_num_differential_per_peptide");
    integerParameters.put("MatchPeakCount", "match_peak_count");
    integerParameters.put("MatchPeakAllowedError", "match_peak_allowed_error");
    integerParameters.put("PrintDuplicateReferences", "print_duplicate_references");
    for(final String setterName : integerParameters.keySet()) {
      final Method setter = sequestParameters.getClass().getMethod("set" + setterName, Integer.class);
      setter.invoke(sequestParameters, 0);
      parametersContents = translator.getSequestParameters(sequestParameters, "/tmp/database.fasta");
      value = getParameterValue(integerParameters.get(setterName), parametersContents);
      assert Integer.parseInt(value) == 0 : "Setter set" + setterName + " failed after translation.";
      setter.invoke(sequestParameters, 1);
      parametersContents = translator.getSequestParameters(sequestParameters, "/tmp/database.fasta");
      value = getParameterValue(integerParameters.get(setterName), parametersContents);
      assert Integer.parseInt(value) == 1 : "Setter set" + setterName + " failed after translation.";
    }
    final java.util.Map<String, String> booleanParameters = new java.util.HashMap<String, String>();
    booleanParameters.put("RemovePrecursorPeak", "remove_precursor_peak");
    booleanParameters.put("NormalizeXCorr", "normalize_xcorr");
    booleanParameters.put("ShowFragmentIons", "show_fragment_ions");
    for(final String setterName : booleanParameters.keySet()) {
      final Method setter = sequestParameters.getClass().getMethod("set" + setterName, Boolean.class);
      setter.invoke(sequestParameters, false);
      parametersContents = translator.getSequestParameters(sequestParameters, "/tmp/database.fasta");
      value = getParameterValue(booleanParameters.get(setterName), parametersContents);
      assert Integer.parseInt(value) == 0 : "Setter set" + setterName + " failed after translation.";
      setter.invoke(sequestParameters, true);
      parametersContents = translator.getSequestParameters(sequestParameters, "/tmp/database.fasta");
      value = getParameterValue(booleanParameters.get(setterName), parametersContents);
      assert Integer.parseInt(value) == 1 : "Setter set" + setterName + " failed after translation.";
    }
    final java.util.Map<String, String> typeParameters = new java.util.HashMap<String, String>();
    typeParameters.put("MassTypeFragment", "mass_type_fragment");
    typeParameters.put("MassTypeParent", "mass_type_parent");
    for(final String setterName : typeParameters.keySet()) {
      final Method setter = sequestParameters.getClass().getMethod("set" + setterName, String.class);
      setter.invoke(sequestParameters, "monoisotopic");
      parametersContents = translator.getSequestParameters(sequestParameters, "/tmp/database.fasta");
      value = getParameterValue(typeParameters.get(setterName), parametersContents);
      assert Integer.parseInt(value) == 1 : "Setter set" + setterName + " failed after translation.";
      setter.invoke(sequestParameters, "average");
      parametersContents = translator.getSequestParameters(sequestParameters, "/tmp/database.fasta");
      value = getParameterValue(typeParameters.get(setterName), parametersContents);
      assert Integer.parseInt(value) == 0 : "Setter set" + setterName + " failed after translation.";
    }
    sequestParameters.setPeptideMassUnits("amu"); // 0amu, 1mmu, 2ppm
    parametersContents = translator.getSequestParameters(sequestParameters, "/tmp/database.fasta");
    value = getParameterValue("peptide_mass_units", parametersContents);
    assert value.equals("0") : "Problem with peptide mass units in translation";
    sequestParameters.setPeptideMassUnits("mmu"); // 0amu, 1mmu, 2ppm
    parametersContents = translator.getSequestParameters(sequestParameters, "/tmp/database.fasta");
    value = getParameterValue("peptide_mass_units", parametersContents);
    assert value.equals("1") : "Problem with peptide mass units in translation";
    sequestParameters.setPeptideMassUnits("ppm"); // 0amu, 1mmu, 2ppm
    parametersContents = translator.getSequestParameters(sequestParameters, "/tmp/database.fasta");
    value = getParameterValue("peptide_mass_units", parametersContents);
    assert value.equals("2") : "Problem with peptide mass units in translation";

    String[] values;

    sequestParameters.setDigestMassRangeLower(0.0);
    sequestParameters.setDigestMassRangeUpper(0.0);
    parametersContents = translator.getSequestParameters(sequestParameters, "/tmp/database.fasta");
    values = getParameterValue("digest_mass_range", parametersContents).split("\\s+");
    assert eq(values[0], 0.0) : "Problem with digest mass range in translation";
    assert eq(values[1], 0.0) : "Problem with digest mass range in translation";
    sequestParameters.setDigestMassRangeLower(1.0);
    sequestParameters.setDigestMassRangeUpper(100.0);
    parametersContents = translator.getSequestParameters(sequestParameters, "/tmp/database.fasta");
    values = getParameterValue("digest_mass_range", parametersContents).split("\\s+");
    assert eq(values[0], 1.0) : "Problem with digest mass range in translation";
    assert eq(values[1], 100.0) : "Problem with digest mass range in translation";

    sequestParameters.setProteinMassFilterLower(0);
    sequestParameters.setProteinMassFilterUpper(0);
    parametersContents = translator.getSequestParameters(sequestParameters, "/tmp/database.fasta");
    values = getParameterValue("protein_mass_filter", parametersContents).split("\\s+");
    assert eq(values[0], "0") : "Problem with protein mass filter in translation";
    assert eq(values[1], "0") : "Problem with protein mass filter in translation";
    sequestParameters.setProteinMassFilterLower(1);
    sequestParameters.setProteinMassFilterUpper(100);
    parametersContents = translator.getSequestParameters(sequestParameters, "/tmp/database.fasta");
    values = getParameterValue("protein_mass_filter", parametersContents).split("\\s+");
    assert eq(values[0], "1") : "Problem with protein mass filter in translation";
    assert eq(values[1], "100") : "Problem with protein mass filter in translation";

    sequestParameters.setDiffSearchC(0.0);
    sequestParameters.setDiffSearchN(0.0);
    parametersContents = translator.getSequestParameters(sequestParameters, "/tmp/database.fasta");
    values = getParameterValue("term_diff_search", parametersContents).split("\\s+");
    assert eq(values[0], 0.0) : "Problem with term_diff_search in translation";
    assert eq(values[1], 0.0) : "Problem with term_diff_search in translation";
    sequestParameters.setDiffSearchC(2.0);
    sequestParameters.setDiffSearchN(4.0);
    parametersContents = translator.getSequestParameters(sequestParameters, "/tmp/database.fasta");
    values = getParameterValue("term_diff_search", parametersContents).split("\\s+");
    assert eq(values[0], 2.0) : "Problem with term_diff_search in translation";
    assert eq(values[1], 4.0) : "Problem with term_diff_search in translation";

    sequestParameters.setEnzymeName("Trypsin");
    sequestParameters.setEnzymeCTerm(true);
    sequestParameters.setEnzymeLimit("full");
    sequestParameters.setEnzymeCleave("");
    sequestParameters.setEnzymeNoCleave("");
    parametersContents = translator.getSequestParameters(sequestParameters, "/tmp/database.fasta");
    values = getParameterValue("enzyme_info", parametersContents).split("\\s+");
    assert eq(values[0], "Trypsin");
    assert eq(values[1], "1");
    assert eq(values[2], "1");
    assert eq(values[3], "-");
    assert eq(values[4], "-");

    sequestParameters.setEnzymeName("AspN");
    sequestParameters.setEnzymeCTerm(false);
    sequestParameters.setEnzymeLimit("partial");
    sequestParameters.setEnzymeCleave("KR");
    sequestParameters.setEnzymeNoCleave("P");
    parametersContents = translator.getSequestParameters(sequestParameters, "/tmp/database.fasta");
    values = getParameterValue("enzyme_info", parametersContents).split("\\s+");
    assert eq(values[0], "AspN");
    assert eq(values[1], "2");
    assert eq(values[2], "0");
    assert eq(values[3], "KR");
    assert eq(values[4], "P");

    sequestParameters.setEnzymeLimit("n");
    parametersContents = translator.getSequestParameters(sequestParameters, "/tmp/database.fasta");
    values = getParameterValue("enzyme_info", parametersContents).split("\\s+");
    assert eq(values[1], "3");

    sequestParameters.setEnzymeLimit("c");
    parametersContents = translator.getSequestParameters(sequestParameters, "/tmp/database.fasta");
    values = getParameterValue("enzyme_info", parametersContents).split("\\s+");
    assert eq(values[1], "4");

    sequestParameters.setEnzymeLimit("none");
    parametersContents = translator.getSequestParameters(sequestParameters, "/tmp/database.fasta");
    values = getParameterValue("enzyme_info", parametersContents).split("\\s+");
    assert eq(values[1], "0");

    // na nb ny a b c d v w x y z
    final Object[] ionInputs = new Object[] {false, false, false, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    for(int i = 0; i < ionInputs.length; i++) {
      final Object[] ionInputsCopy = ionInputs.clone();
      if(ionInputsCopy[i].equals(false)) {
        ionInputsCopy[i] = true;
      } else {
        ionInputsCopy[i] = 1.0;
      }
      sequestParameters.setIonSeriesNA((Boolean) ionInputsCopy[0]);
      sequestParameters.setIonSeriesNB((Boolean) ionInputsCopy[1]);
      sequestParameters.setIonSeriesNY((Boolean) ionInputsCopy[2]);
      sequestParameters.setIonSeriesA((Double) ionInputsCopy[3]);
      sequestParameters.setIonSeriesB((Double) ionInputsCopy[4]);
      sequestParameters.setIonSeriesC((Double) ionInputsCopy[5]);
      sequestParameters.setIonSeriesD((Double) ionInputsCopy[6]);
      sequestParameters.setIonSeriesV((Double) ionInputsCopy[7]);
      sequestParameters.setIonSeriesW((Double) ionInputsCopy[8]);
      sequestParameters.setIonSeriesX((Double) ionInputsCopy[9]);
      sequestParameters.setIonSeriesY((Double) ionInputsCopy[10]);
      sequestParameters.setIonSeriesZ((Double) ionInputsCopy[11]);
      parametersContents = translator.getSequestParameters(sequestParameters, "/tmp/database.fasta");
      values = getParameterValue("ion_series", parametersContents).split("\\s+");
      for(int j = 0; j < ionInputs.length; j++) {
        assert eq(values[j], (i == j) ? 1.0 : 0.0) : "Problem with ion series translation.";
      }
    }

    for(int i = 0; i < 6; i++) {
      sequestParameters.setDiffSearch1Value((i == 0) ? 1.0 : 0.0);
      sequestParameters.setDiffSearch2Value((i == 1) ? 1.0 : 0.0);
      sequestParameters.setDiffSearch3Value((i == 2) ? 1.0 : 0.0);
      sequestParameters.setDiffSearch4Value((i == 3) ? 1.0 : 0.0);
      sequestParameters.setDiffSearch5Value((i == 4) ? 1.0 : 0.0);
      sequestParameters.setDiffSearch6Value((i == 5) ? 1.0 : 0.0);
      parametersContents = translator.getSequestParameters(sequestParameters, "/tmp/database.fasta");
      values = getParameterValue("diff_search_options", parametersContents).split("\\s+");
      for(int j = 0; j < 6; j++) {
        assert eq(values[2 * j], (i == j) ? 1.0 : 0.0) : "Problem with diff search.";
      }
    }
    sequestParameters.setDiffSearch1Value(100.00);
    sequestParameters.setDiffSearch2Value(100.00);
    sequestParameters.setDiffSearch3Value(100.00);
    sequestParameters.setDiffSearch4Value(100.00);
    sequestParameters.setDiffSearch5Value(100.00);
    sequestParameters.setDiffSearch6Value(100.00);

    for(int i = 0; i < 6; i++) {
      sequestParameters.setDiffSearch1Residue((i == 0) ? "A" : "BCD");
      sequestParameters.setDiffSearch2Residue((i == 1) ? "A" : "BCD");
      sequestParameters.setDiffSearch3Residue((i == 2) ? "A" : "BCD");
      sequestParameters.setDiffSearch4Residue((i == 3) ? "A" : "BCD");
      sequestParameters.setDiffSearch5Residue((i == 4) ? "A" : "BCD");
      sequestParameters.setDiffSearch6Residue((i == 5) ? "A" : "BCD");
      parametersContents = translator.getSequestParameters(sequestParameters, "/tmp/database.fasta");
      values = getParameterValue("diff_search_options", parametersContents).split("\\s+");
      for(int j = 0; j < 6; j++) {
        assert eq(values[2 * j + 1], (i == j) ? "A" : "BCD") : "Problem with diff search.";
      }
    }
  }

  private boolean eq(final String a, final Double b) {
    return eq(Double.parseDouble(a), b);
  }

  private boolean eq(final Double a, final Double b) {
    return Math.abs(a - b) < .0001;
  }

  private boolean eq(final Object x, final Object y) {
    return x.equals(y);
  }

}
