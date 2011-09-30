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

package edu.umn.msi.tropix.proteomics.sequest;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.umn.msi.tropix.models.sequest.SequestParameters;

public class SequestBeanParameterTranslator implements SequestParameterTranslator {
  private static final Log LOG = LogFactory.getLog(SequestBeanParameterTranslator.class);

  public String getSequestParameters(final SequestParameters inputParameters, final String firstDatabasePath) {
    return getParameters(inputParameters, firstDatabasePath);
  }

  public static final String[] PARAMETERS_ORDER = {"first_database_name", "second_database_name", "peptide_mass_tolerance", "peptide_mass_units", "ion_series", "fragment_ion_tolerance", "num_output_lines", "num_results", "num_description_lines", "show_fragment_ions",
      "print_duplicate_references", "enzyme_info", "max_num_differential_per_peptide", "diff_search_options", "term_diff_search_options", "nucleotide_reading_frame", "mass_type_parent", "mass_type_fragment", "normalize_xcorr", "remove_precursor_peak", "ion_cutoff_percentage",
      "max_num_internal_cleavage_sites", "protein_mass_filter", "match_peak_count", "match_peak_allowed_error", "match_peak_tolerance", "partial_sequence", "sequence_header_filter", "digest_mass_range", "add_Cterm_peptide", "add_Cterm_protein", "add_Nterm_peptide",
      "add_Nterm_protein", "add_G_Glycine", "add_A_Alanine", "add_S_Serine", "add_P_Proline", "add_V_Valine", "add_T_Threonine", "add_C_Cysteine", "add_L_Leucine", "add_I_Isoleucine", "add_X_LorI", "add_N_Asparagine", "add_O_Ornithine", "add_B_avg_NandD", "add_D_Aspartic_Acid",
      "add_Q_Glutamine", "add_K_Lysine", "add_Z_avg_QandE", "add_E_Glutamic_Acid", "add_M_Methionine", "add_H_Histidine", "add_F_Phenylalanine", "add_R_Arginine", "add_Y_Tyrosine", "add_W_Tryptophan", "add_J_user_amino_acid", "add_U_user_amino_acid"};

  private String getIonSeriesString(final SequestParameters input) {
    final StringBuffer buffer = new StringBuffer();
    buffer.append(convertBoolean(input.getIonSeriesNA()));
    buffer.append(' ');
    buffer.append(convertBoolean(input.getIonSeriesNB()));
    buffer.append(' ');
    buffer.append(convertBoolean(input.getIonSeriesNY()));
    buffer.append(' ');
    buffer.append(input.getIonSeriesA());
    buffer.append(' ');
    buffer.append(input.getIonSeriesB());
    buffer.append(' ');
    buffer.append(input.getIonSeriesC());
    buffer.append(' ');
    buffer.append(input.getIonSeriesD());
    buffer.append(' ');
    buffer.append(input.getIonSeriesV());
    buffer.append(' ');
    buffer.append(input.getIonSeriesW());
    buffer.append(' ');
    buffer.append(input.getIonSeriesX());
    buffer.append(' ');
    buffer.append(input.getIonSeriesY());
    buffer.append(' ');
    buffer.append(input.getIonSeriesZ());
    return buffer.toString();
  }

  private String getEnzymeInfoString(final SequestParameters input) {
    final StringBuffer buffer = new StringBuffer();
    buffer.append(input.getEnzymeName());
    buffer.append(' ');
    buffer.append(convertEnzymeLimit(input.getEnzymeLimit()));
    buffer.append(' ');
    buffer.append(convertBoolean(input.getEnzymeCTerm()));
    buffer.append(' ');
    buffer.append(getCleaveString(input.getEnzymeCleave()));
    buffer.append(' ');
    buffer.append(getCleaveString(input.getEnzymeNoCleave()));
    return buffer.toString();
  }

  private String getCleaveString(final String rawCleaveString) {
    if(null == rawCleaveString || "-".equals(rawCleaveString) || "".equals(rawCleaveString)) {
      return "-";
    } else {
      return rawCleaveString;
    }
  }

  private String convertEnzymeLimit(final String enzymeLimitString) {
    if("full".equals(enzymeLimitString)) {
      return "1";
    } else if("partial".equals(enzymeLimitString)) {
      return "2";
    } else if("n".equals(enzymeLimitString)) {
      return "3";
    } else if("c".equals(enzymeLimitString)) {
      return "4";
    } else if("none".equals(enzymeLimitString)) {
      return "0";
    } else {
      LOG.warn("Unknown enzymeLimitString encountered " + enzymeLimitString);
      return "1";
    }
  }

  private String convertUnits(final String unitStr) {
    if(unitStr.trim().equals("amu")) {
      return "0";
    } else if(unitStr.trim().equals("mmu")) {
      return "1";
    } else if(unitStr.trim().equals("ppm")) {
      return "2";
    } else {
      LOG.warn("Unknown units encountered " + unitStr);
      return "0";
    }
  }

  private String convertMassType(final String massType) {
    if("monoisotopic".equals(massType)) {
      return "1";
    } else if("average".equals(massType)) {
      return "0";
    } else {
      LOG.warn("Unknown massType encountered " + massType);
      return "0";
    }
  }

  private String convertBoolean(final Boolean bool) {
    if(bool != null && bool.booleanValue()) {
      return "1";
    } else {
      return "0";
    }
  }

  private String getDiffSearchString(final Double value, final String residues) {
    if(value == null || value.doubleValue() == 0.00d || residues == null || residues.equals("")) {
      return "0.0 X ";
    } else {
      return value.toString() + " " + residues + " ";
    }
  }

  private String getDiffSearchOptions(final SequestParameters input) {
    final StringBuffer buffer = new StringBuffer();
    buffer.append(getDiffSearchString(input.getDiffSearch1Value(), input.getDiffSearch1Residue()));
    buffer.append(getDiffSearchString(input.getDiffSearch2Value(), input.getDiffSearch2Residue()));
    buffer.append(getDiffSearchString(input.getDiffSearch3Value(), input.getDiffSearch3Residue()));
    buffer.append(getDiffSearchString(input.getDiffSearch4Value(), input.getDiffSearch4Residue()));
    buffer.append(getDiffSearchString(input.getDiffSearch5Value(), input.getDiffSearch5Residue()));
    buffer.append(getDiffSearchString(input.getDiffSearch6Value(), input.getDiffSearch6Residue()));
    return buffer.toString();
  }

  private String getTermDiffSearchOptions(final SequestParameters input) {
    final String deltaC = input.getDiffSearchC().toString();
    final String deltaN = input.getDiffSearchN().toString();
    return deltaC + ' ' + deltaN;
  }

  private String getProteinMassFilterString(final SequestParameters input) {
    final String lower = input.getProteinMassFilterLower().toString();
    final String upper = input.getProteinMassFilterUpper().toString();
    return lower + ' ' + upper;
  }

  private String getDigestMassRangeString(final SequestParameters input) {
    final String lower = input.getDigestMassRangeLower().toString();
    final String upper = input.getDigestMassRangeUpper().toString();
    return lower + ' ' + upper;
  }

  private String getParameters(final SequestParameters input, final String databasePath) {
    final Map<String, String> rawParameters = new HashMap<String, String>(PARAMETERS_ORDER.length);
    rawParameters.put("first_database_name", databasePath);
    rawParameters.put("second_database_name", input.getSecondaryDatabaseId());
    rawParameters.put("peptide_mass_tolerance", input.getPeptideMassTolerance().toString());
    rawParameters.put("peptide_mass_units", convertUnits(input.getPeptideMassUnits()));
    rawParameters.put("ion_series", getIonSeriesString(input));
    rawParameters.put("fragment_ion_tolerance", input.getFragmentMassTolerance().toString());
    rawParameters.put("num_output_lines", input.getNumOutputLines().toString());
    rawParameters.put("num_results", input.getNumResults().toString());
    rawParameters.put("num_description_lines", input.getNumDescriptionLines().toString());
    rawParameters.put("show_fragment_ions", convertBoolean(input.getShowFragmentIons()));
    rawParameters.put("print_duplicate_references", input.getPrintDuplicateReferences().toString());
    rawParameters.put("enzyme_info", getEnzymeInfoString(input));
    rawParameters.put("max_num_differential_per_peptide", input.getMaxNumDifferentialsPerPeptide().toString());
    rawParameters.put("diff_search_options", getDiffSearchOptions(input));
    rawParameters.put("term_diff_search_options", getTermDiffSearchOptions(input));
    rawParameters.put("nucleotide_reading_frame", input.getNucleotideReadingFrame().toString());
    rawParameters.put("mass_type_parent", convertMassType(input.getMassTypeParent()));
    rawParameters.put("mass_type_fragment", convertMassType(input.getMassTypeFragment()));
    rawParameters.put("normalize_xcorr", convertBoolean(input.getNormalizeXCorr()));
    rawParameters.put("remove_precursor_peak", convertBoolean(input.getRemovePrecursorPeak()));
    rawParameters.put("ion_cutoff_percentage", input.getIonCutoffPercentage().toString());
    rawParameters.put("max_num_internal_cleavage_sites", input.getInternalCleavageSites().toString());
    rawParameters.put("protein_mass_filter", getProteinMassFilterString(input));
    rawParameters.put("match_peak_count", input.getMatchPeakCount().toString());
    rawParameters.put("match_peak_allowed_error", input.getMatchPeakAllowedError().toString());
    rawParameters.put("match_peak_tolerance", input.getMatchPeakTolerance().toString());
    rawParameters.put("partial_sequence", input.getPartialSequence());
    rawParameters.put("sequence_header_filter", input.getSequenceHeaderFilter());
    rawParameters.put("digest_mass_range", getDigestMassRangeString(input));
    rawParameters.put("add_Cterm_peptide", input.getAddCTermPeptide().toString());
    rawParameters.put("add_Cterm_protein", input.getAddCTermProtein().toString());
    rawParameters.put("add_Nterm_peptide", input.getAddNTermPeptide().toString());
    rawParameters.put("add_Nterm_protein", input.getAddNTermProtein().toString());
    rawParameters.put("add_G_Glycine", input.getAddG().toString());
    rawParameters.put("add_A_Alanine", input.getAddA().toString());
    rawParameters.put("add_S_Serine", input.getAddS().toString());
    rawParameters.put("add_P_Proline", input.getAddP().toString());
    rawParameters.put("add_V_Valine", input.getAddV().toString());
    rawParameters.put("add_T_Threonine", input.getAddT().toString());
    rawParameters.put("add_C_Cysteine", input.getAddC().toString());
    rawParameters.put("add_L_Leucine", input.getAddL().toString());
    rawParameters.put("add_I_Isoleucine", input.getAddI().toString());
    rawParameters.put("add_X_LorI", input.getAddX().toString());
    rawParameters.put("add_N_Asparagine", input.getAddN().toString());
    rawParameters.put("add_O_Ornithine", input.getAddO().toString());
    rawParameters.put("add_B_avg_NandD", input.getAddB().toString());
    rawParameters.put("add_D_Aspartic_Acid", input.getAddD().toString());
    rawParameters.put("add_Q_Glutamine", input.getAddQ().toString());
    rawParameters.put("add_K_Lysine", input.getAddK().toString());
    rawParameters.put("add_Z_avg_QandE", input.getAddZ().toString());
    rawParameters.put("add_E_Glutamic_Acid", input.getAddE().toString());
    rawParameters.put("add_M_Methionine", input.getAddM().toString());
    rawParameters.put("add_H_Histidine", input.getAddH().toString());
    rawParameters.put("add_F_Phenylalanine", input.getAddF().toString());
    rawParameters.put("add_R_Arginine", input.getAddR().toString());
    rawParameters.put("add_Y_Tyrosine", input.getAddY().toString());
    rawParameters.put("add_W_Tryptophan", input.getAddW().toString());
    rawParameters.put("add_J_user_amino_acid", input.getAddJ().toString());
    rawParameters.put("add_U_user_amino_acid", input.getAddU().toString());

    // Spit out parameters in correct format to a string buffer
    final StringBuffer buffer = new StringBuffer();
    buffer.append("[SEQUEST]\n");
    for(final String paramName : PARAMETERS_ORDER) {
      if(!rawParameters.containsKey(paramName)) {
        LOG.warn("Key not found in parameter map " + paramName);
      }
      buffer.append(paramName + " = " + rawParameters.get(paramName) + "\n");
    }
    return buffer.toString();
  }

}
